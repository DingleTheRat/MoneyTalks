package net.dingletherat.block;

import net.minecraft.world.level.material.PushReaction;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.core.registries.BuiltInRegistries;

import java.util.function.Function;

import net.dingletherat.MoneyTalks;
import net.dingletherat.block.custom.DoubleTrimmedHopper;
import net.dingletherat.block.custom.Doubloon;
import net.dingletherat.block.custom.DoubloonCompressor;
import net.dingletherat.block.custom.MerchantCarpet;
import net.dingletherat.block.custom.TrimmedHopper;
import net.dingletherat.item.MoneyItems;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.resources.Identifier;

public class MoneyBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MoneyTalks.MOD_ID);

    public static final DeferredBlock<Block> BLACK_MERCHANT_CARPET = registerBlock("black_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(DyeColor.BLACK).pushReaction(PushReaction.NORMAL).sound(SoundType.WOOL).strength(0f, 50f).noCollision()));
    public static final DeferredBlock<Block> BLUE_MERCHANT_CARPET = registerBlock("blue_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(DyeColor.BLUE).pushReaction(PushReaction.NORMAL).sound(SoundType.WOOL).strength(0f, 50f).noCollision()));
    public static final DeferredBlock<Block> BROWN_MERCHANT_CARPET = registerBlock("brown_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(DyeColor.BROWN).pushReaction(PushReaction.NORMAL).sound(SoundType.WOOL).strength(0f, 50f).noCollision()));
    public static final DeferredBlock<Block> CYAN_MERCHANT_CARPET = registerBlock("cyan_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(DyeColor.CYAN).pushReaction(PushReaction.NORMAL).sound(SoundType.WOOL).strength(0f, 50f).noCollision()));
    public static final DeferredBlock<Block> GRAY_MERCHANT_CARPET = registerBlock("gray_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(DyeColor.GRAY).pushReaction(PushReaction.NORMAL).sound(SoundType.WOOL).strength(0f, 50f).noCollision()));
    public static final DeferredBlock<Block> GREEN_MERCHANT_CARPET = registerBlock("green_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(DyeColor.GREEN).pushReaction(PushReaction.NORMAL).sound(SoundType.WOOL).strength(0f, 50f).noCollision()));
    public static final DeferredBlock<Block> LIGHT_BLUE_MERCHANT_CARPET = registerBlock("light_blue_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(DyeColor.LIGHT_BLUE).pushReaction(PushReaction.NORMAL).sound(SoundType.WOOL).strength(0f, 50f).noCollision()));
    public static final DeferredBlock<Block> LIGHT_GRAY_MERCHANT_CARPET = registerBlock("light_gray_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(DyeColor.LIGHT_GRAY).pushReaction(PushReaction.NORMAL).sound(SoundType.WOOL).strength(0f, 50f).noCollision()));
    public static final DeferredBlock<Block> LIME_MERCHANT_CARPET = registerBlock("lime_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(DyeColor.LIME).pushReaction(PushReaction.NORMAL).sound(SoundType.WOOL).strength(0f, 50f).noCollision()));
    public static final DeferredBlock<Block> MAGENTA_MERCHANT_CARPET = registerBlock("magenta_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(DyeColor.MAGENTA).pushReaction(PushReaction.NORMAL).sound(SoundType.WOOL).strength(0f, 50f).noCollision()));
    public static final DeferredBlock<Block> ORANGE_MERCHANT_CARPET = registerBlock("orange_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(DyeColor.ORANGE).pushReaction(PushReaction.NORMAL).sound(SoundType.WOOL).strength(0f, 50f).noCollision()));
    public static final DeferredBlock<Block> PINK_MERCHANT_CARPET = registerBlock("pink_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(DyeColor.PINK).pushReaction(PushReaction.NORMAL).sound(SoundType.WOOL).strength(0f, 50f).noCollision()));
    public static final DeferredBlock<Block> PURPLE_MERCHANT_CARPET = registerBlock("purple_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(DyeColor.PURPLE).pushReaction(PushReaction.NORMAL).sound(SoundType.WOOL).strength(0f, 50f).noCollision()));
    public static final DeferredBlock<Block> RED_MERCHANT_CARPET = registerBlock("red_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(DyeColor.RED).pushReaction(PushReaction.NORMAL).sound(SoundType.WOOL).strength(0f, 50f).noCollision()));
    public static final DeferredBlock<Block> WHITE_MERCHANT_CARPET = registerBlock("white_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(DyeColor.WHITE).pushReaction(PushReaction.NORMAL).sound(SoundType.WOOL).strength(0f, 50f).noCollision()));
    public static final DeferredBlock<Block> YELLOW_MERCHANT_CARPET = registerBlock("yellow_merchant_carpet", properties -> new MerchantCarpet(properties.mapColor(DyeColor.YELLOW).pushReaction(PushReaction.NORMAL).sound(SoundType.WOOL).strength(0f, 50f).noCollision()));

    public static final DeferredBlock<Block> TRIMMED_HOPPER = registerBlock("trimmed_hopper", properties -> new TrimmedHopper(properties.noOcclusion().strength(3.0f).requiresCorrectToolForDrops().sound(SoundType.METAL).pushReaction(PushReaction.NORMAL)));
    public static final DeferredBlock<Block> DOUBLE_TRIMMED_HOPPER = registerBlockWithoutBlockItem("double_trimmed_hopper", properties -> new DoubleTrimmedHopper(properties.noOcclusion().strength(3.0f).requiresCorrectToolForDrops().sound(SoundType.METAL).pushReaction(PushReaction.NORMAL)));

    public static final DeferredBlock<Block> DOUBLOON = registerBlock("doubloon", properties -> new Doubloon(properties.strength(0)));
    public static final DeferredBlock<Block> DOUBLOON_COMPRESSOR = registerBlock("doubloon_compressor", properties -> new DoubloonCompressor(properties.noOcclusion().strength(3.0f, 6.0f).requiresCorrectToolForDrops().sound(SoundType.DEEPSLATE).lightLevel(state -> state.getValue(DoubloonCompressor.FUELED) ? 13 : 0)));

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> function) {
        DeferredBlock<T> toReturn = BLOCKS.registerBlock(name, function);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> DeferredBlock<T> registerBlockWithoutBlockItem(String name, Function<BlockBehaviour.Properties, T> function) {
        DeferredBlock<T> toReturn = BLOCKS.registerBlock(name, function);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        MoneyItems.ITEMS.registerItem(name, properties -> new BlockItem(block.get(), properties.useBlockDescriptionPrefix()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
