package com.dupermod.handler;

import com.dupermod.init.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class CoolingHandler {

    private static final int INTERVAL_TICKS = 40;

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.world.isRemote) return;

        EntityPlayer player = event.player;

        // FIXED: Replaced shared instance timer with player-specific tick count
        if (player.ticksExisted % INTERVAL_TICKS == 0) {
            ItemStack helmet = player.inventory.armorItemInSlot(3);
            if (helmet != null && helmet.getItem() == ModItems.coolingHelmet) {
                // Apply cooling effect
                player.extinguish();
            }
        }
    }
}