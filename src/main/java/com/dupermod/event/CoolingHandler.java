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

    private int timer = 0;

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!DuperConfig.enableCoolingArmor || event.phase != TickEvent.Phase.END || event.player.worldObj.isRemote) return;

        timer++;
        if (timer >= DuperConfig.coolingIntervalTicks) {
            timer = 0;
            EntityPlayer player = event.player;

            // Conta quantas peças de armadura/upgrades o jogador tem
            int coolingPower = 0;

            for (EntityEquipmentSlot slot : new EntityEquipmentSlot[]{EntityEquipmentSlot.HEAD, EntityEquipmentSlot.CHEST, EntityEquipmentSlot.LEGS, EntityEquipmentSlot.FEET}) {
                ItemStack stack = player.getItemStackFromSlot(slot);
                if (stack != null && (stack.getItem() == ModItems.coolingHelmet ||
                        stack.getItem() == ModItems.coolingChestplate ||
                        stack.getItem() == ModItems.coolingLeggings ||
                        stack.getItem() == ModItems.coolingBoots)) {
                    coolingPower++;
                }
            }

            // Se tiver pelo menos 1 peça equipada
            if (coolingPower > 0) {
                try {
                    Class<?> tempHelper = Class.forName("toughasnails.api.temperature.TemperatureHelper");
                    Method getTempData = tempHelper.getMethod("getTemperatureData", EntityPlayer.class);
                    Object tempData = getTempData.invoke(null, player);

                    Method getTemp = tempData.getClass().getMethod("getTemperature");
                    Object tempObj = getTemp.invoke(tempData);

                    Method getLevel = tempObj.getClass().getMethod("getTemperatureLevel");
                    Enum<?> levelEnum = (Enum<?>) getLevel.invoke(tempObj);

                    // Se a temperatura estiver quente (ordinal > 2), arrefecer proporcionalmente às peças
                    if (levelEnum.ordinal() > 2) {
                        int targetOrdinal = Math.max(2, levelEnum.ordinal() - coolingPower);
                        Class<?> tempClass = Class.forName("toughasnails.api.temperature.Temperature");
                        Object newTemp = tempClass.getConstructor(int.class).newInstance(targetOrdinal);
                        Method setTemp = tempHelper.getMethod("setTemperature", EntityPlayer.class, tempClass);
                        setTemp.invoke(null, player, newTemp);
                    }
                } catch (Exception ignored) {
                    // Previne crashes caso o TAN não esteja no ambiente
                }
            }
        }
    }
}