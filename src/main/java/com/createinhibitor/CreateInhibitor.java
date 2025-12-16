package com.createinhibitor;

import com.createinhibitor.block.RotationalInhibitorBlock;
import com.createinhibitor.block.entity.RotationalInhibitorBlockEntity;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

@Mod(CreateInhibitor.MODID)
public class CreateInhibitor {
    public static final String MODID = "createinhibitor";
    public static final Logger LOGGER = LogUtils.getLogger();
    
    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID);
    
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    
    public static final ResourceKey<CreativeModeTab> CREATIVE_TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, ResourceLocation.fromNamespaceAndPath(MODID, "main"));
    
    public static final com.tterrag.registrate.util.entry.BlockEntry<RotationalInhibitorBlock> ROTATIONAL_INHIBITOR = REGISTRATE
        .block("rotational_inhibitor", RotationalInhibitorBlock::new)
        .properties(p -> p.mapColor(net.minecraft.world.level.material.MapColor.METAL)
            .strength(3.5f)
            .requiresCorrectToolForDrops()
            .sound(net.minecraft.world.level.block.SoundType.COPPER)
            .lightLevel(s -> s.getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT) ? 15 : 0))
        .lang("Rotational Inhibitor")
        .item()
        .tab(CREATIVE_TAB_KEY)
        .build()
        .register();
    
    public static final RegistryObject<CreativeModeTab> CREATIVE_TAB = CREATIVE_TABS.register("main", () -> 
        CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.createinhibitor"))
            .icon(() -> new ItemStack(ROTATIONAL_INHIBITOR.get()))
            .displayItems((parameters, output) -> {
                output.accept(ROTATIONAL_INHIBITOR.get());
            })
            .build()
    );
    
    public static final RegistryObject<BlockEntityType<RotationalInhibitorBlockEntity>> ROTATIONAL_INHIBITOR_BLOCK_ENTITY = 
        BLOCK_ENTITIES.register("rotational_inhibitor", 
            () -> BlockEntityType.Builder.of(RotationalInhibitorBlockEntity::new, ROTATIONAL_INHIBITOR.get()).build(null));
    
    public CreateInhibitor() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        BLOCK_ENTITIES.register(modEventBus);
        CREATIVE_TABS.register(modEventBus);
        
        REGISTRATE.registerEventListeners(modEventBus);
    }
}

