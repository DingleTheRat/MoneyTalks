package net.dingletherat.block.entity;

import net.dingletherat.MoneyTalks;
import net.dingletherat.block.MoneyBlocks;
import net.dingletherat.block.entity.custom.MerchantCarpetEntity;
import net.dingletherat.block.entity.custom.TrimmedHopperEntity;
import net.dingletherat.block.entity.custom.DoubloonCompressorEntity;
import net.dingletherat.block.entity.custom.DoubleTrimmedHopperEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.Identifier;

public class MoneyBlockEntities {
        public static final BlockEntityType<MerchantCarpetEntity> MERCHANT_CARPET_ENTITY =
                Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE,
                    Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, "merchant_carpet_entity"),
                    FabricBlockEntityTypeBuilder.<MerchantCarpetEntity>create(MerchantCarpetEntity::new,
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
                        (Block) MoneyBlocks.YELLOW_MERCHANT_CARPET)
                    .build());

    public static final BlockEntityType<TrimmedHopperEntity> TRIMMED_HOPPER_ENTITY =
            Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, "trimmed_hopper_entity"),
                    FabricBlockEntityTypeBuilder.<TrimmedHopperEntity>create(TrimmedHopperEntity::new, (Block) MoneyBlocks.TRIMMED_HOPPER).build());
    public static final BlockEntityType<DoubleTrimmedHopperEntity> DOUBLE_TRIMMED_HOPPER_ENTITY =
            Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, "double_trimmed_hopper_entity"),
                    FabricBlockEntityTypeBuilder.<DoubleTrimmedHopperEntity>create(DoubleTrimmedHopperEntity::new, (Block) MoneyBlocks.DOUBLE_TRIMMED_HOPPER).build());
    public static final BlockEntityType<DoubloonCompressorEntity> DOUBLOON_COMPRESSOR_ENTITY =
            Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, "doubloon_compressor_entity"),
                    FabricBlockEntityTypeBuilder.<DoubloonCompressorEntity>create(DoubloonCompressorEntity::new, (Block) MoneyBlocks.DOUBLOON_COMPRESSOR).build());

    public static void registerBlockEntities() {
        MoneyTalks.LOGGER.info("Registering Block Entities for " + MoneyTalks.MOD_ID);
    }
}
