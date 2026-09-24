package com.dupermod.event;

import com.dupermod.DuperConfig;
import com.dupermod.init.ModItems;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
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

    private boolean hasRing(EntityPlayer player) {
        return player.inventory.hasItemStack(new ItemStack(ModItems.accessibilityRing));
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        EntityPlayer player = event.player;
        World world = player.worldObj;

        boolean ringEquipped = hasRing(player);

        // --- 1. ASSISTÊNCIA MOTORA ---

        // Step Assist
        if (DuperConfig.enableStepAssist && ringEquipped) {
            player.stepHeight = 1.25F;
        } else if (player.stepHeight == 1.25F) {
            player.stepHeight = 0.6F;
        }

        if (world.isRemote) return;

        if (ringEquipped) {
            // Magnet
            if (DuperConfig.enableMagnet) {
                AxisAlignedBB area = new AxisAlignedBB(
                        player.posX - 8.0D, player.posY - 8.0D, player.posZ - 8.0D,
                        player.posX + 8.0D, player.posY + 8.0D, player.posZ + 8.0D
                );
                List<EntityItem> items = world.getEntitiesWithinAABB(EntityItem.class, area);
                for (EntityItem item : items) {
                    if (item.isEntityAlive() && !item.cannotPickup()) {
                        item.setPosition(player.posX, player.posY + 0.5D, player.posZ);
                    }
                }
            }

            // Auto-Eat
            if (DuperConfig.enableAutoEat) {
                if (player.getFoodStats().needFood() && player.getFoodStats().getFoodLevel() <= 12) {
                    for (int i = 0; i < 9; i++) {
                        ItemStack stack = player.inventory.getStackInSlot(i);
                        if (stack != null && stack.getItem() instanceof ItemFood) {
                            ItemFood food = (ItemFood) stack.getItem();
                            player.getFoodStats().addStats(food.getHealAmount(stack), food.getSaturationModifier(stack));

                            world.playSound(null, player.posX, player.posY, player.posZ,
                                    SoundEvents.ENTITY_GENERIC_EAT, SoundCategory.PLAYERS, 0.5F, 1.0F);

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

            // Visão Noturna
            if (DuperConfig.enableNightVision) {
                PotionEffect currentEffect = player.getActivePotionEffect(MobEffects.NIGHT_VISION);
                if (currentEffect == null || currentEffect.getDuration() <= 220) {
                    player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 300, 0, true, false));
                }
            }

            // Sonar & Grid de Spawn (Cada 10 ticks)
            if (player.ticksExisted % 10 == 0) {
                if (world instanceof WorldServer) {
                    WorldServer ws = (WorldServer) world;

                    if (DuperConfig.enableSonar) {
                        AxisAlignedBB mobArea = new AxisAlignedBB(
                                player.posX - 12.0D, player.posY - 6.0D, player.posZ - 12.0D,
                                player.posX + 12.0D, player.posY + 6.0D, player.posZ + 12.0D
                        );
                        List<EntityLivingBase> mobs = world.getEntitiesWithinAABB(EntityLivingBase.class, mobArea);
                        for (EntityLivingBase mob : mobs) {
                            if (mob instanceof IMob) {
                                ws.spawnParticle(EnumParticleTypes.VILLAGER_ANGRY, mob.posX, mob.posY + mob.getEyeHeight(), mob.posZ, 2, 0.1, 0.1, 0.1, 0.0);
                            }
                        }
                    }

                    if (DuperConfig.enableLightGrid) {
                        BlockPos playerPos = new BlockPos(player);
                        for (int x = -5; x <= 5; x++) {
                            for (int z = -5; z <= 5; z++) {
                                for (int y = -2; y <= 2; y++) {
                                    BlockPos targetPos = playerPos.add(x, y, z);
                                    if (world.isAirBlock(targetPos) && world.getBlockState(targetPos.down()).isOpaqueCube()) {
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

    @SubscribeEvent
    public void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        EntityPlayer player = event.getEntityPlayer();
        World world = event.getWorld();

        if (world.isRemote || !DuperConfig.enableAutoTool) return;
        if (!hasRing(player)) return;

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