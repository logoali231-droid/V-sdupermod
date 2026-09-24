package com.dupermod.proxy;

import com.dupermod.client.render.RenderCustomSlime;
import com.dupermod.entity.EntityFarmer;
import com.dupermod.entity.EntityFighter;
import com.dupermod.entity.EntityLumberjack;
import com.dupermod.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

        @Override
        public void registerRenders() {
            // 1. Minerador: Cinza Metalizado / Pedra (R: 0.35, G: 0.35, B: 0.4)
            RenderingRegistry.registerEntityRenderingHandler(EntityMiner.class,
                    rm -> new RenderCustomSlime(rm, 0.35F, 0.35F, 0.40F));

            // 2. Lenhador: Marrom/Verde Madeira (R: 0.45, G: 0.25, B: 0.1F)
            RenderingRegistry.registerEntityRenderingHandler(EntityLumberjack.class,
                    rm -> new RenderCustomSlime(rm, 0.45F, 0.25F, 0.10F));

            // 3. Factory Slime: Laranja Industrial / Cobre (R: 0.9F, G: 0.45F, B: 0.1F)
            RenderingRegistry.registerEntityRenderingHandler(EntityFactorySlime.class,
                    rm -> new RenderCustomSlime(rm, 0.90F, 0.45F, 0.10F));

            // 4. Sieve Slime: Amarelo Areia (R: 0.95F, G: 0.85F, B: 0.3F)
            RenderingRegistry.registerEntityRenderingHandler(EntitySieve.class,
                    rm -> new RenderCustomSlime(rm, 0.95F, 0.85F, 0.30F));
        }
    }
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        // Registo dos renderizadores com tint de cor para as entidades Slime
        RenderingRegistry.registerEntityRenderingHandler(EntityLumberjack.class, RenderCustomSlime::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityFarmer.class, RenderCustomSlime::new);
        RenderingRegistry.registerEntityRenderingHandler(EntityFighter.class, RenderCustomSlime::new);
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

        // Aplicação de cores RGB dinâmicas sobre o modelo vanilla (IItemColor)
        // Upgrade Multiplicador: Verde Madeira / Floresta (#2E7D32)
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new IItemColor() {
            @Override
            public int getColorFromItemstack(ItemStack stack, int tintIndex) {
                return tintIndex == 0 ? 0x2E7D32 : 0xFFFFFF;
            }
        }, ModItems.upgradeLogMultiplier);

        // Upgrade Carvão: Cinza Escuro Carvão (#212121)
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new IItemColor() {
            @Override
            public int getColorFromItemstack(ItemStack stack, int tintIndex) {
                return tintIndex == 0 ? 0x212121 : 0xFFFFFF;
            }
        }, ModItems.upgradeCharcoal);

        // Upgrade Velocidade: Azul Elétrico Ciano (#00E5FF)
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new IItemColor() {
            @Override
            public int getColorFromItemstack(ItemStack stack, int tintIndex) {
                return tintIndex == 0 ? 0x00E5FF : 0xFFFFFF;
            }
        }, ModItems.upgradeSpeed);
    }

    @Override
    public void registerItemRenderer(Item item, int meta, String id) {
        // Redireciona o modelo dos upgrades para o ícone vanilla "fireworks_charge"
        if (item == ModItems.upgradeLogMultiplier || item == ModItems.upgradeCharcoal || item == ModItems.upgradeSpeed) {
            ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation("minecraft:fireworks_charge", "inventory"));
        } else {
            ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation("dupermod:" + id, "inventory"));
        }
    }
}