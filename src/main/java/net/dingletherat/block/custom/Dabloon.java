package net.dingletherat.block.custom;

import java.util.Random;

import net.dingletherat.item.potion.MoneyPotions;
import net.dingletherat.villager.MoneyVillagers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.zombie.ZombieVillager;
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
    public final long TRIGGER_TIME = 200;
    public final int SPAWN_OFFSET = 2;
    public ZombieVillager zombieVillager;

    public Dabloon(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos position, RandomSource random) {
        spawnLingeringPotion(level, position);
        if (zombieVillager == null || !zombieVillager.isAlive()) summonZombieVillager(level, position);
        scheduleTick(level, position, false);
    }

    public void spawnLingeringPotion(ServerLevel level, BlockPos position) {
        // Create a potion projectile that will we will throw our potion with once we add the potion type
        ThrownLingeringPotion potionProjectile = EntityType.LINGERING_POTION.create(level, entity -> {}, position, EntitySpawnReason.MOB_SUMMONED, true, true);
        if (potionProjectile == null) return;
        potionProjectile.setPos(position.getX() + 0.5, position.getY() + 0.1, position.getZ() + 0.5);

        // Get an ItemStack of the glowing potion, to set the type, and add it to the potion projectile
        ItemStack stack = PotionContents.createItemStack(Items.LINGERING_POTION,
                BuiltInRegistries.POTION.wrapAsHolder(MoneyPotions.GLOWING_POTION));
        potionProjectile.setItem(stack);

        // Throw our potion
        level.addFreshEntity(potionProjectile);
    }
    public void summonZombieVillager(ServerLevel level, BlockPos position) {
        // Create the zombieVillager that we will summon, containing and investor profession
        ZombieVillager zombieVillager = new ZombieVillager(EntityType.ZOMBIE_VILLAGER, level);
        zombieVillager.setVillagerData(zombieVillager.getVillagerData()
                .withProfession(BuiltInRegistries.VILLAGER_PROFESSION.wrapAsHolder(MoneyVillagers.INVESTOR)));

        // Get two randoms to determine the offset at which the villager will spawn from the block
        Random random = new Random();
        int x = random.nextInt(SPAWN_OFFSET) + 1;
        int z = random.nextInt(SPAWN_OFFSET) + 1;

        // Spawn in the villager at the BlockPos + the offset
        zombieVillager.setPos(position.getX() + x, position.getY(), position.getZ() + z);
        level.addFreshEntity(zombieVillager);
        this.zombieVillager = zombieVillager;
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
