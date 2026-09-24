package com.dupermod.entity;

import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityFarmer extends EntityAllyBase {

    public EntityFarmer(World worldIn) {
        super(worldIn);
    }

    public void harvestCrops() {
        if (this.worldObj.isRemote) return;

        BlockPos origin = new BlockPos(this);
        int radius = 4;

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                BlockPos targetPos = origin.add(x, 0, z);
                IBlockState state = this.worldObj.getBlockState(targetPos);

                if (state.getBlock() instanceof BlockCrops) {
                    BlockCrops crop = (BlockCrops) state.getBlock();
                    if (crop.isMaxAge(state)) {
                        this.worldObj.destroyBlock(targetPos, true);
                        this.worldObj.setBlockState(targetPos, crop.getStateFromMeta(0));
                    }
                }
            }
        }
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (!this.worldObj.isRemote && this.ticksExisted % 40 == 0) {
            harvestCrops();
        }
    }
}