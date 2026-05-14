package net.dingletherat;

import net.dingletherat.block.MoneyBlocks;
import net.dingletherat.block.entity.MoneyBlockEntities;
import net.dingletherat.datagen.MoneyBlockTagProvider;
import net.dingletherat.datagen.MoneyItemTagProvider;
import net.dingletherat.datagen.MoneyLootTableProvider;
import net.dingletherat.datagen.MoneyModels;
import net.dingletherat.datagen.MoneyRecipeProvider;
import net.dingletherat.item.MoneyGroups;
import net.dingletherat.item.MoneyItems;
import net.dingletherat.screen.MoneyMenus;
import net.dingletherat.state.*;
import net.dingletherat.villager.MoneyVillagers;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

@Mod(MoneyTalks.MOD_ID)
public class MoneyTalks {
	public static final String MOD_ID = "moneytalks";
	public static WalletState walletState;
	public static ShopState shopState;
	public static List<Item> nonTransferable;

	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LogUtils.getLogger();

	public MoneyTalks(IEventBus modEventBus, ModContainer modContainer) {
		MoneyItems.register(modEventBus);
		MoneyBlocks.register(modEventBus);
		MoneyBlockEntities.register(modEventBus);
		MoneyGroups.registerItemGroups(modEventBus);
		modEventBus.addListener(MoneyGroups::onBuildCreativeTab);
		MoneyVillagers.registerVillagers(modEventBus);
		MoneyMenus.register(modEventBus);
		modEventBus.addListener(this::onCommonSetup);
		modEventBus.addListener(this::onGatherData);

		// Events
		NeoForge.EVENT_BUS.addListener(this::onServerStarted);
		NeoForge.EVENT_BUS.addListener(this::onPlayerDeath);

		LOGGER.info("Loaded " + MOD_ID + "!");
	}

	private void onCommonSetup(FMLCommonSetupEvent event) {
	    nonTransferable = List.of(MoneyItems.DOLLAR.get(), MoneyBlocks.DOUBLOON.get().asItem());
	}

	private void onGatherData(GatherDataEvent.Client event) {
		DataGenerator generator = event.getGenerator();
		CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

		generator.addProvider(true, new MoneyModels(generator.getPackOutput()));
		generator.addProvider(true, new MoneyBlockTagProvider(generator.getPackOutput(), lookupProvider));
		generator.addProvider(true, new MoneyItemTagProvider(generator.getPackOutput(), lookupProvider));
		generator.addProvider(true, new MoneyRecipeProvider.Runner(generator.getPackOutput(), lookupProvider));
		generator.addProvider(true, new MoneyLootTableProvider(generator.getPackOutput(), lookupProvider));
	}

	private void onServerStarted(ServerStartedEvent event) {
		walletState = WalletState.get(event.getServer());
		shopState = ShopState.get(event.getServer());
	}

	private void onPlayerDeath(LivingDeathEvent event) {
		if (!(event.getEntity() instanceof ServerPlayer player)) return;
		handleDoubloonLoss(player, player);
		if (!(event.getSource().getDirectEntity() instanceof ServerPlayer)) return;
		handleDollarLoss(player, player);
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
			ItemStack drop = new ItemStack(MoneyItems.DOLLAR.get());
			int amount = Math.min(remaining, drop.getMaxStackSize());
			drop.setCount(amount);
			player.drop(drop, false);
			remaining -= amount;
		}
	}
}
