package com.dupermod.item;

import com.dupermod.entity.EntityFighter;
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
        setUnlocalizedName("summoner_" + allyType);
        setMaxStackSize(1);
    }

    public String getAllyType() {
        return allyType;
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            BlockPos spawnPos = pos.offset(facing);
            EntityFighter fighter = new EntityFighter(worldIn);
            fighter.setPosition(spawnPos.getX() + 0.5D, spawnPos.getY(), spawnPos.getZ() + 0.5D);
            worldIn.spawnEntityInWorld(fighter);

            ItemStack stack = player.getHeldItem(hand);
            if (stack != null) {
                stack.stackSize--;
                if (stack.stackSize <= 0) {
                    player.setHeldItem(hand, null);
                }
            }
        }
        return EnumActionResult.SUCCESS;
    }
}