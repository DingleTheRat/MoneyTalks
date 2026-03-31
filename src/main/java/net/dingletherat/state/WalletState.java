package net.dingletherat.state;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.dingletherat.MoneyTalks;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class WalletState extends SavedData {
    private static final Identifier STATE_KEY = Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, "wallet_registry");
    private final Map<UUID, WalletEntry> wallets = new HashMap<>();
    private final Set<UUID> pendingDeductions = new HashSet<>();
    private static final Codec<Map<String, WalletEntry>> MAP_CODEC = Codec.unboundedMap(Codec.STRING, WalletEntry.CODEC);

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

    public static final SavedDataType<WalletState> TYPE = new SavedDataType<>(
            STATE_KEY,
            WalletState::new,
            CODEC,
            DataFixTypes.SAVED_DATA_COMMAND_STORAGE
    );

    public WalletState() {}

    // Called from inventoryTick — NBT is authoritative, just mirror it
    public void update(UUID walletId, String owner, int dollars) {
        wallets.put(walletId, new WalletEntry(owner, dollars));
        setDirty();
    }

    // Called on death — marks wallet as needing NBT update next inventoryTick
    public void applyDeduction(UUID walletId, String owner, int newDollars) {
        wallets.put(walletId, new WalletEntry(owner, newDollars));
        pendingDeductions.add(walletId);
        setDirty();
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
        setDirty();
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
        return server.overworld().getDataStorage().computeIfAbsent(TYPE);
    }
    public Map<UUID, WalletEntry> getAll() {
        return wallets;
    }
}
