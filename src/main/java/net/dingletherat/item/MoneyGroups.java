package net.dingletherat.item;

import net.dingletherat.MoneyTalks;
import net.dingletherat.block.MoneyBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MoneyGroups {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MoneyTalks.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MONEY_GROUP = TABS.register("money_group",
            () -> CreativeModeTab.builder()
                    .icon(() -> new ItemStack(MoneyItems.WALLET.get()))
                    .title(Component.translatable("itemgroup." + MoneyTalks.MOD_ID + ".money_group"))
                    .displayItems((displayContext, entries) -> {
                        entries.accept(MoneyItems.DOLLAR);
                        entries.accept(MoneyItems.WALLET);
                        entries.accept(MoneyBlocks.BLACK_MERCHANT_CARPET.get().asItem());
                        entries.accept(MoneyBlocks.BLUE_MERCHANT_CARPET.get().asItem());
                        entries.accept(MoneyBlocks.BROWN_MERCHANT_CARPET.get().asItem());
                        entries.accept(MoneyBlocks.CYAN_MERCHANT_CARPET.get().asItem());
                        entries.accept(MoneyBlocks.GRAY_MERCHANT_CARPET.get().asItem());
                        entries.accept(MoneyBlocks.GREEN_MERCHANT_CARPET.get().asItem());
                        entries.accept(MoneyBlocks.LIGHT_BLUE_MERCHANT_CARPET.get().asItem());
                        entries.accept(MoneyBlocks.LIGHT_GRAY_MERCHANT_CARPET.get().asItem());
                        entries.accept(MoneyBlocks.LIME_MERCHANT_CARPET.get().asItem());
                        entries.accept(MoneyBlocks.MAGENTA_MERCHANT_CARPET.get().asItem());
                        entries.accept(MoneyBlocks.ORANGE_MERCHANT_CARPET.get().asItem());
                        entries.accept(MoneyBlocks.PINK_MERCHANT_CARPET.get().asItem());
                        entries.accept(MoneyBlocks.PURPLE_MERCHANT_CARPET.get().asItem());
                        entries.accept(MoneyBlocks.RED_MERCHANT_CARPET.get().asItem());
                        entries.accept(MoneyBlocks.WHITE_MERCHANT_CARPET.get().asItem());
                        entries.accept(MoneyBlocks.YELLOW_MERCHANT_CARPET.get().asItem());
                        entries.accept(MoneyItems.DOUBLE_TRIMMED_HOPPER);
                        entries.accept(MoneyBlocks.TRIMMED_HOPPER.get().asItem());
                        entries.accept(MoneyBlocks.DOUBLOON.get().asItem());
                        entries.accept(MoneyBlocks.DOUBLOON_COMPRESSOR.get().asItem());
                    }).build());


    @SubscribeEvent
    public static void onBuildCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (!event.getTabKey().equals(CreativeModeTabs.COLORED_BLOCKS)) return;
        event.accept(MoneyBlocks.BLACK_MERCHANT_CARPET.asItem());
        event.accept(MoneyBlocks.BLUE_MERCHANT_CARPET.asItem());
        event.accept(MoneyBlocks.BROWN_MERCHANT_CARPET.asItem());
        event.accept(MoneyBlocks.CYAN_MERCHANT_CARPET.asItem());
        event.accept(MoneyBlocks.GRAY_MERCHANT_CARPET.asItem());
        event.accept(MoneyBlocks.GREEN_MERCHANT_CARPET.asItem());
        event.accept(MoneyBlocks.LIGHT_BLUE_MERCHANT_CARPET.asItem());
        event.accept(MoneyBlocks.LIGHT_GRAY_MERCHANT_CARPET.asItem());
        event.accept(MoneyBlocks.LIME_MERCHANT_CARPET.asItem());
        event.accept(MoneyBlocks.MAGENTA_MERCHANT_CARPET.asItem());
        event.accept(MoneyBlocks.ORANGE_MERCHANT_CARPET.asItem());
        event.accept(MoneyBlocks.PINK_MERCHANT_CARPET.asItem());
        event.accept(MoneyBlocks.PURPLE_MERCHANT_CARPET.asItem());
        event.accept(MoneyBlocks.RED_MERCHANT_CARPET.asItem());
        event.accept(MoneyBlocks.WHITE_MERCHANT_CARPET.asItem());
        event.accept(MoneyBlocks.YELLOW_MERCHANT_CARPET.asItem());
    }

    public static void registerItemGroups(IEventBus modEventBus) {
        TABS.register(modEventBus);
    }
}
