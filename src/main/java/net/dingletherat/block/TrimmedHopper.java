package net.dingletherat.block;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.redstone.Orientation;


import net.dingletherat.registry.MoneyBlockEntities;
import net.dingletherat.block.entity.TrimmedHopperEntity;
import net.dingletherat.LegacyData;
import net.dingletherat.registry.MoneyDataComponents;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

import java.util.Map;

public class TrimmedHopper extends BaseEntityBlock {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
    public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;

    private static final VoxelShape TOP = Block.box(0, 10, 0, 16, 16, 16);
    private static final VoxelShape FUNNEL = Block.box(4, 4, 4, 12, 10, 12);
    private static final VoxelShape STEM = Block.box(6, 0, 6, 10, 4, 10);
    private static final VoxelShape BASE = Shapes.join(Shapes.join(TOP, FUNNEL, BooleanOp.OR), STEM, BooleanOp.OR);

    private static final VoxelShape DOWN_SHAPE = BASE;
    private static final VoxelShape EAST_SHAPE = Shapes.join(BASE, Block.box(12, 4, 6, 16, 8, 10), BooleanOp.OR);
    private static final VoxelShape NORTH_SHAPE = Shapes.join(BASE, Block.box(6, 4, 0, 10, 8, 4),   BooleanOp.OR);
    private static final VoxelShape SOUTH_SHAPE = Shapes.join(BASE, Block.box(6, 4, 12, 10, 8, 16), BooleanOp.OR);
    private static final VoxelShape WEST_SHAPE = Shapes.join(BASE, Block.box(0, 4, 6, 4, 8, 10),   BooleanOp.OR);

    private static final Map<Direction, VoxelShape> SHAPES = Map.of(
        Direction.DOWN,  DOWN_SHAPE,
        Direction.NORTH, NORTH_SHAPE,
        Direction.SOUTH, SOUTH_SHAPE,
        Direction.WEST,  WEST_SHAPE,
        Direction.EAST,  EAST_SHAPE
    );

    public TrimmedHopper(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.DOWN).setValue(ENABLED, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ENABLED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getClickedFace().getOpposite();
        return this.defaultBlockState()
                .setValue(FACING, direction.getAxis() == Direction.Axis.Y ? Direction.DOWN : direction)
                .setValue(ENABLED, true);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos position, CollisionContext context) {
        return SHAPES.getOrDefault(state.getValue(FACING), DOWN_SHAPE);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos position, BlockState state) {
        return new TrimmedHopperEntity(position, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, MoneyBlockEntities.TRIMMED_HOPPER_ENTITY, TrimmedHopperEntity::tick);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos position, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (world.isClientSide() || !(placer instanceof Player player)) return;
        TrimmedHopperEntity blockEntity = (TrimmedHopperEntity) world.getBlockEntity(position);
        if (blockEntity == null) return;

        LegacyData.migrate(itemStack);
        if (itemStack.has(MoneyDataComponents.COORDINATES)) blockEntity.setLinkedChest(itemStack.get(MoneyDataComponents.COORDINATES));

        blockEntity.setOwner(player.getUUID());
        blockEntity.setChanged();
        world.sendBlockUpdated(position, state, state, 3);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos position, Block block, Orientation orientation, boolean movedByPiston) {
        boolean powered = level.hasNeighborSignal(position);
        if (level.isClientSide() || powered != state.getValue(ENABLED)) return;
        level.setBlock(position, state.setValue(ENABLED, !powered), Block.UPDATE_NEIGHBORS);
    }

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos position, Direction direction) {
        return AbstractContainerMenu.getRedstoneSignalFromBlockEntity(level.getBlockEntity(position));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos position, Player player, BlockHitResult hitResult) {
        if (level.isClientSide() || !(level.getBlockEntity(position) instanceof TrimmedHopperEntity hopper)) return InteractionResult.SUCCESS;
        player.openMenu(hopper);
        return InteractionResult.SUCCESS;
    }

   @Override
   protected InteractionResult useItemOn(final ItemStack itemStack, final BlockState state, final Level level, final BlockPos position, final Player player, final InteractionHand hand, final BlockHitResult hitResult) {
       return useWithoutItem(state, level, position, player, hitResult);
   }
}
