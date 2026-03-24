package net.dingletherat.item.custom;

import java.util.function.Consumer;

import net.minecraft.block.Block;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;

public class DoubleTrimmedHopperItem extends BlockItem {
    public DoubleTrimmedHopperItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        NbtComponent data = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (data != null) {
            return data.copyNbt().contains("linked");
        }
        return false;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        NbtComponent data = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (data != null) {
            long linkedPos = data.copyNbt().getLong("linked", Long.MIN_VALUE);

            if (linkedPos != Long.MIN_VALUE) {
                BlockPos pos = BlockPos.fromLong(linkedPos);
                textConsumer.accept(Text.literal("Linked to a chest at " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ()).formatted(Formatting.GOLD));
            } else
                textConsumer.accept(Text.literal("Not linked").formatted(Formatting.GRAY));
        } else
            textConsumer.accept(Text.literal("Not linked").formatted(Formatting.GRAY));
    }
    public int getMaxCount(ItemStack stack) {
        NbtComponent data = stack.get(DataComponentTypes.CUSTOM_DATA);
        if (data != null && data.copyNbt().contains("linked")) {
            return 1;
        }
        return 64;
    }
}
