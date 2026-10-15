package com.ccx87.annihilator_type_a.client;

import com.ccx87.annihilator_type_a.AnnihilatorTypeA;
import com.ccx87.annihilator_type_a.registry.ATAEffects;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent.Stage;
import org.joml.Matrix4f;

public final class LightningStormPanels {
   private static final int PANEL_COUNT = 6;
   private static final double RING_RADIUS = 1.6;
   private static final float PANEL_WIDTH = 0.78F;
   private static final float PANEL_HEIGHT = 1.12F;
   private static final float PANEL_BOTTOM = 0.35F;
   private static final ResourceLocation PANEL_TEXTURE = AnnihilatorTypeA.id("textures/effect/lightning_panel.png");
   private static final ResourceLocation WARNING_TEXTURE = AnnihilatorTypeA.id("textures/effect/lightning_warning.png");
   private static final RenderType PANEL = RenderType.energySwirl(PANEL_TEXTURE, 0.0F, 0.0F);
   private static final RenderType WARNING = RenderType.energySwirl(WARNING_TEXTURE, 0.0F, 0.0F);
   private static final int CYAN = -10888961;
   private static final int RED = -49088;
   private static final int CAST_TICKS = 40;
   private static final float UNUSED_SPIN = 0.0F;
   private static final float SPIN_PER_TICK = 2.25F;
   private static final int RELEASE_TICKS = 80;
   private static int releaseTicks;
   private static boolean wasReleased;
   private static int chargeTicks;
   private static float scroll;
   private static float spinDegrees;

   private LightningStormPanels() {
   }

   public static void tick(Minecraft minecraft) {
      scroll += 0.012F;
      if (scroll > 1.0F) {
         scroll--;
      }

      Player player = minecraft.player;
      boolean released = player != null && player.hasEffect(ATAEffects.LIGHTNING_STORM);
      if (released && !wasReleased) {
         releaseTicks = 80;
      }

      wasReleased = released;
      if (releaseTicks > 0) {
         releaseTicks--;
      }

      if (player != null && player.hasEffect(ATAEffects.LIGHTNING_CHARGE)) {
         chargeTicks = Math.min(chargeTicks + 1, 40);
         spinDegrees = 0.0F;
         drawConvergingMotes(player);
      } else if (!released) {
         chargeTicks = 0;
         spinDegrees = 0.0F;
      }
   }

   private static void drawConvergingMotes(Player player) {
      ClientLevel level = player.level() instanceof ClientLevel client ? client : null;
      if (level != null) {
         float progress = (float)chargeTicks / 40.0F;
         RandomSource random = level.random;
         float bodySize = player instanceof AbstractClientPlayer local ? AnnihilationRingRenderer.ringScale(local) : 1.0F;
         int count = 2 + Math.round(progress * 6.0F * bodySize);
         double reach = (3.2 - 1.6 * progress) * (0.55 + 0.45 * bodySize);
         double centreY = player.getY() + (double)player.getBbHeight() * 0.5;

         for (int i = 0; i < count; i++) {
            double angle = random.nextDouble() * Math.PI * 2.0;
            double distance = reach * (0.6 + random.nextDouble() * 0.4);
            double half = (double)player.getBbHeight() * 0.5 * random.nextDouble();
            level.addParticle(
               ParticleTypes.END_ROD,
               player.getX() + Math.cos(angle) * distance,
               centreY + (random.nextDouble() - 0.5) * 2.0 * half,
               player.getZ() + Math.sin(angle) * distance,
               -Math.cos(angle) * 0.28,
               0.0,
               -Math.sin(angle) * 0.28
            );
         }
      }
   }

   public static float chargeProgress() {
      return (float)chargeTicks / 40.0F;
   }

   public static boolean isCharging() {
      return chargeTicks > 0 && releaseTicks <= 0;
   }

   public static boolean isReleased() {
      return releaseTicks > 0;
   }

   private static boolean isVisible() {
      return ClientConfig.loaded() && releaseTicks > 0;
   }

   public static void render(RenderLevelStageEvent event) {
      if (isVisible() && event.getStage() == Stage.AFTER_PARTICLES) {
         Minecraft minecraft = Minecraft.getInstance();
         Player player = minecraft.player;
         if (player != null) {
            float size = player instanceof AbstractClientPlayer local ? AnnihilationRingRenderer.ringScale(local) : 1.0F;
            ClientLevel level = minecraft.level;
            double distanceScale = 0.55 + 0.45 * (double)size;
            double radius = (releaseTicks > 0 ? 2.5600000000000005 : Mth.lerp((double)((float)chargeTicks / 40.0F), 2.32, 1.2000000000000002)) * distanceScale;
            float panelWidth = 0.78F * size;
            float panelHeight = 1.12F * size;
            double panelBottom = 0.35 * distanceScale;
            double spin = Math.toRadians((double)spinDegrees);
            Camera camera = event.getCamera();
            Vec3 cameraPos = camera.getPosition();
            float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
            double playerX = Mth.lerp((double)partialTick, player.xo, player.getX());
            double playerY = Mth.lerp((double)partialTick, player.yo, player.getY());
            double playerZ = Mth.lerp((double)partialTick, player.zo, player.getZ());
            boolean released = releaseTicks > 0;
            float fade = released ? Mth.clamp((float)releaseTicks / 12.0F, 0.0F, 1.0F) : 1.0F;
            int tint = withAlpha(released ? -49088 : -10888961, fade);
            int shown = released ? 6 : Math.max(1, Math.round(6.0F * ((float)chargeTicks / 40.0F)));
            PoseStack poseStack = event.getPoseStack();
            BufferSource buffers = minecraft.renderBuffers().bufferSource();

            for (int index = 0; index < shown; index++) {
               double angle = (Math.PI * 2) * (double)index / 6.0 + spin;
               double offsetX = Math.sin(angle) * radius;
               double offsetZ = Math.cos(angle) * radius;
               poseStack.pushPose();
               poseStack.translate(playerX + offsetX - cameraPos.x, playerY + panelBottom - cameraPos.y, playerZ + offsetZ - cameraPos.z);
               poseStack.mulPose(Axis.YP.rotationDegrees(-camera.getYRot()));
               Matrix4f matrix = poseStack.last().pose();
               if (level != null && releaseTicks > RELEASE_TICKS - BURST_TICKS
                     && emitTick != level.getGameTime()) {
                  emitTick = level.getGameTime();
                  emitRelease(level, player, size);
               }
               if (released) {
               }

               poseStack.popPose();
            }

            buffers.endBatch();
         }
      }
   }

   private static final int BURST_TICKS = 12;

   private static long emitTick = Long.MIN_VALUE;

   private static void emitRelease(ClientLevel level, Player player, float size) {
      RandomSource random = level.random;
      double centreY = player.getY() + (double)player.getBbHeight() * 0.5;
      int motes = 8 + Math.round(8.0F * size);
      for (int i = 0; i < motes; i++) {
         double angle = random.nextDouble() * Math.PI * 2.0;
         double speed = 0.28 + random.nextDouble() * 0.22;
         level.addParticle(
            ParticleTypes.END_ROD,
            player.getX() + Math.cos(angle) * 0.5,
            centreY + (random.nextDouble() - 0.5) * (double)player.getBbHeight() * 0.8,
            player.getZ() + Math.sin(angle) * 0.5,
            Math.cos(angle) * speed,
            (random.nextDouble() - 0.35) * 0.18,
            Math.sin(angle) * speed
         );
      }
      int ring = 18;
      double ringRadius = 1.2 + random.nextDouble() * 0.6;
      for (int i = 0; i < ring; i++) {
         double angle = Math.PI * 2.0 * (double)i / (double)ring;
         level.addParticle(
            ParticleTypes.ELECTRIC_SPARK,
            player.getX() + Math.cos(angle) * ringRadius,
            player.getY() + 0.1,
            player.getZ() + Math.sin(angle) * ringRadius,
            Math.cos(angle) * 0.45,
            0.02,
            Math.sin(angle) * 0.45
         );
      }
   }

   private static void quad(VertexConsumer consumer, Matrix4f matrix, float width, float height, int colour, float vOffset) {
      float half = width * 0.5F;
      vertex(consumer, matrix, -half, height, 0.0F, 1.0F, vOffset, colour);
      vertex(consumer, matrix, half, height, 0.0F, 0.0F, vOffset, colour);
      vertex(consumer, matrix, half, 0.0F, 0.0F, 0.0F, vOffset + 1.0F, colour);
      vertex(consumer, matrix, -half, 0.0F, 0.0F, 1.0F, vOffset + 1.0F, colour);
   }

   private static void vertex(VertexConsumer consumer, Matrix4f matrix, float x, float y, float z, float u, float v, int colour) {
      consumer.addVertex(matrix, x, y, z).setColor(colour).setUv(u, v).setOverlay(OverlayTexture.NO_OVERLAY).setLight(15728880).setNormal(0.0F, 0.0F, 1.0F);
   }

   private static int withAlpha(int argb, float factor) {
      int alpha = Math.round((float)(argb >>> 24 & 0xFF) * factor);
      return alpha << 24 | argb & 16777215;
   }
}
