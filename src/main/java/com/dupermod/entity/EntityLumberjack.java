package com.dupermod.entity;

import com.dupermod.init.ModItems;
import net.minecraft.block.BlockLog;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.List;

public class EntityLumberjack extends EntityAllyBase {

    private int workTimer = 0;

    // Estados dos Upgrades
    public boolean hasMultiplierUpgrade = false;
    public boolean hasCharcoalUpgrade = false;
    public boolean hasSpeedUpgrade = false;

    public EntityLumberjack(World worldIn) {
        super(worldIn);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (this.worldObj.isRemote || this.isRecovering) return;

        workTimer++;
        // Se tiver upgrade de velocidade, trabalha a cada 15 ticks (0.75s) em vez de 40 ticks (2s)
        int maxDelay = hasSpeedUpgrade ? 15 : 40;

        if (workTimer >= maxDelay) {
            workTimer = 0;
            findAndCutTree();
        }
    }

    private void findAndCutTree() {
        BlockPos pos = new BlockPos(this);
        int radius = 8 + (this.getAllyLevel() - 1) * 3;

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -2; y <= 5; y++) {
                    BlockPos targetPos = pos.add(x, y, z);
                    IBlockState state = worldObj.getBlockState(targetPos);

                    if (state.getBlock() instanceof BlockLog) {
                        List<BlockPos> treeBlocks = new ArrayList<>();
                        scanTree(targetPos, treeBlocks);

                        for (BlockPos logPos : treeBlocks) {
                            IBlockState logState = worldObj.getBlockState(logPos);
                            List<ItemStack> drops = logState.getBlock().getDrops(worldObj, logPos, logState, 0);
                            worldObj.setBlockToAir(logPos);

                            for (ItemStack drop : drops) {
                                if (drop == null) continue;

                                // UPGRADE 1: Multiplicador de Madeira (x2)
                                if (hasMultiplierUpgrade) {
                                    drop.stackSize *= 2;
                                }

                                // UPGRADE 2: Conversor de Carvão Vegetal (50% Tronco / 50% Carvão)
                                if (hasCharcoalUpgrade) {
                                    int total = drop.stackSize;
                                    int charcoalAmount = total / 2;
                                    int logAmount = total - charcoalAmount;

                                    if (logAmount > 0) {
                                        ItemStack logs = drop.copy();
                                        logs.stackSize = logAmount;
                                        ItemHandlerHelper.insertItemStacked(this.inventory, logs, false);
                                    }

                                    if (charcoalAmount > 0) {
                                        // No Minecraft 1.10.2, Charcoal é Items.COAL com metadata 1
                                        ItemStack charcoal = new ItemStack(Items.COAL, charcoalAmount, 1);
                                        ItemHandlerHelper.insertItemStacked(this.inventory, charcoal, false);
                                    }
                                } else {
                                    ItemHandlerHelper.insertItemStacked(this.inventory, drop, false);
                                }
                            }
                        }
                        return;
                    }
                }
            }
        }
    }

    private void scanTree(BlockPos pos, List<BlockPos> treeBlocks) {
        if (treeBlocks.size() >= 64) return;
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

    // Aplicação de Upgrades via Clique com o Botão Direito
    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand, ItemStack stack) {
        if (stack != null) {
            if (stack.getItem() == ModItems.upgradeLogMultiplier && !hasMultiplierUpgrade) {
                if (!worldObj.isRemote) {
                    hasMultiplierUpgrade = true;
                    consumeItem(player, stack);
                    player.addChatMessage(new TextComponentString("§aUpgrade Aplicado: Multiplicador de Madeira!"));
                }
                return true;
            }

            if (stack.getItem() == ModItems.upgradeCharcoal && !hasCharcoalUpgrade) {
                if (!worldObj.isRemote) {
                    hasCharcoalUpgrade = true;
                    consumeItem(player, stack);
                    player.addChatMessage(new TextComponentString("§aUpgrade Aplicado: Conversor de Carvão Vegetal!"));
                }
                return true;
            }

            if (stack.getItem() == ModItems.upgradeSpeed && !hasSpeedUpgrade) {
                if (!worldObj.isRemote) {
                    hasSpeedUpgrade = true;
                    this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.35D); // Aumenta velocidade de movimento
                    consumeItem(player, stack);
                    player.addChatMessage(new TextComponentString("§aUpgrade Aplicado: Velocidade Aumentada!"));
                }
                return true;
            }
        }
        return super.processInteract(player, hand, stack);
    }

    private void consumeItem(EntityPlayer player, ItemStack stack) {
        if (!player.capabilities.isCreativeMode) {
            stack.stackSize--;
        }
    }

    // Guardar e Carregar Upgrades do NBT (Persistência ao reiniciar o mundo)
    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("UpgradeMultiplier", hasMultiplierUpgrade);
        compound.setBoolean("UpgradeCharcoal", hasCharcoalUpgrade);
        compound.setBoolean("UpgradeSpeed", hasSpeedUpgrade);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.hasMultiplierUpgrade = compound.getBoolean("UpgradeMultiplier");
        this.hasCharcoalUpgrade = compound.getBoolean("UpgradeCharcoal");
        this.hasSpeedUpgrade = compound.getBoolean("UpgradeSpeed");

        if (this.hasSpeedUpgrade) {
            this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.35D);
        }
    }
}