package com.dupermod.entity;

import com.dupermod.init.ModItems;
import net.minecraft.block.BlockOre;
import net.minecraft.block.BlockRedstoneOre;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
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
import net.minecraftforge.oredict.OreDictionary;
import java.util.ArrayList;
import java.util.List;

public class EntityMiner extends EntityAllyBase {

    private int workTimer = 0;
    private BlockPos targetOre = null;

    private enum State { MINING, DELIVERING }
    private State currentState = State.MINING;

    public boolean hasMultiplierUpgrade = false;
    public boolean hasSmelterUpgrade = false;
    public boolean hasSpeedUpgrade = false;
    public boolean hasBackpackUpgrade = false;

    public ItemStackHandler minerInventory = new ItemStackHandler(9); //[cite: 2]

    public EntityMiner(World worldIn) {
        super(worldIn);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (this.worldObj.isRemote || this.isRecovering || this.forceMoveTarget != null) return;

        // Verifica se precisa entregar itens no baú
        if (this.outputPos != null && isInventoryFull() && currentState == State.MINING) {
            currentState = State.DELIVERING;
            targetOre = null;
        }

        if (currentState == State.DELIVERING) {
            this.getNavigator().tryMoveToXYZ(this.outputPos.getX(), this.outputPos.getY(), this.outputPos.getZ(), 1.0D);
            if (this.getDistanceSqToCenter(this.outputPos) < 4.0D) {
                dumpToOutputChest();
                currentState = State.MINING;
            }
            return;
        }

        // Lógica de Mineração
        workTimer++;
        int maxDelay = hasSpeedUpgrade ? 15 : 40;

        if (workTimer >= maxDelay) {
            workTimer = 0;
            if (targetOre == null) {
                targetOre = findNearestOre(8 + (this.getAllyLevel() - 1) * 3);
            }

            if (targetOre != null) {
                if (this.getDistanceSq(targetOre) > 4.0D) {
                    this.getNavigator().tryMoveToXYZ(targetOre.getX(), targetOre.getY(), targetOre.getZ(), 1.0D);
                } else {
                    mineOreVein(targetOre);
                    targetOre = null;
                }
            } else if (this.outputPos != null && hasItems()) {
                // Se não achou minério mas tem itens e baú configurado, limpa a bolsa
                currentState = State.DELIVERING;
            }
        }
    }

    private BlockPos findNearestOre(int radius) {
        // Nova Mecânica: Procura no Centro de Trabalho da Varinha (se existir)
        BlockPos searchCenter = (this.workAreaCenter != null) ? this.workAreaCenter : new BlockPos(this);
        BlockPos closestOre = null;
        double closestDistance = Double.MAX_VALUE;

        BlockPos startPos = searchCenter.add(-radius, -radius, -radius);
        BlockPos endPos = searchCenter.add(radius, 3, radius);

        for (BlockPos pos : BlockPos.getAllInBox(startPos, endPos)) {
            IBlockState state = this.worldObj.getBlockState(pos);
            if (isOre(state)) {
                double distance = searchCenter.distanceSq(pos);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestOre = pos;
                }
            }
        }
        return closestOre;
    }

    private boolean isOre(IBlockState state) {
        if (state.getBlock() instanceof BlockOre || state.getBlock() instanceof BlockRedstoneOre) return true; //[cite: 2]
        Item item = Item.getItemFromBlock(state.getBlock());
        if (item != null) {
            ItemStack stack = new ItemStack(item, 1, state.getBlock().getMetaFromState(state));
            for (int id : OreDictionary.getOreIDs(stack)) {
                if (OreDictionary.getOreName(id).startsWith("ore")) return true; //[cite: 2]
            }
        }
        return false;
    }

    private void mineOreVein(BlockPos startPos) {
        IBlockState targetState = worldObj.getBlockState(startPos);
        if (!isOre(targetState)) return;

        List<BlockPos> veinBlocks = new ArrayList<>();
        scanVein(startPos, targetState, veinBlocks);

        for (BlockPos orePos : veinBlocks) {
            IBlockState oreState = worldObj.getBlockState(orePos);
            int fortune = hasMultiplierUpgrade ? 2 : 0; //[cite: 2]
            List<ItemStack> drops = oreState.getBlock().getDrops(worldObj, orePos, oreState, fortune);
            worldObj.setBlockToAir(orePos);

            for (ItemStack drop : drops) {
                if (drop == null) continue;
                if (hasSmelterUpgrade) {
                    ItemStack smeltResult = FurnaceRecipes.instance().getSmeltingResult(drop); //[cite: 2]
                    if (smeltResult != null) {
                        ItemStack resultCopy = smeltResult.copy();
                        resultCopy.stackSize = drop.stackSize;
                        storeOrDropItem(resultCopy);
                        continue;
                    }
                }
                storeOrDropItem(drop);
            }
        }
    }

    private void scanVein(BlockPos pos, IBlockState targetState, List<BlockPos> veinBlocks) {
        if (veinBlocks.size() >= 64) return;
        IBlockState currentState = worldObj.getBlockState(pos);
        if (currentState.getBlock() == targetState.getBlock() && !veinBlocks.contains(pos)) {
            veinBlocks.add(pos);
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        scanVein(pos.add(x, y, z), targetState, veinBlocks);
                    }
                }
            }
        }
    }

    private void storeOrDropItem(ItemStack stack) {
        ItemStack remainder = ItemHandlerHelper.insertItemStacked(minerInventory, stack, false);
        if (remainder != null && remainder.stackSize > 0) {
            EntityItem entityItem = new EntityItem(worldObj, this.posX, this.posY, this.posZ, remainder);
            worldObj.spawnEntityInWorld(entityItem);
        }
    }

    private boolean isInventoryFull() {
        int emptySlots = 0;
        for (int i = 0; i < minerInventory.getSlots(); i++) {
            if (minerInventory.getStackInSlot(i) == null) emptySlots++;
        }
        return emptySlots <= 1; // Considera cheio se tiver só 1 slot livre
    }

    private boolean hasItems() {
        for (int i = 0; i < minerInventory.getSlots(); i++) {
            if (minerInventory.getStackInSlot(i) != null) return true;
        }
        return false;
    }

    private void dumpToOutputChest() {
        TileEntity te = worldObj.getTileEntity(this.outputPos);
        if (te != null && te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP)) {
            IItemHandler chestInv = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
            for (int i = 0; i < minerInventory.getSlots(); i++) {
                ItemStack stack = minerInventory.getStackInSlot(i);
                if (stack != null) {
                    ItemStack remainder = ItemHandlerHelper.insertItemStacked(chestInv, stack, false);
                    minerInventory.setStackInSlot(i, remainder);
                }
            }
            this.playSound(net.minecraft.init.SoundEvents.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);
        }
    }

    private void dumpInventoryToPlayer() {
        boolean hasItems = false;
        for (int i = 0; i < minerInventory.getSlots(); i++) {
            ItemStack stack = minerInventory.getStackInSlot(i);
            if (stack != null && stack.stackSize > 0) {
                EntityItem entityItem = new EntityItem(worldObj, this.posX, this.posY, this.posZ, stack.copy());
                worldObj.spawnEntityInWorld(entityItem);
                minerInventory.setStackInSlot(i, null);
                hasItems = true;
            }
        }
        if (hasItems) this.playSound(net.minecraft.init.SoundEvents.ENTITY_ITEM_PICKUP, 1.0F, 1.0F);
    }

    private void expandInventory() {
        ItemStackHandler newInv = new ItemStackHandler(27);
        for (int i = 0; i < minerInventory.getSlots(); i++) {
            newInv.setStackInSlot(i, minerInventory.getStackInSlot(i));
        }
        minerInventory = newInv;
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand, ItemStack stack) {
        if (!worldObj.isRemote) {
            if (player.isSneaking() && stack == null) {
                dumpInventoryToPlayer();
                player.addChatMessage(new TextComponentString("§eMinerador: Entregando minérios recolhidos!"));
                return true;
            }

            if (stack != null) {
                if (stack.getItem() == Item.getItemFromBlock(Blocks.CHEST) && !hasBackpackUpgrade) {
                    hasBackpackUpgrade = true;
                    expandInventory();
                    consumeItem(player, stack);
                    player.addChatMessage(new TextComponentString("§aUpgrade: Mochila do Minerador!"));
                    return true;
                }
                if (stack.getItem() == ModItems.upgradeSpeed && !hasSpeedUpgrade) {
                    hasSpeedUpgrade = true;
                    this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.35D); //[cite: 2]
                    consumeItem(player, stack);
                    player.addChatMessage(new TextComponentString("§aUpgrade: Minerador Veloz!"));
                    return true;
                }
                if (stack.getItem() == ModItems.upgradeOreMultiplier && !hasMultiplierUpgrade) {
                    hasMultiplierUpgrade = true;
                    consumeItem(player, stack);
                    player.addChatMessage(new TextComponentString("§aUpgrade: Picareta de Fortuna!"));
                    return true;
                }
                if (stack.getItem() == Item.getItemFromBlock(Blocks.FURNACE) && !hasSmelterUpgrade) {
                    hasSmelterUpgrade = true;
                    consumeItem(player, stack);
                    player.addChatMessage(new TextComponentString("§aUpgrade: Forja Interna!"));
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
        compound.setBoolean("UpgradeSmelter", hasSmelterUpgrade);
        compound.setBoolean("UpgradeSpeed", hasSpeedUpgrade);
        compound.setBoolean("UpgradeBackpack", hasBackpackUpgrade);
        compound.setTag("MinerInventory", minerInventory.serializeNBT());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.hasMultiplierUpgrade = compound.getBoolean("UpgradeMultiplier");
        this.hasSmelterUpgrade = compound.getBoolean("UpgradeSmelter");
        this.hasSpeedUpgrade = compound.getBoolean("UpgradeSpeed");
        this.hasBackpackUpgrade = compound.getBoolean("UpgradeBackpack");

        if (this.hasBackpackUpgrade) this.minerInventory = new ItemStackHandler(27);
        if (compound.hasKey("MinerInventory")) this.minerInventory.deserializeNBT(compound.getCompoundTag("MinerInventory"));
        if (this.hasSpeedUpgrade) this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.35D);
    }
}