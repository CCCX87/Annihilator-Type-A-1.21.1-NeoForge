package com.ccx87.annihilator_type_a.entity.client;

import com.ccx87.annihilator_type_a.entity.custom.MiniatureUAVEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import static com.ccx87.annihilator_type_a.AnnihilatorTypeA.MOD_ID;

public class MiniatureUAVRenderer
        extends MobRenderer<MiniatureUAVEntity, MiniatureUAVModel<MiniatureUAVEntity>> {

    public MiniatureUAVRenderer(EntityRendererProvider.Context context) {
        super(context,
                new MiniatureUAVModel<>(context.bakeLayer(MiniatureUAVModel.LAYER_LOCATION)),
                0.25f);
    }

    @Override
    public ResourceLocation getTextureLocation(MiniatureUAVEntity entity) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID,
                "textures/entity/miniature_uav/miniature_uav_java.png");
    }

    @Override
    public void render(MiniatureUAVEntity entity, float entityYaw, float partialTicks,
                       PoseStack poseStack, MultiBufferSource buffer, int packedLight) {

        LivingEntity owner = entity.getOwner();
        float scale = 0.55f;
        if (owner != null) {
            float ownerHeight = owner.getBbHeight();
            float baseHeight = 1.8F;
            float ratio = ownerHeight / baseHeight;
            float amplifiedRatio;
            if (ratio <= 1.0F) {
                amplifiedRatio = ratio;
            } else {
                amplifiedRatio = 1.0F + (ratio - 1.0F);
            }
            scale = 0.55f * amplifiedRatio;
        }
        poseStack.scale(scale, scale, scale);
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }
}