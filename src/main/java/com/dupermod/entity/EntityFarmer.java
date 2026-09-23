package com.dupermod.entity;

import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.List;

public class EntityFarmer extends EntityAllyBase {

    private int workTimer = 0;

    public EntityFarmer(World worldIn) {
        super(worldIn);
        this.setSize(0.5F, 0.5F); // Slime menor para o agricultor
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (this.worldObj.isRemote || this.isRecovering) return;

        workTimer++;
        if (workTimer >= 30) { // Trabalha a cada 1.5s
            workTimer = 0;
            harvestCrops();
        }
    }

    private void harvestCrops() {
        BlockPos pos = new BlockPos(this);
        int radius = 6;

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -2; y <= 2; y++) {
                    BlockPos targetPos = pos.add(x, y, z);
                    IBlockState state = worldObj.getBlockState(targetPos);

                    if (state.getBlock() instanceof BlockCrops) {
                        BlockCrops crop = (BlockCrops) state.getBlock();
                        if (crop.isMaxAge(state)) { // Plantação totalmente desenvolvida
                            List<ItemStack> drops = crop.getDrops(worldObj, targetPos, state, 0);

                            // Replanta
                            worldObj.setBlockState(targetPos, crop.withAge(0));

                            // Guarda os frutos/sementes no inventário
                            for (ItemStack drop : drops) {
                                ItemHandlerHelper.insertItemStacked(this.inventory, drop, false);
                            }
                            return;
                        }
                    }
                }
            }
        }
    }
}