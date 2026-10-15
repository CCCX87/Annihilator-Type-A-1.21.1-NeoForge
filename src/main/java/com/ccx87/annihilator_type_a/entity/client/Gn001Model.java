package com.ccx87.annihilator_type_a.entity.client;


import com.ccx87.annihilator_type_a.entity.custom.Gn001Entity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;


import static com.ccx87.annihilator_type_a.AnnihilatorTypeA.MOD_ID;

public class Gn001Model<T extends Gn001Entity> extends HierarchicalModel<T> {



    public static final ModelLayerLocation LAYER_GN =
            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(MOD_ID, "gn001_model"), "main");
    private final ModelPart Center;
    private final ModelPart Root;
    private final ModelPart Head;
    private final ModelPart Eye;
    private final ModelPart Light;
    private final ModelPart Body;
    private final ModelPart Sword_Body;
    private final ModelPart GNParticles_4;
    private final ModelPart GNParticles_3;
    private final ModelPart GNParticles_2;
    private final ModelPart GNParticles_1;
    private final ModelPart ArmLeft;
    private final ModelPart ForearmLeft;
    private final ModelPart Shield;
    private final ModelPart HandLeft;
    private final ModelPart ArmRight;
    private final ModelPart ForearmRight;
    private final ModelPart Sword_Hand;
    private final ModelPart Sword_Blade;
    private final ModelPart HandRight;
    private final ModelPart LegLeft;
    private final ModelPart CalfLeft;
    private final ModelPart FootLeft;
    private final ModelPart LegRight;
    private final ModelPart CalfRight;
    private final ModelPart FootRight;

	public Gn001Model( ModelPart root) {
        this.Center = root.getChild("Center");
        this.Root = this.Center.getChild("Root");
        this.Head = this.Root.getChild("Head");
        this.Eye = this.Head.getChild("Eye");
        this.Light = this.Head.getChild("Light");
        this.Body = this.Root.getChild("Body");
        this.Sword_Body = this.Body.getChild("Sword_Body");
        this.GNParticles_4 = this.Body.getChild("GNParticles_4");
        this.GNParticles_3 = this.Body.getChild("GNParticles_3");
        this.GNParticles_2 = this.Body.getChild("GNParticles_2");
        this.GNParticles_1 = this.Body.getChild("GNParticles_1");
        this.ArmLeft = this.Root.getChild("ArmLeft");
        this.ForearmLeft = this.ArmLeft.getChild("ForearmLeft");
        this.Shield = this.ForearmLeft.getChild("Shield");
        this.HandLeft = this.ForearmLeft.getChild("HandLeft");
        this.ArmRight = this.Root.getChild("ArmRight");
        this.ForearmRight = this.ArmRight.getChild("ForearmRight");
        this.Sword_Hand = this.ForearmRight.getChild("Sword_Hand");
        this.Sword_Blade = this.Sword_Hand.getChild("Sword_Blade");
        this.HandRight = this.ForearmRight.getChild("HandRight");
        this.LegLeft = this.Root.getChild("LegLeft");
        this.CalfLeft = this.LegLeft.getChild("CalfLeft");
        this.FootLeft = this.CalfLeft.getChild("FootLeft");
        this.LegRight = this.Root.getChild("LegRight");
        this.CalfRight = this.LegRight.getChild("CalfRight");
        this.FootRight = this.CalfRight.getChild("FootRight");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition Center = partdefinition.addOrReplaceChild("Center", CubeListBuilder.create(), PartPose.offset(-0.6195F, -28.7692F, 0.0414F));
        PartDefinition Root = Center.addOrReplaceChild("Root", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition Head = Root.addOrReplaceChild("Head", CubeListBuilder.create().texOffs(0, 177).addBox(-0.9896F, -9.2816F, -0.6217F, 2.0F, 4.0F, 5.0F, new CubeDeformation(0.001F))
                .texOffs(104, 143).addBox(-2.4896F, -7.8816F, -1.2217F, 5.0F, 8.0F, 5.0F, new CubeDeformation(0.001F))
                .texOffs(166, 0).addBox(-4.2896F, -6.1816F, -2.6217F, 2.0F, 4.0F, 6.0F, new CubeDeformation(-0.15F))
                .texOffs(166, 84).addBox(2.3104F, -6.1816F, -2.6217F, 2.0F, 4.0F, 6.0F, new CubeDeformation(-0.15F))
                .texOffs(22, 119).addBox(-2.4896F, -3.2816F, -2.5217F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(152, 84).addBox(-1.0896F, -4.5816F, -2.5217F, 2.0F, 1.0F, 2.0F, new CubeDeformation(-0.1F))
                .texOffs(86, 104).addBox(-0.4896F, -3.8816F, -2.7217F, 1.0F, 2.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(202, 37).addBox(-0.9896F, -3.8816F, -2.5217F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.001F))
                .texOffs(146, 69).addBox(0.5104F, -3.2816F, -2.5217F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(36, 151).addBox(-2.9896F, -8.3999F, 2.8759F, 2.0F, 4.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(192, 73).addBox(1.0104F, -8.3999F, 2.8759F, 2.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(0.1092F, -15.1491F, -0.4197F));

        PartDefinition Head_r1 = Head.addOrReplaceChild("Head_r1", CubeListBuilder.create().texOffs(26, 197).addBox(-0.5F, -6.0F, -0.5F, 1.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.906F, -5.9577F, -1.5245F, -0.639F, 0.1654F, -1.0558F));

        PartDefinition Head_r2 = Head.addOrReplaceChild("Head_r2", CubeListBuilder.create().texOffs(22, 197).addBox(-0.5F, -6.0F, -0.5F, 1.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.8971F, -5.9577F, -1.5245F, -0.639F, -0.1654F, 1.0558F));

        PartDefinition Head_r3 = Head.addOrReplaceChild("Head_r3", CubeListBuilder.create().texOffs(138, 198).addBox(-0.5F, -2.0F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.4896F, -5.9999F, -1.2194F, 0.3491F, 0.0F, 0.0F));

        PartDefinition Head_r4 = Head.addOrReplaceChild("Head_r4", CubeListBuilder.create().texOffs(182, 148).addBox(-2.5F, -1.0F, -1.0F, 4.0F, 5.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5104F, -4.9999F, 3.9759F, -0.3491F, 0.0F, 0.0F));

        PartDefinition Head_r5 = Head.addOrReplaceChild("Head_r5", CubeListBuilder.create().texOffs(192, 66).addBox(-0.5F, -3.0F, -1.0F, 2.0F, 4.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.4896F, -5.9999F, 3.9759F, -0.3491F, 0.0F, 0.0F));

        PartDefinition Head_r6 = Head.addOrReplaceChild("Head_r6", CubeListBuilder.create().texOffs(86, 65).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.0104F, -0.5792F, -2.4099F, -0.1745F, 0.0F, 0.0F));

        PartDefinition Head_r7 = Head.addOrReplaceChild("Head_r7", CubeListBuilder.create().texOffs(28, 104).addBox(-0.5F, -1.5F, -0.5F, 1.0F, 2.0F, 1.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.0104F, -0.3816F, -2.0217F, -0.3491F, 0.0F, 0.0F));

        PartDefinition Head_r8 = Head.addOrReplaceChild("Head_r8", CubeListBuilder.create().texOffs(82, 65).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.0104F, -1.2816F, -2.2217F, -0.3491F, 0.0F, 0.0F));

        PartDefinition Head_r9 = Head.addOrReplaceChild("Head_r9", CubeListBuilder.create().texOffs(34, 199).addBox(0.5F, 0.3F, -4.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.11F)), PartPose.offsetAndRotation(0.0104F, -3.8816F, 1.4783F, 0.0F, 0.0F, -0.3491F));

        PartDefinition Head_r10 = Head.addOrReplaceChild("Head_r10", CubeListBuilder.create().texOffs(198, 193).addBox(0.5F, 0.3F, -4.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.11F)), PartPose.offsetAndRotation(0.0104F, -3.5816F, 1.3783F, 0.0F, 0.0F, -0.3491F));

        PartDefinition Head_r11 = Head.addOrReplaceChild("Head_r11", CubeListBuilder.create().texOffs(196, 198).addBox(-1.0F, -0.5F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.11F)), PartPose.offsetAndRotation(-1.6896F, -3.6816F, -1.5217F, 0.0F, 0.0F, 0.3491F));

        PartDefinition Head_r12 = Head.addOrReplaceChild("Head_r12", CubeListBuilder.create().texOffs(188, 198).addBox(-1.0F, -0.5F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.11F)), PartPose.offsetAndRotation(-1.6896F, -3.3816F, -1.6217F, 0.0F, 0.0F, 0.3491F));

        PartDefinition Head_r13 = Head.addOrReplaceChild("Head_r13", CubeListBuilder.create().texOffs(164, 174).addBox(-0.5F, -2.0F, -3.0F, 1.0F, 4.0F, 6.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(2.6524F, -2.3419F, 0.2783F, 0.0F, 0.0F, 0.3491F));

        PartDefinition Head_r14 = Head.addOrReplaceChild("Head_r14", CubeListBuilder.create().texOffs(72, 115).addBox(-0.5F, -2.0F, -3.0F, 1.0F, 4.0F, 6.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(-2.6476F, -2.3419F, 0.2783F, 0.0F, 0.0F, -0.3491F));

        PartDefinition Head_r15 = Head.addOrReplaceChild("Head_r15", CubeListBuilder.create().texOffs(190, 16).addBox(0.0F, -1.0F, -2.0F, 1.0F, 2.0F, 5.0F, new CubeDeformation(-0.15F))
                .texOffs(146, 198).addBox(-8.2F, -1.0F, -2.0F, 1.0F, 2.0F, 3.0F, new CubeDeformation(-0.15F))
                .texOffs(198, 140).addBox(0.2F, -1.0F, -2.0F, 1.0F, 2.0F, 3.0F, new CubeDeformation(-0.15F))
                .texOffs(190, 9).addBox(-8.0F, -1.0F, -2.0F, 1.0F, 2.0F, 5.0F, new CubeDeformation(-0.15F)), PartPose.offsetAndRotation(3.5104F, -5.1816F, 3.3783F, 0.3491F, 0.0F, 0.0F));

        PartDefinition Head_r16 = Head.addOrReplaceChild("Head_r16", CubeListBuilder.create().texOffs(152, 191).addBox(-0.5F, -1.5F, -2.0F, 1.0F, 3.0F, 4.0F, new CubeDeformation(-0.15F)), PartPose.offsetAndRotation(3.7789F, -2.2157F, -2.877F, 0.3491F, 0.3491F, 0.2618F));

        PartDefinition Head_r17 = Head.addOrReplaceChild("Head_r17", CubeListBuilder.create().texOffs(142, 191).addBox(-0.5F, -1.5F, -2.0F, 1.0F, 3.0F, 4.0F, new CubeDeformation(-0.15F)), PartPose.offsetAndRotation(-3.8069F, -2.2706F, -2.7033F, 0.3491F, -0.3491F, -0.2618F));

        PartDefinition Eye = Head.addOrReplaceChild("Eye", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition EyeLeft_r1 = Eye.addOrReplaceChild("EyeLeft_r1", CubeListBuilder.create().texOffs(124, 143).addBox(0.5F, -0.6F, -3.7F, 2.0F, 1.0F, 2.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.2104F, -3.8816F, 1.3783F, 0.0F, 0.0F, -0.3491F));

        PartDefinition EyeRight_r1 = Eye.addOrReplaceChild("EyeRight_r1", CubeListBuilder.create().texOffs(142, 10).addBox(-1.0F, -0.5F, -1.0F, 2.0F, 1.0F, 2.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(-1.6896F, -4.5816F, -1.3217F, 0.0F, 0.0F, 0.3491F));

        PartDefinition Light = Head.addOrReplaceChild("Light", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition Light_r1 = Light.addOrReplaceChild("Light_r1", CubeListBuilder.create().texOffs(78, 65).addBox(-0.5F, -0.5F, -0.5F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.15F)), PartPose.offsetAndRotation(0.0104F, -6.4011F, -1.9785F, 0.2519F, -0.2443F, 0.7543F));

        PartDefinition Body = Root.addOrReplaceChild("Body", CubeListBuilder.create().texOffs(54, 159).addBox(-1.5757F, -13.2261F, -0.7401F, 3.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(154, 198).addBox(-2.5757F, -13.2261F, 0.3599F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(162, 198).addBox(1.4243F, -13.2261F, 0.3599F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(72, 26).addBox(-4.5757F, -11.2261F, -3.3401F, 9.0F, 12.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(56, 104).addBox(-3.5757F, 0.7739F, -3.3401F, 7.0F, 3.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(90, 95).addBox(-3.5757F, 3.4739F, -3.3401F, 7.0F, 5.0F, 8.0F, new CubeDeformation(-0.2F))
                .texOffs(42, 184).addBox(1.4243F, 0.4739F, 4.2599F, 1.0F, 8.0F, 3.0F, new CubeDeformation(-0.2F))
                .texOffs(14, 191).addBox(-2.5757F, 0.4739F, 4.2599F, 1.0F, 8.0F, 3.0F, new CubeDeformation(-0.2F))
                .texOffs(162, 191).addBox(-1.5757F, -5.2261F, -5.3401F, 3.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(202, 6).addBox(-1.0757F, -6.5261F, -5.7401F, 2.0F, 3.0F, 1.0F, new CubeDeformation(-0.2F))
                .texOffs(190, 34).addBox(-1.5757F, -0.6261F, -6.3401F, 3.0F, 4.0F, 3.0F, new CubeDeformation(0.1F))
                .texOffs(0, 193).addBox(-4.5757F, -9.2261F, -4.9401F, 1.0F, 7.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(6, 193).addBox(3.4243F, -9.2261F, -4.9401F, 1.0F, 7.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(182, 6).addBox(-3.5757F, -3.4261F, -4.9401F, 7.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(176, 135).addBox(-3.5757F, -9.4261F, -4.4401F, 7.0F, 6.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 135).addBox(4.4243F, -10.8261F, -3.3401F, 3.0F, 5.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(134, 122).addBox(-7.5757F, -10.8261F, -3.3401F, 3.0F, 5.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(188, 28).addBox(-2.0757F, 1.6739F, -5.3401F, 4.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(188, 45).addBox(-2.0757F, 3.3739F, -5.3401F, 4.0F, 3.0F, 3.0F, new CubeDeformation(-0.2F))
                .texOffs(106, 19).addBox(-2.0757F, 6.0739F, -5.8401F, 4.0F, 6.0F, 9.0F, new CubeDeformation(-0.1F))
                .texOffs(14, 177).addBox(-5.8757F, 4.6739F, -5.8401F, 4.0F, 3.0F, 4.0F, new CubeDeformation(-0.1F))
                .texOffs(14, 184).addBox(-4.9757F, 7.4739F, -5.8401F, 3.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(182, 156).addBox(-7.9757F, 6.6739F, -5.8401F, 3.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(50, 183).addBox(4.8243F, 6.6739F, -5.8401F, 3.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(28, 184).addBox(1.8243F, 7.4739F, -5.8401F, 3.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(84, 178).addBox(1.7243F, 4.6739F, -5.8401F, 4.0F, 3.0F, 4.0F, new CubeDeformation(-0.1F)), PartPose.offset(0.1953F, -3.2047F, -0.7013F));

        PartDefinition Body_r1 = Body.addOrReplaceChild("Body_r1", CubeListBuilder.create().texOffs(180, 166).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(-0.01F)), PartPose.offsetAndRotation(8.6913F, 7.3239F, -3.8401F, 0.0F, 0.0F, -0.5236F));

        PartDefinition Body_r2 = Body.addOrReplaceChild("Body_r2", CubeListBuilder.create().texOffs(178, 174).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 3.0F, 4.0F, new CubeDeformation(-0.01F)), PartPose.offsetAndRotation(-8.9087F, 7.3239F, -3.8401F, 0.0F, 0.0F, 0.5236F));

        PartDefinition Body_r3 = Body.addOrReplaceChild("Body_r3", CubeListBuilder.create().texOffs(190, 41).addBox(-2.0F, -1.2F, -0.5F, 4.0F, 3.0F, 1.0F, new CubeDeformation(-0.4F)), PartPose.offsetAndRotation(-2.8757F, -9.3261F, 5.6599F, -1.309F, 0.0F, -0.5236F));

        PartDefinition Body_r4 = Body.addOrReplaceChild("Body_r4", CubeListBuilder.create().texOffs(56, 197).addBox(-2.0F, -1.0F, -0.5F, 4.0F, 3.0F, 1.0F, new CubeDeformation(-0.4F)), PartPose.offsetAndRotation(-3.1087F, -2.5761F, 5.6599F, 1.309F, 0.0F, 0.5236F));

        PartDefinition Body_r5 = Body.addOrReplaceChild("Body_r5", CubeListBuilder.create().texOffs(194, 110).addBox(-2.0F, -1.5F, -0.5F, 4.0F, 3.0F, 1.0F, new CubeDeformation(-0.4F)), PartPose.offsetAndRotation(2.8913F, -2.5761F, 5.6599F, 1.309F, 0.0F, -0.5236F));

        PartDefinition Body_r6 = Body.addOrReplaceChild("Body_r6", CubeListBuilder.create().texOffs(132, 170).addBox(-2.5F, -1.5F, -0.5F, 4.0F, 3.0F, 1.0F, new CubeDeformation(-0.4F)), PartPose.offsetAndRotation(3.1243F, -9.0261F, 5.6599F, -1.309F, 0.0F, 0.5236F));

        PartDefinition Body_r7 = Body.addOrReplaceChild("Body_r7", CubeListBuilder.create().texOffs(200, 120).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(-3.3757F, -10.3372F, 4.5015F, 0.1745F, 0.0F, 0.0F));

        PartDefinition Body_r8 = Body.addOrReplaceChild("Body_r8", CubeListBuilder.create().texOffs(200, 41).addBox(0.0F, -2.0F, -2.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(2.3243F, -9.5261F, 5.6599F, 0.1745F, 0.0F, 0.0F));

        PartDefinition Body_r9 = Body.addOrReplaceChild("Body_r9", CubeListBuilder.create().texOffs(176, 121).addBox(0.0F, -1.0F, -2.5F, 2.0F, 2.0F, 6.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(-4.8757F, -5.9368F, 5.8878F, 0.0F, 0.2618F, 0.0F));

        PartDefinition Body_r10 = Body.addOrReplaceChild("Body_r10", CubeListBuilder.create().texOffs(176, 102).addBox(-1.0F, -1.0F, -2.5F, 2.0F, 2.0F, 6.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(3.7243F, -5.9368F, 5.7878F, 0.0F, -0.2618F, 0.0F));

        PartDefinition Body_r11 = Body.addOrReplaceChild("Body_r11", CubeListBuilder.create().texOffs(176, 94).addBox(-1.0F, -1.0F, -2.5F, 2.0F, 2.0F, 6.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(-0.0757F, -2.1368F, 5.7878F, 0.3491F, 0.0F, 0.0F));

        PartDefinition Body_r12 = Body.addOrReplaceChild("Body_r12", CubeListBuilder.create().texOffs(168, 79).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 2.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.0757F, -10.2261F, 5.6599F, 0.1745F, 0.0F, 0.0F));

        PartDefinition Body_r13 = Body.addOrReplaceChild("Body_r13", CubeListBuilder.create().texOffs(72, 65).addBox(-0.6432F, 1.6621F, -3.5F, 1.0F, 0.0F, 2.0F, new CubeDeformation(-0.2F))
                .texOffs(194, 178).addBox(-0.0432F, 0.0621F, -1.9F, 1.0F, 1.0F, 2.0F, new CubeDeformation(-0.2F))
                .texOffs(84, 175).addBox(-1.2432F, 0.0621F, -1.9F, 1.0F, 1.0F, 2.0F, new CubeDeformation(-0.2F))
                .texOffs(182, 199).addBox(-1.2432F, 0.0621F, -3.5F, 1.0F, 2.0F, 2.0F, new CubeDeformation(-0.2F))
                .texOffs(94, 185).addBox(-0.0432F, 0.0621F, -3.5F, 1.0F, 2.0F, 2.0F, new CubeDeformation(-0.2F))
                .texOffs(36, 159).addBox(-1.1404F, -0.0702F, -0.5F, 2.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.4973F, -11.9338F, -0.8401F, 0.0F, 0.0F, -0.2618F));

        PartDefinition Body_r14 = Body.addOrReplaceChild("Body_r14", CubeListBuilder.create().texOffs(178, 199).addBox(-0.7065F, -4.3562F, -2.7185F, 1.0F, 7.0F, 1.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(-5.4973F, -11.9338F, -0.8401F, -0.4363F, -0.0349F, -0.2618F));

        PartDefinition Body_r15 = Body.addOrReplaceChild("Body_r15", CubeListBuilder.create().texOffs(68, 133).addBox(-0.2728F, -4.3305F, -2.7058F, 1.0F, 7.0F, 1.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(5.3429F, -11.9657F, -0.8401F, -0.4363F, 0.0349F, 0.2618F));

        PartDefinition Body_r16 = Body.addOrReplaceChild("Body_r16", CubeListBuilder.create().texOffs(158, 143).addBox(-0.3452F, 1.1277F, -3.5F, 1.0F, 1.0F, 2.0F, new CubeDeformation(-0.2F))
                .texOffs(90, 167).addBox(-0.9452F, 0.1277F, -1.9F, 1.0F, 1.0F, 2.0F, new CubeDeformation(-0.2F))
                .texOffs(160, 84).addBox(0.2548F, 0.1277F, -1.9F, 1.0F, 1.0F, 2.0F, new CubeDeformation(-0.2F))
                .texOffs(146, 69).addBox(0.2548F, 0.1277F, -3.5F, 1.0F, 2.0F, 2.0F, new CubeDeformation(-0.2F))
                .texOffs(22, 119).addBox(-0.9452F, 0.1277F, -3.5F, 1.0F, 2.0F, 2.0F, new CubeDeformation(-0.2F))
                .texOffs(158, 134).addBox(-0.7643F, -0.1756F, -0.5F, 2.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.3429F, -11.9657F, -0.8401F, 0.0F, 0.0F, 0.2618F));

        PartDefinition Body_r17 = Body.addOrReplaceChild("Body_r17", CubeListBuilder.create().texOffs(146, 87).addBox(-1.5F, -2.5F, -4.0F, 2.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.0757F, -9.3261F, 0.6599F, 0.0F, 0.0F, 0.1745F));

        PartDefinition Body_r18 = Body.addOrReplaceChild("Body_r18", CubeListBuilder.create().texOffs(124, 58).addBox(-3.5F, -3.5F, -4.0F, 3.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.8243F, -3.6261F, -0.3401F, 0.0F, 0.0F, 0.4363F));

        PartDefinition Body_r19 = Body.addOrReplaceChild("Body_r19", CubeListBuilder.create().texOffs(198, 0).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(8.6243F, -4.5658F, -1.6821F, 0.3491F, 0.0F, 0.0F));

        PartDefinition Body_r20 = Body.addOrReplaceChild("Body_r20", CubeListBuilder.create().texOffs(198, 98).addBox(-1.0F, -1.5F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.2243F, -4.8261F, 1.6599F, 0.0F, 0.0F, 0.1745F));

        PartDefinition Body_r21 = Body.addOrReplaceChild("Body_r21", CubeListBuilder.create().texOffs(198, 93).addBox(-1.0F, -1.5F, -1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-8.3757F, -4.8261F, 1.6599F, 0.0F, 0.0F, -0.1745F));

        PartDefinition Body_r22 = Body.addOrReplaceChild("Body_r22", CubeListBuilder.create().texOffs(48, 197).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(5.8154F, -1.4753F, -0.8401F, 0.0F, 0.0F, 0.8727F));

        PartDefinition Body_r23 = Body.addOrReplaceChild("Body_r23", CubeListBuilder.create().texOffs(196, 164).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(-5.8847F, -1.4753F, -0.8401F, 0.0F, 0.0F, -0.8727F));

        PartDefinition Body_r24 = Body.addOrReplaceChild("Body_r24", CubeListBuilder.create().texOffs(100, 178).addBox(-0.6F, -3.0F, -1.0F, 2.0F, 4.0F, 2.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(-8.7757F, -3.6261F, -1.3401F, 0.3491F, 0.0F, 0.0F));

        PartDefinition Body_r25 = Body.addOrReplaceChild("Body_r25", CubeListBuilder.create().texOffs(0, 119).addBox(-2.5F, -3.5F, -4.0F, 3.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.2757F, -4.8261F, -0.3401F, 0.0F, 0.0F, -0.4363F));

        PartDefinition Body_r26 = Body.addOrReplaceChild("Body_r26", CubeListBuilder.create().texOffs(90, 65).addBox(-4.5F, -0.5F, -5.0F, 6.0F, 3.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.1757F, -7.3261F, -0.3401F, 0.0F, 0.0F, 0.2618F));

        PartDefinition Body_r27 = Body.addOrReplaceChild("Body_r27", CubeListBuilder.create().texOffs(56, 90).addBox(-3.0F, -1.5F, -5.5F, 6.0F, 3.0F, 11.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.7243F, -6.8261F, 0.1599F, 0.0F, 0.0F, -0.2618F));

        PartDefinition Body_r28 = Body.addOrReplaceChild("Body_r28", CubeListBuilder.create().texOffs(146, 56).addBox(-1.5F, -2.5F, -4.0F, 2.0F, 5.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(8.9243F, -9.3261F, 0.6599F, 0.0F, 0.0F, -0.1745F));

        PartDefinition Body_r29 = Body.addOrReplaceChild("Body_r29", CubeListBuilder.create().texOffs(96, 153).addBox(-2.5F, 0.5F, -1.0F, 3.0F, 2.0F, 1.0F, new CubeDeformation(-0.09F))
                .texOffs(176, 110).addBox(-5.0F, -1.5F, -1.0F, 8.0F, 3.0F, 1.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.9243F, -9.6261F, -3.8401F, -0.8727F, 0.0F, 0.0F));

        PartDefinition Body_r30 = Body.addOrReplaceChild("Body_r30", CubeListBuilder.create().texOffs(198, 145).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(1.8361F, 0.3052F, -4.8401F, 0.0F, 0.0F, 0.7854F));

        PartDefinition Body_r31 = Body.addOrReplaceChild("Body_r31", CubeListBuilder.create().texOffs(194, 128).addBox(-2.0F, -2.0F, -1.0F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(2.9362F, 3.6052F, -3.8401F, 0.0F, 0.0F, -0.4363F));

        PartDefinition Body_r32 = Body.addOrReplaceChild("Body_r32", CubeListBuilder.create().texOffs(34, 168).addBox(-1.0195F, -1.6176F, -1.5F, 2.0F, 1.0F, 7.0F, new CubeDeformation(0.1F))
                .texOffs(72, 167).addBox(-0.9805F, 0.6176F, -1.5F, 2.0F, 1.0F, 7.0F, new CubeDeformation(0.1F))
                .texOffs(164, 157).addBox(-0.9968F, -0.7113F, -1.5F, 2.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.7091F, 2.2443F, -2.3401F, 0.0F, 0.0F, -0.4363F));

        PartDefinition Body_r33 = Body.addOrReplaceChild("Body_r33", CubeListBuilder.create().texOffs(0, 160).addBox(-2.4F, -2.0F, 0.0F, 2.0F, 2.0F, 7.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.1639F, 4.0052F, -3.8401F, 0.0F, 0.0F, 0.4363F));

        PartDefinition Body_r34 = Body.addOrReplaceChild("Body_r34", CubeListBuilder.create().texOffs(162, 166).addBox(-0.9F, -0.5F, -0.5F, 2.0F, 1.0F, 7.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(-5.4362F, 3.2232F, -3.3401F, 0.0F, 0.0F, 0.4363F));

        PartDefinition Body_r35 = Body.addOrReplaceChild("Body_r35", CubeListBuilder.create().texOffs(128, 87).addBox(-2.0F, -2.0F, 0.0F, 2.0F, 1.0F, 7.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(-4.1639F, 3.0052F, -3.8401F, 0.0F, 0.0F, 0.4363F));

        PartDefinition Body_r36 = Body.addOrReplaceChild("Body_r36", CubeListBuilder.create().texOffs(188, 193).addBox(-2.0F, -2.0F, -1.0F, 3.0F, 3.0F, 2.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(-2.1639F, 4.0052F, -3.8401F, 0.0F, 0.0F, 0.4363F));

        PartDefinition Body_r37 = Body.addOrReplaceChild("Body_r37", CubeListBuilder.create().texOffs(196, 170).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(-2.1639F, 0.3052F, -4.8401F, 0.0F, 0.0F, 0.7854F));

        PartDefinition Sword_Body = Body.addOrReplaceChild("Sword_Body", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition GNParticles_4 = Body.addOrReplaceChild("GNParticles_4", CubeListBuilder.create().texOffs(116, 0).addBox(-3.0F, -3.0F, -3.5F, 6.0F, 6.0F, 7.0F, new CubeDeformation(-2.01F)), PartPose.offset(-0.0757F, -5.8261F, 9.3599F));

        PartDefinition GNParticles_4_r1 = GNParticles_4.addOrReplaceChild("GNParticles_4_r1", CubeListBuilder.create().texOffs(46, 26).addBox(-3.0F, -3.0F, -2.5F, 6.0F, 6.0F, 7.0F, new CubeDeformation(-2.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 0.0F, 0.0F, 0.7854F));

        PartDefinition GNParticles_3 = Body.addOrReplaceChild("GNParticles_3", CubeListBuilder.create().texOffs(146, 146).addBox(-3.0F, -3.0F, -2.8333F, 6.0F, 6.0F, 5.0F, new CubeDeformation(-1.41F)), PartPose.offset(-0.0757F, -5.8261F, 8.6933F));

        PartDefinition GNParticles_3_r1 = GNParticles_3.addOrReplaceChild("GNParticles_3_r1", CubeListBuilder.create().texOffs(124, 146).addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 5.0F, new CubeDeformation(-1.4F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.1667F, 0.0F, 0.0F, 0.7854F));

        PartDefinition GNParticles_2 = Body.addOrReplaceChild("GNParticles_2", CubeListBuilder.create().texOffs(144, 165).addBox(-3.0F, -3.0F, -2.0111F, 6.0F, 6.0F, 3.0F, new CubeDeformation(-0.81F)), PartPose.offset(-0.0757F, -5.8261F, 7.6711F));

        PartDefinition GNParticles_2_r1 = GNParticles_2.addOrReplaceChild("GNParticles_2_r1", CubeListBuilder.create().texOffs(54, 165).addBox(-3.0F, -3.0F, -2.0F, 6.0F, 6.0F, 3.0F, new CubeDeformation(-0.8F)), PartPose.offsetAndRotation(0.0F, 0.0F, -0.0111F, 0.0F, 0.0F, 0.7854F));

        PartDefinition GNParticles_1 = Body.addOrReplaceChild("GNParticles_1", CubeListBuilder.create().texOffs(34, 176).addBox(-3.0F, -3.0F, -1.6704F, 6.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.0757F, -5.8261F, 6.3303F));

        PartDefinition GNParticles_1_r1 = GNParticles_1.addOrReplaceChild("GNParticles_1_r1", CubeListBuilder.create().texOffs(68, 175).addBox(-3.0F, -3.0F, -1.0F, 6.0F, 6.0F, 2.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(0.0F, 0.0F, -0.6704F, 0.0F, 0.0F, 0.7854F));

        PartDefinition ArmLeft = Root.addOrReplaceChild("ArmLeft", CubeListBuilder.create().texOffs(120, 95).addBox(-6.0632F, -4.9611F, -2.9981F, 7.0F, 5.0F, 6.0F, new CubeDeformation(0.002F))
                .texOffs(152, 19).addBox(-0.0632F, -4.9611F, -2.9981F, 5.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(15.0827F, -9.9696F, -0.0433F));

        PartDefinition ArmLeft2_r1 = ArmLeft.addOrReplaceChild("ArmLeft2_r1", CubeListBuilder.create().texOffs(104, 58).addBox(-2.0F, -1.0F, -3.0F, 4.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(104, 197).addBox(-2.0F, -1.0F, 3.0F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(94, 197).addBox(-2.0F, -1.0F, -4.0F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.2067F, -5.063F, 0.0019F, 0.0F, 0.0F, -0.0873F));

        PartDefinition ArmLeft2_r2 = ArmLeft.addOrReplaceChild("ArmLeft2_r2", CubeListBuilder.create().texOffs(198, 89).addBox(-1.9767F, -2.0195F, 3.0F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(198, 85).addBox(-1.9767F, -2.0195F, -4.0F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.9067F, -0.063F, 0.0019F, 0.0F, 0.0F, 0.1745F));

        PartDefinition ArmLeft2_r3 = ArmLeft.addOrReplaceChild("ArmLeft2_r3", CubeListBuilder.create().texOffs(182, 142).addBox(-3.5F, -2.5F, -0.5F, 7.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.4219F, -4.3489F, 3.5019F, 0.0F, 0.0F, 0.0873F));

        PartDefinition ArmLeft2_r4 = ArmLeft.addOrReplaceChild("ArmLeft2_r4", CubeListBuilder.create().texOffs(64, 183).addBox(-3.5F, -2.5F, -0.5F, 7.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.3967F, -4.338F, -3.4981F, 0.0F, 0.0F, 0.0873F));

        PartDefinition ArmLeft2_r5 = ArmLeft.addOrReplaceChild("ArmLeft2_r5", CubeListBuilder.create().texOffs(90, 79).addBox(-5.8F, -1.0F, -3.0F, 13.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.8714F, -6.6421F, 0.0019F, 0.0F, 0.0F, -0.1745F));

        PartDefinition ArmLeft2_r6 = ArmLeft.addOrReplaceChild("ArmLeft2_r6", CubeListBuilder.create().texOffs(150, 107).addBox(-3.4767F, -1.7045F, -3.0F, 7.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.2357F, -4.6173F, 0.0019F, 0.0F, 0.0F, -0.1745F));

        PartDefinition ArmLeft2_r7 = ArmLeft.addOrReplaceChild("ArmLeft2_r7", CubeListBuilder.create().texOffs(144, 157).addBox(-2.0F, -1.0F, -3.0F, 4.0F, 2.0F, 6.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(0.0659F, 5.6775F, 0.0019F, 0.0F, 0.0F, -0.3491F));

        PartDefinition ArmLeft2_r8 = ArmLeft.addOrReplaceChild("ArmLeft2_r8", CubeListBuilder.create().texOffs(52, 174).addBox(-3.4F, -1.5F, -3.0F, 2.0F, 3.0F, 6.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(0.2948F, 6.1473F, 0.0019F, 0.0F, 0.0F, -0.3491F));

        PartDefinition ArmLeft2_r9 = ArmLeft.addOrReplaceChild("ArmLeft2_r9", CubeListBuilder.create().texOffs(174, 19).addBox(1.4F, -1.5F, -3.0F, 2.0F, 3.0F, 6.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(0.1948F, 6.1473F, 0.0019F, 0.0F, 0.0F, -0.3491F));

        PartDefinition ArmLeft2_r10 = ArmLeft.addOrReplaceChild("ArmLeft2_r10", CubeListBuilder.create().texOffs(192, 120).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.9123F, 1.1746F, 0.0019F, 0.0F, 0.0F, -0.3491F));

        PartDefinition ArmLeft2_r11 = ArmLeft.addOrReplaceChild("ArmLeft2_r11", CubeListBuilder.create().texOffs(178, 194).addBox(-2.0F, -2.0F, -0.5F, 4.0F, 4.0F, 1.0F, new CubeDeformation(-0.1F))
                .texOffs(194, 173).addBox(-2.0F, -2.0F, -7.3F, 4.0F, 4.0F, 1.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(-0.6107F, 3.1403F, 3.4019F, 0.0F, 0.0F, -0.3491F));

        PartDefinition ArmLeft2_r12 = ArmLeft.addOrReplaceChild("ArmLeft2_r12", CubeListBuilder.create().texOffs(46, 147).addBox(-2.0F, -3.0F, -3.0F, 4.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.8687F, 2.2006F, 0.0019F, 0.0F, 0.0F, -0.3491F));

        PartDefinition ForearmLeft = ArmLeft.addOrReplaceChild("ForearmLeft", CubeListBuilder.create(), PartPose.offset(0.9747F, 7.8811F, -0.0193F));

        PartDefinition ForearmLeft_r1 = ForearmLeft.addOrReplaceChild("ForearmLeft_r1", CubeListBuilder.create().texOffs(80, 143).addBox(-3.0F, -2.0F, -3.0F, 6.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.6449F, 10.0124F, 0.0212F, 0.0F, 0.0F, -0.3491F));

        PartDefinition ForearmLeft_r2 = ForearmLeft.addOrReplaceChild("ForearmLeft_r2", CubeListBuilder.create().texOffs(22, 140).addBox(-2.0F, -1.5F, -4.0F, 4.0F, 3.0F, 8.0F, new CubeDeformation(-0.4F)), PartPose.offsetAndRotation(2.5999F, 7.6632F, 0.0212F, 0.0F, 0.0F, -0.3491F));

        PartDefinition ForearmLeft_r3 = ForearmLeft.addOrReplaceChild("ForearmLeft_r3", CubeListBuilder.create().texOffs(114, 126).addBox(-2.0F, -6.0F, -3.0F, 4.0F, 11.0F, 6.0F, new CubeDeformation(-0.4F)), PartPose.offsetAndRotation(0.781F, 2.4949F, 0.0212F, 0.0F, 0.0F, -0.3491F));

        PartDefinition ForearmLeft_r4 = ForearmLeft.addOrReplaceChild("ForearmLeft_r4", CubeListBuilder.create().texOffs(50, 115).addBox(1.2F, -5.0F, -4.0F, 3.0F, 10.0F, 8.0F, new CubeDeformation(-0.4F)), PartPose.offsetAndRotation(1.397F, 4.3742F, 0.0212F, 0.0F, 0.0F, -0.3491F));

        PartDefinition ForearmLeft_r5 = ForearmLeft.addOrReplaceChild("ForearmLeft_r5", CubeListBuilder.create().texOffs(108, 108).addBox(-4.2F, -5.0F, -4.0F, 3.0F, 10.0F, 8.0F, new CubeDeformation(-0.4F)), PartPose.offsetAndRotation(1.497F, 4.3742F, 0.0212F, 0.0F, 0.0F, -0.3491F));

        PartDefinition ForearmLeft_r6 = ForearmLeft.addOrReplaceChild("ForearmLeft_r6", CubeListBuilder.create().texOffs(192, 61).addBox(-2.0F, -1.5F, -1.0F, 4.0F, 3.0F, 2.0F, new CubeDeformation(-0.4F))
                .texOffs(192, 56).addBox(-2.0F, -1.5F, -7.0F, 4.0F, 3.0F, 2.0F, new CubeDeformation(-0.4F)), PartPose.offsetAndRotation(0.91F, 2.9647F, 3.0212F, 0.0F, 0.0F, -0.3491F));

        PartDefinition Shield = ForearmLeft.addOrReplaceChild("Shield", CubeListBuilder.create(), PartPose.offset(2.5086F, 7.5035F, -0.0143F));

        PartDefinition Shield_r1 = Shield.addOrReplaceChild("Shield_r1", CubeListBuilder.create().texOffs(168, 143).addBox(-1.5F, -4.0F, -2.0F, 3.0F, 8.0F, 4.0F, new CubeDeformation(-0.4F))
                .texOffs(159, 137).addBox(-4.5F, -3.0F, -5.2F, 6.0F, 2.0F, 10.0F, new CubeDeformation(-0.1F))
                .texOffs(56, 67).addBox(0.7F, -14.4F, -7.0F, 3.0F, 9.0F, 14.0F, new CubeDeformation(-1.0F))
                .texOffs(72, 126).addBox(0.7F, 26.5F, -4.0F, 3.0F, 8.0F, 8.0F, new CubeDeformation(-1.4F))
                .texOffs(0, 82).addBox(0.7F, 19.1F, -6.0F, 3.0F, 10.0F, 12.0F, new CubeDeformation(-1.2F))
                .texOffs(46, 0).addBox(0.7F, 11.6F, -8.0F, 3.0F, 10.0F, 16.0F, new CubeDeformation(-1.0F))
                .texOffs(0, 0).addBox(0.7F, -7.0F, -10.0F, 3.0F, 20.0F, 20.0F, new CubeDeformation(-0.4F)), PartPose.offsetAndRotation(4.5579F, -1.9861F, 0.0355F, 0.0F, 0.0F, -0.3491F));

        PartDefinition Shield_r2 = Shield.addOrReplaceChild("Shield_r2", CubeListBuilder.create().texOffs(152, 69).addBox(-1.5F, 1.6F, 9.7F, 3.0F, 10.0F, 5.0F, new CubeDeformation(-1.01F)), PartPose.offsetAndRotation(3.2392F, -12.0415F, -7.4645F, 0.4363F, 0.0F, -0.3491F));

        PartDefinition Shield_r3 = Shield.addOrReplaceChild("Shield_r3", CubeListBuilder.create().texOffs(20, 151).addBox(-1.5F, -4.8F, -1.1F, 3.0F, 10.0F, 5.0F, new CubeDeformation(-1.01F)), PartPose.offsetAndRotation(3.2392F, -12.0415F, -7.4645F, -0.4363F, 0.0F, -0.3491F));

        PartDefinition Shield_r4 = Shield.addOrReplaceChild("Shield_r4", CubeListBuilder.create().texOffs(156, 114).addBox(-1.5F, -1.1F, -1.2F, 3.0F, 6.0F, 6.0F, new CubeDeformation(-1.401F)), PartPose.offsetAndRotation(17.0568F, 25.9221F, 0.0355F, -0.7854F, 0.0F, -0.3491F));

        PartDefinition HandLeft = ForearmLeft.addOrReplaceChild("HandLeft", CubeListBuilder.create(), PartPose.offsetAndRotation(3.9226F, 12.118F, -0.1338F, 0.1745F, 0.3491F, 0.0F));

        PartDefinition HandLeft_r1 = HandLeft.addOrReplaceChild("HandLeft_r1", CubeListBuilder.create().texOffs(0, 186).addBox(-1.0F, -2.5F, -2.5F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.8058F, 1.8909F, 0.4801F, 0.0F, -0.6981F, -0.3491F));

        PartDefinition HandLeft_r2 = HandLeft.addOrReplaceChild("HandLeft_r2", CubeListBuilder.create().texOffs(114, 184).addBox(-1.0F, -1.0F, -2.5F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.9768F, 2.3607F, 0.4801F, 0.0F, -0.6981F, -0.3491F));

        PartDefinition HandLeft_r3 = HandLeft.addOrReplaceChild("HandLeft_r3", CubeListBuilder.create().texOffs(94, 201).addBox(-1.0F, -0.6907F, -0.6502F, 2.0F, 3.0F, 1.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(1.5855F, -0.471F, -1.5617F, -0.7995F, -0.3119F, 0.2F));

        PartDefinition HandLeft_r4 = HandLeft.addOrReplaceChild("HandLeft_r4", CubeListBuilder.create().texOffs(80, 201).addBox(-1.6F, -1.6F, -0.3F, 2.0F, 3.0F, 1.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(1.6453F, 1.824F, -3.5844F, -0.6176F, -0.0094F, 0.6888F));

        PartDefinition HandLeft_r5 = HandLeft.addOrReplaceChild("HandLeft_r5", CubeListBuilder.create().texOffs(6, 202).addBox(-1.0F, -0.5F, -0.5F, 2.0F, 3.0F, 1.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(0.0276F, 3.8561F, 2.0888F, -0.6963F, -0.0561F, 1.1548F));

        PartDefinition HandLeft_r6 = HandLeft.addOrReplaceChild("HandLeft_r6", CubeListBuilder.create().texOffs(0, 202).addBox(-1.0F, -0.5F, -0.5F, 2.0F, 3.0F, 1.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(0.9336F, 3.5263F, 0.9397F, -0.6963F, -0.0561F, 1.1548F));

        PartDefinition HandLeft_r7 = HandLeft.addOrReplaceChild("HandLeft_r7", CubeListBuilder.create().texOffs(170, 201).addBox(-1.0F, -0.5F, -0.5F, 2.0F, 3.0F, 1.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(1.8396F, 3.1965F, -0.2094F, -0.6963F, -0.0561F, 1.1548F));

        PartDefinition HandLeft_r8 = HandLeft.addOrReplaceChild("HandLeft_r8", CubeListBuilder.create().texOffs(202, 22).addBox(-2.5F, 2.6F, -0.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(0.9757F, 6.5866F, 2.0527F, -0.6398F, 0.3013F, 1.6012F));

        PartDefinition HandLeft_r9 = HandLeft.addOrReplaceChild("HandLeft_r9", CubeListBuilder.create().texOffs(22, 123).addBox(-2.5F, 2.6F, -0.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(1.8817F, 6.2568F, 0.9036F, -0.6398F, 0.3013F, 1.6012F));

        PartDefinition HandLeft_r10 = HandLeft.addOrReplaceChild("HandLeft_r10", CubeListBuilder.create().texOffs(202, 19).addBox(-2.5F, 2.6F, -0.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(2.7877F, 5.9271F, -0.2454F, -0.6398F, 0.3013F, 1.6012F));

        PartDefinition HandLeft_r11 = HandLeft.addOrReplaceChild("HandLeft_r11", CubeListBuilder.create().texOffs(202, 34).addBox(-1.3F, -0.8F, 1.65F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.3F))
                .texOffs(202, 31).addBox(-1.3F, -0.8F, 0.15F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.3F))
                .texOffs(202, 28).addBox(-1.3F, -0.8F, -1.35F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.3F))
                .texOffs(202, 25).addBox(-1.3F, -0.8F, -2.65F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(-1.0166F, 3.4255F, -1.4176F, 0.1091F, 0.691F, 2.9627F));

        PartDefinition HandLeft_r12 = HandLeft.addOrReplaceChild("HandLeft_r12", CubeListBuilder.create().texOffs(202, 16).addBox(-2.5F, 2.6F, -0.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(3.573F, 5.6412F, -1.2413F, -0.6398F, 0.3013F, 1.6012F));

        PartDefinition HandLeft_r13 = HandLeft.addOrReplaceChild("HandLeft_r13", CubeListBuilder.create().texOffs(124, 201).addBox(-1.0F, -0.5F, -0.5F, 2.0F, 3.0F, 1.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(2.6248F, 2.9107F, -1.2052F, -0.6963F, -0.0561F, 1.1548F));

        PartDefinition ArmRight = Root.addOrReplaceChild("ArmRight", CubeListBuilder.create().texOffs(106, 34).addBox(-0.9564F, -5.5927F, -3.1434F, 8.0F, 5.0F, 6.0F, new CubeDeformation(0.002F))
                .texOffs(152, 10).addBox(-4.5564F, -5.4927F, -3.1434F, 5.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-15.424F, -9.438F, 0.102F));

        PartDefinition ArmRight_r1 = ArmRight.addOrReplaceChild("ArmRight_r1", CubeListBuilder.create().texOffs(124, 197).addBox(-2.0F, -1.5F, -0.5F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(114, 197).addBox(-2.0F, -1.5F, -7.5F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(2.9564F, -1.0965F, 3.3566F, 0.0F, 0.0F, -0.1745F));

        PartDefinition ArmRight_r2 = ArmRight.addOrReplaceChild("ArmRight_r2", CubeListBuilder.create().texOffs(182, 0).addBox(-5.0F, -1.5F, -0.5F, 7.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.8301F, -5.9079F, 3.3566F, 0.0F, 0.0F, -0.0873F));

        PartDefinition ArmRight_r3 = ArmRight.addOrReplaceChild("ArmRight_r3", CubeListBuilder.create().texOffs(116, 13).addBox(-5.0F, -1.5F, -0.5F, 7.0F, 5.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.8301F, -6.0079F, -3.6434F, 0.0F, 0.0F, -0.0873F));

        PartDefinition ArmRight_r4 = ArmRight.addOrReplaceChild("ArmRight_r4", CubeListBuilder.create().texOffs(76, 197).addBox(-2.0F, -1.5F, -0.5F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(66, 197).addBox(-2.0F, -1.5F, 6.5F, 4.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.7564F, -5.0965F, -3.6434F, 0.0F, 0.0F, 0.0873F));

        PartDefinition ArmRight_r5 = ArmRight.addOrReplaceChild("ArmRight_r5", CubeListBuilder.create().texOffs(90, 87).addBox(-6.0F, -2.5F, -3.0F, 13.0F, 2.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(150, 100).addBox(-5.0F, -0.5F, -3.0F, 7.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.6564F, -6.0927F, -0.1434F, 0.0F, 0.0F, 0.1745F));

        PartDefinition ArmRight_r6 = ArmRight.addOrReplaceChild("ArmRight_r6", CubeListBuilder.create().texOffs(84, 19).addBox(-2.0F, -0.5F, -3.0F, 4.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(3.8436F, -6.0927F, -0.1434F, 0.0F, 0.0F, 0.0873F));

        PartDefinition ArmRight_r7 = ArmRight.addOrReplaceChild("ArmRight_r7", CubeListBuilder.create().texOffs(192, 135).addBox(-2.0F, -2.0F, -0.5F, 4.0F, 4.0F, 1.0F, new CubeDeformation(-0.1F))
                .texOffs(192, 105).addBox(-2.0F, -2.0F, -7.4F, 4.0F, 4.0F, 1.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(0.496F, 2.6087F, 3.2566F, 0.0F, 0.0F, 0.3491F));

        PartDefinition ArmRight_r8 = ArmRight.addOrReplaceChild("ArmRight_r8", CubeListBuilder.create().texOffs(0, 148).addBox(-2.0F, -3.0F, -3.0F, 4.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.838F, 1.669F, -0.1434F, 0.0F, 0.0F, 0.3491F));

        PartDefinition ArmRight_r9 = ArmRight.addOrReplaceChild("ArmRight_r9", CubeListBuilder.create().texOffs(156, 126).addBox(-2.0F, -1.0F, -3.0F, 4.0F, 2.0F, 6.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(-0.4274F, 5.1459F, -0.1434F, 0.0F, 0.0F, 0.3491F));

        PartDefinition ArmRight_r10 = ArmRight.addOrReplaceChild("ArmRight_r10", CubeListBuilder.create().texOffs(172, 45).addBox(-1.0F, -1.5F, -3.0F, 2.0F, 3.0F, 6.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(-2.8537F, 4.7949F, -0.1434F, 0.0F, 0.0F, 0.3491F));

        PartDefinition ArmRight_r11 = ArmRight.addOrReplaceChild("ArmRight_r11", CubeListBuilder.create().texOffs(174, 10).addBox(-1.0F, -1.5F, -3.0F, 2.0F, 3.0F, 6.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(1.6568F, 6.4366F, -0.1434F, 0.0F, 0.0F, 0.3491F));

        PartDefinition ArmRight_r12 = ArmRight.addOrReplaceChild("ArmRight_r12", CubeListBuilder.create().texOffs(34, 191).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 6.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.981F, 0.643F, -0.1434F, 0.0F, 0.0F, 0.3491F));

        PartDefinition ForearmRight = ArmRight.addOrReplaceChild("ForearmRight", CubeListBuilder.create(), PartPose.offset(-1.661F, 6.8415F, -0.1756F));

        PartDefinition ForearmRight_r1 = ForearmRight.addOrReplaceChild("ForearmRight_r1", CubeListBuilder.create().texOffs(192, 51).addBox(-2.0F, -1.5F, -1.0F, 4.0F, 3.0F, 2.0F, new CubeDeformation(-0.4F))
                .texOffs(190, 23).addBox(-2.0F, -1.5F, -7.0F, 4.0F, 3.0F, 2.0F, new CubeDeformation(-0.4F)), PartPose.offsetAndRotation(-0.6475F, 3.4727F, 3.0322F, 0.0F, 0.0F, 0.3491F));

        PartDefinition ForearmRight_r2 = ForearmRight.addOrReplaceChild("ForearmRight_r2", CubeListBuilder.create().texOffs(168, 67).addBox(-1.5F, -4.0F, -2.0F, 3.0F, 8.0F, 4.0F, new CubeDeformation(-0.4F)), PartPose.offsetAndRotation(-6.7911F, 6.0254F, 0.0322F, 0.0F, 0.0F, 0.3491F));

        PartDefinition ForearmRight_r3 = ForearmRight.addOrReplaceChild("ForearmRight_r3", CubeListBuilder.create().texOffs(94, 126).addBox(-2.0F, -6.0F, -3.0F, 4.0F, 11.0F, 6.0F, new CubeDeformation(-0.4F)), PartPose.offsetAndRotation(-0.4765F, 3.0029F, 0.0322F, 0.0F, 0.0F, 0.3491F));

        PartDefinition ForearmRight_r4 = ForearmRight.addOrReplaceChild("ForearmRight_r4", CubeListBuilder.create().texOffs(28, 108).addBox(-1.5F, -5.0F, -4.0F, 3.0F, 10.0F, 8.0F, new CubeDeformation(-0.4F)), PartPose.offsetAndRotation(-3.6977F, 3.9588F, 0.0322F, 0.0F, 0.0F, 0.3491F));

        PartDefinition ForearmRight_r5 = ForearmRight.addOrReplaceChild("ForearmRight_r5", CubeListBuilder.create().texOffs(86, 108).addBox(-1.5F, -5.0F, -4.0F, 3.0F, 10.0F, 8.0F, new CubeDeformation(-0.4F)), PartPose.offsetAndRotation(1.3766F, 5.8057F, 0.0322F, 0.0F, 0.0F, 0.3491F));

        PartDefinition ForearmRight_r6 = ForearmRight.addOrReplaceChild("ForearmRight_r6", CubeListBuilder.create().texOffs(134, 135).addBox(-2.0F, -1.5F, -4.0F, 4.0F, 3.0F, 8.0F, new CubeDeformation(-0.4F)), PartPose.offsetAndRotation(-2.3576F, 8.1712F, 0.0322F, 0.0F, 0.0F, 0.3491F));

        PartDefinition ForearmRight_r7 = ForearmRight.addOrReplaceChild("ForearmRight_r7", CubeListBuilder.create().texOffs(142, 0).addBox(-3.0F, -2.0F, -3.0F, 6.0F, 4.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-3.2127F, 10.5204F, 0.0322F, 0.0F, 0.0F, 0.3491F));

        PartDefinition Sword_Hand = ForearmRight.addOrReplaceChild("Sword_Hand", CubeListBuilder.create(), PartPose.offset(-6.9301F, 17.8716F, -1.4474F));

        PartDefinition Sword_Hand_r1 = Sword_Hand.addOrReplaceChild("Sword_Hand_r1", CubeListBuilder.create().texOffs(192, 114).addBox(3.2F, 11.2F, -4.8F, 3.0F, 4.0F, 2.0F, new CubeDeformation(-0.1F))
                .texOffs(146, 183).addBox(3.8F, 8.6F, -7.6F, 2.0F, 3.0F, 5.0F, new CubeDeformation(-0.1F))
                .texOffs(0, 104).addBox(3.8F, 7.9F, -6.6F, 2.0F, 3.0F, 12.0F, new CubeDeformation(-0.2F))
                .texOffs(166, 94).addBox(3.2F, 11.2F, -7.8F, 3.0F, 4.0F, 2.0F, new CubeDeformation(-0.1F))
                .texOffs(148, 174).addBox(5.2F, 14.0F, -2.2F, 1.0F, 2.0F, 7.0F, new CubeDeformation(-0.1F))
                .texOffs(68, 142).addBox(2.5F, -1.0F, 4.5F, 4.0F, 21.0F, 2.0F, new CubeDeformation(-0.1F))
                .texOffs(132, 174).addBox(3.2F, 14.0F, -2.2F, 1.0F, 2.0F, 7.0F, new CubeDeformation(-0.1F))
                .texOffs(134, 80).addBox(3.7F, 16.2F, -7.0F, 2.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(22, 126).addBox(3.7F, 14.8F, -7.0F, 2.0F, 4.0F, 10.0F, new CubeDeformation(-0.11F))
                .texOffs(172, 28).addBox(3.2F, 15.0F, -7.0F, 3.0F, 3.0F, 5.0F, new CubeDeformation(-0.1F))
                .texOffs(176, 129).addBox(-2.5F, 15.0F, -6.0F, 6.0F, 3.0F, 3.0F, new CubeDeformation(-0.2F))
                .texOffs(32, 40).addBox(-2.5F, 8.0F, -9.0F, 2.0F, 9.0F, 18.0F, new CubeDeformation(0.001F))
                .texOffs(0, 40).addBox(-2.5F, -7.0F, -7.0F, 2.0F, 28.0F, 14.0F, new CubeDeformation(0.0F))
                .texOffs(104, 46).addBox(-1.5F, -1.0F, -4.0F, 5.0F, 2.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.3277F, -13.4428F, 1.4796F, 0.0F, 0.0F, 0.3491F));

        PartDefinition Sword_Blade = Sword_Hand.addOrReplaceChild("Sword_Blade", CubeListBuilder.create(), PartPose.offset(-1.3529F, 7.8153F, -0.5147F));

        PartDefinition Sword_Blade_r1 = Sword_Blade.addOrReplaceChild("Sword_Blade_r1", CubeListBuilder.create().texOffs(128, 74).addBox(-1.0F, -1.5F, -5.0F, 2.0F, 3.0F, 10.0F, new CubeDeformation(-0.4F)), PartPose.offsetAndRotation(0.3593F, -1.1387F, -0.0056F, 0.0F, 0.0F, 0.3491F));

        PartDefinition Sword_Blade_r2 = Sword_Blade.addOrReplaceChild("Sword_Blade_r2", CubeListBuilder.create().texOffs(128, 157).addBox(-1.0F, 1.5009F, 1.196F, 2.0F, 7.0F, 5.9F, new CubeDeformation(-0.9F)), PartPose.offsetAndRotation(-8.2307F, 22.4622F, 0.0194F, -0.5236F, 0.0F, 0.3491F));

        PartDefinition Sword_Blade_r3 = Sword_Blade.addOrReplaceChild("Sword_Blade_r3", CubeListBuilder.create().texOffs(64, 189).addBox(-1.0F, 4.6804F, -8.6649F, 2.0F, 4.0F, 4.0F, new CubeDeformation(-0.92F)), PartPose.offsetAndRotation(-8.2307F, 22.4622F, 0.0194F, 0.7854F, 0.0F, 0.3491F));

        PartDefinition Sword_Blade_r4 = Sword_Blade.addOrReplaceChild("Sword_Blade_r4", CubeListBuilder.create().texOffs(112, 157).addBox(-1.0F, 1.2759F, -7.0393F, 2.0F, 7.0F, 5.9F, new CubeDeformation(-0.91F)), PartPose.offsetAndRotation(-8.2307F, 22.4622F, 0.0194F, 0.5236F, 0.0F, 0.3491F));

        PartDefinition Sword_Blade_r5 = Sword_Blade.addOrReplaceChild("Sword_Blade_r5", CubeListBuilder.create().texOffs(32, 67).addBox(3.7F, 19.8F, -7.0F, 2.0F, 31.0F, 10.0F, new CubeDeformation(-0.8F)), PartPose.offsetAndRotation(2.6805F, -21.2581F, 1.9944F, 0.0F, 0.0F, 0.3491F));

        PartDefinition HandRight = ForearmRight.addOrReplaceChild("HandRight", CubeListBuilder.create(), PartPose.offsetAndRotation(-3.9082F, 15.4134F, 0.3399F, 0.3491F, -0.6981F, 0.0F));

        PartDefinition HandRight_r1 = HandRight.addOrReplaceChild("HandRight_r1", CubeListBuilder.create().texOffs(132, 183).addBox(-1.0F, -3.5F, -2.5F, 2.0F, 3.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(100, 184).addBox(-1.0F, -0.5F, -2.5F, 2.0F, 2.0F, 5.0F, new CubeDeformation(0.001F)), PartPose.offsetAndRotation(-1.6154F, -0.7352F, 1.9085F, 0.0F, 0.6981F, 0.3491F));

        PartDefinition HandRight_r2 = HandRight.addOrReplaceChild("HandRight_r2", CubeListBuilder.create().texOffs(200, 124).addBox(-0.4F, -1.3F, -2.1F, 2.0F, 3.0F, 1.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(-1.1664F, -1.6137F, -2.1381F, 0.0677F, -0.9397F, 0.0134F));

        PartDefinition HandRight_r3 = HandRight.addOrReplaceChild("HandRight_r3", CubeListBuilder.create().texOffs(74, 201).addBox(-2.4576F, -0.9475F, -0.5497F, 2.0F, 3.0F, 1.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(-1.7517F, 0.4556F, -1.0542F, -0.145F, -0.7275F, -2.5697F));

        PartDefinition HandRight_r4 = HandRight.addOrReplaceChild("HandRight_r4", CubeListBuilder.create().texOffs(202, 13).addBox(-1.0F, -1.0F, -0.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(0.026F, -0.7748F, -2.1779F, 0.0981F, -0.7257F, -2.8777F));

        PartDefinition HandRight_r5 = HandRight.addOrReplaceChild("HandRight_r5", CubeListBuilder.create().texOffs(12, 202).addBox(-1.0F, -1.0F, -0.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(0.7006F, -0.5276F, -1.0761F, 0.0981F, -0.7257F, -2.8777F));

        PartDefinition HandRight_r6 = HandRight.addOrReplaceChild("HandRight_r6", CubeListBuilder.create().texOffs(202, 10).addBox(-1.0F, -1.0F, -0.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(1.6828F, -0.1166F, 0.1182F, 0.0981F, -0.7257F, -2.8777F));

        PartDefinition HandRight_r7 = HandRight.addOrReplaceChild("HandRight_r7", CubeListBuilder.create().texOffs(200, 178).addBox(-1.0F, -1.0F, -0.5F, 2.0F, 2.0F, 1.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(2.5969F, 0.177F, 1.2772F, 0.0981F, -0.7257F, -2.8777F));

        PartDefinition HandRight_r8 = HandRight.addOrReplaceChild("HandRight_r8", CubeListBuilder.create().texOffs(56, 201).addBox(-2.4762F, -0.8074F, -0.5479F, 2.0F, 3.0F, 1.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(0.8292F, 1.4135F, 2.3797F, -0.145F, -0.7275F, -2.5697F));

        PartDefinition HandRight_r9 = HandRight.addOrReplaceChild("HandRight_r9", CubeListBuilder.create().texOffs(62, 201).addBox(-2.4576F, -0.9475F, -0.5497F, 2.0F, 3.0F, 1.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(-0.0768F, 1.0837F, 1.2306F, -0.145F, -0.7275F, -2.5697F));

        PartDefinition HandRight_r10 = HandRight.addOrReplaceChild("HandRight_r10", CubeListBuilder.create().texOffs(68, 201).addBox(-2.4576F, -0.9475F, -0.5497F, 2.0F, 3.0F, 1.0F, new CubeDeformation(-0.25F)), PartPose.offsetAndRotation(-1.0433F, 0.7319F, 0.005F, -0.145F, -0.7275F, -2.5697F));

        PartDefinition HandRight_r11 = HandRight.addOrReplaceChild("HandRight_r11", CubeListBuilder.create().texOffs(50, 108).addBox(-1.0F, -2.0F, -0.5F, 2.0F, 5.0F, 1.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(-1.7607F, -2.9065F, -0.8583F, -1.362F, -0.8926F, 0.0307F));

        PartDefinition HandRight_r12 = HandRight.addOrReplaceChild("HandRight_r12", CubeListBuilder.create().texOffs(118, 201).addBox(-0.6175F, 0.0389F, -0.4667F, 2.0F, 3.0F, 1.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(-2.7062F, 0.5705F, 1.2191F, -0.6906F, 0.1119F, -1.0875F));

        PartDefinition HandRight_r13 = HandRight.addOrReplaceChild("HandRight_r13", CubeListBuilder.create().texOffs(112, 201).addBox(-0.6175F, 0.0389F, -0.4668F, 2.0F, 3.0F, 1.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(-1.7398F, 0.9222F, 2.4448F, -0.6906F, 0.1119F, -1.0875F));

        PartDefinition HandRight_r14 = HandRight.addOrReplaceChild("HandRight_r14", CubeListBuilder.create().texOffs(100, 201).addBox(-0.6175F, 0.0389F, -0.4667F, 2.0F, 3.0F, 1.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(-0.8338F, 1.252F, 3.5938F, -0.6906F, 0.1119F, -1.0875F));

        PartDefinition HandRight_r15 = HandRight.addOrReplaceChild("HandRight_r15", CubeListBuilder.create().texOffs(106, 201).addBox(-0.6175F, 0.0389F, -0.4667F, 2.0F, 3.0F, 1.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(-3.4465F, 0.2516F, 0.2299F, -0.6906F, 0.1119F, -1.0875F));

        PartDefinition LegLeft = Root.addOrReplaceChild("LegLeft", CubeListBuilder.create(), PartPose.offset(6.2369F, 3.2589F, 0.6363F));

        PartDefinition LegLeft_r1 = LegLeft.addOrReplaceChild("LegLeft_r1", CubeListBuilder.create().texOffs(84, 0).addBox(-3.5F, -5.0F, -4.5F, 7.0F, 10.0F, 9.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(1.5982F, 11.1718F, -0.2827F, 0.0F, -0.1745F, -0.1745F));

        PartDefinition LegLeft_r2 = LegLeft.addOrReplaceChild("LegLeft_r2", CubeListBuilder.create().texOffs(80, 185).addBox(-2.0F, 1.5F, -3.5F, 4.0F, 4.0F, 3.0F, new CubeDeformation(0.1F))
                .texOffs(196, 148).addBox(-1.0F, -5.5F, 3.3F, 2.0F, 7.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(42, 195).addBox(-1.0F, -5.5F, -4.7F, 2.0F, 7.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(46, 133).addBox(-2.0F, -5.5F, -3.5F, 4.0F, 7.0F, 7.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(3.0601F, 19.5317F, -0.2471F, 0.0F, -0.1745F, -0.1745F));

        PartDefinition LegLeft_r3 = LegLeft.addOrReplaceChild("LegLeft_r3", CubeListBuilder.create().texOffs(90, 170).addBox(-2.5F, -2.0F, -2.0F, 5.0F, 4.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(3.4089F, 23.0237F, 1.2301F, 0.0F, -0.1745F, -0.1745F));

        PartDefinition LegLeft_r4 = LegLeft.addOrReplaceChild("LegLeft_r4", CubeListBuilder.create().texOffs(132, 13).addBox(-3.0F, -5.5F, -2.5F, 5.0F, 11.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.7148F, 3.7064F, -0.1777F, 0.0F, -0.1745F, -0.1745F));

        PartDefinition CalfLeft = LegLeft.addOrReplaceChild("CalfLeft", CubeListBuilder.create(), PartPose.offset(5.1512F, 24.5636F, -1.1851F));

        PartDefinition CalfLeft_r1 = CalfLeft.addOrReplaceChild("CalfLeft_r1", CubeListBuilder.create().texOffs(192, 85).addBox(-1.0F, -4.5F, -0.5F, 2.0F, 9.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.917F, 4.9367F, 4.8772F, 0.0F, -0.1745F, -0.1745F));

        PartDefinition CalfLeft_r2 = CalfLeft.addOrReplaceChild("CalfLeft_r2", CubeListBuilder.create().texOffs(96, 156).addBox(-1.5F, 3.5F, -2.5F, 3.0F, 9.0F, 5.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(-0.1803F, 4.8465F, 0.938F, 0.0F, -0.1745F, -0.1745F));

        PartDefinition CalfLeft_r3 = CalfLeft.addOrReplaceChild("CalfLeft_r3", CubeListBuilder.create().texOffs(134, 197).addBox(-0.5F, -4.5F, -0.5F, 1.0F, 9.0F, 1.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(1.5885F, 12.6647F, -2.0316F, 0.0F, -0.1745F, -0.1745F));

        PartDefinition CalfLeft_r4 = CalfLeft.addOrReplaceChild("CalfLeft_r4", CubeListBuilder.create().texOffs(88, 192).addBox(-1.0F, -2.5F, -4.5F, 2.0F, 9.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(156, 28).addBox(-1.5F, -2.5F, -2.5F, 3.0F, 9.0F, 5.0F, new CubeDeformation(1.0F)), PartPose.offsetAndRotation(-0.5803F, 2.8465F, 0.938F, 0.0F, -0.1745F, -0.1745F));

        PartDefinition CalfLeft_r5 = CalfLeft.addOrReplaceChild("CalfLeft_r5", CubeListBuilder.create().texOffs(120, 170).addBox(0.0F, -0.4F, -3.8F, 1.0F, 9.0F, 5.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(1.1016F, 0.8433F, 2.2449F, 0.1745F, -0.1745F, -0.1745F));

        PartDefinition CalfLeft_r6 = CalfLeft.addOrReplaceChild("CalfLeft_r6", CubeListBuilder.create().texOffs(182, 80).addBox(-1.2F, 0.0F, -1.5F, 1.0F, 9.0F, 4.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(-2.5354F, 1.2251F, -1.5279F, 0.0F, -0.1745F, -0.1745F));

        PartDefinition CalfLeft_r7 = CalfLeft.addOrReplaceChild("CalfLeft_r7", CubeListBuilder.create().texOffs(182, 54).addBox(-0.9F, 0.0F, -1.5F, 1.0F, 9.0F, 4.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(-3.3898F, 1.4965F, 1.9569F, 0.0F, -0.1745F, -0.1745F));

        PartDefinition FootLeft = CalfLeft.addOrReplaceChild("FootLeft", CubeListBuilder.create(), PartPose.offset(2.8468F, 18.1539F, 1.5217F));

        PartDefinition FootLeft_r1 = FootLeft.addOrReplaceChild("FootLeft_r1", CubeListBuilder.create().texOffs(50, 191).addBox(-1.5F, 10.7F, -0.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.1F))
                .texOffs(166, 56).addBox(-1.5F, 10.6F, -2.5F, 3.0F, 6.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.727F, -11.3074F, -0.5837F, 0.0F, -0.1745F, -0.1745F));

        PartDefinition FootLeft_r2 = FootLeft.addOrReplaceChild("FootLeft_r2", CubeListBuilder.create().texOffs(174, 114).addBox(-2.5F, -1.0F, -1.5F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.3453F, 5.686F, 0.46F, 0.0F, -0.1745F, 0.0F));

        PartDefinition FootLeft_r3 = FootLeft.addOrReplaceChild("FootLeft_r3", CubeListBuilder.create().texOffs(188, 187).addBox(-2.5F, -1.5F, -1.5F, 4.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.7453F, 5.186F, -2.94F, 0.0F, -0.1745F, 0.0F));

        PartDefinition FootLeft_r4 = FootLeft.addOrReplaceChild("FootLeft_r4", CubeListBuilder.create().texOffs(94, 191).addBox(-1.5F, -1.5F, -1.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(0.153F, 3.5931F, -2.7411F, -0.7854F, -0.1745F, 0.0F));

        PartDefinition FootLeft_r5 = FootLeft.addOrReplaceChild("FootLeft_r5", CubeListBuilder.create().texOffs(118, 191).addBox(-2.0F, -1.5F, -1.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(0.8007F, 5.0257F, -3.4791F, -0.3491F, -0.1745F, 0.0F));

        PartDefinition FootLeft_r6 = FootLeft.addOrReplaceChild("FootLeft_r6", CubeListBuilder.create().texOffs(76, 192).addBox(-2.0F, -1.5F, -2.0F, 3.0F, 2.0F, 3.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(1.1571F, 6.186F, -5.2623F, 0.0F, -0.1745F, 0.0F));

        PartDefinition LegRight = Root.addOrReplaceChild("LegRight", CubeListBuilder.create(), PartPose.offset(-6.0223F, 3.2765F, 0.6354F));

        PartDefinition LegRight_r1 = LegRight.addOrReplaceChild("LegRight_r1", CubeListBuilder.create().texOffs(72, 46).addBox(-3.9F, 2.5F, 0.4F, 7.0F, 10.0F, 9.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(-0.6903F, 3.6887F, -5.1768F, 0.0F, 0.1745F, 0.1745F));

        PartDefinition LegRight_r2 = LegRight.addOrReplaceChild("LegRight_r2", CubeListBuilder.create().texOffs(0, 169).addBox(-2.5F, -2.0F, -2.0F, 5.0F, 4.0F, 4.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(-3.432F, 23.0061F, 1.231F, 0.0F, 0.1745F, 0.1745F));

        PartDefinition LegRight_r3 = LegRight.addOrReplaceChild("LegRight_r3", CubeListBuilder.create().texOffs(160, 184).addBox(-2.0F, -2.0F, -1.5F, 4.0F, 4.0F, 3.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(-4.0305F, 22.9006F, -2.2158F, 0.0F, 0.1745F, 0.1745F));

        PartDefinition LegRight_r4 = LegRight.addOrReplaceChild("LegRight_r4", CubeListBuilder.create().texOffs(134, 29).addBox(-2.0F, -5.5F, -3.5F, 4.0F, 7.0F, 7.0F, new CubeDeformation(0.1F))
                .texOffs(196, 156).addBox(-1.0F, -5.5F, 3.3F, 2.0F, 7.0F, 1.0F, new CubeDeformation(0.1F))
                .texOffs(150, 114).addBox(-1.0F, -5.5F, -4.7F, 2.0F, 7.0F, 1.0F, new CubeDeformation(0.1F)), PartPose.offsetAndRotation(-3.0807F, 19.514F, -0.2462F, 0.0F, 0.1745F, 0.1745F));

        PartDefinition LegRight_r5 = LegRight.addOrReplaceChild("LegRight_r5", CubeListBuilder.create().texOffs(130, 106).addBox(-2.0F, -5.5F, -2.5F, 5.0F, 11.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.6903F, 3.6887F, -0.1768F, 0.0F, 0.1745F, 0.1745F));

        PartDefinition CalfRight = LegRight.addOrReplaceChild("CalfRight", CubeListBuilder.create(), PartPose.offset(-5.1287F, 24.7109F, -1.2045F));

        PartDefinition CalfRight_r1 = CalfRight.addOrReplaceChild("CalfRight_r1", CubeListBuilder.create().texOffs(192, 95).addBox(-1.0F, -2.5F, 3.5F, 2.0F, 9.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(172, 191).addBox(-1.0F, -2.5F, -4.5F, 2.0F, 9.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(80, 153).addBox(-1.5F, -2.5F, -2.5F, 3.0F, 9.0F, 5.0F, new CubeDeformation(1.0F)), PartPose.offsetAndRotation(0.6588F, 2.6816F, 0.9583F, 0.0F, 0.1745F, 0.1745F));

        PartDefinition CalfRight_r2 = CalfRight.addOrReplaceChild("CalfRight_r2", CubeListBuilder.create().texOffs(182, 67).addBox(-0.1F, -0.5F, -2.5F, 1.0F, 9.0F, 4.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(2.8625F, 2.1072F, -0.6879F, 0.0F, 0.1745F, 0.1745F));

        PartDefinition CalfRight_r3 = CalfRight.addOrReplaceChild("CalfRight_r3", CubeListBuilder.create().texOffs(178, 181).addBox(0.0F, -0.3F, -1.1F, 1.0F, 9.0F, 4.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(3.2378F, 1.9706F, 1.5772F, 0.0F, 0.1745F, 0.1745F));

        PartDefinition CalfRight_r4 = CalfRight.addOrReplaceChild("CalfRight_r4", CubeListBuilder.create().texOffs(108, 170).addBox(-0.9F, -0.4F, -2.5F, 1.0F, 9.0F, 5.0F, new CubeDeformation(-0.3F)), PartPose.offsetAndRotation(-1.3589F, 1.124F, 1.3532F, 0.0873F, 0.1745F, 0.1745F));

        PartDefinition CalfRight_r5 = CalfRight.addOrReplaceChild("CalfRight_r5", CubeListBuilder.create().texOffs(30, 197).addBox(-0.5F, -4.5F, -0.5F, 1.0F, 9.0F, 1.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(-1.6724F, 12.4998F, -2.0114F, 0.0F, 0.1745F, 0.1745F));

        PartDefinition CalfRight_r6 = CalfRight.addOrReplaceChild("CalfRight_r6", CubeListBuilder.create().texOffs(156, 42).addBox(-1.5F, 3.5F, -2.5F, 3.0F, 9.0F, 5.0F, new CubeDeformation(0.2F)), PartPose.offsetAndRotation(0.1588F, 4.6816F, 0.9583F, 0.0F, 0.1745F, 0.1745F));

        PartDefinition FootRight = CalfRight.addOrReplaceChild("FootRight", CubeListBuilder.create(), PartPose.offset(-2.8907F, 17.9891F, 1.5426F));

        PartDefinition FootRight_r1 = FootRight.addOrReplaceChild("FootRight_r1", CubeListBuilder.create().texOffs(22, 191).addBox(-1.5F, 10.7F, -0.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(0.4F)), PartPose.offsetAndRotation(2.7495F, -11.3074F, -0.5844F, 0.0F, 0.1745F, 0.1745F));

        PartDefinition FootRight_r2 = FootRight.addOrReplaceChild("FootRight_r2", CubeListBuilder.create().texOffs(106, 191).addBox(-1.0F, -1.5F, -1.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(-0.8228F, 5.0257F, -3.4798F, -0.3491F, 0.1745F, 0.0F));

        PartDefinition FootRight_r3 = FootRight.addOrReplaceChild("FootRight_r3", CubeListBuilder.create().texOffs(130, 191).addBox(-1.5F, -0.5F, -1.5F, 3.0F, 3.0F, 3.0F, new CubeDeformation(-0.2F)), PartPose.offsetAndRotation(0.0218F, 2.886F, -2.0407F, -0.7854F, 0.1745F, 0.0F));

        PartDefinition FootRight_r4 = FootRight.addOrReplaceChild("FootRight_r4", CubeListBuilder.create().texOffs(192, 80).addBox(-1.1F, -1.5F, -1.5F, 3.0F, 2.0F, 3.0F, new CubeDeformation(-0.1F)), PartPose.offsetAndRotation(-1.1547F, 6.186F, -5.7728F, 0.0F, 0.1745F, 0.0F));

        PartDefinition FootRight_r5 = FootRight.addOrReplaceChild("FootRight_r5", CubeListBuilder.create().texOffs(188, 181).addBox(-1.5F, -2.0F, -1.5F, 4.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.7782F, 5.686F, -2.9407F, 0.0F, 0.1745F, 0.0F));

        PartDefinition FootRight_r6 = FootRight.addOrReplaceChild("FootRight_r6", CubeListBuilder.create().texOffs(172, 38).addBox(-1.5F, -1.0F, -1.5F, 4.0F, 2.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.3781F, 5.686F, 0.4593F, 0.0F, 0.1745F, 0.0F));

        PartDefinition FootRight_r7 = FootRight.addOrReplaceChild("FootRight_r7", CubeListBuilder.create().texOffs(18, 166).addBox(-1.5F, -3.0F, -2.5F, 3.0F, 6.0F, 5.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.3879F, 2.086F, -0.5844F, 0.0F, 0.1745F, 0.1745F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }


    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {

        this.root().getAllParts().forEach(ModelPart::resetPose);

        Gn001ClientAnimStates.States s = Gn001ClientAnimStates.get(entity.getUUID());

        updateAnimationFlags(entity, s);

        boolean hasSpecialAnimation = false;
        if (s.flySpin.isStarted()) {
            this.animate(s.flySpin, Gn001Animations.fly_spin, ageInTicks, 0.6667f);
            hasSpecialAnimation = true;
        } else if (s.useItemRight.isStarted()) {
            this.animate(s.useItemRight, Gn001Animations.use_item_right, ageInTicks, 1.0f);
            hasSpecialAnimation = true;
        } else if (s.attack1.isStarted()) {
            this.animate(s.attack1, Gn001Animations.attack_1, ageInTicks, 1.0f);
            hasSpecialAnimation = true;
        } else if (s.attack2.isStarted()) {
            this.animate(s.attack2, Gn001Animations.attack_2, ageInTicks, 1.0f);
            hasSpecialAnimation = true;
        } else if (s.attack3.isStarted()) {
            this.animate(s.attack3, Gn001Animations.attack_3, ageInTicks, 1.0f);
            hasSpecialAnimation = true;
        } else if (s.breath.isStarted()) {
            this.animate(s.breath, Gn001Animations.breath, ageInTicks, 1.0f);
            hasSpecialAnimation = true;
        } else if (s.flyFire.isStarted()) {
            this.animate(s.flyFire, Gn001Animations.fly_fire, ageInTicks, 1.0f);
            hasSpecialAnimation = true;
        } else if (s.flyKick.isStarted()) {
            this.animate(s.flyKick, Gn001Animations.fly_kick, ageInTicks, 1.0f);
            hasSpecialAnimation = true;
        } else if (s.castMassBuff.isStarted()) {
            this.animate(s.castMassBuff, Gn001Animations.cast_mass_buff, ageInTicks, 1.0f);
            hasSpecialAnimation = true;
        } else if (s.selfBuff.isStarted()) {
            this.animate(s.selfBuff, Gn001Animations.self_buff, ageInTicks, 1.0f);
            hasSpecialAnimation = true;
        } else if (s.flyLandEnd.isStarted()) {
            this.animate(s.flyLandEnd, Gn001Animations.fly_land_end, ageInTicks, 1.0f);
            hasSpecialAnimation = true;
        } else if (s.land.isStarted()) {
            this.animate(s.land, Gn001Animations.land, ageInTicks, 1.0f);
            hasSpecialAnimation = true;
        } else if (s.jump.isStarted()) {
            this.animate(s.jump, Gn001Animations.jump, ageInTicks, 1.0f);
            hasSpecialAnimation = true;
        } else if (s.fallLoop.isStarted()) {
            this.animate(s.fallLoop, Gn001Animations.fall_loop, ageInTicks, 1.0f);
            hasSpecialAnimation = true;
        } else if (s.appearance.isStarted()) {
            this.animate(s.appearance, Gn001Animations.appearance, ageInTicks, 1.0f);
            hasSpecialAnimation = true;
        } else if (s.death.isStarted()) {
            this.animate(s.death, Gn001Animations.death, ageInTicks, 1.0f);
            hasSpecialAnimation = true;
        } else if (s.walk.isStarted()) {
            this.animate(s.walk, Gn001Animations.walk, ageInTicks, 1.0f);
            hasSpecialAnimation = true;
        } else if (s.blitzAttack1.isStarted()) {
            this.animate(s.blitzAttack1, Gn001Animations.blitz_attack1, ageInTicks, 1.0f);
            hasSpecialAnimation = true;
        } else if (s.blitzAttack2.isStarted()) {
            this.animate(s.blitzAttack2, Gn001Animations.blitz_attack2, ageInTicks, 1.0f);
            hasSpecialAnimation = true;
        } else if (s.blitzAttack3.isStarted()) {
            this.animate(s.blitzAttack3, Gn001Animations.blitz_attack3, ageInTicks, 1.0f);
            hasSpecialAnimation = true;
        } else if (s.blitzAttack4.isStarted()) {
            this.animate(s.blitzAttack4, Gn001Animations.blitz_attack4, ageInTicks, 1.0f);
            hasSpecialAnimation = true;
        } else if (s.blitzAttack5.isStarted()) {
            this.animate(s.blitzAttack5, Gn001Animations.blitz_attack5, ageInTicks, 1.0f);
            hasSpecialAnimation = true;
        } else if (s.blitzAttack6.isStarted()) {
            this.animate(s.blitzAttack6, Gn001Animations.blitz_attack6, ageInTicks, 1.0f);
            hasSpecialAnimation = true;
        } else if (s.blitzAttack7.isStarted()) {
            this.animate(s.blitzAttack7, Gn001Animations.blitz_attack7, ageInTicks, 1.0f);
            hasSpecialAnimation = true;
        } else if (s.downwardChop.isStarted()) {
            this.animate(s.downwardChop, Gn001Animations.downward_chop, ageInTicks, 1.0f);
            hasSpecialAnimation = true;
        }

        if (hasSpecialAnimation) {
            return;
        }

        if (entity.isFlyingMode()) {
            if (s.flyDive.isStarted()) {
                this.animate(s.flyDive, Gn001Animations.fly_dive, ageInTicks, 1.0f);
            } else if (s.flyDiveAlt.isStarted()) {
                this.animate(s.flyDiveAlt, Gn001Animations.fly_dive_alt, ageInTicks, 1.0f);
            } else if (s.flySoaring.isStarted()) {
                this.animate(s.flySoaring, Gn001Animations.fly_soaring, ageInTicks, 1.0f);
            } else if (s.flyLand.isStarted()) {
                this.animate(s.flyLand, Gn001Animations.fly_land, ageInTicks, 1.0f);
            } else {
                this.animate(s.fly, Gn001Animations.fly, ageInTicks, 1.0f);
            }
            return;
        }

        if (entity.isInWater()) {
            if (entity.getDeltaMovement().horizontalDistanceSqr() > 0.8) {
                this.animate(s.swimFast, Gn001Animations.swim_fast, ageInTicks, 1.0f);
            } else {
                this.animate(s.swim, Gn001Animations.swim, ageInTicks, 1.0f);
            }
            return;
        }

        if (entity.isCrouching()) {
            if (limbSwingAmount > 0.01f) {
                this.animateWalk(Gn001Animations.sneak_walk, limbSwing, limbSwingAmount, 2.0f, 2.5f);
            } else {
                this.animate(s.sneak, Gn001Animations.sneak, ageInTicks, 1.0f);
            }
            return;
        }

        if (entity.isSleeping()) {
            this.animate(s.sleep, Gn001Animations.sleep, ageInTicks, 1.0f);
            return;
        }

        if (s.statuePose1.isStarted()) {
            this.animate(s.statuePose1, Gn001Animations.statue_pose_1, ageInTicks, 1.0f);
            return;
        }

        if (limbSwingAmount > 0.01f) {
            if (entity.getDeltaMovement().horizontalDistanceSqr() > 0.8) {
                this.animateWalk(Gn001Animations.run, limbSwing, limbSwingAmount, 1.5f, 2.0f);
            } else {
                this.animateWalk(Gn001Animations.walk, limbSwing, limbSwingAmount, 2.0f, 2.5f);
            }
            return;
        }

        this.animate(s.idle, Gn001Animations.idle, ageInTicks, 1.0f);
    }

    private void updateAnimationFlags(Gn001Entity entity, Gn001ClientAnimStates.States s) {

        if (entity.isDying()) {
            stopAll(s);
            if (!s.death.isStarted()) s.death.start(entity.tickCount);
            return;
        }

        if (entity.isSpawning()) {
            stopAll(s);
            if (!s.appearance.isStarted()) s.appearance.start(entity.tickCount);
            return;
        } else {
            s.appearance.stop();
        }

        if (entity.isUltimateActive()) {
            updateUltimateAnimations(entity, s);
            return;
        } else {
            stopBlitz(s);
            s.downwardChop.stop();
        }

        if (entity.isFlyingMode()) {
            s.idle.stop();
            s.walk.stop();
            s.useItemRight.stop();
            s.attack1.stop();
            s.attack2.stop();
            s.attack3.stop();
            s.breath.stop();
            s.flySpin.stop();
            s.flyLand.stop();
            s.appearance.stop();

            int phase = entity.getFlyPhase();
            if (phase == 1) {
                if (!s.flyFire.isStarted()) s.flyFire.start(entity.tickCount);
                s.flyKick.stop();
                s.fly.stop();
            } else if (phase == 3) {
                if (!s.flyKick.isStarted()) s.flyKick.start(entity.tickCount);
                s.flyFire.stop();
                s.fly.stop();
            } else {
                if (!s.fly.isStarted()) s.fly.start(entity.tickCount);
                s.flyFire.stop();
                s.flyKick.stop();
            }
            return;
        } else {
            s.fly.stop();
            s.flyFire.stop();
            s.flyKick.stop();
        }

        boolean isMoving = entity.getDeltaMovement().horizontalDistanceSqr() > 0.01
                && entity.onGround();
        if (isMoving) {
            s.idle.stop();
            if (!s.walk.isStarted()) s.walk.start(entity.tickCount);
        } else {
            s.walk.stop();
            if (s.idleTimeout <= 0) {
                s.idleTimeout = 80;
                s.idle.start(entity.tickCount);
            } else {
                s.idleTimeout--;
            }
        }

        if (entity.isMeleeAttacking()) {
            int animType = entity.getAttackAnimType();
            switch (animType) {
                case 1:
                    if (!s.attack1.isStarted()) s.attack1.start(entity.tickCount);
                    break;
                case 2:
                    if (!s.attack2.isStarted()) s.attack2.start(entity.tickCount);
                    break;
                case 3:
                    if (!s.attack3.isStarted()) s.attack3.start(entity.tickCount);
                    break;
                default:
                    if (!s.useItemRight.isStarted()) s.useItemRight.start(entity.tickCount);
                    break;
            }
        } else {
            s.useItemRight.stop();
            s.attack1.stop();
            s.attack2.stop();
            s.attack3.stop();
        }

        if (entity.isRangedAttacking()) {
            if (!s.breath.isStarted()) s.breath.start(entity.tickCount);
        } else {
            s.breath.stop();
        }

        if (entity.isSpinAttacking()) {
            if (!s.flySpin.isStarted()) s.flySpin.start(entity.tickCount);
        } else {
            s.flySpin.stop();
        }
    }


    private void updateUltimateAnimations(Gn001Entity entity, Gn001ClientAnimStates.States s) {


        boolean shouldPlayBlitz = (entity.getUltimateFinalSubPhase() == -1)
                && (entity.getUltimateSubPhase() == 0);
        int blitzIndex = shouldPlayBlitz ? (entity.getUltimateAnimIndex() % 7) : -1;


        if (s.lastBlitzIndex != blitzIndex) {
            stopBlitz(s);
            s.lastBlitzIndex = blitzIndex;
            if (blitzIndex >= 0) {
                switch (blitzIndex) {
                    case 0: s.blitzAttack1.start(entity.tickCount); break;
                    case 1: s.blitzAttack2.start(entity.tickCount); break;
                    case 2: s.blitzAttack3.start(entity.tickCount); break;
                    case 3: s.blitzAttack4.start(entity.tickCount); break;
                    case 4: s.blitzAttack5.start(entity.tickCount); break;
                    case 5: s.blitzAttack6.start(entity.tickCount); break;
                    case 6: s.blitzAttack7.start(entity.tickCount); break;
                }
            }
        }


        s.idle.stop();
        s.walk.stop();
        s.useItemRight.stop();
        s.attack1.stop();
        s.attack2.stop();
        s.attack3.stop();
        s.breath.stop();
        s.flyLand.stop();
        s.death.stop();
        s.appearance.stop();
        s.flySpin.stop();
        s.flyKick.stop();

        int finalSubPhase = entity.getUltimateFinalSubPhase();


        if (finalSubPhase != -1) {
            s.flyFire.stop();
            s.walk.stop();
            s.idleTimeout = 0;

            if (finalSubPhase == 2) {
                s.fly.stop();
                if (!s.downwardChop.isStarted()) {
                    s.downwardChop.start(entity.tickCount);
                }
            } else {
                s.downwardChop.stop();
                if (!s.fly.isStarted()) s.fly.start(entity.tickCount);
            }
            return;
        }

        if (entity.getUltimateSubPhase() == 0) {
            s.fly.stop();
            s.flyFire.stop();
            s.downwardChop.stop();
            s.walk.stop();
            return;
        }

        s.downwardChop.stop();
        if (!s.fly.isStarted()) s.fly.start(entity.tickCount);
        if (entity.isUltimateFiring()) {
            if (!s.flyFire.isStarted()) s.flyFire.start(entity.tickCount);
        } else {
            s.flyFire.stop();
        }
    }

    private void stopBlitz(Gn001ClientAnimStates.States s) {
        s.blitzAttack1.stop();
        s.blitzAttack2.stop();
        s.blitzAttack3.stop();
        s.blitzAttack4.stop();
        s.blitzAttack5.stop();
        s.blitzAttack6.stop();
        s.blitzAttack7.stop();
    }

    private void stopAll(Gn001ClientAnimStates.States s) {
        s.idle.stop();
        s.walk.stop();
        s.useItemRight.stop();
        s.attack1.stop();
        s.attack2.stop();
        s.attack3.stop();
        s.breath.stop();
        s.flySpin.stop();
        s.fly.stop();
        s.flyLand.stop();
        s.flyFire.stop();
        s.flyKick.stop();
        stopBlitz(s);
        s.lastBlitzIndex = -1;
        s.downwardChop.stop();
    }



    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer,
                               int packedLight, int packedOverlay, int color) {
        Center.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
    }

    @Override
    public ModelPart root() {
        return Center;
    }
}
