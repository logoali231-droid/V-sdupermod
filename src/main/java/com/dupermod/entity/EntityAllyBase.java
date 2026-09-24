package com.dupermod.entity;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import com.dupermod.items.ItemCommandWand.WandMode;

import java.util.UUID;

public abstract class EntityAllyBase extends EntityCreature {

    private UUID ownerId;
    protected boolean isRecovering = false;
    public ItemStackHandler inventory = new ItemStackHandler(18);

    public BlockPos inputPos = null;
    public BlockPos outputPos = null;
    public BlockPos workAreaCenter = null;
    public BlockPos forceMoveTarget = null;

    // Sistema de Baú e Nível de Fusão
    private BlockPos boundChestPos = null;
    private int allyLevel = 1; // Nível 1 a 3

    public EntityAllyBase(World worldIn) {
        super(worldIn);
        this.updateAllySize();
    }

    public void receiveWandCommand(WandMode mode, BlockPos pos, EntityPlayer player) {
        switch (mode) {
            case SET_INPUT:
                this.inputPos = pos;
                player.addChatMessage(new TextComponentString("§a[Slime] Baú de Entrada configurado!"));
                break;
            case SET_OUTPUT:
                this.outputPos = pos;
                player.addChatMessage(new TextComponentString("§a[Slime] Baú de Saída configurado!"));
                break;
            case SET_WORKAREA:
                this.workAreaCenter = pos;
                player.addChatMessage(new TextComponentString("§a[Slime] Área de Trabalho centralizada configurada!"));
                break;
            case MOVE:
                this.forceMoveTarget = pos;
                player.addChatMessage(new TextComponentString("§a[Slime] Indo para o local!"));
                break;
        }
    }



    @Override
    protected void initEntityAI() {
        this.tasks.addTask(1, new EntityAIAttackMelee(this, 1.2D, false));
        this.tasks.addTask(2, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(3, new EntityAILookIdle(this));

        this.targetTasks.addTask(1, new EntityAINearestAttackableTarget<>(this, EntityLivingBase.class, 10, true, false, target -> target instanceof IMob));
    }

    public void setOwner(EntityPlayer player) {
        if (player != null) {
            this.ownerId = player.getUniqueID();
        }
    }

    public EntityPlayer getOwner() {
        return ownerId != null ? this.worldObj.getPlayerEntityByUUID(ownerId) : null;
    }

    public int getAllyLevel() {
        return this.allyLevel;
    }

    private void updateAllySize() {
        float baseSize = 0.6F + (this.allyLevel * 0.3F);
        this.setSize(baseSize, baseSize);
    }

    // --- LÓGICA DE DESCARREGAMENTO NO BAÚ ---
    public boolean isInventoryFull() {
        for (int i = 0; i < inventory.getSlots(); i++) {
            if (inventory.getStackInSlot(i) == null) return false;
        }
        return true;
    }

    private void unloadToChest() {
        if (boundChestPos == null) return;

        TileEntity te = worldObj.getTileEntity(boundChestPos);
        if (te != null && te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP)) {
            IItemHandler chestHandler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
            if (chestHandler != null) {
                boolean unloadedAny = false;
                for (int i = 0; i < inventory.getSlots(); i++) {
                    ItemStack stack = inventory.getStackInSlot(i);
                    if (stack != null) {
                        ItemStack remainder = ItemHandlerHelper.insertItemStacked(chestHandler, stack, false);
                        inventory.setStackInSlot(i, remainder);
                        unloadedAny = true;
                    }
                }
                if (unloadedAny && getOwner() != null) {
                    getOwner().addChatMessage(new TextComponentString("§aO teu aliado descarregou os recursos no baú!"));
                }
            }
        }
    }

    // --- IMORTALIDADE E CICLO DE VIDA ---
    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isRecovering) return false;

        if (this.getHealth() - amount <= 1.0F) {
            this.setHealth(1.0F);
            this.isRecovering = true;
            this.setAttackTarget(null);
            return false;
        }
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (this.worldObj.isRemote) return;

        // Recuperação
        if (this.isRecovering) {
            this.setAttackTarget(null);
            EntityPlayer owner = getOwner();
            if (owner != null) {
                this.getNavigator().tryMoveToEntityLiving(owner, 1.3D);
            }
            if (this.ticksExisted % 10 == 0) {
                this.heal(1.0F);
                if (this.getHealth() >= this.getMaxHealth()) {
                    this.isRecovering = false;
                }
            }
            return;
        }

        // Rotina de Descarregamento Automático quando o inventário está cheio
        if (boundChestPos != null && isInventoryFull()) {
            this.getNavigator().tryMoveToXYZ(boundChestPos.getX() + 0.5, boundChestPos.getY() + 1, boundChestPos.getZ() + 0.5, 1.2D);
            if (this.getDistanceSqToCenter(boundChestPos) <= 9.0D) {
                unloadToChest();
            }
        }

        if (!worldObj.isRemote && forceMoveTarget != null) {
            this.getNavigator().tryMoveToXYZ(forceMoveTarget.getX(), forceMoveTarget.getY(), forceMoveTarget.getZ(), 1.2D);

            // Se chegou a 2 blocos de distância, limpa o comando de movimento
            if (this.getDistanceSqToCenter(forceMoveTarget) < 4.0D) {
                forceMoveTarget = null;
            }
        }
    }

    // --- INTERAÇÕES DO JOGADOR ---
    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand, ItemStack stack) {
        if (!this.worldObj.isRemote && hand == EnumHand.MAIN_HAND) {

            // 1. Vinculação / Desvinculação de Baú (Usando o Item de Baú)
            if (stack != null && stack.getItem() == Item.getItemFromBlock(Blocks.CHEST)) {
                if (player.isSneaking()) {
                    this.boundChestPos = null;
                    player.addChatMessage(new TextComponentString("§cBaú desvinculado do aliado."));
                } else {
                    RayTraceResult ray = player.rayTrace(5.0D, 1.0F);
                    if (ray != null && ray.typeOfHit == RayTraceResult.Type.BLOCK) {
                        BlockPos hitPos = ray.getBlockPos();
                        if (worldObj.getBlockState(hitPos).getBlock() == Blocks.CHEST) {
                            this.boundChestPos = hitPos;
                            player.addChatMessage(new TextComponentString("§aBaú vinculado com sucesso em: " + hitPos.getX() + ", " + hitPos.getY() + ", " + hitPos.getZ()));
                        } else {
                            player.addChatMessage(new TextComponentString("§cOlha para um baú ao clicar para o vincular!"));
                        }
                    }
                }
                return true;
            }

            // 2. Upgrade de Fusão / Tamanho (Usando Bloco de Slime)
            if (stack != null && stack.getItem() == Item.getItemFromBlock(Blocks.SLIME_BLOCK)) {
                if (this.allyLevel < 3) {
                    this.allyLevel++;
                    this.updateAllySize();

                    // Aumenta vida máxima com a fusão
                    double newMaxHealth = 20.0D + (allyLevel * 10.0D);
                    this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(newMaxHealth);
                    this.heal(10.0F);

                    if (!player.capabilities.isCreativeMode) stack.stackSize--;
                    player.addChatMessage(new TextComponentString("§aFusão efetuada! Nível do Aliado: " + allyLevel));
                } else {
                    player.addChatMessage(new TextComponentString("§eEste aliado já atingiu o nível máximo de fusão (3)!"));
                }
                return true;
            }

            // 3. Recolher Inventário do Aliado (Clique Normal)
            int collected = 0;
            for (int i = 0; i < inventory.getSlots(); i++) {
                ItemStack item = inventory.getStackInSlot(i);
                if (item != null) {
                    if (player.inventory.addItemStackToInventory(item)) {
                        inventory.setStackInSlot(i, null);
                        collected++;
                    }
                }
            }
            if (collected > 0) {
                player.addChatMessage(new TextComponentString("Recolheste os recursos do teu aliado!"));
            }
            return true;
        }
        return super.processInteract(player, hand, stack);
    }

    // --- PERSISTÊNCIA NBT ---
    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        if (inputPos != null) compound.setLong("UnivInputPos", inputPos.toLong());
        if (outputPos != null) compound.setLong("UnivOutputPos", outputPos.toLong());
        if (workAreaCenter != null) compound.setLong("UnivWorkArea", workAreaCenter.toLong());
        if (ownerId != null) compound.setString("OwnerUUID", ownerId.toString());
        compound.setTag("Inventory", inventory.serializeNBT());
        compound.setInteger("AllyLevel", allyLevel);

        if (boundChestPos != null) {
            compound.setInteger("ChestX", boundChestPos.getX());
            compound.setInteger("ChestY", boundChestPos.getY());
            compound.setInteger("ChestZ", boundChestPos.getZ());
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasKey("OwnerUUID")) ownerId = UUID.fromString(compound.getString("OwnerUUID"));
        if (compound.hasKey("Inventory")) inventory.deserializeNBT(compound.getCompoundTag("Inventory"));
        if (compound.hasKey("UnivInputPos")) inputPos = BlockPos.fromLong(compound.getLong("UnivInputPos"));
        if (compound.hasKey("UnivOutputPos")) outputPos = BlockPos.fromLong(compound.getLong("UnivOutputPos"));
        if (compound.hasKey("UnivWorkArea")) workAreaCenter = BlockPos.fromLong(compound.getLong("UnivWorkArea"));

        this.allyLevel = compound.getInteger("AllyLevel");
        if (this.allyLevel < 1) this.allyLevel = 1;
        this.updateAllySize();

        if (compound.hasKey("ChestX")) {
            this.boundChestPos = new BlockPos(compound.getInteger("ChestX"), compound.getInteger("ChestY"), compound.getInteger("ChestZ"));
        }
    }
}