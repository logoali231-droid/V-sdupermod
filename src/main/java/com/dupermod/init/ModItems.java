package com.dupermod.init;

import com.dupermod.item.ItemAccessibilityRing;
import com.dupermod.item.ItemAllySummoner;
import com.dupermod.item.ItemCommandWand;
import com.dupermod.item.ItemCoolingArmor;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
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

    public static void init() {
        upgradeLogMultiplier = new Item().setUnlocalizedName("upgrade_log_multiplier");
        upgradeOreMultiplier = new Item().setUnlocalizedName("upgrade_ore_multiplier");
        upgradeCharcoal = new Item().setUnlocalizedName("upgrade_charcoal");
        upgradeSpeed = new Item().setUnlocalizedName("upgrade_speed");
        accessibilityRing = new ItemAccessibilityRing();
        allySummoner = new ItemAllySummoner();
        commandWand = new ItemCommandWand();
        coolingHelmet = new ItemCoolingArmor(ItemCoolingArmor.COOLING_MATERIAL, 1, EntityEquipmentSlot.HEAD);

        // FIXED: Removed duplicate registrations
        registerItem(upgradeLogMultiplier, "upgrade_log_multiplier");
        registerItem(upgradeOreMultiplier, "upgrade_ore_multiplier");
        registerItem(upgradeCharcoal, "upgrade_charcoal");
        registerItem(upgradeSpeed, "upgrade_speed");
        registerItem(accessibilityRing, "accessibility_ring");
        registerItem(allySummoner, "ally_summoner");
        registerItem(commandWand, "command_wand");
        registerItem(coolingHelmet, "cooling_helmet");
    }

    private static void registerItem(Item item, String name) {
        item.setRegistryName(new ResourceLocation("dupermod", name));
        GameRegistry.register(item);
    }
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

        GameRegistry.addRecipe(new ItemStack(summonerMiner),
                " P ", " S ", "   ",
                'P', Items.IRON_PICKAXE, 'S', Items.SLIME_BALL
        );

        // Receitas das Armaduras de Arrefecimento
        GameRegistry.addShapelessRecipe(new ItemStack(coolingHelmet), Items.LEATHER_HELMET, coolingUpgrade);
        GameRegistry.addShapelessRecipe(new ItemStack(coolingChestplate), Items.LEATHER_CHESTPLATE, coolingUpgrade);
        GameRegistry.addShapelessRecipe(new ItemStack(coolingLeggings), Items.LEATHER_LEGGINGS, coolingUpgrade);
        GameRegistry.addShapelessRecipe(new ItemStack(coolingBoots), Items.LEATHER_BOOTS, coolingUpgrade);
    }
}