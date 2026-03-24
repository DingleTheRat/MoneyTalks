package net.dingletherat.block;

import java.util.function.Function;

import net.dingletherat.MoneyTalks;
import net.dingletherat.block.custom.MerchantCarpet;
import net.dingletherat.block.custom.DoubleTrimmedHopper;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.dingletherat.block.custom.TrimmedHopper;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.Item.Settings;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public class MoneyBlocks {
    public static final Block BLACK_MERCHANT_CARPET = registerBlock("black_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(MapColor.BLACK).pistonBehavior(PistonBehavior.NORMAL).sounds(BlockSoundGroup.WOOL).strength(0f, 50f).noCollision()));
    public static final Block BLUE_MERCHANT_CARPET = registerBlock("blue_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(MapColor.BLUE).pistonBehavior(PistonBehavior.NORMAL).sounds(BlockSoundGroup.WOOL).strength(0f, 50f).noCollision()));
    public static final Block BROWN_MERCHANT_CARPET = registerBlock("brown_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(MapColor.BROWN).pistonBehavior(PistonBehavior.NORMAL).sounds(BlockSoundGroup.WOOL).strength(0f, 50f).noCollision()));
    public static final Block CYAN_MERCHANT_CARPET = registerBlock("cyan_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(MapColor.CYAN).pistonBehavior(PistonBehavior.NORMAL).sounds(BlockSoundGroup.WOOL).strength(0f, 50f).noCollision()));
    public static final Block GRAY_MERCHANT_CARPET = registerBlock("gray_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(MapColor.GRAY).pistonBehavior(PistonBehavior.NORMAL).sounds(BlockSoundGroup.WOOL).strength(0f, 50f).noCollision()));
    public static final Block GREEN_MERCHANT_CARPET = registerBlock("green_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(MapColor.GREEN).pistonBehavior(PistonBehavior.NORMAL).sounds(BlockSoundGroup.WOOL).strength(0f, 50f).noCollision()));
    public static final Block LIGHT_BLUE_MERCHANT_CARPET = registerBlock("light_blue_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(MapColor.LIGHT_BLUE).pistonBehavior(PistonBehavior.NORMAL).sounds(BlockSoundGroup.WOOL).strength(0f, 50f).noCollision()));
    public static final Block LIGHT_GRAY_MERCHANT_CARPET = registerBlock("light_gray_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(MapColor.LIGHT_GRAY).pistonBehavior(PistonBehavior.NORMAL).sounds(BlockSoundGroup.WOOL).strength(0f, 50f).noCollision()));
    public static final Block LIME_MERCHANT_CARPET = registerBlock("lime_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(MapColor.LIME).pistonBehavior(PistonBehavior.NORMAL).sounds(BlockSoundGroup.WOOL).strength(0f, 50f).noCollision()));
    public static final Block MAGENTA_MERCHANT_CARPET = registerBlock("magenta_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(MapColor.MAGENTA).pistonBehavior(PistonBehavior.NORMAL).sounds(BlockSoundGroup.WOOL).strength(0f, 50f).noCollision()));
    public static final Block ORANGE_MERCHANT_CARPET = registerBlock("orange_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(MapColor.ORANGE).pistonBehavior(PistonBehavior.NORMAL).sounds(BlockSoundGroup.WOOL).strength(0f, 50f).noCollision()));
    public static final Block PINK_MERCHANT_CARPET = registerBlock("pink_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(MapColor.PINK).pistonBehavior(PistonBehavior.NORMAL).sounds(BlockSoundGroup.WOOL).strength(0f, 50f).noCollision()));
    public static final Block PURPLE_MERCHANT_CARPET = registerBlock("purple_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(MapColor.PURPLE).pistonBehavior(PistonBehavior.NORMAL).sounds(BlockSoundGroup.WOOL).strength(0f, 50f).noCollision()));
    public static final Block RED_MERCHANT_CARPET = registerBlock("red_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(MapColor.RED).pistonBehavior(PistonBehavior.NORMAL).sounds(BlockSoundGroup.WOOL).strength(0f, 50f).noCollision()));
    public static final Block WHITE_MERCHANT_CARPET = registerBlock("white_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(MapColor.WHITE).pistonBehavior(PistonBehavior.NORMAL).sounds(BlockSoundGroup.WOOL).strength(0f, 50f).noCollision()));
    public static final Block YELLOW_MERCHANT_CARPET = registerBlock("yellow_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(MapColor.YELLOW).pistonBehavior(PistonBehavior.NORMAL).sounds(BlockSoundGroup.WOOL).strength(0f, 50f).noCollision()));

    public static final Block TRIMMED_HOPPER = registerBlock("trimmed_hopper", properties -> new TrimmedHopper(properties.nonOpaque().strength(3.0f).requiresTool().sounds(BlockSoundGroup.METAL).pistonBehavior(PistonBehavior.NORMAL)));
    public static final Block DOUBLE_TRIMMED_HOPPER = registerBlockWithoutBlockItem("double_trimmed_hopper", properties -> new DoubleTrimmedHopper(properties.nonOpaque().strength(3.0f).requiresTool().sounds(BlockSoundGroup.METAL).pistonBehavior(PistonBehavior.NORMAL)));

    private static Block registerBlock(String name, Function<AbstractBlock.Settings, Block> function) {
        Block toRegister = function.apply(AbstractBlock.Settings.create().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(MoneyTalks.MOD_ID, name))));
        registerBlockItem(name, toRegister);
        return Registry.register(Registries.BLOCK, Identifier.of(MoneyTalks.MOD_ID, name), toRegister);
    }
    private static Block registerBlockWithoutBlockItem(String name, Function<AbstractBlock.Settings, Block> function) {
        return Registry.register(Registries.BLOCK, Identifier.of(MoneyTalks.MOD_ID, name),
                function.apply(AbstractBlock.Settings.create().registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(MoneyTalks.MOD_ID, name)))));
    }
    private static void registerBlockItem(String name, Block block) {
        Registry.register(Registries.ITEM, Identifier.of(MoneyTalks.MOD_ID, name),
                new BlockItem(block, new Settings().useBlockPrefixedTranslationKey()
                        .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MoneyTalks.MOD_ID, name)))));
    }

    public static void registerBlocks() {
        MoneyTalks.LOGGER.info("Registering Mod Blocks for " + MoneyTalks.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.COLORED_BLOCKS).register(entries -> {
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
