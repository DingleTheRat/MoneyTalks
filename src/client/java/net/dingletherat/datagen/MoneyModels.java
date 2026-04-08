package net.dingletherat.datagen;

import java.util.Optional;

import net.dingletherat.MoneyTalks;
import net.dingletherat.block.MoneyBlocks;
import net.dingletherat.item.MoneyItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TexturedModel;
import net.minecraft.client.data.models.model.TextureMapping;
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

    private void registerMerchantCarpet(BlockModelGenerators gen, Block block, String textureName) {
        TextureMapping textureMap = new TextureMapping().put(TextureSlot.ALL, TextureMapping.getBlockTexture(block));
        Identifier modelId = MERCHANT_CARPET_TEMPLATE.create(block, textureMap, gen.modelOutput);

        gen.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block,
            BlockModelGenerators.variant(new Variant(modelId))));
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        blockStateModelGenerator.createTrivialBlock((Block) MoneyBlocks.DABLOON_COMPRESSOR, TexturedModel.CUBE);
        registerMerchantCarpet(blockStateModelGenerator, (Block) MoneyBlocks.BLACK_MERCHANT_CARPET, "black_merchant_carpet");
        registerMerchantCarpet(blockStateModelGenerator, (Block) MoneyBlocks.BLUE_MERCHANT_CARPET, "blue_merchant_carpet");
        registerMerchantCarpet(blockStateModelGenerator, (Block) MoneyBlocks.BROWN_MERCHANT_CARPET, "brown_merchant_carpet");
        registerMerchantCarpet(blockStateModelGenerator, (Block) MoneyBlocks.CYAN_MERCHANT_CARPET, "cyan_merchant_carpet");
        registerMerchantCarpet(blockStateModelGenerator, (Block) MoneyBlocks.GRAY_MERCHANT_CARPET, "gray_merchant_carpet");
        registerMerchantCarpet(blockStateModelGenerator, (Block) MoneyBlocks.GREEN_MERCHANT_CARPET, "green_merchant_carpet");
        registerMerchantCarpet(blockStateModelGenerator, (Block) MoneyBlocks.LIGHT_BLUE_MERCHANT_CARPET, "light_blue_merchant_carpet");
        registerMerchantCarpet(blockStateModelGenerator, (Block) MoneyBlocks.LIGHT_GRAY_MERCHANT_CARPET, "light_gray_merchant_carpet");
        registerMerchantCarpet(blockStateModelGenerator, (Block) MoneyBlocks.LIME_MERCHANT_CARPET, "lime_merchant_carpet");
        registerMerchantCarpet(blockStateModelGenerator, (Block) MoneyBlocks.MAGENTA_MERCHANT_CARPET, "magenta_merchant_carpet");
        registerMerchantCarpet(blockStateModelGenerator, (Block) MoneyBlocks.ORANGE_MERCHANT_CARPET, "orange_merchant_carpet");
        registerMerchantCarpet(blockStateModelGenerator, (Block) MoneyBlocks.PINK_MERCHANT_CARPET, "pink_merchant_carpet");
        registerMerchantCarpet(blockStateModelGenerator, (Block) MoneyBlocks.PURPLE_MERCHANT_CARPET, "purple_merchant_carpet");
        registerMerchantCarpet(blockStateModelGenerator, (Block) MoneyBlocks.RED_MERCHANT_CARPET, "red_merchant_carpet");
        registerMerchantCarpet(blockStateModelGenerator, (Block) MoneyBlocks.YELLOW_MERCHANT_CARPET, "yellow_merchant_carpet");
        registerMerchantCarpet(blockStateModelGenerator, (Block) MoneyBlocks.WHITE_MERCHANT_CARPET, "white_merchant_carpet");
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
        itemModelGenerator.generateFlatItem(MoneyItems.DOLLAR, ModelTemplates.FLAT_ITEM);
        itemModelGenerator.generateFlatItem(MoneyItems.WALLET, ModelTemplates.FLAT_ITEM);
    }
}
