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
    private static final String NBT_DOLLARS = "Dollars";
    private static final String NBT_OWNER = "Owner";
    private static final String NBT_WALLET_ID = "WalletId";
    private static final int ITEM_BAR_COLOR = (255 << 24) | (255 << 16) | (214 << 8) | 0;

    public Wallet(Properties properties) {
        super(properties);
    }

    public static int getDollars(ItemStack wallet) {
        CustomData data = wallet.get(DataComponents.CUSTOM_DATA);
        if (data == null) return 0;
        return data.copyTag().getInt(NBT_DOLLARS).orElse(0);
    }

    public static void setDollars(ItemStack wallet, int count) {
        CompoundTag nbt = wallet.has(DataComponents.CUSTOM_DATA)
                ? wallet.get(DataComponents.CUSTOM_DATA).copyTag()
                : new CompoundTag();
        nbt.putInt(NBT_DOLLARS, Math.max(0, count));
        wallet.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
    }

    public static String getOwner(ItemStack wallet) {
        CustomData data = wallet.get(DataComponents.CUSTOM_DATA);
        if (data == null) return null;
        String owner = data.copyTag().getString(NBT_OWNER).orElse("");
        return owner.isEmpty() ? null : owner;
    }

    private static boolean isDollar(ItemStack stack) {
        return !stack.isEmpty() && stack.is(MoneyItems.DOLLAR);
    }

    private static boolean insertDollars(ItemStack wallet, ItemStack incoming, Player player) {
        if (!isDollar(incoming) || incoming.isEmpty()) return false;
        CompoundTag nbt = wallet.has(DataComponents.CUSTOM_DATA)
                ? wallet.get(DataComponents.CUSTOM_DATA).copyTag()
                : new CompoundTag();
        nbt.putInt(NBT_DOLLARS, Math.max(0, nbt.getInt(NBT_DOLLARS).orElse(0) + incoming.getCount()));
        nbt.putString(NBT_OWNER, player.getName().getString());
        wallet.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
        incoming.setCount(0);
        notifyScreenHandler(player);
        return true;
    }

    private static ItemStack removeDollars(ItemStack wallet, Player player) {
        int current = getDollars(wallet);
        if (current <= 0) return ItemStack.EMPTY;
        ItemStack dollar = MoneyItems.DOLLAR.getDefaultInstance();
        int take = Math.min(current, dollar.getMaxStackSize());
        dollar.setCount(take);
        setDollars(wallet, current - take);
        notifyScreenHandler(player);
        return dollar;
    }

    private static void notifyScreenHandler(Player player) {
        if (player.containerMenu != null)
            player.containerMenu.slotsChanged(player.getInventory());
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

            // Only override NBT if a deduction is pending (e.g. died while wallet was in container)
            if (MoneyTalks.walletState.hasPendingDeduction(walletId)) {
                Integer deductedAmount = MoneyTalks.walletState.consumePendingDeduction(walletId);
                if (deductedAmount != null) {
                    nbt.putInt(NBT_DOLLARS, deductedAmount);
                }
            }

            nbt.putString(NBT_OWNER, player.getName().getString());
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));

            // NBT is authoritative — mirror it into WalletState
            MoneyTalks.walletState.update(walletId, player.getName().getString(), nbt.getInt(NBT_DOLLARS).orElse(0));
        }
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack wallet, Slot slot, ClickAction clickType, Player player) {
        if (clickType == ClickAction.SECONDARY && !slot.getItem().isEmpty()) {
            if (!isDollar(slot.getItem())) {
                playInsertFailSound(player);
                return true;
            }
            if (insertDollars(wallet, slot.getItem(), player)) {
                slot.set(ItemStack.EMPTY);
                playInsertSound(player);
            } else {
                playInsertFailSound(player);
            }
            return true;
        }
        if (clickType == ClickAction.PRIMARY && slot.getItem().isEmpty()) {
            ItemStack removed = removeDollars(wallet, player);
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
            if (!isDollar(otherStack)) {
                playInsertFailSound(player);
                return true;
            }

            if (insertDollars(wallet, otherStack, player)) playInsertSound(player);
            else playInsertFailSound(player);

            return true;
        }
        if (clickType == ClickAction.SECONDARY && otherStack.isEmpty()) {
            ItemStack removed = removeDollars(wallet, player);
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
        int dollars = getDollars(stack);
        String owner = getOwner(stack);

        // Prefer WalletState value if available — it reflects deductions applied on death
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        String walletId = customData != null ? customData.copyTag().getString(NBT_WALLET_ID).orElse("") : "";
        if (!walletId.isEmpty() && MoneyTalks.walletState != null) {
            WalletState.WalletEntry entry = MoneyTalks.walletState.get(UUID.fromString(walletId));
            if (entry != null) dollars = entry.dollars;
        }

        String phrase = owner != null ? ("Inside " + owner + "'s Wallet is " + dollars + " Dollars") : ("Inside this Wallet is " + dollars + " Dollars");
        textConsumer.accept(Component.literal(phrase)
                .withStyle(dollars > 0 ? ChatFormatting.GOLD : ChatFormatting.GRAY));
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        return Optional.empty();
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        return getDollars(stack) > 0;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        int count = getDollars(stack);
        return Math.min(1 + (count * 12 / 1000), 13);
    }

    @Override
    public int getBarColor(ItemStack stack) {
        return ITEM_BAR_COLOR;
    }

    @Override
    public void onDestroyed(ItemEntity entity) {
        int dollars = getDollars(entity.getItem());
        setDollars(entity.getItem(), 0);
        while (dollars > 0) {
            ItemStack drop = MoneyItems.DOLLAR.getDefaultInstance();
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
