package net.dingletherat.datagen;

import java.util.Optional;

import net.dingletherat.MoneyTalks;
import net.dingletherat.block.MoneyBlocks;
import net.dingletherat.item.MoneyItems;
import net.dingletherat.item.WalletCoinsProperty;
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
        registerMerchantCarpet(blockModelGenerators, (Block) MoneyBlocks.BLACK_MERCHANT_CARPET, "black_merchant_carpet");
        registerMerchantCarpet(blockModelGenerators, (Block) MoneyBlocks.BLUE_MERCHANT_CARPET, "blue_merchant_carpet");
        registerMerchantCarpet(blockModelGenerators, (Block) MoneyBlocks.BROWN_MERCHANT_CARPET, "brown_merchant_carpet");
        registerMerchantCarpet(blockModelGenerators, (Block) MoneyBlocks.CYAN_MERCHANT_CARPET, "cyan_merchant_carpet");
        registerMerchantCarpet(blockModelGenerators, (Block) MoneyBlocks.GRAY_MERCHANT_CARPET, "gray_merchant_carpet");
        registerMerchantCarpet(blockModelGenerators, (Block) MoneyBlocks.GREEN_MERCHANT_CARPET, "green_merchant_carpet");
        registerMerchantCarpet(blockModelGenerators, (Block) MoneyBlocks.LIGHT_BLUE_MERCHANT_CARPET, "light_blue_merchant_carpet");
        registerMerchantCarpet(blockModelGenerators, (Block) MoneyBlocks.LIGHT_GRAY_MERCHANT_CARPET, "light_gray_merchant_carpet");
        registerMerchantCarpet(blockModelGenerators, (Block) MoneyBlocks.LIME_MERCHANT_CARPET, "lime_merchant_carpet");
        registerMerchantCarpet(blockModelGenerators, (Block) MoneyBlocks.MAGENTA_MERCHANT_CARPET, "magenta_merchant_carpet");
        registerMerchantCarpet(blockModelGenerators, (Block) MoneyBlocks.ORANGE_MERCHANT_CARPET, "orange_merchant_carpet");
        registerMerchantCarpet(blockModelGenerators, (Block) MoneyBlocks.PINK_MERCHANT_CARPET, "pink_merchant_carpet");
        registerMerchantCarpet(blockModelGenerators, (Block) MoneyBlocks.PURPLE_MERCHANT_CARPET, "purple_merchant_carpet");
        registerMerchantCarpet(blockModelGenerators, (Block) MoneyBlocks.RED_MERCHANT_CARPET, "red_merchant_carpet");
        registerMerchantCarpet(blockModelGenerators, (Block) MoneyBlocks.YELLOW_MERCHANT_CARPET, "yellow_merchant_carpet");
        registerMerchantCarpet(blockModelGenerators, (Block) MoneyBlocks.WHITE_MERCHANT_CARPET, "white_merchant_carpet");

        blockModelGenerators.createTrivialBlock((Block) MoneyBlocks.DOUBLOON_COMPRESSOR, TexturedModel.CUBE);
        blockModelGenerators.createTrivialBlock((Block) MoneyBlocks.DOUBLOON, TexturedModel.CUBE);
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerators) {
        itemModelGenerators.generateFlatItem(MoneyItems.COIN, ModelTemplates.FLAT_ITEM);

        ItemModel.Unbaked unbakedEmptyWallet = ItemModelUtils.plainModel(itemModelGenerators.createFlatItemModel(MoneyItems.WALLET, "_empty", ModelTemplates.FLAT_HANDHELD_ITEM));
        ItemModel.Unbaked unbakedHalfFilledWallet = ItemModelUtils.plainModel(itemModelGenerators.createFlatItemModel(MoneyItems.WALLET, "_half_filled", ModelTemplates.FLAT_HANDHELD_ITEM));
        ItemModel.Unbaked unbakedFilledWallet = ItemModelUtils.plainModel(itemModelGenerators.createFlatItemModel(MoneyItems.WALLET, "_filled", ModelTemplates.FLAT_HANDHELD_ITEM));
        itemModelGenerators.itemModelOutput.accept(MoneyItems.WALLET, ItemModelUtils.rangeSelect(new WalletCoinsProperty(), unbakedEmptyWallet,
                ItemModelUtils.override(unbakedHalfFilledWallet, MoneyTalks.TEXTURE_WALLET_HALF_FULL_GOAL),
                ItemModelUtils.override(unbakedFilledWallet, MoneyTalks.TEXTURE_WALLET_FULL_GOAL)));
    }
}
