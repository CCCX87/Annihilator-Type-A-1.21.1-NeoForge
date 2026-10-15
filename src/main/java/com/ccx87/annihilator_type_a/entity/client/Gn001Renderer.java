package com.ccx87.annihilator_type_a.entity.client;

import com.ccx87.annihilator_type_a.entity.custom.Gn001Entity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import static com.ccx87.annihilator_type_a.AnnihilatorTypeA.MOD_ID;

public class Gn001Renderer extends MobRenderer<Gn001Entity,Gn001Model<Gn001Entity>> {
    public Gn001Renderer(EntityRendererProvider.Context context) {
        super(context, new Gn001Model<>(context.bakeLayer(Gn001Model.LAYER_GN)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(Gn001Entity gn001Entity) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID,"textures/entity/gn001/gn001.png");
    }

    @Override
    public void render(Gn001Entity entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        LivingEntity owner = entity.getOwner();
        float scale = 1f;
        if (owner != null) {
            float ownerHeight = owner.getBbHeight();
            float baseHeight = 1.8F;
            float ratio = ownerHeight / baseHeight / 3;
            float amplifiedRatio;
            if (ratio <= 1.0F) {
                amplifiedRatio = ratio;
            } else {
                amplifiedRatio = 1.0F + (ratio - 1.0F) ;
            }
            scale = amplifiedRatio;
        }
        poseStack.scale(scale, scale, scale);
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}
