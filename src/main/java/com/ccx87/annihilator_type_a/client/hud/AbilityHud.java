package com.ccx87.annihilator_type_a.client.hud;

import com.ccx87.annihilator_type_a.client.AnnihilationClientState;
import com.ccx87.annihilator_type_a.client.ClientConfig;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public final class AbilityHud {
   private static final ResourceLocation ICON = ResourceLocation.fromNamespaceAndPath("dragonsurvival", "abilities/annihilator_type_a/annihilation_formula");
   private static final Component TITLE = Component.translatable("dragon_ability.dragonsurvival.annihilation_moment");
   private static final int WIDTH = 162;
   private static final int HEIGHT = 26;
   private static final int ICON_SIZE = 16;
   private static boolean iconUnavailable;
   private static long cachedTick = Long.MIN_VALUE;
   private static Component levelText = Component.empty();
   private static Component remainingText = Component.empty();
   private static int titleWidth;
   private static int remainingWidth;

   private AbilityHud() {
   }

   public static void render(GuiGraphics graphics) {
      if (ClientConfig.loaded() && (Boolean)ClientConfig.HUD_ENABLED.get() && AnnihilationClientState.isActive()) {
         Minecraft minecraft = Minecraft.getInstance();
         if (AnnihilationClientState.isMechanicalDragon(minecraft.player)) {
            int screenWidth = minecraft.getWindow().getGuiScaledWidth();
            int x = (screenWidth - 162) / 2 + (Integer)ClientConfig.HUD_TOP_OFFSET_X.get();
            int y = (Integer)ClientConfig.HUD_TOP_OFFSET_Y.get();
            long tick = minecraft.level == null ? 0L : minecraft.level.getGameTime();
            if (tick != cachedTick) {
               cachedTick = tick;
               titleWidth = minecraft.font.width(TITLE);
               levelText = Component.translatable("hud.annihilator_type_a.level", new Object[]{AnnihilationClientState.getLevel()});
               remainingText = Component.translatable(
                  "hud.annihilator_type_a.remaining", new Object[]{HudTheme.seconds(AnnihilationClientState.getRemainingTicks())}
               );
               remainingWidth = minecraft.font.width(remainingText);
            }

            draw(graphics, x, y);
         }
      }
   }

   private static void draw(GuiGraphics graphics, int x, int y) {
      Minecraft minecraft = Minecraft.getInstance();
      float progress = AnnihilationClientState.getProgress();
      HudTheme.panel(graphics, x, y, 162, 26);
      HudTheme.sweep(graphics, x + 1, y + 1, 160, 24, (float)(Util.getMillis() % 2000L) / 1000.0F);
      drawIcon(graphics, x + 4, y + 5, 16);
      graphics.drawString(minecraft.font, TITLE, x + 24, y + 5, -1378561, true);
      graphics.drawString(minecraft.font, levelText, x + 28 + titleWidth, y + 5, -9444353, true);
      graphics.drawString(minecraft.font, remainingText, x + 162 - 5 - remainingWidth, y + 5, progress < 0.25F ? -42419 : -1378561, true);
      HudTheme.bar(graphics, x + 24, y + 18, 132, 3, progress, -9444353, HudTheme.lerpColor(-9444353, -42419, 1.0F - progress), -15853536);
   }

   private static void drawIcon(GuiGraphics graphics, int x, int y, int size) {
      if (!iconUnavailable) {
         try {
            graphics.blitSprite(ICON, x, y, size, size);
         } catch (RuntimeException var5) {
            iconUnavailable = true;
         }
      }
   }
}
