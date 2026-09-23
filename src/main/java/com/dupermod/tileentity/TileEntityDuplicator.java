package com.dupermod.tileentity;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class TileEntityDuplicator extends TileEntity implements ITickable {
    private int tier = 1;
    private int tickCount = 0;
    private int maxTicks = 100;

    public TileEntityDuplicator() {}

    public TileEntityDuplicator(int tier) {
        this.tier = tier;
        this.maxTicks = Math.max(10, 120 - (tier * 20)); // T1: 100t, T2: 80t, T3: 60t, T4: 40t, T5: 20t
    }

    @Override
    public void update() {
        if (this.getWorld() == null || this.getWorld().isRemote) return;

        tickCount++;
        if (tickCount >= maxTicks) {
            tickCount = 0;
            duplicateBlock();
        }
    }

    private void duplicateBlock() {
        IBlockState stateAbove = this.getWorld().getBlockState(this.getPos().up());
        Block blockAbove = stateAbove.getBlock();

        if (blockAbove.isAir(stateAbove, this.getWorld(), this.getPos().up())) return;

        ItemStack stackToDupe = blockAbove.getItem(this.getWorld(), this.getPos().up(), stateAbove);
        if (stackToDupe == null || stackToDupe.getItem() == null) return;

        stackToDupe.stackSize = 1;

        for (EnumFacing facing : EnumFacing.values()) {
            if (facing == EnumFacing.UP) continue;

            TileEntity adjacentTE = this.getWorld().getTileEntity(this.getPos().offset(facing));

            if (adjacentTE != null && adjacentTE.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite())) {
                IItemHandler inventory = adjacentTE.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, facing.getOpposite());

                if (inventory != null) {
                    ItemStack remainder = ItemHandlerHelper.insertItemStacked(inventory, stackToDupe.copy(), false);

                    if (remainder == null || remainder.stackSize == 0) {
                        return;
                    }
                }
            }
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.tier = compound.getInteger("Tier");
        if (this.tier <= 0) this.tier = 1;
        this.maxTicks = Math.max(10, 120 - (this.tier * 20));
        this.tickCount = compound.getInteger("TickCount");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("Tier", this.tier);
        compound.setInteger("TickCount", this.tickCount);
        return compound;
    }
}