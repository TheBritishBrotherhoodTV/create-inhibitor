package com.createinhibitor.ponder;

import com.createinhibitor.CreateInhibitor;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.registration.PonderTagRegistrationHelper;
import net.createmod.ponder.api.registration.SharedTextRegistrationHelper;
import net.minecraft.resources.ResourceLocation;

public class PonderPlugin implements net.createmod.ponder.api.registration.PonderPlugin {
    
    @Override
    public String getModId() {
        return CreateInhibitor.MODID;
    }
    
    @Override
    public void registerScenes(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        PonderScenes.register(helper);
    }
    
    @Override
    public void registerTags(PonderTagRegistrationHelper<ResourceLocation> helper) {
        PonderTagRegistrationHelper<com.tterrag.registrate.util.entry.RegistryEntry<?>> HELPER = 
            helper.withKeyFunction(com.tterrag.registrate.util.entry.RegistryEntry::getId);
        ResourceLocation utilityTag = ResourceLocation.fromNamespaceAndPath(CreateInhibitor.MODID, "utility");
        
        HELPER.registerTag(utilityTag)
            .addToIndex()
            .item(CreateInhibitor.ROTATIONAL_INHIBITOR, true, false)
            .title("Utility")
            .description("Utility blocks for automation and protection")
            .register();
    }
    
    @Override
    public void registerSharedText(SharedTextRegistrationHelper helper) {
    }
}

