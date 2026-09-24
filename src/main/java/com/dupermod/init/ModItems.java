package com.dupermod.init;

import com.dupermod.DuperMod;
import com.dupermod.item.ItemAccessibilityRing;
import com.dupermod.item.ItemAllySummoner;
import com.dupermod.item.ItemCommandWand;
import com.dupermod.item.ItemCoolingArmor;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModItems {

    public static Item upgradeLogMultiplier;
    public static Item upgradeOreMultiplier;
    public static Item upgradeCharcoal;
    public static Item upgradeSpeed;
    public static Item accessibilityRing;
    public static Item allySummoner;
    public static Item commandWand;

    public static Item coolingHelmet;
    public static Item coolingChestplate;
    public static Item coolingLeggings;
    public static Item coolingBoots;
    public static Item coolingUpgrade;

    public static Item summonerLumberjack;
    public static Item summonerFarmer;
    public static Item summonerFighter;
    public static Item summonerCooler;
    public static Item summonerMiner;

    public static void init() {
        upgradeLogMultiplier = new Item().setUnlocalizedName("upgrade_log_multiplier");
        upgradeOreMultiplier = new Item().setUnlocalizedName("upgrade_ore_multiplier");
        upgradeCharcoal = new Item().setUnlocalizedName("upgrade_charcoal");
        upgradeSpeed = new Item().setUnlocalizedName("upgrade_speed");
        accessibilityRing = new ItemAccessibilityRing();
        allySummoner = new ItemAllySummoner("generic");
        commandWand = new ItemCommandWand();

        coolingHelmet = new ItemCoolingArmor(EntityEquipmentSlot.HEAD, "cooling_helmet");
        coolingChestplate = new ItemCoolingArmor(EntityEquipmentSlot.CHEST, "cooling_chestplate");
        coolingLeggings = new ItemCoolingArmor(EntityEquipmentSlot.LEGS, "cooling_leggings");
        coolingBoots = new ItemCoolingArmor(EntityEquipmentSlot.FEET, "cooling_boots");
        coolingUpgrade = new Item().setUnlocalizedName("cooling_upgrade");

        summonerLumberjack = new ItemAllySummoner("lumberjack");
        summonerFarmer = new ItemAllySummoner("farmer");
        summonerFighter = new ItemAllySummoner("fighter");
        summonerCooler = new ItemAllySummoner("cooler");
        summonerMiner = new ItemAllySummoner("miner");

        registerItem(upgradeLogMultiplier, "upgrade_log_multiplier");
        registerItem(upgradeOreMultiplier, "upgrade_ore_multiplier");
        registerItem(upgradeCharcoal, "upgrade_charcoal");
        registerItem(upgradeSpeed, "upgrade_speed");
        registerItem(accessibilityRing, "accessibility_ring");
        registerItem(allySummoner, "ally_summoner");
        registerItem(commandWand, "command_wand");

        registerItem(coolingHelmet, "cooling_helmet");
        registerItem(coolingChestplate, "cooling_chestplate");
        registerItem(coolingLeggings, "cooling_leggings");
        registerItem(coolingBoots, "cooling_boots");
        registerItem(coolingUpgrade, "cooling_upgrade");

        registerItem(summonerLumberjack, "summoner_lumberjack");
        registerItem(summonerFarmer, "summoner_farmer");
        registerItem(summonerFighter, "summoner_fighter");
        registerItem(summonerCooler, "summoner_cooler");
        registerItem(summonerMiner, "summoner_miner");
    }

    private static void registerItem(Item item, String name) {
        if (item.getRegistryName() == null) {
            item.setRegistryName(new ResourceLocation(DuperMod.MODID, name));
        }

        GameRegistry.register(item);
        DuperMod.proxy.registerItemRenderer(item, 0, name);
    }

    public static void registerRecipes() {
        GameRegistry.addRecipe(new ItemStack(accessibilityRing),
                " R ", "RER", " R ",
                'R', Items.REDSTONE,
                'E', Items.EMERALD);

        GameRegistry.addRecipe(new ItemStack(summonerLumberjack),
                " W ", " W ", " S ",
                'W', Items.WOODEN_AXE,
                'S', Items.STICK);

        GameRegistry.addRecipe(new ItemStack(summonerFarmer),
                " H ", " H ", " S ",
                'H', Items.WOODEN_HOE,
                'S', Items.STICK);

        GameRegistry.addRecipe(new ItemStack(summonerFighter),
                " S ", " S ", " S ",
                'S', Items.IRON_SWORD);

        GameRegistry.addRecipe(new ItemStack(upgradeLogMultiplier),
                " D ", " L ", " D ",
                'D', Items.DIAMOND,
                'L', Items.OAK_DOOR);

        GameRegistry.addRecipe(new ItemStack(upgradeCharcoal),
                " C ", " F ", " C ",
                'C', Items.COAL,
                'F', Items.FLINT);

        GameRegistry.addRecipe(new ItemStack(upgradeSpeed),
                " S ", " R ", " S ",
                'S', Items.FEATHER,
                'R', Items.REDSTONE);

        GameRegistry.addRecipe(new ItemStack(summonerCooler),
                " I ", " I ", " S ",
                'I', Blocks.ICE,
                'S', Items.STICK);

        GameRegistry.addRecipe(new ItemStack(summonerMiner),
                " P ", " P ", " S ",
                'P', Items.IRON_PICKAXE,
                'S', Items.STICK);

        GameRegistry.addShapelessRecipe(new ItemStack(coolingHelmet), Items.LEATHER_HELMET, coolingUpgrade);
        GameRegistry.addShapelessRecipe(new ItemStack(coolingChestplate), Items.LEATHER_CHESTPLATE, coolingUpgrade);
        GameRegistry.addShapelessRecipe(new ItemStack(coolingLeggings), Items.LEATHER_LEGGINGS, coolingUpgrade);
        GameRegistry.addShapelessRecipe(new ItemStack(coolingBoots), Items.LEATHER_BOOTS, coolingUpgrade);
    }
}