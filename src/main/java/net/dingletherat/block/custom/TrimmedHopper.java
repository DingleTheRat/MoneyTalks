package net.dingletherat.block.custom;

import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;

import com.mojang.serialization.MapCodec;

import net.dingletherat.block.entity.MoneyBlockEntities;
import net.dingletherat.block.entity.custom.TrimmedHopperEntity;
import net.dingletherat.state.ShopState;
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
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public class TrimmedHopper extends BaseEntityBlock {
  public static final MapCodec<TrimmedHopper> CODEC = TrimmedHopper.simpleCodec(TrimmedHopper::new);
    public static final EnumProperty<Direction> FACING = BlockStateProperties.FACING;
    public static final BooleanProperty ENABLED = BlockStateProperties.ENABLED;

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
