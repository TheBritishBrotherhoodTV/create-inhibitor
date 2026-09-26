package com.createinhibitor.client;

import com.createinhibitor.CreateInhibitor;
import com.createinhibitor.ponder.PonderPlugin;
import net.createmod.ponder.foundation.PonderIndex;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = CreateInhibitor.MODID, value = Dist.CLIENT)
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
