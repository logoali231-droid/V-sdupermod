package com.dupermod.block;

import com.dupermod.DuperMod;
import com.dupermod.tileentity.TileEntityDuplicator;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class BlockDuplicator extends Block implements ITileEntityProvider {

    private final int tier;

    public BlockDuplicator(int tier) {
        super(Material.IRON);
        this.tier = tier;
        this.setRegistryName("block_duplicator_tier_" + tier);
        this.setUnlocalizedName("block_duplicator_tier_" + tier);
        this.setCreativeTab(CreativeTabs.REDSTONE);
        this.setHardness(3.0F);
        this.setResistance(5.0F);
    }

    public int getTier() {
        return tier;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityDuplicator(this.tier);
    }

    // Interação do jogador ao clicar com o botão direito no bloco
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            TileEntity te = worldIn.getTileEntity(pos);
            if (te instanceof TileEntityDuplicator) {
                // Abre a GUI cadastrada no seu GuiHandler principal
                playerIn.openGui(DuperMod.instance, 0, worldIn, pos.getX(), pos.getY(), pos.getZ());
            }
        }
        return true;
    }

    // Droppa os itens do inventário antes de destruir o bloco e a TileEntity
    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity te = worldIn.getTileEntity(pos);

        if (te instanceof TileEntityDuplicator) {
            IItemHandler inventory = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            if (inventory != null) {
                for (int i = 0; i < inventory.getSlots(); i++) {
                    ItemStack stack = inventory.getStackInSlot(i);
                    if (stack != null) {
                        float rx = worldIn.rand.nextFloat() * 0.8F + 0.1F;
                        float ry = worldIn.rand.nextFloat() * 0.8F + 0.1F;
                        float rz = worldIn.rand.nextFloat() * 0.8F + 0.1F;

                        EntityItem entityItem = new EntityItem(
                                worldIn,
                                pos.getX() + rx,
                                pos.getY() + ry,
                                pos.getZ() + rz,
                                stack.copy()
                        );

                        float factor = 0.05F;
                        entityItem.motionX = worldIn.rand.nextGaussian() * factor;
                        entityItem.motionY = worldIn.rand.nextGaussian() * factor + 0.2F;
                        entityItem.motionZ = worldIn.rand.nextGaussian() * factor;

                        worldIn.spawnEntityInWorld(entityItem);
                    }
                }
            }
        }

        // Deve ser chamado por ÚLTIMO para não remover a TileEntity antes da leitura dos itens
        super.breakBlock(worldIn, pos, state);
    }
}