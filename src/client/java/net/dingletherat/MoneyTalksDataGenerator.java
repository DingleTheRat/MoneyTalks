package net.dingletherat;

import net.dingletherat.datagen.MoneyBlockTagProvider;
import net.dingletherat.datagen.MoneyItemTagProvider;
import net.dingletherat.datagen.MoneyLootTableProvider;
import net.dingletherat.datagen.MoneyModels;
import net.dingletherat.datagen.MoneyRecipeProvider;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class MoneyTalksDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();

		pack.addProvider(MoneyModels::new);
		pack.addProvider(MoneyItemTagProvider::new);
		pack.addProvider(MoneyBlockTagProvider::new);
		pack.addProvider(MoneyRecipeProvider::new);
		pack.addProvider(MoneyLootTableProvider::new);
	}
}
