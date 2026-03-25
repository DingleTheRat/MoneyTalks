package net.dingletherat.block.entity.renderer;

import org.jetbrains.annotations.Nullable;

import net.dingletherat.block.entity.custom.MerchantCarpetEntity;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.CameraRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.core.BlockPos;
import com.mojang.math.Axis;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.Level;

public class MerchantRenderer implements BlockEntityRenderer<MerchantCarpetEntity, MerchantRenderState> {
    private final ItemModelResolver itemModelManager;

    public MerchantRenderer(BlockEntityRendererProvider.Context context) {
        itemModelManager = context.itemModelManager();
    }

    @Override
    public MerchantRenderState createRenderState() {
        return new MerchantRenderState();
    }

    @Override
    public void updateRenderState(MerchantCarpetEntity blockEntity, MerchantRenderState state, float tickProgress,
                                  Vec3 cameraPos, @Nullable ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.updateRenderState(blockEntity, state, tickProgress, cameraPos, crumblingOverlay);

        state.lightPosition = blockEntity.getPos();
        state.blockEntityWorld = blockEntity.getWorld();
        state.rotation = blockEntity.getRenderingRotation();

        itemModelManager.clearAndUpdate(state.itemRenderState,
                blockEntity.getDisplay(), ItemDisplayContext.FIXED, blockEntity.getWorld(), null, 0);
    }

    @Override
    public void render(MerchantRenderState state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState cameraState) {
        matrices.push();

        matrices.translate(0.5f, 0.5f, 0.5f);
        matrices.scale(0.5f, 0.5f, 0.5f);
        matrices.multiply(Axis.POSITIVE_Y.rotationDegrees(state.rotation));

        state.itemRenderState.render(matrices, queue, getLightLevel(state.blockEntityWorld, state.lightPosition), OverlayTexture.DEFAULT_UV, 0);

        matrices.pop();
    }

    private int getLightLevel(Level world, BlockPos pos) {
        int bLight = world.getLightLevel(LightLayer.BLOCK, pos);
        int sLight = world.getLightLevel(LightLayer.SKY, pos);
        return LightTexture.pack(bLight, sLight);
    }
}
