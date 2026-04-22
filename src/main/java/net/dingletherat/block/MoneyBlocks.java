package net.dingletherat.block;

import java.util.function.Function;

import net.dingletherat.MoneyTalks;
import net.dingletherat.block.custom.MerchantCarpet;
import net.dingletherat.block.custom.Doubloon;
import net.dingletherat.block.custom.DoubloonCompressor;
import net.dingletherat.block.custom.DoubleTrimmedHopper;
import net.dingletherat.block.custom.TrimmedHopper;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.resources.Identifier;

public class MoneyBlocks {
    public static final BlockBehaviour BLACK_MERCHANT_CARPET = registerBlock("black_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(DyeColor.BLACK).pushReaction(PushReaction.NORMAL).sound(SoundType.WOOL).strength(0f, 50f).noCollision()));
    public static final BlockBehaviour BLUE_MERCHANT_CARPET = registerBlock("blue_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(DyeColor.BLUE).pushReaction(PushReaction.NORMAL).sound(SoundType.WOOL).strength(0f, 50f).noCollision()));
    public static final BlockBehaviour BROWN_MERCHANT_CARPET = registerBlock("brown_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(DyeColor.BROWN).pushReaction(PushReaction.NORMAL).sound(SoundType.WOOL).strength(0f, 50f).noCollision()));
    public static final BlockBehaviour CYAN_MERCHANT_CARPET = registerBlock("cyan_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(DyeColor.CYAN).pushReaction(PushReaction.NORMAL).sound(SoundType.WOOL).strength(0f, 50f).noCollision()));
    public static final BlockBehaviour GRAY_MERCHANT_CARPET = registerBlock("gray_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(DyeColor.GRAY).pushReaction(PushReaction.NORMAL).sound(SoundType.WOOL).strength(0f, 50f).noCollision()));
    public static final BlockBehaviour GREEN_MERCHANT_CARPET = registerBlock("green_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(DyeColor.GREEN).pushReaction(PushReaction.NORMAL).sound(SoundType.WOOL).strength(0f, 50f).noCollision()));
    public static final BlockBehaviour LIGHT_BLUE_MERCHANT_CARPET = registerBlock("light_blue_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(DyeColor.LIGHT_BLUE).pushReaction(PushReaction.NORMAL).sound(SoundType.WOOL).strength(0f, 50f).noCollision()));
    public static final BlockBehaviour LIGHT_GRAY_MERCHANT_CARPET = registerBlock("light_gray_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(DyeColor.LIGHT_GRAY).pushReaction(PushReaction.NORMAL).sound(SoundType.WOOL).strength(0f, 50f).noCollision()));
    public static final BlockBehaviour LIME_MERCHANT_CARPET = registerBlock("lime_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(DyeColor.LIME).pushReaction(PushReaction.NORMAL).sound(SoundType.WOOL).strength(0f, 50f).noCollision()));
    public static final BlockBehaviour MAGENTA_MERCHANT_CARPET = registerBlock("magenta_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(DyeColor.MAGENTA).pushReaction(PushReaction.NORMAL).sound(SoundType.WOOL).strength(0f, 50f).noCollision()));
    public static final BlockBehaviour ORANGE_MERCHANT_CARPET = registerBlock("orange_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(DyeColor.ORANGE).pushReaction(PushReaction.NORMAL).sound(SoundType.WOOL).strength(0f, 50f).noCollision()));
    public static final BlockBehaviour PINK_MERCHANT_CARPET = registerBlock("pink_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(DyeColor.PINK).pushReaction(PushReaction.NORMAL).sound(SoundType.WOOL).strength(0f, 50f).noCollision()));
    public static final BlockBehaviour PURPLE_MERCHANT_CARPET = registerBlock("purple_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(DyeColor.PURPLE).pushReaction(PushReaction.NORMAL).sound(SoundType.WOOL).strength(0f, 50f).noCollision()));
    public static final BlockBehaviour RED_MERCHANT_CARPET = registerBlock("red_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(DyeColor.RED).pushReaction(PushReaction.NORMAL).sound(SoundType.WOOL).strength(0f, 50f).noCollision()));
    public static final BlockBehaviour WHITE_MERCHANT_CARPET = registerBlock("white_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(DyeColor.WHITE).pushReaction(PushReaction.NORMAL).sound(SoundType.WOOL).strength(0f, 50f).noCollision()));
    public static final BlockBehaviour YELLOW_MERCHANT_CARPET = registerBlock("yellow_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(DyeColor.YELLOW).pushReaction(PushReaction.NORMAL).sound(SoundType.WOOL).strength(0f, 50f).noCollision()));

    public static final BlockBehaviour TRIMMED_HOPPER = registerBlock("trimmed_hopper", properties -> new TrimmedHopper(properties.noOcclusion().strength(3.0f).requiresCorrectToolForDrops().sound(SoundType.METAL).pushReaction(PushReaction.NORMAL)));
    public static final BlockBehaviour DOUBLE_TRIMMED_HOPPER = registerBlockWithoutBlockItem("double_trimmed_hopper", properties -> new DoubleTrimmedHopper(properties.noOcclusion().strength(3.0f).requiresCorrectToolForDrops().sound(SoundType.METAL).pushReaction(PushReaction.NORMAL)));

    public static final BlockBehaviour DOUBLOON = registerBlock("doubloon", properties -> new Doubloon(properties.strength(0)));
    public static final BlockBehaviour DOUBLOON_COMPRESSOR = registerBlock("doubloon_compressor", properties -> new DoubloonCompressor(properties.noOcclusion().strength(3.0f, 6.0f).requiresCorrectToolForDrops().sound(SoundType.DEEPSLATE).lightLevel(state -> state.getValue(DoubloonCompressor.FUELED) ? 13 : 0)));

    private static Block registerBlock(String name, Function<BlockBehaviour.Properties, Block> function) {
        Identifier id = Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, name);
        Block toRegister = function.apply(BlockBehaviour.Properties.of()
            .setId(ResourceKey.create(Registries.BLOCK, id)));
        registerBlockItem(name, toRegister);
        return Registry.register(BuiltInRegistries.BLOCK, id, toRegister);
    }

    private static Block registerBlockWithoutBlockItem(String name, Function<BlockBehaviour.Properties, Block> function) {
        Identifier id = Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, name);
        return Registry.register(BuiltInRegistries.BLOCK, id,
            function.apply(BlockBehaviour.Properties.of()
                .setId(ResourceKey.create(Registries.BLOCK, id))));
    }

    private static void registerBlockItem(String name, Block block) {
        Identifier id = Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, name);
        Registry.register(BuiltInRegistries.ITEM, id,
            new BlockItem(block, new Item.Properties()
                .useBlockDescriptionPrefix()
                .setId(ResourceKey.create(Registries.ITEM, id))));
    }

    public static void registerBlocks() {
        MoneyTalks.LOGGER.info("Registering Mod Blocks for " + MoneyTalks.MOD_ID);

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.COLORED_BLOCKS).register(entries -> {
            entries.accept(MoneyBlocks.BLACK_MERCHANT_CARPET.asItem());
            entries.accept(MoneyBlocks.BLUE_MERCHANT_CARPET.asItem());
            entries.accept(MoneyBlocks.BROWN_MERCHANT_CARPET.asItem());
            entries.accept(MoneyBlocks.CYAN_MERCHANT_CARPET.asItem());
            entries.accept(MoneyBlocks.GRAY_MERCHANT_CARPET.asItem());
            entries.accept(MoneyBlocks.GREEN_MERCHANT_CARPET.asItem());
            entries.accept(MoneyBlocks.LIGHT_BLUE_MERCHANT_CARPET.asItem());
            entries.accept(MoneyBlocks.LIGHT_GRAY_MERCHANT_CARPET.asItem());
            entries.accept(MoneyBlocks.LIME_MERCHANT_CARPET.asItem());
            entries.accept(MoneyBlocks.MAGENTA_MERCHANT_CARPET.asItem());
            entries.accept(MoneyBlocks.ORANGE_MERCHANT_CARPET.asItem());
            entries.accept(MoneyBlocks.PINK_MERCHANT_CARPET.asItem());
            entries.accept(MoneyBlocks.PURPLE_MERCHANT_CARPET.asItem());
            entries.accept(MoneyBlocks.RED_MERCHANT_CARPET.asItem());
            entries.accept(MoneyBlocks.WHITE_MERCHANT_CARPET.asItem());
            entries.accept(MoneyBlocks.YELLOW_MERCHANT_CARPET.asItem());
        });
    }
}
