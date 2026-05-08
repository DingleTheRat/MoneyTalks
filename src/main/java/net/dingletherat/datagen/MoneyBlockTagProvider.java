package net.dingletherat.datagen;

import net.dingletherat.block.MoneyBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;

import java.util.concurrent.CompletableFuture;

public class MoneyBlockTagProvider extends BlockTagsProvider {
    public MoneyBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, "moneytalks");
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.NEEDS_IRON_TOOL)
            .add(MoneyBlocks.TRIMMED_HOPPER.get())
            .add(MoneyBlocks.DOUBLE_TRIMMED_HOPPER.get());
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(MoneyBlocks.TRIMMED_HOPPER.get())
            .add(MoneyBlocks.DOUBLE_TRIMMED_HOPPER.get())
            .add(MoneyBlocks.DOUBLOON_COMPRESSOR.get());
    }
}
