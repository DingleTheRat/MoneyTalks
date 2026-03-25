package net.dingletherat;

import net.dingletherat.block.MoneyBlocks;
import net.dingletherat.block.entity.MoneyBlockEntities;
import net.dingletherat.item.MoneyGroups;
import net.dingletherat.item.MoneyItems;
import net.dingletherat.state.*;
import net.dingletherat.villager.MoneyVillagers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.ItemCost;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MoneyTalks implements ModInitializer {
	public static final String MOD_ID = "moneytalks";
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

		MoneyItems.registerModItems();
		MoneyVillagers.registerVillagers();
		MoneyBlocks.registerBlocks();
		MoneyBlockEntities.registerBlockEntities();
		MoneyGroups.registerItemGroups();
		LOGGER.info("Loaded MoneyTalks!");

		ServerLifecycleEvents.SERVER_STARTED.register(server -> {
			walletState = WalletState.get(server);
			shopState = ShopState.get(server);
		});

		ServerLivingEntityEvents.AFTER_DEATH.register((entity, damageSource) -> {
			if (!(entity instanceof ServerPlayer player)) return;
			if (!(damageSource.getAttacker() instanceof ServerPlayer)) return;
			handleDollarLoss(player, player);
		});

		// Trades
		TradeOfferHelper.registerVillagerOffers(MoneyVillagers.INVESTOR_KEY, 1, factories -> {
			factories.add((world, entity, random) -> new TradeOffer(
						new TradedItem(Items.EMERALD, 15),
						new ItemStack(MoneyItems.DOLLAR, 1),
						10, 2, 0.1f));
			factories.add((world, entity, random) -> new TradeOffer(
						new TradedItem(MoneyItems.DOLLAR, 2),
						new ItemStack(MoneyItems.WALLET, 1),
						1, 1, 0.04f));
		});
		TradeOfferHelper.registerVillagerOffers(MoneyVillagers.INVESTOR_KEY, 2, factories -> {
			factories.add((world, entity, random) -> new TradeOffer(
						new TradedItem(MoneyItems.DOLLAR, 1),
						new ItemStack(Items.SPRUCE_LOG, 8),
						10, 5, 0f));
			factories.add((world, entity, random) -> new TradeOffer(
						new TradedItem(MoneyItems.DOLLAR, 1),
						new ItemStack(Items.ACACIA_LOG, 8),
						10, 5, 0f));
			factories.add((world, entity, random) -> new TradeOffer(
						new TradedItem(MoneyItems.DOLLAR, 1),
						new ItemStack(Items.OAK_LOG, 8),
						10, 5, 0f));

			factories.add((world, entity, random) -> new TradeOffer(
						new TradedItem(Items.SPRUCE_LOG, 8),
						new ItemStack(MoneyItems.DOLLAR, 1),
						10, 4, 0f));
			factories.add((world, entity, random) -> new TradeOffer(
						new TradedItem(Items.ACACIA_LOG, 8),
						new ItemStack(MoneyItems.DOLLAR, 1),
						10, 4, 0f));
			factories.add((world, entity, random) -> new TradeOffer(
						new TradedItem(Items.OAK_LOG, 8),
						new ItemStack(MoneyItems.DOLLAR, 1),
						10, 4, 0f));
		});
		TradeOfferHelper.registerVillagerOffers(MoneyVillagers.INVESTOR_KEY, 3, factories -> {
			factories.add((world, entity, random) -> new TradeOffer(
						new TradedItem(Items.DIAMOND, 4),
						new ItemStack(MoneyItems.DOLLAR, 4),
						3, 8, 0f));
		});
		TradeOfferHelper.registerVillagerOffers(MoneyVillagers.INVESTOR_KEY, 4, factories -> {
			factories.add((world, entity, random) -> new TradeOffer(
						new TradedItem(MoneyItems.DOLLAR, 4),
						new ItemStack(Items.DIAMOND, 4),
						10, 8, 0.1f));
			factories.add((world, entity, random) -> new TradeOffer(
						new TradedItem(MoneyItems.DOLLAR, 9),
						new ItemStack(Items.GOLDEN_CARROT, 1),
						10, 8, 0.2f));
			factories.add((world, entity, random) -> new TradeOffer(
						new TradedItem(MoneyItems.DOLLAR, 12),
						new ItemStack(Items.GOLDEN_APPLE, 1),
						10, 8, 0.2f));
		});
		TradeOfferHelper.registerVillagerOffers(MoneyVillagers.INVESTOR_KEY, 5, factories -> {
			factories.add((world, entity, random) -> new TradeOffer(
						new TradedItem(Items.NETHERITE_SCRAP, 1),
						new ItemStack(MoneyItems.DOLLAR, 25),
						3, 9, 0f));
			factories.add((world, entity, random) -> new TradeOffer(
						new TradedItem(MoneyItems.DOLLAR, 25),
						new ItemStack(Items.NETHERITE_SCRAP, 1),
						1, 11, 0f));
			factories.add((world, entity, random) -> new TradeOffer(
						new TradedItem(MoneyItems.DOLLAR, 38),
						new ItemStack(Items.TOTEM_OF_UNDYING, 1),
						2, 11, 0f));
			factories.add((world, entity, random) -> new TradeOffer(
						new TradedItem(MoneyItems.DOLLAR, 40),
						new ItemStack(Items.ENCHANTED_GOLDEN_APPLE, 1),
						2, 11, 0.1f));
			factories.add((world, entity, random) -> new TradeOffer(
						new TradedItem(MoneyItems.DOLLAR, 64),
						new ItemStack(Items.NETHER_STAR, 1),
						1, 11, 0f));
		});
	}
	private void handleDollarLoss(ServerPlayer oldPlayer, ServerPlayer player) {
		int invSize = player.getInventory().size();

		// Deduct 15% from wallets in player inventory
		int walletDollarsLost = 0;
		Set<String> inventoryWalletIds = new HashSet<>();
		for (int i = 0; i < invSize; i++) {
			ItemStack stack = player.getInventory().getStack(i);
			if (stack.isOf(MoneyItems.WALLET) && stack.contains(DataComponents.CUSTOM_DATA)) {
				CompoundTag nbt = stack.get(DataComponents.CUSTOM_DATA).copyNbt();
				int stored = nbt.getInt("Dollars", 0);
				int loss = (int) Math.ceil(stored * 0.15);
				int newAmount = Math.max(0, stored - loss);
				nbt.putInt("Dollars", newAmount);
				stack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
				walletDollarsLost += loss;
				String walletId = nbt.getString("WalletId", "");
				if (!walletId.isEmpty()) {
					inventoryWalletIds.add(walletId);
					if (walletState != null) {
						walletState.update(UUID.fromString(walletId), nbt.getString("Owner", ""), newAmount);
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
			ItemStack stack = player.getInventory().getStack(i);
			if (stack.isOf(MoneyItems.DOLLAR)) looseDollars += stack.getCount();
		}
		int looseLoss = (int) Math.ceil(looseDollars * 0.20);
		int toRemove = looseLoss;
		for (int i = 0; i < invSize && toRemove > 0; i++) {
			ItemStack stack = player.getInventory().getStack(i);
			if (stack.isOf(MoneyItems.DOLLAR)) {
				int removed = Math.min(stack.getCount(), toRemove);
				stack.decrement(removed);
				toRemove -= removed;
			}
		}

		// Drop lost dollars
		int totalDropped = (looseLoss - toRemove) + walletDollarsLost;
		int remaining = totalDropped;
		while (remaining > 0) {
			ItemStack drop = new ItemStack(MoneyItems.DOLLAR);
			int amount = Math.min(remaining, drop.getMaxCount());
			drop.setCount(amount);
			player.dropItem(drop, false);
			remaining -= amount;
		}
	}
}
