package com.dupermod.client.gui;

import com.dupermod.DuperConfig;
import com.dupermod.DuperMod;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;

public class DuperGuiConfig extends GuiConfig {
    public DuperGuiConfig(GuiScreen parent) {
        super(parent,
                new ConfigElement(DuperConfig.config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(),
                DuperMod.MODID,
                false,
                false,
                GuiConfig.getAbridgedConfigPath(DuperConfig.config.toString()));
    }
}