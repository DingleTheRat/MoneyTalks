package net.dingletherat.datagen;

import net.dingletherat.MoneyItemTags;
import net.dingletherat.block.MoneyBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class MoneyItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public MoneyItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.Provider wrapperLookup) {
        valueLookupBuilder(MoneyItemTags.MERCHANT_CARPETS)
            .add(
                MoneyBlocks.BLACK_MERCHANT_CARPET.asItem(),
                MoneyBlocks.BLUE_MERCHANT_CARPET.asItem(),
                MoneyBlocks.BROWN_MERCHANT_CARPET.asItem(),
                MoneyBlocks.CYAN_MERCHANT_CARPET.asItem(),
                MoneyBlocks.GRAY_MERCHANT_CARPET.asItem(),
                MoneyBlocks.GREEN_MERCHANT_CARPET.asItem(),
                MoneyBlocks.LIGHT_BLUE_MERCHANT_CARPET.asItem(),
                MoneyBlocks.LIGHT_GRAY_MERCHANT_CARPET.asItem(),
                MoneyBlocks.LIME_MERCHANT_CARPET.asItem(),
                MoneyBlocks.MAGENTA_MERCHANT_CARPET.asItem(),
                MoneyBlocks.ORANGE_MERCHANT_CARPET.asItem(),
                MoneyBlocks.PINK_MERCHANT_CARPET.asItem(),
                MoneyBlocks.PURPLE_MERCHANT_CARPET.asItem(),
                MoneyBlocks.RED_MERCHANT_CARPET.asItem(),
                MoneyBlocks.WHITE_MERCHANT_CARPET.asItem(),
                MoneyBlocks.YELLOW_MERCHANT_CARPET.asItem()
            );
    }
}
