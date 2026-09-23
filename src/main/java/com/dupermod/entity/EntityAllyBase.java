package com.dupermod.entity;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;

import java.util.UUID;

public abstract class EntityAllyBase extends EntitySlime {

    private UUID ownerId;
    protected boolean isRecovering = false;
    public ItemStackHandler inventory = new ItemStackHandler(18); // 18 slots de inventário interno

    public EntityAllyBase(World worldIn) {
        super(worldIn);
        this.setSlimeSize(2, true); // Tamanho médio de Slime
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(1, new EntityAIAttackMelee(this, 1.2D, false));
        this.tasks.addTask(2, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(3, new EntityAILookIdle(this));

        // Ataca mobs hostis automaticamente
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

    // --- MECÂNICA DE IMORTALIDADE E RECUPERAÇÃO ---
    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (this.isRecovering) return false; // Invulnerável enquanto recupera

        if (this.getHealth() - amount <= 1.0F) {
            this.setHealth(1.0F);
            this.isRecovering = true;
            this.setAttackTarget(null); // Mobs perdem o alvo
            return false;
        }
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (this.isRecovering) {
            this.setAttackTarget(null); // Garante que nenhum mob ataca nem ele ataca ninguém
            EntityPlayer owner = getOwner();

            if (owner != null) {
                // Voltar e seguir o jogador
                this.getNavigator().tryMoveToEntityLiving(owner, 1.3D);
            }

            // Regeneração rápida
            if (this.ticksExisted % 10 == 0) {
                this.heal(1.0F);
                if (this.getHealth() >= this.getMaxHealth()) {
                    this.isRecovering = false; // Recuperado! Volta à luta e ao trabalho
                }
            }
        }
    }

    // Interação com jogador: Clique com o botão direito para recolher o inventário do Aliado
    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand, ItemStack stack) {
        if (!this.worldObj.isRemote && hand == EnumHand.MAIN_HAND) {
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

    // Guardar inventário e Dono no NBT
    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        if (ownerId != null) compound.setString("OwnerUUID", ownerId.toString());
        compound.setTag("Inventory", inventory.serializeNBT());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        if (compound.hasKey("OwnerUUID")) ownerId = UUID.fromString(compound.getString("OwnerUUID"));
        if (compound.hasKey("Inventory")) inventory.deserializeNBT(compound.getCompoundTag("Inventory"));
    }
}