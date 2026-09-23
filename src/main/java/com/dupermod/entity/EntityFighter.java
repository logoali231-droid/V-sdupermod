package com.dupermod.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

public class EntityFighter extends EntityAllyBase {

    public EntityFighter(World worldIn) {
        super(worldIn);
        this.setSize(1.0F, 1.0F); // Slime ligeiramente maior para combate
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        // Status de combate reforçados
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(40.0D); // 40 HP (20 Corações)
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.28D);
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(6.0D); // 3 Corações de dano por golpe
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        float damage = (float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
        return entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), damage);
    }
}