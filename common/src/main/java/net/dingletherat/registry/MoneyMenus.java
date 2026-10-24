package net.dingletherat.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.dingletherat.MoneyTalks;
import net.dingletherat.screen.CompressorMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

public class MoneyMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(MoneyTalks.MOD_ID, Registries.MENU);

    public static final RegistrySupplier<MenuType<CompressorMenu>> COMPRESSOR_MENU = registerMenu("doubloon_compressor", CompressorMenu::new);

    public static <T extends AbstractContainerMenu> RegistrySupplier<MenuType<T>> registerMenu(String name, MenuType.MenuSupplier<T> supplier) {
        return MENUS.register(name, () -> new MenuType<>(supplier, FeatureFlags.VANILLA_SET));
    }

    public static void initialize() {
        MoneyTalks.LOGGER.info("Initializing Menus for " + MoneyTalks.MOD_ID);
        MENUS.register();
    }
}
