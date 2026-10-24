package net.dingletherat;

import net.dingletherat.registry.MoneyBlocks;
import net.dingletherat.registry.MoneyBlockEntities;
import net.dingletherat.registry.MoneyDataComponents;
import net.dingletherat.registry.MoneyTabs;
import net.dingletherat.registry.MoneyItems;
import net.dingletherat.item.Wallet;
import net.dingletherat.registry.MoneyMenus;
import net.dingletherat.state.*;
import net.dingletherat.registry.MoneyVillagers;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Prediction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.LifecycleEvent;

public class MoneyTalks {
    public static final String MOD_ID = "moneytalks";

    // Note to self: run datagen when changing
    public static final int ADVANCEMENT_DEEP_POCKETS_GOAL = 1000;
    public static final int ADVANCEMENT_MONOPOLY_GOAL = 100;
    public static final int ADVANCEMENT_RICHEST_PIRATE_GOAL = 192;
    public static final int TEXTURE_WALLET_HALF_FULL_GOAL = 1;
    public static final int TEXTURE_WALLET_FULL_GOAL = ADVANCEMENT_DEEP_POCKETS_GOAL;

    public static WalletState walletState;

    // This logger is used to write text to the console and the log file.
    // It is considered best practice to use your mod id as the logger's name.
    // That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);


    public static void initialize() {
        // Order matters on Fabric, where each register is applied immediately
        MoneyDataComponents.initialize();
        MoneyBlocks.initialize();
        MoneyItems.initialize();
        MoneyTabs.initialize();
        MoneyBlockEntities.initialize();
        MoneyMenus.initialize();
        MoneyVillagers.initialize();

        LifecycleEvent.SERVER_STARTED.register(server -> {
            walletState = WalletState.get(server);
        });

        EntityEvent.LIVING_DEATH.register((entity, damageSource) -> {
            if (!(entity instanceof ServerPlayer player)) return EventResult.pass();
            handleDoubloonLoss(player, player);
            if (!(damageSource.getDirectEntity() instanceof ServerPlayer)) return EventResult.pass();
            handleDollarLoss(player, player);
            return EventResult.pass();
        });

        LOGGER.info("Loaded " + MOD_ID + "!");
    }
    // Items that must stay in a player's inventory (no hoppers, containers or merchant carpets)
    public static boolean isNonTransferable(Item item) {
        return item == MoneyItems.COIN.get() || item == MoneyBlocks.DOUBLOON.get().asItem();
    }

    public static void handleDoubloonLoss(ServerPlayer oldPlayer, ServerPlayer newPlayer) {
        for (int i = 0; i < oldPlayer.getInventory().getContainerSize(); i++) {
            ItemStack stack = oldPlayer.getInventory().getItem(i);
            if (stack.is(MoneyBlocks.DOUBLOON.get().asItem())) {
                oldPlayer.getInventory().setItem(i, ItemStack.EMPTY);
                ItemEntity itemEntity = oldPlayer.createItemStackToDrop(stack, true, false);
                if (itemEntity != null) oldPlayer.level().addFreshEntity(itemEntity);
            }
        }
    }
    public static void handleDollarLoss(ServerPlayer oldPlayer, ServerPlayer player) {
        int invSize = player.getInventory().getContainerSize();

        // Deduct 15% from wallets in player inventory
        int walletDollarsLost = 0;
        Set<UUID> inventoryWalletIds = new HashSet<>();
        for (int i = 0; i < invSize; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(MoneyItems.WALLET.get()) && stack.has(MoneyDataComponents.COINS.get())) {
                int stored = stack.get(MoneyDataComponents.COINS.get());
                int loss = (int) Math.ceil(stored * 0.15);
                int newAmount = Math.max(0, stored - loss);
                stack.set(MoneyDataComponents.COINS.get(), newAmount);
                walletDollarsLost += loss;
                UUID walletId = stack.get(MoneyDataComponents.WALLET_ID.get());
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
            if (stack.is(MoneyItems.COIN.get())) looseDollars += stack.getCount();
        }
        int looseLoss = (int) Math.ceil(looseDollars * 0.20);
        int toRemove = looseLoss;
        for (int i = 0; i < invSize && toRemove > 0; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(MoneyItems.COIN.get())) {
                int removed = Math.min(stack.getCount(), toRemove);
                stack.shrink(removed);
                toRemove -= removed;
            }
        }

        // Drop lost dollars
        int totalDropped = (looseLoss - toRemove) + walletDollarsLost;
        int remaining = totalDropped;
        while (remaining > 0) {
            ItemStack drop = new ItemStack(MoneyItems.COIN.get());
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
