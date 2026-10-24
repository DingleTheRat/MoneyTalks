package net.dingletherat.neoforge;

import net.dingletherat.MoneyTalks;
import net.neoforged.fml.common.Mod;

@Mod(MoneyTalks.MOD_ID)
public final class MoneyTalksNeoForge {
    public MoneyTalksNeoForge() {
        MoneyTalks.initialize();
    }
}
