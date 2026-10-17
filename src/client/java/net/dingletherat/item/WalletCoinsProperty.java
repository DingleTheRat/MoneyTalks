package net.dingletherat.item;

import com.mojang.serialization.MapCodec;

import net.dingletherat.MoneyTalks;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperties;
import net.minecraft.client.renderer.item.properties.numeric.RangeSelectItemModelProperty;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemStack;

public record WalletCoinsProperty() implements RangeSelectItemModelProperty {
    public static final MapCodec<WalletCoinsProperty> MAP_CODEC = MapCodec.unit(new WalletCoinsProperty());

    @Override
    public float get(ItemStack stack, ClientLevel level, ItemOwner owner, int seed) {
        return stack.getOrDefault(MoneyDataComponents.COINS, 0);
    }

    @Override
    public MapCodec<WalletCoinsProperty> type() {
        return MAP_CODEC;
    }

    public static void register() {
        RangeSelectItemModelProperties.ID_MAPPER.put(Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, "coins"), MAP_CODEC);
    }
}
