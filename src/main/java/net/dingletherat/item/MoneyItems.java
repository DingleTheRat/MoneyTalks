package net.dingletherat.item;

import java.util.function.Consumer;

import net.dingletherat.MoneyTalks;
import net.dingletherat.block.MoneyBlocks;
import net.dingletherat.item.custom.DoubleTrimmedHopperItem;
import net.dingletherat.item.custom.Wallet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.TooltipDisplay;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

public class MoneyItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MoneyTalks.MOD_ID);

    public static final DeferredItem<Item> DOLLAR = ITEMS.registerItem("dollar", properties -> new Item(properties) {
        @Override
        public void appendHoverText(ItemStack itemStack, TooltipContext context, TooltipDisplay display, Consumer<Component> builder, TooltipFlag tooltipFlag) {
            builder.accept(Component.translatable("Worth a dollar").withStyle(ChatFormatting.GOLD));
        }
    });
    public static final DeferredItem<Item> WALLET = ITEMS.registerItem("wallet", properties -> new Wallet(properties.stacksTo(1).component(DataComponents.BUNDLE_CONTENTS, BundleContents.EMPTY)));
    public static final DeferredItem<Item> DOUBLE_TRIMMED_HOPPER = ITEMS.registerItem("double_trimmed_hopper", properties -> new DoubleTrimmedHopperItem(MoneyBlocks.DOUBLE_TRIMMED_HOPPER.get(), properties));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
