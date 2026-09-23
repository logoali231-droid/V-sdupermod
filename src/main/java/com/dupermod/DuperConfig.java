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
    public static int coolingIntervalTicks = 40;

    // Acessibilidade Motora
    public static boolean enableMagnet = true;
    public static boolean enableAutoTool = true;
    public static boolean enableAutoEat = true;
    public static boolean enableStepAssist = true;

    // Acessibilidade Visual
    public static boolean enableNightVision = true;
    public static boolean enableSonar = true;
    public static boolean enableLightGrid = true;

    public static void init(File file) {
        config = new Configuration(file);
        loadConfig();
    }

    public static void loadConfig() {
        try {
            config.load();

            // Categoria Geral
            duplicationTicks = config.getInt("duplicationTicks", Configuration.CATEGORY_GENERAL, 100, 1, 1200, "Tempo base em ticks para duplicar um item.");
            enableParticles = config.getBoolean("enableParticles", Configuration.CATEGORY_GENERAL, true, "Ativar efeitos de partículas nos duplicadores.");

            // Categoria Cooling
            enableCoolingArmor = config.getBoolean("enableCoolingArmor", "cooling", true, "Ativar efeito de arrefecimento da armadura.");
            coolingIntervalTicks = config.getInt("coolingIntervalTicks", "cooling", 40, 10, 200, "Intervalo em ticks para aplicar o arrefecimento.");

            // Categoria Motor Accessibility
            enableMagnet = config.getBoolean("enableMagnet", "motor_accessibility", true, "Atrai itens do chão num raio de 8 blocos.");
            enableAutoTool = config.getBoolean("enableAutoTool", "motor_accessibility", true, "Troca para a ferramenta adequada automaticamente ao bater num bloco.");
            enableAutoEat = config.getBoolean("enableAutoEat", "motor_accessibility", true, "Consome comida da hotbar quando a fome ou vida estiverem baixas.");
            enableStepAssist = config.getBoolean("enableStepAssist", "motor_accessibility", true, "Permite subir blocos de 1 de altura sem pular.");

            // Categoria Visual Accessibility
            enableNightVision = config.getBoolean("enableNightVision", "visual_accessibility", true, "Aplica Visão Noturna contínua e sem partículas.");
            enableSonar = config.getBoolean("enableSonar", "visual_accessibility", true, "Destaca minérios raros e mobs hostis ao redor com partículas.");
            enableLightGrid = config.getBoolean("enableLightGrid", "visual_accessibility", true, "Exibe partículas em blocos onde mobs hostis podem nascer (luz <= 7).");

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