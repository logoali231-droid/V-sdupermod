package com.dupermod.proxy;

import com.dupermod.client.render.RenderCustomSlime;
import com.dupermod.entity.*;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        // Registra a renderização no mundo para todas as entidades
        RenderingRegistry.registerEntityRenderingHandler(EntityMiner.class, manager -> new RenderCustomSlime(manager, 0.3F, 0.3F, 0.3F));
        RenderingRegistry.registerEntityRenderingHandler(EntityLumberjack.class, manager -> new RenderCustomSlime(manager, 0.4F, 0.25F, 0.1F));
        RenderingRegistry.registerEntityRenderingHandler(EntityFactorySlime.class, manager -> new RenderCustomSlime(manager, 0.9F, 0.5F, 0.1F));
        RenderingRegistry.registerEntityRenderingHandler(EntityCooler.class, manager -> new RenderCustomSlime(manager, 0.2F, 0.8F, 1.0F));
        RenderingRegistry.registerEntityRenderingHandler(EntityFarmer.class, manager -> new RenderCustomSlime(manager, 0.2F, 0.9F, 0.2F));
        RenderingRegistry.registerEntityRenderingHandler(EntityFighter.class, manager -> new RenderCustomSlime(manager, 0.9F, 0.2F, 0.2F));
    }

    @Override
    public void registerItemRenderer(Item item, int meta, String id) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation("dupermod:" + id, "inventory"));
    }
}