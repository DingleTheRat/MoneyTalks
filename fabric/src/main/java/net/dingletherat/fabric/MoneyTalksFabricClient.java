package net.dingletherat.fabric;

import net.dingletherat.client.MoneyTalksClient;
import net.fabricmc.api.ClientModInitializer;

public class MoneyTalksFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        MoneyTalksClient.initialize();
    }
}
