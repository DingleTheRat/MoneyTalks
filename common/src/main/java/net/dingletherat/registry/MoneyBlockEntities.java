package net.dingletherat.registry;

import java.util.Arrays;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.dingletherat.MoneyTalks;
import net.dingletherat.block.entity.MerchantCarpetEntity;
import net.dingletherat.block.entity.TrimmedHopperEntity;
import net.dingletherat.block.entity.DoubloonCompressorEntity;
import net.dingletherat.block.entity.DoubloonEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.registries.Registries;

public class MoneyBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(MoneyTalks.MOD_ID, Registries.BLOCK_ENTITY_TYPE);

    public static final RegistrySupplier<BlockEntityType<MerchantCarpetEntity>> MERCHANT_CARPET_ENTITY = registerBlockEntity("merchant_carpet_entity", MerchantCarpetEntity::new, MoneyBlocks.CARPETS.toArray(new RegistrySupplier[0]));
    public static final RegistrySupplier<BlockEntityType<TrimmedHopperEntity>> TRIMMED_HOPPER_ENTITY = registerBlockEntity("trimmed_hopper_entity", TrimmedHopperEntity::new, MoneyBlocks.TRIMMED_HOPPER);
    public static final RegistrySupplier<BlockEntityType<DoubloonCompressorEntity>> DOUBLOON_COMPRESSOR_ENTITY = registerBlockEntity("doubloon_compressor_entity", (position, state) -> new DoubloonCompressorEntity(position, state), MoneyBlocks.DOUBLOON_COMPRESSOR);
    public static final RegistrySupplier<BlockEntityType<DoubloonEntity>> DOUBLOON_ENTITY = registerBlockEntity("doubloon_entity", (position, state) -> new DoubloonEntity(position, state), MoneyBlocks.DOUBLOON);

    @SafeVarargs
    public static <T extends BlockEntity> RegistrySupplier<BlockEntityType<T>> registerBlockEntity(String name, BlockEntityType.BlockEntitySupplier<T> supplier, Supplier<? extends Block>... blocks) {
        return BLOCK_ENTITIES.register(name, () -> new BlockEntityType<T>(supplier, Arrays.stream(blocks).map(Supplier::get).collect(Collectors.toSet())));
    }

    public static void initialize() {
        MoneyTalks.LOGGER.info("Initializing Block Entities for " + MoneyTalks.MOD_ID);
        BLOCK_ENTITIES.register();
    }
}
