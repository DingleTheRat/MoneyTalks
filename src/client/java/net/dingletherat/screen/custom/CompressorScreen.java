package net.dingletherat.screen.custom;

import net.dingletherat.block.entity.custom.DoubloonCompressorEntity;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class CompressorScreen extends AbstractContainerScreen<CompressorMenu> {
    public static final Identifier TEXTURE = Identifier.withDefaultNamespace("textures/gui/container/furnace.png") ;
    public static final Identifier ARROW_TEXTURE = Identifier.withDefaultNamespace("container/furnace/burn_progress");

    public CompressorScreen(CompressorMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a) {
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, leftPos, topPos, 0f, 0f, this.imageWidth, this.imageHeight, 256, 256);

        int max = menu.data.get(DoubloonCompressorEntity.ARROW_COMPRESSION_TICKS);
        int progress = menu.data.get(DoubloonCompressorEntity.ARROW_PROGRESS);
        System.out.println("max=" + max + " progress=" + progress);

        float burnProgress = max == 0 ? 0f : (float)(max - progress) / (float)max;
        int burnProgressWidth = (int) Math.ceil(burnProgress * 24.0f);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ARROW_TEXTURE, 24, 17, 0, 0, leftPos + 79, topPos + 34, burnProgressWidth, 16);

        super.extractRenderState(graphics, mouseX, mouseY, a);
    }
}
