package net.dingletherat.block.entity.custom;

import net.dingletherat.block.entity.MoneyBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.monster.zombie.ZombieVillager;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class DoubloonEntity extends BlockEntity {
    public ZombieVillager zombieVillager;
    public BlockPos zombieVillagerSpawnPosition;
    private EntityReference<ZombieVillager> zombieVillagerReference;

    public DoubloonEntity(BlockPos position, BlockState state) {
        super(MoneyBlockEntities.DOUBLOON_ENTITY, position, state);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);

        if (zombieVillager == null) return;
        int[] arrayPosition = {zombieVillagerSpawnPosition.getX(), zombieVillagerSpawnPosition.getY(), zombieVillagerSpawnPosition.getZ()};
        output.putIntArray("zombieVillagerSpawnPosition", arrayPosition);

        zombieVillagerReference = EntityReference.of(zombieVillager);
        EntityReference.store(zombieVillagerReference, output, "zombieVillager");
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        zombieVillagerReference = EntityReference.read(input, "zombieVillager");

        int[] arrayPosition = input.getIntArray("zombieVillagerSpawnPosition").orElse(new int[]{0, 0, 0});
        zombieVillagerSpawnPosition = new BlockPos(arrayPosition[0], arrayPosition[1], arrayPosition[2]);
    }

    public void resolveVillager() {
        if (zombieVillager == null && zombieVillagerReference != null && level != null)
            zombieVillager = EntityReference.get(zombieVillagerReference, level, ZombieVillager.class);
    }
}
