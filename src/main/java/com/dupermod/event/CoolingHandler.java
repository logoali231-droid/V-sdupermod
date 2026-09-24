package com.dupermod.event;

import com.dupermod.DuperConfig;
import com.dupermod.init.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.lang.reflect.Method;

public class CoolingHandler {

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!DuperConfig.enableCoolingArmor || event.phase != TickEvent.Phase.END) return;

        EntityPlayer player = event.player;
        if (player.worldObj.isRemote) return;

        if (player.ticksExisted % DuperConfig.coolingIntervalTicks == 0) {

            int coolingPower = 0;

            // Conta quantas peças da armadura de arrefecimento estão equipadas
            for (EntityEquipmentSlot slot : new EntityEquipmentSlot[]{
                    EntityEquipmentSlot.HEAD,
                    EntityEquipmentSlot.CHEST,
                    EntityEquipmentSlot.LEGS,
                    EntityEquipmentSlot.FEET}) {

                ItemStack stack = player.getItemStackFromSlot(slot);
                if (stack != null && (
                        stack.getItem() == ModItems.coolingHelmet ||
                                stack.getItem() == ModItems.coolingChestplate ||
                                stack.getItem() == ModItems.coolingLeggings ||
                                stack.getItem() == ModItems.coolingBoots)) {
                    coolingPower++;
                }
            }

            if (coolingPower > 0) {
                // Tenta resfriar o jogador diretamente via API do Tough As Nails
                boolean tanApplied = tryCoolToughAsNails(player, coolingPower);

                // Apaga o fogo se o jogador estiver queimando
                if (player.isBurning()) {
                    player.extinguish();
                }

                // Efeitos Vanilla adicionais (caso o TAN não esteja ativo ou para proteção extra)
                if (!tanApplied) {
                    if (coolingPower >= 4) {
                        player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, DuperConfig.coolingIntervalTicks + 40, 0, true, false));
                    }
                    player.addPotionEffect(new PotionEffect(MobEffects.RESISTANCE, DuperConfig.coolingIntervalTicks + 40, coolingPower - 1, true, false));
                }
            }
        }
    }

    /**
     * Altera diretamente o nível de temperatura corporal do Tough As Nails via Reflection.
     * Reduz a temperatura em direção ao nível Normal (NEUTRAL) de acordo com o número de peças.
     */
    private boolean tryCoolToughAsNails(EntityPlayer player, int coolingPower) {
        try {
            // Acessa TemperatureHelper.getTemperatureData(player)
            Class<?> tempHelper = Class.forName("toughasnails.api.temperature.TemperatureHelper");
            Method getTempData = tempHelper.getMethod("getTemperatureData", EntityPlayer.class);
            Object tempData = getTempData.invoke(null, player);

            if (tempData == null) return false;

            // Acessa ITemperatureData.getTemperature()
            Method getTemp = tempData.getClass().getMethod("getTemperature");
            Object tempObj = getTemp.invoke(tempData);

            // Acessa Temperature.getTemperatureLevel()
            Method getLevel = tempObj.getClass().getMethod("getTemperatureLevel");
            Enum<?> currentLevel = (Enum<?>) getLevel.invoke(tempObj);

            // Ordinais do Enum Temperature do TAN:
            // 0: HYPOTHERMIA, 1: COLD, 2: NEUTRAL, 3: WARM, 4: HOT, 5: HYPERTHERMIA
            int currentOrdinal = currentLevel.ordinal();

            // Se o jogador estiver quente (WARM, HOT, HYPERTHERMIA)
            if (currentOrdinal > 2) {
                int targetOrdinal = Math.max(2, currentOrdinal - coolingPower);

                Class<?> tempEnumClass = Class.forName("toughasnails.api.temperature.Temperature");
                Object[] enumConstants = tempEnumClass.getEnumConstants();

                if (enumConstants != null && targetOrdinal < enumConstants.length) {
                    Object newTemp = enumConstants[targetOrdinal];

                    // Executa setTemperature(newTemp)
                    Method setTempMethod = tempData.getClass().getMethod("setTemperature", tempEnumClass);
                    setTempMethod.invoke(tempData, newTemp);
                    return true;
                }
            }
        } catch (Exception ignored) {
            // O Tough As Nails não está instalado ou a versão possui outra estrutura
        }
        return false;
    }
}