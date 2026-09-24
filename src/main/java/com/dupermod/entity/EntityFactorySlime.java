package com.dupermod.entity;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.Random;

public class EntityFactorySlime extends EntityAllyBase {

    // Inventário interno de 54 slots (Baú duplo)
    private ItemStackHandler internalInventory = new ItemStackHandler(54);

    // Filas de processamento
    private int gravelQueue = 0;
    private int dirtQueue = 0;
    private int sandQueue = 0;
    private int dustQueue = 0;
    private State currentState = State.IDLE;
    private int workTimer = 0;
    private int stuckTimer = 0;
    private Random rand = new Random();
    public EntityFactorySlime(World worldIn) {
        super(worldIn);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.worldObj.isRemote) return;

        switch (currentState) {
            case IDLE:
                if (inputPos != null && outputPos != null && hasMaterialsInInput()) {
                    currentState = State.GOING_TO_INPUT;
                }
                break;

            case GOING_TO_INPUT:
                if (this.getDistanceSqToCenter(inputPos) < 4.0D) {
                    currentState = State.PROCESSING;
                } else {
                    moveToPos(inputPos);
                }
                break;

            case PROCESSING:
                processMaterials();
                currentState = State.GOING_TO_OUTPUT;
                break;

            case GOING_TO_OUTPUT:
                if (this.getDistanceSqToCenter(outputPos) < 4.0D) {
                    dumpInventory();
                    currentState = State.IDLE;
                } else {
                    moveToPos(outputPos);
                }
                break;
        }
    }

    private void moveToPos(BlockPos pos) {
        this.getNavigator().tryMoveToXYZ(pos.getX(), pos.getY(), pos.getZ(), 1.0D);
        if (this.getNavigator().noPath()) {
            stuckTimer++;
            if (stuckTimer > 100) {
                this.setPositionAndUpdate(pos.getX(), pos.getY() + 1, pos.getZ());
                stuckTimer = 0;
            }
        } else {
            stuckTimer = 0;
        }
    }

    public boolean hasMaterialsInInput() {
        if (inputPos == null) return false;
        TileEntity te = worldObj.getTileEntity(inputPos);
        if (te != null && te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP)) {
            IItemHandler chestInv = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
            for (int i = 0; i < chestInv.getSlots(); i++) {
                ItemStack stack = chestInv.getStackInSlot(i);
                if (stack != null && stack.getItem() == Item.getItemFromBlock(Blocks.COBBLESTONE)) {
                    return true;
                }
            }
        }
        return false;
    }

    private void extractCobblestone() {
        TileEntity te = worldObj.getTileEntity(this.inputPos);
        if (te != null && te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP)) {
            IItemHandler chestInv = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
            int totalExtracted = 0;
            int maxToExtract = 256;

            for (int i = 0; i < chestInv.getSlots(); i++) {
                ItemStack stack = chestInv.getStackInSlot(i);
                if (stack != null && stack.getItem() == Item.getItemFromBlock(Blocks.COBBLESTONE)) {
                    ItemStack extracted = chestInv.extractItem(i, maxToExtract - totalExtracted, false);
                    if (extracted != null) totalExtracted += extracted.stackSize;
                }
                if (totalExtracted >= maxToExtract) break;
            }

            if (totalExtracted > 0) {
                int perQueue = totalExtracted / 4;
                gravelQueue += perQueue;
                dirtQueue += perQueue;
                sandQueue += perQueue;
                dustQueue += (totalExtracted - (perQueue * 3));
                this.playSound(net.minecraft.init.SoundEvents.BLOCK_ANVIL_USE, 0.5F, 1.5F);
            } else {
                currentState = State.IDLE;
            }
        }
    }

    private void processMaterials() {
        extractCobblestone();

        ItemStack result = new ItemStack(Blocks.COBBLESTONE, 2);

        ItemStack remainder = ItemHandlerHelper.insertItemStacked(this.internalInventory, result, false);
        if (remainder != null && remainder.stackSize > 0) {
            EntityItem itemEntity = new EntityItem(this.worldObj, this.posX, this.posY, this.posZ, remainder);
            this.worldObj.spawnEntityInWorld(itemEntity);
        }
    }

    private void dumpInventory() {
        if (outputPos == null) {
            currentState = State.IDLE;
            return;
        }

        TileEntity te = worldObj.getTileEntity(this.outputPos);
        if (te != null && te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP)) {
            IItemHandler chestInv = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
            for (int i = 0; i < internalInventory.getSlots(); i++) {
                ItemStack stack = internalInventory.getStackInSlot(i);
                if (stack != null) {
                    ItemStack remainder = ItemHandlerHelper.insertItemStacked(chestInv, stack, false);
                    internalInventory.setStackInSlot(i, remainder);
                }
            }
            this.playSound(net.minecraft.init.SoundEvents.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);
        } else {
            currentState = State.IDLE;
        }
    }

    private boolean queuesEmpty() {
        return gravelQueue == 0 && dirtQueue == 0 && sandQueue == 0 && dustQueue == 0;
    }

    public boolean isInventoryFull() {
        int usedSlots = 0;
        for (int i = 0; i < internalInventory.getSlots(); i++) {
            if (internalInventory.getStackInSlot(i) != null) usedSlots++;
        }
        return usedSlots >= 50;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setTag("InternalInventory", internalInventory.serializeNBT());
        compound.setInteger("GravelQ", gravelQueue);
        compound.setInteger("DirtQ", dirtQueue);
        compound.setInteger("SandQ", sandQueue);
        compound.setInteger("DustQ", dustQueue);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasKey("InternalInventory")) {
            internalInventory.deserializeNBT(compound.getCompoundTag("InternalInventory"));
        }
        gravelQueue = compound.getInteger("GravelQ");
        dirtQueue = compound.getInteger("DirtQ");
        sandQueue = compound.getInteger("SandQ");
        dustQueue = compound.getInteger("DustQ");
    }

    private enum State {IDLE, GOING_TO_INPUT, PROCESSING, GOING_TO_OUTPUT}
}