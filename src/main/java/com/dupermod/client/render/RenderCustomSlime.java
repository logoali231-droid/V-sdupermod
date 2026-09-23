package com.dupermod.client.render;

import com.dupermod.entity.EntityAllyBase;
import com.dupermod.entity.EntityFarmer;
import com.dupermod.entity.EntityFighter;
import com.dupermod.entity.EntityLumberjack;
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

        if (entity instanceof EntityLumberjack) {
            // Castanho / Madeira
            GlStateManager.color(0.55F, 0.27F, 0.07F, 1.0F);
        } else if (entity instanceof EntityFarmer) {
            // Amarelo / Trigo
            GlStateManager.color(1.0F, 0.84F, 0.0F, 1.0F);
        } else if (entity instanceof EntityFighter) {
            // Vermelho / Guerreiro
            GlStateManager.color(0.85F, 0.15F, 0.15F, 1.0F);
        }
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityAllyBase entity) {
        return SLIME_TEXTURES;
    }
}