package net.dingletherat.client;

import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;
import net.dingletherat.client.item.WalletCoinsProperty;
import net.dingletherat.client.renderer.MerchantRenderer;
import net.dingletherat.client.screen.CompressorScreen;
import net.dingletherat.registry.MoneyBlockEntities;
import net.dingletherat.registry.MoneyMenus;
import net.minecraft.client.gui.screens.MenuScreens;

public class MoneyTalksClient {
    public static void initialize() {
        BlockEntityRendererRegistry.register(MoneyBlockEntities.MERCHANT_CARPET_ENTITY.get(), MerchantRenderer::new);
        MenuScreens.register(MoneyMenus.COMPRESSOR_MENU.get(), CompressorScreen::new);
        WalletCoinsProperty.register();
        DevShutdownFix.register();
    }
}
