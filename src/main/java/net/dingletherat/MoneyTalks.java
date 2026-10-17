package net.dingletherat;

import net.dingletherat.block.MoneyBlocks;
import net.dingletherat.block.entity.MoneyBlockEntities;
import net.dingletherat.item.MoneyDataComponents;
import net.dingletherat.item.MoneyGroups;
import net.dingletherat.item.MoneyItems;
import net.dingletherat.item.custom.Wallet;
import net.dingletherat.screen.MoneyMenus;
import net.dingletherat.state.*;
import net.dingletherat.villager.MoneyVillagers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Prediction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoneyTalks implements ModInitializer {
	public static final String MOD_ID = "moneytalks";
	public static final List<Item> NON_TRANSFERABLE = List.of(MoneyItems.COIN, MoneyBlocks.DOUBLOON.asItem());

	// Note to self: run datagen when changing
	public static final int ADVANCEMENT_DEEP_POCKETS_GOAL = 1000;
	public static final int ADVANCEMENT_MONOPOLY_GOAL = 100;
	public static final int ADVANCEMENT_RICHEST_PIRATE_GOAL = 192;

	public static WalletState walletState;
	public static ShopState shopState;

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		MoneyItems.registerItems();
		MoneyMenus.registerMenus();
		MoneyVillagers.registerVillagers();
		MoneyBlocks.registerBlocks();
		MoneyBlockEntities.registerBlockEntities();
		MoneyGroups.registerItemGroups();
		MoneyDataComponents.registerDataComponents();
		LOGGER.info("Loaded " + MOD_ID + "!");

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			walletState = WalletState.get(server);
			shopState = ShopState.get(server);
		});

		ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
			if (!(entity instanceof ServerPlayer player)) return;
			handleDoubloonLoss(player, player);
			if (!(damageSource.getDirectEntity() instanceof ServerPlayer)) return;
			handleDollarLoss(player, player);
		});
	}
	private void handleDoubloonLoss(ServerPlayer oldPlayer, ServerPlayer newPlayer) {
		for (int i = 0; i < oldPlayer.getInventory().getContainerSize(); i++) {
			ItemStack stack = oldPlayer.getInventory().getItem(i);
			if (stack.is(MoneyBlocks.DOUBLOON.asItem())) {
				oldPlayer.getInventory().setItem(i, ItemStack.EMPTY);
				ItemEntity itemEntity = oldPlayer.createItemStackToDrop(stack, true, false);
				if (itemEntity != null) oldPlayer.level().addFreshEntity(itemEntity);
			}
		}
	}
	private void handleDollarLoss(ServerPlayer oldPlayer, ServerPlayer player) {
		int invSize = player.getInventory().getContainerSize();

		// Deduct 15% from wallets in player inventory
		int walletDollarsLost = 0;
		Set<UUID> inventoryWalletIds = new HashSet<>();
		for (int i = 0; i < invSize; i++) {
			ItemStack stack = player.getInventory().getItem(i);
			if (stack.is(MoneyItems.WALLET) && stack.has(MoneyDataComponents.COINS)) {
				int stored = stack.get(MoneyDataComponents.COINS);
				int loss = (int) Math.ceil(stored * 0.15);
				int newAmount = Math.max(0, stored - loss);
				stack.set(MoneyDataComponents.COINS, newAmount);
				walletDollarsLost += loss;
				UUID walletId = stack.get(MoneyDataComponents.WALLET_ID);
				if (walletId != null) {
					inventoryWalletIds.add(walletId);
					if (walletState != null) {
						String owner = Wallet.getOwnerName(stack);
						walletState.update(walletId, owner != null ? owner : "", newAmount);
					}
				}
			}
		}

		// Deduct 15% from wallets in containers via WalletState
		if (walletState != null) {
			for (Map.Entry<UUID, WalletState.WalletEntry> entry : walletState.getWalletsOwnedBy(player.getName().getString()).entrySet()) {
				if (!inventoryWalletIds.contains(entry.getKey())) {
					int stored = entry.getValue().coins;
					int loss = (int) Math.ceil(stored * 0.15);
					walletDollarsLost += loss;
					walletState.applyDeduction(entry.getKey(), entry.getValue().owner, Math.max(0, stored - loss));
				}
			}
		}

		// Deduct 20% of loose dollars
		int looseDollars = 0;
		for (int i = 0; i < invSize; i++) {
			ItemStack stack = player.getInventory().getItem(i);
			if (stack.is(MoneyItems.COIN)) looseDollars += stack.getCount();
		}
		int looseLoss = (int) Math.ceil(looseDollars * 0.20);
		int toRemove = looseLoss;
		for (int i = 0; i < invSize && toRemove > 0; i++) {
			ItemStack stack = player.getInventory().getItem(i);
			if (stack.is(MoneyItems.COIN)) {
				int removed = Math.min(stack.getCount(), toRemove);
				stack.shrink(removed);
				toRemove -= removed;
			}
		}

		// Drop lost dollars
		int totalDropped = (looseLoss - toRemove) + walletDollarsLost;
		int remaining = totalDropped;
		while (remaining > 0) {
			ItemStack drop = new ItemStack(MoneyItems.COIN);
			int amount = Math.min(remaining, drop.getMaxStackSize());
			drop.setCount(amount);
			player.drop(drop, false, Prediction.PREDICTED);
			remaining -= amount;
		}
	}

	public static void grantAdvancement(Player player, String advancement, String criterion) {
        if (!(player instanceof ServerPlayer serverPlayer)) return;

        AdvancementHolder advancementHolder = serverPlayer.level().getServer().getAdvancements()
                .get(Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, advancement));
        if (advancementHolder != null) serverPlayer.getAdvancements().award(advancementHolder, criterion);
	}
}
