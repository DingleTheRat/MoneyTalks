package net.dingletherat.item.custom;

import net.dingletherat.MoneyTalks;
import net.dingletherat.item.MoneyItems;
import net.dingletherat.state.WalletState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.ChatFormatting;
import org.jetbrains.annotations.Nullable;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class Wallet extends BundleItem {
    private static final String NBT_COINS = "Coins";
    private static final String NBT_OWNER = "Owner";
    public static final String NBT_WALLET_ID = "WalletId";
    private static final int ITEM_BAR_COLOR = (255 << 24) | (255 << 16) | (214 << 8) | 0;

    public Wallet(Properties properties) {
        super(properties);
    }

    public static int getCoins(ItemStack wallet) {
        CustomData data = wallet.get(DataComponents.CUSTOM_DATA);
        if (data == null) return 0;
        return data.copyTag().getInt(NBT_COINS).orElse(0);
    }

    public static void setCoins(ItemStack wallet, int count) {
        CompoundTag nbt = wallet.has(DataComponents.CUSTOM_DATA)
                ? wallet.get(DataComponents.CUSTOM_DATA).copyTag()
                : new CompoundTag();
        nbt.putInt(NBT_COINS, Math.max(0, count));
        wallet.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
    }

    public static String getOwner(ItemStack wallet) {
        CustomData data = wallet.get(DataComponents.CUSTOM_DATA);
        if (data == null) return null;
        String owner = data.copyTag().getString(NBT_OWNER).orElse("");
        return owner.isEmpty() ? null : owner;
    }

    private static boolean isCoin(ItemStack stack) {
        return !stack.isEmpty() && stack.is(MoneyItems.COIN);
    }

    private static boolean insertCoins(ItemStack wallet, ItemStack incoming, Player player) {
        if (!isCoin(incoming) || incoming.isEmpty()) return false;

        CompoundTag nbt = wallet.has(DataComponents.CUSTOM_DATA)
                ? wallet.get(DataComponents.CUSTOM_DATA).copyTag()
                : new CompoundTag();
        nbt.putInt(NBT_COINS, Math.max(0, nbt.getInt(NBT_COINS).orElse(0) + incoming.getCount()));
        nbt.putString(NBT_OWNER, player.getName().getString());

        wallet.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
        incoming.setCount(0);

        notifyScreenHandler(player);
        return true;
    }

    private static ItemStack removeCoins(ItemStack wallet, Player player) {
        int current = getCoins(wallet);
        if (current <= 0) return ItemStack.EMPTY;
        ItemStack dollar = MoneyItems.COIN.getDefaultInstance();
        int take = Math.min(current, dollar.getMaxStackSize());
        dollar.setCount(take);
        setCoins(wallet, current - take);
        notifyScreenHandler(player);
        return dollar;
    }

    private static void notifyScreenHandler(Player player) {
        if (player.containerMenu != null) player.containerMenu.slotsChanged(player.getInventory());
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel world, Entity entity, @Nullable EquipmentSlot slot) {
        if (entity instanceof Player player) {
            if (MoneyTalks.walletState == null)
                MoneyTalks.walletState = WalletState.get(world.getServer());

            CompoundTag nbt = stack.has(DataComponents.CUSTOM_DATA)
                    ? stack.get(DataComponents.CUSTOM_DATA).copyTag()
                    : new CompoundTag();

            if (nbt.getString(NBT_WALLET_ID).orElse("").isEmpty())
                nbt.putString(NBT_WALLET_ID, UUID.randomUUID().toString());

            UUID walletId = UUID.fromString(nbt.getString(NBT_WALLET_ID).orElse(""));

            if (MoneyTalks.walletState.hasPendingDeduction(walletId)) {
                Integer deductedAmount = MoneyTalks.walletState.consumePendingDeduction(walletId);
                if (deductedAmount != null) nbt.putInt(NBT_COINS, deductedAmount);
            }

            nbt.putString(NBT_OWNER, player.getName().getString());
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));

            // Since creative inventory clicks never reach the server, grant advancements here
            int coins = nbt.getIntOr(NBT_COINS, 0);
            WalletState.WalletEntry previous = MoneyTalks.walletState.get(walletId);
            if (coins > (previous != null ? previous.coins : 0)) MoneyTalks.grantAdvancement(player, "pocket_change", "inserted_coin");

            int deepPocketsGoal = MoneyTalks.ADVANCEMENT_DEEP_POCKETS_GOAL;
            if (coins >= deepPocketsGoal) MoneyTalks.grantAdvancement(player, "deep_pockets", "inserted_" + deepPocketsGoal + "_coins");

            MoneyTalks.walletState.update(walletId, player.getName().getString(), coins);
        }
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
        if (clickType == ClickAction.SECONDARY && slot.getItem().isEmpty() && getCoins(wallet) != 0) {
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
    public void appendHoverText(ItemStack stack, BundleItem.TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> textConsumer, TooltipFlag type) {
        int coins = getCoins(stack);
        String owner = getOwner(stack);

        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        String walletId = customData != null ? customData.copyTag().getString(NBT_WALLET_ID).orElse("") : "";
        if (!walletId.isEmpty() && MoneyTalks.walletState != null) {
            WalletState.WalletEntry entry = MoneyTalks.walletState.get(UUID.fromString(walletId));
            if (entry != null) coins = entry.coins;
        }

        String phrase = owner != null ? (owner + "'s Wallet has " + coins + " coins") : ("This Wallet has " + coins + " coins");
        textConsumer.accept(Component.literal(phrase).withStyle(coins > 0 ? ChatFormatting.GOLD : ChatFormatting.GRAY));
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return Optional.empty();
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return getCoins(stack) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        int count = getCoins(stack);
        return Math.min(1 + (count * 12 / 1000), 13);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return ITEM_BAR_COLOR;
    }

    @Override
    public void onDestroyed(ItemEntity entity) {
        int dollars = getCoins(entity.getItem());
        setCoins(entity.getItem(), 0);
        while (dollars > 0) {
            ItemStack drop = MoneyItems.COIN.getDefaultInstance();
            int amount = Math.min(dollars, drop.getMaxStackSize());
            drop.setCount(amount);
            entity.level().addFreshEntity(new ItemEntity(
                    entity.level(),
                    entity.getX(), entity.getY(), entity.getZ(),
                    drop
            ));
            dollars -= amount;
        }
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.get(DataComponents.ENCHANTMENT_GLINT_OVERRIDE) == null ? false : true;
    }

    private static void playInsertSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_INSERT, 0.8F,
                0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }

    private static void playInsertFailSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_INSERT_FAIL, 1.0F, 1.0F);
    }

    private static void playRemoveOneSound(Entity entity) {
        entity.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F,
                0.8F + entity.level().getRandom().nextFloat() * 0.4F);
    }
}
