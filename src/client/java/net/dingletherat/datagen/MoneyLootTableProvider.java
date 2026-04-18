package net.dingletherat.datagen;

import net.dingletherat.block.MoneyBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

public class MoneyLootTableProvider extends FabricBlockLootSubProvider {
    public MoneyLootTableProvider(FabricPackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        dropSelf((Block) MoneyBlocks.TRIMMED_HOPPER);
        dropSelf((Block) MoneyBlocks.DOUBLE_TRIMMED_HOPPER);
        dropSelf((Block) MoneyBlocks.DOUBLOON_COMPRESSOR);
    }
}
