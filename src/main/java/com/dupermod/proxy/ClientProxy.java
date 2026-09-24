package com.dupermod.proxy;

import com.dupermod.entity.EntityMiner;
import com.dupermod.entity.EntityLumberjack;
import com.dupermod.entity.EntityFactorySlime;
import com.dupermod.client.render.RenderCustomSlime;

import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        // Registra apenas as entidades existentes passando os parâmetros necessários do RenderCustomSlime
        RenderingRegistry.registerEntityRenderingHandler(EntityMiner.class,
                manager -> new RenderCustomSlime(manager, 1.0F, 1.0F, 1.0F));

        RenderingRegistry.registerEntityRenderingHandler(EntityLumberjack.class,
                manager -> new RenderCustomSlime(manager, 1.0F, 1.0F, 1.0F));

        RenderingRegistry.registerEntityRenderingHandler(EntityFactorySlime.class,
                manager -> new RenderCustomSlime(manager, 1.0F, 1.0F, 1.0F));
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        super.postInit(event);
    }
}