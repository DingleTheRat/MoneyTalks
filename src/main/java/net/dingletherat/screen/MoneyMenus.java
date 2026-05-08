package net.dingletherat.screen;

import net.dingletherat.MoneyTalks;
import net.dingletherat.screen.custom.CompressorMenu;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SimpleContainerData;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.Registries;

public class MoneyMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
        DeferredRegister.create(Registries.MENU, MoneyTalks.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<CompressorMenu>> COMPRESSOR_MENU = MENUS.register("doubloon_compressor", () -> new MenuType<>((syncId, inventory) ->
                new CompressorMenu(syncId, inventory, new SimpleContainer(3), new SimpleContainerData(4)),
                FeatureFlags.DEFAULT_FLAGS));

    public static void register(IEventBus modEventBus) {
        MENUS.register(modEventBus);
    }
}
