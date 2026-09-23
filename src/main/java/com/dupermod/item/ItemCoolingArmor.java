package com.dupermod.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCoolingArmor extends ItemArmor {

    public static final ArmorMaterial COOLING_MATERIAL = EnumHelper.addArmorMaterial(
            "cooling_material",
            "leather",
            15,
            new int[]{2, 5, 4, 1},
            12,
            net.minecraft.init.SoundEvents.ITEM_ARMOR_EQUIP_LEATHER,
            0.0F
    );

    public ItemCoolingArmor(EntityEquipmentSlot slot, String name) {
        super(COOLING_MATERIAL, 1, slot);
        this.setUnlocalizedName(name);
        this.setRegistryName(name);
        this.setCreativeTab(CreativeTabs.COMBAT);
    }

    // Aplica o brilho de encantamento permanentemente
    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return true;
    }
}