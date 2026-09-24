package com.dupermod.client.render;

import com.dupermod.entity.EntityAllyBase;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderCustomSlime extends RenderLiving<EntityAllyBase> {

    private static final ResourceLocation SLIME_TEXTURES = new ResourceLocation("textures/entity/slime/slime.png");
    private final float red;
    private final float green;
    private final float blue;

    public RenderCustomSlime(RenderManager renderManagerIn, float red, float green, float blue) {
        super(renderManagerIn, new ModelSlime(16), 0.25F);
        this.red = red;
        this.green = green;
        this.blue = blue;
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityAllyBase entity) {
        return SLIME_TEXTURES;
    }

    @Override
    protected void preRenderCallback(EntityAllyBase entity, float partialTickTime) {
        // Redimensiona levemente para evitar Z-fighting na renderização
        GlStateManager.scale(0.999F, 0.999F, 0.999F);
        // Aplica o filtro de cor no Slime (R, G, B, Alpha)
        GlStateManager.color(this.red, this.green, this.blue, 1.0F);
    }
}