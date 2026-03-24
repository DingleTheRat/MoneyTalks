package net.dingletherat.block.custom;

import net.dingletherat.block.entity.MoneyBlockEntities;
import net.dingletherat.block.entity.custom.DoubleTrimmedHopperEntity;
import net.dingletherat.state.ShopState;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DoubleTrimmedHopper extends TrimmedHopper {
    public DoubleTrimmedHopper(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DoubleTrimmedHopperEntity(pos, state);
    }
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, MoneyBlockEntities.DOUBLE_TRIMMED_HOPPER_ENTITY, DoubleTrimmedHopperEntity::tick);
    }
    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (!world.isClient() && placer instanceof PlayerEntity player) {
            DoubleTrimmedHopperEntity be = (DoubleTrimmedHopperEntity) world.getBlockEntity(pos);
            if (be != null) {
                be.setOwner(player.getUuid());
                NbtComponent data = itemStack.get(DataComponentTypes.CUSTOM_DATA);
                if (data != null) {
                    long linkedPos = data.copyNbt().getLong("linked", Long.MIN_VALUE);
                    if (linkedPos != Long.MIN_VALUE) {
                        be.setLinkedChest(BlockPos.fromLong(linkedPos));
                    }
                }
                be.markDirty();
                world.updateListeners(pos, state, state, 3);
                ShopState.get(((ServerWorld) world).getServer()).register(player.getUuid(), pos);
            }
        }
    }
}
