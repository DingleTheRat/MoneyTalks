package net.dingletherat.block.entity.custom;

import java.util.List;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;
import net.dingletherat.block.MoneyBlocks;
import net.dingletherat.block.entity.MoneyBlockEntities;
import net.dingletherat.item.MoneyItems;
import net.dingletherat.item.custom.Wallet;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

public class DoubloonCompressorEntity extends BlockEntity implements WorldlyContainer {
    protected UUID owner;
    protected int compressing_ticks = 100;
    protected int original_wallet_coins;
    protected String name;
    protected String walletOwner;
    // Item 0: wallet
    // Item 1: fuel
    // Item 2: doubloons
    protected final NonNullList<ItemStack> inventory = NonNullList.withSize(3, ItemStack.EMPTY);

    public <T extends DoubloonCompressorEntity> DoubloonCompressorEntity(BlockEntityType<T> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);
    }

    public DoubloonCompressorEntity(BlockPos pos, BlockState state) {
        super(MoneyBlockEntities.DOUBLOON_COMPRESSOR_ENTITY, pos, state);
    }

    public static void tick(Level world, BlockPos pos, BlockState state, DoubloonCompressorEntity compressor) {
        ItemStack wallet = compressor.getItem(0);
        ItemStack fuel = compressor.getItem(1);
        ItemStack doubloons = compressor.getItem(2);
        int dollars = Wallet.getDollars(wallet);
        if (dollars < 10) compressor.setItem(0, ItemStack.EMPTY);
        if (world.isClientSide()) return;
        if (compressor.getItem(0).is(MoneyItems.WALLET) && compressor.getItem(1).is(Items.BLAZE_POWDER)) {
            if (compressor.compressing_ticks == 0) {
                if (dollars >= 10) {
                    if (doubloons.is(MoneyBlocks.DOUBLOON.asItem()) && doubloons.getCount() != 64) {
                        Wallet.setDollars(wallet, dollars - 10);
                        doubloons.grow(1);
                        fuel.shrink(1);
                        compressor.compressing_ticks = 100;
                    } else if (doubloons.isEmpty()) {
                        Wallet.setDollars(wallet, dollars - 10);
                        compressor.setItem(2, new ItemStack(Items.GOLD_BLOCK));
                        fuel.shrink(1);
                        compressor.compressing_ticks = 100;
                    }
                }
            } else {
                compressor.compressing_ticks--;
            }
        }
    }

    public UUID getOwner() { return owner; }
    public void setOwner(UUID owner) { this.owner = owner; }
    public int getTransactionCoins() { return original_wallet_coins; }
    public void setTransactionCoins(int coins) { this.original_wallet_coins = coins; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getWalletOwner() { return walletOwner; }
    public void setWalletOwner(String owner) { this.walletOwner = owner; }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putString("owner", owner == null ? "" : owner.toString());
        output.putString("compressing_ticks", Integer.toString(compressing_ticks));
        output.putString("original_wallet_coins", Integer.toString(original_wallet_coins));
        output.putString("name", name == null ? "Doubloon Compressor" : name);
        output.putString("wallet_owner", walletOwner == null ? "Unknown Player" : name);
        ContainerHelper.saveAllItems(output, inventory);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        String ownerStr = input.getString("owner").orElse("");
        owner = ownerStr.isEmpty() ? null : UUID.fromString(ownerStr);
        compressing_ticks = input.getIntOr("compressing_ticks", 100);
        original_wallet_coins = input.getIntOr("original_wallet_coins", 0);
        name = input.getStringOr("name", "Doubloon Compressor");
        walletOwner = input.getStringOr("wallet_owner", "Unknown Player");
        ContainerHelper.loadAllItems(input, inventory);
    }

    @Override
    public int getContainerSize() { return 3; }

    @Override
    public int @NonNull [] getSlotsForFace(Direction side) {
        if (side == Direction.UP) return new int[]{ 0 };
        if (side == Direction.DOWN) return new int[]{ 2 };
        return new int[]{ 1 };
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction dir) {
        if (slot == 0) { return stack.is(MoneyItems.WALLET); }
        if (slot == 1) { return stack.is(Items.BLAZE_POWDER); }
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return slot == 2;
    }

    @Override
    public boolean isEmpty() { return inventory.stream().allMatch(ItemStack::isEmpty); }

    @Override
    public ItemStack getItem(int slot) { return inventory.get(slot); }

    @Override
    public ItemStack removeItem(int slot, int amount) { return ContainerHelper.removeItem(inventory, slot, amount); }

    @Override
    public ItemStack removeItemNoUpdate(int slot) { return ContainerHelper.takeItem(inventory, slot); }

    @Override
    public void setItem(int slot, ItemStack stack) { inventory.set(slot, stack); setChanged(); }

    @Override
    public boolean stillValid(Player player) { return true; }

    @Override
    public void clearContent() { inventory.clear(); }
}
