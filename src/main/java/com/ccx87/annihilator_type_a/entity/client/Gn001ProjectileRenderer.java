package com.ccx87.annihilator_type_a.entity.client;

import com.ccx87.annihilator_type_a.entity.custom.Gn001Projectile;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class Gn001ProjectileRenderer extends EntityRenderer<Gn001Projectile> {
    public Gn001ProjectileRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public ResourceLocation getTextureLocation(Gn001Projectile entity) {
        return null;
    }

    @Override
    public void render(Gn001Projectile entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {

    }
}