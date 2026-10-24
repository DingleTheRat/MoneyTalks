package net.dingletherat.registry.fabric;

import java.util.Set;
import java.util.function.Supplier;

import net.fabricmc.fabric.api.object.builder.v1.world.poi.PoiHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.state.BlockState;

public class MoneyVillagersImpl {
    public static void registerPoi(ResourceKey<PoiType> key, int ticketCount, int searchDistance, Supplier<Set<BlockState>> states) {
        PoiHelper.register(key.identifier(), ticketCount, searchDistance, states.get());
    }
}
