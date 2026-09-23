package com.dupermod.event;

import com.dupermod.DuperConfig;
import com.dupermod.init.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;

import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.List;

public class AccessibilityHandler {

    private int sonarTimer = 0;

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        EntityPlayer player = event.player;
        World world = player.worldObj;

        // Verifica se o jogador possui o Anel de Acessibilidade no inventário ou hotbar
        boolean hasRing = player.inventory.hasItemStack(new ItemStack(ModItems.accessibilityRing));

        // --- 1. ASSISTÊNCIA MOTORA ---

        // Step Assist (Subir degraus automaticamente)
        if (DuperConfig.enableStepAssist && hasRing) {
            player.stepHeight = 1.25F;
        } else if (player.stepHeight == 1.25F) {
            player.stepHeight = 0.6F; // Restaura o valor padrão
        }

        if (world.isRemote) return; // Apenas servidor para as lógicas abaixo

        if (hasRing) {
            // Magnet (Coleta Automática)
            if (DuperConfig.enableMagnet) {
                AxisAlignedBB area = player.getEntityBoundingBox().expand(8.0D, 8.0D, 8.0D);
                List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class, area);
                for (EntityItem item : items) {
                    if (item.isEntityAlive() && !item.cannotPickup()) {
                        item.setPosition(player.posX, player.posY + 0.5D, player.posZ);
                    }
                }
            }

            // Consumo Automático de Comida (Auto-Eat)
            if (DuperConfig.enableAutoEat) {
                if (player.getFoodStats().needFood() && player.getFoodStats().getFoodLevel() <= 12) {
                    for (int i = 0; i < 9; i++) {
                        ItemStack stack = player.inventory.getStackInSlot(i);
                        if (stack != null && stack.getItem() instanceof ItemFood) {
                            ItemFood food = (ItemFood) stack.getItem();
                            player.getFoodStats().addStats(food, stack);
                            stack.stackSize--;
                            if (stack.stackSize <= 0) {
                                player.inventory.setInventorySlotContents(i, null);
                            }
                            break;
                        }
                    }
                }
            }

            // --- 2. ACESSIBILIDADE VISUAL ---

            // Visão Noturna Contínua
            if (DuperConfig.enableNightVision) {
                player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 300, 0, true, false));
            }

            // Sonar & Grid de Spawn (Executado a cada 10 ticks)
            sonarTimer++;
            if (sonarTimer >= 10) {
                sonarTimer = 0;

                if (world instanceof WorldServer) {
                    WorldServer ws = (WorldServer) world;

                    // Sonar de Mobs Hostis
                    if (DuperConfig.enableSonar) {
                        AxisAlignedBB mobArea = player.getEntityBoundingBox().expand(12.0D, 6.0D, 12.0D);
                        List<EntityLivingBase> mobs = world.getEntitiesWithinAABB(EntityLivingBase.class, mobArea);
                        for (EntityLivingBase mob : mobs) {
                            if (mob instanceof IMob) {
                                ws.spawnParticle(EnumParticleTypes.VILLAGER_ANGRY, mob.posX, mob.posY + mob.getEyeHeight(), mob.posZ, 2, 0.1, 0.1, 0.1, 0.0);
                            }
                        }
                    }

                    // Grid de Spawn de Mobs (Luz <= 7)
                    if (DuperConfig.enableLightGrid) {
                        BlockPos playerPos = new BlockPos(player);
                        for (int x = -5; x <= 5; x++) {
                            for (int z = -5; z <= 5; z++) {
                                for (int y = -2; y <= 2; y++) {
                                    BlockPos targetPos = playerPos.add(x, y, z);
                                    if (world.isAirBlock(targetPos) && world.getBlockState(targetPos.down()).isFullyOpaque()) {
                                        int lightLevel = world.getLightFor(EnumSkyBlock.BLOCK, targetPos);
                                        if (lightLevel <= 7) {
                                            ws.spawnParticle(EnumParticleTypes.SPELL_MOB, targetPos.getX() + 0.5, targetPos.getY() + 0.1, targetPos.getZ() + 0.5, 1, 0.0, 0.0, 0.0, 0.0);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Auto-Tool (Troca automática de ferramenta ao golpear bloco)
    @SubscribeEvent
    public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        EntityPlayer player = event.getEntityPlayer();
        World world = event.getWorld();

        if (world.isRemote || !DuperConfig.enableAutoTool) return;
        if (!player.inventory.hasItemStack(new ItemStack(ModItems.accessibilityRing))) return;

        IBlockState state = world.getBlockState(event.getPos());
        int bestSlot = -1;
        float bestSpeed = 1.0F;

        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.inventory.getStackInSlot(i);
            if (stack != null) {
                float speed = stack.getStrVsBlock(state);
                if (speed > bestSpeed) {
                    bestSpeed = speed;
                    bestSlot = i;
                }
            }
        }

        if (bestSlot != -1) {
            player.inventory.currentItem = bestSlot;
        }
    }
}