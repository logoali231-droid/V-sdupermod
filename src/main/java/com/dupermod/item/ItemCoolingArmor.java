package com.dupermod.item;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraftforge.common.util.EnumHelper;

public class ItemCoolingArmor extends ItemArmor {

    public static final ArmorMaterial COOLING_MATERIAL = EnumHelper.addArmorMaterial(
            "COOLING",
            "dupermod:cooling",
            15,
            new int[]{2, 5, 4, 1},
            12,
            null,
            0.0F
    );

    public ItemCoolingArmor(EntityEquipmentSlot equipmentSlotIn, String name) {
        super(COOLING_MATERIAL, 1, equipmentSlotIn);
        setUnlocalizedName(name);
    }
}