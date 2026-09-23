package com.dupermod.entity;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class EntityMiner extends EntityAllyBase {

    private int workTimer = 0;
    public boolean hasSmeltUpgrade = false;
    public boolean hasFortuneUpgrade = false;

    public EntityMiner(World worldIn) {
        super(worldIn);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (this.world.isRemote || this.isRecovering) return;

        workTimer++;
        if (workTimer >= 30) { // A cada 1.5 segundos executa a rotina
            workTimer = 0;
            scanAndMineVein();
        }
    }

    private void scanAndMineVein() {
        BlockPos pos = new BlockPos(this);
        int radius = 6 + (this.getAllyLevel() - 1) * 3;

        for (int x = -radius; x <= radius; x++) {
            for (int y = -4; y <= 4; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos targetPos = pos.add(x, y, z);
                    IBlockState state = world.getBlockState(targetPos);
                    Block block = state.getBlock();

                    if (isGeolosysOrOre(state, block)) {

                        // SE FOR UMA AMOSTRA DE SUPERFÍCIE (SAMPLE):
                        // O Slime minera a amostra e escava uma coluna para baixo até encontrar a veia principal
                        if (isGeolosysSample(block)) {
                            world.setBlockToAir(targetPos);
                            digVerticalShaft(targetPos.down());
                            return;
                        }

                        // SE FOR MINÉRIO DE VEIA: Minera em cadeia (Veinminer de até 8 blocos por ciclo)
                        mineVeinChain(targetPos, state, block);
                        return;
                    }
                }
            }
        }
    }

    /**
     * Algoritmo de Busca em Largura (BFS) para minerar a veia conectada
     */
    private void mineVeinChain(BlockPos startPos, IBlockState targetState, Block targetBlock) {
        Queue<BlockPos> toMine = new ArrayDeque<>();
        Set<BlockPos> visited = new HashSet<>();

        toMine.add(startPos);
        visited.add(startPos);

        int minedCount = 0;
        int maxPerCycle = 4 + (this.getAllyLevel() * 2); // Nível 1: 6 blocos/ciclo | Nível 3: 10 blocos

        while (!toMine.isEmpty() && minedCount < maxPerCycle) {
            BlockPos current = toMine.poll();
            IBlockState state = world.getBlockState(current);

            if (state.getBlock() == targetBlock) {
                int fortune = hasFortuneUpgrade ? 2 : 0;
                List<ItemStack> drops = targetBlock.getDrops(world, current, state, fortune);
                world.setBlockToAir(current);
                minedCount++;

                for (ItemStack drop : drops) {
                    if (drop.isEmpty()) continue;

                    // Upgrade Smelt: Converte minérios/peças do Geolosys no produto final do forno
                    if (hasSmeltUpgrade) {
                        ItemStack smeltedResult = FurnaceRecipes.instance().getSmeltingResult(drop);
                        if (!smeltedResult.isEmpty()) {
                            ItemStack resultCopy = smeltedResult.copy();
                            resultCopy.setCount(drop.getCount());
                            ItemHandlerHelper.insertItemStacked(this.inventory, resultCopy, false);
                            continue;
                        }
                    }
                    ItemHandlerHelper.insertItemStacked(this.inventory, drop, false);
                }

                // Procura blocos vizinhos adjacentes da mesma veia
                for (BlockPos neighbor : BlockPos.getAllInBoxMutable(current.add(-1, -1, -1), current.add(1, 1, 1))) {
                    BlockPos immutable = neighbor.toImmutable();
                    if (!visited.contains(immutable)) {
                        visited.add(immutable);
                        if (world.getBlockState(immutable).getBlock() == targetBlock) {
                            toMine.add(immutable);
                        }
                    }
                }
            }
        }
    }

    /**
     * Se o Slime achar uma amostra no chão, ele escava um poço de 1x1 direto para baixo até achar a veia
     */
    private void digVerticalShaft(BlockPos start) {
        for (int i = 0; i < 25; i++) { // Procura até 25 blocos abaixo da amostra
            BlockPos check = start.down(i);
            IBlockState state = world.getBlockState(check);
            Block block = state.getBlock();

            if (isGeolosysOrOre(state, block) && !isGeolosysSample(block)) {
                // Achou a veia escondida lá embaixo!
                mineVeinChain(check, state, block);
                break;
            }
        }
    }

    /**
     * Identifica Amostras do Geolosys (ex: geolosys:ore_sample)
     */
    private boolean isGeolosysSample(Block block) {
        String name = block.getRegistryName() != null ? block.getRegistryName().toString() : "";
        return name.contains("geolosys") && name.contains("sample");
    }

    /**
     * Suporte Universal a Minérios (Geolosys + OreDictionary)
     */
    private boolean isGeolosysOrOre(IBlockState state, Block block) {
        String regName = block.getRegistryName() != null ? block.getRegistryName().toString() : "";

        // Compatibilidade direta com Geolosys
        if (regName.contains("geolosys")) {
            return true;
        }

        // OreDictionary Padrão
        ItemStack stack = new ItemStack(block, 1, block.getMetaFromState(state));
        int[] oreIDs = OreDictionary.getOreIDs(stack);
        for (int id : oreIDs) {
            String oreName = OreDictionary.getOreName(id);
            if (oreName.startsWith("ore") || oreName.startsWith("cluster")) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand, ItemStack stack) {
        if (!stack.isEmpty() && !world.isRemote) {
            if (stack.getItem() == Items.GOLD_INGOT && !hasSmeltUpgrade) {
                hasSmeltUpgrade = true;
                if (!player.capabilities.isCreativeMode) stack.shrink(1);
                player.sendMessage(new TextComponentString("§aUpgrade Aplicado: Auto-Fundição (Geolosys / Smelt Direct)!"));
                return true;
            }

            if (stack.getItem() == Items.DIAMOND && !hasFortuneUpgrade) {
                hasFortuneUpgrade = true;
                if (!player.capabilities.isCreativeMode) stack.shrink(1);
                player.sendMessage(new TextComponentString("§aUpgrade Aplicado: Fortuna II!"));
                return true;
            }
        }
        return super.processInteract(player, hand, stack);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("SmeltUpgrade", hasSmeltUpgrade);
        compound.setBoolean("FortuneUpgrade", hasFortuneUpgrade);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        this.hasSmeltUpgrade = compound.getBoolean("SmeltUpgrade");
        this.hasFortuneUpgrade = compound.getBoolean("FortuneUpgrade");
    }
}