package net.dingletherat.item;

import net.dingletherat.MoneyTalks;
import net.dingletherat.block.MoneyBlocks;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class MoneyGroups {
    public static final CreativeModeTab MONEY_GROUP = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB,
            Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, "money_group"),
            FabricCreativeModeTab.builder().icon(() -> new ItemStack(MoneyItems.WALLET))
                    .title(Component.translatable("itemgroup." + MoneyTalks.MOD_ID + ".money_group"))
                    .displayItems((displayContext, entries) -> {
                        entries.accept(MoneyItems.DOLLAR);
                        entries.accept(MoneyItems.WALLET);

                        entries.accept(MoneyItems.DOUBLE_TRIMMED_HOPPER);
                        entries.accept(MoneyBlocks.TRIMMED_HOPPER.asItem());

                        entries.accept(MoneyBlocks.BLACK_MERCHANT_CARPET.asItem());
                        entries.accept(MoneyBlocks.BLUE_MERCHANT_CARPET.asItem());
                        entries.accept(MoneyBlocks.BROWN_MERCHANT_CARPET.asItem());
                        entries.accept(MoneyBlocks.CYAN_MERCHANT_CARPET.asItem());
                        entries.accept(MoneyBlocks.GRAY_MERCHANT_CARPET.asItem());
                        entries.accept(MoneyBlocks.GREEN_MERCHANT_CARPET.asItem());
                        entries.accept(MoneyBlocks.LIGHT_BLUE_MERCHANT_CARPET.asItem());
                        entries.accept(MoneyBlocks.LIGHT_GRAY_MERCHANT_CARPET.asItem());
                        entries.accept(MoneyBlocks.LIME_MERCHANT_CARPET.asItem());
                        entries.accept(MoneyBlocks.MAGENTA_MERCHANT_CARPET.asItem());
                        entries.accept(MoneyBlocks.ORANGE_MERCHANT_CARPET.asItem());
                        entries.accept(MoneyBlocks.PINK_MERCHANT_CARPET.asItem());
                        entries.accept(MoneyBlocks.PURPLE_MERCHANT_CARPET.asItem());
                        entries.accept(MoneyBlocks.RED_MERCHANT_CARPET.asItem());
                        entries.accept(MoneyBlocks.WHITE_MERCHANT_CARPET.asItem());
                        entries.accept(MoneyBlocks.YELLOW_MERCHANT_CARPET.asItem());
                    }).build());

    public static void registerItemGroups() {
        MoneyTalks.LOGGER.info("Registering Item Groups for MoneyTalks");
    }
}
