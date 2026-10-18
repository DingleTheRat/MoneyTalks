package net.dingletherat.item;

import java.util.UUID;

import net.dingletherat.item.custom.TrimmedHopperItem;
import net.dingletherat.item.custom.Wallet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ResolvableProfile;

public class LegacyData {
    private static final String NBT_DOLLARS = "Dollars"; // Before dollars were renamed to coins
    private static final String NBT_COINS = "Coins";
    private static final String NBT_OWNER = "Owner";
    private static final String NBT_WALLET_ID = "WalletId";
    private static final String NBT_LINKED = "linked";

    public static void migrate(ItemStack stack) {
        if (stack.isEmpty() || !stack.has(DataComponents.CUSTOM_DATA)) return;

        if (stack.getItem() instanceof Wallet) migrateWallet(stack);
        else if (stack.getItem() instanceof TrimmedHopperItem) migrateHopper(stack);
    }

    private static void migrateWallet(ItemStack wallet) {
        CompoundTag tag = wallet.get(DataComponents.CUSTOM_DATA).copyTag();
        if (!tag.contains(NBT_COINS) && !tag.contains(NBT_DOLLARS) && !tag.contains(NBT_OWNER) && !tag.contains(NBT_WALLET_ID)) return;

        int coins = tag.getIntOr(NBT_COINS, 0) + tag.getIntOr(NBT_DOLLARS, 0);
        if (coins > 0 || !wallet.has(MoneyDataComponents.COINS))
            wallet.set(MoneyDataComponents.COINS, Math.max(0, wallet.getOrDefault(MoneyDataComponents.COINS, 0) + coins));

        String owner = tag.getStringOr(NBT_OWNER, "");
        if (!owner.isEmpty() && !wallet.has(MoneyDataComponents.OWNER))
            wallet.set(MoneyDataComponents.OWNER, ResolvableProfile.createUnresolved(owner));

        UUID walletId = parseUUID(tag.getStringOr(NBT_WALLET_ID, ""));
        if (walletId != null && !wallet.has(MoneyDataComponents.WALLET_ID))
            wallet.set(MoneyDataComponents.WALLET_ID, walletId);

        tag.remove(NBT_COINS);
        tag.remove(NBT_DOLLARS);
        tag.remove(NBT_OWNER);
        tag.remove(NBT_WALLET_ID);
        CustomData.set(DataComponents.CUSTOM_DATA, wallet, tag);
    }

    private static void migrateHopper(ItemStack hopper) {
        CompoundTag tag = hopper.get(DataComponents.CUSTOM_DATA).copyTag();
        if (!tag.contains(NBT_LINKED)) return;

        long linked = tag.getLongOr(NBT_LINKED, Long.MIN_VALUE);
        if (linked != Long.MIN_VALUE && !hopper.has(MoneyDataComponents.COORDINATES)) {
            hopper.set(MoneyDataComponents.COORDINATES, BlockPos.of(linked));
            if (hopper.getCount() == 1) hopper.set(DataComponents.MAX_STACK_SIZE, 1);
        }

        tag.remove(NBT_LINKED);
        CustomData.set(DataComponents.CUSTOM_DATA, hopper, tag);
    }

    private static UUID parseUUID(String string) {
        try {
            return string.isEmpty() ? null : UUID.fromString(string);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }
}
