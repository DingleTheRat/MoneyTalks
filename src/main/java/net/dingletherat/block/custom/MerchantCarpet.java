package net.dingletherat.block.custom;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.MapCodec;

import net.dingletherat.block.entity.custom.MerchantCarpetEntity;
import net.dingletherat.state.ShopState;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;

public class MerchantCarpet extends BaseEntityBlock {
    public static final MapCodec<MerchantCarpet> CODEC = MerchantCarpet.createCodec(MerchantCarpet::new);
    private static final VoxelShape SHAPE = Shapes.cuboid(0, 0, 0, 1, 0.1875, 1); // 1/16 tall

    public MerchantCarpet(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> getCodec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MerchantCarpetEntity(pos, state);
    }

    @Override
    public BlockState onBreak(ServerLevel world, BlockPos pos, BlockState state, Player player) {
        if (!world.isClient()) {

            MerchantCarpetEntity blockEntity = (MerchantCarpetEntity) world.getBlockEntity(pos);
            if (blockEntity != null) {
                if (!player.getUuid().equals(blockEntity.getOwner())) {
                    world.setBlockState(pos, state);
                    return state;
                }
                blockEntity.onBreak();
            }
        }
        return super.onBreak(world, pos, state, player);
    }


    @Override
    public float calcBlockBreakingDelta(BlockState state, Player player, BlockGetter world, BlockPos pos) {
        if (world instanceof LevelReader) {
            BlockEntity be = ((LevelReader) world).getBlockEntity(pos);
            if (be instanceof MerchantCarpetEntity stall)
                if (player.getUuid().equals(stall.getOwner()))
                    return 1f;
        }

        return 0f;
    }

    @Override
    public void onPlaced(ServerLevel world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (!world.isClient() && placer instanceof Player player) {
            MerchantCarpetEntity blockEntity = (MerchantCarpetEntity) world.getBlockEntity(pos);
            if (blockEntity != null) {
                blockEntity.setOwner(player.getUuid());
                blockEntity.markDirty();
                ShopState.get(((ServerLevel) world).getServer()).register(player.getUuid(), pos);
            }
        }
    }

    @Override
    protected InteractionResult onUseWithItem(ItemStack stack, BlockState state, ServerLevel world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.FAIL;

        if(world.getBlockEntity(pos) instanceof MerchantCarpetEntity merchantStallEntity)
            return merchantStallEntity.click(stack, world, player);

        return InteractionResult.FAIL;
    }
}
