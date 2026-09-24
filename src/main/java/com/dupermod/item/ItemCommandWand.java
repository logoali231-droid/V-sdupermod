package com.dupermod.item;

import com.dupermod.entity.EntityAllyBase;
import net.minecraft.creativetab.CreativeTabs;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

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
        this.setCreativeTab(CreativeTabs.TOOLS);
        this.setMaxStackSize(1);
    }

    // --- VISUAL DE GRAVETO ENCANTADO ---
    @Override
    @SideOnly(Side.CLIENT)
    public boolean hasEffect(ItemStack stack) {
        return true; // Aplica o brilho animado de encantamento ao item
    }

    private NBTTagCompound getNBT(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        NBTTagCompound nbt = stack.getTagCompound();
        if (!nbt.hasKey("Mode")) {
            nbt.setString("Mode", WandMode.MOVE.name());
        }
        return nbt;
    }

    private WandMode getMode(ItemStack stack) {
        NBTTagCompound nbt = getNBT(stack);
        try {
            return WandMode.valueOf(nbt.getString("Mode"));
        } catch (IllegalArgumentException | NullPointerException e) {
            return WandMode.MOVE;
        }
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
                NBTTagCompound nbt = getNBT(stack);
                nbt.setLong("SavedPos", pos.toLong());
                WandMode mode = getMode(stack);
                playerIn.addChatMessage(new TextComponentString("§e[Varinha] §fPosição §a(" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + ")§f salva para o modo: §b" + mode.name()));
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
                playerIn.addChatMessage(new TextComponentString("§c[Varinha] Nenhuma coordenada salva! Clique num bloco primeiro."));
                return true;
            }

            WandMode mode = getMode(stack);
            BlockPos savedPos = BlockPos.fromLong(nbt.getLong("SavedPos"));

            // Envia o comando para o Slime
            ((EntityAllyBase) target).receiveWandCommand(mode, savedPos, playerIn);
            return true;
        }
        return false;
    }

    private void cycleMode(ItemStack stack, EntityPlayer player) {
        WandMode currentMode = getMode(stack);
        WandMode nextMode = currentMode.next();
        getNBT(stack).setString("Mode", nextMode.name());
        player.addChatMessage(new TextComponentString("§e[Varinha] §fModo alterado para: §b" + nextMode.name()));
    }

    // --- TOOLTIP INFORMACIONAL ---
    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        WandMode mode = getMode(stack);
        NBTTagCompound nbt = stack.getTagCompound();

        tooltip.add("§7Modo Atual: §b" + mode.name());

        if (nbt != null && nbt.hasKey("SavedPos")) {
            BlockPos pos = BlockPos.fromLong(nbt.getLong("SavedPos"));
            tooltip.add("§7Posição Salva: §a" + pos.getX() + ", " + pos.getY() + ", " + pos.getZ());
        } else {
            tooltip.add("§7Posição Salva: §cnenhuma");
        }

        tooltip.add("");
        tooltip.add("§8- §eShift + Clique direito:§7 Alterna o modo");
        tooltip.add("§8- §eClique direito num bloco:§7 Salva a posição");
        tooltip.add("§8- §eClique num Slime:§7 Aplica o comando");
    }
}