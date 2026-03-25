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
    public DoubleTrimmedHopperItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data != null) {
            return data.copyNbt().contains("linked");
        }
        return false;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> textConsumer, TooltipFlag type) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data != null) {
            long linkedPos = data.copyNbt().getLong("linked", Long.MIN_VALUE);

            if (linkedPos != Long.MIN_VALUE) {
                BlockPos pos = BlockPos.fromLong(linkedPos);
                textConsumer.accept(Component.literal("Linked to a chest at " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ()).formatted(ChatFormatting.GOLD));
            } else
                textConsumer.accept(Component.literal("Not linked").formatted(ChatFormatting.GRAY));
        } else
            textConsumer.accept(Component.literal("Not linked").formatted(ChatFormatting.GRAY));
    }
    public int getMaxCount(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data != null && data.copyNbt().contains("linked")) {
            return 1;
        }
        return 64;
    }
}
