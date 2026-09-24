package com.dupermod.init;

import com.dupermod.DuperMod;
import com.dupermod.entity.EntityFactorySlime;
import com.dupermod.entity.EntityLumberjack;
import com.dupermod.entity.EntityMiner;
import com.dupermod.entity.EntitySieve;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class ModEntities {

    private static int entityId = 0;

    public static void registerEntities() {
        // Minerador
        registerSlime(EntityMiner.class, "miner_slime", 0x3A3A3A, 0xF1C40F);

        // Lenhador
        registerSlime(EntityLumberjack.class, "lumberjack_slime", 0x5C4033, 0x2ECC71);

        // Agricultor
        registerSlime(EntityFarmer.class, "farmer_slime", 0x27AE60, 0xF39C12);

        // Lutador
        registerSlime(EntityFighter.class, "fighter_slime", 0xC0392B, 0xBDC3C7);

        // Cooler (Ar Condicionado)
        registerSlime(EntityCooler.class, "cooler_slime", 0x3498DB, 0xECF0F1);

        // Factory Slime
        registerSlime(EntityFactorySlime.class, "factory_slime", 0xE67E22, 0x7F8C8D);

        // Sieve Slime
        registerSlime(EntitySieve.class, "sieve_slime", 0xF39C12, 0xECF0F1);
    }

    private static void registerSlime(Class<? extends Entity> entityClass, String name, int primaryEggColor, int secondaryEggColor) {
        EntityRegistry.registerModEntity(
                new ResourceLocation(DuperMod.MODID, name),
                entityClass,
                name,
                ++entityId,
                DuperMod.instance,
                64, 1, true,
                primaryEggColor, secondaryEggColor
        );
    }
}