package com.dupermod.block;

import com.dupermod.tileentity.TileEntityDuplicator;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        super.breakBlock(worldIn, pos, state);
        worldIn.removeTileEntity(pos);
    }
}
