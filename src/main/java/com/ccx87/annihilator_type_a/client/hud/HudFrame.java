package com.ccx87.annihilator_type_a.client.hud;

import com.ccx87.annihilator_type_a.client.AnnihilationClientState;
import com.ccx87.annihilator_type_a.client.ClientConfig;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

public final class HudFrame {
   private static final int CONTOUR_THICKNESS = 2;
   private static final int ECHO_THICKNESS = 1;
   private static final int TICK_THICKNESS = 1;
   private static final int MARKER_THICKNESS = 1;
   private static final int ECHO_OFFSET = 10;
   private static final int TICK_HALF = 5;
   private static final int MARKER_SIZE = 6;
   private static final float[] TICK_POSITIONS = new float[]{0.34F, 0.66F};
   private static final int MARKER_RGB = 16730667;
   private static final int FALLBACK_ACCENT_RGB = 5232895;
   private static final float ECHO_ALPHA_FACTOR = 0.4F;
   private static final float TICK_ALPHA_FACTOR = 0.8F;
   private static final float MARKER_ALPHA_FACTOR = 1.15F;
   private static String cachedColorSource = "";
   private static int cachedColorRgb = 5232895;

   private HudFrame() {
   }

   public static void render(GuiGraphics graphics) {
      if (ClientConfig.loaded() && (Boolean)ClientConfig.HUD_ENABLED.get() && (Boolean)ClientConfig.HUD_FRAME.get() && AnnihilationClientState.isActive()) {
         Minecraft minecraft = Minecraft.getInstance();
         if (AnnihilationClientState.isMechanicalDragon(minecraft.player)) {
            int width = minecraft.getWindow().getGuiScaledWidth();
            int height = minecraft.getWindow().getGuiScaledHeight();
            if (width >= 64 && height >= 64) {
               int insetX = (int)((float)width * Mth.clamp(((Double)ClientConfig.HUD_FRAME_INSET_X.get()).floatValue(), 0.0F, 0.45F));
               int insetY = (int)((float)height * Mth.clamp(((Double)ClientConfig.HUD_FRAME_INSET_Y.get()).floatValue(), 0.0F, 0.45F));
               int x1 = width - insetX - 1;
               int y1 = height - insetY - 1;
               int chamfer = (int)((float)height * Mth.clamp(((Double)ClientConfig.HUD_FRAME_CHAMFER.get()).floatValue(), 0.0F, 0.6F));
               chamfer = Math.min(chamfer, Math.min((x1 - insetX) / 2 - 1, (y1 - insetY) / 2 - 1));
               if (chamfer >= 1) {
                  int[][] ring = new int[][]{
                     {insetX + chamfer, insetY},
                     {x1 - chamfer, insetY},
                     {x1, insetY + chamfer},
                     {x1, y1 - chamfer},
                     {x1 - chamfer, y1},
                     {insetX + chamfer, y1},
                     {insetX, y1 - chamfer},
                     {insetX, insetY + chamfer}
                  };
                  int accent = accentRgb();
                  int alpha = (int)(Mth.clamp(((Double)ClientConfig.HUD_FRAME_ALPHA.get()).floatValue(), 0.0F, 1.0F) * 255.0F);
                  int contourColor = argb(alpha, accent);
                  int echoColor = argb((int)((float)alpha * 0.4F), accent);
                  int tickColor = argb((int)((float)alpha * 0.8F), accent);
                  int markerColor = argb((int)Math.min(255.0F, (float)alpha * 1.15F), 16730667);
                  int centreX = (insetX + x1) / 2;
                  int centreY = (insetY + y1) / 2;
                  int[][] echo = new int[ring.length][];

                  for (int index = 0; index < ring.length; index++) {
                     echo[index] = pushedOutward(ring[index], centreX, centreY, 10);
                  }

                  stroke(graphics, echo, 1, echoColor);
                  stroke(graphics, ring, 2, contourColor);

                  for (int index = 0; index < ring.length; index++) {
                     int[] a = ring[index];
                     int[] b = ring[(index + 1) % ring.length];
                     if (a[0] == b[0] || a[1] == b[1]) {
                        double nx = (double)(-(b[1] - a[1]));
                        double ny = (double)(b[0] - a[0]);
                        double length = Math.hypot(nx, ny);
                        if (!(length < 1.0E-4)) {
                           nx /= length;
                           ny /= length;

                           for (float position : TICK_POSITIONS) {
                              int px = Math.round((float)a[0] + (float)(b[0] - a[0]) * position);
                              int py = Math.round((float)a[1] + (float)(b[1] - a[1]) * position);
                              drawLine(
                                 graphics,
                                 px - (int)Math.round(nx * 5.0),
                                 py - (int)Math.round(ny * 5.0),
                                 px + (int)Math.round(nx * 5.0),
                                 py + (int)Math.round(ny * 5.0),
                                 1,
                                 tickColor
                              );
                           }
                        }
                     }
                  }

                  drawMarker(graphics, ring, 0, 0.42F, markerColor);
                  drawMarker(graphics, ring, 4, 0.58F, markerColor);
                  drawMarker(graphics, ring, 2, 0.5F, markerColor);
               }
            }
         }
      }
   }

   private static void stroke(GuiGraphics graphics, int[][] ring, int thickness, int color) {
      for (int index = 0; index < ring.length; index++) {
         int[] a = ring[index];
         int[] b = ring[(index + 1) % ring.length];
         drawLine(graphics, a[0], a[1], b[0], b[1], thickness, color);
      }
   }

   private static void drawMarker(GuiGraphics graphics, int[][] ring, int edge, float position, int color) {
      int[] a = ring[edge];
      int[] b = ring[(edge + 1) % ring.length];
      int cx = Math.round((float)a[0] + (float)(b[0] - a[0]) * position);
      int cy = Math.round((float)a[1] + (float)(b[1] - a[1]) * position);
      drawLine(graphics, cx, cy - 6, cx - 6, cy + 6, 1, color);
      drawLine(graphics, cx - 6, cy + 6, cx + 6, cy + 6, 1, color);
      drawLine(graphics, cx + 6, cy + 6, cx, cy - 6, 1, color);
   }

   private static void drawLine(GuiGraphics graphics, int x0, int y0, int x1, int y1, int thickness, int color) {
      int dx = x1 - x0;
      int dy = y1 - y0;
      if (dx != 0 || dy != 0) {
         float length = Mth.sqrt((float)(dx * dx + dy * dy));
         float angle = (float)Math.toDegrees(Math.atan2((double)dy, (double)dx));
         int half = thickness / 2;
         PoseStack pose = graphics.pose();
         pose.pushPose();
         pose.translate((float)x0, (float)y0, 0.0F);
         pose.mulPose(Axis.ZP.rotationDegrees(angle));
         graphics.fill(0, -half, Math.round(length), -half + thickness, color);
         pose.popPose();
      }
   }

   private static int[] pushedOutward(int[] point, int centreX, int centreY, int distance) {
      double dx = (double)(point[0] - centreX);
      double dy = (double)(point[1] - centreY);
      double length = Math.hypot(dx, dy);
      return length < 1.0E-4
         ? new int[]{point[0], point[1]}
         : new int[]{(int)Math.round((double)point[0] + dx / length * (double)distance), (int)Math.round((double)point[1] + dy / length * (double)distance)};
   }

   private static int argb(int alpha, int rgb) {
      return Mth.clamp(alpha, 0, 255) << 24 | rgb & 16777215;
   }

   private static int accentRgb() {
      String value = (String)ClientConfig.HUD_FRAME_COLOR.get();
      if (!value.equals(cachedColorSource)) {
         cachedColorSource = value;
         cachedColorRgb = parseHex(value);
      }

      return cachedColorRgb;
   }

   private static int parseHex(String value) {
      try {
         String cleaned = value.startsWith("#") ? value.substring(1) : value;
         return Integer.parseInt(cleaned.trim(), 16) & 16777215;
      } catch (RuntimeException var2) {
         return 5232895;
      }
   }
}
