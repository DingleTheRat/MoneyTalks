package net.dingletherat.datagen;

import net.dingletherat.MoneyItemTags;
import net.dingletherat.MoneyTalks;
import net.dingletherat.block.MoneyBlocks;
import net.dingletherat.item.MoneyItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MoneyRecipeProvider extends RecipeProvider {
    public MoneyRecipeProvider(HolderLookup.Provider lookupProvider, RecipeOutput output) {
        super(lookupProvider, output);
    }

    record CarpetPair(Block carpet, Item wool, Item dye) {}

    @Override
    protected void buildRecipes() {
        List<CarpetPair> pairs = List.of(
            new CarpetPair(MoneyBlocks.BLACK_MERCHANT_CARPET.get(), Items.BLACK_WOOL, Items.BLACK_DYE),
            new CarpetPair(MoneyBlocks.BLUE_MERCHANT_CARPET.get(), Items.BLUE_WOOL, Items.BLUE_DYE),
            new CarpetPair(MoneyBlocks.BROWN_MERCHANT_CARPET.get(), Items.BROWN_WOOL, Items.BROWN_DYE),
            new CarpetPair(MoneyBlocks.CYAN_MERCHANT_CARPET.get(), Items.CYAN_WOOL, Items.CYAN_DYE),
            new CarpetPair(MoneyBlocks.GRAY_MERCHANT_CARPET.get(), Items.GRAY_WOOL, Items.GRAY_DYE),
            new CarpetPair(MoneyBlocks.GREEN_MERCHANT_CARPET.get(), Items.GREEN_WOOL, Items.GREEN_DYE),
            new CarpetPair(MoneyBlocks.LIGHT_BLUE_MERCHANT_CARPET.get(), Items.LIGHT_BLUE_WOOL, Items.LIGHT_BLUE_DYE),
            new CarpetPair(MoneyBlocks.LIGHT_GRAY_MERCHANT_CARPET.get(), Items.LIGHT_GRAY_WOOL, Items.LIGHT_GRAY_DYE),
            new CarpetPair(MoneyBlocks.LIME_MERCHANT_CARPET.get(), Items.LIME_WOOL, Items.LIME_DYE),
            new CarpetPair(MoneyBlocks.MAGENTA_MERCHANT_CARPET.get(), Items.MAGENTA_WOOL, Items.MAGENTA_DYE),
            new CarpetPair(MoneyBlocks.ORANGE_MERCHANT_CARPET.get(), Items.ORANGE_WOOL, Items.ORANGE_DYE),
            new CarpetPair(MoneyBlocks.PINK_MERCHANT_CARPET.get(), Items.PINK_WOOL, Items.PINK_DYE),
            new CarpetPair(MoneyBlocks.PURPLE_MERCHANT_CARPET.get(), Items.PURPLE_WOOL, Items.PURPLE_DYE),
            new CarpetPair(MoneyBlocks.RED_MERCHANT_CARPET.get(), Items.RED_WOOL, Items.RED_DYE),
            new CarpetPair(MoneyBlocks.WHITE_MERCHANT_CARPET.get(), Items.WHITE_WOOL, Items.WHITE_DYE),
            new CarpetPair(MoneyBlocks.YELLOW_MERCHANT_CARPET.get(), Items.YELLOW_WOOL, Items.YELLOW_DYE)
        );

        for (CarpetPair pair : pairs) {
            shaped(RecipeCategory.MISC, pair.carpet())
                .pattern("WWW")
                .define('W', pair.wool())
                .unlockedBy("has_wool", has(pair.wool()))
                .save(output, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID,
                    "merchant_carpet_" + BuiltInRegistries.ITEM.getKey(pair.wool()).getPath())));
        }

        for (CarpetPair target : pairs) {
            shapeless(RecipeCategory.MISC, target.carpet())
                .requires(MoneyItemTags.MERCHANT_CARPETS)
                .requires(target.dye())
                .unlockedBy("has_dye", has(target.dye()))
                .save(output, ResourceKey.create(Registries.RECIPE, Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID,
                    "merchant_carpet_dye_" + BuiltInRegistries.ITEM.getKey(target.dye()).getPath())));
        }

        shaped(RecipeCategory.MISC, MoneyBlocks.DOUBLE_TRIMMED_HOPPER.get())
            .pattern("D D")
            .pattern("DHD")
            .pattern("D D")
            .define('D', Items.DIAMOND)
            .define('H', Blocks.HOPPER)
            .unlockedBy("has_diamond", has(Items.DIAMOND))
            .save(output);

        shaped(RecipeCategory.MISC, MoneyBlocks.TRIMMED_HOPPER.get())
            .pattern("DHD")
            .define('D', Items.DIAMOND)
            .define('H', Blocks.HOPPER)
            .unlockedBy("has_diamond", has(Items.DIAMOND))
            .save(output);

        shapeless(RecipeCategory.MISC, MoneyItems.DOLLAR.get(), 10)
            .requires(MoneyBlocks.DOUBLOON.get().asItem())
            .unlockedBy("has_doubloon", has(MoneyBlocks.DOUBLOON.get().asItem()))
            .save(output);

        shaped(RecipeCategory.MISC, MoneyBlocks.DOUBLOON_COMPRESSOR.get())
            .pattern("DDD")
            .pattern("DND")
            .pattern("SSS")
            .define('D', Items.DEEPSLATE)
            .define('N', Items.NETHERITE_INGOT)
            .define('S', Blocks.SOUL_SAND)
            .unlockedBy("has_netherite_ingot", has(Items.NETHERITE_INGOT))
            .save(output);
    }

    public static class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(output, lookupProvider);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider lookupProvider, RecipeOutput output) {
            return new MoneyRecipeProvider(lookupProvider, output);
        }

        @Override
        public String getName() {
            return MoneyTalks.MOD_ID + " Recipes";
        }
    }
}
