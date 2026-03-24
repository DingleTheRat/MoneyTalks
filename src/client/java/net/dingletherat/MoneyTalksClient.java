package net.dingletherat;

import net.dingletherat.block.entity.MoneyBlockEntities;
import net.dingletherat.block.entity.renderer.MerchantRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;

public class MoneyTalksClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		BlockEntityRendererFactories.register(MoneyBlockEntities.MERCHANT_CARPET_ENTITY, MerchantRenderer::new);
	}
}
