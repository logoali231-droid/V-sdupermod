package com.dupermod.init;

import com.dupermod.DuperMod;
import com.dupermod.item.ItemCoolingArmor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModItems {

    public static Item coolingUpgrade;
    public static Item coolingHelmet;
    public static Item coolingChestplate;
    public static Item coolingLeggings;
    public static Item coolingBoots;

    public static void init() {
        // Upgrade de Arrefecimento
        coolingUpgrade = new Item().setUnlocalizedName("cooling_upgrade").setRegistryName("cooling_upgrade").setCreativeTab(CreativeTabs.MISC);
        GameRegistry.register(coolingUpgrade);
        DuperMod.proxy.registerItemRenderer(coolingUpgrade, 0, "cooling_upgrade");

        // Armaduras
        coolingHelmet = new ItemCoolingArmor(EntityEquipmentSlot.HEAD, "cooling_helmet");
        coolingChestplate = new ItemCoolingArmor(EntityEquipmentSlot.CHEST, "cooling_chestplate");
        coolingLeggings = new ItemCoolingArmor(EntityEquipmentSlot.LEGS, "cooling_leggings");
        coolingBoots = new ItemCoolingArmor(EntityEquipmentSlot.FEET, "cooling_boots");

        registerItem(coolingHelmet, "cooling_helmet");
        registerItem(coolingChestplate, "cooling_chestplate");
        registerItem(coolingLeggings, "cooling_leggings");
        registerItem(coolingBoots, "cooling_boots");
    }

    private static void registerItem(Item item, String name) {
        GameRegistry.register(item);
        DuperMod.proxy.registerItemRenderer(item, 0, name);
    }

    public static void registerRecipes() {
        // Receita do Upgrade (Gelo + Bola de Neve + Redstone)
        GameRegistry.addRecipe(new ItemStack(coolingUpgrade),
                "SGS", "GRG", "SGS",
                'S', Items.SNOWBALL, 'G', Blocks.ICE, 'R', Items.REDSTONE
        );

        // Receitas da Armadura de Arrefecimento (Peça de Couro + Upgrade)
        GameRegistry.addShapelessRecipe(new ItemStack(coolingHelmet), Items.LEATHER_HELMET, coolingUpgrade);
        GameRegistry.addShapelessRecipe(new ItemStack(coolingChestplate), Items.LEATHER_CHESTPLATE, coolingUpgrade);
        GameRegistry.addShapelessRecipe(new ItemStack(coolingLeggings), Items.LEATHER_LEGGINGS, coolingUpgrade);
        GameRegistry.addShapelessRecipe(new ItemStack(coolingBoots), Items.LEATHER_BOOTS, coolingUpgrade);
    }
}