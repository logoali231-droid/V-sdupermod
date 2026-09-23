package com.dupermod.init;

import com.dupermod.DuperMod;
import com.dupermod.block.BlockDuplicator;
import com.dupermod.tileentity.TileEntityDuplicator;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ModBlocks {
    public static BlockDuplicator[] duplicators = new BlockDuplicator[5];

    public static void init() {
        for (int i = 1; i <= 5; i++) {
            duplicators[i - 1] = new BlockDuplicator(i);
            registerBlock(duplicators[i - 1]);
        }

        GameRegistry.registerTileEntity(TileEntityDuplicator.class, DuperMod.MODID + ":tile_entity_duplicator");
    }

    private static void registerBlock(BlockDuplicator block) {
        GameRegistry.register(block);
        ItemBlock itemBlock = new ItemBlock(block);
        itemBlock.setRegistryName(block.getRegistryName());
        GameRegistry.register(itemBlock);

        DuperMod.proxy.registerItemRenderer(Item.getItemFromBlock(block), 0, block.getRegistryName().getResourcePath());
    }

    public static void registerRecipes() {
        // Tier 1: Iron surround Redstone
        GameRegistry.addRecipe(new ItemStack(duplicators[0]),
                "III", "IRI", "III",
                'I', Items.IRON_INGOT, 'R', Items.REDSTONE
        );

        // Tier 2: Gold surround Tier 1
        GameRegistry.addRecipe(new ItemStack(duplicators[1]),
                "GGG", "GTG", "GGG",
                'G', Items.GOLD_INGOT, 'T', new ItemStack(duplicators[0])
        );

        // Tier 3: Diamond surround Tier 2
        GameRegistry.addRecipe(new ItemStack(duplicators[2]),
                "DDD", "DTD", "DDD",
                'D', Items.DIAMOND, 'T', new ItemStack(duplicators[1])
        );

        // Tier 4: Emerald surround Tier 3
        GameRegistry.addRecipe(new ItemStack(duplicators[3]),
                "EEE", "ETE", "EEE",
                'E', Items.EMERALD, 'T', new ItemStack(duplicators[2])
        );

        // Tier 5: Nether Star surround Tier 4
        GameRegistry.addRecipe(new ItemStack(duplicators[4]),
                "NNN", "NTN", "NNN",
                'N', Items.NETHER_STAR, 'T', new ItemStack(duplicators[3])
        );
    }
}