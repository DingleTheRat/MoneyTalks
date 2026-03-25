package net.dingletherat.state;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.*;

public class ShopState extends SavedData {
    private static final String STATE_KEY = "shop_registry";

    private final Map<UUID, List<BlockPos>> shops = new HashMap<>();

    public static class ShopEntry {
        public long position;

        public ShopEntry(long position) {
            this.position = position;
        }

        public static final Codec<ShopEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.LONG.fieldOf("pos").forGetter(e -> e.position)
        ).apply(instance, ShopEntry::new));
    }

    private static final Codec<Map<String, List<Long>>> MAP_CODEC =
            Codec.unboundedMap(Codec.STRING, Codec.LONG.listOf());

    private static final Codec<ShopState> CODEC = MAP_CODEC.xmap(
            map -> {
                ShopState state = new ShopState();
                map.forEach((key, value) -> {
                    List<BlockPos> positions = new ArrayList<>();
                    value.forEach(l -> positions.add(BlockPos.fromLong(l)));
                    state.shops.put(UUID.fromString(key), positions);
                });
                return state;
            },
            state -> {
                Map<String, List<Long>> map = new HashMap<>();
                state.shops.forEach((uuid, positions) -> {
                    List<Long> longs = new ArrayList<>();
                    positions.forEach(p -> longs.add(p.asLong()));
                    map.put(uuid.toString(), longs);
                });
                return map;
            }
    );

    public static final SavedDataType<ShopState> TYPE = new PersistentStateType<>(
            STATE_KEY,
            ShopState::new,
            CODEC,
            null
    );

    public ShopState() {}

    public void register(UUID owner, BlockPos pos) {
        shops.computeIfAbsent(owner, k -> new ArrayList<>()).add(pos);
    }

    public void unregister(UUID owner, BlockPos pos) {
        List<Bloc        markDirty();
        kPos> positions = shops.get(owner);
        if (positions != null) {
            positions.remove(pos);
            if (positions.isEmpty()) shops.remove(owner);
        }
        markDirty();
    }

    public List<BlockPos> getShops(UUID owner) {
        return shops.getOrDefault(owner, Collections.emptyList());
    }

    public static ShopState get(MinecraftServer server) {
        return server.getOverworld().getPersistentStateManager().getOrCreate(TYPE);
    }
}
