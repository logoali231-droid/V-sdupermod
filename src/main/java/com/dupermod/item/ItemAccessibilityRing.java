package com.dupermod.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class ItemAccessibilityRing extends Item {

    public ItemAccessibilityRing() {
        this.setUnlocalizedName("accessibility_ring");
        this.setRegistryName("accessibility_ring");
        this.setCreativeTab(CreativeTabs.TOOLS);
        this.setMaxStackSize(1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return true; // Mantém o brilho encantado contínuo no item
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        tooltip.add("§7Mantenha no inventário para ativar:");
        tooltip.add("§8- §aSubida Automática de Degraus (Step Assist)");
        tooltip.add("§8- §aAtração de Itens Próximos (Magnet)");
        tooltip.add("§8- §aAlimentação Automática (Auto-Eat)");
        tooltip.add("§8- §aVisão Noturna Contínua");
        tooltip.add("§8- §aSonar de Mobs Hostis");
        tooltip.add("§8- §aGrid de Nível de Luz / Spawn");
        tooltip.add("§8- §aTroca Automática de Ferramenta (Auto-Tool)");
    }
}