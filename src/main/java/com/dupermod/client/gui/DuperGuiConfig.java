package com.dupermod.client.gui;

import com.dupermod.DuperConfig;
import com.dupermod.DuperMod;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.fml.client.config.DummyConfigElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

import java.util.ArrayList;
import java.util.List;

public class DuperGuiConfig extends GuiConfig {
    public DuperGuiConfig(GuiScreen parent) {
        super(parent, getConfigElements(), DuperMod.MODID, false, false, "DuperMod Config");
    }

    private static List<IConfigElement> getConfigElements() {
        List<IConfigElement> list = new ArrayList<>();
        list.add(new ConfigElement(DuperConfig.config.getCategory("general")));
        list.add(new ConfigElement(DuperConfig.config.getCategory("cooling")));
        return list;
    }
}