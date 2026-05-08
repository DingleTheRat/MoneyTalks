package net.dingletherat;

import net.dingletherat.block.entity.MoneyBlockEntities;
import net.dingletherat.block.entity.renderer.MerchantRenderer;
import net.dingletherat.screen.MoneyMenus;
import net.dingletherat.screen.custom.CompressorScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@Mod(value = MoneyTalks.MOD_ID, dist = Dist.CLIENT)
public class MoneyTalksClient {
    public MoneyTalksClient(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::onRegisterRenderers);
        modEventBus.addListener(this::onRegisterMenuScreens);
    }

    private void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(MoneyBlockEntities.MERCHANT_CARPET_ENTITY.get(), MerchantRenderer::new);
    }

    private void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(MoneyMenus.COMPRESSOR_MENU.get(), CompressorScreen::new);
    }
}
