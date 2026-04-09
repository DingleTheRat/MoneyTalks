package net.dingletherat.item.potion;

import net.dingletherat.MoneyTalks;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.alchemy.Potion;

public class MoneyPotions {
    public static final Potion SLIMEY_POTION = registerPotion("glowing_potion",
            new Potion("glowing_potion", new MobEffectInstance(MobEffects.GLOWING, 1200, 0)));


    private static Potion registerPotion(String name, Potion potion) {
        return Registry.register(BuiltInRegistries.POTION, Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, name), potion);
    }

    public static void registerPotions() {
        MoneyTalks.LOGGER.info("Registering Mod Potions for " + MoneyTalks.MOD_ID);
    }
}
