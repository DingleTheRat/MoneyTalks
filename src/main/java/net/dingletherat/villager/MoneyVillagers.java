package net.dingletherat.villager;

import com.google.common.collect.ImmutableSet;

import net.dingletherat.MoneyTalks;
import net.dingletherat.block.MoneyBlocks;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.poi.PointOfInterestType;

public class MoneyVillagers {
    public static final RegistryKey<PointOfInterestType> INVESTOR_POI_KEY = registerPoiKey("investor_poi");
    public static final PointOfInterestType INVESTOR_POI = registerPOI("investor_poi",
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
    public static final RegistryKey<VillagerProfession> INVESTOR_KEY = RegistryKey.of(RegistryKeys.VILLAGER_PROFESSION, Identifier.of(MoneyTalks.MOD_ID, "investor"));
    public static final VillagerProfession INVESTOR = registerProfession("investor", INVESTOR_POI_KEY);

    private static VillagerProfession registerProfession(String name, RegistryKey<PointOfInterestType> type) {
        return Registry.register(Registries.VILLAGER_PROFESSION, Identifier.of(MoneyTalks.MOD_ID, name),
                new VillagerProfession(Text.literal(name), entry -> entry.matchesKey(type), entry -> entry.matchesKey(type),
                        ImmutableSet.of(), ImmutableSet.of(), SoundEvents.ENTITY_VILLAGER_WORK_LIBRARIAN));
    }

    private static PointOfInterestType registerPOI(String name, Block... blocks) {
        return PointOfInterestHelper.register(Identifier.of(MoneyTalks.MOD_ID, name), 1, 1, blocks);
    }

    private static RegistryKey<PointOfInterestType> registerPoiKey(String name) {
        return RegistryKey.of(RegistryKeys.POINT_OF_INTEREST_TYPE, Identifier.of(MoneyTalks.MOD_ID, name));
    }

    public static void registerVillagers() {
        MoneyTalks.LOGGER.info("Registering Villagers for " + MoneyTalks.MOD_ID);
    }
}
