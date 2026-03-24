package net.dingletherat.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.dingletherat.item.custom.DoubleTrimmedHopperItem;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChestBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@Mixin(AbstractBlock.class)
public class ChestBlockMixin {
    @Inject(method = "onUseWithItem", at = @At("HEAD"), cancellable = true)
    private void onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (!(state.getBlock() instanceof ChestBlock)) return;
        if (!stack.isEmpty() && stack.getItem() instanceof DoubleTrimmedHopperItem) {
            if (!world.isClient()) {
                // Create a single linked item
                ItemStack linked = stack.copyWithCount(1);
                NbtCompound nbt = linked.contains(DataComponentTypes.CUSTOM_DATA)
                    ? linked.get(DataComponentTypes.CUSTOM_DATA).copyNbt()
                    : new NbtCompound();
                nbt.putLong("linked", pos.asLong());
                linked.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));

                // Remove one from the original stack
                stack.decrement(1);

                // Give the linked item to the player
                player.getInventory().insertStack(linked);
                world.playSound(null, pos, SoundEvents.BLOCK_CHAIN_PLACE, player.getSoundCategory(), 1.0f, 1.0f);           }
            cir.setReturnValue(ActionResult.SUCCESS);
        }
    }
}
