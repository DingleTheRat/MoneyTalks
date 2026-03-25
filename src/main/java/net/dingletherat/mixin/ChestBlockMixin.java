package net.dingletherat.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.dingletherat.item.custom.DoubleTrimmedHopperItem;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

@Mixin(BlockBehaviour.class)
public class ChestBlockMixin {
    @Inject(method = "useItemOn", at = @At("HEAD"), cancellable = true)
    private void onUseWithItem(ItemStack stack, BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir) {
        if (!(state.getBlock() instanceof ChestBlock)) return;
        if (!stack.isEmpty() && stack.getItem() instanceof DoubleTrimmedHopperItem) {
            if (!world.isClient()) {
                // Create a single linked item
                ItemStack linked = stack.copyWithCount(1);
                CompoundTag nbt = linked.contains(DataComponents.CUSTOM_DATA)
                    ? linked.get(DataComponents.CUSTOM_DATA).copyNbt()
                    : new NbtCompound();
                nbt.putLong("linked", pos.asLong());
                linked.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));

                // Remove one from the original stack
                stack.decrement(1);

                // Give the linked item to the player
                player.getInventory().insertStack(linked);
                world.playSound(null, pos, SoundEvents.BLOCK_CHAIN_PLACE, player.getSoundCategory(), 1.0f, 1.0f);           }
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }
}
