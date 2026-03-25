package net.dingletherat.item;

import net.dingletherat.MoneyTalks;
import net.dingletherat.block.MoneyBlocks;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class MoneyGroups {
    public static final ItemGroup MONEY_GROUP = Registry.register(BuiltInRegistries.ITEM_GROUP,
            Identifier.of(MoneyTalks.MOD_ID, "money_group"),
            FabricItemGroup.builder().icon(() -> new ItemStack(MoneyItems.WALLET))
                    .displayName(Component.translatable("itemgroup." + MoneyTalks.MOD_ID + ".money_group"))
                    .entries((displayContext, entries) -> {
                        entries.add(MoneyItems.DOLLAR);
                        entries.add(MoneyItems.WALLET);

                        entries.add(MoneyItems.DOUBLE_TRIMMED_HOPPER);
                        entries.add(MoneyBlocks.TRIMMED_HOPPER);

                        entries.add(MoneyBlocks.BLACK_MERCHANT_CARPET);
                        entries.add(MoneyBlocks.BLUE_MERCHANT_CARPET);
                        entries.add(MoneyBlocks.BROWN_MERCHANT_CARPET);
                        entries.add(MoneyBlocks.CYAN_MERCHANT_CARPET);
                        entries.add(MoneyBlocks.GRAY_MERCHANT_CARPET);
                        entries.add(MoneyBlocks.GREEN_MERCHANT_CARPET);
                        entries.add(MoneyBlocks.LIGHT_BLUE_MERCHANT_CARPET);
                        entries.add(MoneyBlocks.LIGHT_GRAY_MERCHANT_CARPET);
                        entries.add(MoneyBlocks.LIME_MERCHANT_CARPET);
                        entries.add(MoneyBlocks.MAGENTA_MERCHANT_CARPET);
                        entries.add(MoneyBlocks.ORANGE_MERCHANT_CARPET);
                        entries.add(MoneyBlocks.PINK_MERCHANT_CARPET);
                        entries.add(MoneyBlocks.PURPLE_MERCHANT_CARPET);
                        entries.add(MoneyBlocks.RED_MERCHANT_CARPET);
                        entries.add(MoneyBlocks.WHITE_MERCHANT_CARPET);
                        entries.add(MoneyBlocks.YELLOW_MERCHANT_CARPET);
                    }).build());

    public static void registerItemGroups() {
        MoneyTalks.LOGGER.info("Registering Item Groups for MoneyTalks");
    }
}
