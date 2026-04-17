package net.dingletherat.screen.custom;

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
        super.extractRenderState(graphics, mouseX, mouseY, a);
    }
}
