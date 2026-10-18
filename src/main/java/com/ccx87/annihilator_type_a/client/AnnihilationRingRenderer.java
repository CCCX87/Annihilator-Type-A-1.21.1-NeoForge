package com.ccx87.annihilator_type_a.client;

import com.ccx87.annihilator_type_a.AnnihilatorTypeA;
import com.ccx87.annihilator_type_a.registry.ATAEffects;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage;
import org.joml.Matrix4f;

public final class AnnihilationRingRenderer {
   public static final float OUTER_GEAR_RADIUS = 3.9F;
   private static final float ENCODER_DIAL_RADIUS = 3.0F;
   private static final float INDEX_DIAL_RADIUS = 2.0F;
   private static final float SCANNER_RADIUS = 3.7F;
   private static final double OUTER_GEAR_HEIGHT = 0.14;
   private static final double ENCODER_DIAL_HEIGHT = 0.72;
   private static final double INDEX_DIAL_HEIGHT = 0.04;
   private static final double SCANNER_HEIGHT = 0.22;
   public static final int GEAR_TEETH = 32;
   private static final float MAX_SIZE_RATIO = 4.0F;
   private static final float MIN_SIZE_RATIO = 0.35F;
   private static final float REFERENCE_HEIGHT = 1.0F;
   private static final ResourceLocation DIAL_TEXTURE = AnnihilatorTypeA.id("textures/effect/annihilation_ring.png");
   private static final ResourceLocation SCANNER_TEXTURE = AnnihilatorTypeA.id("textures/effect/annihilation_scanner.png");
   private static final RenderType DIAL = RenderType.energySwirl(DIAL_TEXTURE, 0.0F, 0.0F);
   private static final RenderType SCANNER = RenderType.energySwirl(SCANNER_TEXTURE, 0.0F, 0.0F);

   private AnnihilationRingRenderer() {
   }

   public static float gearAngleDegrees(double seconds) {
      return (float)(seconds * 9.0) + 1.1F * Mth.sin((float)(seconds * 11.0));
   }

   private static float indexAngleDegrees(double seconds) {
      return (float)(Math.floor(seconds * 6.0) * 10.0);
   }

   public static void onRenderLevelStage(RenderLevelStageEvent event) {
      if (event.getStage() == Stage.AFTER_PARTICLES) {
         if (ClientConfig.loaded()) {
            Minecraft minecraft = Minecraft.getInstance();
            ClientLevel level = minecraft.level;
            if (level != null && (Boolean)ClientConfig.RING_ENABLED.get()) {
               Vec3 camera = event.getCamera().getPosition();
               float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
               double seconds = (double)((float)level.getGameTime() + partialTick) / 20.0;
               double visibleDistance = (Double)ClientConfig.RING_VISIBLE_DISTANCE.get();
               double visibleDistanceSqr = visibleDistance * visibleDistance;
               float opacity = ((Double)ClientConfig.RING_OPACITY.get()).floatValue();
               PoseStack poseStack = event.getPoseStack();
               BufferSource buffers = minecraft.renderBuffers().bufferSource();
               Frustum frustum = event.getFrustum();
               boolean cullOffscreen = (Boolean)ClientConfig.RING_CULL_OFFSCREEN.get();
               boolean distanceLod = (Boolean)ClientConfig.RING_DISTANCE_LOD.get();
               boolean drewAnything = false;
               long timing = DebugTimings.begin();
               float gearAngle = gearAngleDegrees(seconds);
               float encoderAngle = (float)(-seconds * 22.0);
               float indexAngle = indexAngleDegrees(seconds);
               float sweepAngle = (float)(-seconds * 95.0);

               for (AbstractClientPlayer player : level.players()) {
                  if (player.hasEffect(ATAEffects.IGNORE_INVINCIBILITY)) {
                     double distanceSqr = player.distanceToSqr(camera.x, camera.y, camera.z);
                     if (!(distanceSqr > visibleDistanceSqr)) {
                        float size = ringScale(player);
                        float outerRadius = 3.9F * size;
                        if (!cullOffscreen || frustum.isVisible(player.getBoundingBox().inflate((double)(outerRadius + 1.0F)))) {
                           double x = Mth.lerp((double)partialTick, player.xo, player.getX()) - camera.x;
                           double y = Mth.lerp((double)partialTick, player.yo, player.getY()) - camera.y;
                           double z = Mth.lerp((double)partialTick, player.zo, player.getZ()) - camera.z;
                           int layers = 4;
                           if (distanceLod) {
                              double distance = Math.sqrt(distanceSqr);
                              layers = distance <= 24.0 ? 4 : (distance <= 40.0 ? 2 : 1);
                           }

                           poseStack.pushPose();
                           poseStack.translate(x, y, z);
                           drawLayer(poseStack, buffers, DIAL, 0.14 * (double)size, outerRadius, gearAngle, 1.0F, 0.55F, 0.88F, 1.0F, opacity);
                           if (layers >= 2) {
                              drawLayer(poseStack, buffers, SCANNER, 0.22 * (double)size, 3.7F * size, sweepAngle, 0.85F, 0.4F, 0.86F, 1.0F, opacity);
                           }

                           if (layers >= 4) {
                              drawLayer(poseStack, buffers, DIAL, 0.72 * (double)size, 3.0F * size, encoderAngle, 0.5F, 0.62F, 0.9F, 1.0F, opacity);
                              drawLayer(poseStack, buffers, DIAL, 0.04 * (double)size, 2.0F * size, indexAngle, 0.32F, 0.45F, 0.72F, 1.0F, opacity);
                           }

                           poseStack.popPose();
                           drewAnything = true;
                        }
                     }
                  }
               }

               if (drewAnything) {
                  buffers.endBatch(DIAL);
                  buffers.endBatch(SCANNER);
               }

               DebugTimings.end("ring", timing);
            }
         }
      }
   }

   public static float ringScale(AbstractClientPlayer player) {
      float multiplier = ((Double)ClientConfig.RING_SIZE_MULTIPLIER.get()).floatValue();
      float maxScale = ((Double)ClientConfig.RING_MAX_SCALE.get()).floatValue();
      if (!(Boolean)ClientConfig.RING_FOLLOW_SIZE.get()) {
         return Math.min(multiplier, maxScale);
      } else {
         float ratio = Mth.clamp(player.getBbHeight() / 1.0F, 0.35F, 4.0F);
         float strength = ((Double)ClientConfig.RING_SIZE_FOLLOW_STRENGTH.get()).floatValue();
         float grown = 1.0F + (ratio - 1.0F) * strength;
         return Mth.clamp(multiplier * Mth.clamp(grown, 0.35F, 4.0F), 0.35F, maxScale);
      }
   }

   private static void drawLayer(
      PoseStack poseStack,
      MultiBufferSource buffers,
      RenderType type,
      double yOffset,
      float radius,
      float angleDegrees,
      float brightness,
      float red,
      float green,
      float blue,
      float opacity
   ) {
      poseStack.pushPose();
      poseStack.translate(0.0, yOffset, 0.0);
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

   private static void vertex(VertexConsumer consumer, Matrix4f matrix, float x, float z, float red, float green, float blue, float u, float v) {
      consumer.addVertex(matrix, x, 0.0F, z)
         .setColor(red, green, blue, 1.0F)
         .setUv(u, v)
         .setOverlay(OverlayTexture.NO_OVERLAY)
         .setLight(15728880)
         .setNormal(0.0F, 1.0F, 0.0F);
   }
}
