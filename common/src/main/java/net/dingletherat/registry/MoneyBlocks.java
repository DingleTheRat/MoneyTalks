package net.dingletherat.registry;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.DyeColor;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.dingletherat.MoneyTalks;
import net.dingletherat.block.Doubloon;
import net.dingletherat.block.DoubloonCompressor;
import net.dingletherat.block.MerchantCarpet;
import net.dingletherat.block.TrimmedHopper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.resources.Identifier;

public class MoneyBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(MoneyTalks.MOD_ID, Registries.BLOCK);

    public static final List<RegistrySupplier<Block>> CARPETS = registerColorfulBlock("merchant_carpet", properties -> new MerchantCarpet(properties.sound(SoundType.WOOL).strength(0f, 50f).noCollision()));

    public static final RegistrySupplier<Block> TRIMMED_HOPPER = registerBlockNoItem("trimmed_hopper", properties -> new TrimmedHopper(properties.noOcclusion().strength(3.0f).requiresCorrectToolForDrops().sound(SoundType.METAL)));

    public static final RegistrySupplier<Block> DOUBLOON = registerBlock("doubloon", properties -> new Doubloon(properties.strength(0)));
    public static final RegistrySupplier<Block> DOUBLOON_COMPRESSOR = registerBlock("doubloon_compressor", properties -> new DoubloonCompressor(properties.noOcclusion().strength(3.0f, 6.0f).requiresCorrectToolForDrops().sound(SoundType.DEEPSLATE).lightLevel(state -> state.getValue(DoubloonCompressor.FUELED) ? 13 : 0)));

    public static <T extends Block> RegistrySupplier<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> function) {
        RegistrySupplier<T> block = registerBlockNoItem(name, function);
        registerBlockItem(name, block);
        return block;
    }

    public static <T extends Block> RegistrySupplier<T> registerBlockNoItem(String name, Function<BlockBehaviour.Properties, T> function) {
        return BLOCKS.register(name, () -> {
            ResourceKey<Block> key = ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, name));
            return function.apply(BlockBehaviour.Properties.of().setId(key));
        });
    }

    public static RegistrySupplier<BlockItem> registerBlockItem(String name, Supplier<? extends Block> block) {
        return MoneyItems.registerItem(name, properties -> new BlockItem(block.get(), properties.useBlockDescriptionPrefix()));
    }

    public static <T extends Block> List<RegistrySupplier<T>> registerColorfulBlock(String name, Function<BlockBehaviour.Properties, T> function) {
        List<RegistrySupplier<T>> colorfulBlocks = new ArrayList<>();
        for (DyeColor color : DyeColor.values())
            colorfulBlocks.add(registerBlock(color.getSerializedName() + "_" + name, properties -> function.apply(properties.mapColor(color))));
        return colorfulBlocks;
    }

    public static void initialize() {
        MoneyTalks.LOGGER.info("Initializing Blocks for " + MoneyTalks.MOD_ID);
        BLOCKS.register();
        CreativeTabRegistry.append(CreativeModeTabs.COLORED_BLOCKS, CARPETS.toArray(new RegistrySupplier[0]));
    }
}
