package com.createinhibitor.client;

import com.createinhibitor.block.entity.RotationalInhibitorBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.joml.Matrix4f;

public class RadiusDebugRenderer {
    
    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
            return;
        }
        
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null || mc.player == null) {
            return;
        }
        
        Level level = mc.level;
        Camera camera = event.getCamera();
        PoseStack poseStack = event.getPoseStack();
        
        BlockPos playerPos = mc.player.blockPosition();
        int renderDistance = mc.options.renderDistance().get() * 16;
        int renderDistanceSq = renderDistance * renderDistance;
        
        int chunkX = playerPos.getX() >> 4;
        int chunkZ = playerPos.getZ() >> 4;
        int searchChunks = (renderDistance >> 4) + 2;
        
        for (int cx = -searchChunks; cx <= searchChunks; cx++) {
            for (int cz = -searchChunks; cz <= searchChunks; cz++) {
                int checkChunkX = chunkX + cx;
                int checkChunkZ = chunkZ + cz;
                
                if (!level.hasChunk(checkChunkX, checkChunkZ)) {
                    continue;
                }
                
                var chunk = level.getChunk(checkChunkX, checkChunkZ);
                if (chunk == null) {
                    continue;
                }
                
                for (BlockEntity be : chunk.getBlockEntities().values()) {
                    if (be instanceof RotationalInhibitorBlockEntity inhibitor && 
                        inhibitor.isDebugMode()) {
                        
                        BlockPos bePos = be.getBlockPos();
                        double distSq = playerPos.distSqr(bePos);
                        if (distSq > renderDistanceSq) {
                            continue;
                        }
                        
                        double radius = inhibitor.getCurrentRadius();
                        float rpm = inhibitor.getCurrentRPM();
                        
                        if (radius <= 0) {
                            radius = 1.0;
                            rpm = 0;
                        }
                        
                        renderRadius(poseStack, camera, bePos, radius, rpm);
                    }
                }
            }
        }
    }
    
    private static void renderRadius(PoseStack poseStack, Camera camera, BlockPos pos, 
                                     double radius, float rpm) {
        if (radius <= 0) return;
        
        boolean isActive = rpm > 0;
        
        poseStack.pushPose();
        
        Vec3 cameraPos = camera.getPosition();
        Vec3 blockCenter = Vec3.atCenterOf(pos);
        poseStack.translate(blockCenter.x - cameraPos.x, 
                          blockCenter.y - cameraPos.y, 
                          blockCenter.z - cameraPos.z);
        
        Matrix4f matrix = poseStack.last().pose();
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer buffer = bufferSource.getBuffer(RenderType.lines());
        
        int r, g, b;
        if (isActive) {
            float intensity = Math.min(rpm / 128f, 1f);
            r = (int) (intensity * 255);
            g = (int) ((1f - intensity) * 255);
            b = 0;
        } else {
            r = 128;
            g = 128;
            b = 128;
        }
        
        int segments = 64;
        double angleStep = 2 * Math.PI / segments;
        
        for (int i = 0; i < segments; i++) {
            double angle1 = i * angleStep;
            double angle2 = (i + 1) * angleStep;
            
            float x1 = (float) (Math.cos(angle1) * radius);
            float z1 = (float) (Math.sin(angle1) * radius);
            float x2 = (float) (Math.cos(angle2) * radius);
            float z2 = (float) (Math.sin(angle2) * radius);
            
            buffer.vertex(matrix, x1, 0.5f, z1).color(r, g, b, 255).normal(0, 1, 0).endVertex();
            buffer.vertex(matrix, x2, 0.5f, z2).color(r, g, b, 255).normal(0, 1, 0).endVertex();
        }
        
        for (int i = 0; i < segments; i++) {
            double angle1 = i * angleStep;
            double angle2 = (i + 1) * angleStep;
            
            float x1 = (float) (Math.cos(angle1) * radius);
            float z1 = (float) (Math.sin(angle1) * radius);
            float x2 = (float) (Math.cos(angle2) * radius);
            float z2 = (float) (Math.sin(angle2) * radius);
            
            buffer.vertex(matrix, x1, 0.5f, 0).color(r, g, b, 255).normal(1, 0, 0).endVertex();
            buffer.vertex(matrix, x2, 0.5f, 0).color(r, g, b, 255).normal(1, 0, 0).endVertex();
            
            buffer.vertex(matrix, 0, 0.5f, z1).color(r, g, b, 255).normal(0, 0, 1).endVertex();
            buffer.vertex(matrix, 0, 0.5f, z2).color(r, g, b, 255).normal(0, 0, 1).endVertex();
        }
        
        bufferSource.endBatch(RenderType.lines());
        poseStack.popPose();
    }
    
    public static void register() {
        MinecraftForge.EVENT_BUS.register(RadiusDebugRenderer.class);
    }
}

