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
    public static final String DATA_LINKED = "linked";

    private BlockPos linked = null;

    public DoubleTrimmedHopperEntity(BlockPos pos, BlockState state) {
        super(MoneyBlockEntities.DOUBLE_TRIMMED_HOPPER_ENTITY, pos, state);
    }


    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putLong(DATA_LINKED,linked != null ? linked.asLong() : Long.MIN_VALUE);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        long linkedLong = input.getLongOr(DATA_LINKED,Long.MIN_VALUE);
        linked = linkedLong == Long.MIN_VALUE ? null : BlockPos.of(linkedLong);
    }

    public static void tick(Level world, BlockPos position, BlockState state, DoubleTrimmedHopperEntity hopper) {
        if (world.isClientSide()) return;
        if (hopper.transferCooldown > 0) {
            hopper.transferCooldown--;
            return;
        }

        hopper.transferCooldown = 0;
        hopper.pullItems(world, position, state);
        hopper.pushItems(world, position, state);
    }

    @Override
    protected void pushItems(Level world, BlockPos position, BlockState state) {
        if (linked == null) return;

        BlockEntity blockEntity = world.getBlockEntity(linked);
        if (blockEntity == null || !(blockEntity instanceof Container)) {
            linked = null;
            setChanged();
            return;
        }

        transferTo((Container) blockEntity);
    }

    public void setLinkedChest(BlockPos position) {
        linked = position;
        setChanged();
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.moneytalks.double_trimmed_hopper");
    }
}
