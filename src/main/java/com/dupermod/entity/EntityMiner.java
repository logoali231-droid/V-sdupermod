package com.dupermod.entity;

import com.dupermod.init.ModItems;
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

import java.util.ArrayList;
import java.util.List;

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
        if (workTimer >= 30) { // Procura minérios a cada 1.5 segundos
            workTimer = 0;
            findAndMineOre();
        }
    }

    private void findAndMineOre() {
        BlockPos pos = new BlockPos(this);
        int radius = 5 + (this.getAllyLevel() - 1) * 3; // Raio escala com a fusão

        for (int x = -radius; x <= radius; x++) {
            for (int y = -3; y <= 3; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos targetPos = pos.add(x, y, z);
                    IBlockState state = worldObj.getBlockState(targetPos);
                    Block block = state.getBlock();

                    if (isOreBlock(state, block)) {
                        int fortune = hasFortuneUpgrade ? 2 : 0;
                        List<ItemStack> drops = block.getDrops(worldObj, targetPos, state, fortune);
                        worldObj.setBlockToAir(targetPos);

                        for (ItemStack drop : drops) {
                            if (drop == null) continue;

                            // UPGRADE: Smelt Direct (Auto-Fundição de Minérios no Forno Vanilla/Modded)
                            if (hasSmeltUpgrade) {
                                ItemStack smeltedResult = FurnaceRecipes.instance().getSmeltingResult(drop);
                                if (smeltedResult != null) {
                                    ItemStack finalResult = smeltedResult.copy();
                                    finalResult.stackSize = drop.stackSize;
                                    ItemHandlerHelper.insertItemStacked(this.inventory, finalResult, false);
                                    continue;
                                }
                            }

                            // Coloca o drop normal/fortuna no inventário do Slime
                            ItemHandlerHelper.insertItemStacked(this.inventory, drop, false);
                        }
                        return; // Minera 1 bloco por ciclo
                    }
                }
            }
        }
    }

    /**
     * Verifica se o bloco é um minério usando o OreDictionary (Compatibilidade Universal de Mods)
     */
    private boolean isOreBlock(IBlockState state, Block block) {
        ItemStack stack = new ItemStack(block, 1, block.getMetaFromState(state));
        int[] oreIDs = OreDictionary.getOreIDs(stack);

        for (int id : oreIDs) {
            String oreName = OreDictionary.getOreName(id);
            if (oreName.startsWith("ore") || oreName.startsWith("denseore")) {
                return true;
            }
        }

        // Fallback para blocos com nomes personalizados de modpacks (ex: gravel ores)
        String unlocalizedName = block.getUnlocalizedName().toLowerCase();
        return unlocalizedName.contains("ore") || unlocalizedName.contains("gravel_ore");
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand, ItemStack stack) {
        if (stack != null && !worldObj.isRemote) {
            // Aplica Upgrade de Smelt Direct (com Barra de Ouro ou item de upgrade custom)
            if (stack.getItem() == Items.GOLD_INGOT && !hasSmeltUpgrade) {
                hasSmeltUpgrade = true;
                if (!player.capabilities.isCreativeMode) stack.stackSize--;
                player.addChatMessage(new TextComponentString("§aUpgrade Aplicado: Auto-Fundição (Smelt Direct)!"));
                return true;
            }

            // Aplica Upgrade de Fortune (com Diamante)
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