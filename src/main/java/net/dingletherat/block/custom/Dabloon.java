package net.dingletherat.block.custom;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class Dabloon extends Block {
    public static final Set<BlockPos> DABLOONS = new HashSet<>();

    public Dabloon(BlockPos position, BlockBehaviour.Properties properties) {
        super(properties);
        DABLOONS.add(position);
    }
}
