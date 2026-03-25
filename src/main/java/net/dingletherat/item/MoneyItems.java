package net.dingletherat.item;

import java.util.function.Consumer;
import java.util.function.Function;

import net.dingletherat.MoneyTalks;
import net.dingletherat.block.MoneyBlocks;
import net.dingletherat.item.custom.DoubleTrimmedHopperItem;
import net.dingletherat.item.custom.Wallet;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.Identifier;

public class MoneyItems {
    public static final Item DOLLAR = registerItem("dollar", setting -> new Item(setting) {
        @Override
        public void appendTooltip(ItemStack stack, Item.TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> textConsumer, TooltipFlag type) {
            textConsumer.accept(Component.translatable("Worth a dollar").formatted(ChatFormatting.GOLD));
        }
    });
    public static final Item WALLET = registerItem("wallet", setting -> new Wallet(setting.maxCount(1).component(DataComponents.BUNDLE_CONTENTS, BundleContents.DEFAULT)));
    public static final Item DOUBLE_TRIMMED_HOPPER = registerItem("double_trimmed_hopper", setting -> new DoubleTrimmedHopperItem(MoneyBlocks.DOUBLE_TRIMMED_HOPPER, setting));

    private static Item registerItem(String name, Function<Item.Settings, Item> function) {
        return Registry.register(BuiltInRegistries.ITEM, Identifier.of(MoneyTalks.MOD_ID, name),
                function.apply(new Item.Settings().registryKey(ResourceKey.of(Registries.ITEM, Identifier.of(MoneyTalks.MOD_ID, name)))));
    }
    public static void registerModItems() {
        MoneyTalks.LOGGER.info("Registering Mod Items for " + MoneyTalks.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.SEARCH).register(entries -> {
            entries.add(DOLLAR);
        });
    }
	
}
