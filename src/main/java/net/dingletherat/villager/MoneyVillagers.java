package net.dingletherat.villager;

import java.util.Map;

import com.google.common.collect.ImmutableSet;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.dingletherat.MoneyTalks;
import net.dingletherat.block.MoneyBlocks;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PoiHelper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.level.block.Block;

public class MoneyVillagers {
    public static final ResourceKey<PoiType> INVESTOR_POI_KEY = registerPoiKey("investor_poi");
    public static final PoiType INVESTOR_POI = registerPOI("investor_poi",
        (Block) MoneyBlocks.BLACK_MERCHANT_CARPET,
        (Block) MoneyBlocks.BLUE_MERCHANT_CARPET,
        (Block) MoneyBlocks.BROWN_MERCHANT_CARPET,
        (Block) MoneyBlocks.CYAN_MERCHANT_CARPET,
        (Block) MoneyBlocks.GRAY_MERCHANT_CARPET,
        (Block) MoneyBlocks.GREEN_MERCHANT_CARPET,
        (Block) MoneyBlocks.LIGHT_BLUE_MERCHANT_CARPET,
        (Block) MoneyBlocks.LIGHT_GRAY_MERCHANT_CARPET,
        (Block) MoneyBlocks.LIME_MERCHANT_CARPET,
        (Block) MoneyBlocks.MAGENTA_MERCHANT_CARPET,
        (Block) MoneyBlocks.ORANGE_MERCHANT_CARPET,
        (Block) MoneyBlocks.PINK_MERCHANT_CARPET,
        (Block) MoneyBlocks.PURPLE_MERCHANT_CARPET,
        (Block) MoneyBlocks.RED_MERCHANT_CARPET,
        (Block) MoneyBlocks.WHITE_MERCHANT_CARPET,
        (Block) MoneyBlocks.YELLOW_MERCHANT_CARPET,

        (Block) MoneyBlocks.DOUBLOON
    );

    public static final ResourceKey<VillagerProfession> INVESTOR_KEY = ResourceKey.create(
        Registries.VILLAGER_PROFESSION, Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, "investor"));
    public static final VillagerProfession INVESTOR = registerProfession("investor", INVESTOR_POI_KEY);

    private static VillagerProfession registerProfession(String name, ResourceKey<PoiType> type) {
        return Registry.register(BuiltInRegistries.VILLAGER_PROFESSION,
            Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, name),
            new VillagerProfession(
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

    private static PoiType registerPOI(String name, Block... blocks) {
        return PoiHelper.register(Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, name), 1, 1, blocks);
    }

    private static ResourceKey<PoiType> registerPoiKey(String name) {
        return ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, name));
    }

    public static void registerVillagers() {
        MoneyTalks.LOGGER.info("Registering Villagers for " + MoneyTalks.MOD_ID);
    }
}
