package com.dupermod;

import com.dupermod.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = DuperMod.MODID, name = DuperMod.NAME, version = DuperMod.VERSION)
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
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }
}