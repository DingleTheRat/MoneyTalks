package net.dingletherat.block.custom;


import net.dingletherat.MoneyTalks;
import net.dingletherat.item.potion.MoneyPotions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownLingeringPotion;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.ScheduledTick;

public class Dabloon extends Block {
    ScheduledTick<Block> scheduledTick;
    public final int TRIGGER_TIME = 500;

    public Dabloon(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos position, RandomSource random) {
        spawnLingeringPotion(level, position);
        scheduleTick(level, position, false);
    }

    public void spawnLingeringPotion(ServerLevel level, BlockPos position) {
        ThrownLingeringPotion potionProjectile = EntityType.LINGERING_POTION.create(level, entity -> {}, position, EntitySpawnReason.MOB_SUMMONED, true, true);
        if (potionProjectile == null) return;

        potionProjectile.setPos(position.getX() + 0.5, position.getY() + 0.1, position.getZ() + 0.5);
        ItemStack stack = PotionContents.createItemStack(Items.LINGERING_POTION, BuiltInRegistries.POTION.wrapAsHolder(MoneyPotions.GLOWING_POTION));
        potionProjectile.setItem(stack);
        level.addFreshEntity(potionProjectile);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos position, BlockState oldState, boolean isMoving) {
        if (!level.isClientSide()) scheduleTick(level, position, true);
    }

    public void scheduleTick(Level level, BlockPos position, boolean first) {
        long triggerTime = level.getGameTime() + (first ? 0 : TRIGGER_TIME);
        ScheduledTick<Block> tick = new ScheduledTick<>(this, position, triggerTime, 0);
        level.getBlockTicks().schedule(tick);
    }
}
