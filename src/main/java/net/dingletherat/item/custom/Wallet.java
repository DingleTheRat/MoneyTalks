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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.inventory.Slot;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.ChatFormatting;
import net.minecraft.util.ARGB;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class Wallet extends BundleItem {
    private static final String NBT_DOLLARS = "Dollars";
    private static final String NBT_OWNER = "Owner";
    private static final String NBT_WALLET_ID = "WalletId";
    private static final int ITEM_BAR_COLOR = ARGB.fromFloats(1.0F, 1.0F, 0.84F, 0.0F);

    public Wallet(Settings settings) {
        super(settings);
    }

    private static int getDollars(ItemStack wallet) {
        CustomData data = wallet.get(DataComponents.CUSTOM_DATA);
        if (data == null) return 0;
        return data.copyNbt().getInt(NBT_DOLLARS, 0);
    }

    private static void setDollars(ItemStack wallet, int count) {
        CompoundTag nbt = wallet.contains(DataComponents.CUSTOM_DATA)
                ? wallet.get(DataComponents.CUSTOM_DATA).copyNbt()
                : new NbtCompound();
        nbt.putInt(NBT_DOLLARS, Math.max(0, count));
        wallet.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
    }

    private static String getOwner(ItemStack wallet) {
        CustomData data = wallet.get(DataComponents.CUSTOM_DATA);
        if (data == null) return null;
        String owner = data.copyNbt().getString(NBT_OWNER, "");
        return owner.isEmpty() ? null : owner;
    }

    private static boolean isDollar(ItemStack stack) {
        return !stack.isEmpty() && stack.isOf(MoneyItems.DOLLAR);
    }

    private static boolean insertDollars(ItemStack wallet, ItemStack incoming, Player player) {
        if (!isDollar(incoming) || incoming.isEmpty()) return false;
        CompoundTag nbt = wallet.contains(DataComponents.CUSTOM_DATA)
                ? wallet.get(DataComponents.CUSTOM_DATA).copyNbt()
                : new NbtCompound();
        nbt.putInt(NBT_DOLLARS, Math.max(0, nbt.getInt(NBT_DOLLARS, 0) + incoming.getCount()));
        nbt.putString(NBT_OWNER, player.getName().getString());
        wallet.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
        incoming.setCount(0);
        notifyScreenHandler(player);
        return true;
    }

    private static ItemStack removeDollars(ItemStack wallet, Player player) {
        int current = getDollars(wallet);
        if (current <= 0) return ItemStack.EMPTY;
        ItemStack dollar = MoneyItems.DOLLAR.getDefaultStack();
        int take = Math.min(current, dollar.getMaxCount());
        dollar.setCount(take);
        setDollars(wallet, current - take);
        notifyScreenHandler(player);
        return dollar;
    }

    private static void notifyScreenHandler(Player player) {
        if (player.currentScreenHandler != null) {
            player.currentScreenHandler.onContentChanged(player.getInventory());
        }
    }


    @Override
    public void inventoryTick(ItemStack stack, ServerLevel world, Entity entity, @Nullable EquipmentSlot slot) {
        if (entity instanceof Player player) {
            if (MoneyTalks.walletState == null)
                MoneyTalks.walletState = WalletState.get(world.getServer());

            CompoundTag nbt = stack.contains(DataComponents.CUSTOM_DATA)
                    ? stack.get(DataComponents.CUSTOM_DATA).copyNbt()
                    : new NbtCompound();

            if (nbt.getString(NBT_WALLET_ID, "").isEmpty())
                nbt.putString(NBT_WALLET_ID, UUID.randomUUID().toString());

            UUID walletId = UUID.fromString(nbt.getString(NBT_WALLET_ID, ""));

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
            MoneyTalks.walletState.update(walletId, player.getName().getString(), nbt.getInt(NBT_DOLLARS, 0));
        }
    }

    @Override
    public boolean onStackClicked(ItemStack wallet, EquipmentSlot slot, ClickAction clickType, Player player) {
        if (clickType == ClickAction.LEFT && !slot.getStack().isEmpty()) {
            if (!isDollar(slot.getStack())) {
                playInsertFailSound(player);
                return true;
            }
            if (insertDollars(wallet, slot.getStack(), player)) {
                slot.setStack(ItemStack.EMPTY);
                playInsertSound(player);
            } else {
                playInsertFailSound(player);
            }
            return true;
        }
        if (clickType == ClickAction.RIGHT && slot.getStack().isEmpty()) {
            ItemStack removed = removeDollars(wallet, player);
            if (!removed.isEmpty()) {
                slot.setStack(removed);
                playRemoveOneSound(player);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onClicked(ItemStack wallet, ItemStack otherStack, EquipmentSlot slot,
                             ClickAction clickType, Player player,
                             SlotAccess cursorStackReference) {
        if (clickType == ClickAction.LEFT && !otherStack.isEmpty()) {
            if (!isDollar(otherStack)) {
                playInsertFailSound(player);
                return true;
            }
            if (insertDollars(wallet, otherStack, player)) {
                playInsertSound(player);
            } else {
                playInsertFailSound(player);
            }
            return true;
        }
        if (clickType == ClickAction.RIGHT && otherStack.isEmpty()) {
            ItemStack removed = removeDollars(wallet, player);
            if (!removed.isEmpty()) {
                cursorStackReference.set(removed);
                playRemoveOneSound(player);
            }
            return true;
        }
        return false;
    }

    @Override
    public void appendTooltip(ItemStack stack, BundleItem.TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> textConsumer, TooltipFlag type) {
        int dollars = getDollars(stack);
        String owner = getOwner(stack);

        // Prefer WalletState value if available — it reflects deductions applied on death
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        String walletId = customData != null ? customData.copyNbt().getString(NBT_WALLET_ID, "") : "";
        if (!walletId.isEmpty() && MoneyTalks.walletState != null) {
            WalletState.WalletEntry entry = MoneyTalks.walletState.get(UUID.fromString(walletId));
            if (entry != null) dollars = entry.dollars;
        }

        String phrase = owner != null ? ("Inside " + owner + "'s Wallet is " + dollars + " Dollars") : ("Inside this Wallet is " + dollars + " Dollars");
        textConsumer.accept(Component.literal(phrase)
                .formatted(dollars > 0 ? ChatFormatting.GOLD : ChatFormatting.GRAY));
    }

    @Override
    public Optional<TooltipComponent> getTooltipData(ItemStack stack) {
        return Optional.empty();
    }

    @Override
    public boolean isItemBarVisible(ItemStack stack) {
        return getDollars(stack) > 0;
    }

    @Override
    public int getItemBarStep(ItemStack stack) {
        int count = getDollars(stack);
        return Math.min(1 + (count * 12 / 1000), 13);
    }

    @Override
    public int getItemBarColor(ItemStack stack) {
        return ITEM_BAR_COLOR;
    }

    @Override
    public void onItemEntityDestroyed(ItemEntity entity) {
        int dollars = getDollars(entity.getStack());
        setDollars(entity.getStack(), 0);
        while (dollars > 0) {
            ItemStack drop = MoneyItems.DOLLAR.getDefaultStack();
            int amount = Math.min(dollars, drop.getMaxCount());
            drop.setCount(amount);
            entity.getEntityWorld().spawnEntity(new ItemEntity(
                    entity.getEntityWorld(),
                    entity.getX(), entity.getY(), entity.getZ(),
                    drop
            ));
            dollars -= amount;
        }
    }

    private static void playInsertSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_BUNDLE_INSERT, 0.8F,
                0.8F + entity.getEntityWorld().getRandom().nextFloat() * 0.4F);
    }

    private static void playInsertFailSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_BUNDLE_INSERT_FAIL, 1.0F, 1.0F);
    }

    private static void playRemoveOneSound(Entity entity) {
        entity.playSound(SoundEvents.ITEM_BUNDLE_REMOVE_ONE, 0.8F,
                0.8F + entity.getEntityWorld().getRandom().nextFloat() * 0.4F);
    }
}
