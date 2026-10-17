package net.dingletherat.block.custom;

import net.dingletherat.block.entity.MoneyBlockEntities;
import net.dingletherat.block.entity.custom.DoubleTrimmedHopperEntity;
import net.dingletherat.item.LegacyData;
import net.dingletherat.item.MoneyDataComponents;
import net.dingletherat.state.ShopState;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class DoubleTrimmedHopper extends TrimmedHopper {
    public DoubleTrimmedHopper(Properties settings) {
        super(settings);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new DoubleTrimmedHopperEntity(pos, state);
    }
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, MoneyBlockEntities.DOUBLE_TRIMMED_HOPPER_ENTITY, DoubleTrimmedHopperEntity::tick);
    }
    @Override
    public void setPlacedBy(Level world, BlockPos position, BlockState state, LivingEntity placer, ItemStack itemStack) {
        if (!world.isClientSide() && placer instanceof Player player) {
            DoubleTrimmedHopperEntity blockEntity = (DoubleTrimmedHopperEntity) world.getBlockEntity(position);
            if (blockEntity == null) return;

            LegacyData.migrate(itemStack);
            if (itemStack.has(MoneyDataComponents.COORDINATES)) blockEntity.setLinkedChest(itemStack.get(MoneyDataComponents.COORDINATES));

            blockEntity.setOwner(player.getUUID());
            blockEntity.setChanged();
            world.sendBlockUpdated(position, state, state, 3);
            ShopState.get(((ServerLevel) world).getServer()).register(player.getUUID(), position);
        }
    }
}
