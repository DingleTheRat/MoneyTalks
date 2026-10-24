package net.dingletherat.registry;

import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableSet;

import dev.architectury.injectables.annotations.ExpectPlatform;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.dingletherat.MoneyTalks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class MoneyVillagers {
    public static final DeferredRegister<VillagerProfession> PROFESSIONS = DeferredRegister.create(MoneyTalks.MOD_ID, Registries.VILLAGER_PROFESSION);

    public static final ResourceKey<PoiType> INVESTOR_POI_KEY = registerPoiKey("investor_poi");

    public static final ResourceKey<VillagerProfession> INVESTOR_KEY = ResourceKey.create(
        Registries.VILLAGER_PROFESSION, Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, "investor"));
    public static final RegistrySupplier<VillagerProfession> INVESTOR = registerProfession("investor", INVESTOR_POI_KEY);

    private static RegistrySupplier<VillagerProfession> registerProfession(String name, ResourceKey<PoiType> type) {
        return PROFESSIONS.register(name, () -> new VillagerProfession(
                Component.translatable("entity.minecraft.villager." + name),
                entry -> entry.is(type), entry -> entry.is(type),
                ImmutableSet.of(), ImmutableSet.of(),
                SoundEvents.VILLAGER_WORK_LIBRARIAN,
                new Int2ObjectOpenHashMap<>(Map.of(
                    1, ResourceKey.create(Registries.TRADE_SET, Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, "investor/level_1")),
                    2, ResourceKey.create(Registries.TRADE_SET, Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, "investor/level_2")),
                    3, ResourceKey.create(Registries.TRADE_SET, Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, "investor/level_3")),
                    4, ResourceKey.create(Registries.TRADE_SET, Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, "investor/level_4")),
                    5, ResourceKey.create(Registries.TRADE_SET, Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, "investor/level_5"))
            ))));
    }

    @SafeVarargs
    private static Supplier<Set<BlockState>> poiStates(Supplier<? extends Block>... blocks) {
        return () -> Stream.of(blocks)
            .flatMap(block -> block.get().getStateDefinition().getPossibleStates().stream())
            .collect(Collectors.toUnmodifiableSet());
    }

    private static ResourceKey<PoiType> registerPoiKey(String name) {
        return ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, name));
    }

    @ExpectPlatform
    public static void registerPoi(ResourceKey<PoiType> key, int ticketCount, int searchDistance, Supplier<Set<BlockState>> states) {
        throw new AssertionError();
    }

    public static void initialize() {
        MoneyTalks.LOGGER.info("Initializing Villagers for " + MoneyTalks.MOD_ID);
        Supplier<? extends Block>[] poiBlocks = Stream.concat(MoneyBlocks.CARPETS.stream(), Stream.of(MoneyBlocks.DOUBLOON)).toArray(Supplier[]::new);
        registerPoi(INVESTOR_POI_KEY, 1, 1, poiStates(poiBlocks));
        PROFESSIONS.register();
    }
}
