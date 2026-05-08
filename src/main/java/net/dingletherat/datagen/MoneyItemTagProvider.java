package net.dingletherat.datagen;

import net.dingletherat.MoneyItemTags;
import net.dingletherat.block.MoneyBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.ItemTagsProvider;

import java.util.concurrent.CompletableFuture;

public class MoneyItemTagProvider extends ItemTagsProvider {
    public MoneyItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, "moneytalks");
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(MoneyItemTags.MERCHANT_CARPETS)
            .add(MoneyBlocks.BLACK_MERCHANT_CARPET.get().asItem(),
                MoneyBlocks.BLUE_MERCHANT_CARPET.get().asItem(),
                MoneyBlocks.BROWN_MERCHANT_CARPET.get().asItem(),
                MoneyBlocks.CYAN_MERCHANT_CARPET.get().asItem(),
                MoneyBlocks.GRAY_MERCHANT_CARPET.get().asItem(),
                MoneyBlocks.GREEN_MERCHANT_CARPET.get().asItem(),
                MoneyBlocks.LIGHT_BLUE_MERCHANT_CARPET.get().asItem(),
                MoneyBlocks.LIGHT_GRAY_MERCHANT_CARPET.get().asItem(),
                MoneyBlocks.LIME_MERCHANT_CARPET.get().asItem(),
                MoneyBlocks.MAGENTA_MERCHANT_CARPET.get().asItem(),
                MoneyBlocks.ORANGE_MERCHANT_CARPET.get().asItem(),
                MoneyBlocks.PINK_MERCHANT_CARPET.get().asItem(),
                MoneyBlocks.PURPLE_MERCHANT_CARPET.get().asItem(),
                MoneyBlocks.RED_MERCHANT_CARPET.get().asItem(),
                MoneyBlocks.WHITE_MERCHANT_CARPET.get().asItem(),
                MoneyBlocks.YELLOW_MERCHANT_CARPET.get().asItem());
    }
}
