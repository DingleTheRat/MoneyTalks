package net.dingletherat.registry;

import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.dingletherat.MoneyTalks;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;

public class MoneyTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(MoneyTalks.MOD_ID, Registries.CREATIVE_MODE_TAB);

    public static final RegistrySupplier<CreativeModeTab> MONEY_TALKS = TABS.register("money_talks", () ->
        CreativeTabRegistry.create(builder -> {
            builder.title(Component.translatable("tab." + MoneyTalks.MOD_ID + ".money_talks"));
            builder.icon(() -> {
                ItemStack fullWallet = new ItemStack(MoneyItems.WALLET.get());
                fullWallet.set(MoneyDataComponents.COINS.get(), MoneyTalks.TEXTURE_WALLET_FULL_GOAL);
                return fullWallet;
            });
        }));

    public static void initialize() {
        MoneyTalks.LOGGER.info("Initializing Tabs for " + MoneyTalks.MOD_ID);
        TABS.register();
    }
}
