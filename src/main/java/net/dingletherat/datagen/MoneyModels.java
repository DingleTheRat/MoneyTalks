package net.dingletherat.datagen;

import net.dingletherat.MoneyTalks;
import net.dingletherat.block.MoneyBlocks;
import net.dingletherat.item.MoneyItems;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.MultiVariant;
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.client.data.models.blockstates.PropertyDispatch;
import net.minecraft.client.data.models.model.ModelTemplate;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.client.data.models.model.TextureMapping;
import net.minecraft.client.data.models.model.TextureSlot;
import net.minecraft.client.renderer.block.dispatch.Variant;
import net.minecraft.core.Direction;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.Optional;

import com.mojang.math.Quadrant;

public class MoneyModels extends ModelProvider {
    public MoneyModels(PackOutput output) {
        super(output, MoneyTalks.MOD_ID);
    }

    private static final ModelTemplate MERCHANT_CARPET_TEMPLATE = new ModelTemplate(
        Optional.of(Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, "block/merchant_carpet_base")),
        Optional.empty(),
        TextureSlot.ALL
    );

    private void registerMerchantCarpet(BlockModelGenerators gen, Block block) {
        TextureMapping textureMap = new TextureMapping().put(TextureSlot.ALL, TextureMapping.getBlockTexture(block));
        Identifier modelId = MERCHANT_CARPET_TEMPLATE.create(block, textureMap, gen.modelOutput);
        gen.blockStateOutput.accept(BlockModelGenerators.createSimpleBlock(block, BlockModelGenerators.plainVariant(modelId)));
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        registerMerchantCarpet(blockModels, MoneyBlocks.BLACK_MERCHANT_CARPET.get());
        registerMerchantCarpet(blockModels, MoneyBlocks.BLUE_MERCHANT_CARPET.get());
        registerMerchantCarpet(blockModels, MoneyBlocks.BROWN_MERCHANT_CARPET.get());
        registerMerchantCarpet(blockModels, MoneyBlocks.CYAN_MERCHANT_CARPET.get());
        registerMerchantCarpet(blockModels, MoneyBlocks.GRAY_MERCHANT_CARPET.get());
        registerMerchantCarpet(blockModels, MoneyBlocks.GREEN_MERCHANT_CARPET.get());
        registerMerchantCarpet(blockModels, MoneyBlocks.LIGHT_BLUE_MERCHANT_CARPET.get());
        registerMerchantCarpet(blockModels, MoneyBlocks.LIGHT_GRAY_MERCHANT_CARPET.get());
        registerMerchantCarpet(blockModels, MoneyBlocks.LIME_MERCHANT_CARPET.get());
        registerMerchantCarpet(blockModels, MoneyBlocks.MAGENTA_MERCHANT_CARPET.get());
        registerMerchantCarpet(blockModels, MoneyBlocks.ORANGE_MERCHANT_CARPET.get());
        registerMerchantCarpet(blockModels, MoneyBlocks.PINK_MERCHANT_CARPET.get());
        registerMerchantCarpet(blockModels, MoneyBlocks.PURPLE_MERCHANT_CARPET.get());
        registerMerchantCarpet(blockModels, MoneyBlocks.RED_MERCHANT_CARPET.get());
        registerMerchantCarpet(blockModels, MoneyBlocks.YELLOW_MERCHANT_CARPET.get());
        registerMerchantCarpet(blockModels, MoneyBlocks.WHITE_MERCHANT_CARPET.get());

        blockModels.createTrivialCube(MoneyBlocks.DOUBLOON_COMPRESSOR.get());
        blockModels.createTrivialCube(MoneyBlocks.DOUBLOON.get());

        itemModels.generateFlatItem(MoneyItems.DOLLAR.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(MoneyItems.WALLET.get(), ModelTemplates.FLAT_ITEM);

        registerHopper(blockModels, MoneyBlocks.TRIMMED_HOPPER.get(), "trimmed_hopper");
        registerHopper(blockModels, MoneyBlocks.DOUBLE_TRIMMED_HOPPER.get(), "double_trimmed_hopper");
    }

    private void registerHopper(BlockModelGenerators gen, Block block, String name) {
        Identifier down = Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, "block/" + name);
        Identifier side = Identifier.fromNamespaceAndPath(MoneyTalks.MOD_ID, "block/" + name + "_side");

        gen.blockStateOutput.accept(
            MultiVariantGenerator.dispatch(block)
                .with(PropertyDispatch.initial(BlockStateProperties.FACING)
                    .select(Direction.DOWN,  new MultiVariant(WeightedList.of(new Variant(down))))
                    .select(Direction.NORTH, new MultiVariant(WeightedList.of(new Variant(side))))
                    .select(Direction.EAST,  new MultiVariant(WeightedList.of(new Variant(side).withYRot(Quadrant.R90))))
                    .select(Direction.SOUTH, new MultiVariant(WeightedList.of(new Variant(side).withYRot(Quadrant.R180))))
                    .select(Direction.WEST,  new MultiVariant(WeightedList.of(new Variant(side).withYRot(Quadrant.R270))))
                    .select(Direction.UP,    new MultiVariant(WeightedList.of(new Variant(down))))
                )
        );
    }
}
