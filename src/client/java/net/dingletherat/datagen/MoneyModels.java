package net.dingletherat.datagen;

import java.util.Optional;

import net.dingletherat.MoneyTalks;
import net.dingletherat.block.MoneyBlocks;
import net.dingletherat.item.MoneyItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.resources.Identifier;

public class MoneyModels extends FabricModelProvider{
    public MoneyModels(FabricDataOutput output) {
        super(output);
    }

    private static final ModelTemplate MERCHANT_CARPET_TEMPLATE = new Model(
        Optional.of(Identifier.of(MoneyTalks.MOD_ID, "block/merchant_carpet_base")),
        Optional.empty(),
        TextureSlot.ALL
    );

    private void registerMerchantCarpet(BlockModelGenerators gen, Block block, String textureName) {
        TextureMapping textureMap = TextureMapping.all(Identifier.of(MoneyTalks.MOD_ID, "block/" + textureName));
        Identifier modelId = MERCHANT_CARPET_TEMPLATE.upload(block, textureMap, gen.modelCollector);
        gen.blockStateCollector.accept(BlockModelGenerators.createSingletonBlockState(block,
            BlockModelGenerators.createWeightedVariant(modelId)));
    }

    @Override
    public void generateBlockStateModels(BlockModelGenerators blockStateModelGenerator) {
        registerMerchantCarpet(blockStateModelGenerator, MoneyBlocks.BLACK_MERCHANT_CARPET, "black_merchant_carpet");
        registerMerchantCarpet(blockStateModelGenerator, MoneyBlocks.BLUE_MERCHANT_CARPET, "blue_merchant_carpet");
        registerMerchantCarpet(blockStateModelGenerator, MoneyBlocks.BROWN_MERCHANT_CARPET, "brown_merchant_carpet");
        registerMerchantCarpet(blockStateModelGenerator, MoneyBlocks.CYAN_MERCHANT_CARPET, "cyan_merchant_carpet");
        registerMerchantCarpet(blockStateModelGenerator, MoneyBlocks.GRAY_MERCHANT_CARPET, "gray_merchant_carpet");
        registerMerchantCarpet(blockStateModelGenerator, MoneyBlocks.GREEN_MERCHANT_CARPET, "green_merchant_carpet");
        registerMerchantCarpet(blockStateModelGenerator, MoneyBlocks.LIGHT_BLUE_MERCHANT_CARPET, "light_blue_merchant_carpet");
        registerMerchantCarpet(blockStateModelGenerator, MoneyBlocks.LIGHT_GRAY_MERCHANT_CARPET, "light_gray_merchant_carpet");
        registerMerchantCarpet(blockStateModelGenerator, MoneyBlocks.LIME_MERCHANT_CARPET, "lime_merchant_carpet");
        registerMerchantCarpet(blockStateModelGenerator, MoneyBlocks.MAGENTA_MERCHANT_CARPET, "magenta_merchant_carpet");
        registerMerchantCarpet(blockStateModelGenerator, MoneyBlocks.ORANGE_MERCHANT_CARPET, "orange_merchant_carpet");
        registerMerchantCarpet(blockStateModelGenerator, MoneyBlocks.PINK_MERCHANT_CARPET, "pink_merchant_carpet");
        registerMerchantCarpet(blockStateModelGenerator, MoneyBlocks.PURPLE_MERCHANT_CARPET, "purple_merchant_carpet");
        registerMerchantCarpet(blockStateModelGenerator, MoneyBlocks.RED_MERCHANT_CARPET, "red_merchant_carpet");
        registerMerchantCarpet(blockStateModelGenerator, MoneyBlocks.YELLOW_MERCHANT_CARPET, "yellow_merchant_carpet");
        registerMerchantCarpet(blockStateModelGenerator, MoneyBlocks.WHITE_MERCHANT_CARPET, "white_merchant_carpet");
    }

    @Override
    public void generateItemModels(ItemModelGenerators itemModelGenerator) {
        itemModelGenerator.register(MoneyItems.DOLLAR, ModelTemplates.GENERATED);
        itemModelGenerator.register(MoneyItems.WALLET, ModelTemplates.GENERATED);
    }
}
