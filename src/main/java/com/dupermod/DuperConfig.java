package com.dupermod;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.File;

public class DuperConfig {

    public static Configuration config;

    // --- Nomes das Categorias ---
    public static final String CATEGORY_GENERAL = Configuration.CATEGORY_GENERAL;
    public static final String CATEGORY_COOLING = "cooling";
    public static final String CATEGORY_MOTOR = "motor_accessibility";
    public static final String CATEGORY_VISUAL = "visual_accessibility";

    // --- Categoria Geral ---
    public static int duplicationTicks = 100;
    public static boolean enableParticles = true;

    // --- Arrefecimento / Tough As Nails ---
    public static boolean enableCoolingArmor = true;
    public static int coolingIntervalTicks = 40;

    // --- Acessibilidade Motora ---
    public static boolean enableMagnet = true;
    public static boolean enableAutoTool = true;
    public static boolean enableAutoEat = true;
    public static boolean enableStepAssist = true;

    // --- Acessibilidade Visual ---
    public static boolean enableNightVision = true;
    public static boolean enableSonar = true;
    public static boolean enableLightGrid = true;

    public static void init(File file) {
        if (config == null) {
            config = new Configuration(file);
            loadConfig();
        }
    }

    public static void loadConfig() {
        try {
            config.load();

            // Categoria Geral
            duplicationTicks = config.getInt("duplicationTicks", CATEGORY_GENERAL, 100, 1, 1200, "Tempo base em ticks para duplicar um item nos duplicadores.");
            enableParticles = config.getBoolean("enableParticles", CATEGORY_GENERAL, true, "Ativar efeitos de partículas nos duplicadores durante o processo.");

            // Categoria Cooling
            enableCoolingArmor = config.getBoolean("enableCoolingArmor", CATEGORY_COOLING, true, "Ativar efeito de arrefecimento/apagar fogo da armadura.");
            coolingIntervalTicks = config.getInt("coolingIntervalTicks", CATEGORY_COOLING, 40, 10, 200, "Intervalo em ticks para aplicar o efeito de arrefecimento.");

            // Categoria Acessibilidade Motora
            enableMagnet = config.getBoolean("enableMagnet", CATEGORY_MOTOR, true, "Atrai itens do chão num raio de 8 blocos ao carregar o Anel de Acessibilidade.");
            enableAutoTool = config.getBoolean("enableAutoTool", CATEGORY_MOTOR, true, "Troca para a ferramenta adequada automaticamente da hotbar ao bater num bloco.");
            enableAutoEat = config.getBoolean("enableAutoEat", CATEGORY_MOTOR, true, "Consome comida da hotbar quando a fome estiver baixa.");
            enableStepAssist = config.getBoolean("enableStepAssist", CATEGORY_MOTOR, true, "Permite subir blocos de 1 de altura sem precisar pular.");

            // Categoria Acessibilidade Visual
            enableNightVision = config.getBoolean("enableNightVision", CATEGORY_VISUAL, true, "Aplica Visão Noturna contínua e sem partículas de poção.");
            enableSonar = config.getBoolean("enableSonar", CATEGORY_VISUAL, true, "Destaca mobs hostis ao redor com partículas visíveis.");
            enableLightGrid = config.getBoolean("enableLightGrid", CATEGORY_VISUAL, true, "Exibe partículas nos blocos onde mobs hostis podem nascer (Nível de Luz <= 7).");

        } catch (Exception e) {
            System.err.println("Erro ao carregar as configurações do DuperMod!");
            e.printStackTrace();
        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }

    // Evento disparado caso a configuração seja alterada pela GUI do Forge no jogo
    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equalsIgnoreCase(DuperMod.MODID)) {
            loadConfig();
        }
    }
}