package net.dingletherat.datagen;

import net.minecraft.world.item.DyeColor;
import net.dingletherat.registry.MoneyBlocks;
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
        dropSelf((Block) MoneyBlocks.TRIMMED_HOPPER.get());
        dropSelf(MoneyBlocks.CARPETS.get(DyeColor.RED.ordinal()).get());
        dropSelf(MoneyBlocks.CARPETS.get(DyeColor.ORANGE.ordinal()).get());
        dropSelf(MoneyBlocks.CARPETS.get(DyeColor.YELLOW.ordinal()).get());
        dropSelf(MoneyBlocks.CARPETS.get(DyeColor.LIME.ordinal()).get());
        dropSelf(MoneyBlocks.CARPETS.get(DyeColor.GREEN.ordinal()).get());
        dropSelf(MoneyBlocks.CARPETS.get(DyeColor.CYAN.ordinal()).get());
        dropSelf(MoneyBlocks.CARPETS.get(DyeColor.LIGHT_BLUE.ordinal()).get());
        dropSelf(MoneyBlocks.CARPETS.get(DyeColor.BLUE.ordinal()).get());
        dropSelf(MoneyBlocks.CARPETS.get(DyeColor.PURPLE.ordinal()).get());
        dropSelf(MoneyBlocks.CARPETS.get(DyeColor.MAGENTA.ordinal()).get());
        dropSelf(MoneyBlocks.CARPETS.get(DyeColor.PINK.ordinal()).get());
        dropSelf(MoneyBlocks.CARPETS.get(DyeColor.WHITE.ordinal()).get());
        dropSelf(MoneyBlocks.CARPETS.get(DyeColor.LIGHT_GRAY.ordinal()).get());
        dropSelf(MoneyBlocks.CARPETS.get(DyeColor.GRAY.ordinal()).get());
        dropSelf(MoneyBlocks.CARPETS.get(DyeColor.BLACK.ordinal()).get());
        dropSelf(MoneyBlocks.CARPETS.get(DyeColor.BROWN.ordinal()).get());
        dropSelf((Block) MoneyBlocks.DOUBLOON_COMPRESSOR.get());
    }
}
