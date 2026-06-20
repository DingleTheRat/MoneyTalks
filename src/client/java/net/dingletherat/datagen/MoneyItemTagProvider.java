package net.dingletherat.datagen;

import net.dingletherat.MoneyItemTags;
import net.dingletherat.block.MoneyBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagsProvider;
import net.minecraft.core.HolderLookup;

import java.util.concurrent.CompletableFuture;

public class MoneyItemTagProvider extends FabricTagsProvider.ItemTagsProvider {
    public MoneyItemTagProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider wrapperLookup) {
        builder(MoneyItemTags.MERCHANT_CARPETS)
            .add(MoneyBlocks.BLACK_MERCHANT_CARPET.asItem().builtInRegistryHolder().key(),
                MoneyBlocks.BLUE_MERCHANT_CARPET.asItem().builtInRegistryHolder().key(),
                MoneyBlocks.BROWN_MERCHANT_CARPET.asItem().builtInRegistryHolder().key(),
                MoneyBlocks.CYAN_MERCHANT_CARPET.asItem().builtInRegistryHolder().key(),
                MoneyBlocks.GRAY_MERCHANT_CARPET.asItem().builtInRegistryHolder().key(),
                MoneyBlocks.GREEN_MERCHANT_CARPET.asItem().builtInRegistryHolder().key(),
                MoneyBlocks.LIGHT_BLUE_MERCHANT_CARPET.asItem().builtInRegistryHolder().key(),
                MoneyBlocks.LIGHT_GRAY_MERCHANT_CARPET.asItem().builtInRegistryHolder().key(),
                MoneyBlocks.LIME_MERCHANT_CARPET.asItem().builtInRegistryHolder().key(),
                MoneyBlocks.MAGENTA_MERCHANT_CARPET.asItem().builtInRegistryHolder().key(),
                MoneyBlocks.ORANGE_MERCHANT_CARPET.asItem().builtInRegistryHolder().key(),
                MoneyBlocks.PINK_MERCHANT_CARPET.asItem().builtInRegistryHolder().key(),
                MoneyBlocks.PURPLE_MERCHANT_CARPET.asItem().builtInRegistryHolder().key(),
                MoneyBlocks.RED_MERCHANT_CARPET.asItem().builtInRegistryHolder().key(),
                MoneyBlocks.WHITE_MERCHANT_CARPET.asItem().builtInRegistryHolder().key(),
                MoneyBlocks.YELLOW_MERCHANT_CARPET.asItem().builtInRegistryHolder().key());
    }
}
