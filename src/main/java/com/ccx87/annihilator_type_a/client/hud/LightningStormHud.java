package com.ccx87.annihilator_type_a.client.hud;

import com.ccx87.annihilator_type_a.client.ClientConfig;
import com.ccx87.annihilator_type_a.client.LightningStormPanels;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public final class LightningStormHud {
   private static final Component TITLE = Component.literal("LIGHTNING STORM");
   private static final Component CHARGING = Component.literal("CHARGING");
   private static final Component RELEASED = Component.literal("RELEASED");
   private static final int PANEL_WIDTH = 176;
   private static final int PANEL_HEIGHT = 34;
   private static final int BACKGROUND = -1072949728;
   private static final int TRACK = 1090519039;
   private static final int CYAN = -10888961;
   private static final int RED = -49088;

   private static void renderNumberFlow(GuiGraphics graphics, Minecraft minecraft, int screenHeight, int accent, float progress) {
      int lines = 14;
      int baseY = screenHeight / 2 - lines * 5 - 20;
      long tick = minecraft.level == null ? 0L : minecraft.level.getGameTime();
      int percent = Math.round(progress * 100.0F);

      for (int line = 0; line < lines; line++) {
         long seed = tick / 3L + (long)line;
         int value = (int)(seed * 2654435761L >>> 11) & 65535;
         String text = String.format("%04X %02d%% %04X", value, (percent + line * 7) % 100, value * 31 & 65535);
         int alpha = Math.min(255, 80 + line * 8);
         graphics.drawString(minecraft.font, text, 8, baseY + line * 10, accent & 16777215 | alpha << 24, false);
      }
   }

   private LightningStormHud() {
   }

   public static void render(GuiGraphics graphics, int screenWidth, int screenHeight) {
      if (ClientConfig.loaded() && (Boolean)ClientConfig.LIGHTNING_HUD.get()) {
         boolean released = LightningStormPanels.isReleased();
         if (released || LightningStormPanels.isCharging()) {
            float progress = LightningStormPanels.chargeProgress();
            int accent = released ? -49088 : -10888961;
            int x = (screenWidth - 176) / 2;
            int y = screenHeight - 58;
            Minecraft minecraft = Minecraft.getInstance();
            if (!released) {
               renderNumberFlow(graphics, minecraft, screenHeight, accent, progress);
            }

            int barX = x + 7;
            int barY = y + 21;
            int barWidth = 162;
            int filled = Math.round((float)barWidth * (released ? 1.0F : progress));
            if (filled > 0) {
            }
         }
      }
   }
}
