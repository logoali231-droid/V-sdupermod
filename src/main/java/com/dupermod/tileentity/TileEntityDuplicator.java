package com.dupermod.tileentity;

import com.dupermod.DuperConfig;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ITickable;
import net.minecraft.world.WorldServer;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class TileEntityDuplicator extends TileEntity implements ITickable {
    private int tier = 1;
    private int tickCount = 0;

    public TileEntityDuplicator() {}

    public TileEntityDuplicator(int tier) {
        this.tier = tier;
    }

    @Override
    public void update() {
        if (this.getWorld() == null || this.getWorld().isRemote) return;

        // Calcula o tempo dinamicamente a partir do config e escala pelo Tier
        int maxTicks = Math.max(10, DuperConfig.duplicationTicks - ((this.tier - 1) * 15));

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
                        // Se a duplicação ocorreu com sucesso e as partículas estão ativas na config
                        if (DuperConfig.enableParticles && this.getWorld() instanceof WorldServer) {
                            WorldServer ws = (WorldServer) this.getWorld();
                            ws.spawnParticle(
                                    EnumParticleTypes.SPELL_WITCH,
                                    this.getPos().getX() + 0.5,
                                    this.getPos().getY() + 1.1,
                                    this.getPos().getZ() + 0.5,
                                    10,   // quantidade de partículas
                                    0.2, 0.2, 0.2, // dispersão X, Y, Z
                                    0.05  // velocidade
                            );
                        }
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