package net.dingletherat.datagen;

import net.minecraft.world.item.DyeColor;
import net.dingletherat.registry.MoneyItemTags;
import net.dingletherat.registry.MoneyBlocks;
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
            .add(MoneyBlocks.CARPETS.get(DyeColor.BLACK.ordinal()).get().asItem().builtInRegistryHolder().key(),
                MoneyBlocks.CARPETS.get(DyeColor.BLUE.ordinal()).get().asItem().builtInRegistryHolder().key(),
                MoneyBlocks.CARPETS.get(DyeColor.BROWN.ordinal()).get().asItem().builtInRegistryHolder().key(),
                MoneyBlocks.CARPETS.get(DyeColor.CYAN.ordinal()).get().asItem().builtInRegistryHolder().key(),
                MoneyBlocks.CARPETS.get(DyeColor.GRAY.ordinal()).get().asItem().builtInRegistryHolder().key(),
                MoneyBlocks.CARPETS.get(DyeColor.GREEN.ordinal()).get().asItem().builtInRegistryHolder().key(),
                MoneyBlocks.CARPETS.get(DyeColor.LIGHT_BLUE.ordinal()).get().asItem().builtInRegistryHolder().key(),
                MoneyBlocks.CARPETS.get(DyeColor.LIGHT_GRAY.ordinal()).get().asItem().builtInRegistryHolder().key(),
                MoneyBlocks.CARPETS.get(DyeColor.LIME.ordinal()).get().asItem().builtInRegistryHolder().key(),
                MoneyBlocks.CARPETS.get(DyeColor.MAGENTA.ordinal()).get().asItem().builtInRegistryHolder().key(),
                MoneyBlocks.CARPETS.get(DyeColor.ORANGE.ordinal()).get().asItem().builtInRegistryHolder().key(),
                MoneyBlocks.CARPETS.get(DyeColor.PINK.ordinal()).get().asItem().builtInRegistryHolder().key(),
                MoneyBlocks.CARPETS.get(DyeColor.PURPLE.ordinal()).get().asItem().builtInRegistryHolder().key(),
                MoneyBlocks.CARPETS.get(DyeColor.RED.ordinal()).get().asItem().builtInRegistryHolder().key(),
                MoneyBlocks.CARPETS.get(DyeColor.WHITE.ordinal()).get().asItem().builtInRegistryHolder().key(),
                MoneyBlocks.CARPETS.get(DyeColor.YELLOW.ordinal()).get().asItem().builtInRegistryHolder().key());
    }
}
