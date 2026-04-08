package net.dingletherat.block.entity.custom;

import java.util.UUID;

import org.jetbrains.annotations.Nullable;

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
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public class DabloonCompressorEntity extends BlockEntity implements WorldlyContainer {
    protected UUID owner;
    protected int compressing_ticks = 100;
    // Item 0: wallet
    // Item 1: fuel
    // Item 2: dabloons
    protected final NonNullList<ItemStack> inventory = NonNullList.withSize(3, ItemStack.EMPTY);

    public <T extends DabloonCompressorEntity> DabloonCompressorEntity(BlockEntityType<T> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);
    }

    public DabloonCompressorEntity(BlockPos pos, BlockState state) {
        super(MoneyBlockEntities.DABLOON_COMPRESSOR_ENTITY, pos, state);
    }

    public static void tick(Level world, BlockPos pos, BlockState state, DabloonCompressorEntity compressor) {
        if (world.isClientSide()) return;
        if (compressor.getItem(0).is(MoneyItems.WALLET) && compressor.getItem(1).is(Items.BLAZE_POWDER)) {
            if (compressor.compressing_ticks == 0) {
                ItemStack wallet = compressor.getItem(0);
                ItemStack fuel = compressor.getItem(1);
                ItemStack dabloons = compressor.getItem(2);
                int dollars = Wallet.getDollars(wallet);
                if (dollars >= 10) {
                    if (dabloons.is(Items.GOLD_BLOCK) && dabloons.getCount() != 64) {
                        Wallet.setDollars(wallet, dollars - 10);
                        dabloons.grow(1);
                        fuel.shrink(1);
                        compressor.compressing_ticks = 100;
                    } else if (dabloons.isEmpty()) {
                        Wallet.setDollars(wallet, dollars - 10);
                        compressor.setItem(2, new ItemStack(Items.GOLD_BLOCK));
                        fuel.shrink(1);
                        compressor.compressing_ticks = 100;
                    }
                } else if (dabloons.isEmpty() && !wallet.isEmpty()) {
                    compressor.setItem(0, ItemStack.EMPTY);
                    compressor.setItem(2, wallet);
                    compressor.compressing_ticks = 100;
                }
            } else {
                compressor.compressing_ticks--;
            }
        }
        System.out.printf("%d, wallet: %d, blaze: %d, gold: %d\n", compressor.compressing_ticks,
                Wallet.getDollars(compressor.getItem(0)), compressor.getItem(1).count(), compressor.getItem(2).count());
    }

    public UUID getOwner() { return owner; }
    public void setOwner(UUID owner) { this.owner = owner; }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        String ownerStr = input.getString("owner").orElse("");
        owner = ownerStr.isEmpty() ? null : UUID.fromString(ownerStr);
        ContainerHelper.loadAllItems(input, inventory);
    }

    @Override
    public int getContainerSize() { return 3; }
    @Override
    public int[] getSlotsForFace(Direction side) {
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
