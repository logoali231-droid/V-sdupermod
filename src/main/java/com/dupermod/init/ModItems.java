package com.dupermod.init;

import com.dupermod.DuperMod;
import com.dupermod.item.ItemAccessibilityRing;
import com.dupermod.item.ItemAllySummoner;
import com.dupermod.item.ItemCoolingArmor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ModItems {

    // Arrefecimento
    public static Item coolingUpgrade;
    public static Item coolingHelmet;
    public static Item coolingChestplate;
    public static Item coolingLeggings;
    public static Item coolingBoots;

    // Acessibilidade
    public static Item accessibilityRing;

    // Invocadores de Aliados
    public static Item summonerLumberjack;
    public static Item summonerFarmer;
    public static Item summonerFighter;

    // Upgrades do Lenhador
    public static Item upgradeLogMultiplier;
    public static Item upgradeCharcoal;
    public static Item upgradeSpeed;
    public static Item summonerCooler;

    public static void init() {
        // Upgrade de Arrefecimento
        coolingUpgrade = new Item() {
            @Override
            @SideOnly(Side.CLIENT)
            public boolean hasEffect(ItemStack stack) {
                return true;
            }
        }.setUnlocalizedName("cooling_upgrade").setRegistryName("cooling_upgrade").setCreativeTab(CreativeTabs.MISC);

        GameRegistry.register(coolingUpgrade);
        DuperMod.proxy.registerItemRenderer(coolingUpgrade, 0, "cooling_upgrade");

        // Anel de Acessibilidade
        accessibilityRing = new ItemAccessibilityRing();
        registerItem(accessibilityRing, "accessibility_ring");

        summonerCooler = new ItemAllySummoner("cooler");
        registerItem(summonerCooler, "summoner_cooler");

        // Armaduras de Arrefecimento
        coolingHelmet = new ItemCoolingArmor(EntityEquipmentSlot.HEAD, "cooling_helmet");
        coolingChestplate = new ItemCoolingArmor(EntityEquipmentSlot.CHEST, "cooling_chestplate");
        coolingLeggings = new ItemCoolingArmor(EntityEquipmentSlot.LEGS, "cooling_leggings");
        coolingBoots = new ItemCoolingArmor(EntityEquipmentSlot.FEET, "cooling_boots");

        registerItem(coolingHelmet, "cooling_helmet");
        registerItem(coolingChestplate, "cooling_chestplate");
        registerItem(coolingLeggings, "cooling_leggings");
        registerItem(coolingBoots, "cooling_boots");

        // Invocadores dos Aliados (Slimes)
        summonerLumberjack = new ItemAllySummoner("lumberjack");
        summonerFarmer = new ItemAllySummoner("farmer");
        summonerFighter = new ItemAllySummoner("fighter");


        registerItem(summonerLumberjack, "summoner_lumberjack");
        registerItem(summonerFarmer, "summoner_farmer");
        registerItem(summonerFighter, "summoner_fighter");

        // Upgrades do Lenhador
        upgradeLogMultiplier = new Item().setUnlocalizedName("upgrade_log_multiplier").setRegistryName("upgrade_log_multiplier").setCreativeTab(CreativeTabs.MISC);
        upgradeCharcoal = new Item().setUnlocalizedName("upgrade_charcoal").setRegistryName("upgrade_charcoal").setCreativeTab(CreativeTabs.MISC);
        upgradeSpeed = new Item().setUnlocalizedName("upgrade_speed").setRegistryName("upgrade_speed").setCreativeTab(CreativeTabs.MISC);

        registerItem(upgradeLogMultiplier, "upgrade_log_multiplier");
        registerItem(upgradeCharcoal, "upgrade_charcoal");
        registerItem(upgradeSpeed, "upgrade_speed");
    }

    private static void registerItem(Item item, String name) {
        GameRegistry.register(item);
        DuperMod.proxy.registerItemRenderer(item, 0, name);
    }

    public static void registerRecipes() {
        // Receita do Upgrade de Arrefecimento
        GameRegistry.addRecipe(new ItemStack(coolingUpgrade),
                "SGS", "GRG", "SGS",
                'S', Items.SNOWBALL, 'G', Blocks.ICE, 'R', Items.REDSTONE
        );

        // Receita do Anel de Acessibilidade
        GameRegistry.addRecipe(new ItemStack(accessibilityRing),
                " G ", "GCG", " G ",
                'G', Items.GOLD_INGOT, 'C', Items.COMPASS
        );

        // Receitas dos Invocadores
        GameRegistry.addRecipe(new ItemStack(summonerLumberjack),
                " A ", " S ", "   ",
                'A', Items.IRON_AXE, 'S', Items.SLIME_BALL
        );

        GameRegistry.addRecipe(new ItemStack(summonerFarmer),
                " H ", " S ", "   ",
                'H', Items.IRON_HOE, 'S', Items.SLIME_BALL
        );

        GameRegistry.addRecipe(new ItemStack(summonerFighter),
                " S ", " X ", "   ",
                'S', Items.IRON_SWORD, 'X', Items.SLIME_BALL
        );

        // Receitas dos Upgrades do Lenhador
        // 1. Multiplicador: Diamante + Bola de Slime + Machado de Ouro
        GameRegistry.addRecipe(new ItemStack(upgradeLogMultiplier),
                " D ", " S ", " A ",
                'D', Items.DIAMOND, 'S', Items.SLIME_BALL, 'A', Items.GOLDEN_AXE
        );

        // 2. Carvão: Forno + Bola de Slime + Carvão
        GameRegistry.addRecipe(new ItemStack(upgradeCharcoal),
                " C ", " S ", " F ",
                'C', Items.COAL, 'S', Items.SLIME_BALL, 'F', Blocks.FURNACE
        );

        // 3. Velocidade: Açúcar + Bola de Slime + Pena
        GameRegistry.addRecipe(new ItemStack(upgradeSpeed),
                " S ", " X ", " P ",
                'S', Items.SUGAR, 'X', Items.SLIME_BALL, 'P', Items.FEATHER
        );
        GameRegistry.addRecipe(new ItemStack(summonerCooler),
                " I ", " S ", "   ",
                'I', Blocks.PACKED_ICE, 'S', Items.SLIME_BALL
        );

        // Receitas das Armaduras de Arrefecimento
        GameRegistry.addShapelessRecipe(new ItemStack(coolingHelmet), Items.LEATHER_HELMET, coolingUpgrade);
        GameRegistry.addShapelessRecipe(new ItemStack(coolingChestplate), Items.LEATHER_CHESTPLATE, coolingUpgrade);
        GameRegistry.addShapelessRecipe(new ItemStack(coolingLeggings), Items.LEATHER_LEGGINGS, coolingUpgrade);
        GameRegistry.addShapelessRecipe(new ItemStack(coolingBoots), Items.LEATHER_BOOTS, coolingUpgrade);
    }
}