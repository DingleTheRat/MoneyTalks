package net.dingletherat.datagen;

import net.dingletherat.block.MoneyBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class MoneyLootTableProvider extends FabricBlockLootTableProvider {
    public MoneyLootTableProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
        super(dataOutput, registryLookup);
    }

    @Override
    public void generate() {
        addDrop(MoneyBlocks.TRIMMED_HOPPER);
        addDrop(MoneyBlocks.DOUBLE_TRIMMED_HOPPER);
    }
}
