package com.dupermod.client.render;

import com.dupermod.entity.EntityFarmer;
import com.dupermod.entity.EntityLumberjack;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSlime;
import net.minecraft.entity.monster.EntitySlime;

public class RenderCustomSlime extends RenderSlime {

    public RenderCustomSlime(RenderManager renderManager) {
        super(renderManager);
    }

    @Override
    protected void preRenderCallback(EntitySlime entity, float partialTickTime) {
        super.preRenderCallback(entity, partialTickTime);

        // Aplica o tom de cor (R, G, B, Alpha) sobre a textura vanilla
        if (entity instanceof EntityLumberjack) {
            // Castanho / Madeira
            GlStateManager.color(0.55F, 0.27F, 0.07F, 1.0F);
        } else if (entity instanceof EntityFarmer) {
            // Amarelo / Trigo
            GlStateManager.color(1.0F, 0.84F, 0.0F, 1.0F);
        }
    }
}