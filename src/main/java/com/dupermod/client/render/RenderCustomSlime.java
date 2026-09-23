package com.dupermod.client.render;

import com.dupermod.entity.EntityAllyBase;
import com.dupermod.entity.EntityCooler;
import com.dupermod.entity.EntityFarmer;
import com.dupermod.entity.EntityFighter;
import com.dupermod.entity.EntityLumberjack;
import com.dupermod.entity.EntityMiner;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderCustomSlime extends RenderLiving<EntityAllyBase> {

    private static final ResourceLocation SLIME_TEXTURES = new ResourceLocation("textures/entity/slime/slime.png");

    public RenderCustomSlime(RenderManager renderManager) {
        super(renderManager, new ModelSlime(16), 0.25F);
    }

    @Override
    protected void preRenderCallback(EntityAllyBase entity, float partialTickTime) {
        super.preRenderCallback(entity, partialTickTime);

        // Escala proporcional ao Nível de Fusão
        float scale = 0.8F + (entity.getAllyLevel() - 1) * 0.4F;
        GlStateManager.scale(scale, scale, scale);

        if (entity instanceof EntityLumberjack) {
            GlStateManager.color(0.55F, 0.27F, 0.07F, 1.0F); // Castanho
        } else if (entity instanceof EntityFarmer) {
            GlStateManager.color(1.0F, 0.84F, 0.0F, 1.0F); // Amarelo
        } else if (entity instanceof EntityFighter) {
            GlStateManager.color(0.85F, 0.15F, 0.15F, 1.0F); // Vermelho
        } else if (entity instanceof EntityCooler) {
            GlStateManager.color(0.95F, 0.98F, 1.0F, 1.0F); // Branco/Gelo
        } else if (entity instanceof EntityMiner) {
            GlStateManager.color(0.5F, 0.1F, 0.8F, 1.0F); // Roxo Minerador
        } else if (entity instanceof EntityMiner) {
            GlStateManager.color(0.5F, 0.1F, 0.8F, 1.0F); // Roxo Ametista / Minerador
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityAllyBase entity) {
        return SLIME_TEXTURES;
    }
}