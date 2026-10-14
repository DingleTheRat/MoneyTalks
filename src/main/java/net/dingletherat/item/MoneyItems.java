package net.dingletherat.item;

import java.util.function.Function;

import net.dingletherat.MoneyTalks;
import net.dingletherat.block.MoneyBlocks;
import net.dingletherat.item.custom.DoubleTrimmedHopperItem;
import net.dingletherat.item.custom.Wallet;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;

public class MoneyItems {
    public static final Item COIN = registerItem("coin", properties -> new Item(properties));
    public static final Item WALLET = registerItem("wallet", properties -> new Wallet(properties.stacksTo(1).component(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY)));
    public static final Item DOUBLE_TRIMMED_HOPPER = registerItem("double_trimmed_hopper", properties -> new DoubleTrimmedHopperItem((Block) MoneyBlocks.DOUBLE_TRIMMED_HOPPER, properties));

    private static Item registerItem(String name, Function<Item.Properties, Item> function) {
        return Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, name),
                function.apply(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, name)))));
    }
    public static void registerItems() {
        MoneyTalks.LOGGER.info("Registering Items for " + MoneyTalks.MOD_ID);

        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.SEARCH).register(entries -> {
            entries.accept(COIN);
        });
    }
	
}
