package com.dupermod.items;

import com.dupermod.entity.EntityAllyBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class ItemCommandWand extends Item {

    public enum WandMode {
        MOVE, SET_INPUT, SET_OUTPUT, SET_WORKAREA;

        public WandMode next() {
            return values()[(this.ordinal() + 1) % values().length];
        }
    }

    public ItemCommandWand() {
        this.setRegistryName("command_wand");
        this.setUnlocalizedName("command_wand");
        this.setMaxStackSize(1);
    }

    // Garante que o item tenha NBT
    private NBTTagCompound getNBT(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
            stack.getTagCompound().setString("Mode", WandMode.MOVE.name());
        }
        return stack.getTagCompound();
    }

    // 1. MUDAR DE MODO (Shift + Clique no Ar)
    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World worldIn, EntityPlayer playerIn, EnumHand hand) {
        if (!worldIn.isRemote && playerIn.isSneaking()) {
            cycleMode(stack, playerIn);
            return new ActionResult<>(EnumActionResult.SUCCESS, stack);
        }
        return super.onItemRightClick(stack, worldIn, playerIn, hand);
    }

    // 2. SALVAR COORDENADA OU MUDAR MODO (Clique no Bloco)
    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer playerIn, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            if (playerIn.isSneaking()) {
                cycleMode(stack, playerIn);
            } else {
                // Salva a posição no NBT
                NBTTagCompound nbt = getNBT(stack);
                nbt.setLong("SavedPos", pos.toLong());
                String modeName = nbt.getString("Mode");
                playerIn.addChatMessage(new TextComponentString("§e[Varinha] §fPosição salva para o modo: §a" + modeName));
            }
        }
        return EnumActionResult.SUCCESS;
    }

    // 3. APLICAR NO SLIME (Clique na Entidade)
    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer playerIn, EntityLivingBase target, EnumHand hand) {
        if (!playerIn.worldObj.isRemote && target instanceof EntityAllyBase) {

            if (playerIn.isSneaking()) return false;

            NBTTagCompound nbt = getNBT(stack);

            if (!nbt.hasKey("SavedPos")) {
                playerIn.addChatMessage(new TextComponentString("§c[Varinha] Nenhuma coordenada salva! Clique em um bloco primeiro."));
                return true;
            }

            WandMode mode = WandMode.valueOf(nbt.getString("Mode"));
            BlockPos savedPos = BlockPos.fromLong(nbt.getLong("SavedPos"));

            // Envia o comando para a base do Slime
            ((EntityAllyBase) target).receiveWandCommand(mode, savedPos, playerIn);
            return true;
        }
        return false;
    }

    private void cycleMode(ItemStack stack, EntityPlayer player) {
        NBTTagCompound nbt = getNBT(stack);
        WandMode currentMode = WandMode.valueOf(nbt.getString("Mode"));
        WandMode nextMode = currentMode.next();
        nbt.setString("Mode", nextMode.name());
        player.addChatMessage(new TextComponentString("§e[Varinha] §fModo alterado para: §b" + nextMode.name()));
    }
}