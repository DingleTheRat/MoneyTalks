package net.dingletherat.neoforge;

import net.dingletherat.MoneyTalks;
import net.dingletherat.client.DevShutdownFix;
import net.dingletherat.client.item.WalletCoinsProperty;
import net.dingletherat.client.renderer.MerchantRenderer;
import net.dingletherat.client.screen.CompressorScreen;
import net.dingletherat.registry.MoneyBlockEntities;
import net.dingletherat.registry.MoneyMenus;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterRangeSelectItemModelPropertyEvent;

@Mod(value = MoneyTalks.MOD_ID, dist = Dist.CLIENT)
public final class MoneyTalksNeoForgeClient {
    public MoneyTalksNeoForgeClient(IEventBus modBus) {
        modBus.addListener(RegisterRangeSelectItemModelPropertyEvent.class, event ->
                event.register(WalletCoinsProperty.ID, WalletCoinsProperty.MAP_CODEC));
        modBus.addListener(RegisterMenuScreensEvent.class, event ->
                event.register(MoneyMenus.COMPRESSOR_MENU.get(), CompressorScreen::new));
        modBus.addListener(EntityRenderersEvent.RegisterRenderers.class, event ->
                event.registerBlockEntityRenderer(MoneyBlockEntities.MERCHANT_CARPET_ENTITY.get(), MerchantRenderer::new));
        DevShutdownFix.register();
    }
}
