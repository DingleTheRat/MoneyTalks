package net.dingletherat.item;

import java.util.function.Consumer;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Prediction;
import net.dingletherat.LegacyData;
import net.dingletherat.registry.MoneyDataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;

public class TrimmedHopperItem extends BlockItem {
    public TrimmedHopperItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel world, Entity entity, EquipmentSlot slot) {
        LegacyData.migrate(stack);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return stack.has(MoneyDataComponents.COORDINATES);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay displayComponent, Consumer<Component> textConsumer, TooltipFlag type) {
        if (stack.has(MoneyDataComponents.COORDINATES)) {
            BlockPos position = stack.get(MoneyDataComponents.COORDINATES);
            String positionText = position.getX() + ", " + position.getY() + ", " + position.getZ();
            textConsumer.accept(Component.literal("Linked to a chest at " + positionText).withStyle(ChatFormatting.GOLD));
        } else textConsumer.accept(Component.literal("Not linked to a chest").withStyle(ChatFormatting.GRAY));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos position = context.getClickedPos();
        Player player = context.getPlayer();

        // Place it if in non-linking conditions
        if (player == null || !(level.getBlockEntity(position) instanceof Container)) return super.useOn(context);
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        ItemStack linked = context.getItemInHand().copyWithCount(1);
        linked.set(MoneyDataComponents.COORDINATES, position);
        linked.set(DataComponents.MAX_STACK_SIZE, 1);

        player.setItemInHand(context.getHand(), replaceItem(player, context.getItemInHand(), linked));
        level.playSound(null, position, SoundEvents.CHAIN_PLACE, player.getSoundSource(), 1.0f, 1.0f);
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        // Crouching and using it on air unlinks a linked hopper
        if (!player.isCrouching() || !stack.has(MoneyDataComponents.COORDINATES)) return super.use(level, player, hand);
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        ItemStack unlinked = stack.copyWithCount(1);
        unlinked.remove(MoneyDataComponents.COORDINATES);
        unlinked.remove(DataComponents.MAX_STACK_SIZE);

        level.playSound(null, player.blockPosition(), SoundEvents.CHAIN_PLACE, player.getSoundSource(), 1.0f, 1.0f);
        return InteractionResult.SUCCESS.heldItemTransformedTo(replaceItem(player, stack, unlinked));
    }

    /* Takes one item out of the held stack and gives the replacement (linked/unlinked)
     I do this so it stacks nicely, follows stack rules, and also cuz I want it to only link one item */
    public static ItemStack replaceItem(Player player, ItemStack held, ItemStack replacement) {
        if (held.getCount() <= 1) return replacement;

        if (!player.getInventory().add(replacement)) player.drop(replacement, false, Prediction.PREDICTED);
        return held.copyWithCount(held.getCount() - 1);
    }
}
