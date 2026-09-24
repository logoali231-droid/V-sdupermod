package com.dupermod.entity;

import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityFarmer extends EntityAllyBase {

    public EntityFarmer(World worldIn) {
        super(worldIn);
    }

    public void harvestCrops() {
        if (this.world.isRemote) return;

        BlockPos origin = new BlockPos(this);
        int radius = 4;

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) { // FIXED: Corrected loop increment from x++ to z++
                BlockPos targetPos = origin.add(x, 0, z);
                IBlockState state = this.world.getBlockState(targetPos);

                if (state.getBlock() instanceof BlockCrops) {
                    BlockCrops crop = (BlockCrops) state.getBlock();
                    if (crop.isMaxAge(state)) {
                        this.world.destroyBlock(targetPos, true);
                        this.world.setBlockState(targetPos, crop.getStateFromMeta(0));
                    }
                }
            }
        }
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (!this.world.isRemote && this.ticksExisted % 40 == 0) {
            harvestCrops();
        }
    }
}