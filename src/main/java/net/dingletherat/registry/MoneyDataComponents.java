package net.dingletherat.registry;

import java.util.UUID;
import java.util.function.UnaryOperator;

import net.dingletherat.MoneyTalks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.component.ResolvableProfile;

public class MoneyDataComponents {
	// General
	public static final DataComponentType<BlockPos> COORDINATES = register("coordinates", builder -> builder.persistent(BlockPos.CODEC).networkSynchronized(BlockPos.STREAM_CODEC));

	// Wallets (note: ResolvableProfile stores UUID and name of a player, quite cool :D)
	public static final DataComponentType<Integer> COINS = register("coins", builder -> builder.persistent(ExtraCodecs.NON_NEGATIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT));
	public static final DataComponentType<ResolvableProfile> OWNER = register("owner", builder -> builder.persistent(ResolvableProfile.CODEC).networkSynchronized(ResolvableProfile.STREAM_CODEC));
	public static final DataComponentType<UUID> WALLET_ID = register("wallet_id", builder -> builder.persistent(UUIDUtil.CODEC).networkSynchronized(UUIDUtil.STREAM_CODEC));

	private static <T> DataComponentType<T> register(String name, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
		return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, name), builderOperator.apply(DataComponentType.builder()).build());
	}

	public static void registerDataComponents() {
		MoneyTalks.LOGGER.info("Registering Data Components for " + MoneyTalks.MOD_ID);
	}
}
