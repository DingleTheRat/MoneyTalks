package net.dingletherat.block.entity.custom;

import java.util.Map;
import java.util.UUID;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.dingletherat.MoneyTalks;
import net.dingletherat.block.entity.MoneyBlockEntities;
import net.dingletherat.item.LegacyData;
import net.dingletherat.item.MoneyDataComponents;
import net.dingletherat.item.MoneyItems;
import net.dingletherat.state.WalletState;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class MerchantCarpetEntity extends BlockEntity implements Container {
    public static final String DATA_PRICE = "price";
    public static final String DATA_AMOUNT = "amount";
    public static final String DATA_PURCHASE_AMOUNT = "purchaseAmount";
    public static final String DATA_COLLECTED = "collected";
    public static final String DATA_OWNER = "owner";
    public static final String DATA_TRADE = "trade";
    public static final String DATA_ITEM = "item";
    public static final String DATA_WALLET = "wallet";
    public static final String DATA_PURCHASES = "purchases";

    private int price = 0;
    private int purchaseAmount = 0;
    private int collected = 0;
    private int purchases = 0; // Counter for Monopoly advancement
    private UUID owner = null;
    private int amount = 0;
    private ItemStack trade = ItemStack.EMPTY;
    private Item item = null;
    private float rotation = 0;
    private UUID wallet = null;

    public MerchantCarpetEntity(BlockPos position, BlockState state) {
        super(MoneyBlockEntities.MERCHANT_CARPET_ENTITY, position, state);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt(DATA_PRICE, price);
        output.putInt(DATA_AMOUNT, amount);
        output.putInt(DATA_PURCHASE_AMOUNT, purchaseAmount);
        output.putInt(DATA_COLLECTED, collected);
        output.putInt(DATA_PURCHASES, purchases);
        output.putString(DATA_OWNER, owner != null ? owner.toString() : "");
        output.putString(DATA_TRADE, trade.isEmpty() ? "" : ItemStack.CODEC.encodeStart(
                JsonOps.INSTANCE, trade).result().map(Object::toString).orElse(""));
        output.putString(DATA_ITEM, item != null ? BuiltInRegistries.ITEM.getKey(item).toString() : "minecraft:air");
        output.putString(DATA_WALLET, wallet != null ? wallet.toString() : "");
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        price = input.getIntOr(DATA_PRICE, 0);
        amount = input.getIntOr(DATA_AMOUNT, 0);
        purchaseAmount = input.getIntOr(DATA_PURCHASE_AMOUNT, 0);
        collected = input.getIntOr(DATA_COLLECTED, 0);
        purchases = input.getIntOr(DATA_PURCHASES, 0);

        String ownerString = input.getStringOr(DATA_OWNER, "");
        owner = ownerString.isEmpty() ? null : UUID.fromString(ownerString);

        String tradeString = input.getStringOr(DATA_TRADE, "");
        trade = tradeString.isEmpty() ? ItemStack.EMPTY : ItemStack.CODEC.parse(
                JsonOps.INSTANCE, new JsonParser().parse(tradeString)).result().orElse(ItemStack.EMPTY);
        LegacyData.migrate(trade);

        Identifier identifier = Identifier.tryParse(input.getStringOr(DATA_ITEM, "minecraft:air"));
        item = BuiltInRegistries.ITEM.getValue(identifier);

        String walletString = input.getStringOr(DATA_WALLET, "");
        wallet = walletString.isEmpty() ? null : UUID.fromString(walletString);

    }

    public void setOwner(UUID player) {
        owner = player;
        update();
    }

    public UUID getOwner() {
        return owner;
    }

    public ItemStack getDisplay() {
        if (item != null && item != Items.AIR && amount >= purchaseAmount && purchaseAmount > 0)
            return new ItemStack(item);
        if (price != 0)
            return new ItemStack(MoneyItems.COIN);
        return ItemStack.EMPTY;
    }

    public float getRenderingRotation() {
        float addition;
        if (item != null && purchaseAmount > 0 && amount > 0) {
            float stockRatio = (float) amount / purchaseAmount;
            addition = Math.min(8.0f, Math.max(0.1f, stockRatio * 0.4f));
        } else {
            addition = 0.1f;
        }
        rotation += addition;
        if (rotation >= 360) rotation = 0;
        return rotation;
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    public void update() {
        setChanged();
        if (getLevel() != null && !getLevel().isClientSide())
            getLevel().sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
    }

    private void payOwner(Level level, int amount) {
        if (owner == null || level.isClientSide()) return;
        if (MoneyTalks.walletState == null) return;

        Player ownerPlayer = level.getPlayerByUUID(owner);
        String ownerName = ownerPlayer != null ? ownerPlayer.getName().getString() : null;

        if (ownerName == null) {
            collected += amount;
            return;
        }

        Map<UUID, WalletState.WalletEntry> ownerWallets = MoneyTalks.walletState.getWalletsOwnedBy(ownerName);
        if (wallet != null) {
            WalletState.WalletEntry walletEntry = ownerWallets.get(wallet);
            if (walletEntry != null) {
                int newAmount = walletEntry.coins + amount;
                MoneyTalks.walletState.applyDeduction(wallet, walletEntry.owner, newAmount);
                return;
            }
        }

        Inventory inventory = ownerPlayer.getInventory();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.is(MoneyItems.WALLET) || !stack.has(MoneyDataComponents.WALLET_ID)) continue;

            int newAmount = stack.getOrDefault(MoneyDataComponents.COINS, 0) + amount;
            stack.set(MoneyDataComponents.COINS, newAmount);
            MoneyTalks.walletState.update(stack.get(MoneyDataComponents.WALLET_ID), ownerName, newAmount);
            return;
        }

        if (!ownerWallets.isEmpty()) {
            Map.Entry<UUID, WalletState.WalletEntry> first = ownerWallets.entrySet().iterator().next();
            int newAmount = first.getValue().coins + amount;
            MoneyTalks.walletState.applyDeduction(first.getKey(), first.getValue().owner, newAmount);
            return;
        }

        collected += amount;
        update();
    }

    public InteractionResult click(ItemStack stack, Level level, Player player) {
        if (level.isClientSide()) return InteractionResult.SUCCESS;

        if (owner != null && player != null && owner.equals(player.getUUID()))
            if (ownerClick(stack, player)) return InteractionResult.SUCCESS;

        if (price == 0 || item == null || amount == 0) return InteractionResult.FAIL;
        if (stack.getCount() < price) return InteractionResult.FAIL;
        if (!stack.is(MoneyItems.COIN)) return InteractionResult.FAIL;
        if (amount < purchaseAmount) return InteractionResult.FAIL;

        stack.shrink(price);
        payOwner(level, price);
        purchases++;

        ItemStack purchase = trade.copy();
        purchase.setCount(purchaseAmount);
        Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), purchase);
        amount -= purchaseAmount;

        if (!level.isClientSide()) {
            String coinString = price == 1 ? " Coin" : " Coins";
            String message = player.getName().getString() + " bought " + purchaseAmount + " " + trade.getItemName().getString() + " for " + price + coinString;

            ServerPlayer ownerPlayer = level.getServer().getPlayerList().getPlayer(owner);
            if (ownerPlayer != null) {
                ownerPlayer.sendSystemMessage(Component.literal(message).withStyle(ChatFormatting.GOLD), true);

                MoneyTalks.grantAdvancement(ownerPlayer, "pleasure_doing_business", "something_bought");
                int monopolyGoal = MoneyTalks.ADVANCEMENT_MONOPOLY_GOAL;
                if (purchases >= monopolyGoal) MoneyTalks.grantAdvancement(ownerPlayer, "monopoly", "bought_" + monopolyGoal);
            } 
        }

        player.playSound(SoundEvents.VAULT_OPEN_SHUTTER, 1.0f, 1.0f);
        MoneyTalks.grantAdvancement(player, "no_refunds", "bought_something");
        update();
        return InteractionResult.SUCCESS;
    }

    /**
     * @return If to return in the method calling it
     **/
    public boolean ownerClick(ItemStack stack, Player player) {
        if (price == 0) {
            if (!stack.is(MoneyItems.COIN)) return true;
            price = stack.getCount();
            stack.setCount(0);
            player.playSound(SoundEvents.VAULT_INSERT_ITEM, 1.0f, 1.0f);
            update();
            return true;
        }
        if (item == null && !MoneyTalks.NON_TRANSFERABLE.contains(stack.getItem())) {
            trade = stack.copy();
            item = trade.getItem();
            amount = stack.getCount();
            purchaseAmount = stack.getCount();

            stack.shrink(stack.getCount());
            player.playSound(SoundEvents.VAULT_INSERT_ITEM, 1.0f, 1.0f);
            update();
            MoneyTalks.grantAdvancement(player, "open_for_business", "setup_merchant_carpet");

            return true;
        }
        if (item == stack.getItem() && ItemStack.isSameItemSameComponents(trade, stack)) {
            amount += stack.getCount();
            stack.shrink(stack.getCount());
            player.playSound(SoundEvents.VAULT_INSERT_ITEM, 1.0f, 1.0f);
            update();
            return true;
        }
        if (stack.isEmpty()) {
            player.getInventory().add(new ItemStack(MoneyItems.COIN, collected));
            collected = 0;
            player.playSound(SoundEvents.VAULT_EJECT_ITEM, 1.0f, 1.0f);
            update();
            return true;
        }
        if (stack.is(MoneyItems.WALLET)) {
            stack.set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true);
            wallet = stack.get(MoneyDataComponents.WALLET_ID);
            MoneyTalks.grantAdvancement(player, "direct_deposit", "setup_auto_deposit");
        }

        return false;
    }

    public void onBreak() {
        Level level = getLevel();
        if (level != null) {
            if (!trade.isEmpty() && amount > 0) {
                ItemStack drop = trade.copy();
                drop.setCount(amount);
                Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), drop);
            }
            if (collected > 0)
                Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), new ItemStack(MoneyItems.COIN, collected));
        }
    }

    @Override
    public int getContainerSize() { return 1; }

    @Override
    public boolean isEmpty() { return true; }

    @Override
    public ItemStack getItem(int slot) { return ItemStack.EMPTY; }

    @Override
    public ItemStack removeItem(int slot, int amount) { return ItemStack.EMPTY; }

    @Override
    public ItemStack removeItemNoUpdate(int slot) { return ItemStack.EMPTY; }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if (!canPlaceItem(slot, stack)) return;
        amount += stack.getCount();
        stack.shrink(stack.getCount());
        update();
    }

    @Override
    public boolean stillValid(Player player) {
        return Container.stillValidBlockEntity(this, player);
    }

    @Override
    public void clearContent() {
        if (amount == 0) update();
        amount = 0;
    }

    @Override
    public boolean canPlaceItem(int slot, ItemStack stack) {
        return slot == 0 && item != null && trade != null && item != Items.AIR && ItemStack.isSameItemSameComponents(trade, stack);
    }
}
