package com.dupermod.init;

import com.dupermod.DuperMod
import com.dupermod.entity.*;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class ModEntities {

    private static int entityId = 0;

    public static void registerEntities() {
        registerSlime(EntityMiner.class, "miner_slime", 0x3A3A3A, 0xF1C40F);
        registerSlime(EntityLumberjack.class, "lumberjack_slime", 0x5C4033, 0x2ECC71);
        registerSlime(EntityFactorySlime.class, "factory_slime", 0xE67E22, 0x7F8C8D);
        registerSlime(EntityCooler.class, "cooler_slime", 0x00FFFF, 0xFFFFFF);
        registerSlime(EntityFarmer.class, "farmer_slime", 0x2ECC71, 0xF1C40F);
        registerSlime(EntityFighter.class, "fighter_slime", 0xE74C3C, 0x95A5A6);
    }

    private static void registerSlime(Class<? extends Entity> entityClass, String name, int eggPrimary, int eggSecondary) {
        EntityRegistry.registerModEntity(
                entityClass,
                name,
                entityId++,
                DuperMod.instance,
                64,
                1,
                true,
                eggPrimary,
                eggSecondary
        );
    }
}