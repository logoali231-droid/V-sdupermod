package com.dupermod.proxy;

import com.dupermod.entity.EntityCooler;
import com.dupermod.entity.EntityFactorySlime;
import com.dupermod.entity.EntityFarmer;
import com.dupermod.entity.EntityFighter;
import com.dupermod.entity.EntityLumberjack;
import com.dupermod.entity.EntityMiner;
import com.dupermod.entity.EntitySieve;
import com.dupermod.client.render.RenderCustomSlime; // Ajuste o pacote do seu Render se necessário

import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        // Registra os Renderers de todas as entidades customizadas
        RenderingRegistry.registerEntityRenderingHandler(EntityMiner.class, RenderCustomSlime::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityLumberjack.class, RenderCustomSlime::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityFarmer.class, RenderCustomSlime::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityFighter.class, RenderCustomSlime::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityCooler.class, RenderCustomSlime::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityFactorySlime.class, RenderCustomSlime::new);
        RenderingRegistry.registerEntityRenderingHandler(EntitySieve.class, RenderCustomSlime::new);
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