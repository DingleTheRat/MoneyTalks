package net.dingletherat.block.custom;

import org.joml.Vector3f;

import com.mojang.math.Transformation;
import com.mojang.serialization.MapCodec;
import net.dingletherat.block.entity.custom.DoubloonEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.Display.BlockDisplay;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class Doubloon extends BaseEntityBlock {
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
        if (!level.isClientSide()) {
            // Create a block display, which will give the block an outline
            BlockDisplay display = new BlockDisplay(EntityTypes.BLOCK_DISPLAY, level);
            display.setPos(position.getX(), position.getY(), position.getZ());
            display.setBlockState(state);
            display.setGlowingTag(true);

            // Resize it to be a bit smaller than the block so there's no Z-fighting
            display.setPos(position.getX() + 0.005, position.getY() + 0.005, position.getZ() + 0.005);
            Transformation transformation = new Transformation(null, null, new Vector3f(0.99f, 0.99f, 0.99f), null);
            display.setTransformation(transformation);
            
            // Add it in!
            level.addFreshEntity(display);
            if (level.getBlockEntity(position) instanceof DoubloonEntity entity) {
                entity.outline = display;
                entity.setChanged();
            }
        }
    }

    
    @Override
    public BlockState playerWillDestroy(Level level, BlockPos position, BlockState state, Player player) {
        // When the block is broken, destroy the outline as well
        if (!level.isClientSide())
            if (level.getBlockEntity(position) instanceof DoubloonEntity entity)
                entity.outline.discard();

        return super.playerWillDestroy(level, position, state, player);
    }
}
