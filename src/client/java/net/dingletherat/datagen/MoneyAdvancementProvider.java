package net.dingletherat.datagen;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import net.dingletherat.MoneyTalks;
import net.dingletherat.block.MoneyBlocks;
import net.dingletherat.item.MoneyDataComponents;
import net.dingletherat.item.MoneyItems;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.advancements.triggers.ImpossibleTrigger;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;

public class MoneyAdvancementProvider extends FabricAdvancementProvider {
    public MoneyAdvancementProvider(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
    }

    @Override
    public void generateAdvancement(Provider registryLookup, Consumer<AdvancementHolder> consumer) {
        var items = registryLookup.lookupOrThrow(Registries.ITEM);
        var blocks = registryLookup.lookupOrThrow(Registries.BLOCK);

        // Assets
        ItemStackTemplate fueledCompressor = new ItemStackTemplate(MoneyBlocks.DOUBLOON_COMPRESSOR.asItem(), DataComponentPatch.builder()
            .set(DataComponents.ITEM_MODEL, Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, "doubloon_compressor_fueled")).build());
        ItemStackTemplate glintHalfFullWallet = new ItemStackTemplate(MoneyItems.WALLET, DataComponentPatch.builder()
            .set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true).set(MoneyDataComponents.COINS, 1).build());
        ItemStackTemplate halfFullWallet = new ItemStackTemplate(MoneyItems.WALLET, DataComponentPatch.builder()
            .set(MoneyDataComponents.COINS, MoneyTalks.TEXTURE_WALLET_HALF_FULL_GOAL).build());
        ItemStackTemplate fullWallet = new ItemStackTemplate(MoneyItems.WALLET, DataComponentPatch.builder()
            .set(MoneyDataComponents.COINS, MoneyTalks.TEXTURE_WALLET_FULL_GOAL).build());
        ItemStackTemplate glintCoin = new ItemStackTemplate(MoneyItems.COIN, DataComponentPatch.builder()
            .set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true).build());
        ItemStackTemplate glintDoubloon = new ItemStackTemplate(MoneyBlocks.DOUBLOON.asItem(), DataComponentPatch.builder()
            .set(DataComponents.ENCHANTMENT_GLINT_OVERRIDE, true).build());

        // Root
        AdvancementHolder moneyTalks = Advancement.Builder.advancement()
            .rootDisplay(MoneyItems.COIN, Component.translatable("advancements.moneytalks.money_talks.title"), Component.translatable("advancements.moneytalks.money_talks.description"),
                    Identifier.withDefaultNamespace("gui/advancements/backgrounds/adventure"),
                    AdvancementType.TASK, true, true, false)
            .addCriterion("has_coin", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(items, MoneyItems.COIN)))
            .save(consumer, Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, "money_talks"));

        // Given via code in the Wallet
        AdvancementHolder pocketChange = Advancement.Builder.advancement()
            .parent(moneyTalks)
            .display(halfFullWallet, Component.translatable("advancements.moneytalks.pocket_change.title"), Component.translatable("advancements.moneytalks.pocket_change.description"),
                    AdvancementType.TASK, true, true, false)
            .addCriterion("inserted_coin", CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
            .save(consumer, Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, "pocket_change"));

        // -------------------------
        // Main Path 
        // -------------------------

        // Given via code in the MerchantCarpetEntity
        AdvancementHolder noRefunds = Advancement.Builder.advancement()
            .parent(pocketChange)
            .display(Items.BUNDLE, Component.translatable("advancements.moneytalks.no_refunds.title"), Component.translatable("advancements.moneytalks.no_refunds.description"),
                    AdvancementType.TASK, true, true, false)
            .addCriterion("bought_something", CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
            .save(consumer, Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, "no_refunds"));

        // Given via code in the Wallet
        AdvancementHolder deepPockets = Advancement.Builder.advancement()
            .parent(noRefunds)
            .display(fullWallet, Component.translatable("advancements.moneytalks.deep_pockets.title"), Component.translatable("advancements.moneytalks.deep_pockets.description"),
                    AdvancementType.CHALLENGE, true, true, false)
            .addCriterion("inserted_" + MoneyTalks.ADVANCEMENT_DEEP_POCKETS_GOAL + "_coins", CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
            .save(consumer, Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, "deep_pockets"));

        // -------------------------
        // Shop Owner Path 
        // -------------------------

        // Given via code in the MerchantCarpetEntity
        AdvancementHolder openForBusiness = Advancement.Builder.advancement()
            .parent(pocketChange)
            .display(MoneyBlocks.RED_MERCHANT_CARPET.asItem(), Component.translatable("advancements.moneytalks.open_for_business.title"), Component.translatable("advancements.moneytalks.open_for_business.description"),
                    AdvancementType.TASK, true, true, false)
            .addCriterion("setup_merchant_carpet", CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
            .save(consumer, Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, "open_for_business"));

        // Given via code in the MerchantCarpetEntity
        AdvancementHolder directDeposit = Advancement.Builder.advancement()
            .parent(openForBusiness)
            .display(glintHalfFullWallet, Component.translatable("advancements.moneytalks.direct_deposit.title"), Component.translatable("advancements.moneytalks.direct_deposit.description"),
                    AdvancementType.TASK, true, true, false)
            .addCriterion("setup_auto_deposit", CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
            .save(consumer, Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, "direct_deposit"));

        // Given via code in the MerchantCarpetEntity
        AdvancementHolder pleasureDoingBusiness = Advancement.Builder.advancement()
            .parent(directDeposit)
            .display(glintCoin, Component.translatable("advancements.moneytalks.pleasure_doing_business.title"), Component.translatable("advancements.moneytalks.pleasure_doing_business.description"),
                    AdvancementType.GOAL, true, true, false)
            .addCriterion("something_bought", CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
            .save(consumer, Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, "pleasure_doing_business"));

        // Given via code in the MerchantCarpetEntity
        AdvancementHolder monopoly = Advancement.Builder.advancement()
            .parent(pleasureDoingBusiness)
            .display(Items.GOLD_BLOCK, Component.translatable("advancements.moneytalks.monopoly.title"), Component.translatable("advancements.moneytalks.monopoly.description"),
                    AdvancementType.CHALLENGE, true, true, false)
            .addCriterion("bought_" + MoneyTalks.ADVANCEMENT_MONOPOLY_GOAL, CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
            .save(consumer, Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, "monopoly"));

        // -------------------------
        // Doubloon Path
        // -------------------------

        // Given via code in the DoubloonCompressorEntity
        AdvancementHolder underPressure = Advancement.Builder.advancement()
            .parent(pocketChange)
            .display(fueledCompressor, Component.translatable("advancements.moneytalks.under_pressure.title"), Component.translatable("advancements.moneytalks.under_pressure.description"),
                    AdvancementType.TASK, true, true, false)
            .addCriterion("fueled_compressor", CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
            .save(consumer, Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, "under_pressure"));

        AdvancementHolder arrMatey = Advancement.Builder.advancement()
            .parent(underPressure)
            .display(MoneyBlocks.DOUBLOON.asItem(), Component.translatable("advancements.moneytalks.arr_matey.title"), Component.translatable("advancements.moneytalks.arr_matey.description"),
                    AdvancementType.GOAL, true, true, false)
            .addCriterion("has_doubloon", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(items, MoneyBlocks.DOUBLOON.asItem())))
            .save(consumer, Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, "arr_matey"));

        // Given via code in the InventoryChangeTriggerMixin
        AdvancementHolder richestPirate = Advancement.Builder.advancement()
            .parent(arrMatey)
            .display(glintDoubloon, Component.translatable("advancements.moneytalks.richest_pirate.title"), Component.translatable("advancements.moneytalks.richest_pirate.description"),
                    AdvancementType.CHALLENGE, true, true, false)
            .addCriterion("has_" + MoneyTalks.ADVANCEMENT_RICHEST_PIRATE_GOAL + "_doubloons", CriteriaTriggers.IMPOSSIBLE.createCriterion(new ImpossibleTrigger.TriggerInstance()))
            .save(consumer, Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, "richest_pirate"));
    }
}
