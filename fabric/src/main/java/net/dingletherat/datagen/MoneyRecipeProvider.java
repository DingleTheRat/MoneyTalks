package net.dingletherat.datagen;

import net.dingletherat.registry.MoneyItemTags;
import net.dingletherat.MoneyTalks;
import net.dingletherat.registry.MoneyBlocks;
import net.dingletherat.registry.MoneyItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.world.item.crafting.Recipe;
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
    protected RecipeProvider createRecipeProvider(HolderLookup.Provider wrapperLookup, BootstrapContext<Recipe<?>> recipes, BootstrapContext<Advancement> advancements) {
        return new RecipeProvider(recipes, advancements) {
            @Override
            public void buildRecipes() {
            List<CarpetPair> pairs = List.of(
                new CarpetPair(MoneyBlocks.CARPETS.get(DyeColor.BLACK.ordinal()).get(), Items.WOOL.pick(DyeColor.BLACK), Items.DYE.pick(DyeColor.BLACK)),
                new CarpetPair(MoneyBlocks.CARPETS.get(DyeColor.BLUE.ordinal()).get(), Items.WOOL.pick(DyeColor.BLUE), Items.DYE.pick(DyeColor.BLUE)),
                new CarpetPair(MoneyBlocks.CARPETS.get(DyeColor.BROWN.ordinal()).get(), Items.WOOL.pick(DyeColor.BROWN), Items.DYE.pick(DyeColor.BROWN)),
                new CarpetPair(MoneyBlocks.CARPETS.get(DyeColor.CYAN.ordinal()).get(), Items.WOOL.pick(DyeColor.CYAN), Items.DYE.pick(DyeColor.CYAN)),
                new CarpetPair(MoneyBlocks.CARPETS.get(DyeColor.GRAY.ordinal()).get(), Items.WOOL.pick(DyeColor.GRAY), Items.DYE.pick(DyeColor.GRAY)),
                new CarpetPair(MoneyBlocks.CARPETS.get(DyeColor.GREEN.ordinal()).get(), Items.WOOL.pick(DyeColor.GREEN), Items.DYE.pick(DyeColor.GREEN)),
                new CarpetPair(MoneyBlocks.CARPETS.get(DyeColor.LIGHT_BLUE.ordinal()).get(), Items.WOOL.pick(DyeColor.LIGHT_BLUE), Items.DYE.pick(DyeColor.LIGHT_BLUE)),
                new CarpetPair(MoneyBlocks.CARPETS.get(DyeColor.LIGHT_GRAY.ordinal()).get(), Items.WOOL.pick(DyeColor.LIGHT_GRAY), Items.DYE.pick(DyeColor.LIGHT_GRAY)),
                new CarpetPair(MoneyBlocks.CARPETS.get(DyeColor.LIME.ordinal()).get(), Items.WOOL.pick(DyeColor.LIME), Items.DYE.pick(DyeColor.LIME)),
                new CarpetPair(MoneyBlocks.CARPETS.get(DyeColor.MAGENTA.ordinal()).get(), Items.WOOL.pick(DyeColor.MAGENTA), Items.DYE.pick(DyeColor.MAGENTA)),
                new CarpetPair(MoneyBlocks.CARPETS.get(DyeColor.ORANGE.ordinal()).get(), Items.WOOL.pick(DyeColor.ORANGE), Items.DYE.pick(DyeColor.ORANGE)),
                new CarpetPair(MoneyBlocks.CARPETS.get(DyeColor.PINK.ordinal()).get(), Items.WOOL.pick(DyeColor.PINK), Items.DYE.pick(DyeColor.PINK)),
                new CarpetPair(MoneyBlocks.CARPETS.get(DyeColor.PURPLE.ordinal()).get(), Items.WOOL.pick(DyeColor.PURPLE), Items.DYE.pick(DyeColor.PURPLE)),
                new CarpetPair(MoneyBlocks.CARPETS.get(DyeColor.RED.ordinal()).get(), Items.WOOL.pick(DyeColor.RED), Items.DYE.pick(DyeColor.RED)),
                new CarpetPair(MoneyBlocks.CARPETS.get(DyeColor.WHITE.ordinal()).get(), Items.WOOL.pick(DyeColor.WHITE), Items.DYE.pick(DyeColor.WHITE)),
                new CarpetPair(MoneyBlocks.CARPETS.get(DyeColor.YELLOW.ordinal()).get(), Items.WOOL.pick(DyeColor.YELLOW), Items.DYE.pick(DyeColor.YELLOW))
            );

                for (CarpetPair pair : pairs) {
                    shaped(RecipeCategory.MISC, pair.carpet())
                        .pattern("WW")
                        .pattern("WW")
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

                shaped(RecipeCategory.MISC, MoneyItems.WALLET.get())
                    .pattern("L L")
                    .pattern("LLL")
                    .define('L', Items.LEATHER)
                    .unlockedBy("has_leather", has(Items.LEATHER))
                    .save(output);

                shaped(RecipeCategory.MISC, MoneyItems.TRIMMED_HOPPER.get())
                    .pattern("D D")
                    .pattern("DHD")
                    .pattern("D D")
                    .define('D', Items.DIAMOND)
                    .define('H', Blocks.HOPPER)
                    .unlockedBy("has_hopper", has(Items.HOPPER))
                    .save(output);

                shapeless(RecipeCategory.MISC, new ItemStackTemplate(MoneyItems.COIN.get(), 10))
                    .requires(MoneyBlocks.DOUBLOON.get().asItem())
                    .unlockedBy("has_doubloon", has(MoneyBlocks.DOUBLOON.get().asItem()))
                    .save(output);
                shaped(RecipeCategory.MISC, (Block) MoneyBlocks.DOUBLOON_COMPRESSOR.get())
                    .pattern("DDD")
                    .pattern("DND")
                    .pattern("SSS")
                    .define('D', Items.DEEPSLATE)
                    .define('N', Items.NETHERITE_INGOT)
                    .define('S', Blocks.SOUL_SAND)
                    .unlockedBy("has_netherite_ingot", has(Items.NETHERITE_INGOT))
                    .save(output);
            }
        };
    }

    @Override
    public String getName() {
        return MoneyTalks.MOD_ID + " Recipes";
    }
}
