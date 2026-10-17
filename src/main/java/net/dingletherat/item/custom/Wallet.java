package net.dingletherat.item.custom;

import net.dingletherat.MoneyTalks;
import net.dingletherat.item.LegacyData;
import net.dingletherat.item.MoneyDataComponents;
import net.dingletherat.item.MoneyItems;
import net.dingletherat.state.WalletState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.ResolvableProfile;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.item.BundleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.ChatFormatting;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class Wallet extends BundleItem {
    private static final int ITEM_BAR_COLOR = (255 << 24) | (255 << 16) | (214 << 8) | 0;

    public Wallet(Properties properties) {
        super(properties);
    }

    public static String getOwnerName(ItemStack wallet) {
        ResolvableProfile owner = wallet.get(MoneyDataComponents.OWNER);
        return owner != null ? owner.name().orElse(null) : null;
    }

    private static boolean isCoin(ItemStack stack) {
        return !stack.isEmpty() && stack.is(MoneyItems.COIN);
    }

    private static boolean insertCoins(ItemStack wallet, ItemStack incomingCoins, Player player) {
        if (!isCoin(incomingCoins) || incomingCoins.isEmpty()) return false;

        int coins = wallet.getOrDefault(MoneyDataComponents.COINS, 0);
        wallet.set(MoneyDataComponents.COINS, coins + incomingCoins.getCount());
        wallet.set(MoneyDataComponents.OWNER, ResolvableProfile.createResolved(player.getGameProfile()));
        incomingCoins.setCount(0);

        notifyScreenHandler(player);
        return true;
    }

    private static ItemStack removeCoins(ItemStack wallet, Player player) {
        if (!wallet.has(MoneyDataComponents.COINS)) return ItemStack.EMPTY;

        int coins = wallet.get(MoneyDataComponents.COINS);
        if (coins <= 0) return ItemStack.EMPTY;

        ItemStack coin = MoneyItems.COIN.getDefaultInstance();
        int toTake = Math.min(coins, coin.getMaxStackSize());
        coin.setCount(toTake);
        wallet.set(MoneyDataComponents.COINS, coins - toTake);

        notifyScreenHandler(player);
        return coin;
    }

    private static void notifyScreenHandler(Player player) {
        if (player.containerMenu != null) player.containerMenu.slotsChanged(player.getInventory());
    }

    @Override
    public void inventoryTick(ItemStack wallet, ServerLevel world, Entity entity, EquipmentSlot slot) {
        if (!(entity instanceof Player player)) return;

        // Data setters
        LegacyData.migrate(wallet);
        if (MoneyTalks.walletState == null) MoneyTalks.walletState = WalletState.get(world.getServer());
        if (!wallet.has(MoneyDataComponents.COINS)) wallet.set(MoneyDataComponents.COINS, 0);
        if (!wallet.has(MoneyDataComponents.OWNER)) wallet.set(MoneyDataComponents.OWNER, ResolvableProfile.createResolved(player.getGameProfile()));
        if (!wallet.has(MoneyDataComponents.WALLET_ID)) wallet.set(MoneyDataComponents.WALLET_ID, UUID.randomUUID());

        UUID walletId = wallet.get(MoneyDataComponents.WALLET_ID);

        if (MoneyTalks.walletState.hasPendingDeduction(walletId)) {
            Integer deductedAmount = MoneyTalks.walletState.consumePendingDeduction(walletId);
            if (deductedAmount != null) wallet.set(MoneyDataComponents.COINS, deductedAmount);
        }

        int coins = wallet.get(MoneyDataComponents.COINS);

        // Since creative inventory clicks never reach the server, grant advancements here
        WalletState.WalletEntry previous = MoneyTalks.walletState.get(walletId);
        if (coins > (previous != null ? previous.coins : 0)) MoneyTalks.grantAdvancement(player, "pocket_change", "inserted_coin");

        int deepPocketsGoal = MoneyTalks.ADVANCEMENT_DEEP_POCKETS_GOAL;
        if (coins >= deepPocketsGoal) MoneyTalks.grantAdvancement(player, "deep_pockets", "inserted_" + deepPocketsGoal + "_coins");

        MoneyTalks.walletState.update(walletId, player.getName().getString(), coins);
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack wallet, Slot slot, ClickAction clickType, Player player) {
        if (clickType == ClickAction.PRIMARY && !slot.getItem().isEmpty()) {
            if (!isCoin(slot.getItem())) {
                playInsertFailSound(player);
                return true;
            }
            if (insertCoins(wallet, slot.getItem(), player)) {
                slot.set(ItemStack.EMPTY);
                playInsertSound(player);
            } else
                playInsertFailSound(player);
            return true;
        }
        if (clickType == ClickAction.SECONDARY && slot.getItem().isEmpty() && wallet.getOrDefault(MoneyDataComponents.COINS, 0) != 0) {
            ItemStack removed = removeCoins(wallet, player);
            if (!removed.isEmpty()) {
                slot.set(removed);
                playRemoveOneSound(player);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack wallet, ItemStack otherStack, Slot slot, ClickAction clickType, Player player, SlotAccess carriedItem) {
        if (clickType == ClickAction.PRIMARY && !otherStack.isEmpty()) {
            if (!isCoin(otherStack)) {
                playInsertFailSound(player);
                return true;
            }

            if (insertCoins(wallet, otherStack, player)) playInsertSound(player);
            else playInsertFailSound(player);

            return true;
        }
        if (clickType == ClickAction.SECONDARY && otherStack.isEmpty()) {
            ItemStack removed = removeCoins(wallet, player);
            if (!removed.isEmpty()) {
                carriedItem.set(removed);
                playRemoveOneSound(player);
            }
            return true;
        }
        return false;
    }

    @Override
    public void appendHoverText(ItemStack wallet, BundleItem.TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> textConsumer, TooltipFlag type) {
        int coins = wallet.getOrDefault(MoneyDataComponents.COINS, 0);
        String owner = getOwnerName(wallet);

        String phrase = owner != null ? (owner + "'s Wallet has " + coins + " coins") : ("This Wallet has " + coins + " coins");
        textConsumer.accept(Component.literal(phrase).withStyle(coins > 0 ? ChatFormatting.GOLD : ChatFormatting.GRAY));
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return Optional.empty();
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return stack.getOrDefault(MoneyDataComponents.COINS, 0) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        int count = stack.getOrDefault(MoneyDataComponents.COINS, 0);
        return Math.min(1 + (count * 12 / 1000), 13);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return ITEM_BAR_COLOR;
    }

    @Override
    public void onDestroyed(ItemEntity entity) {
        ItemStack wallet = entity.getItem();
        int coins = wallet.getOrDefault(MoneyDataComponents.COINS, 0);
        wallet.set(MoneyDataComponents.COINS, 0);

        while (coins > 0) {
            ItemStack drop = MoneyItems.COIN.getDefaultInstance();
            int amount = Math.min(coins, drop.getMaxStackSize());
            drop.setCount(amount);
            entity.level().addFreshEntity(new ItemEntity(entity.level(), entity.getX(), entity.getY(), entity.getZ(), drop));
            coins -= amount;
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.get(DataComponents.ENCHANTMENT_GLINT_OVERRIDE) == null ? false : true;
    }

    private static void playInsertSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    private static void playInsertFailSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_INSERT_FAIL, 1.0F, 1.0F);
    }

    private static void playRemoveOneSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }
}
