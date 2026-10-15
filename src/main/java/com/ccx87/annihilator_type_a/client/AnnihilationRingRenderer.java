package com.ccx87.annihilator_type_a.client;

import com.ccx87.annihilator_type_a.AnnihilatorTypeA;
import com.ccx87.annihilator_type_a.registry.ATAEffects;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;


public final class AnnihilationRingRenderer {


    public static final float OUTER_GEAR_RADIUS = 3.90F;
    private static final float ENCODER_DIAL_RADIUS = 3.00F;
    private static final float INDEX_DIAL_RADIUS = 2.00F;
    private static final float SCANNER_RADIUS = 3.70F;
    private static final double OUTER_GEAR_HEIGHT = 0.14D;
    private static final double ENCODER_DIAL_HEIGHT = 0.72D;
    private static final double INDEX_DIAL_HEIGHT = 0.04D;
    private static final double SCANNER_HEIGHT = 0.22D;

    public static final int GEAR_TEETH = 32;

    private static final float MAX_SIZE_RATIO = 4.0F;

    private static final float MIN_SIZE_RATIO = 0.35F;

    private static final float REFERENCE_HEIGHT = 1.0F;

    private static final ResourceLocation DIAL_TEXTURE =
            AnnihilatorTypeA.id("textures/effect/annihilation_ring.png");
    private static final ResourceLocation SCANNER_TEXTURE =
            AnnihilatorTypeA.id("textures/effect/annihilation_scanner.png");

    private static final RenderType DIAL = RenderType.energySwirl(DIAL_TEXTURE, 0.0F, 0.0F);
    private static final RenderType SCANNER = RenderType.energySwirl(SCANNER_TEXTURE, 0.0F, 0.0F);

    private AnnihilationRingRenderer() {}

    public static float gearAngleDegrees(double seconds) {
        return (float) (seconds * 9.0D) + 1.1F * Mth.sin((float) (seconds * 11.0D));
    }

    private static float indexAngleDegrees(double seconds) {
        return (float) (Math.floor(seconds * 6.0D) * 10.0D);
    }

    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        ClientLevel level = minecraft.level;
        if (level == null || !ClientConfig.RING_ENABLED.get()) {
            return;
        }

        Vec3 camera = event.getCamera().getPosition();
        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        double seconds = (level.getGameTime() + partialTick) / 20.0D;

        double visibleDistance = ClientConfig.RING_VISIBLE_DISTANCE.get();
        double visibleDistanceSqr = visibleDistance * visibleDistance;
        float opacity = ClientConfig.RING_OPACITY.get().floatValue();

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource buffers = minecraft.renderBuffers().bufferSource();
        boolean drewAnything = false;

        float gearAngle = gearAngleDegrees(seconds);
        float encoderAngle = (float) (-seconds * 22.0D);
        float indexAngle = indexAngleDegrees(seconds);
        float sweepAngle = (float) (-seconds * 95.0D);

        for (AbstractClientPlayer player : level.players()) {
            if (!player.hasEffect(ATAEffects.IGNORE_INVINCIBILITY)) {
                continue;
            }
            if (player.distanceToSqr(camera.x, camera.y, camera.z) > visibleDistanceSqr) {
                continue;
            }

            double x = Mth.lerp(partialTick, player.xo, player.getX()) - camera.x;
            double y = Mth.lerp(partialTick, player.yo, player.getY()) - camera.y;
            double z = Mth.lerp(partialTick, player.zo, player.getZ()) - camera.z;

            float size = ringScale(player);

            poseStack.pushPose();
            poseStack.translate(x, y, z);
            drawLayer(poseStack, buffers, DIAL, OUTER_GEAR_HEIGHT * size, OUTER_GEAR_RADIUS * size,
                    gearAngle, 1.00F, 0.55F, 0.88F, 1.00F, opacity);
            drawLayer(poseStack, buffers, DIAL, ENCODER_DIAL_HEIGHT * size, ENCODER_DIAL_RADIUS * size,
                    encoderAngle, 0.50F, 0.62F, 0.90F, 1.00F, opacity);
            drawLayer(poseStack, buffers, DIAL, INDEX_DIAL_HEIGHT * size, INDEX_DIAL_RADIUS * size,
                    indexAngle, 0.32F, 0.45F, 0.72F, 1.00F, opacity);
            drawLayer(poseStack, buffers, SCANNER, SCANNER_HEIGHT * size, SCANNER_RADIUS * size,
                    sweepAngle, 0.85F, 0.40F, 0.86F, 1.00F, opacity);
            poseStack.popPose();
            drewAnything = true;
        }

        if (drewAnything) {
            buffers.endBatch(DIAL);
            buffers.endBatch(SCANNER);
        }
    }
    public static float ringScale(AbstractClientPlayer player) {
        float multiplier = ClientConfig.RING_SIZE_MULTIPLIER.get().floatValue();
        if (!ClientConfig.RING_FOLLOW_SIZE.get()) {
            return multiplier;
        }
        float ratio = Mth.clamp(player.getBbHeight() / REFERENCE_HEIGHT,
                MIN_SIZE_RATIO, MAX_SIZE_RATIO);
        float strength = ClientConfig.RING_SIZE_FOLLOW_STRENGTH.get().floatValue();
        float grown = 1.0F + (ratio - 1.0F) * strength;
        return multiplier * Mth.clamp(grown, MIN_SIZE_RATIO, MAX_SIZE_RATIO);
    }

    private static void drawLayer(PoseStack poseStack, MultiBufferSource buffers, RenderType type,
                                  double yOffset, float radius, float angleDegrees, float brightness,
                                  float red, float green, float blue, float opacity) {
        poseStack.pushPose();
        poseStack.translate(0.0D, yOffset, 0.0D);
        poseStack.mulPose(Axis.YP.rotationDegrees(angleDegrees));
        poseStack.scale(radius, 1.0F, radius);

        float fade = brightness * opacity;
        float r = red * fade;
        float g = green * fade;
        float b = blue * fade;

        Matrix4f matrix = poseStack.last().pose();
        VertexConsumer consumer = buffers.getBuffer(type);
        vertex(consumer, matrix, -1.0F, -1.0F, r, g, b, 0.0F, 0.0F);
        vertex(consumer, matrix, -1.0F, 1.0F, r, g, b, 0.0F, 1.0F);
        vertex(consumer, matrix, 1.0F, 1.0F, r, g, b, 1.0F, 1.0F);
        vertex(consumer, matrix, 1.0F, -1.0F, r, g, b, 1.0F, 0.0F);
        poseStack.popPose();
    }

    private static void vertex(VertexConsumer consumer, Matrix4f matrix, float x, float z,
                               float red, float green, float blue, float u, float v) {
        consumer.addVertex(matrix, x, 0.0F, z)
                .setColor(red, green, blue, 1.0F)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(LightTexture.FULL_BRIGHT)
                .setNormal(0.0F, 1.0F, 0.0F);
    }
}
