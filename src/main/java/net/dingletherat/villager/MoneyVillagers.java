package net.dingletherat.villager;

import java.util.Map;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.dingletherat.MoneyTalks;
import net.dingletherat.block.MoneyBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MoneyVillagers {
    public static final DeferredRegister<PoiType> POI_TYPES =
            DeferredRegister.create(Registries.POINT_OF_INTEREST_TYPE, MoneyTalks.MOD_ID);

    public static final DeferredRegister<VillagerProfession> PROFESSIONS =
            DeferredRegister.create(Registries.VILLAGER_PROFESSION, MoneyTalks.MOD_ID);

    public static final ResourceKey<PoiType> INVESTOR_POI_KEY = ResourceKey.create(
            Registries.POINT_OF_INTEREST_TYPE,
            Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, "investor_poi"));

    public static final DeferredHolder<PoiType, PoiType> INVESTOR_POI = POI_TYPES.register("investor_poi",
            () -> new PoiType(
                    Stream.of(
                            MoneyBlocks.BLACK_MERCHANT_CARPET.get(),
                            MoneyBlocks.BLUE_MERCHANT_CARPET.get(),
                            MoneyBlocks.BROWN_MERCHANT_CARPET.get(),
                            MoneyBlocks.CYAN_MERCHANT_CARPET.get(),
                            MoneyBlocks.GRAY_MERCHANT_CARPET.get(),
                            MoneyBlocks.GREEN_MERCHANT_CARPET.get(),
                            MoneyBlocks.LIGHT_BLUE_MERCHANT_CARPET.get(),
                            MoneyBlocks.LIGHT_GRAY_MERCHANT_CARPET.get(),
                            MoneyBlocks.LIME_MERCHANT_CARPET.get(),
                            MoneyBlocks.MAGENTA_MERCHANT_CARPET.get(),
                            MoneyBlocks.ORANGE_MERCHANT_CARPET.get(),
                            MoneyBlocks.PINK_MERCHANT_CARPET.get(),
                            MoneyBlocks.PURPLE_MERCHANT_CARPET.get(),
                            MoneyBlocks.RED_MERCHANT_CARPET.get(),
                            MoneyBlocks.WHITE_MERCHANT_CARPET.get(),
                            MoneyBlocks.YELLOW_MERCHANT_CARPET.get(),
                            MoneyBlocks.DOUBLOON.get())
                    .flatMap(block -> block.getStateDefinition().getPossibleStates().stream())
                    .collect(ImmutableSet.toImmutableSet()),
                    1, 1));

    public static final ResourceKey<VillagerProfession> INVESTOR_KEY = ResourceKey.create(
            Registries.VILLAGER_PROFESSION,
            Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, "investor"));

    public static final DeferredHolder<VillagerProfession, VillagerProfession> INVESTOR = PROFESSIONS.register("investor",
            () -> new VillagerProfession(
                    Component.translatable("entity." + MoneyTalks.MOD_ID + ".villager.investor"),
                    entry -> entry.is(INVESTOR_POI_KEY),
                    entry -> entry.is(INVESTOR_POI_KEY),
                    ImmutableSet.of(),
                    ImmutableSet.of(),
                    SoundEvents.VILLAGER_WORK_LIBRARIAN,
                    new Int2ObjectOpenHashMap<>(Map.of(
                            1, ResourceKey.create(Registries.TRADE_SET, Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, "investor/level_1")),
                            2, ResourceKey.create(Registries.TRADE_SET, Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, "investor/level_2")),
                            3, ResourceKey.create(Registries.TRADE_SET, Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, "investor/level_3")),
                            4, ResourceKey.create(Registries.TRADE_SET, Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, "investor/level_4")),
                            5, ResourceKey.create(Registries.TRADE_SET, Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, "investor/level_5"))))));

    public static void registerVillagers(IEventBus modEventBus) {
        MoneyTalks.LOGGER.info("Registering Villagers for " + MoneyTalks.MOD_ID);
        POI_TYPES.register(modEventBus);
        PROFESSIONS.register(modEventBus);
    }
}
