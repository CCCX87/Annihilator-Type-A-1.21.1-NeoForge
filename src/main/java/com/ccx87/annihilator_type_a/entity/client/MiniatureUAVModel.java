package com.ccx87.annihilator_type_a.entity.client;

import com.ccx87.annihilator_type_a.entity.custom.MiniatureUAVEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.AnimationState;
import org.joml.Vector3f;


import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.ccx87.annihilator_type_a.AnnihilatorTypeA.MOD_ID;

public class MiniatureUAVModel<T extends MiniatureUAVEntity> extends HierarchicalModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(MOD_ID, "miniature_uav_model"), "main");
    private final ModelPart root;
    private final Vector3f animationVecCache = new Vector3f();

    private final Map<UUID, AnimationState> attackStates = new HashMap<>();

    public MiniatureUAVModel(ModelPart root) {
        this.root = root;
        ;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition miniatureUAV = partdefinition.addOrReplaceChild("miniatureUAV", CubeListBuilder.create().texOffs(0, 0).addBox(-3.754F, -0.0493F, -10.2554F, 8.0F, 2.0F, 15.0F, new CubeDeformation(0.0F))
                .texOffs(0, 77).addBox(4.527F, -0.0143F, -6.7814F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.006F))
                .texOffs(78, 24).addBox(-7.025F, -0.0143F, -6.7814F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(66, 42).addBox(4.229F, -0.0143F, -5.2374F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.004F))
                .texOffs(60, 53).addBox(-9.746F, -0.0143F, -5.2374F, 6.0F, 1.0F, 1.0F, new CubeDeformation(0.004F))
                .texOffs(60, 45).addBox(3.773F, -0.0143F, -4.4654F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.003F))
                .texOffs(60, 47).addBox(-11.746F, -0.0143F, -4.4654F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.003F))
                .texOffs(0, 60).addBox(3.931F, -0.0143F, -3.6934F, 9.0F, 1.0F, 1.0F, new CubeDeformation(0.002F))
                .texOffs(20, 60).addBox(-12.746F, -0.0143F, -3.6934F, 9.0F, 1.0F, 1.0F, new CubeDeformation(0.002F))
                .texOffs(36, 56).addBox(3.896F, -0.0143F, -2.9214F, 10.0F, 1.0F, 1.0F, new CubeDeformation(0.001F))
                .texOffs(36, 58).addBox(-13.746F, -0.0143F, -2.9214F, 10.0F, 1.0F, 1.0F, new CubeDeformation(0.001F))
                .texOffs(46, 11).addBox(4.168F, -0.0143F, -2.1494F, 10.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(46, 15).addBox(-13.746F, -0.0143F, -2.1494F, 10.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(78, 44).addBox(-5.746F, -0.0143F, 0.8316F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(78, 46).addBox(4.176F, -0.0143F, 0.8316F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(78, 30).addBox(6.141F, -0.0143F, 0.8316F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(78, 33).addBox(-6.676F, -0.0143F, 0.8316F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(52, 65).addBox(7.878F, -0.0143F, 0.8316F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(12, 66).addBox(-8.413F, -0.0143F, 0.8316F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(64, 74).addBox(14.14F, -0.0143F, -1.3774F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(72, 74).addBox(-14.668F, -0.0143F, -1.3774F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(36, 68).addBox(12.896F, -0.0143F, 0.8316F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(42, 76).addBox(-13.424F, -0.0143F, 0.8316F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(60, 78).addBox(11.352F, -0.0143F, 0.8316F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(64, 78).addBox(-11.88F, -0.0143F, 0.8316F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(68, 78).addBox(9.615F, -0.0143F, 0.8316F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(72, 78).addBox(-10.143F, -0.0143F, 0.8316F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(10, 74).addBox(4.071F, -0.0143F, -6.0094F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.005F))
                .texOffs(0, 51).addBox(-8.413F, 0.7577F, -7.3604F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(18, 51).addBox(7.771F, 0.7577F, -7.3604F, 1.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(22, 74).addBox(-8.746F, -0.0143F, -6.0094F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.005F))
                .texOffs(16, 62).addBox(4.211F, -0.0143F, 4.7986F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(28, 62).addBox(-4.746F, -0.0143F, 4.7986F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(70, 19).addBox(5.176F, -0.0143F, 5.5706F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(36, 71).addBox(-5.711F, -0.0143F, 5.5706F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 72).addBox(6.134F, -0.0143F, 6.1496F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(72, 10).addBox(-6.676F, -0.0143F, 6.1496F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(56, 74).addBox(7.092F, -0.0143F, 6.7286F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(10, 76).addBox(-7.634F, -0.0143F, 6.7286F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(78, 36).addBox(3.825F, 0.9857F, -8.3254F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(46, 71).addBox(-4.346F, 0.9857F, -8.3254F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(46, 19).addBox(-3.754F, 0.1787F, 5.1846F, 8.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 17).addBox(-3.754F, -0.1793F, -6.2024F, 8.0F, 1.0F, 15.0F, new CubeDeformation(-0.001F))
                .texOffs(46, 24).addBox(-1.7691F, -0.0484F, -14.1164F, 4.0F, 2.0F, 6.0F, new CubeDeformation(0.001F))
                .texOffs(60, 49).addBox(-2.2971F, -0.9784F, -8.3263F, 5.0F, 1.0F, 3.0F, new CubeDeformation(0.1F))
                .texOffs(72, 15).addBox(-1.7761F, -1.1714F, -5.8173F, 4.0F, 1.0F, 2.0F, new CubeDeformation(0.001F))
                .texOffs(74, 53).addBox(-1.2621F, -1.1714F, -3.8873F, 3.0F, 1.0F, 2.0F, new CubeDeformation(0.001F))
                .texOffs(38, 33).addBox(-1.2621F, -1.1714F, -1.7643F, 3.0F, 1.0F, 11.0F, new CubeDeformation(0.001F))
                .texOffs(78, 26).addBox(-1.2481F, -0.3994F, 8.4297F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.001F))
                .texOffs(48, 78).addBox(-0.7341F, -0.3994F, 8.8156F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.001F))
                .texOffs(54, 78).addBox(-0.7271F, -0.3994F, 9.2016F, 2.0F, 1.0F, 1.0F, new CubeDeformation(-0.2F))
                .texOffs(38, 45).addBox(2.6679F, -1.1714F, -1.3783F, 1.0F, 1.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(68, 62).addBox(2.6679F, -1.7504F, 3.8327F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.001F))
                .texOffs(56, 62).addBox(2.6679F, -2.5224F, 4.6046F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.002F))
                .texOffs(40, 65).addBox(2.6679F, -3.2944F, 5.3766F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.003F))
                .texOffs(0, 66).addBox(2.6679F, -4.0664F, 6.1486F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.004F))
                .texOffs(66, 24).addBox(2.6679F, -4.8384F, 6.9207F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.005F))
                .texOffs(66, 30).addBox(2.6679F, -5.6104F, 7.6927F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.006F))
                .texOffs(66, 36).addBox(-3.1941F, -5.6104F, 7.6927F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.006F))
                .texOffs(68, 0).addBox(-3.1941F, -4.8384F, 6.9207F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.005F))
                .texOffs(12, 68).addBox(-3.1941F, -4.0664F, 6.1486F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.004F))
                .texOffs(24, 68).addBox(-3.1941F, -3.2944F, 5.3766F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.003F))
                .texOffs(52, 68).addBox(-3.1941F, -2.5224F, 4.6046F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.002F))
                .texOffs(64, 68).addBox(-3.1941F, -1.7504F, 3.8327F, 1.0F, 1.0F, 5.0F, new CubeDeformation(0.001F))
                .texOffs(18, 76).addBox(3.2469F, -1.1714F, 5.5696F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.001F))
                .texOffs(78, 39).addBox(2.6679F, -0.9784F, 8.5436F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.002F))
                .texOffs(42, 78).addBox(-3.1941F, -0.9784F, 8.5436F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.002F))
                .texOffs(26, 76).addBox(-3.7661F, -1.1714F, 5.5696F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.001F))
                .texOffs(46, 0).addBox(-3.1941F, -1.1714F, -1.3783F, 1.0F, 1.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(68, 6).addBox(-1.2551F, -1.7504F, -4.8523F, 3.0F, 1.0F, 3.0F, new CubeDeformation(0.001F))
                .texOffs(0, 62).addBox(-2.2831F, -1.7504F, -8.3263F, 5.0F, 1.0F, 3.0F, new CubeDeformation(0.001F))
                .texOffs(58, 56).addBox(-1.7691F, -0.3644F, -12.9583F, 4.0F, 1.0F, 5.0F, new CubeDeformation(0.002F))
                .texOffs(40, 60).addBox(-1.7691F, -0.7504F, -11.9933F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.001F))
                .texOffs(34, 76).addBox(-1.2621F, -0.0484F, -15.0814F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.2F))
                .texOffs(46, 74).addBox(-1.2481F, -0.0484F, -16.6254F, 3.0F, 2.0F, 2.0F, new CubeDeformation(0.001F))
                .texOffs(76, 49).addBox(-0.056F, 0.7577F, -16.6244F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(76, 71).addBox(-1.456F, 0.7577F, -16.6244F, 2.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(76, 56).addBox(-1.2481F, -0.0484F, -17.5904F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(76, 59).addBox(-0.7271F, 1.7236F, -17.1044F, 2.0F, 1.0F, 2.0F, new CubeDeformation(-0.2F))
                .texOffs(76, 68).addBox(-0.7341F, 1.7236F, -15.4673F, 2.0F, 1.0F, 2.0F, new CubeDeformation(-0.1F))
                .texOffs(0, 33).addBox(-0.8411F, 1.7236F, -13.7303F, 2.0F, 1.0F, 17.0F, new CubeDeformation(0.001F))
                .texOffs(78, 28).addBox(-1.2481F, 0.9516F, -18.5553F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.001F)), PartPose.offset(0.0F, 20.1193F, 2.5354F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }



    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        long flyTime = (long) (ageInTicks * 50);
        KeyframeAnimations.animate(this, MiniatureUAVAnimations.fly,
                flyTime, 1.0F, this.animationVecCache);

        UUID id = entity.getUUID();
        AnimationState attackState = attackStates.computeIfAbsent(id, k -> new AnimationState());

        if (entity.isAttacking()) {
            if (!attackState.isStarted()) {
                attackState.start(entity.tickCount);
            }
            long attackTime = attackState.getAccumulatedTime();
            KeyframeAnimations.animate(this, MiniatureUAVAnimations.attack,
                    attackTime, 1.0F, this.animationVecCache);
        } else {
            attackState.stop();
        }
    }





    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
        this.root.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
}
