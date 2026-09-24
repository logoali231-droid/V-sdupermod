package com.dupermod.entity;

import com.dupermod.init.ModItems;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
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

import java.util.ArrayList;
import java.util.List;

public class EntityLumberjack extends EntityAllyBase {

    public boolean hasMultiplierUpgrade = false;
    public boolean hasCharcoalUpgrade = false;
    public boolean hasSpeedUpgrade = false;
    public boolean hasBackpackUpgrade = false;
    public ItemStackHandler lumberInventory = new ItemStackHandler(9); //[cite: 2]
    private int workTimer = 0;
    private BlockPos targetTree = null;
    private State currentState = State.GATHERING;

    public EntityLumberjack(World worldIn) {
        super(worldIn);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (this.worldObj.isRemote || this.isRecovering || this.forceMoveTarget != null) return;

        if (this.outputPos != null && isInventoryFull() && currentState == State.GATHERING) {
            currentState = State.DELIVERING;
            targetTree = null;
        }

        if (currentState == State.DELIVERING) {
            this.getNavigator().tryMoveToXYZ(this.outputPos.getX(), this.outputPos.getY(), this.outputPos.getZ(), 1.0D);
            if (this.getDistanceSqToCenter(this.outputPos) < 4.0D) {
                dumpToOutputChest();
                currentState = State.GATHERING;
            }
            return;
        }

        workTimer++;
        int maxDelay = hasSpeedUpgrade ? 15 : 40;

        if (workTimer >= maxDelay) {
            workTimer = 0;

            if (targetTree == null) {
                targetTree = findNearestTree(8 + (this.getAllyLevel() - 1) * 3);
            }

            if (targetTree != null) {
                if (this.getDistanceSq(targetTree) > 4.0D) {
                    this.getNavigator().tryMoveToXYZ(targetTree.getX(), targetTree.getY(), targetTree.getZ(), 1.0D);
                } else {
                    cutTree(targetTree);
                    targetTree = null;
                }
            } else if (this.outputPos != null && hasItems()) {
                currentState = State.DELIVERING;
            }
        }
    }

    private BlockPos findNearestTree(int radius) {
        // Nova Mecânica: Procura centrado na Work Area
        BlockPos searchCenter = (this.workAreaCenter != null) ? this.workAreaCenter : new BlockPos(this);
        BlockPos closestTree = null;
        double closestDistance = Double.MAX_VALUE;

        BlockPos startPos = searchCenter.add(-radius, -2, -radius);
        BlockPos endPos = searchCenter.add(radius, 5, radius);

        for (BlockPos pos : BlockPos.getAllInBox(startPos, endPos)) {
            IBlockState state = this.worldObj.getBlockState(pos);
            if (state.getBlock() instanceof BlockLog) { //[cite: 2]
                if (hasLeavesAbove(pos, 7)) {
                    double distance = searchCenter.distanceSq(pos);
                    if (distance < closestDistance) {
                        closestDistance = distance;
                        closestTree = pos;
                    }
                }
            }
        }
        return closestTree;
    }

    private boolean hasLeavesAbove(BlockPos logPos, int maxHeight) {
        for (int i = 1; i <= maxHeight; i++) {
            BlockPos checkPos = logPos.up(i);
            IBlockState state = this.worldObj.getBlockState(checkPos);
            if (state.getBlock() instanceof BlockLeaves) return true; //[cite: 2]
        }
        return false;
    }

    private void cutTree(BlockPos startPos) {
        List<BlockPos> treeBlocks = new ArrayList<>();
        scanTree(startPos, treeBlocks);

        for (BlockPos logPos : treeBlocks) {
            IBlockState logState = worldObj.getBlockState(logPos);
            List<ItemStack> drops = logState.getBlock().getDrops(worldObj, logPos, logState, 0);
            worldObj.setBlockToAir(logPos);

            for (ItemStack drop : drops) {
                if (drop == null) continue;

                if (hasMultiplierUpgrade) drop.stackSize *= 2; //[cite: 2]

                if (hasCharcoalUpgrade) {
                    int total = drop.stackSize;
                    int charcoalAmount = total / 2;
                    int logAmount = total - charcoalAmount;

                    if (logAmount > 0) {
                        ItemStack logs = drop.copy();
                        logs.stackSize = logAmount;
                        storeOrDropItem(logs);
                    }
                    if (charcoalAmount > 0) {
                        ItemStack charcoal = new ItemStack(Items.COAL, charcoalAmount, 1);
                        storeOrDropItem(charcoal);
                    }
                } else {
                    storeOrDropItem(drop);
                }
            }
        }
    }

    private void scanTree(BlockPos pos, List<BlockPos> treeBlocks) {
        if (treeBlocks.size() >= 128) return;
        if (worldObj.getBlockState(pos).getBlock() instanceof BlockLog && !treeBlocks.contains(pos)) {
            treeBlocks.add(pos);
            for (int x = -1; x <= 1; x++) {
                for (int y = 0; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        scanTree(pos.add(x, y, z), treeBlocks); //[cite: 2]
                    }
                }
            }
        }
    }

    private void storeOrDropItem(ItemStack stack) {
        ItemStack remainder = ItemHandlerHelper.insertItemStacked(lumberInventory, stack, false);
        if (remainder != null && remainder.stackSize > 0) {
            EntityItem entityItem = new EntityItem(worldObj, this.posX, this.posY, this.posZ, remainder);
            worldObj.spawnEntityInWorld(entityItem);
        }
    }

    public boolean isInventoryFull() {
        int emptySlots = 0;
        for (int i = 0; i < lumberInventory.getSlots(); i++) {
            if (lumberInventory.getStackInSlot(i) == null) emptySlots++;
        }
        return emptySlots <= 1;
    }

    private boolean hasItems() {
        for (int i = 0; i < lumberInventory.getSlots(); i++) {
            if (lumberInventory.getStackInSlot(i) != null) return true;
        }
        return false;
    }

    private void dumpToOutputChest() {
        TileEntity te = worldObj.getTileEntity(this.outputPos);
        if (te != null && te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP)) {
            IItemHandler chestInv = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
            for (int i = 0; i < lumberInventory.getSlots(); i++) {
                ItemStack stack = lumberInventory.getStackInSlot(i);
                if (stack != null) {
                    ItemStack remainder = ItemHandlerHelper.insertItemStacked(chestInv, stack, false);
                    lumberInventory.setStackInSlot(i, remainder);
                }
            }
            this.playSound(net.minecraft.init.SoundEvents.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);
        }
    }

    private void dumpInventoryToPlayer() {
        boolean hasItems = false;
        for (int i = 0; i < lumberInventory.getSlots(); i++) {
            ItemStack stack = lumberInventory.getStackInSlot(i);
            if (stack != null && stack.stackSize > 0) {
                EntityItem entityItem = new EntityItem(worldObj, this.posX, this.posY, this.posZ, stack.copy());
                worldObj.spawnEntityInWorld(entityItem);
                lumberInventory.setStackInSlot(i, null);
                hasItems = true;
            }
        }
        if (hasItems) this.playSound(net.minecraft.init.SoundEvents.ENTITY_ITEM_PICKUP, 1.0F, 1.0F); //[cite: 2]
    }

    private void expandInventory() {
        ItemStackHandler newInv = new ItemStackHandler(27);
        for (int i = 0; i < lumberInventory.getSlots(); i++) {
            newInv.setStackInSlot(i, lumberInventory.getStackInSlot(i));
        }
        lumberInventory = newInv;
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand, ItemStack stack) {
        if (!worldObj.isRemote) {
            if (player.isSneaking() && stack == null) {
                dumpInventoryToPlayer();
                player.addChatMessage(new TextComponentString("§eLenhador: Aqui estão meus recursos!"));
                return true;
            }

            if (stack != null) {
                if (stack.getItem() == Item.getItemFromBlock(Blocks.CHEST) && !hasBackpackUpgrade) {
                    hasBackpackUpgrade = true;
                    expandInventory();
                    consumeItem(player, stack);
                    player.addChatMessage(new TextComponentString("§aUpgrade: Mochila (Inventário Expandido)!")); //[cite: 2]
                    return true;
                }
                if (stack.getItem() == ModItems.upgradeLogMultiplier && !hasMultiplierUpgrade) {
                    hasMultiplierUpgrade = true;
                    consumeItem(player, stack);
                    player.addChatMessage(new TextComponentString("§aUpgrade: Multiplicador de Madeira!"));
                    return true;
                }
                if (stack.getItem() == ModItems.upgradeCharcoal && !hasCharcoalUpgrade) {
                    hasCharcoalUpgrade = true;
                    consumeItem(player, stack);
                    player.addChatMessage(new TextComponentString("§aUpgrade: Conversor de Carvão!"));
                    return true;
                }
                if (stack.getItem() == ModItems.upgradeSpeed && !hasSpeedUpgrade) {
                    hasSpeedUpgrade = true;
                    this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.35D);
                    consumeItem(player, stack);
                    player.addChatMessage(new TextComponentString("§aUpgrade: Velocidade!"));
                    return true;
                }
            }
        }
        return super.processInteract(player, hand, stack);
    }

    private void consumeItem(EntityPlayer player, ItemStack stack) {
        if (!player.capabilities.isCreativeMode) stack.stackSize--;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("UpgradeMultiplier", hasMultiplierUpgrade);
        compound.setBoolean("UpgradeCharcoal", hasCharcoalUpgrade);
        compound.setBoolean("UpgradeSpeed", hasSpeedUpgrade);
        compound.setBoolean("UpgradeBackpack", hasBackpackUpgrade);
        compound.setTag("LumberInventory", lumberInventory.serializeNBT());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.hasMultiplierUpgrade = compound.getBoolean("UpgradeMultiplier");
        this.hasCharcoalUpgrade = compound.getBoolean("UpgradeCharcoal");
        this.hasSpeedUpgrade = compound.getBoolean("UpgradeSpeed");
        this.hasBackpackUpgrade = compound.getBoolean("UpgradeBackpack");

        if (this.hasBackpackUpgrade) this.lumberInventory = new ItemStackHandler(27);
        if (compound.hasKey("LumberInventory"))
            this.lumberInventory.deserializeNBT(compound.getCompoundTag("LumberInventory"));
        if (this.hasSpeedUpgrade) this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.35D);
    }

    private enum State {GATHERING, DELIVERING}
}