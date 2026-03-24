package net.dingletherat.block.entity.custom;

import java.util.Map;
import java.util.UUID;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;

import net.dingletherat.MoneyTalks;
import net.dingletherat.block.entity.MoneyBlockEntities;
import net.dingletherat.item.MoneyItems;
import net.dingletherat.state.WalletState;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvents;
import net.minecraft.storage.ReadView;
import net.minecraft.storage.WriteView;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MerchantCarpetEntity extends BlockEntity{
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
    protected void writeData(WriteView view) {
        super.writeData(view);
        view.putInt("price", price);
        view.putInt("amount", amount);
        view.putString("owner", owner != null ? owner.toString() : "");
        view.putInt("purchaseAmount", purchaseAmount);
        view.putInt("collected", collected);
        view.putString("trade", trade.isEmpty() ? "" : ItemStack.CODEC.encodeStart(
            JsonOps.INSTANCE, trade).result().map(Object::toString).orElse(""));
        view.putString("item", item != null ? Registries.ITEM.getId(item).toString() : "minecraft:air");
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        price = view.getInt("price", 0);
        amount = view.getInt("amount", 0);
        String ownerStr = view.getString("owner", "");
        owner = ownerStr.isEmpty() ? null : UUID.fromString(ownerStr);
        purchaseAmount = view.getInt("purchaseAmount", 0);
        collected = view.getInt("collected", 0);

        String tradeStr = view.getString("trade", "");
        trade = tradeStr.isEmpty() ? ItemStack.EMPTY : ItemStack.CODEC.parse(
            JsonOps.INSTANCE, new JsonParser().parse(tradeStr)).result().orElse(ItemStack.EMPTY);

        Identifier id = Identifier.tryParse(view.getString("item", "minecraft:air"));
        item = Registries.ITEM.get(id);
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
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registries) {
        return createNbt(registries);
    }

    public void update() {
        markDirty();
        if (world != null && !world.isClient())
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
    }

    private void payOwner(World world, int amount) {
        if (owner == null || world.isClient()) return;
        if (MoneyTalks.walletState == null) return;

        PlayerEntity ownerPlayer = world.getPlayerByUuid(owner);
        String ownerName = ownerPlayer != null ? ownerPlayer.getName().getString() : null;

        if (ownerName == null) {
            collected += amount;
            return;
        }

        // Check player inventory first
        if (ownerPlayer != null) {
            PlayerInventory inventory = ownerPlayer.getInventory();
            for (int i = 0; i < inventory.size(); i++) {
                ItemStack stack = inventory.getStack(i);
                if (stack.isOf(MoneyItems.WALLET) && stack.contains(DataComponentTypes.CUSTOM_DATA)) {
                    NbtCompound nbt = stack.get(DataComponentTypes.CUSTOM_DATA).copyNbt();
                    int newAmount = nbt.getInt("Dollars", 0) + amount;
                    nbt.putInt("Dollars", newAmount);
                    stack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(nbt));
                    String walletId = nbt.getString("WalletId", "");
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

    public ActionResult click(ItemStack stack, World world, PlayerEntity player) {
        if (owner != null && player != null && owner.equals(player.getUuid())) {
            if (price == 0) {
                if (!stack.isOf(MoneyItems.DOLLAR)) return ActionResult.FAIL;

                price = stack.getCount();
                player.playSound(SoundEvents.BLOCK_VAULT_INSERT_ITEM, 1.0f, 1.0f);

                update();
                return ActionResult.SUCCESS;
            }
            if (item == null) {
                trade = stack.copy();
                item = trade.getItem();
                amount = stack.getCount();
                purchaseAmount = stack.getCount();
                stack.decrement(stack.getCount());

                player.playSound(SoundEvents.BLOCK_VAULT_INSERT_ITEM, 1.0f, 1.0f);

                update();
                return ActionResult.SUCCESS;
            }
            if (item == stack.getItem() && ItemStack.areItemsAndComponentsEqual(trade, stack)) {
                amount += stack.getCount();
                stack.decrement(stack.getCount());

                player.playSound(SoundEvents.BLOCK_VAULT_INSERT_ITEM, 1.0f, 1.0f);

                update();
                return ActionResult.SUCCESS;
            }
            if (stack.isEmpty()) {
                player.getInventory().insertStack(new ItemStack(MoneyItems.DOLLAR, collected));
                collected = 0;

                player.playSound(SoundEvents.BLOCK_VAULT_EJECT_ITEM, 1.0f, 1.0f);

                update();
                return ActionResult.SUCCESS;
            }
        }

        if (price == 0 || item == null || amount == 0) return ActionResult.FAIL;
        if (stack.getCount() < price) return ActionResult.FAIL;
        if (!stack.isOf(MoneyItems.DOLLAR)) return ActionResult.FAIL;
        if (amount < purchaseAmount) return ActionResult.FAIL;


        stack.decrement(price);
        payOwner(world, price);

        ItemStack purchase = trade.copy();
        purchase.setCount(purchaseAmount);
        ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), purchase);
        amount -= purchaseAmount;

        if (!world.isClient()) {
            String message = player.getName().getString() + " bought " + purchaseAmount + " " + trade.getName().getString() + " for " + price + " Dollar!";
            ServerPlayerEntity ownerPlayer = world.getServer().getPlayerManager().getPlayer(owner);
            if (ownerPlayer != null)
                ownerPlayer.sendMessage(Text.literal(message).formatted(Formatting.GOLD), true);
        }

        player.playSound(SoundEvents.BLOCK_VAULT_OPEN_SHUTTER, 1.0f, 1.0f);

        update();
        return ActionResult.SUCCESS;
    }

    public ActionResult hopperInsert(ItemStack stack, World world) {
        if (item == null || item == Items.AIR) return ActionResult.FAIL;
        if (stack.getItem() != item) return ActionResult.FAIL;
        if (!ItemStack.areItemsAndComponentsEqual(trade, stack)) return ActionResult.FAIL;

        amount += stack.getCount();
        stack.decrement(stack.getCount());
        update();
        return ActionResult.SUCCESS;
    }

    public void onBreak() {
        if (trade != null && trade.getCount() > 0) {
            ItemStack drop = trade.copy();
            drop.setCount(amount);
            ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(), drop);
        }
        if (collected > 0) {
            ItemScatterer.spawn(world, pos.getX(), pos.getY(), pos.getZ(),
                    new ItemStack(MoneyItems.DOLLAR, collected));
        }
    }
}
