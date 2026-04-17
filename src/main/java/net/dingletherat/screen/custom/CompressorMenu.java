package net.dingletherat.screen.custom;

import net.dingletherat.MoneyTalks;
import net.dingletherat.item.MoneyItems;
import net.dingletherat.screen.MoneyMenus;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class CompressorMenu extends AbstractContainerMenu {
    // Slots
    public static final int WALLET_SLOT = 0;
    public static final int FUEL_SLOT = 1;
    public static final int DOUBLOON_SLOT = 2;

    // Inventory
    public static final int CONTAINER_SLOTS = 3;
    public static final int INVENTORY_START = CONTAINER_SLOTS;
    public static final int INVENTORY_END = INVENTORY_START + 27;
    public static final int HOTBAR_START = INVENTORY_END;
    public static final int HOTBAR_END = HOTBAR_START + 9;

    public Container container;
    public Inventory playerInventory;
    public final ContainerData data;

    public CompressorMenu(int syncId, Inventory playerInventory, Container container, ContainerData data) {
        super(MoneyMenus.COMPRESSOR_MENU, syncId);
        this.container = container;
        this.playerInventory = playerInventory;
        this.data = data;

        // Container slot thingys
        addSlot(new Slot(container, WALLET_SLOT, 56, 17) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(MoneyItems.WALLET);
            }

            @Override
            public Identifier getNoItemIcon() {
                return Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, "wallet");
            }
        });
        addSlot(new Slot(container, FUEL_SLOT, 56, 53) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.is(Items.BLAZE_POWDER);
            }

            @Override
            public Identifier getNoItemIcon() {
                return Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, "blaze_powder");
            }
        });
        addSlot(new Slot(container, DOUBLOON_SLOT, 116, 35) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return false;
            }
        });
        
        // Inventory slot thingys
        addPlayerInventory(playerInventory);
        addPlayerHotbar(playerInventory);
    }
    public CompressorMenu(int syncId, Inventory playerInventory) {
        this(syncId, playerInventory, new SimpleContainer(3), new SimpleContainerData(2));
    }

    @Override
    public boolean stillValid(Player player) {
        return container.stillValid(player);
    }
    @Override
    public ItemStack quickMoveStack(Player player, int slotIndex) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = getSlot(slotIndex);

        // If the slot has nothing, return. If it does, continue and use the item as a result.
        if (!slot.hasItem()) return result;
        result = slot.getItem().copy();

        if (slotIndex < CONTAINER_SLOTS) {
            // COMPRESSOR (scawy) -> inventory
            if (!this.moveItemStackTo(slot.getItem(), INVENTORY_START, HOTBAR_END, true))
                return ItemStack.EMPTY;
        } else {
            // Inventory -> compressor slots
            if (slotIndex < INVENTORY_END) {
                // Inventory row -> try the compressor slots first, then try hotbar slots
                if (!this.moveItemStackTo(slot.getItem(), WALLET_SLOT, CONTAINER_SLOTS, false))
                    if (!this.moveItemStackTo(slot.getItem(), HOTBAR_START, HOTBAR_END, false))
                        return ItemStack.EMPTY;
            } else {
                // Hotbar -> try compressor slots first (again), then inventory slots
                if (!this.moveItemStackTo(slot.getItem(), WALLET_SLOT, CONTAINER_SLOTS, false))
                    if (!this.moveItemStackTo(slot.getItem(), INVENTORY_START, INVENTORY_END, false))
                        return ItemStack.EMPTY;
            }
        }

        if (slot.getItem().isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();

        if (slot.getItem().getCount() == result.getCount()) return ItemStack.EMPTY;
        slot.onTake(player, slot.getItem());

        return result;
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; ++row)
            for (int col = 0; col < 9; ++col)
                this.addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int col = 0; col < 9; ++col)
            this.addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
    }
}
