package com.dupermod.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

import java.lang.reflect.Method;

public class EntityCooler extends EntityAllyBase {

    public EntityCooler(World worldIn) {
        super(worldIn);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        // 1. Lado do Cliente: Efeito visual de partículas de neve
        if (this.worldObj.isRemote) {
            for (int i = 0; i < 3; ++i) {
                double px = this.posX + (this.rand.nextDouble() - 0.5D) * this.width * 1.5D;
                double py = this.posY + this.rand.nextDouble() * this.height + 0.2D;
                double pz = this.posZ + (this.rand.nextDouble() - 0.5D) * this.width * 1.5D;

                // Partículas de pá de neve (Snow Shovel) flutuando ao redor
                this.worldObj.spawnParticle(EnumParticleTypes.SNOW_SHOVEL, px, py, pz, 0.0D, 0.03D, 0.0D);
            }
        }
        // 2. Lado do Servidor: Aura de Ar Condicionado (Raio de 20 blocos)
        else if (!this.isRecovering) {
            // O raio aumenta com o nível de fusão (20 blocos no Nível 1, 25 no Nível 2, 30 no Nível 3)
            double radius = 20.0D + (this.getAllyLevel() - 1) * 5.0D;

            for (EntityPlayer player : this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, this.getEntityBoundingBox().expand(radius, radius, radius))) {
                if (this.getDistanceSqToEntity(player) <= radius * radius) {

                    // Apaga o jogador instantaneamente se estiver em chamas
                    if (player.isBurning()) {
                        player.extinguish();
                    }

                    // Tenta regular a temperatura via Tough As Nails (se o mod estiver instalado)
                    applyToughAsNailsCooling(player);
                }
            }
        }
    }

    /**
     * Integração via Reflexão com Tough As Nails (TAN).
     * Mantém a temperatura do jogador amena (Target/Neutral) sem exigir o TAN como dependência obrigatória.
     */
    private void applyToughAsNailsCooling(EntityPlayer player) {
        try {
            // Acessa o TemperatureHelper do TAN
            Class<?> tempHelperClass = Class.forName("toughasnails.api.temperature.TemperatureHelper");
            Method getTempDataMethod = tempHelperClass.getMethod("getTemperatureData", EntityPlayer.class);
            Object tempStats = getTempDataMethod.invoke(null, player);

            if (tempStats != null) {
                Class<?> tempEnumClass = Class.forName("toughasnails.api.temperature.Temperature");
                // Define a temperatura como "TARGET" (Ameana / Normal)
                Object targetTemp = Enum.valueOf((Class<Enum>) tempEnumClass, "TARGET");

                Method setTempMethod = tempStats.getClass().getMethod("setTemperature", tempEnumClass);
                setTempMethod.invoke(tempStats, targetTemp);
            }
        } catch (Exception ignored) {
            // TAN não está instalado; aplica um efeito de regeneração suave como alternativa vanilla
            player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, 40, 0, true, false));
        }
    }
}