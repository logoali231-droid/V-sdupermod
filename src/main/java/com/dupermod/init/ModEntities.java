package com.dupermod.init;

import com.dupermod.DuperMod;
import com.dupermod.entity.EntityFarmer;
import com.dupermod.entity.EntityLumberjack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class ModEntities {

    public static void init() {
        int id = 1;
        EntityRegistry.registerModEntity(new ResourceLocation(DuperMod.MODID, "lumberjack_slime"), EntityLumberjack.class, "LumberjackSlime", id++, DuperMod.instance, 64, 1, true, 0x00FF00, 0x8B4513);
        EntityRegistry.registerModEntity(new ResourceLocation(DuperMod.MODID, "farmer_slime"), EntityFarmer.class, "FarmerSlime", id++, DuperMod.instance, 64, 1, true, 0x00FF00, 0xFFFF00);
    }
}