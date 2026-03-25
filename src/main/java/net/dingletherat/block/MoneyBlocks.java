package net.dingletherat.block;

import java.util.function.Function;

import net.dingletherat.MoneyTalks;
import net.dingletherat.block.custom.MerchantCarpet;
import net.dingletherat.block.custom.DoubleTrimmedHopper;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.dingletherat.block.custom.TrimmedHopper;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.resources.Identifier;

public class MoneyBlocks {
    public static final BlockBehaviour BLACK_MERCHANT_CARPET = registerBlock("black_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(MapColor.BLACK).pistonBehavior(PushReaction.NORMAL).sounds(SoundType.WOOL).strength(0f, 50f).noCollision()));
    public static final BlockBehaviour BLUE_MERCHANT_CARPET = registerBlock("blue_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(MapColor.BLUE).pistonBehavior(PushReaction.NORMAL).sounds(SoundType.WOOL).strength(0f, 50f).noCollision()));
    public static final BlockBehaviour BROWN_MERCHANT_CARPET = registerBlock("brown_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(MapColor.BROWN).pistonBehavior(PushReaction.NORMAL).sounds(SoundType.WOOL).strength(0f, 50f).noCollision()));
    public static final BlockBehaviour CYAN_MERCHANT_CARPET = registerBlock("cyan_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(MapColor.CYAN).pistonBehavior(PushReaction.NORMAL).sounds(SoundType.WOOL).strength(0f, 50f).noCollision()));
    public static final BlockBehaviour GRAY_MERCHANT_CARPET = registerBlock("gray_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(MapColor.GRAY).pistonBehavior(PushReaction.NORMAL).sounds(SoundType.WOOL).strength(0f, 50f).noCollision()));
    public static final BlockBehaviour GREEN_MERCHANT_CARPET = registerBlock("green_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(MapColor.GREEN).pistonBehavior(PushReaction.NORMAL).sounds(SoundType.WOOL).strength(0f, 50f).noCollision()));
    public static final BlockBehaviour LIGHT_BLUE_MERCHANT_CARPET = registerBlock("light_blue_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(MapColor.LIGHT_BLUE).pistonBehavior(PushReaction.NORMAL).sounds(SoundType.WOOL).strength(0f, 50f).noCollision()));
    public static final BlockBehaviour LIGHT_GRAY_MERCHANT_CARPET = registerBlock("light_gray_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(MapColor.LIGHT_GRAY).pistonBehavior(PushReaction.NORMAL).sounds(SoundType.WOOL).strength(0f, 50f).noCollision()));
    public static final BlockBehaviour LIME_MERCHANT_CARPET = registerBlock("lime_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(MapColor.LIME).pistonBehavior(PushReaction.NORMAL).sounds(SoundType.WOOL).strength(0f, 50f).noCollision()));
    public static final BlockBehaviour MAGENTA_MERCHANT_CARPET = registerBlock("magenta_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(MapColor.MAGENTA).pistonBehavior(PushReaction.NORMAL).sounds(SoundType.WOOL).strength(0f, 50f).noCollision()));
    public static final BlockBehaviour ORANGE_MERCHANT_CARPET = registerBlock("orange_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(MapColor.ORANGE).pistonBehavior(PushReaction.NORMAL).sounds(SoundType.WOOL).strength(0f, 50f).noCollision()));
    public static final BlockBehaviour PINK_MERCHANT_CARPET = registerBlock("pink_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(MapColor.PINK).pistonBehavior(PushReaction.NORMAL).sounds(SoundType.WOOL).strength(0f, 50f).noCollision()));
    public static final BlockBehaviour PURPLE_MERCHANT_CARPET = registerBlock("purple_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(MapColor.PURPLE).pistonBehavior(PushReaction.NORMAL).sounds(SoundType.WOOL).strength(0f, 50f).noCollision()));
    public static final BlockBehaviour RED_MERCHANT_CARPET = registerBlock("red_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(MapColor.RED).pistonBehavior(PushReaction.NORMAL).sounds(SoundType.WOOL).strength(0f, 50f).noCollision()));
    public static final BlockBehaviour WHITE_MERCHANT_CARPET = registerBlock("white_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(MapColor.WHITE).pistonBehavior(PushReaction.NORMAL).sounds(SoundType.WOOL).strength(0f, 50f).noCollision()));
    public static final BlockBehaviour YELLOW_MERCHANT_CARPET = registerBlock("yellow_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(MapColor.YELLOW).pistonBehavior(PushReaction.NORMAL).sounds(SoundType.WOOL).strength(0f, 50f).noCollision()));

    public static final BlockBehaviour TRIMMED_HOPPER = registerBlock("trimmed_hopper", properties -> new TrimmedHopper(properties.nonOpaque().strength(3.0f).requiresTool().sounds(SoundType.METAL).pistonBehavior(PushReaction.NORMAL)));
    public static final BlockBehaviour DOUBLE_TRIMMED_HOPPER = registerBlockWithoutBlockItem("double_trimmed_hopper", properties -> new DoubleTrimmedHopper(properties.nonOpaque().strength(3.0f).requiresTool().sounds(SoundType.METAL).pistonBehavior(PushReaction.NORMAL)));

    private static BlockBehaviour registerBlock(String name, Function<BlockBehaviour.Properties, BlockBehaviour> function) {
        BlockBehaviour toRegister = function.apply(BlockBehaviour.Properties.create().registryKey(ResourceKey.of(Registries.BLOCK, Identifier.of(MoneyTalks.MOD_ID, name))));
        registerBlockItem(name, toRegister);
        return Registry.register(BuiltInRegistries.BLOCK, Identifier.of(MoneyTalks.MOD_ID, name), toRegister);
    }
    private static BlockBehaviour registerBlockWithoutBlockItem(String name, Function<BlockBehaviour.Properties, BlockBehaviour> function) {
        return Registry.register(BuiltInRegistries.BLOCK, Identifier.of(MoneyTalks.MOD_ID, name),
                function.apply(BlockBehaviour.Properties.create().registryKey(ResourceKey.of(Registries.BLOCK, Identifier.of(MoneyTalks.MOD_ID, name)))));
    }
    private static void registerBlockItem(String name, BlockBehaviour block) {
        Registry.register(BuiltInRegistries.ITEM, Identifier.of(MoneyTalks.MOD_ID, name),
                new BlockItem(block, new Settings().useBlockPrefixedTranslationKey()
                        .registryKey(ResourceKey.of(Registries.ITEM, Identifier.of(MoneyTalks.MOD_ID, name)))));
    }

    public static void registerBlocks() {
        MoneyTalks.LOGGER.info("Registering Mod Blocks for " + MoneyTalks.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.COLORED_BLOCKS).register(entries -> {
            entries.add(MoneyBlocks.BLACK_MERCHANT_CARPET);
            entries.add(MoneyBlocks.BLUE_MERCHANT_CARPET);
            entries.add(MoneyBlocks.BROWN_MERCHANT_CARPET);
            entries.add(MoneyBlocks.CYAN_MERCHANT_CARPET);
            entries.add(MoneyBlocks.GRAY_MERCHANT_CARPET);
            entries.add(MoneyBlocks.GREEN_MERCHANT_CARPET);
            entries.add(MoneyBlocks.LIGHT_BLUE_MERCHANT_CARPET);
            entries.add(MoneyBlocks.LIGHT_GRAY_MERCHANT_CARPET);
            entries.add(MoneyBlocks.LIME_MERCHANT_CARPET);
            entries.add(MoneyBlocks.MAGENTA_MERCHANT_CARPET);
            entries.add(MoneyBlocks.ORANGE_MERCHANT_CARPET);
            entries.add(MoneyBlocks.PINK_MERCHANT_CARPET);
            entries.add(MoneyBlocks.PURPLE_MERCHANT_CARPET);
            entries.add(MoneyBlocks.RED_MERCHANT_CARPET);
            entries.add(MoneyBlocks.WHITE_MERCHANT_CARPET);
            entries.add(MoneyBlocks.YELLOW_MERCHANT_CARPET);
        });
    }
}
