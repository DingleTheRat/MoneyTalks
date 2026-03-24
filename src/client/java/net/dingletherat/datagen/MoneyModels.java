package net.dingletherat.datagen;

import java.util.Optional;

import net.dingletherat.MoneyTalks;
import net.dingletherat.block.MoneyBlocks;
import net.dingletherat.item.MoneyItems;
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.block.Block;
import net.minecraft.client.data.BlockStateModelGenerator;
import net.minecraft.client.data.ItemModelGenerator;
import net.minecraft.client.data.Models;
import net.minecraft.client.data.TextureKey;
import net.minecraft.client.data.TextureMap;
import net.minecraft.client.data.Model;
import net.minecraft.util.Identifier;

public class MoneyModels extends FabricModelProvider{
    public MoneyModels(FabricDataOutput output) {
        super(output);
    }

    private static final Model MERCHANT_CARPET_TEMPLATE = new Model(
        Optional.of(Identifier.of(MoneyTalks.MOD_ID, "block/merchant_carpet_base")),
        Optional.empty(),
        TextureKey.ALL
    );

    private void registerMerchantCarpet(BlockStateModelGenerator gen, Block block, String textureName) {
        TextureMap textureMap = TextureMap.all(Identifier.of(MoneyTalks.MOD_ID, "block/" + textureName));
        Identifier modelId = MERCHANT_CARPET_TEMPLATE.upload(block, textureMap, gen.modelCollector);
        gen.blockStateCollector.accept(BlockStateModelGenerator.createSingletonBlockState(block, 
            BlockStateModelGenerator.createWeightedVariant(modelId)));
    }

    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
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
    public void generateItemModels(ItemModelGenerator itemModelGenerator) {
        itemModelGenerator.register(MoneyItems.DOLLAR, Models.GENERATED);
        itemModelGenerator.register(MoneyItems.WALLET, Models.GENERATED);
    }
}
