package net.dingletherat.datagen;

import net.dingletherat.MoneyItemTags;
import net.dingletherat.MoneyTalks;
import net.dingletherat.block.MoneyBlocks;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MoneyRecipeProvider extends FabricRecipeProvider {
    public MoneyRecipeProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    record CarpetPair(Block carpet, Item wool, Item dye) {}

    @Override
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider wrapperLookup, RecipeOutput recipeExporter) {
        return new RecipeProvider(wrapperLookup, recipeExporter) {
            @Override
            public void buildRecipes() {
            List<CarpetPair> pairs = List.of(
                new CarpetPair((Block) MoneyBlocks.BLACK_MERCHANT_CARPET, Items.BLACK_WOOL, Items.BLACK_DYE),
                new CarpetPair((Block) MoneyBlocks.BLUE_MERCHANT_CARPET, Items.BLUE_WOOL, Items.BLUE_DYE),
                new CarpetPair((Block) MoneyBlocks.BROWN_MERCHANT_CARPET, Items.BROWN_WOOL, Items.BROWN_DYE),
                new CarpetPair((Block) MoneyBlocks.CYAN_MERCHANT_CARPET, Items.CYAN_WOOL, Items.CYAN_DYE),
                new CarpetPair((Block) MoneyBlocks.GRAY_MERCHANT_CARPET, Items.GRAY_WOOL, Items.GRAY_DYE),
                new CarpetPair((Block) MoneyBlocks.GREEN_MERCHANT_CARPET, Items.GREEN_WOOL, Items.GREEN_DYE),
                new CarpetPair((Block) MoneyBlocks.LIGHT_BLUE_MERCHANT_CARPET, Items.LIGHT_BLUE_WOOL, Items.LIGHT_BLUE_DYE),
                new CarpetPair((Block) MoneyBlocks.LIGHT_GRAY_MERCHANT_CARPET, Items.LIGHT_GRAY_WOOL, Items.LIGHT_GRAY_DYE),
                new CarpetPair((Block) MoneyBlocks.LIME_MERCHANT_CARPET, Items.LIME_WOOL, Items.LIME_DYE),
                new CarpetPair((Block) MoneyBlocks.MAGENTA_MERCHANT_CARPET, Items.MAGENTA_WOOL, Items.MAGENTA_DYE),
                new CarpetPair((Block) MoneyBlocks.ORANGE_MERCHANT_CARPET, Items.ORANGE_WOOL, Items.ORANGE_DYE),
                new CarpetPair((Block) MoneyBlocks.PINK_MERCHANT_CARPET, Items.PINK_WOOL, Items.PINK_DYE),
                new CarpetPair((Block) MoneyBlocks.PURPLE_MERCHANT_CARPET, Items.PURPLE_WOOL, Items.PURPLE_DYE),
                new CarpetPair((Block) MoneyBlocks.RED_MERCHANT_CARPET, Items.RED_WOOL, Items.RED_DYE),
                new CarpetPair((Block) MoneyBlocks.WHITE_MERCHANT_CARPET, Items.WHITE_WOOL, Items.WHITE_DYE),
                new CarpetPair((Block) MoneyBlocks.YELLOW_MERCHANT_CARPET, Items.YELLOW_WOOL, Items.YELLOW_DYE)
            );

                for (CarpetPair pair : pairs) {
                    shaped(RecipeCategory.MISC, pair.carpet())
                        .pattern("WWW")
                        .define('W', pair.wool())
                        .unlockedBy("has_wool", has(pair.wool()))
                        .save(recipeExporter, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID,
                            "merchant_carpet_" + BuiltInRegistries.ITEM.getKey(pair.wool()).getPath())));
                }
                for (CarpetPair target : pairs) {
                    shapeless(RecipeCategory.MISC, target.carpet())
                        .requires(MoneyItemTags.MERCHANT_CARPETS)
                        .requires(target.dye())
                        .unlockedBy("has_dye", has(target.dye()))
                        .save(recipeExporter, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID,
                            "merchant_carpet_dye_" + BuiltInRegistries.ITEM.getKey(target.dye()).getPath())));
                }

                shaped(RecipeCategory.MISC, (Block) MoneyBlocks.DOUBLE_TRIMMED_HOPPER)
                    .pattern("D D")
                    .pattern("DHD")
                    .pattern("D D")
                    .define('D', Items.DIAMOND)
                    .define('H', Blocks.HOPPER)
                    .unlockedBy("has_diamond", has(Items.DIAMOND))
                    .save(recipeExporter);
                shaped(RecipeCategory.MISC, (Block) MoneyBlocks.TRIMMED_HOPPER)
                    .pattern("DHD")
                    .define('D', Items.DIAMOND)
                    .define('H', Blocks.HOPPER)
                    .unlockedBy("has_diamond", has(Items.DIAMOND))
                    .save(recipeExporter);
            }
        };
    }

    @Override
    public String getName() {
        return MoneyTalks.MOD_ID + " Recipes";
    }
}
