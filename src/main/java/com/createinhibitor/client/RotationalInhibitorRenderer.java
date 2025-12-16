package com.createinhibitor.client;

import com.createinhibitor.block.entity.RotationalInhibitorBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;

public class RotationalInhibitorRenderer extends KineticBlockEntityRenderer<RotationalInhibitorBlockEntity> {
    
    public RotationalInhibitorRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }
    
    @Override
    protected void renderSafe(RotationalInhibitorBlockEntity be, float partialTicks, PoseStack ms, 
                              MultiBufferSource buffer, int light, int overlay) {
        super.renderSafe(be, partialTicks, ms, buffer, light, overlay);
        
        if (be.isActive()) {
            float angle = be.getVisualAngle();
            float intensity = Math.min(be.getCurrentRPM() / 128f, 1f);
        }
    }
}

