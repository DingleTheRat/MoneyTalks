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
    protected final NonNullList<ItemStack> inventory = NonNullList.withSize(5, ItemStack.EMPTY);
    protected int transferCooldown = -1;

    public <T extends TrimmedHopperEntity> TrimmedHopperEntity(BlockEntityType<T> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);
    }

    public TrimmedHopperEntity(BlockPos pos, BlockState state) {
        super(MoneyBlockEntities.TRIMMED_HOPPER_ENTITY, pos, state);
    }

    public static void tick(Level world, BlockPos pos, BlockState state, TrimmedHopperEntity hopper) {
        if (world.isClientSide()) return;
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
        BlockPos above = pos.above();

        BlockEntity above_be = world.getBlockEntity(above);
        if (above_be instanceof Container aboveInv) {
            transferFrom(aboveInv);
            return;
        }

        AABB searchBox = AABB.ofSize(Vec3.atCenterOf(above), 1, 1, 1);

        for (MinecartChest cart : world.getEntitiesOfClass(MinecartChest.class, searchBox, e -> true)) {
            if (transferFrom(cart)) return;
        }

        for (MinecartHopper cart : world.getEntitiesOfClass(MinecartHopper.class, searchBox, e -> true)) {
            if (transferFrom(cart)) return;
        }

        AABB itemBox = AABB.ofSize(Vec3.atCenterOf(above), 1, 1, 1);
        for (ItemEntity itemEntity : world.getEntitiesOfClass(ItemEntity.class, itemBox, e -> !e.isRemoved())) {
            ItemStack stack = itemEntity.getItem();
            if (!stack.isEmpty()) {
                for (int j = 0; j < inventory.size(); j++) {
                    ItemStack slot = inventory.get(j);
                    if (slot.isEmpty()) {
                        inventory.set(j, stack.copy());
                        itemEntity.discard();
                        setChanged();
                        transferCooldown = 8;
                        return;
                    } else if (ItemStack.isSameItemSameComponents(slot, stack)) {
                        int space = slot.getMaxStackSize() - slot.getCount();
                        int transfer = Math.min(space, stack.getCount());
                        if (transfer > 0) {
                            slot.grow(transfer);
                            stack.shrink(transfer);
                            if (stack.isEmpty()) itemEntity.discard();
                            setChanged();
                            transferCooldown = 8;
                            return;
                        }
                    }
                }
            }
        }
    }

    protected void pushItems(Level world, BlockPos pos, BlockState state) {
        Direction facing = state.getValue(TrimmedHopper.FACING);
        BlockPos targetPos = pos.relative(facing);

        BlockEntity target_be = world.getBlockEntity(targetPos);
        if (target_be instanceof Container targetInv) {
            transferTo(targetInv);
            return;
        }

        AABB searchBox = AABB.ofSize(Vec3.atCenterOf(targetPos), 1, 1, 1);

        for (MinecartChest cart : world.getEntitiesOfClass(MinecartChest.class, searchBox, e -> true)) {
            if (transferTo(cart)) return;
        }

        for (MinecartHopper cart : world.getEntitiesOfClass(MinecartHopper.class, searchBox, e -> true)) {
            if (transferTo(cart)) return;
        }
    }

    protected boolean transferFrom(Container from) {
        for (int i = 0; i < from.getContainerSize(); i++) {
            ItemStack stack = from.getItem(i);
            if (!stack.isEmpty()) {
                for (int j = 0; j < inventory.size(); j++) {
                    ItemStack slot = inventory.get(j);
                    if (slot.isEmpty()) {
                        inventory.set(j, stack.copy());
                        from.removeItem(i, stack.getCount());
                        setChanged();
                        transferCooldown = 8;
                        return true;
                    } else if (ItemStack.isSameItemSameComponents(slot, stack)) {
                        int space = slot.getMaxStackSize() - slot.getCount();
                        int transfer = Math.min(space, stack.getCount());
                        if (transfer > 0) {
                            slot.grow(transfer);
                            from.removeItem(i, transfer);
                            setChanged();
                            transferCooldown = 8;
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    protected boolean transferTo(Container to) {
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.get(i);
            if (!stack.isEmpty()) {
                for (int j = 0; j < to.getContainerSize(); j++) {
                    ItemStack slot = to.getItem(j);
                    if (slot.isEmpty()) {
                        to.setItem(j, stack.copy());
                        inventory.set(i, ItemStack.EMPTY);
                        setChanged();
                        transferCooldown = 8;
                        return true;
                    } else if (ItemStack.isSameItemSameComponents(slot, stack)) {
                        int space = slot.getMaxStackSize() - slot.getCount();
                        int transfer = Math.min(space, stack.getCount());
                        if (transfer > 0) {
                            slot.grow(transfer);
                            inventory.set(i, ItemStack.EMPTY);
                            setChanged();
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
                            setChanged();
                            transferCooldown = 8;
                            return;
                        }
                    }
                }
            }
        }
    }

    public UUID getOwner() { return owner; }
    public void setOwner(UUID owner) { this.owner = owner; }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putString("owner", owner == null ? "" : owner.toString());
        ContainerHelper.saveAllItems(output, inventory);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        String ownerStr = input.getString("owner").orElse("");
        owner = ownerStr.isEmpty() ? null : UUID.fromString(ownerStr);
        ContainerHelper.loadAllItems(input, inventory);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.moneytalks.trimmed_hopper");
    }

    @Override
    public HopperMenu createMenu(int syncId, Inventory playerInventory, Player player) {
        return new HopperMenu(syncId, playerInventory, this);
    }

    @Override
    public double getLevelX() { return worldPosition.getX() + 0.5; }

    @Override
    public double getLevelY() { return worldPosition.getY() + 0.5; }

    @Override
    public double getLevelZ() { return worldPosition.getZ() + 0.5; }

    @Override
    public int getContainerSize() { return 5; }

    @Override
    public boolean isEmpty() { return inventory.stream().allMatch(ItemStack::isEmpty); }

    @Override
    public ItemStack getItem(int slot) { return inventory.get(slot); }

    @Override
    public ItemStack removeItem(int slot, int amount) { return ContainerHelper.removeItem(inventory, slot, amount); }

    @Override
    public ItemStack removeItemNoUpdate(int slot) { return ContainerHelper.takeItem(inventory, slot); }

    @Override
    public void setItem(int slot, ItemStack stack) { inventory.set(slot, stack); setChanged(); }

    @Override
    public boolean stillValid(Player player) { return true; }

    @Override
    public void clearContent() { inventory.clear(); }

    @Override
    public boolean isGridAligned() { return true; }
}
