package net.dingletherat.datagen;

import net.dingletherat.block.MoneyBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

public class MoneyBlockTagProvider extends FabricTagsProvider.BlockTagsProvider {
    public MoneyBlockTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        builder(BlockTags.NEEDS_IRON_TOOL)
            .add(((Block) MoneyBlocks.TRIMMED_HOPPER).builtInRegistryHolder().key(),
                 ((Block) MoneyBlocks.DOUBLE_TRIMMED_HOPPER).builtInRegistryHolder().key());

        builder(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(((Block) MoneyBlocks.TRIMMED_HOPPER).builtInRegistryHolder().key(),
                 ((Block) MoneyBlocks.DOUBLE_TRIMMED_HOPPER).builtInRegistryHolder().key(),
                 ((Block) MoneyBlocks.DOUBLOON_COMPRESSOR).builtInRegistryHolder().key());
    }
}
