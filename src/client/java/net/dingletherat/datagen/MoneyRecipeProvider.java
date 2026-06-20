package net.dingletherat.datagen;

import net.dingletherat.MoneyItemTags;
import net.dingletherat.MoneyTalks;
import net.dingletherat.block.MoneyBlocks;
import net.dingletherat.item.MoneyItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
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
                new CarpetPair((Block) MoneyBlocks.BLACK_MERCHANT_CARPET, Items.WOOL.pick(DyeColor.BLACK), Items.DYE.pick(DyeColor.BLACK)),
                new CarpetPair((Block) MoneyBlocks.BLUE_MERCHANT_CARPET, Items.WOOL.pick(DyeColor.BLUE), Items.DYE.pick(DyeColor.BLUE)),
                new CarpetPair((Block) MoneyBlocks.BROWN_MERCHANT_CARPET, Items.WOOL.pick(DyeColor.BROWN), Items.DYE.pick(DyeColor.BROWN)),
                new CarpetPair((Block) MoneyBlocks.CYAN_MERCHANT_CARPET, Items.WOOL.pick(DyeColor.CYAN), Items.DYE.pick(DyeColor.CYAN)),
                new CarpetPair((Block) MoneyBlocks.GRAY_MERCHANT_CARPET, Items.WOOL.pick(DyeColor.GRAY), Items.DYE.pick(DyeColor.GRAY)),
                new CarpetPair((Block) MoneyBlocks.GREEN_MERCHANT_CARPET, Items.WOOL.pick(DyeColor.GREEN), Items.DYE.pick(DyeColor.GREEN)),
                new CarpetPair((Block) MoneyBlocks.LIGHT_BLUE_MERCHANT_CARPET, Items.WOOL.pick(DyeColor.LIGHT_BLUE), Items.DYE.pick(DyeColor.LIGHT_BLUE)),
                new CarpetPair((Block) MoneyBlocks.LIGHT_GRAY_MERCHANT_CARPET, Items.WOOL.pick(DyeColor.LIGHT_GRAY), Items.DYE.pick(DyeColor.LIGHT_GRAY)),
                new CarpetPair((Block) MoneyBlocks.LIME_MERCHANT_CARPET, Items.WOOL.pick(DyeColor.LIME), Items.DYE.pick(DyeColor.LIME)),
                new CarpetPair((Block) MoneyBlocks.MAGENTA_MERCHANT_CARPET, Items.WOOL.pick(DyeColor.MAGENTA), Items.DYE.pick(DyeColor.MAGENTA)),
                new CarpetPair((Block) MoneyBlocks.ORANGE_MERCHANT_CARPET, Items.WOOL.pick(DyeColor.ORANGE), Items.DYE.pick(DyeColor.ORANGE)),
                new CarpetPair((Block) MoneyBlocks.PINK_MERCHANT_CARPET, Items.WOOL.pick(DyeColor.PINK), Items.DYE.pick(DyeColor.PINK)),
                new CarpetPair((Block) MoneyBlocks.PURPLE_MERCHANT_CARPET, Items.WOOL.pick(DyeColor.PURPLE), Items.DYE.pick(DyeColor.PURPLE)),
                new CarpetPair((Block) MoneyBlocks.RED_MERCHANT_CARPET, Items.WOOL.pick(DyeColor.RED), Items.DYE.pick(DyeColor.RED)),
                new CarpetPair((Block) MoneyBlocks.WHITE_MERCHANT_CARPET, Items.WOOL.pick(DyeColor.WHITE), Items.DYE.pick(DyeColor.WHITE)),
                new CarpetPair((Block) MoneyBlocks.YELLOW_MERCHANT_CARPET, Items.WOOL.pick(DyeColor.YELLOW), Items.DYE.pick(DyeColor.YELLOW))
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

                shapeless(RecipeCategory.MISC, new ItemStackTemplate(MoneyItems.DOLLAR, 10))
                    .requires(MoneyBlocks.DOUBLOON.asItem())
                    .unlockedBy("has_doubloon", has(MoneyBlocks.DOUBLOON.asItem()))
                    .save(recipeExporter);
                shaped(RecipeCategory.MISC, (Block) MoneyBlocks.DOUBLOON_COMPRESSOR)
                    .pattern("DDD")
                    .pattern("DND")
                    .pattern("SSS")
                    .define('D', Items.DEEPSLATE)
                    .define('N', Items.NETHERITE_INGOT)
                    .define('S', Blocks.SOUL_SAND)
                    .unlockedBy("has_netherite_ingot", has(Items.NETHERITE_INGOT))
                    .save(recipeExporter);
            }
        };
    }

    @Override
    public String getName() {
        return MoneyTalks.MOD_ID + " Recipes";
    }
}
