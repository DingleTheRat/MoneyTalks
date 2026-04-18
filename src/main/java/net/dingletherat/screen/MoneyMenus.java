package net.dingletherat.screen;

import net.dingletherat.MoneyTalks;
import net.dingletherat.screen.custom.CompressorMenu;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;

public class MoneyMenus {
    public static final MenuType<CompressorMenu> COMPRESSOR_MENU = Registry.register(BuiltInRegistries.MENU,
            Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, "doubloon_compressor"), new MenuType<>((syncId, inventory) -> new CompressorMenu(syncId, inventory, new SimpleContainer(3), new SimpleContainerData(4)), FeatureFlags.DEFAULT_FLAGS));

    public static void registerMenus() {
        MoneyTalks.LOGGER.info("Registering menus for " + MoneyTalks.MOD_ID);
    }
}
