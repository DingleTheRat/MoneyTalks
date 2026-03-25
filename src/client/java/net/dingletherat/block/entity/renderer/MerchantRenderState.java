package net.dingletherat.block.entity.renderer;

import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class MerchantRenderState extends BlockEntityRenderState {
    public BlockPos lightPosition;
    public Level blockEntityWorld;
    public float rotation;

    final ItemStackRenderState itemRenderState = new ItemRenderState();
}
