package net.dingletherat.block.entity.custom;

import java.util.UUID;

import net.dingletherat.block.custom.TrimmedHopper;
import net.dingletherat.block.entity.MoneyBlockEntities;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.Hopper;
import net.minecraft.world.level.block.entity.HopperBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.HopperMenu;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public class TrimmedHopperEntity extends BlockEntity implements Hopper, MenuProvider {
    public static final String DATA_OWNER = "owner";
    public static final String DATA_LINKED = "linked";
    public static final int COOLDOWN = 10;

    protected UUID owner;
    private BlockPos linked = null;
    protected final NonNullList<ItemStack> inventory = NonNullList.withSize(HopperBlockEntity.HOPPER_CONTAINER_SIZE, ItemStack.EMPTY);
    protected int transferCooldown = COOLDOWN;

    public TrimmedHopperEntity(BlockPos position, BlockState state) {
        super(MoneyBlockEntities.TRIMMED_HOPPER_ENTITY, position, state);
    }

    public static void tick(Level world, BlockPos position, BlockState state, TrimmedHopperEntity hopper) {
        if (world.isClientSide()) return;
        if (hopper.transferCooldown > 0) {
            hopper.transferCooldown--;
            return;
        }

        hopper.transferCooldown = COOLDOWN;
        if (!state.getValue(TrimmedHopper.ENABLED)) return;

        boolean moved = hopper.pushItems(world, state);
        if (!hopper.isFull()) moved |= HopperBlockEntity.suckInItems(world, hopper);

        if (moved) {
            hopper.transferCooldown = HopperBlockEntity.MOVE_ITEM_SPEED;
            hopper.setChanged();
        }
    }

    protected boolean pushItems(Level world, BlockState state) {
        if (linked == null || !world.isLoaded(linked)) return false;

        Container target = HopperBlockEntity.getContainerAt(world, linked);
        if (target == null) {
            linked = null;
            setChanged();
            return false;
        }

        Direction direction = state.getValue(TrimmedHopper.FACING).getOpposite();
        for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = getItem(i);
            if (stack.isEmpty()) continue;

            int count = stack.getCount();
            ItemStack remainder = HopperBlockEntity.addItem(this, target, removeItem(i, 1), direction);
            if (remainder.isEmpty()) {
                target.setChanged();
                return true;
            }

            stack.setCount(count);
            setItem(i, stack);
        }
        return false;
    }

    private boolean isFull() {
        return inventory.stream().allMatch(stack -> !stack.isEmpty() && stack.getCount() >= stack.getMaxStackSize());
    }

    public UUID getOwner() { return owner; }
    public void setOwner(UUID owner) { this.owner = owner; }

    public void setLinkedChest(BlockPos position) {
        linked = position;
        setChanged();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putString(DATA_OWNER, owner == null ? "" : owner.toString());
        ContainerHelper.saveAllItems(output, inventory);
        output.putLong(DATA_LINKED, linked != null ? linked.asLong() : Long.MIN_VALUE);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        String ownerStr = input.getString(DATA_OWNER).orElse("");
        owner = ownerStr.isEmpty() ? null : UUID.fromString(ownerStr);
        ContainerHelper.loadAllItems(input, inventory);
        long linkedLong = input.getLongOr(DATA_LINKED, Long.MIN_VALUE);
        linked = linkedLong == Long.MIN_VALUE ? null : BlockPos.of(linkedLong);
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
    public int getContainerSize() { return inventory.size(); }

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
    public boolean stillValid(Player player) { return Container.stillValidBlockEntity(this, player); }

    @Override
    public void clearContent() { inventory.clear(); }

    @Override
    public boolean isGridAligned() { return true; }
}
