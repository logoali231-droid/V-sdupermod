package com.dupermod.item;

import com.dupermod.entity.EntityFighter;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemAllySummoner extends Item {

    private final String allyType;

    public ItemAllySummoner(String allyType) {
        this.allyType = allyType;
        this.setUnlocalizedName("summoner_" + allyType);
        this.setRegistryName("summoner_" + allyType);
        this.setCreativeTab(CreativeTabs.MISC);
        this.setMaxStackSize(16);
    }

    public String getAllyType() {
        return allyType;
    }

    // No Forge 1.10.2, o PRIMEIRO parâmetro é o ItemStack stack
    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            BlockPos spawnPos = pos.offset(facing);

            // Instancia o aliado (EntityFighter)
            EntityFighter fighter = new EntityFighter(worldIn);
            fighter.setPosition(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D);
            fighter.setOwner(player); // Define o jogador como dono

            worldIn.spawnEntityInWorld(fighter);

            // Consome o item se o jogador não estiver no Criativo
            if (!player.capabilities.isCreativeMode) {
                stack.stackSize--;
                if (stack.stackSize <= 0) {
                    player.setHeldItem(hand, null);
                }
            }
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }
}