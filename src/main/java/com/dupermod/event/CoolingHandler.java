package com.dupermod.event;

import com.dupermod.DuperConfig;
import com.dupermod.init.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.lang.reflect.Method;

public class CoolingHandler {

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!DuperConfig.enableCoolingArmor || event.phase != TickEvent.Phase.END) return;

        EntityPlayer player = event.player;
        if (player.worldObj.isRemote) return;

        // USA O TICK PRÓPRIO DO JOGADOR EM VEZ DE UMA VARIÁVEL GLOBAL
        if (player.ticksExisted % DuperConfig.coolingIntervalTicks == 0) {

            // Conta quantas peças de armadura de arrefecimento o jogador tem equipadas
            int coolingPower = 0;

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

            // Se tiver pelo menos 1 peça equipada
            if (coolingPower > 0) {

                // Efeito Vanilla: Apaga o fogo do jogador se ele estiver a arder
                if (player.isBurning()) {
                    player.extinguish();
                }

                // Efeito Modded: Integração via Reflection com Tough As Nails
                applyToughAsNailsCooling(player, coolingPower);
            }
        }
    }

    private void applyToughAsNailsCooling(EntityPlayer player, int coolingPower) {
        try {
            Class<?> tempHelper = Class.forName("toughasnails.api.temperature.TemperatureHelper");
            Method getTempData = tempHelper.getMethod("getTemperatureData", EntityPlayer.class);
            Object tempData = getTempData.invoke(null, player);

            Method getTemp = tempData.getClass().getMethod("getTemperature");
            Object tempObj = getTemp.invoke(tempData);

            Method getLevel = tempObj.getClass().getMethod("getTemperatureLevel");
            Enum<?> levelEnum = (Enum<?>) getLevel.invoke(tempObj);

            // Se a temperatura estiver quente (ordinal > 2), reduz a temperatura proporcionalmente às peças equipadas
            if (levelEnum.ordinal() > 2) {
                int targetOrdinal = Math.max(2, levelEnum.ordinal() - coolingPower);
                Class<?> tempEnumClass = Class.forName("toughasnails.api.temperature.Temperature");
                Object[] enumConstants = tempEnumClass.getEnumConstants();

                if (enumConstants != null && targetOrdinal < enumConstants.length) {
                    Object newTemp = enumConstants[targetOrdinal];
                    Method setTempMethod = tempData.getClass().getMethod("setTemperature", tempEnumClass);
                    setTempMethod.invoke(tempData, newTemp);
                }
            }
        } catch (Exception ignored) {
            // Silencia a exceção caso o Tough As Nails não esteja presente no Modpack
        }
    }
}