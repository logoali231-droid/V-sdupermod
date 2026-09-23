package com.dupermod.item;

import com.dupermod.entity.EntityAllyBase;
import com.dupermod.entity.EntityFarmer;
import com.dupermod.entity.EntityFighter;
import com.dupermod.entity.EntityLumberjack;
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

    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            BlockPos spawnPos = pos.offset(facing);
            EntityAllyBase ally;

            if ("lumberjack".equals(allyType)) {
                ally = new EntityLumberjack(worldIn);
            } else if ("farmer".equals(allyType)) {
                ally = new EntityFarmer(worldIn);
            }  else if ("cooler".equals(allyType)) {
                ally = new EntityCooler(worldIn);
            } else {
                ally = new EntityFighter(worldIn);
            }else if ("miner".equals(allyType)) {
                ally = new EntityMiner(worldIn);
            }



            ally.setPosition(spawnPos.getX() + 0.5, spawnPos.getY(), spawnPos.getZ() + 0.5);
            ally.setOwner(player);
            worldIn.spawnEntityInWorld(ally);

            if (!player.capabilities.isCreativeMode) {
                stack.stackSize--;
            }
        }
        return EnumActionResult.SUCCESS;
    }
}