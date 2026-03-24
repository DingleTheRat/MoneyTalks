package net.dingletherat.datagen;

import net.dingletherat.MoneyItemTags;
import net.dingletherat.block.MoneyBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.BlockTags;

import java.util.concurrent.CompletableFuture;

public class MoneyBlockTagProvider extends FabricTagProvider.BlockTagProvider {
    public MoneyBlockTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {
        valueLookupBuilder(BlockTags.NEEDS_IRON_TOOL)
            .add(MoneyBlocks.TRIMMED_HOPPER)
            .add(MoneyBlocks.DOUBLE_TRIMMED_HOPPER);

        valueLookupBuilder(BlockTags.PICKAXE_MINEABLE)
            .add(MoneyBlocks.TRIMMED_HOPPER)
            .add(MoneyBlocks.DOUBLE_TRIMMED_HOPPER);
    }
}
