package net.dingletherat.block.custom;

import net.minecraft.world.level.block.state.BlockBehaviour;

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
    public static final MapCodec<MerchantCarpet> CODEC = MerchantCarpet.simpleCodec(MerchantCarpet::new);
    private static final VoxelShape SHAPE = Shapes.box(0, 0, 0, 1, 0.1875, 1); // 1/16 tall

    public MerchantCarpet(BlockBehaviour.Properties properties) {
        super(properties);
    }


    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new MerchantCarpetEntity(pos, state);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide()) {

            MerchantCarpetEntity blockEntity = (MerchantCarpetEntity) level.getBlockEntity(pos);
            if (blockEntity != null) {
                if (!player.getUUID().equals(blockEntity.getOwner())) {
                    level.getBlockState(pos);
                    return state;
                }
                blockEntity.onBreak();
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public float getDestroyProgress(BlockState state, Player player, BlockGetter level, BlockPos pos) {
        if (level instanceof LevelReader) {
            BlockEntity be = ((LevelReader) level).getBlockEntity(pos);
            if (be instanceof MerchantCarpetEntity stall)
                if (player.getUUID().equals(stall.getOwner()))
                    return 1f;
        }

        return 0f;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        if (!level.isClientSide() && placer instanceof Player player) {
            MerchantCarpetEntity blockEntity = (MerchantCarpetEntity) level.getBlockEntity(pos);
            if (blockEntity != null) {
                blockEntity.setOwner(player.getUUID());
                blockEntity.setChanged();
                ShopState.get((level).getServer()).register(player.getUUID(), pos);
            }
        }
    }

    @Override
    protected InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
        if (level.getBlockEntity(pos) instanceof MerchantCarpetEntity merchantCarpetEntity) {
            return merchantCarpetEntity.click(stack, level, player);
        }
        return InteractionResult.PASS;
    }
}
