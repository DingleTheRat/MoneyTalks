package net.dingletherat.block.custom;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.redstone.Orientation;

import com.mojang.serialization.MapCodec;

import net.dingletherat.block.entity.MoneyBlockEntities;
import net.dingletherat.block.entity.custom.TrimmedHopperEntity;
import net.dingletherat.state.ShopState;
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
  public static final MapCodec<TrimmedHopper> CODEC = TrimmedHopper.simpleCodec(TrimmedHopper::new);
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
    public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;

    // --- VoxelShapes (matching vanilla HopperBlock exactly) ---
    private static final VoxelShape TOP = Block.box(0, 10, 0, 16, 16, 16);
    private static final VoxelShape FUNNEL = Block.box(4, 4, 4, 12, 10, 12);
    private static final VoxelShape STEM = Block.box(6, 0, 6, 10, 4, 10);
    private static final VoxelShape BASE = Shapes.join(Shapes.join(TOP, FUNNEL, BooleanOp.OR), STEM, BooleanOp.OR);

    private static final VoxelShape DOWN_SHAPE  = BASE;
    private static final VoxelShape EAST_SHAPE  = Shapes.join(BASE, Block.box(12, 4, 6, 16, 8, 10), BooleanOp.OR);
    private static final VoxelShape NORTH_SHAPE = Shapes.join(BASE, Block.box(6, 4, 0, 10, 8, 4),   BooleanOp.OR);
    private static final VoxelShape SOUTH_SHAPE = Shapes.join(BASE, Block.box(6, 4, 12, 10, 8, 16), BooleanOp.OR);
    private static final VoxelShape WEST_SHAPE  = Shapes.join(BASE, Block.box(0, 4, 6, 4, 8, 10),   BooleanOp.OR);

    private static final Map<Direction, VoxelShape> SHAPES = Map.of(
        Direction.DOWN,  DOWN_SHAPE,
        Direction.NORTH, NORTH_SHAPE,
        Direction.SOUTH, SOUTH_SHAPE,
        Direction.WEST,  WEST_SHAPE,
        Direction.EAST,  EAST_SHAPE
    );

    public TrimmedHopper(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.DOWN)
                .setValue(ENABLED, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ENABLED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Direction direction = ctx.getClickedFace().getOpposite();
        return this.defaultBlockState()
                .setValue(FACING, direction.getAxis() == Direction.Axis.Y ? Direction.DOWN : direction)
                .setValue(ENABLED, true);
    }

    @Override
    public MapCodec<TrimmedHopper> codec(){
        return CODEC;
    }

    // --- Shape ---

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPES.getOrDefault(state.getValue(FACING), DOWN_SHAPE);
    }

    // --- Rendering ---

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    // --- Block entity ---

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TrimmedHopperEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, MoneyBlockEntities.TRIMMED_HOPPER_ENTITY, TrimmedHopperEntity::tick);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (!world.isClientSide() && placer instanceof Player player) {
            TrimmedHopperEntity be = (TrimmedHopperEntity) world.getBlockEntity(pos);
            if (be != null) {
                be.setOwner(player.getUUID());
                be.setChanged();
                ShopState.get((world).getServer()).register(player.getUUID(), pos);
            }
        }
    }

// --- Redstone: enable/disable via redstone signal (vanilla behaviour) ---

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, Orientation orientation, boolean movedByPiston) {
        if (!level.isClientSide()) {
            boolean powered = level.hasNeighborSignal(pos);
            if (powered == state.getValue(ENABLED)) {
                level.setBlock(pos, state.setValue(ENABLED, !powered), Block.UPDATE_NEIGHBORS);
            }
        }
    }

    // --- Comparator support ---

    @Override
    protected boolean hasAnalogOutputSignal(BlockState state) {
        return true;
    }

    @Override
    protected int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
        if (level.getBlockEntity(pos) instanceof TrimmedHopperEntity hopper) {
            int slots = hopper.getContainerSize();
            if (slots == 0) return 0;
            int filledSlots = 0;
            float ratio = 0.0F;
            for (int i = 0; i < slots; i++) {
                ItemStack stack = hopper.getItem(i);
                if (!stack.isEmpty()) {
                    ratio += (float) stack.getCount() / stack.getMaxStackSize();
                    filledSlots++;
                }
            }
            ratio /= slots;
            return filledSlots == 0 ? 0 : (int)(ratio * 14) + 1;
        }
        return 0;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide()) {
            if (level.getBlockEntity(pos) instanceof TrimmedHopperEntity hopper) {
                player.openMenu(hopper);
            }
        }
        return InteractionResult.SUCCESS;
    }

   @Override
   protected InteractionResult useItemOn(final ItemStack itemStack, final BlockState state, final Level level, final BlockPos pos, final Player player, final InteractionHand hand, final BlockHitResult hitResult) {
       return useWithoutItem(state, level, pos, player, hitResult);
   }
}
