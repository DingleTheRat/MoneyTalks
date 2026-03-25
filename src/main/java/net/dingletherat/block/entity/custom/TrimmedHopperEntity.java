package net.dingletherat.block.entity.custom;

import java.util.List;
import java.util.UUID;

import net.dingletherat.MoneyTalks;
import net.dingletherat.block.custom.TrimmedHopper;
import net.dingletherat.block.entity.MoneyBlockEntities;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.vehicle.minecart.MinecartChest;
import net.minecraft.world.entity.vehicle.minecart.MinecartHopper;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.HopperMenu;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class TrimmedHopperEntity extends BlockEntity implements Hopper, MenuProvider {
    protected UUID owner;
    protected final NonNullList<ItemStack> inventory = NonNullList.ofSize(5, ItemStack.EMPTY);
    protected int transferCooldown = -1;

    public <T extends TrimmedHopperEntity> TrimmedHopperEntity(BlockEntityType<T> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);
    }
    public TrimmedHopperEntity(BlockPos pos, BlockState state) {
        super(MoneyBlockEntities.TRIMMED_HOPPER_ENTITY, pos, state);
    }
    
    public static void tick(Level world, BlockPos pos, BlockState state, TrimmedHopperEntity hopper) {
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

    protected void pullItems(Level world, BlockPos pos, BlockState state) {
        BlockPos above = pos.up();

        // Pull from block inventory above
        BlockEntity above_be = world.getBlockEntity(above);
        if (above_be instanceof Inventory aboveInv) {
           transferTo(aboveInv);
            return;
        }

        // Pull from minecart entities above
        AABB searchBox = AABB.from(Vec3.ofCenter(above)).expand(0.5);

        for (MinecartChest cart : world.getEntitiesByClass(MinecartChest.class, searchBox, e -> true)) {
            if (transferTo(cart)) return;
        }

        for (MinecartHopper cart : world.getEntitiesByClass(MinecartHopper.class, searchBox, e -> true)) {
            if (transferTo(cart)) return;
        }

        // Pull from dropped items above
        AABB itemBox = new Box(above);
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

    protected void pushItems(Level world, BlockPos pos, BlockState state) {
        Direction facing = state.get(TrimmedHopper.FACING);
        BlockPos targetPos = pos.offset(facing);

        // Push to block inventory in facing direction
        BlockEntity target_be = world.getBlockEntity(targetPos);
        if (target_be instanceof Inventory targetInv) {
            transferTo(targetInv);
            return;
        }

        // Push to minecart entities in facing direction
        AABB searchBox = AABB.from(Vec3.ofCenter(targetPos)).expand(0.5);

        for (MinecartChest cart : world.getEntitiesByClass(MinecartChest.class, searchBox, e -> true)) {
            if (transferTo(cart)) return;
        }

        for (MinecartHopper cart : world.getEntitiesByClass(MinecartHopper.class, searchBox, e -> true)) {
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

    private void insertIntoCarpets(Level world) {
        if (owner == null || MoneyTalks.shopState == null) return;
        List<BlockPos> carpets = MoneyTalks.shopState.getShops(owner);
        if (carpets.isEmpty()) return;

        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.get(i);
            if (!stack.isEmpty()) {
                for (BlockPos carpetPos : carpets) {
                    BlockEntity be = world.getBlockEntity(carpetPos);
                    if (be instanceof MerchantCarpetEntity carpet) {
                        InteractionResult result = carpet.hopperInsert(stack, world);
                        if (result == InteractionResult.SUCCESS) {
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
    protected void writeData(ValueOutput view) {
        super.writeData(view);
        view.putString("owner", owner == null ? "" : owner.toString());
        ContainerHelper.writeData(view, inventory);
    }

    @Override
    protected void readData(ValueInput view) {
        super.readData(view);
        String ownerStr = view.getString("owner", "");
        owner = ownerStr.isEmpty() ? null : UUID.fromString(ownerStr);
        ContainerHelper.readData(view, inventory);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.moneytalks.trimmed_hopper");
    }

    @Override
    public HopperMenu createMenu(int syncId, Inventory playerInventory, Player player) {
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
        return ContainerHelper.splitStack(inventory, slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        return ContainerHelper.removeStack(inventory, slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        inventory.set(slot, stack);
        markDirty();
    }


    @Override
    public boolean canPlayerUse(Player player) {
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
