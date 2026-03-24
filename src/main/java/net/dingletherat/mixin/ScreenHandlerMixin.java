package net.dingletherat.mixin;

import net.dingletherat.item.MoneyItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScreenHandler.class)
public class ScreenHandlerMixin {

    @Inject(method = "internalOnSlotClick", at = @At("HEAD"), cancellable = true)
    private void blockDollarIntoContainers(int slotIndex, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        ScreenHandler handler = (ScreenHandler) (Object) this;

        // Only restrict when a non-player inventory is open
        if (handler instanceof PlayerScreenHandler) return;

        if (slotIndex < 0 || slotIndex >= handler.slots.size()) return;

        Slot targetSlot = handler.slots.get(slotIndex);
        ItemStack stackInSlot = targetSlot.getStack();
        ItemStack cursorStack = handler.getCursorStack();

        // Block placing dollars from cursor into a container slot
        if (cursorStack.isOf(MoneyItems.DOLLAR) && !isPlayerInventorySlot(targetSlot)) {
            ci.cancel();
            return;
        }

        // Block shift-clicking dollars out of player inventory into container
        if (actionType == SlotActionType.QUICK_MOVE && stackInSlot.isOf(MoneyItems.DOLLAR) && isPlayerInventorySlot(targetSlot))
            ci.cancel();
    }

    private boolean isPlayerInventorySlot(Slot slot) {
        return slot.inventory instanceof net.minecraft.entity.player.PlayerInventory;
    }
}
