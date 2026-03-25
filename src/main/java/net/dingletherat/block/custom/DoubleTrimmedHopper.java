package net.dingletherat.block.custom;

import net.dingletherat.block.entity.MoneyBlockEntities;
import net.dingletherat.block.entity.custom.DoubleTrimmedHopperEntity;
import net.dingletherat.state.ShopState;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class DoubleTrimmedHopper extends TrimmedHopper {
    public DoubleTrimmedHopper(Settings settings) {
        super(settings);
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DoubleTrimmedHopperEntity(pos, state);
    }
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(ServerLevel world, BlockState state, BlockEntityType<T> type) {
        return validateTicker(type, MoneyBlockEntities.DOUBLE_TRIMMED_HOPPER_ENTITY, DoubleTrimmedHopperEntity::tick);
    }
    @Override
    public void onPlaced(ServerLevel world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (!world.isClient() && placer instanceof Player player) {
            DoubleTrimmedHopperEntity be = (DoubleTrimmedHopperEntity) world.getBlockEntity(pos);
            if (be != null) {
                be.setOwner(player.getUuid());
                CustomData data = itemStack.get(DataComponents.CUSTOM_DATA);
                if (data != null) {
                    long linkedPos = data.copyNbt().getLong("linked", Long.MIN_VALUE);
                    if (linkedPos != Long.MIN_VALUE) {
                        be.setLinkedChest(BlockPos.fromLong(linkedPos));
                    }
                }
                be.markDirty();
                world.updateListeners(pos, state, state, 3);
                ShopState.get(((ServerLevel) world).getServer()).register(player.getUuid(), pos);
            }
        }
    }
}
