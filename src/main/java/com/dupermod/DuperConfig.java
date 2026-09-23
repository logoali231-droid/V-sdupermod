package com.dupermod;

import net.minecraftforge.common.config.Configuration;
import java.io.File;

public class DuperConfig {

    public static Configuration config;

    // Opções configuráveis
    public static int duplicationTicks = 100;
    public static boolean enableParticles = true;

    public static void init(File file) {
        config = new Configuration(file);
        loadConfig();
    }

    public static void loadConfig() {
        try {
            config.load();

            // Definir categorias, propriedades e valores padrão
            duplicationTicks = config.getInt("duplicationTicks", Configuration.CATEGORY_GENERAL, 100, 1, 1200, "Tempo em ticks para duplicar um item.");
            enableParticles = config.getBoolean("enableParticles", Configuration.CATEGORY_GENERAL, true, "Ativar efeitos de partículas nos duplicadores.");

        } catch (Exception e) {
            System.err.println("Erro ao carregar o ficheiro de configuração do DuperMod!");
            e.printStackTrace();
        } finally {
            if (config.hasChanged()) {
                config.save();
            }
        }
    }
}