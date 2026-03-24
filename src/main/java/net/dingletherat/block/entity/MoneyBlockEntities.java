package net.dingletherat.block.entity;

import net.dingletherat.MoneyTalks;
import net.dingletherat.block.MoneyBlocks;
import net.dingletherat.block.entity.custom.MerchantCarpetEntity;
import net.dingletherat.block.entity.custom.TrimmedHopperEntity;
import net.dingletherat.block.entity.custom.DoubleTrimmedHopperEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class MoneyBlockEntities {
    public static final BlockEntityType<MerchantCarpetEntity> MERCHANT_CARPET_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(MoneyTalks.MOD_ID, "merchant_carpet_entity"),
                    FabricBlockEntityTypeBuilder.<MerchantCarpetEntity>create(MerchantCarpetEntity::new,
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
                        MoneyBlocks.YELLOW_MERCHANT_CARPET)
                    .build());

    public static final BlockEntityType<TrimmedHopperEntity> TRIMMED_HOPPER_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(MoneyTalks.MOD_ID, "trimmed_hopper_entity"),
                    FabricBlockEntityTypeBuilder.<TrimmedHopperEntity>create(TrimmedHopperEntity::new, MoneyBlocks.TRIMMED_HOPPER).build());
    public static final BlockEntityType<DoubleTrimmedHopperEntity> DOUBLE_TRIMMED_HOPPER_ENTITY =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(MoneyTalks.MOD_ID, "double_trimmed_hopper_entity"),
                    FabricBlockEntityTypeBuilder.<DoubleTrimmedHopperEntity>create(DoubleTrimmedHopperEntity::new, MoneyBlocks.DOUBLE_TRIMMED_HOPPER).build());

    public static void registerBlockEntities() {
        MoneyTalks.LOGGER.info("Registering Block Entities for " + MoneyTalks.MOD_ID);
    }
}
