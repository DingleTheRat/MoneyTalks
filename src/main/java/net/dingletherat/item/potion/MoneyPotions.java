package net.dingletherat.item.potion;

import net.dingletherat.MoneyTalks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class MoneyPotions {
    public static final DeferredRegister<Potion> POTIONS =
        DeferredRegister.create(Registries.POTION, MoneyTalks.MOD_ID);

    public static final DeferredHolder<Potion, Potion> GLOWING_POTION = POTIONS.register("glowing_potion", () -> new Potion("glowing_potion", new MobEffectInstance(MobEffects.GLOWING, 1200, 0)));

    public static void register(IEventBus modEventBus) {
        POTIONS.register(modEventBus);
    }
}
