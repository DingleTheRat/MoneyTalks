package net.dingletherat.item.custom;

import java.util.function.Consumer;

import net.minecraft.world.level.block.Block;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;

public class DoubleTrimmedHopperItem extends BlockItem {
    public DoubleTrimmedHopperItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data != null) return data.copyTag().contains("linked");

        return false;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> textConsumer, TooltipFlag type) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data != null) {
            long linkedPos = data.copyTag().getLong("linked").orElse(Long.MIN_VALUE);

            if (linkedPos != Long.MIN_VALUE) {
                BlockPos pos = BlockPos.of(linkedPos);
                textConsumer.accept(Component.literal("Linked to a chest at " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ()).withStyle(ChatFormatting.GOLD));
            } else
                textConsumer.accept(Component.literal("Not linked").withStyle(ChatFormatting.GRAY));
        } else
            textConsumer.accept(Component.literal("Not linked").withStyle(ChatFormatting.GRAY));
    }
    public int getMaxCount(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data != null && data.copyTag().contains("linked"))
            return 1;

        return 64;
    }
}
