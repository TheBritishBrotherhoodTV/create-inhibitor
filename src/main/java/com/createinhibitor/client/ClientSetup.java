package com.createinhibitor.client;

import com.createinhibitor.CreateInhibitor;
import com.createinhibitor.ponder.PonderPlugin;
import net.createmod.ponder.foundation.PonderIndex;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = CreateInhibitor.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    
    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(CreateInhibitor.ROTATIONAL_INHIBITOR_BLOCK_ENTITY.get(), RotationalInhibitorRenderer::new);
    }
    
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        RadiusDebugRenderer.register();
        
        PonderIndex.addPlugin(new PonderPlugin());
    }
}

