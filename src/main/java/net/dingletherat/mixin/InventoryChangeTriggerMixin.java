package net.dingletherat.mixin;

import net.dingletherat.MoneyTalks;
import net.dingletherat.block.MoneyBlocks;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InventoryChangeTrigger.class)
public class InventoryChangeTriggerMixin {

    @Inject(method = "trigger(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/world/entity/player/Inventory;Lnet/minecraft/world/item/ItemStack;)V", at = @At("HEAD"))
    private void checkDoubloonCount(ServerPlayer player, Inventory inventory, ItemStack stack, CallbackInfo callbackInfo) {
        if (!stack.is(MoneyBlocks.DOUBLOON.asItem())) return;

        int goal = MoneyTalks.ADVANCEMENT_RICHEST_PIRATE_GOAL;
        if (inventory.countItem(MoneyBlocks.DOUBLOON.asItem()) >= goal) MoneyTalks.grantAdvancement(player, "richest_pirate", "has_" + goal + "_doubloons");
    }
}
