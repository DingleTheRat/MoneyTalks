package net.dingletherat.block.entity.renderer;

import net.dingletherat.block.entity.custom.MerchantCarpetEntity;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.core.BlockPos;
import com.mojang.math.Axis;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.Level;

public class MerchantRenderer implements BlockEntityRenderer<MerchantCarpetEntity, MerchantRenderState> {
    private final ItemModelResolver itemModelResolver;

    public MerchantRenderer(BlockEntityRendererProvider.Context context) {
        itemModelResolver = context.itemModelResolver();
    }

    @Override
    public MerchantRenderState createRenderState() {
        return new MerchantRenderState();
    }

    @Override
    public void extractRenderState(MerchantCarpetEntity blockEntity, MerchantRenderState state, float tickProgress,
                                  Vec3 cameraPos, ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderer.super.extractRenderState(blockEntity, state, tickProgress, cameraPos, crumblingOverlay);
        state.lightPosition = blockEntity.getBlockPos();
        state.blockEntityWorld = blockEntity.getLevel();
        state.rotation = blockEntity.getRenderingRotation();
        itemModelResolver.updateForTopItem(state.itemRenderState, blockEntity.getDisplay(), ItemDisplayContext.FIXED, blockEntity.getLevel(), null, 0);
    }

    @Override
    public void submit(MerchantRenderState state, PoseStack matrices, SubmitNodeCollector queue, CameraRenderState cameraState) {
        matrices.pushPose();
        matrices.translate(0.5f, 0.5f, 0.5f);
        matrices.scale(0.5f, 0.5f, 0.5f);
        matrices.mulPose(Axis.YP.rotationDegrees(state.rotation));
        state.itemRenderState.submit(matrices, queue, getLightLevel(state.blockEntityWorld, state.lightPosition), OverlayTexture.NO_OVERLAY, 0);
        matrices.popPose();
    }

    private int getLightLevel(Level level, BlockPos pos) {
        int bLight = level.getBrightness(LightLayer.BLOCK, pos);
        int sLight = level.getBrightness(LightLayer.SKY, pos);
        return (sLight << 20) | (bLight << 4);
    }
}
