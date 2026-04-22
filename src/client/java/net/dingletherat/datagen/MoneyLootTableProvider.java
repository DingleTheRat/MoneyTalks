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
        dropSelf((Block) MoneyBlocks.RED_MERCHANT_CARPET);
        dropSelf((Block) MoneyBlocks.ORANGE_MERCHANT_CARPET);
        dropSelf((Block) MoneyBlocks.YELLOW_MERCHANT_CARPET);
        dropSelf((Block) MoneyBlocks.LIME_MERCHANT_CARPET);
        dropSelf((Block) MoneyBlocks.GREEN_MERCHANT_CARPET);
        dropSelf((Block) MoneyBlocks.CYAN_MERCHANT_CARPET);
        dropSelf((Block) MoneyBlocks.LIGHT_BLUE_MERCHANT_CARPET);
        dropSelf((Block) MoneyBlocks.BLUE_MERCHANT_CARPET);
        dropSelf((Block) MoneyBlocks.PURPLE_MERCHANT_CARPET);
        dropSelf((Block) MoneyBlocks.MAGENTA_MERCHANT_CARPET);
        dropSelf((Block) MoneyBlocks.PINK_MERCHANT_CARPET);
        dropSelf((Block) MoneyBlocks.WHITE_MERCHANT_CARPET);
        dropSelf((Block) MoneyBlocks.LIGHT_GRAY_MERCHANT_CARPET);
        dropSelf((Block) MoneyBlocks.GRAY_MERCHANT_CARPET);
        dropSelf((Block) MoneyBlocks.BLACK_MERCHANT_CARPET);
        dropSelf((Block) MoneyBlocks.BROWN_MERCHANT_CARPET);
        dropSelf((Block) MoneyBlocks.DOUBLOON_COMPRESSOR);
    }
}
