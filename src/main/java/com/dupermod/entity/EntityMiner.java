package com.dupermod.entity;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class EntityMiner extends EntityAllyBase {

    private int workTimer = 0;
    public boolean hasSmeltUpgrade = false;
    public boolean hasFortuneUpgrade = false;

    public EntityMiner(World worldIn) {
        super(worldIn);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (this.worldObj.isRemote || this.isRecovering) return;

        workTimer++;
        if (workTimer >= 30) {
            workTimer = 0;
            scanAndMineVein();
        }
    }

    private void scanAndMineVein() {
        BlockPos pos = new BlockPos(this);
        int radius = 6 + (this.getAllyLevel() - 1) * 3;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -4; y <= 4; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos targetPos = pos.add(x, y, z);
                    IBlockState state = worldObj.getBlockState(targetPos);
                    Block block = state.getBlock();

                    if (isGeolosysOrOre(state, block)) {
                        if (isGeolosysSample(block)) {
                            worldObj.setBlockToAir(targetPos);
                            digVerticalShaft(targetPos.down());
                            return;
                        }

                        mineVeinChain(targetPos, state, block);
                        return;
                    }
                }
            }
        }
    }

    private void mineVeinChain(BlockPos startPos, IBlockState targetState, Block targetBlock) {
        Queue<BlockPos> toMine = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>();

        toMine.add(startPos);
        visited.add(startPos);

        int minedCount = 0;
        int maxPerCycle = 4 + (this.getAllyLevel() * 2);

        while (!toMine.isEmpty() && minedCount < maxPerCycle) {
            BlockPos current = toMine.poll();
            IBlockState state = worldObj.getBlockState(current);

            if (state.getBlock() == targetBlock) {
                int fortune = hasFortuneUpgrade ? 2 : 0;
                List<ItemStack> drops = targetBlock.getDrops(worldObj, current, state, fortune);
                worldObj.setBlockToAir(current);
                minedCount++;

                for (ItemStack drop : drops) {
                    if (drop == null) continue;

                    if (hasSmeltUpgrade) {
                        ItemStack smeltedResult = FurnaceRecipes.instance().getSmeltingResult(drop);
                        if (smeltedResult != null) {
                            ItemStack resultCopy = smeltedResult.copy();
                            resultCopy.stackSize = drop.stackSize;
                            ItemHandlerHelper.insertItemStacked(this.inventory, resultCopy, false);
                            continue;
                        }
                    }
                    ItemHandlerHelper.insertItemStacked(this.inventory, drop, false);
                }

                for (int x = -1; x <= 1; x++) {
                    for (int y = -1; y <= 1; y++) {
                        for (int z = -1; z <= 1; z++) {
                            BlockPos immutable = current.add(x, y, z);
                            if (!visited.contains(immutable)) {
                                visited.add(immutable);
                                if (worldObj.getBlockState(immutable).getBlock() == targetBlock) {
                                    toMine.add(immutable);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private void digVerticalShaft(BlockPos start) {
        for (int i = 0; i < 25; i++) {
            BlockPos check = start.down(i);
            IBlockState state = worldObj.getBlockState(check);
            Block block = state.getBlock();

            if (isGeolosysOrOre(state, block) && !isGeolosysSample(block)) {
                mineVeinChain(check, state, block);
                break;
            }
        }
    }

    private boolean isGeolosysSample(Block block) {
        String name = block.getRegistryName() != null ? block.getRegistryName().toString() : "";
        return name.contains("geolosys") && name.contains("sample");
    }

    private boolean isGeolosysOrOre(IBlockState state, Block block) {
        String regName = block.getRegistryName() != null ? block.getRegistryName().toString() : "";

        if (regName.contains("geolosys")) {
            return true;
        }

        ItemStack stack = new ItemStack(block, 1, block.getMetaFromState(state));
        int[] oreIDs = OreDictionary.getOreIDs(stack);
        for (int id : oreIDs) {
            String oreName = OreDictionary.getOreName(id);
            if (oreName.startsWith("ore") || oreName.startsWith("cluster")) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand, ItemStack stack) {
        if (stack != null && !worldObj.isRemote) {
            if (stack.getItem() == Items.GOLD_INGOT && !hasSmeltUpgrade) {
                hasSmeltUpgrade = true;
                if (!player.capabilities.isCreativeMode) stack.stackSize--;
                player.addChatMessage(new TextComponentString("§aUpgrade Aplicado: Auto-Fundição (Geolosys / Smelt Direct)!"));
                return true;
            }

            if (stack.getItem() == Items.DIAMOND && !hasFortuneUpgrade) {
                hasFortuneUpgrade = true;
                if (!player.capabilities.isCreativeMode) stack.stackSize--;
                player.addChatMessage(new TextComponentString("§aUpgrade Aplicado: Fortuna II!"));
                return true;
            }
        }
        return super.processInteract(player, hand, stack);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("SmeltUpgrade", hasSmeltUpgrade);
        compound.setBoolean("FortuneUpgrade", hasFortuneUpgrade);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.hasSmeltUpgrade = compound.getBoolean("SmeltUpgrade");
        this.hasFortuneUpgrade = compound.getBoolean("FortuneUpgrade");
    }
}