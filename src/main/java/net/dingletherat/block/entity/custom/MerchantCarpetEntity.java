package net.dingletherat.block.entity.custom;

import java.util.Map;
import java.util.UUID;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;

import net.dingletherat.MoneyTalks;
import net.dingletherat.block.entity.MoneyBlockEntities;
import net.dingletherat.item.MoneyItems;
import net.dingletherat.state.WalletState;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.ChatFormatting;
import net.minecraft.resources.Identifier;
import net.minecraft.world.Containers;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class MerchantCarpetEntity extends BlockEntity {
    private int price = 0;
    private UUID owner = null;
    private int purchaseAmount = 0;
    private int collected = 0;
    private int amount = 0;
    private ItemStack trade = ItemStack.EMPTY;
    private Item item = null;

    private float rotation = 0;

    public MerchantCarpetEntity(BlockPos pos, BlockState state) {
        super(MoneyBlockEntities.MERCHANT_CARPET_ENTITY, pos, state);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("price", price);
        output.putInt("amount", amount);
        output.putString("owner", owner != null ? owner.toString() : "");
        output.putInt("purchaseAmount", purchaseAmount);
        output.putInt("collected", collected);
        output.putString("trade", trade.isEmpty() ? "" : ItemStack.CODEC.encodeStart(
                JsonOps.INSTANCE, trade).result().map(Object::toString).orElse(""));
        output.putString("item", item != null ? BuiltInRegistries.ITEM.getId(item) : "minecraft:air");
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        price = input.getInt("price").orElse(0);
        amount = input.getInt("amount").orElse(0);
        String ownerStr = input.getString("owner").orElse("");
        owner = ownerStr.isEmpty() ? null : UUID.fromString(ownerStr);
        purchaseAmount = input.getInt("purchaseAmount").orElse(0);
        collected = input.getInt("collected").orElse(0);

        String tradeStr = input.getString("trade").orElse("");
        trade = tradeStr.isEmpty() ? ItemStack.EMPTY : ItemStack.CODEC.parse(
                JsonOps.INSTANCE, new JsonParser().parse(tradeStr)).result().orElse(ItemStack.EMPTY);

        Identifier id = Identifier.tryParse(input.getString("item").orElse("minecraft:air"));
        item = BuiltInRegistries.ITEM.get(id);
    }

    public void setOwner(UUID player) {
        owner = player;
        update();
    }
    public UUID getOwner() {
        return owner;
    }
    public ItemStack getDisplay() {
        if (item != null && trade.getItem() != Items.AIR && amount >= purchaseAmount)
            return new ItemStack(item);

        if (price != 0)
            return new ItemStack(MoneyItems.DOLLAR);

        return ItemStack.EMPTY;
    }
    public float getRenderingRotation() {
        float addition;
        if (item != null && purchaseAmount > 0 && amount > 0) {
            float stockRatio = (float) amount / purchaseAmount;
            addition = Math.min(8.0f, Math.max(0.1f, stockRatio * 0.4f));
        } else addition = 0.1f;

        rotation += addition;
        if (rotation >= 360) rotation = 0;
        return rotation;
    }

    @Override
    public Packet<ClientGamePacketListener> toUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag toInitialChunkDataNbt(HolderLookup.Provider registries) {
        return createNbt(registries);
    }

    public void update() {
        setChanged();
        if (world != null && !world.isClient())
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
    }

    private void payOwner(Level world, int amount) {
        if (owner == null || world.isClientSide()) return;
        if (MoneyTalks.walletState == null) return;

        Player ownerPlayer = world.getPlayerByUUID(owner);
        String ownerName = ownerPlayer != null ? ownerPlayer.getName().getString() : null;

        if (ownerName == null) {
            collected += amount;
            return;
        }

        // Check player inventory first
        if (ownerPlayer != null) {
            Inventory inventory = ownerPlayer.getInventory();
            for (int i = 0; i < inventory.size(); i++) {
                ItemStack stack = inventory.getItem(i);
                if (stack.is(MoneyItems.WALLET) && stack.has(DataComponents.CUSTOM_DATA)) {
                    CompoundTag nbt = stack.get(DataComponents.CUSTOM_DATA).copyTag();
                    int newAmount = nbt.getInt("Dollars").orElse(0) + amount;
                    nbt.putInt("Dollars", newAmount);
                    stack.set(DataComponents.CUSTOM_DATA, CustomData.of(nbt));
                    String walletId = nbt.getString("WalletId").orElse("");
                    if (!walletId.isEmpty()) {
                        MoneyTalks.walletState.update(UUID.fromString(walletId), ownerName, newAmount);
                    }
                    return;
                }
            }
        }

        // Check wallets in containers via WalletState
        Map<UUID, WalletState.WalletEntry> ownerWallets = MoneyTalks.walletState.getWalletsOwnedBy(ownerName);
        if (!ownerWallets.isEmpty()) {
            Map.Entry<UUID, WalletState.WalletEntry> first = ownerWallets.entrySet().iterator().next();
            int newAmount = first.getValue().dollars + amount;
            MoneyTalks.walletState.applyDeduction(first.getKey(), first.getValue().owner, newAmount);
            return;
        }

        // No wallet found anywhere
        collected += amount; update();
    }

    public InteractionResult click(ItemStack stack, Level world, Player player) {
        if (owner != null && player != null && owner.equals(player.getUUID())) {
            if (price == 0) {
                if (!stack.is(MoneyItems.DOLLAR)) return InteractionResult.FAIL;

                price = stack.getCount();
                player.playSound(SoundEvents.VAULT_INSERT_ITEM, 1.0f, 1.0f);

                update();
                return InteractionResult.SUCCESS;
            }
            if (item == null) {
                trade = stack.copy();
                item = trade.getItem();
                amount = stack.getCount();
                purchaseAmount = stack.getCount();
                stack.shrink(stack.getCount());

                player.playSound(SoundEvents.VAULT_INSERT_ITEM, 1.0f, 1.0f);

                update();
                return InteractionResult.SUCCESS;
            }
            if (item == stack.getItem() && ItemStack.isSameItemSameComponents(trade, stack)) {
                amount += stack.getCount();
                stack.shrink(stack.getCount());

                player.playSound(SoundEvents.VAULT_INSERT_ITEM, 1.0f, 1.0f);

                update();
                return InteractionResult.SUCCESS;
            }
            if (stack.isEmpty()) {
                player.getInventory().add(new ItemStack(MoneyItems.DOLLAR, collected));
                collected = 0;

                player.playSound(SoundEvents.VAULT_EJECT_ITEM, 1.0f, 1.0f);

                update();
                return InteractionResult.SUCCESS;
            }
        }

        if (price == 0 || item == null || amount == 0) return InteractionResult.FAIL;
        if (stack.getCount() < price) return InteractionResult.FAIL;
        if (!stack.is(MoneyItems.DOLLAR)) return InteractionResult.FAIL;
        if (amount < purchaseAmount) return InteractionResult.FAIL;


        stack.shrink(price);
        payOwner(world, price);

        ItemStack purchase = trade.copy();
        purchase.setCount(purchaseAmount);
        Containers.spawn(world, pos.getX(), pos.getY(), pos.getZ(), purchase);
        amount -= purchaseAmount;

        if (!world.isClientSide()) {
            String message = player.getName().getString() + " bought " + purchaseAmount + " " + trade.getItemName().getString() + " for " + price + " Dollar!";
            ServerPlayer ownerPlayer = world.getServer().getPlayerManager().getPlayer(owner);
            if (ownerPlayer != null)
                ownerPlayer.sendMessage(Component.literal(message).formatted(ChatFormatting.GOLD), true);
        }

        player.playSound(SoundEvents.VAULT_OPEN_SHUTTER, 1.0f, 1.0f);

        update();
        return InteractionResult.SUCCESS;
    }

    public InteractionResult hopperInsert(ItemStack stack, Level world) {
        if (item == null || item == Items.AIR) return InteractionResult.FAIL;
        if (stack.getItem() != item) return InteractionResult.FAIL;
        if (!ItemStack.isSameItemSameComponents(trade, stack)) return InteractionResult.FAIL;

        amount += stack.getCount();
        stack.shrink(stack.getCount());
        update();
        return InteractionResult.SUCCESS;
    }

    public void onBreak() {
        if (trade != null && trade.getCount() > 0) {
            ItemStack drop = trade.copy();
            drop.setCount(amount);
            Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), drop);
        }
        if (collected > 0) {
            Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(MoneyItems.DOLLAR, collected));
        }
    }
}
