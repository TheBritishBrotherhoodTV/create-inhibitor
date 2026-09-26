package com.createinhibitor.events;

import com.createinhibitor.block.entity.RotationalInhibitorBlockEntity;
import com.createinhibitor.CreateInhibitor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;

@EventBusSubscriber(modid = CreateInhibitor.MODID)
public class MobSpawnHandler {
    
    private static final int MAX_SEARCH_RADIUS = 64;
    
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onMobSpawn(FinalizeSpawnEvent event) {
        if (event.getEntity().level().isClientSide) {
            return;
        }
        
        if (event.getSpawnType() != MobSpawnType.NATURAL && 
            event.getSpawnType() != MobSpawnType.STRUCTURE &&
            event.getSpawnType() != MobSpawnType.CHUNK_GENERATION) {
            return;
        }
        
        if (event.getEntity().getType().getCategory().isFriendly()) {
            return;
        }
        
        CreateInhibitor.LOGGER.debug("Checking spawn for {} at {} (type: {})", 
            event.getEntity().getType().getDescription().getString(),
            event.getEntity().blockPosition(),
            event.getSpawnType());
        
        Level level = event.getEntity().level();
        BlockPos spawnPos = event.getEntity().blockPosition();
        
        int searchRadius = MAX_SEARCH_RADIUS;
        int minX = spawnPos.getX() - searchRadius;
        int maxX = spawnPos.getX() + searchRadius;
        int minY = Math.max(level.getMinBuildHeight(), spawnPos.getY() - searchRadius);
        int maxY = Math.min(level.getMaxBuildHeight() - 1, spawnPos.getY() + searchRadius);
        int minZ = spawnPos.getZ() - searchRadius;
        int maxZ = spawnPos.getZ() + searchRadius;
        
        int chunkX = spawnPos.getX() >> 4;
        int chunkZ = spawnPos.getZ() >> 4;
        int searchChunks = (searchRadius >> 4) + 2;
        
        for (int cx = -searchChunks; cx <= searchChunks; cx++) {
            for (int cz = -searchChunks; cz <= searchChunks; cz++) {
                int startX = ((chunkX + cx) << 4);
                int startZ = ((chunkZ + cz) << 4);
                int endX = startX + 15;
                int endZ = startZ + 15;
                
                int chunkCenterX = startX + 8;
                int chunkCenterZ = startZ + 8;
                double chunkDistSq = spawnPos.distSqr(new BlockPos(chunkCenterX, spawnPos.getY(), chunkCenterZ));
                if (chunkDistSq > (searchRadius + 8) * (searchRadius + 8)) {
                    continue;
                }
                
                for (int x = startX; x <= endX; x++) {
                    for (int z = startZ; z <= endZ; z++) {
                        for (int y = minY; y <= maxY; y++) {
                            BlockPos checkPos = new BlockPos(x, y, z);
                            
                            double distSq = spawnPos.distSqr(checkPos);
                            if (distSq > searchRadius * searchRadius) {
                                continue;
                            }
                            
                            if (level.getBlockState(checkPos).getBlock() == CreateInhibitor.ROTATIONAL_INHIBITOR.get()) {
                                BlockEntity be = level.getBlockEntity(checkPos);
                                if (be instanceof RotationalInhibitorBlockEntity inhibitor) {
                                    if (inhibitor.isActive()) {
                                        double radius = inhibitor.getCurrentRadius();
                                        
                                        if (distSq <= radius * radius) {
                                            CreateInhibitor.LOGGER.info("Prevented {} spawn at {} (distance: {}, radius: {}, RPM: {}, inhibitor at {})", 
                                                event.getEntity().getType().getDescription().getString(),
                                                spawnPos, 
                                                String.format("%.2f", Math.sqrt(distSq)), 
                                                String.format("%.2f", radius), 
                                                String.format("%.1f", inhibitor.getCurrentRPM()),
                                                checkPos);
                                            
                                            event.setSpawnCancelled(true);
                                            event.setCanceled(true);
                                            
                                            if (event.getEntity().isAlive()) {
                                                event.getEntity().remove(net.minecraft.world.entity.Entity.RemovalReason.DISCARDED);
                                            }
                                            return;
                                        }
                                    } else {
                                        CreateInhibitor.LOGGER.debug("Found inactive inhibitor at {} (RPM: {})", 
                                            checkPos, inhibitor.getCurrentRPM());
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
