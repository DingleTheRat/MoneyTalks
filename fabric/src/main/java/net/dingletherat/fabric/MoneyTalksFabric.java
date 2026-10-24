package net.dingletherat.fabric;

import net.dingletherat.MoneyTalks;
import net.fabricmc.api.ModInitializer;

public class MoneyTalksFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        MoneyTalks.initialize();
    }
}
