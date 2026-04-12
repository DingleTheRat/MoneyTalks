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
    private EntityReference<ZombieVillager> zombieVillagerReference;

    public DoubloonEntity(BlockPos position, BlockState state) {
        super(MoneyBlockEntities.DOUBLOON_ENTITY, position, state);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (zombieVillager == null) return;
        zombieVillagerReference = EntityReference.of(zombieVillager); // pass entity not UUID
        EntityReference.store(zombieVillagerReference, output, "zombieVillager");
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        zombieVillagerReference = EntityReference.read(input, "zombieVillager");
        // don't try to resolve here, level is null
    }

    public void resolveVillager() {
        if (zombieVillager == null && zombieVillagerReference != null && level != null) {
            zombieVillager = EntityReference.get(zombieVillagerReference, level, ZombieVillager.class);
        }
    }
}
