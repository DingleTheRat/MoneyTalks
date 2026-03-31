package net.dingletherat.state;

import com.mojang.serialization.Codec;

import net.dingletherat.MoneyTalks;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.*;

public class ShopState extends SavedData {
    private static final Identifier STATE_KEY = Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, "shop_registry");
    private final Map<UUID, List<BlockPos>> shops = new HashMap<>();
    private static final Codec<Map<String, List<Long>>> MAP_CODEC = Codec.unboundedMap(Codec.STRING, Codec.LONG.listOf());

    private static final Codec<ShopState> CODEC = MAP_CODEC.xmap(
            map -> {
                ShopState state = new ShopState();
                map.forEach((key, value) -> {
                    List<BlockPos> positions = new ArrayList<>();
                    value.forEach(l -> positions.add(BlockPos.of(l)));
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

    public static final SavedDataType<ShopState> TYPE = new SavedDataType<>(
            STATE_KEY,
            ShopState::new,
            CODEC,
            DataFixTypes.SAVED_DATA_COMMAND_STORAGE
    );

    public ShopState() {}

    public void register(UUID owner, BlockPos pos) {
        shops.computeIfAbsent(owner, k -> new ArrayList<>()).add(pos);
        setDirty();
    }

    public void unregister(UUID owner, BlockPos pos) {
        List<BlockPos> positions = shops.get(owner);
        if (positions != null && positions.remove(pos)) {
            if (positions.isEmpty()) {
                shops.remove(owner);
            }
            setDirty();
        }
    }

    public List<BlockPos> getShops(UUID owner) {
        return shops.getOrDefault(owner, Collections.emptyList());
    }

    public static ShopState get(MinecraftServer server) {
        return server.overworld().getDataStorage().computeIfAbsent(TYPE);
    }
}
