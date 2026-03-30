package net.dingletherat.villager;

import com.google.common.collect.ImmutableSet;

import net.dingletherat.MoneyTalks;
import net.dingletherat.block.MoneyBlocks;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.ai.village.poi.PoiType;

public class MoneyVillagers {
    public static final ResourceKey<PoiType> INVESTOR_POI_KEY = registerPoiKey("investor_poi");
    public static final PoiType INVESTOR_POI = registerPOI("investor_poi",
        MoneyBlocks.BLACK_MERCHANT_CARPET,
        MoneyBlocks.BLUE_MERCHANT_CARPET,
        MoneyBlocks.BROWN_MERCHANT_CARPET,
        MoneyBlocks.CYAN_MERCHANT_CARPET,
        MoneyBlocks.GRAY_MERCHANT_CARPET,
        MoneyBlocks.GREEN_MERCHANT_CARPET,
        MoneyBlocks.LIGHT_BLUE_MERCHANT_CARPET,
        MoneyBlocks.LIGHT_GRAY_MERCHANT_CARPET,
        MoneyBlocks.LIME_MERCHANT_CARPET,
        MoneyBlocks.MAGENTA_MERCHANT_CARPET,
        MoneyBlocks.ORANGE_MERCHANT_CARPET,
        MoneyBlocks.PINK_MERCHANT_CARPET,
        MoneyBlocks.PURPLE_MERCHANT_CARPET,
        MoneyBlocks.RED_MERCHANT_CARPET,
        MoneyBlocks.WHITE_MERCHANT_CARPET,
        MoneyBlocks.YELLOW_MERCHANT_CARPET
    );
    public static final ResourceKey<VillagerProfession> INVESTOR_KEY = ResourceKey.of(Registries.VILLAGER_PROFESSION, Identifier.of(MoneyTalks.MOD_ID, "investor"));
    public static final VillagerProfession INVESTOR = registerProfession("investor", INVESTOR_POI_KEY);

    private static VillagerProfession registerProfession(String name, ResourceKey<PoiType> type) {
        return Registry.register(BuiltInRegistries.VILLAGER_PROFESSION, Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, name),
                new VillagerProfession(Component.literal(name), entry -> entry.matchesKey(type), entry -> entry.matchesKey(type),
                        ImmutableSet.of(), ImmutableSet.of(), SoundEvents.VILLAGER_WORK_LIBRARIAN));
    }

    private static PoiType registerPOI(String name, Block... blocks) {
        return PointOfInterestHelper.register(Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, name), 1, 1, blocks);
    }

    private static ResourceKey<PoiType> registerPoiKey(String name) {
        return ResourceKey.create(Registries.POINT_OF_INTEREST_TYPE, Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, name));
    }

    public static void registerVillagers() {
        MoneyTalks.LOGGER.info("Registering Villagers for " + MoneyTalks.MOD_ID);
    }
}
