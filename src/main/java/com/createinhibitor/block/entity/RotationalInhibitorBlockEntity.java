package com.createinhibitor.block.entity;

import com.createinhibitor.CreateInhibitor;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class RotationalInhibitorBlockEntity extends KineticBlockEntity {
    public static final double RADIUS_MULTIPLIER = 0.25;
    
    private float currentRPM = 0;
    private double currentRadius = 0;
    private float visualAngle = 0;
    private boolean isActive = false;
    private boolean debugMode = false;
    
    public RotationalInhibitorBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    
    public RotationalInhibitorBlockEntity(BlockPos pos, BlockState state) {
        this(CreateInhibitor.ROTATIONAL_INHIBITOR_BLOCK_ENTITY.get(), pos, state);
    }
    
    @Override
    public void tick() {
        super.tick();
        
        float speed = getSpeed();
        float newRPM = Math.abs(speed);
        double newRadius = newRPM * RADIUS_MULTIPLIER;
        boolean newActive = newRPM > 0;
        
        boolean changed = currentRPM != newRPM || currentRadius != newRadius || isActive != newActive;
        
        currentRPM = newRPM;
        currentRadius = newRadius;
        isActive = newActive;
        
        if (level != null && level.isClientSide && isActive) {
            visualAngle += speed * 2;
            if (visualAngle > 360) visualAngle -= 360;
            if (visualAngle < 0) visualAngle += 360;
        }
        
        if (changed && !level.isClientSide) {
            notifyUpdate();
        }
    }
    
    public double getCurrentRadius() {
        return currentRadius;
    }
    
    public float getCurrentRPM() {
        return currentRPM;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public float getVisualAngle() {
        return visualAngle;
    }
    
    public boolean isDebugMode() {
        return debugMode;
    }
    
    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
        setChanged();
        if (level != null && !level.isClientSide) {
            notifyUpdate();
        }
    }
    
    public void toggleDebugMode() {
        setDebugMode(!debugMode);
    }
    
    @Override
    protected void write(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.write(tag, registries, clientPacket);
        tag.putFloat("CurrentRPM", currentRPM);
        tag.putDouble("CurrentRadius", currentRadius);
        tag.putBoolean("IsActive", isActive);
        tag.putBoolean("DebugMode", debugMode);
        if (clientPacket) {
            setChanged();
        }
    }
    
    @Override
    protected void read(CompoundTag tag, HolderLookup.Provider registries, boolean clientPacket) {
        super.read(tag, registries, clientPacket);
        currentRPM = tag.getFloat("CurrentRPM");
        currentRadius = tag.getDouble("CurrentRadius");
        isActive = tag.getBoolean("IsActive");
        debugMode = tag.getBoolean("DebugMode");
    }
    
    @Override
    public float calculateStressApplied() {
        return 8.0f;
    }
}
