package net.dingletherat;

import net.dingletherat.block.entity.MoneyBlockEntities;
import net.dingletherat.block.entity.renderer.MerchantRenderer;
import net.dingletherat.screen.MoneyMenus;
import net.dingletherat.screen.custom.CompressorScreen;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;

public class MoneyTalksClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		BlockEntityRenderers.register(MoneyBlockEntities.MERCHANT_CARPET_ENTITY, MerchantRenderer::new);
		MenuScreens.register(MoneyMenus.COMPRESSOR_MENU, CompressorScreen::new);
	}
}
