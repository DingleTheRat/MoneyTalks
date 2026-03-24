package net.dingletherat.item.custom;

import net.dingletherat.MoneyTalks;
import net.dingletherat.item.MoneyItems;
import net.dingletherat.state.WalletState;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.BundleItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.slot.Slot;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ClickType;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.ColorHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

public class Wallet extends BundleItem {
    private static final String NBT_DOLLARS = "Dollars";
    private static final String NBT_OWNER = "Owner";
    private static final String NBT_WALLET_ID = "WalletId";
    private static final int ITEM_BAR_COLOR = ColorHelper.fromFloats(1.0F, 1.0F, 0.84F, 0.0F);

    public Wallet(Settings settings) {
        super(settings);
    }

    private static int getDollars(ItemStack wallet) {
        NbtComponent data = wallet.get(DataComponentTypes.CUSTOM_DATA);
        if (data == null) return 0;
        return data.copyNbt().getInt(NBT_DOLLARS, 0);
    }

    private static void setDollars(ItemStack wallet, int count) {
        NbtCompound nbt = wallet.contains(DataComponentTypes.CUSTOM_DATA)
                ? wallet.get(DataComponentTypes.CUSTOM_DATA).copyNbt()
                : new NbtCompound();
        nbt.putInt(NBT_DOLLARS, Math.max(0, count));
        wallet.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
    }

    private static String getOwner(ItemStack wallet) {
        NbtComponent data = wallet.get(DataComponentTypes.CUSTOM_DATA);
        if (data == null) return null;
        String owner = data.copyNbt().getString(NBT_OWNER, "");
        return owner.isEmpty() ? null : owner;
    }

    private static boolean isDollar(ItemStack stack) {
        return !stack.isEmpty() && stack.isOf(MoneyItems.DOLLAR);
    }

    private static boolean insertDollars(ItemStack wallet, ItemStack incoming, PlayerEntity player) {
        if (!isDollar(incoming) || incoming.isEmpty()) return false;
        NbtCompound nbt = wallet.contains(DataComponentTypes.CUSTOM_DATA)
                ? wallet.get(DataComponentTypes.CUSTOM_DATA).copyNbt()
                : new NbtCompound();
        nbt.putInt(NBT_DOLLARS, Math.max(0, nbt.getInt(NBT_DOLLARS, 0) + incoming.getCount()));
        nbt.putString(NBT_OWNER, player.getName().getString());
        wallet.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
        incoming.setCount(0);
        notifyScreenHandler(player);
        return true;
    }

    private static ItemStack removeDollars(ItemStack wallet, PlayerEntity player) {
        int current = getDollars(wallet);
        if (current <= 0) return ItemStack.EMPTY;
        ItemStack dollar = MoneyItems.DOLLAR.getDefaultStack();
        int take = Math.min(current, dollar.getMaxCount());
        dollar.setCount(take);
        setDollars(wallet, current - take);
        notifyScreenHandler(player);
        return dollar;
    }

    private static void notifyScreenHandler(PlayerEntity player) {
        if (player.currentScreenHandler != null) {
            player.currentScreenHandler.onContentChanged(player.getInventory());
        }
    }


    @Override
    public void inventoryTick(ItemStack stack, ServerWorld world, Entity entity, @Nullable EquipmentSlot slot) {
        if (entity instanceof PlayerEntity player) {
            if (MoneyTalks.walletState == null)
                MoneyTalks.walletState = WalletState.get(world.getServer());

            NbtCompound nbt = stack.contains(DataComponentTypes.CUSTOM_DATA)
                    ? stack.get(DataComponentTypes.CUSTOM_DATA).copyNbt()
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
            stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));

            // NBT is authoritative — mirror it into WalletState
            MoneyTalks.walletState.update(walletId, player.getName().getString(), nbt.getInt(NBT_DOLLARS, 0));
        }
    }

    @Override
    public boolean onStackClicked(ItemStack wallet, Slot slot, ClickType clickType, PlayerEntity player) {
        if (clickType == ClickType.LEFT && !slot.getStack().isEmpty()) {
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
        if (clickType == ClickType.RIGHT && slot.getStack().isEmpty()) {
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
    public boolean onClicked(ItemStack wallet, ItemStack otherStack, Slot slot,
                             ClickType clickType, PlayerEntity player,
                             StackReference cursorStackReference) {
        if (clickType == ClickType.LEFT && !otherStack.isEmpty()) {
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
        if (clickType == ClickType.RIGHT && otherStack.isEmpty()) {
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
    public void appendTooltip(ItemStack stack, Item.TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        int dollars = getDollars(stack);
        String owner = getOwner(stack);

        // Prefer WalletState value if available — it reflects deductions applied on death
        NbtComponent customData = stack.get(DataComponentTypes.CUSTOM_DATA);
        String walletId = customData != null ? customData.copyNbt().getString(NBT_WALLET_ID, "") : "";
        if (!walletId.isEmpty() && MoneyTalks.walletState != null) {
            WalletState.WalletEntry entry = MoneyTalks.walletState.get(UUID.fromString(walletId));
            if (entry != null) dollars = entry.dollars;
        }

        String phrase = owner != null ? ("Inside " + owner + "'s Wallet is " + dollars + " Dollars") : ("Inside this Wallet is " + dollars + " Dollars");
        textConsumer.accept(Text.literal(phrase)
                .formatted(dollars > 0 ? Formatting.GOLD : Formatting.GRAY));
    }

    @Override
    public Optional<TooltipData> getTooltipData(ItemStack stack) {
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
