package com.ccx87.annihilator_type_a.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ccx87.annihilator_type_a.AnnihilatorTypeA;
import com.ccx87.annihilator_type_a.registry.ATAEffects;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Matrix4f;

public final class MarkAdjudicateOrbs {
    private static final int DURATION = 45;
    private static final double RING_RADIUS = 7.0D;
    private static final double RISE_HEIGHT = 4.6D;

    private static final ResourceLocation ORB_TEXTURE =
            AnnihilatorTypeA.id("textures/effect/orb_glow.png");
    private static final RenderType ORB = RenderType.energySwirl(ORB_TEXTURE, 0.0F, 0.0F);
    private static final net.minecraft.resources.ResourceLocation CORE_TEXTURE =
            AnnihilatorTypeA.id("textures/effect/orb_core.png");
    private static final RenderType CORE = RenderType.entityTranslucent(CORE_TEXTURE);

    private static final int[] COLOURS = {0xFF3A2E6E, 0xFFFF4040, 0xFF2E6EFF, 0xFFFF4040, 0xFF3A2E6E, 0xFF2E6EFF};

    private static int ticks;
    private static boolean wasCharging;
    private static int armedTicks;
    private static int startDelay;

    private MarkAdjudicateOrbs() {}

    public static void reset() {
        ticks = 0;
        startDelay = 0;
        armedTicks = 0;
        wasCharging = false;
    }

    public static void tick(Minecraft minecraft) {
        Player player = minecraft.player;
        boolean charging = player != null && player.hasEffect(ATAEffects.MARK_ADJUDICATE_CHARGE);
        if (charging) {
            armedTicks++;
        } else {
            if (wasCharging && armedTicks >= 58) {
                startDelay = 15;
            }
            armedTicks = 0;
        }
        wasCharging = charging;
        if (startDelay > 0) {
            startDelay--;
            if (startDelay == 0) {
                ticks = 1;
            }
        } else if (ticks > 0) {
            ticks++;
            if (ticks > DURATION) {
                ticks = 0;
            }
        }
    }

    private static boolean isRunning() {
        return ticks > 0 && ClientConfig.loaded();
    }

    public static void render(RenderLevelStageEvent event) {
        if (!isRunning() || event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null) {
            return;
        }
        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        Vec3 caster = new Vec3(
                Mth.lerp(partialTick, player.xo, player.getX()),
                Mth.lerp(partialTick, player.yo, player.getY()),
                Mth.lerp(partialTick, player.zo, player.getZ()));
        float size = player instanceof net.minecraft.client.player.AbstractClientPlayer local
                ? AnnihilationRingRenderer.ringScale(local)
                : 1.0F;
        renderAt(event, caster, size, ticks / (float) DURATION);
    }

    public static void renderAt(RenderLevelStageEvent event, Vec3 caster, float size, float t) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        float growth = Mth.clamp(t * 3.0F, 0.0F, 1.0F);
        float merge = Mth.clamp((t - 0.78F) / 0.22F, 0.0F, 1.0F);
        double radius = Math.max(1.6D * size, RING_RADIUS * size * (1.0D - 0.92D * t));
        radius *= (1.0D - merge);
        double rise = RISE_HEIGHT * size * t;
        float orbSize = (0.14F + 0.16F * growth) * size;

        Camera camera = event.getCamera();
        Vec3 cameraPos = camera.getPosition();

        PoseStack poseStack = event.getPoseStack();
        MultiBufferSource.BufferSource buffers = minecraft.renderBuffers().bufferSource();

        for (int index = 0; index < 6; index++) {
            for (int ghost = 3; ghost >= 1; ghost--) {
                float past = t - 0.012F * ghost;
                if (past < 0.0F) {
                    continue;
                }
                Vec3 ghostPos = caster.add(orbPosition(past, index, size));
                float ghostAlpha = 0.10F * (1.0F - ghost / 4.0F) * (1.0F - merge * 0.5F);
                billboardGlow(buffers, poseStack, ghostPos, camera, cameraPos,
                        orbSize * (0.55F - 0.08F * ghost), COLOURS[index], ghostAlpha);
            }
            Vec3 position = caster.add(orbPosition(t, index, size));
            float alpha = 1.0F - merge * 0.35F;
            poseStack.pushPose();
            poseStack.translate(position.x - cameraPos.x, position.y - cameraPos.y, position.z - cameraPos.z);
            poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-camera.getYRot()));
            poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(camera.getXRot()));
            Matrix4f matrix = poseStack.last().pose();
            quad(buffers.getBuffer(ORB), matrix, orbSize * 2.4F, withAlpha(COLOURS[index], alpha * 0.55F));
            quad(buffers.getBuffer(ORB), matrix, orbSize * 1.6F, withAlpha(COLOURS[index], alpha * 0.85F));
            quad(buffers.getBuffer(ORB), matrix, orbSize * 1.1F, withAlpha(COLOURS[index], alpha));
            quad(buffers.getBuffer(CORE), matrix, orbSize * 0.62F, withAlpha(0xFF05050A, alpha));
            poseStack.popPose();
        }

        for (int index = 0; index < 6; index++) {
            Vec3 a = caster.add(orbPosition(t, index, size));
            Vec3 b = caster.add(orbPosition(t, (index + 1) % 6, size));
            if (a.distanceTo(b) < 3.2D) {
                drawArc(buffers, poseStack, a, b, camera, cameraPos, t, index, merge);
            }
        }

        if (merge > 0.55F) {
            float ringT = (merge - 0.55F) / 0.45F;
            Vec3 centre = caster.add(0.0D, 0.9D + RISE_HEIGHT * size * t, 0.0D);
            float ringRadius = (0.8F + 5.5F * ringT) * size;
            flatRing(buffers, poseStack, centre, cameraPos, ringRadius * 0.86F, ringRadius,
                    withAlpha(0xFFFFFFFF, (1.0F - ringT) * 0.55F), 32);
        }

        if (merge > 0.0F) {
            poseStack.pushPose();
            poseStack.translate(caster.x - cameraPos.x, caster.y + 0.9D + rise - cameraPos.y,
                    caster.z - cameraPos.z);
            poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-camera.getYRot()));
            poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(camera.getXRot()));
            float flash = 0.6F + 1.4F * merge;
            quad(buffers.getBuffer(ORB), poseStack.last().pose(), flash,
                    withAlpha(0xFFFFFFFF, 0.85F * merge));
            poseStack.popPose();
        }
        buffers.endBatch();
    }

    private static Vec3 orbPosition(float t, int index, float size) {
        float merge = Mth.clamp((t - 0.78F) / 0.22F, 0.0F, 1.0F);
        double radius = Math.max(1.6D * size, RING_RADIUS * size * (1.0D - 0.92D * t)) * (1.0D - merge);
        double rise = RISE_HEIGHT * size * t;
        double angle = Math.toRadians(index * 60.0D + t * 260.0F);
        double height = rise + 0.55D * Math.sin(t * Math.PI * 2.0D + index);
        return new Vec3(Math.cos(angle) * radius, 0.9D + height, Math.sin(angle) * radius);
    }

    private static void billboardGlow(MultiBufferSource.BufferSource buffers, PoseStack poseStack,
                                      Vec3 world, Camera camera, Vec3 cameraPos, float half, int colour,
                                      float alpha) {
        poseStack.pushPose();
        poseStack.translate(world.x - cameraPos.x, world.y - cameraPos.y, world.z - cameraPos.z);
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-camera.getYRot()));
        poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(camera.getXRot()));
        quad(buffers.getBuffer(ORB), poseStack.last().pose(), half, withAlpha(colour, alpha));
        poseStack.popPose();
    }

    private static void drawArc(MultiBufferSource.BufferSource buffers, PoseStack poseStack, Vec3 a, Vec3 b,
                                Camera camera, Vec3 cameraPos, float t, int index, float merge) {
        Vec3 delta = b.subtract(a);
        int dots = 7;
        for (int i = 1; i < dots; i++) {
            double fraction = i / (double) dots;
            double jitter = Math.sin(t * 53.0D + index * 17.0D + i * 7.0D) * 0.18D;
            Vec3 point = a.add(delta.scale(fraction)).add(0.0D, jitter, 0.0D);
            float alpha = 0.55F * (1.0F - merge) * (float) Math.sin(fraction * Math.PI);
            billboardGlow(buffers, poseStack, point, camera, cameraPos, 0.10F, 0xFFB8D8FF, alpha);
        }
    }

    private static void flatRing(MultiBufferSource.BufferSource buffers, PoseStack poseStack, Vec3 centre,
                                 Vec3 cameraPos, float inner, float outer, int colour, int segments) {
        poseStack.pushPose();
        poseStack.translate(centre.x - cameraPos.x, centre.y - cameraPos.y, centre.z - cameraPos.z);
        Matrix4f matrix = poseStack.last().pose();
        VertexConsumer consumer = buffers.getBuffer(ORB);
        for (int i = 0; i < segments; i++) {
            double a0 = Math.PI * 2.0D * i / segments;
            double a1 = Math.PI * 2.0D * (i + 1) / segments;
            float i0x = (float) (Math.cos(a0) * inner);
            float i0z = (float) (Math.sin(a0) * inner);
            float o0x = (float) (Math.cos(a0) * outer);
            float o0z = (float) (Math.sin(a0) * outer);
            float o1x = (float) (Math.cos(a1) * outer);
            float o1z = (float) (Math.sin(a1) * outer);
            float i1x = (float) (Math.cos(a1) * inner);
            float i1z = (float) (Math.sin(a1) * inner);
            vertexXZ(consumer, matrix, i0x, i0z, colour);
            vertexXZ(consumer, matrix, o0x, o0z, colour);
            vertexXZ(consumer, matrix, o1x, o1z, colour);
            vertexXZ(consumer, matrix, i1x, i1z, colour);
            vertexXZ(consumer, matrix, i1x, i1z, colour);
            vertexXZ(consumer, matrix, o1x, o1z, colour);
            vertexXZ(consumer, matrix, o0x, o0z, colour);
            vertexXZ(consumer, matrix, i0x, i0z, colour);
        }
        poseStack.popPose();
    }

    private static void vertexXZ(VertexConsumer consumer, Matrix4f matrix, float x, float z, int colour) {
        consumer.addVertex(matrix, x, 0.0F, z).setColor(colour).setUv(0.5F, 0.5F)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT)
                .setNormal(0.0F, 1.0F, 0.0F);
    }

    private static void quad(VertexConsumer consumer, Matrix4f matrix, float half, int colour) {
        consumer.addVertex(matrix, -half, half, 0.0F).setColor(colour).setUv(0.0F, 0.0F)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0F, 0.0F, 1.0F);
        consumer.addVertex(matrix, half, half, 0.0F).setColor(colour).setUv(1.0F, 0.0F)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0F, 0.0F, 1.0F);
        consumer.addVertex(matrix, half, -half, 0.0F).setColor(colour).setUv(1.0F, 1.0F)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0F, 0.0F, 1.0F);
        consumer.addVertex(matrix, -half, -half, 0.0F).setColor(colour).setUv(0.0F, 1.0F)
                .setOverlay(OverlayTexture.NO_OVERLAY).setLight(LightTexture.FULL_BRIGHT).setNormal(0.0F, 0.0F, 1.0F);
    }

    private static int withAlpha(int argb, float factor) {
        int alpha = Math.round(((argb >>> 24) & 0xFF) * factor);
        return (alpha << 24) | (argb & 0x00FFFFFF);
    }
}
