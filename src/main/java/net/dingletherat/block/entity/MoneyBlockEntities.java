package net.dingletherat.block.entity;

import net.dingletherat.MoneyTalks;
import net.dingletherat.block.MoneyBlocks;
import net.dingletherat.block.entity.custom.MerchantCarpetEntity;
import net.dingletherat.block.entity.custom.TrimmedHopperEntity;
import net.dingletherat.block.entity.custom.DoubloonCompressorEntity;
import net.dingletherat.block.entity.custom.DoubloonEntity;
import net.dingletherat.block.entity.custom.DoubleTrimmedHopperEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;

public class MoneyBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MoneyTalks.MOD_ID);

        public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MerchantCarpetEntity>> MERCHANT_CARPET_ENTITY =
                BLOCK_ENTITIES.register("merchant_carpet_entity", () -> new BlockEntityType<>(MerchantCarpetEntity::new,
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
                        MoneyBlocks.YELLOW_MERCHANT_CARPET.get()));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<TrimmedHopperEntity>> TRIMMED_HOPPER_ENTITY =
            BLOCK_ENTITIES.register("trimmed_hopper_entity", () -> new BlockEntityType<>(TrimmedHopperEntity::new, MoneyBlocks.TRIMMED_HOPPER.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DoubleTrimmedHopperEntity>> DOUBLE_TRIMMED_HOPPER_ENTITY =
            BLOCK_ENTITIES.register("double_trimmed_hoppper", () -> new BlockEntityType<>(DoubleTrimmedHopperEntity::new, MoneyBlocks.DOUBLE_TRIMMED_HOPPER.get()));
                    

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DoubloonCompressorEntity>> DOUBLOON_COMPRESSOR_ENTITY =
            BLOCK_ENTITIES.register("doubloon_compressor", () -> new BlockEntityType<>(DoubloonCompressorEntity::new, MoneyBlocks.DOUBLOON_COMPRESSOR.get()));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DoubloonEntity>> DOUBLOON_ENTITY =
            BLOCK_ENTITIES.register("doubloon", () -> new BlockEntityType<>(DoubloonEntity::new, MoneyBlocks.DOUBLOON.get()));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
