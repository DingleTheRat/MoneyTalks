package net.dingletherat.block.custom;

import org.jetbrains.annotations.Nullable;

import com.mojang.serialization.MapCodec;

import net.dingletherat.block.entity.custom.MerchantCarpetEntity;
import net.dingletherat.state.ShopState;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class MerchantCarpet extends BlockWithEntity {
    public static final MapCodec<MerchantCarpet> CODEC = MerchantCarpet.createCodec(MerchantCarpet::new);
    private static final VoxelShape SHAPE = VoxelShapes.cuboid(0, 0, 0, 1, 0.1875, 1); // 1/16 tall

    public MerchantCarpet(Settings settings) {
        super(settings);
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new MerchantCarpetEntity(pos, state);
    }

    @Override
    public BlockState onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
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
    public float calcBlockBreakingDelta(BlockState state, PlayerEntity player, BlockView world, BlockPos pos) {
        if (world instanceof WorldView) {
            BlockEntity be = ((WorldView) world).getBlockEntity(pos);
            if (be instanceof MerchantCarpetEntity stall)
                if (player.getUuid().equals(stall.getOwner()))
                    return 1f;
        }

        return 0f;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        if (!world.isClient() && placer instanceof PlayerEntity player) {
            MerchantCarpetEntity blockEntity = (MerchantCarpetEntity) world.getBlockEntity(pos);
            if (blockEntity != null) {
                blockEntity.setOwner(player.getUuid());
                blockEntity.markDirty();
                ShopState.get(((ServerWorld) world).getServer()).register(player.getUuid(), pos);
            }
        }
    }

    @Override
    protected ActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (hand != Hand.MAIN_HAND) return ActionResult.FAIL;

        if(world.getBlockEntity(pos) instanceof MerchantCarpetEntity merchantStallEntity)
            return merchantStallEntity.click(stack, world, player);

        return ActionResult.FAIL;
    }
}
