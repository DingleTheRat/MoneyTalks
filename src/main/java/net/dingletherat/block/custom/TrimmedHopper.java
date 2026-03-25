package net.dingletherat.block.custom;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.MapCodec;

import net.dingletherat.block.entity.MoneyBlockEntities;
import net.dingletherat.block.entity.custom.TrimmedHopperEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.state.property.*;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.Level;

public class TrimmedHopper extends BaseEntityBlock {
  public static final MapCodec<TrimmedHopper> CODEC = TrimmedHopper.createCodec(TrimmedHopper::new);
    public static final Property<Direction> FACING = Properties.FACING;
    public static final BooleanProperty ENABLED = Properties.ENABLED;

    public TrimmedHopper(Settings settings) {
        super(settings);
        setDefaultState(getStateManager().getDefaultState()
            .with(FACING, Direction.DOWN)
            .with(ENABLED, true));
    }

    @Override
    protected void appendProperties(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ENABLED);
    }

    @Override
    public BlockState getPlacementState(BlockPlaceContext ctx) {
        Direction direction = ctx.getSide().getOpposite();
        return (BlockState)((BlockState)this.getDefaultState().with(FACING, direction.getAxis() == Axis.Y ? Direction.DOWN : direction)).with(ENABLED, true);
    }

    @Override
    public MapCodec<TrimmedHopper> getCodec(){
        return CODEC;
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new TrimmedHopperEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, MoneyBlockEntities.TRIMMED_HOPPER_ENTITY, TrimmedHopperEntity::tick);
    }

    @Override
    public void onPlaced(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (!world.isClient() && placer instanceof Player player) {
            TrimmedHopperEntity be = (TrimmedHopperEntity) world.getBlockEntity(pos);
            if (be != null) {
                be.setOwner(player.getUuid());
                be.markDirty();
            }
        }
    }

    @Override
    protected InteractionResult onUse(BlockState state, Level world, BlockPos pos, Player player, BlockHitResult hit) {
        if (!world.isClient()) {
            if (world.getBlockEntity(pos) instanceof TrimmedHopperEntity hopper) {
                player.openHandledScreen(hopper);
            }
        }
        return InteractionResult.SUCCESS;
    }
    @Override
    protected InteractionResult onUseWithItem(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        return onUse(state, world, pos, player, hit);
    }
}
