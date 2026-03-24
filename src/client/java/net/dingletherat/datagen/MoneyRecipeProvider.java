package net.dingletherat.datagen;

import net.dingletherat.MoneyItemTags;
import net.dingletherat.MoneyTalks;
import net.dingletherat.block.MoneyBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.data.recipe.RecipeExporter;
import net.minecraft.data.recipe.RecipeGenerator;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MoneyRecipeProvider extends FabricRecipeProvider {
    public MoneyRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    record CarpetPair(Block carpet, Item wool, Item dye) {}

    @Override
    protected RecipeGenerator getRecipeGenerator(RegistryWrapper.WrapperLookup wrapperLookup, RecipeExporter recipeExporter) {
        return new RecipeGenerator(wrapperLookup, recipeExporter) {
            @Override
            public void generate() {
            List<CarpetPair> pairs = List.of(
                new CarpetPair(MoneyBlocks.BLACK_MERCHANT_CARPET, Items.BLACK_WOOL, Items.BLACK_DYE),
                new CarpetPair(MoneyBlocks.BLUE_MERCHANT_CARPET, Items.BLUE_WOOL, Items.BLUE_DYE),
                new CarpetPair(MoneyBlocks.BROWN_MERCHANT_CARPET, Items.BROWN_WOOL, Items.BROWN_DYE),
                new CarpetPair(MoneyBlocks.CYAN_MERCHANT_CARPET, Items.CYAN_WOOL, Items.CYAN_DYE),
                new CarpetPair(MoneyBlocks.GRAY_MERCHANT_CARPET, Items.GRAY_WOOL, Items.GRAY_DYE),
                new CarpetPair(MoneyBlocks.GREEN_MERCHANT_CARPET, Items.GREEN_WOOL, Items.GREEN_DYE),
                new CarpetPair(MoneyBlocks.LIGHT_BLUE_MERCHANT_CARPET, Items.LIGHT_BLUE_WOOL, Items.LIGHT_BLUE_DYE),
                new CarpetPair(MoneyBlocks.LIGHT_GRAY_MERCHANT_CARPET, Items.LIGHT_GRAY_WOOL, Items.LIGHT_GRAY_DYE),
                new CarpetPair(MoneyBlocks.LIME_MERCHANT_CARPET, Items.LIME_WOOL, Items.LIME_DYE),
                new CarpetPair(MoneyBlocks.MAGENTA_MERCHANT_CARPET, Items.MAGENTA_WOOL, Items.MAGENTA_DYE),
                new CarpetPair(MoneyBlocks.ORANGE_MERCHANT_CARPET, Items.ORANGE_WOOL, Items.ORANGE_DYE),
                new CarpetPair(MoneyBlocks.PINK_MERCHANT_CARPET, Items.PINK_WOOL, Items.PINK_DYE),
                new CarpetPair(MoneyBlocks.PURPLE_MERCHANT_CARPET, Items.PURPLE_WOOL, Items.PURPLE_DYE),
                new CarpetPair(MoneyBlocks.RED_MERCHANT_CARPET, Items.RED_WOOL, Items.RED_DYE),
                new CarpetPair(MoneyBlocks.WHITE_MERCHANT_CARPET, Items.WHITE_WOOL, Items.WHITE_DYE),
                new CarpetPair(MoneyBlocks.YELLOW_MERCHANT_CARPET, Items.YELLOW_WOOL, Items.YELLOW_DYE)
            );

                for (CarpetPair pair : pairs) {
                    createShaped(RecipeCategory.MISC, pair.carpet())
                        .pattern("WWW")
                        .input('W', pair.wool())
                        .criterion(hasItem(pair.wool()), conditionsFromItem(pair.wool()))
                        .offerTo(exporter, RegistryKey.of(RegistryKeys.RECIPE, Identifier.of(MoneyTalks.MOD_ID,
                            "merchant_carpet_" + Registries.ITEM.getId(pair.wool()).getPath())));
                }
                for (CarpetPair target : pairs) {
                    createShapeless(RecipeCategory.MISC, target.carpet())
                        .input(MoneyItemTags.MERCHANT_CARPETS)
                        .input(target.dye())
                        .criterion(hasItem(target.dye()), conditionsFromItem(target.dye()))
                        .offerTo(exporter, RegistryKey.of(RegistryKeys.RECIPE, Identifier.of(MoneyTalks.MOD_ID,
                            "merchant_carpet_dye_" + Registries.ITEM.getId(target.dye()).getPath())));
                }

                createShaped(RecipeCategory.MISC, MoneyBlocks.DOUBLE_TRIMMED_HOPPER)
                    .pattern("D D")
                    .pattern("DHD")
                    .pattern("D D")
                    .input('D', Items.DIAMOND)
                    .input('H', Blocks.HOPPER)
                    .criterion(hasItem(Items.DIAMOND), conditionsFromItem(Items.DIAMOND))
                    .offerTo(exporter);
                createShaped(RecipeCategory.MISC, MoneyBlocks.TRIMMED_HOPPER)
                    .pattern("DHD")
                    .input('D', Items.DIAMOND)
                    .input('H', Blocks.HOPPER)
                    .criterion(hasItem(Items.DIAMOND), conditionsFromItem(Items.DIAMOND))
                    .offerTo(exporter);
            }
        };
    }

    @Override
    public String getName() {
        return "TutorialMod Recipes";
    }
}
