package com.ccx87.annihilator_type_a.client.hud;

import com.ccx87.annihilator_type_a.client.ClientConfig;
import com.ccx87.annihilator_type_a.registry.ATAEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public final class LightningStormWarning {
   private static final int WARNING_TICKS = 45;
   private static final Component MESSAGE = Component.literal("WARNING: LIGHTNING STORM CREATED");
   private static final int RED = -50384;
   private static final int RED_DIM = 1442790192;
   private static int ticksLeft;
   private static boolean wasActive;

   private LightningStormWarning() {
   }

   public static void tick(Minecraft minecraft) {
      Player player = minecraft.player;
      boolean active = player != null && player.hasEffect(ATAEffects.LIGHTNING_STORM);
      if (active && !wasActive) {
         ticksLeft = 45;
      }

      wasActive = active;
      if (ticksLeft > 0) {
         ticksLeft--;
      }
   }

   public static boolean isVisible() {
      return ticksLeft > 0 && ClientConfig.loaded() && (Boolean)ClientConfig.LIGHTNING_WARNING.get();
   }

   public static void render(GuiGraphics graphics, int screenWidth, int screenHeight) {
      if (isVisible()) {
         boolean blink = ticksLeft > 22 && ticksLeft / 4 % 2 == 0;
         int alpha = blink ? 255 : 204;
         Minecraft minecraft = Minecraft.getInstance();
         int textWidth = minecraft.font.width(MESSAGE);
         int centreY = screenHeight / 3;
         int half = textWidth / 2;
         graphics.fill(screenWidth / 2 - half - 10, centreY - 8, screenWidth / 2 + half + 10, centreY + 12, 1442790192);
         graphics.fill(screenWidth / 2 - half - 10, centreY - 8, screenWidth / 2 + half + 10, centreY - 7, -50384);
         graphics.fill(screenWidth / 2 - half - 10, centreY + 11, screenWidth / 2 + half + 10, centreY + 12, -50384);
         graphics.drawCenteredString(minecraft.font, MESSAGE, screenWidth / 2, centreY, 16726832 | alpha << 24);
      }
   }
}
