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

public class Doubloon extends Block {
    public static final long TRIGGER_TIME = 200;
    public static final int SPAWN_OFFSET = 2;
    public static final int MAX_Y_SPAWN_OFFSET = 32;
    ScheduledTick<Block> scheduledTick;
    public ZombieVillager zombieVillager;

    public Doubloon(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos position, RandomSource random) {
        spawnLingeringPotion(level, position);
        if (zombieVillager == null || !zombieVillager.isAlive() || zombieVillager.isInvulnerable())
            summonZombieVillager(level, position);
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
        // Kill off the zombieVillager in case he is isAlive
        if (zombieVillager != null && zombieVillager.isAlive()) zombieVillager.discard();

        // Create the zombieVillager that we will summon, containing and investor profession
        ZombieVillager zombieVillager = new ZombieVillager(EntityType.ZOMBIE_VILLAGER, level);
        zombieVillager.setVillagerData(zombieVillager.getVillagerData()
                .withProfession(BuiltInRegistries.VILLAGER_PROFESSION.wrapAsHolder(MoneyVillagers.INVESTOR)));

        // Get two randoms to determine the offset at which the villager will spawn from the block
        Random random = new Random();
        int xOffset = random.nextInt(SPAWN_OFFSET) + 1;
        int zOffset = random.nextInt(SPAWN_OFFSET) + 1;

        // Get the blockpos of our current cordinates
        BlockPos targetPosition = new BlockPos(position.getX() + xOffset, position.getY(), position.getZ() + zOffset);

        // Check if the spot at block Y is clear (2 blocks tall so the villager does not suffercate)
        if (!isValidSpawn(level, targetPosition)) {
            // Search outward from block Y, alternating up and down
            boolean found = false;
            for (int yOffset = 1; yOffset <= MAX_Y_SPAWN_OFFSET; yOffset++) {
                BlockPos above = new BlockPos(targetPosition.getX(), targetPosition.getY() + yOffset, targetPosition.getZ());
                BlockPos below = new BlockPos(targetPosition.getX(), targetPosition.getY() - yOffset, targetPosition.getZ());

                if (isValidSpawn(level, above)) {
                    targetPosition = above;
                    found = true;
                    break;
                }
                if (isValidSpawn(level, below)) {
                    targetPosition = below;
                    found = true;
                    break;
                }
            }

            // If nothing is found, that sucks. GIVE HIM INVINCIBILITY!!!!!
            if (!found) zombieVillager.setInvulnerable(true);
        }

        zombieVillager.setPos(targetPosition.getX(), targetPosition.getY(), targetPosition.getZ());
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
    public boolean isValidSpawn(ServerLevel level, BlockPos position) {
        BlockPos ground = position.below();
        return level.getBlockState(ground).isSolid()
            && level.getBlockState(position).isAir()
            && !level.getBlockState(position.above()).isSolid();
    }
}
