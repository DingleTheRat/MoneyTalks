package net.dingletherat.block.entity.custom;

import java.util.UUID;
import org.jetbrains.annotations.Nullable;
import net.dingletherat.block.MoneyBlocks;
import net.dingletherat.block.custom.DoubloonCompressor;
import net.dingletherat.block.entity.MoneyBlockEntities;
import net.dingletherat.item.MoneyItems;
import net.dingletherat.item.custom.Wallet;
import net.dingletherat.screen.custom.CompressorMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.NonNull;

public class DoubloonCompressorEntity extends BlockEntity implements WorldlyContainer, MenuProvider {
    // Slots
    public static final int WALLET_SLOT = 0;
    public static final int FUEL_SLOT = 1;
    public static final int DOUBLOON_SLOT = 2;

    // Other
    public static final int COMPRESSION_TICKS = 100;
    public static final int COMPRESSIONS_PER_FUEL = 3;
    public static final int TAKE_COINS = 10;

    protected UUID owner;
    protected int compression_progress = COMPRESSION_TICKS;
    protected int compressions = 0;
    protected int original_wallet_coins;
    protected String name;
    protected String walletOwner;
    protected final NonNullList<ItemStack> inventory = NonNullList.withSize(3, ItemStack.EMPTY);

    // For the displays in the UI (arrow and fuel)
    public static final int DISPLAYS_PROGRESS = 0;
    public static final int DISPLAYS_COMPRESSION_TICKS = 1;
    public static final int DISPLAYS_COMPRESSIONS = 2;
    public static final int DISPLAYS_COMPRESSIONS_PER_FUEL = 3;
    public final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case DISPLAYS_PROGRESS -> compression_progress;
                case DISPLAYS_COMPRESSION_TICKS -> COMPRESSION_TICKS;
                case DISPLAYS_COMPRESSIONS -> compressions;
                case DISPLAYS_COMPRESSIONS_PER_FUEL -> COMPRESSIONS_PER_FUEL;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case DISPLAYS_PROGRESS -> compression_progress = value;
                case DISPLAYS_COMPRESSIONS -> compressions = value;
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    public <T extends DoubloonCompressorEntity> DoubloonCompressorEntity(BlockEntityType<T> blockEntityType, BlockPos pos, BlockState state) {
        super(blockEntityType, pos, state);
    }

    public DoubloonCompressorEntity(BlockPos pos, BlockState state) {
        super(MoneyBlockEntities.DOUBLOON_COMPRESSOR_ENTITY, pos, state);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.moneytalks.doubloon_compressor");
    }

    @Override
    public AbstractContainerMenu createMenu(int syncId, Inventory inventory, Player player) {
        return new CompressorMenu(syncId, inventory, this, data);
    }

    public static void tick(Level world, BlockPos position, BlockState state, DoubloonCompressorEntity compressor) {
        // NO CLIENTS >:(
        if (world.isClientSide()) return;

        // Get every item in every slot
        ItemStack wallet = compressor.getItem(WALLET_SLOT);
        ItemStack fuel = compressor.getItem(FUEL_SLOT);
        ItemStack doubloons = compressor.getItem(DOUBLOON_SLOT);

        // If the compressor has no more fuel, take some.
        if (fuel.is(Items.BLAZE_POWDER) && !state.getValue(DoubloonCompressor.FUELED)) {
            fuel.shrink(1);
            world.setBlock(position, state.setValue(DoubloonCompressor.FUELED, true), 3);
            compressor.compressions = COMPRESSIONS_PER_FUEL;
            setChanged(world, position, state);
            return;
        }

        // Return if it's not fueled or has no wallet to deduct the coins from
        if (!wallet.is(MoneyItems.WALLET) || !state.getValue(DoubloonCompressor.FUELED)) return;

        // Get the amount of dollars in the wallet and get rid of it if we can't take anymore coins
        int dollars = Wallet.getDollars(wallet);
        if (dollars < TAKE_COINS) compressor.setItem(WALLET_SLOT, ItemStack.EMPTY);

        // If we aren't done compressing, decrease the progress and return
        if (compressor.compression_progress != 0) {
            compressor.compression_progress--;
            setChanged(world, position, state);
            return;
        }

        // Finish compression, as long as it isn't the max stack size
        if (doubloons.getCount() != doubloons.getMaxStackSize()) {
            // Remove da money from da wallet
            Wallet.setDollars(wallet, dollars - TAKE_COINS);

            // Increase or add in a doubloon into the output slot (doubloon slot)
            if (compressor.getItem(DOUBLOON_SLOT) == ItemStack.EMPTY) compressor.setItem(DOUBLOON_SLOT, new ItemStack(MoneyBlocks.DOUBLOON.asItem()));
            else compressor.getItem(DOUBLOON_SLOT).grow(1);

            // Increment compressions. If it reached 3, set fueled to be false so it can consume some.
            compressor.compressions--;
            if (compressor.compressions <= 0) world.setBlock(position, state.setValue(DoubloonCompressor.FUELED, false), 3);

            // Finish everything off
            compressor.compression_progress = COMPRESSION_TICKS;
            setChanged(world, position, state);
        }
    }

    public UUID getOwner() { return owner; }
    public void setOwner(UUID owner) { this.owner = owner; }
    public int getTransactionCoins() { return original_wallet_coins; }
    public void setTransactionCoins(int coins) { this.original_wallet_coins = coins; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getWalletOwner() { return walletOwner; }
    public void setWalletOwner(String owner) { this.walletOwner = owner; }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putString("owner", owner == null ? "" : owner.toString());

        output.putInt("compressing_progress", compression_progress);
        output.putInt("compressions", compressions);
        
        output.putInt("original_wallet_coins", original_wallet_coins);
        output.putString("name", name == null ? "Doubloon Compressor" : name);
        output.putString("wallet_owner", walletOwner == null ? "Unknown Player" : name);
        ContainerHelper.saveAllItems(output, inventory);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        String ownerStr = input.getString("owner").orElse("");
        owner = ownerStr.isEmpty() ? null : UUID.fromString(ownerStr);

        compression_progress = input.getIntOr("compressing_progress", COMPRESSION_TICKS);
        compressions = input.getIntOr("compressings", COMPRESSIONS_PER_FUEL);

        original_wallet_coins = input.getIntOr("original_wallet_coins", 0);
        name = input.getStringOr("name", "Doubloon Compressor");
        walletOwner = input.getStringOr("wallet_owner", "Unknown Player");
        ContainerHelper.loadAllItems(input, inventory);
    }

    @Override
    public int getContainerSize() { return 3; }

    @Override
    public int @NonNull [] getSlotsForFace(Direction side) {
        if (side == Direction.UP) return new int[]{ 0 };
        if (side == Direction.DOWN) return new int[]{ 2 };
        return new int[]{ 1 };
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, @Nullable Direction direction) {
        if (slot == WALLET_SLOT) { return stack.is(MoneyItems.WALLET); }
        if (slot == FUEL_SLOT) { return stack.is(Items.BLAZE_POWDER); }
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction direction) {
        return slot == DOUBLOON_SLOT;
    }

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
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() { inventory.clear(); }
}
