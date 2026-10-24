package net.dingletherat.registry;

import java.util.function.Function;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.dingletherat.MoneyTalks;
import net.dingletherat.item.TrimmedHopperItem;
import net.dingletherat.item.Wallet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.Item;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;

public class MoneyItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(MoneyTalks.MOD_ID, Registries.ITEM);

    public static final RegistrySupplier<Item> COIN = registerItem("coin", properties -> new Item(properties));
    public static final RegistrySupplier<Item> WALLET = registerItem("wallet", properties -> new Wallet(properties.stacksTo(1).component(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY)));
    public static final RegistrySupplier<Item> TRIMMED_HOPPER = registerItem("trimmed_hopper", properties -> new TrimmedHopperItem(MoneyBlocks.TRIMMED_HOPPER.get(), properties));

    public static <T extends Item> RegistrySupplier<T> registerItem(String name, Function<Item.Properties, T> function) {
        return ITEMS.register(name, () -> {
            ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, name));
            return function.apply(new Item.Properties().arch$tab(MoneyTabs.MONEY_TALKS).setId(key));
        });
    }

    public static void initialize() {
        MoneyTalks.LOGGER.info("Initializing Items for " + MoneyTalks.MOD_ID);
        ITEMS.register();
    }
}
