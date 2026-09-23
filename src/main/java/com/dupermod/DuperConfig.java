package com.dupermod;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import java.io.File;

public class DuperConfig {

    public static Configuration config;

    // Geral
    public static int duplicationTicks = 100;
    public static boolean enableParticles = true;

    // Arrefecimento / Tough As Nails
    public static boolean enableCoolingArmor = true;
    public static int coolingIntervalTicks = 40; // 2 segundos

    public static void init(File file) {
        config = new Configuration(file);
        loadConfig();
    }

    public static void loadConfig() {
        try {
            config.load();

            // Categoria Geral
            duplicationTicks = config.getInt("duplicationTicks", Configuration.CATEGORY_GENERAL, 100, 1, 1200, "Tempo em ticks para duplicar um item.");
            enableParticles = config.getBoolean("enableParticles", Configuration.CATEGORY_GENERAL, true, "Ativar efeitos de partículas nos duplicadores.");

            // Categoria Cooling
            enableCoolingArmor = config.getBoolean("enableCoolingArmor", "cooling", true, "Ativar efeito de arrefecimento da armadura e upgrades.");
            coolingIntervalTicks = config.getInt("coolingIntervalTicks", "cooling", 40, 10, 200, "Intervalo em ticks (20 ticks = 1s) para aplicar o arrefecimento.");

        } catch (Exception e) {
            System.err.println("Erro ao carregar as configurações do DuperMod!");
            e.printStackTrace();
        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }

    @SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equalsIgnoreCase(DuperMod.MODID)) {
            loadConfig();
        }
    }
}