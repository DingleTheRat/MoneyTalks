package net.dingletherat.mixin;

import net.dingletherat.MoneyTalks;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.world.inventory.CraftingMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractContainerMenu.class)
public class ScreenHandlerMixin {
    @Inject(method = "doClick", at = @At("HEAD"), cancellable = true)
    private void blockDollarIntoContainers(int slotIndex, int button, ContainerInput containerInput, Player player, CallbackInfo ci) {
        AbstractContainerMenu handler = (AbstractContainerMenu)(Object) this;

        // Only restrict when a non-player inventory is open
        if (handler instanceof InventoryMenu) return;
        if (handler instanceof CraftingMenu) return;
        if (slotIndex < 0 || slotIndex >= handler.slots.size()) return;

        Slot targetSlot = handler.slots.get(slotIndex);
        ItemStack stackInSlot = targetSlot.getItem();
        ItemStack cursorStack = handler.getCarried();

        // Block placing dollars from cursor into a container slot
        if (MoneyTalks.nonTransferable.contains(cursorStack.getItem()) && !isPlayerInventorySlot(targetSlot)) {
            ci.cancel();
            return;
        }

        // Block shift-clicking dollars out of player inventory into container
        if (containerInput == ContainerInput.QUICK_MOVE && MoneyTalks.nonTransferable.contains(stackInSlot.getItem()) && isPlayerInventorySlot(targetSlot)) {
            ci.cancel();
        }
    }

    private boolean isPlayerInventorySlot(Slot slot) {
        return slot.container instanceof Inventory;
    }
}
