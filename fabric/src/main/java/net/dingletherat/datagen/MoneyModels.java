package net.dingletherat.datagen;

import net.minecraft.world.item.DyeColor;
import java.util.Optional;

import net.dingletherat.MoneyTalks;
import net.dingletherat.registry.MoneyBlocks;
import net.dingletherat.registry.MoneyItems;
import net.dingletherat.client.item.WalletCoinsProperty;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.ItemModelUtils;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.resources.Identifier;

public class MoneyModels extends FabricModelProvider{
    public MoneyModels(FabricPackOutput output) {
        super(output);
    }

    private static final ModelTemplate MERCHANT_CARPET_TEMPLATE = new ModelTemplate(
        Optional.of(Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, "block/merchant_carpet_base")),
        Optional.empty(),
        TextureSlot.ALL
    );

    private void registerMerchantCarpet(BlockModelGenerators blockModelGenerators, Block block, String textureName) {
        TextureMapping textureMap = new TextureMapping().put(TextureSlot.ALL, TextureMapping.getBlockTexture(block));
        Identifier modelId = MERCHANT_CARPET_TEMPLATE.create(block, textureMap, blockModelGenerators.modelOutput);

        blockModelGenerators.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block, BlockModelGenerators.variant(new Variant(modelId))));
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockModelGenerators) {
        registerMerchantCarpet(blockModelGenerators, MoneyBlocks.CARPETS.get(DyeColor.BLACK.ordinal()).get(), "black_merchant_carpet");
        registerMerchantCarpet(blockModelGenerators, MoneyBlocks.CARPETS.get(DyeColor.BLUE.ordinal()).get(), "blue_merchant_carpet");
        registerMerchantCarpet(blockModelGenerators, MoneyBlocks.CARPETS.get(DyeColor.BROWN.ordinal()).get(), "brown_merchant_carpet");
        registerMerchantCarpet(blockModelGenerators, MoneyBlocks.CARPETS.get(DyeColor.CYAN.ordinal()).get(), "cyan_merchant_carpet");
        registerMerchantCarpet(blockModelGenerators, MoneyBlocks.CARPETS.get(DyeColor.GRAY.ordinal()).get(), "gray_merchant_carpet");
        registerMerchantCarpet(blockModelGenerators, MoneyBlocks.CARPETS.get(DyeColor.GREEN.ordinal()).get(), "green_merchant_carpet");
        registerMerchantCarpet(blockModelGenerators, MoneyBlocks.CARPETS.get(DyeColor.LIGHT_BLUE.ordinal()).get(), "light_blue_merchant_carpet");
        registerMerchantCarpet(blockModelGenerators, MoneyBlocks.CARPETS.get(DyeColor.LIGHT_GRAY.ordinal()).get(), "light_gray_merchant_carpet");
        registerMerchantCarpet(blockModelGenerators, MoneyBlocks.CARPETS.get(DyeColor.LIME.ordinal()).get(), "lime_merchant_carpet");
        registerMerchantCarpet(blockModelGenerators, MoneyBlocks.CARPETS.get(DyeColor.MAGENTA.ordinal()).get(), "magenta_merchant_carpet");
        registerMerchantCarpet(blockModelGenerators, MoneyBlocks.CARPETS.get(DyeColor.ORANGE.ordinal()).get(), "orange_merchant_carpet");
        registerMerchantCarpet(blockModelGenerators, MoneyBlocks.CARPETS.get(DyeColor.PINK.ordinal()).get(), "pink_merchant_carpet");
        registerMerchantCarpet(blockModelGenerators, MoneyBlocks.CARPETS.get(DyeColor.PURPLE.ordinal()).get(), "purple_merchant_carpet");
        registerMerchantCarpet(blockModelGenerators, MoneyBlocks.CARPETS.get(DyeColor.RED.ordinal()).get(), "red_merchant_carpet");
        registerMerchantCarpet(blockModelGenerators, MoneyBlocks.CARPETS.get(DyeColor.YELLOW.ordinal()).get(), "yellow_merchant_carpet");
        registerMerchantCarpet(blockModelGenerators, MoneyBlocks.CARPETS.get(DyeColor.WHITE.ordinal()).get(), "white_merchant_carpet");

        blockModelGenerators.createTrivialBlock((Block) MoneyBlocks.DOUBLOON_COMPRESSOR.get(), TexturedModel.CUBE);
        blockModelGenerators.createTrivialBlock((Block) MoneyBlocks.DOUBLOON.get(), TexturedModel.CUBE);
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerators) {
        itemModelGenerators.generateFlatItem(MoneyItems.COIN.get(), ModelTemplates.FLAT_ITEM);

        ItemModel.Unbaked unbakedEmptyWallet = ItemModelUtils.plainModel(itemModelGenerators.createFlatItemModel(MoneyItems.WALLET.get(), "_empty", ModelTemplates.FLAT_HANDHELD_ITEM));
        ItemModel.Unbaked unbakedHalfFilledWallet = ItemModelUtils.plainModel(itemModelGenerators.createFlatItemModel(MoneyItems.WALLET.get(), "_half_filled", ModelTemplates.FLAT_HANDHELD_ITEM));
        ItemModel.Unbaked unbakedFilledWallet = ItemModelUtils.plainModel(itemModelGenerators.createFlatItemModel(MoneyItems.WALLET.get(), "_filled", ModelTemplates.FLAT_HANDHELD_ITEM));
        itemModelGenerators.itemModelOutput.accept(MoneyItems.WALLET.get(), ItemModelUtils.rangeSelect(new WalletCoinsProperty(), unbakedEmptyWallet,
                ItemModelUtils.override(unbakedHalfFilledWallet, MoneyTalks.TEXTURE_WALLET_HALF_FULL_GOAL),
                ItemModelUtils.override(unbakedFilledWallet, MoneyTalks.TEXTURE_WALLET_FULL_GOAL)));
    }
}
