package net.dingletherat.block.entity.custom;

import java.util.List;
import java.util.UUID;

import net.dingletherat.MoneyTalks;
import net.dingletherat.block.custom.TrimmedHopper;
import net.dingletherat.block.entity.MoneyBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.Hopper;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.entity.vehicle.HopperMinecartEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.HopperScreenHandler;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class TrimmedHopperEntity extends BlockEntity implements Hopper, NamedScreenHandlerFactory {
    protected UUID owner;
    protected final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(5, ItemStack.EMPTY);
    protected int transferCooldown = -1;

    public <T extends TrimmedHopperEntity> TrimmedHopperEntity(BlockEntityType<T> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);
    }
    public TrimmedHopperEntity(BlockPos pos, BlockState state) {
        super(MoneyBlockEntities.TRIMMED_HOPPER_ENTITY, pos, state);
    }
    
    public static void tick(World world, BlockPos pos, BlockState state, TrimmedHopperEntity hopper) {
        if (world.isClient()) return;
        if (hopper.transferCooldown > 0) {
            hopper.transferCooldown--;
            return;
        }

        hopper.transferCooldown = 0;
        hopper.pullItems(world, pos, state);
        hopper.insertIntoCarpets(world);
        hopper.pushItems(world, pos, state);
    }

    protected void pullItems(World world, BlockPos pos, BlockState state) {
        BlockPos above = pos.up();

        // Pull from block inventory above
        BlockEntity above_be = world.getBlockEntity(above);
        if (above_be instanceof Inventory aboveInv) {
           transferTo(aboveInv);
            return;
        }

        // Pull from minecart entities above
        Box searchBox = Box.from(Vec3d.ofCenter(above)).expand(0.5);

        for (ChestMinecartEntity cart : world.getEntitiesByClass(ChestMinecartEntity.class, searchBox, e -> true)) {
            if (transferTo(cart)) return;
        }

        for (HopperMinecartEntity cart : world.getEntitiesByClass(HopperMinecartEntity.class, searchBox, e -> true)) {
            if (transferTo(cart)) return;
        }

        // Pull from dropped items above
        Box itemBox = new Box(above);
        for (ItemEntity itemEntity : world.getEntitiesByClass(ItemEntity.class, itemBox, e -> !e.isRemoved())) {
            ItemStack stack = itemEntity.getStack();
            if (!stack.isEmpty()) {
                for (int j = 0; j < inventory.size(); j++) {
                    ItemStack slot = inventory.get(j);
                    if (slot.isEmpty()) {
                        inventory.set(j, stack.copy());
                        itemEntity.discard();
                        markDirty();
                        transferCooldown = 8;
                        return;
                    } else if (ItemStack.areItemsAndComponentsEqual(slot, stack)) {
                        int space = slot.getMaxCount() - slot.getCount();
                        int transfer = Math.min(space, stack.getCount());
                        if (transfer > 0) {
                            slot.increment(transfer);
                            stack.decrement(transfer);
                            if (stack.isEmpty()) itemEntity.discard();
                            markDirty();
                            transferCooldown = 8;
                            return;
                        }
                    }
                }
            }
        }
    }

    protected void pushItems(World world, BlockPos pos, BlockState state) {
        Direction facing = state.get(TrimmedHopper.FACING);
        BlockPos targetPos = pos.offset(facing);

        // Push to block inventory in facing direction
        BlockEntity target_be = world.getBlockEntity(targetPos);
        if (target_be instanceof Inventory targetInv) {
            transferTo(targetInv);
            return;
        }

        // Push to minecart entities in facing direction
        Box searchBox = Box.from(Vec3d.ofCenter(targetPos)).expand(0.5);

        for (ChestMinecartEntity cart : world.getEntitiesByClass(ChestMinecartEntity.class, searchBox, e -> true)) {
            if (transferTo(cart)) return;
        }

        for (HopperMinecartEntity cart : world.getEntitiesByClass(HopperMinecartEntity.class, searchBox, e -> true)) {
            if (transferTo(cart)) return;
        }
    }

    protected boolean transferTo(Inventory to) {
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.get(i);
            if (!stack.isEmpty()) {
                for (int j = 0; j < to.size(); j++) {
                    ItemStack slot = to.getStack(j);
                    if (slot.isEmpty()) {
                        to.setStack(j, stack.copy());
                        inventory.set(i, ItemStack.EMPTY);
                        markDirty();
                        transferCooldown = 8;
                        return true;
                    } else if (ItemStack.areItemsAndComponentsEqual(slot, stack)) {
                        int space = slot.getMaxCount() - slot.getCount();
                        int transfer = Math.min(space, stack.getCount());
                        if (transfer > 0) {
                            slot.increment(transfer);
                            inventory.set(i, ItemStack.EMPTY);
                            markDirty();
                            transferCooldown = 8;
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    private void insertIntoCarpets(World world) {
        if (owner == null || MoneyTalks.shopState == null) return;
        List<BlockPos> carpets = MoneyTalks.shopState.getShops(owner);
        if (carpets.isEmpty()) return;

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.get(i);
            if (!stack.isEmpty()) {
                for (BlockPos carpetPos : carpets) {
                    BlockEntity be = world.getBlockEntity(carpetPos);
                    if (be instanceof MerchantCarpetEntity carpet) {
                        ActionResult result = carpet.hopperInsert(stack, world);
                        if (result == ActionResult.SUCCESS) {
                            inventory.set(i, stack);
                            markDirty();
                            transferCooldown = 8;
                            return;
                        }
                    }
                }
            }
        }
    }

    public UUID getOwner() {
        return owner;
    }
    public void setOwner(UUID owner) {
        this.owner = owner;
    }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        view.putString("owner", owner == null ? "" : owner.toString());
        Inventories.writeData(view, inventory);
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        String ownerStr = view.getString("owner", "");
        owner = ownerStr.isEmpty() ? null : UUID.fromString(ownerStr);
        Inventories.readData(view, inventory);
    }

    @Override
    public Text getDisplayName() {
        return Text.translatable("block.moneytalks.trimmed_hopper");
    }

    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new HopperScreenHandler(syncId, playerInventory, this);
    }

    @Override
    public double getHopperX() {
        return pos.getX() + 0.5;
    }

    @Override
    public double getHopperY() {
        return pos.getY() + 0.5;
    }

    @Override
    public double getHopperZ() {
        return pos.getZ() + 0.5;
    }

    @Override
    public int size() {
        return 5;
    }

    @Override
    public boolean isEmpty() {
        return inventory.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getStack(int slot) {
        return inventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return Inventories.splitStack(inventory, slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return Inventories.removeStack(inventory, slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        inventory.set(slot, stack);
        markDirty();
    }


    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        inventory.clear();
    }

    @Override
    public boolean canBlockFromAbove() {
        return false;
    }
}
