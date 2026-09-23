package com.dupermod.init;

import com.dupermod.DuperMod;
import com.dupermod.entity.EntityFarmer;
import com.dupermod.entity.EntityFighter;
import com.dupermod.entity.EntityLumberjack;
import com.dupermod.entity.EntityCooler;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class ModEntities {

    public static void init() {
        int id = 1;
        EntityRegistry.registerModEntity(EntityLumberjack.class, "LumberjackSlime", id++, DuperMod.instance, 64, 1, true, 0x00FF00, 0x8B4513);
        EntityRegistry.registerModEntity(EntityFarmer.class, "FarmerSlime", id++, DuperMod.instance, 64, 1, true, 0x00FF00, 0xFFFF00);
        EntityRegistry.registerModEntity(EntityFighter.class, "FighterSlime", id++, DuperMod.instance, 64, 1, true, 0x00FF00, 0xFF0000);
        EntityRegistry.registerModEntity(EntityCooler.class, "CoolerSlime", id++, DuperMod.instance, 64, 1, true, 0xFFFFFF, 0xE0FFFF);
        EntityRegistry.registerModEntity(EntityMiner.class, "MinerSlime", id++, DuperMod.instance, 64, 1, true, 0x800080, 0x4B0082);
    }
}