package net.dingletherat.block.entity.custom;

import net.dingletherat.block.entity.MoneyBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DoubleTrimmedHopperEntity extends TrimmedHopperEntity {
    private BlockPos linked = null;

    public DoubleTrimmedHopperEntity(BlockPos pos, BlockState state) {
        super(MoneyBlockEntities.DOUBLE_TRIMMED_HOPPER_ENTITY, pos, state);
    }


    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        view.putLong("linked", linked != null ? linked.asLong() : Long.MIN_VALUE);
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        long linkedLong = view.getLong("linked", Long.MIN_VALUE);
        linked = linkedLong == Long.MIN_VALUE ? null : BlockPos.fromLong(linkedLong);
    }

    public static void tick(World world, BlockPos pos, BlockState state, DoubleTrimmedHopperEntity hopper) {
        if (world.isClient()) return;
        if (hopper.transferCooldown > 0) {
            hopper.transferCooldown--;
            return;
        }

        hopper.transferCooldown = 0;
        hopper.pullItems(world, pos, state);
        hopper.pushItems(world, pos, state);
    }

    @Override
    protected void pushItems(World world, BlockPos pos, BlockState state) {
        if (linked == null) return;

        BlockEntity be = world.getBlockEntity(linked);
        if (be == null || !(be instanceof Inventory)) {
            linked = null;
            markDirty();
            return;
        }

        transferTo((Inventory) be);
    }

    public void setLinkedChest(BlockPos pos) {
        linked = pos;
        markDirty();
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("block.moneytalks.double_trimmed_hopper");
    }
}
