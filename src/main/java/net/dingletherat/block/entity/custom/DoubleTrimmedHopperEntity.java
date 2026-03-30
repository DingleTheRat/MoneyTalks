package net.dingletherat.block.entity.custom;

import net.dingletherat.block.entity.MoneyBlockEntities;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class DoubleTrimmedHopperEntity extends TrimmedHopperEntity {
    private BlockPos linked = null;

    public DoubleTrimmedHopperEntity(BlockPos pos, BlockState state) {
        super(MoneyBlockEntities.DOUBLE_TRIMMED_HOPPER_ENTITY, pos, state);
    }


    @Override
    protected void writeData(ValueOutput view) {
        super.writeData(view);
        view.putLong("linked", linked != null ? linked.asLong() : Long.MIN_VALUE);
    }

    @Override
    protected void readData(ValueInput input) {
        super.readData(input);
        long linkedLong = input.getLong("linked").orElse(Long.MIN_VALUE);
        linked = linkedLong == Long.MIN_VALUE ? null : BlockPos.of(linkedLong);
    }

    public static void tick(Level world, BlockPos pos, BlockState state, DoubleTrimmedHopperEntity hopper) {
        if (world.isClientSide()) return;
        if (hopper.transferCooldown > 0) {
            hopper.transferCooldown--;
            return;
        }

        hopper.transferCooldown = 0;
        hopper.pullItems(world, pos, state);
        hopper.pushItems(world, pos, state);
    }

    @Override
    protected void pushItems(Level world, BlockPos pos, BlockState state) {
        if (linked == null) return;

        BlockEntity be = world.getBlockEntity(linked);
        if (be == null || !(be instanceof Container)) {
            linked = null;
            setChanged();
            return;
        }

        transferTo((Container) be);
    }

    public void setLinkedChest(BlockPos pos) {
        linked = pos;
        setChanged();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.moneytalks.double_trimmed_hopper");
    }
}
