package net.dingletherat.mixin;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.dingletherat.MoneyTalks;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(HopperBlockEntity.class)
public class HopperBlockEntityMixin {

    /**
     * Prevents hoppers from pulling/pushing a specific item.
     * This injects into the static transfer method that handles all
     * hopper item movement (both entity pickup and container transfer).
     */
    @Inject(
        method = "addItem(Lnet/minecraft/world/Container;Lnet/minecraft/world/Container;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/core/Direction;)Lnet/minecraft/world/item/ItemStack;",
        at = @At("HEAD"),
        cancellable = true
    )
    private static void preventSpecificItemPickup(
        Container to,
        Container from,
        ItemStack stack,
        Direction direction,
        CallbackInfoReturnable<ItemStack> cir
    ) {
        if (MoneyTalks.nonTransferable.contains(stack.getItem())) {
            cir.setReturnValue(stack);
        }
    }
}
