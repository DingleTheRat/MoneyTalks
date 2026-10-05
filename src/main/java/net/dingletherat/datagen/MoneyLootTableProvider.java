package net.dingletherat.datagen;

import net.dingletherat.block.MoneyBlocks;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;

public class MoneyLootTableProvider extends LootTableProvider {
    public MoneyLootTableProvider() {
        super(Set.of(), List.of(new SubProviderEntry(MoneyBlockLoot::new, LootContextParamSets.BLOCK)));
    }

    public static class MoneyBlockLoot extends BlockLootSubProvider {
        protected MoneyBlockLoot(LootTableSubProvider.Context output) {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags(), output);
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
