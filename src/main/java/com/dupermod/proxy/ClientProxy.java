package com.dupermod.proxy;

import com.dupermod.client.render.RenderCustomSlime;
import com.dupermod.entity.EntityFarmer;
import com.dupermod.entity.EntityLumberjack;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        // Registo dos renderizadores com tint de cor
        RenderingRegistry.registerEntityRenderingHandler(EntityLumberjack.class, RenderCustomSlime::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityFarmer.class, RenderCustomSlime::new);
    }

    @Override
    public void registerItemRenderer(Item item, int meta, String id) {
        ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation("dupermod:" + id, "inventory"));
    }
}