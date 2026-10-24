package net.dingletherat.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.mojang.math.Transformation;
import net.minecraft.world.entity.Display;

@Mixin(Display.class)
public interface DisplayAccessor {
    @Invoker("setTransformation")
    void moneytalks$setTransformation(Transformation transformation);
}
