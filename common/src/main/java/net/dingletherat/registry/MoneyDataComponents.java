package net.dingletherat.registry;

import java.util.UUID;
import java.util.function.UnaryOperator;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.dingletherat.MoneyTalks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.component.ResolvableProfile;

public class MoneyDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(MoneyTalks.MOD_ID, Registries.DATA_COMPONENT_TYPE);

    // General
    public static final RegistrySupplier<DataComponentType<BlockPos>> COORDINATES = registerDataComponent("coordinates", builder -> builder.persistent(BlockPos.CODEC).networkSynchronized(BlockPos.STREAM_CODEC));

    // Wallets (note: ResolvableProfile stores UUID and name of a player, quite cool :D)
    public static final RegistrySupplier<DataComponentType<Integer>> COINS = registerDataComponent("coins", builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT));
    public static final RegistrySupplier<DataComponentType<ResolvableProfile>> OWNER = registerDataComponent("owner", builder -> builder.persistent(ResolvableProfile.CODEC).networkSynchronized(ResolvableProfile.STREAM_CODEC));
    public static final RegistrySupplier<DataComponentType<UUID>> WALLET_ID = registerDataComponent("wallet_id", builder -> builder.persistent(UUIDUtil.CODEC).networkSynchronized(UUIDUtil.STREAM_CODEC));

    public static <T> RegistrySupplier<DataComponentType<T>> registerDataComponent(String name, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return DATA_COMPONENTS.register(name, () -> builderOperator.apply(DataComponentType.builder()).build());
    }

    public static void initialize() {
        MoneyTalks.LOGGER.info("Initializing Data Components for " + MoneyTalks.MOD_ID);
        DATA_COMPONENTS.register();
    }
}
