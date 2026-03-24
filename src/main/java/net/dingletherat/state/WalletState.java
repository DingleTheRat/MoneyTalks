package net.dingletherat.state;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class WalletState extends PersistentState {
private static final String STATE_KEY = "wallet_registry";

private final Map<UUID, WalletEntry> wallets = new HashMap<>();
    // Wallets that have a pending deduction to apply to NBT next inventoryTick
    private final Set<UUID> pendingDeductions = new HashSet<>();

    public static class WalletEntry {
        public String owner;
        public int dollars;

        public WalletEntry(String owner, int dollars) {
            this.owner = owner;
            this.dollars = dollars;
        }

        public static final Codec<WalletEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.fieldOf("owner").forGetter(e -> e.owner),
                Codec.INT.fieldOf("dollars").forGetter(e -> e.dollars)
        ).apply(instance, WalletEntry::new));
    }

    private static final Codec<Map<String, WalletEntry>> MAP_CODEC =
            Codec.unboundedMap(Codec.STRING, WalletEntry.CODEC);

    private static final Codec<WalletState> CODEC = MAP_CODEC.xmap(
            map -> {
                WalletState state = new WalletState();
                map.forEach((key, value) -> state.wallets.put(UUID.fromString(key), value));
                return state;
            },
            state -> {
                Map<String, WalletEntry> map = new HashMap<>();
                state.wallets.forEach((uuid, entry) -> map.put(uuid.toString(), entry));
                return map;
            }
    );

    public static final PersistentStateType<WalletState> TYPE = new PersistentStateType<>(
            STATE_KEY,
            WalletState::new,
            CODEC,
            null
    );

    public WalletState() {}

    // Called from inventoryTick — NBT is authoritative, just mirror it
    public void update(UUID walletId, String owner, int dollars) {
        wallets.put(walletId, new WalletEntry(owner, dollars));
        markDirty();
    }

    // Called on death — marks wallet as needing NBT update next inventoryTick
    public void applyDeduction(UUID walletId, String owner, int newDollars) {
        wallets.put(walletId, new WalletEntry(owner, newDollars));
        pendingDeductions.add(walletId);
        markDirty();
    }

    // Returns the deducted amount if pending, then clears the flag
    public Integer consumePendingDeduction(UUID walletId) {
        if (!pendingDeductions.contains(walletId)) return null;
        pendingDeductions.remove(walletId);
        WalletEntry entry = wallets.get(walletId);
        return entry != null ? entry.dollars : null;
    }

    public boolean hasPendingDeduction(UUID walletId) {
        return pendingDeductions.contains(walletId);
    }

    public void remove(UUID walletId) {
        wallets.remove(walletId);
        pendingDeductions.remove(walletId);
        markDirty();
    }

    public Map<UUID, WalletEntry> getWalletsOwnedBy(String playerName) {
        Map<UUID, WalletEntry> result = new HashMap<>();
        for (Map.Entry<UUID, WalletEntry> entry : wallets.entrySet()) {
            if (playerName.equals(entry.getValue().owner)) {
                result.put(entry.getKey(), entry.getValue());
            }
        }
        return result;
    }

    public WalletEntry get(UUID walletId) {
        return wallets.get(walletId);
    }

    public static WalletState get(MinecraftServer server) {
        return server.getOverworld().getPersistentStateManager().getOrCreate(TYPE);
    }
    public Map<UUID, WalletEntry> getAll() {
        return wallets;
    }
}
