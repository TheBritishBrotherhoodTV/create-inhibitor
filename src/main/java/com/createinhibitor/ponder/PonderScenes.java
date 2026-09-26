package com.createinhibitor.ponder;

import com.createinhibitor.CreateInhibitor;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import com.tterrag.registrate.util.entry.ItemProviderEntry;
import com.tterrag.registrate.util.entry.RegistryEntry;
import net.createmod.ponder.api.registration.PonderSceneRegistrationHelper;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

public class PonderScenes {
    
    public static void register(PonderSceneRegistrationHelper<ResourceLocation> helper) {
        PonderSceneRegistrationHelper<ItemProviderEntry<?, ?>> HELPER = helper.withKeyFunction(RegistryEntry::getId);
        ResourceLocation utilityTag = ResourceLocation.fromNamespaceAndPath(CreateInhibitor.MODID, "utility");
        
        HELPER.forComponents(CreateInhibitor.ROTATIONAL_INHIBITOR)
            .addStoryBoard("rotational_inhibitor", PonderScenes::rotationalInhibitor, utilityTag);
    }
    
    public static void rotationalInhibitor(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("rotational_inhibitor", "Using the Rotational Inhibitor");
        scene.configureBasePlate(0, 0, 5);
        scene.showBasePlate();
        scene.idle(5);
        
        BlockPos inhibitorPos = util.grid().at(2, 1, 2);
        scene.world().modifyBlock(inhibitorPos, s -> CreateInhibitor.ROTATIONAL_INHIBITOR.getDefaultState(), false);
        scene.world().showIndependentSection(util.select().position(inhibitorPos), Direction.DOWN);
        scene.idle(10);
        
        scene.overlay().showText(60)
            .attachKeyFrame()
            .text("The Rotational Inhibitor prevents hostile mob spawns in a radius around it.")
            .pointAt(util.vector().centerOf(inhibitorPos).add(0, 1.5, 0))
            .placeNearTarget();
        scene.idle(70);
        
        BlockPos shaftPos = util.grid().at(2, 1, 1);
        scene.world().modifyBlock(shaftPos, s -> com.simibubi.create.AllBlocks.SHAFT.getDefaultState(), false);
        scene.world().showIndependentSection(util.select().position(shaftPos), Direction.SOUTH);
        scene.idle(10);
        
        BlockPos cogPos = util.grid().at(2, 1, 0);
        scene.world().modifyBlock(cogPos, s -> com.simibubi.create.AllBlocks.LARGE_COGWHEEL.getDefaultState(), false);
        scene.world().showIndependentSection(util.select().position(cogPos), Direction.SOUTH);
        scene.idle(10);
        
        scene.world().modifyKineticSpeed(util.select().fromTo(shaftPos, inhibitorPos), f -> 16f);
        scene.world().modifyKineticSpeed(util.select().position(cogPos), f -> 16f);
        scene.effects().indicateSuccess(inhibitorPos);
        scene.idle(20);
        
        scene.overlay().showText(60)
            .attachKeyFrame()
            .text("At 16 RPM, the protection radius is 4 blocks.")
            .pointAt(util.vector().centerOf(inhibitorPos))
            .placeNearTarget();
        scene.idle(70);
        
        scene.world().modifyKineticSpeed(util.select().fromTo(shaftPos, inhibitorPos), f -> 64f);
        scene.world().modifyKineticSpeed(util.select().position(cogPos), f -> 64f);
        scene.effects().indicateSuccess(inhibitorPos);
        scene.idle(20);
        
        scene.overlay().showText(60)
            .attachKeyFrame()
            .text("At 64 RPM, the protection radius increases to 16 blocks.")
            .pointAt(util.vector().centerOf(inhibitorPos))
            .placeNearTarget();
        scene.idle(70);
        
        scene.world().modifyKineticSpeed(util.select().fromTo(shaftPos, inhibitorPos), f -> 128f);
        scene.world().modifyKineticSpeed(util.select().position(cogPos), f -> 128f);
        scene.effects().indicateSuccess(inhibitorPos);
        scene.idle(20);
        
        scene.overlay().showText(60)
            .attachKeyFrame()
            .text("At 128 RPM, the protection radius reaches 32 blocks.")
            .pointAt(util.vector().centerOf(inhibitorPos))
            .placeNearTarget();
        scene.idle(70);
        
        scene.overlay().showText(80)
            .attachKeyFrame()
            .text("The Rotational Inhibitor requires significant stress capacity to operate.")
            .pointAt(util.vector().centerOf(inhibitorPos))
            .placeNearTarget();
        scene.idle(90);
        
        scene.world().modifyKineticSpeed(util.select().fromTo(shaftPos, inhibitorPos), f -> 0f);
        scene.world().modifyKineticSpeed(util.select().position(cogPos), f -> 0f);
        scene.idle(10);
        
        scene.overlay().showText(60)
            .attachKeyFrame()
            .text("When RPM reaches 0, the effect instantly stops.")
            .pointAt(util.vector().centerOf(inhibitorPos))
            .placeNearTarget();
        scene.idle(70);
        
        scene.markAsFinished();
    }
}
