package net.dingletherat;

import net.dingletherat.block.MoneyBlocks;
import net.dingletherat.block.entity.MoneyBlockEntities;
import net.dingletherat.item.MoneyGroups;
import net.dingletherat.item.MoneyItems;
import net.dingletherat.item.potion.MoneyPotions;
import net.dingletherat.screen.MoneyMenus;
import net.dingletherat.state.*;
import net.dingletherat.villager.MoneyVillagers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoneyTalks implements ModInitializer {
	public static final String MOD_ID = "moneytalks";
	public static WalletState walletState;
	public static ShopState shopState;
    public static final List<Item> nonTransferable = List.of(MoneyItems.DOLLAR, MoneyBlocks.DOUBLOON.asItem());

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
				oldPlayer.drop(stack, true, false);
			}
		}
	}
	private void handleDollarLoss(ServerPlayer oldPlayer, ServerPlayer player) {
		int invSize = player.getInventory().getContainerSize();

		// Deduct 15% from wallets in player inventory
		int walletDollarsLost = 0;
		Set<String> inventoryWalletIds = new HashSet<>();
		for (int i = 0; i < invSize; i++) {
			ItemStack stack = player.getInventory().getItem(i);
			if (stack.is(MoneyItems.WALLET) && stack.has(DataComponents.CUSTOM_DATA)) {
				CompoundTag nbt = stack.get(DataComponents.CUSTOM_DATA).copyTag();
				int stored = nbt.getInt("Dollars").orElse(0);
				int loss = (int) Math.ceil(stored * 0.15);
				int newAmount = Math.max(0, stored - loss);
				nbt.putInt("Dollars", newAmount);
				stack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
				walletDollarsLost += loss;
				String walletId = nbt.getString("WalletId").orElse("");
				if (!walletId.isEmpty()) {
					inventoryWalletIds.add(walletId);
					if (walletState != null) {
						walletState.update(UUID.fromString(walletId), nbt.getString("Owner").orElse(""), newAmount);
					}
				}
			}
		}

		// Deduct 15% from wallets in containers via WalletState
		if (walletState != null) {
			for (Map.Entry<UUID, WalletState.WalletEntry> entry : walletState.getWalletsOwnedBy(player.getName().getString()).entrySet()) {
				if (!inventoryWalletIds.contains(entry.getKey().toString())) {
					int stored = entry.getValue().dollars;
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
			if (stack.is(MoneyItems.DOLLAR)) looseDollars += stack.getCount();
		}
		int looseLoss = (int) Math.ceil(looseDollars * 0.20);
		int toRemove = looseLoss;
		for (int i = 0; i < invSize && toRemove > 0; i++) {
			ItemStack stack = player.getInventory().getItem(i);
			if (stack.is(MoneyItems.DOLLAR)) {
				int removed = Math.min(stack.getCount(), toRemove);
				stack.shrink(removed);
				toRemove -= removed;
			}
		}

		// Drop lost dollars
		int totalDropped = (looseLoss - toRemove) + walletDollarsLost;
		int remaining = totalDropped;
		while (remaining > 0) {
			ItemStack drop = new ItemStack(MoneyItems.DOLLAR);
			int amount = Math.min(remaining, drop.getMaxStackSize());
			drop.setCount(amount);
			player.drop(drop, false);
			remaining -= amount;
		}
	}
}
