package net.dingletherat.block.custom;

import java.util.Random;
import com.mojang.serialization.MapCodec;
import net.dingletherat.block.MoneyBlocks;
import net.dingletherat.block.entity.custom.DoubloonEntity;
import net.dingletherat.item.potion.MoneyPotions;
import net.dingletherat.villager.MoneyVillagers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.entity.monster.zombie.ZombieVillager;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrownLingeringPotion;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.ticks.ScheduledTick;

public class Doubloon extends BaseEntityBlock {
    public static final long TRIGGER_TIME = 200;
    public static final int SPAWN_OFFSET = 2;
    public static final int MAX_Y_SPAWN_OFFSET = 32;
    public static final int NON_SPAWN_CHANCE = 70;
    public static final MapCodec<Doubloon> CODEC = simpleCodec(Doubloon::new);

    public Doubloon(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public MapCodec<Doubloon> codec() { return CODEC; }

    @Override
    public BlockEntity newBlockEntity(BlockPos position, BlockState state) {
        return new DoubloonEntity(position, state);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos position, BlockState oldState, boolean isMoving) {
        if (!level.isClientSide()) scheduleTick(level, position, true);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos position, RandomSource random) {
        // Get block entity
        DoubloonEntity blockEntity = (DoubloonEntity) level.getBlockEntity(position);
        if (blockEntity == null) return;
        blockEntity.resolveVillager();

        if (blockEntity.zombieVillager == null || !blockEntity.zombieVillager.isAlive())
            summonZombieVillager(level, position, blockEntity);

        if (blockEntity.zombieVillager != null && blockEntity.zombieVillager.isAlive())
            spawnLingeringPotion(level, new BlockPos(position.getX(), blockEntity.zombieVillagerSpawnPosition.getY(), position.getZ()), blockEntity, false);

        scheduleTick(level, position, false);
    }

    public void scheduleTick(Level level, BlockPos position, boolean first) {
        long triggerTime = level.getGameTime() + (first ? 0 : TRIGGER_TIME);
        level.getBlockTicks().schedule(new ScheduledTick<>(this, position, triggerTime, 0));
    }

    public void spawnLingeringPotion(ServerLevel level, BlockPos position, DoubloonEntity blockEntity, boolean ignoreBottom) {
        // Create a potion projectile that will we will throw our potion with once we add the potion type
        ThrownLingeringPotion potion = EntityType.LINGERING_POTION.create(level, entity -> {}, position, EntitySpawnReason.MOB_SUMMONED, true, true);
        if (potion == null) return;
        potion.setPos(position.getX() + 0.5, position.getY() + 0.1, position.getZ() + 0.5);

        // Get an ItemStack of the glowing potion, to set the type, and add it to the potion projectile
        potion.setItem(PotionContents.createItemStack(Items.LINGERING_POTION,
            BuiltInRegistries.POTION.wrapAsHolder(MoneyPotions.GLOWING_POTION)));

        // Throw our potion
        level.addFreshEntity(potion);
    }

    public void summonZombieVillager(ServerLevel level, BlockPos position, DoubloonEntity blockEntity) {
        Random random = new Random();

        // If there's already a doubloon at the bottom, adding another zombieVillager might make it cluttered, so make it only a chance
        if (level.getBlockState(position.below()).getBlock() == MoneyBlocks.DOUBLOON
                && random.nextInt(100) <= NON_SPAWN_CHANCE) return;

        // Create the zombieVillager that we will summon, containing an investor profession
        ZombieVillager zombieVillager = new ZombieVillager(EntityType.ZOMBIE_VILLAGER, level);

        // Get two randoms to determine the offset at which the villager will spawn from the block
        int xOffset = random.nextInt(SPAWN_OFFSET) + 1;
        int zOffset = random.nextInt(SPAWN_OFFSET) + 1;
        boolean xAddition = random.nextBoolean();
        boolean zAddition = random.nextBoolean();

        // Set the position at which the villager will spawn, with block offsets applied
        int spawnX = xAddition ? position.getX() + xOffset : position.getX() - xOffset;
        int spawnZ = zAddition ? position.getZ() + zOffset : position.getZ() - zOffset;

        // Now change the Y to a position it can spawn safely
        int spawnY = getValidSpawn(level, new BlockPos(spawnX, position.getY(), spawnZ));
        BlockPos targetPosition = new BlockPos(spawnX, spawnY, spawnZ);

        // Make the zombie villager **REALLY** like this doubloon block
        // IMPORTANT: This might show as an error, BUT IT IS NOT, it compiles
        zombieVillager.goalSelector.addGoal(0, new Goal() {
            private int ticksAlive = 0;

            @Override
            public boolean canUse() {
                return zombieVillager.isAlive() && !zombieVillager.hasEffect(MobEffects.GLOWING);
            }

            @Override
            public boolean canContinueToUse() { return canUse(); }

            @Override
            public void tick() {
                ticksAlive++;
                // Wait a bit before checking pathfinding so the villager has time to load in
                if (ticksAlive < 40) return;

                double targetX = xAddition ? position.getX() + 1.5 : position.getX() - 0.5;
                double targetZ = zAddition ? position.getZ() + 1.5 : position.getZ() - 0.5;

                double dx = zombieVillager.getX() - targetX;
                double dz = zombieVillager.getZ() - targetZ;
                // Already at the target, no need to move
                if (dx * dx + dz * dz < 1.0) return;

                Path path = zombieVillager.getNavigation().createPath(targetX, spawnY, targetZ, 0);
                if (path == null) {
                    zombieVillager.discard();
                    summonZombieVillager(level, position, blockEntity);
                }
                else zombieVillager.getNavigation().moveTo(path, 1.0);
            }
        });

        // Give the villager the investor profession
        zombieVillager.setVillagerData(zombieVillager.getVillagerData()
            .withProfession(BuiltInRegistries.VILLAGER_PROFESSION.wrapAsHolder(MoneyVillagers.INVESTOR)));

        // Unleash our amazing creation into the world
        zombieVillager.setPos(targetPosition.getX() + 0.5, targetPosition.getY(), targetPosition.getZ() + 0.5);
        level.addFreshEntity(zombieVillager);

        // Update all the block entity stats
        blockEntity.zombieVillager = zombieVillager;
        blockEntity.zombieVillagerSpawnPosition = targetPosition;
        blockEntity.setChanged();
    }

    public boolean isValidSpawn(ServerLevel level, BlockPos pos) {
        // Ground must be solid, and the two blocks above must not be solid so the villager does not suffocate
        return level.getBlockState(pos.below()).isSolid()
            && level.getBlockState(pos).isAir()
            && !level.getBlockState(pos.above()).isSolid();
    }

    public int getValidSpawn(ServerLevel level, BlockPos position) {
        // Check position itself first
        if (isValidSpawn(level, position)) return position.getY();
        // Search outward from block Y, alternating up and down
        for (int i = 1; i <= MAX_Y_SPAWN_OFFSET; i++) {
            if (isValidSpawn(level, position.above(i))) return position.above(i).getY();
            if (isValidSpawn(level, position.below(i))) return position.below(i).getY();
        }
        return position.getY();
    }
}
