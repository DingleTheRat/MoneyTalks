package net.dingletherat.datagen;

import net.dingletherat.block.MoneyBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class MoneyLootTableProvider extends LootTableProvider {
    public MoneyLootTableProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, Set.of(), List.of(new SubProviderEntry(MoneyBlockLoot::new, LootContextParamSets.BLOCK)), lookupProvider);
    }

    public static class MoneyBlockLoot extends BlockLootSubProvider {
        protected MoneyBlockLoot(HolderLookup.Provider lookupProvider) {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags(), lookupProvider);
        }

        @Override
        protected void generate() {
            dropSelf(MoneyBlocks.TRIMMED_HOPPER.get());
            dropSelf(MoneyBlocks.DOUBLE_TRIMMED_HOPPER.get());
            dropSelf(MoneyBlocks.RED_MERCHANT_CARPET.get());
            dropSelf(MoneyBlocks.ORANGE_MERCHANT_CARPET.get());
            dropSelf(MoneyBlocks.YELLOW_MERCHANT_CARPET.get());
            dropSelf(MoneyBlocks.LIME_MERCHANT_CARPET.get());
            dropSelf(MoneyBlocks.GREEN_MERCHANT_CARPET.get());
            dropSelf(MoneyBlocks.CYAN_MERCHANT_CARPET.get());
            dropSelf(MoneyBlocks.LIGHT_BLUE_MERCHANT_CARPET.get());
            dropSelf(MoneyBlocks.BLUE_MERCHANT_CARPET.get());
            dropSelf(MoneyBlocks.PURPLE_MERCHANT_CARPET.get());
            dropSelf(MoneyBlocks.MAGENTA_MERCHANT_CARPET.get());
            dropSelf(MoneyBlocks.PINK_MERCHANT_CARPET.get());
            dropSelf(MoneyBlocks.WHITE_MERCHANT_CARPET.get());
            dropSelf(MoneyBlocks.LIGHT_GRAY_MERCHANT_CARPET.get());
            dropSelf(MoneyBlocks.GRAY_MERCHANT_CARPET.get());
            dropSelf(MoneyBlocks.BLACK_MERCHANT_CARPET.get());
            dropSelf(MoneyBlocks.BROWN_MERCHANT_CARPET.get());
            dropSelf(MoneyBlocks.DOUBLOON_COMPRESSOR.get());
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return List.of(
                MoneyBlocks.TRIMMED_HOPPER.get(),
                MoneyBlocks.DOUBLE_TRIMMED_HOPPER.get(),
                MoneyBlocks.RED_MERCHANT_CARPET.get(),
                MoneyBlocks.ORANGE_MERCHANT_CARPET.get(),
                MoneyBlocks.YELLOW_MERCHANT_CARPET.get(),
                MoneyBlocks.LIME_MERCHANT_CARPET.get(),
                MoneyBlocks.GREEN_MERCHANT_CARPET.get(),
                MoneyBlocks.CYAN_MERCHANT_CARPET.get(),
                MoneyBlocks.LIGHT_BLUE_MERCHANT_CARPET.get(),
                MoneyBlocks.BLUE_MERCHANT_CARPET.get(),
                MoneyBlocks.PURPLE_MERCHANT_CARPET.get(),
                MoneyBlocks.MAGENTA_MERCHANT_CARPET.get(),
                MoneyBlocks.PINK_MERCHANT_CARPET.get(),
                MoneyBlocks.WHITE_MERCHANT_CARPET.get(),
                MoneyBlocks.LIGHT_GRAY_MERCHANT_CARPET.get(),
                MoneyBlocks.GRAY_MERCHANT_CARPET.get(),
                MoneyBlocks.BLACK_MERCHANT_CARPET.get(),
                MoneyBlocks.BROWN_MERCHANT_CARPET.get(),
                MoneyBlocks.DOUBLOON_COMPRESSOR.get()
            );
        }
    }
}
