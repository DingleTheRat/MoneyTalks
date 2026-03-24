package net.dingletherat;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public class MoneyItemTags {
	public static final TagKey<Item> MERCHANT_CARPETS = createTag("merchant_carpets");

	private static TagKey<Item> createTag(String name) {
		return TagKey.of(RegistryKeys.ITEM, Identifier.of(MoneyTalks.MOD_ID, name));
	}
}
