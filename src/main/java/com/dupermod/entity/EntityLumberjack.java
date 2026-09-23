package com.dupermod.entity;

import net.minecraft.block.BlockLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.List;

public class EntityLumberjack extends EntityAllyBase {

    private int workTimer = 0;

    public EntityLumberjack(World worldIn) {
        super(worldIn);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (this.worldObj.isRemote || this.isRecovering) return;

        workTimer++;
        if (workTimer >= 40) { // Trabalha a cada 2 segundos
            workTimer = 0;
            findAndCutTree();
        }
    }

    private void findAndCutTree() {
        BlockPos pos = new BlockPos(this);
        int radius = 8;

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -2; y <= 5; y++) {
                    BlockPos targetPos = pos.add(x, y, z);
                    IBlockState state = worldObj.getBlockState(targetPos);

                    if (state.getBlock() instanceof BlockLog) {
                        // Árvore encontrada! Derrubar em cadeia
                        List<BlockPos> treeBlocks = new ArrayList<>();
                        scanTree(targetPos, treeBlocks);

                        for (BlockPos logPos : treeBlocks) {
                            IBlockState logState = worldObj.getBlockState(logPos);
                            List<ItemStack> drops = logState.getBlock().getDrops(worldObj, logPos, logState, 0);
                            worldObj.setBlockToAir(logPos); // Quebra o bloco

                            // Guarda no inventário do aliado
                            for (ItemStack drop : drops) {
                                ItemHandlerHelper.insertItemStacked(this.inventory, drop, false);
                            }
                        }
                        return; // Processa uma árvore por ciclo
                    }
                }
            }
        }
    }

    private void scanTree(BlockPos pos, List<BlockPos> treeBlocks) {
        if (treeBlocks.size() >= 64) return; // Limite por árvore
        if (worldObj.getBlockState(pos).getBlock() instanceof BlockLog && !treeBlocks.contains(pos)) {
            treeBlocks.add(pos);
            for (int x = -1; x <= 1; x++) {
                for (int y = 0; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        scanTree(pos.add(x, y, z), treeBlocks);
                    }
                }
            }
        }
    }
}