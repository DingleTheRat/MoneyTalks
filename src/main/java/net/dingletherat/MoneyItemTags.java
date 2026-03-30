package net.dingletherat;

import net.minecraft.world.item.Item;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.resources.Identifier;

public class MoneyItemTags {
	public static final TagKey<Item> MERCHANT_CARPETS = createTag("merchant_carpets");

	private static TagKey<Item> createTag(String name) {
		return TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, name));
	}
}
