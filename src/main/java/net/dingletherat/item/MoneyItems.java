package net.dingletherat.item;

import java.util.function.Consumer;
import java.util.function.Function;

import net.dingletherat.MoneyTalks;
import net.dingletherat.block.MoneyBlocks;
import net.dingletherat.item.custom.DoubleTrimmedHopperItem;
import net.dingletherat.item.custom.Wallet;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

public class MoneyItems {
    public static final Item DOLLAR = registerItem("dollar", setting -> new Item(setting) {
        @Override
        public void appendTooltip(ItemStack stack, Item.TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
            textConsumer.accept(Text.translatable("Worth a dollar").formatted(Formatting.GOLD));
        }
    });
    public static final Item WALLET = registerItem("wallet", setting -> new Wallet(setting.maxCount(1).component(DataComponentTypes.BUNDLE_CONTENTS, BundleContentsComponent.DEFAULT)));
    public static final Item DOUBLE_TRIMMED_HOPPER = registerItem("double_trimmed_hopper", setting -> new DoubleTrimmedHopperItem(MoneyBlocks.DOUBLE_TRIMMED_HOPPER, setting));

    private static Item registerItem(String name, Function<Item.Settings, Item> function) {
        return Registry.register(Registries.ITEM, Identifier.of(MoneyTalks.MOD_ID, name),
                function.apply(new Item.Settings().registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of(MoneyTalks.MOD_ID, name)))));
    }
    public static void registerModItems() {
        MoneyTalks.LOGGER.info("Registering Mod Items for " + MoneyTalks.MOD_ID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.SEARCH).register(entries -> {
            entries.add(DOLLAR);
        });
    }
	
}
