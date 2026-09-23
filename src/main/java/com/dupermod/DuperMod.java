package com.dupermod;

import com.dupermod.event.CoolingHandler;
import com.dupermod.init.ModBlocks;
import com.dupermod.init.ModItems;
import com.dupermod.proxy.CommonProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import java.io.File;

@Mod(modid = DuperMod.MODID, name = DuperMod.NAME, version = DuperMod.VERSION, guiFactory = "com.dupermod.client.gui.DuperGuiFactory")
public class DuperMod {
    public static final String MODID = "dupermod";
    public static final String NAME = "Block Duper Mod";
    public static final String VERSION = "1.0";

    @Mod.Instance
    public static DuperMod instance;

    @SidedProxy(clientSide = "com.dupermod.proxy.ClientProxy", serverSide = "com.dupermod.proxy.CommonProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        File configFile = new File(event.getModConfigurationDirectory(), "dupermod.cfg");
        DuperConfig.init(configFile);

        ModItems.init();
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
        ModItems.registerRecipes();

        // Registo dos Eventos
        MinecraftForge.EVENT_BUS.register(new DuperConfig());
        MinecraftForge.EVENT_BUS.register(new CoolingHandler());
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }
}