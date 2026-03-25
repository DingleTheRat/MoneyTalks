package net.dingletherat.mixin;

import net.dingletherat.item.MoneyItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.ClickType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryMenu.class)
public class ScreenHandlerMixin {

    @Inject(method = "internalOnSlotClick", at = @At("HEAD"), cancellable = true)
    private void blockDollarIntoContainers(int slotIndex, int button, ClickType actionType, Player player, CallbackInfo ci) {
        InventoryMenu handler = (InventoryMenu) (Object) this;

        // Only restrict when a non-player inventory is open
        if (handler instanceof InventoryMenu) return;

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
        if (actionType == ClickType.QUICK_MOVE && stackInSlot.isOf(MoneyItems.DOLLAR) && isPlayerInventorySlot(targetSlot))
            ci.cancel();
    }

    private boolean isPlayerInventorySlot(Slot slot) {
        return slot.inventory instanceof net.minecraft.entity.player.PlayerInventory;
    }
}
