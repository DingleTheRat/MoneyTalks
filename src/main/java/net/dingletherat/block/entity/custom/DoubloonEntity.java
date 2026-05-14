package net.dingletherat.block.entity.custom;

import net.dingletherat.block.entity.MoneyBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityReference;
import net.minecraft.world.entity.Display.BlockDisplay;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class DoubloonEntity extends BlockEntity {
    public BlockDisplay outline;
    private EntityReference<BlockDisplay> outlineReference;

    public DoubloonEntity(BlockPos position, BlockState state) {
        super(MoneyBlockEntities.DOUBLOON_ENTITY.get(), position, state);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);

        if (outline == null) return;
        outlineReference = EntityReference.of(outline);
        EntityReference.store(outlineReference, output, "outline");
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        outlineReference = EntityReference.read(input, "outline");
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);

        // When the block is loaded, also load the outline as well
        if (outline == null && outlineReference != null && level != null)
            outline = EntityReference.get(outlineReference, level, BlockDisplay.class);
    }
}
