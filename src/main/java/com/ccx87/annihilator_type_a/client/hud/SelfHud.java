package com.ccx87.annihilator_type_a.client.hud;

import com.ccx87.annihilator_type_a.client.AnnihilationClientState;
import com.ccx87.annihilator_type_a.client.ClientConfig;
import java.util.Locale;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public final class SelfHud {
   private static final int WIDTH = 104;
   private static final int ROW = 9;
   private static final int HEIGHT = 48;
   private static final Component LABEL_HEALTH = Component.translatable("hud.annihilator_type_a.health");
   private static final Component LABEL_ABSORPTION = Component.translatable("hud.annihilator_type_a.absorption");
   private static final Component LABEL_ATTACK = Component.translatable("hud.annihilator_type_a.attack");
   private static final Component LABEL_ATTACK_SPEED = Component.translatable("hud.annihilator_type_a.attack_speed");
   private static long cachedTick = Long.MIN_VALUE;
   private static String healthValue = "";
   private static String absorptionValue = "-";
   private static String attackValue = "";
   private static String speedValue = "";
   private static float healthRatio;
   private static float absorptionRatio;

   private SelfHud() {
   }

   public static void render(GuiGraphics graphics) {
      if (ClientConfig.loaded() && (Boolean)ClientConfig.HUD_ENABLED.get()) {
         Minecraft minecraft = Minecraft.getInstance();
         Player player = minecraft.player;
         if (player != null) {
            if (AnnihilationClientState.isMechanicalDragon(player)) {
               if (AnnihilationClientState.isActive() || !(Boolean)ClientConfig.HUD_SELF_ONLY_WHILE_ACTIVE.get()) {
                  long tick = player.level().getGameTime();
                  if (tick != cachedTick) {
                     refresh(player, tick);
                  }

                  int screenHeight = minecraft.getWindow().getGuiScaledHeight();
                  int x = (Integer)ClientConfig.HUD_LEFT_OFFSET_X.get();
                  int y = (screenHeight - 48) / 2 + (Integer)ClientConfig.HUD_LEFT_OFFSET_Y.get();
                  draw(graphics, x, y);
               }
            }
         }
      }
   }

   private static void draw(GuiGraphics graphics, int x, int y) {
      Minecraft minecraft = Minecraft.getInstance();
      HudTheme.panel(graphics, x, y, 104, 48);
      HudTheme.sweep(graphics, x + 1, y + 1, 102, 46, (float)(Util.getMillis() % 2400L) / 2400.0F);
      int rowY = y + 4;
      row(graphics, minecraft, x, rowY, LABEL_HEALTH, healthValue, -1378561);
      HudTheme.bar(graphics, x + 5, rowY + 9, 94, 3, healthRatio, -45747, -23701, -15853536);
      if (absorptionRatio > 0.0F) {
         int absorptionWidth = Math.round(94.0F * absorptionRatio);
         graphics.fill(x + 5, rowY + 9, x + 5 + absorptionWidth, rowY + 11, -10934);
      }

      rowY += 14;
      row(graphics, minecraft, x, rowY, LABEL_ABSORPTION, absorptionValue, absorptionRatio > 0.0F ? -10934 : -6310204);
      rowY += 9;
      row(graphics, minecraft, x, rowY, LABEL_ATTACK, attackValue, -1378561);
      rowY += 9;
      row(graphics, minecraft, x, rowY, LABEL_ATTACK_SPEED, speedValue, -1378561);
   }

   private static void refresh(Player player, long tick) {
      cachedTick = tick;
      float health = player.getHealth();
      float maxHealth = Math.max(player.getMaxHealth(), 1.0F);
      float absorption = player.getAbsorptionAmount();
      healthValue = String.format(Locale.ROOT, "%.1f/%.1f", health, maxHealth);
      healthRatio = Mth.clamp(health / maxHealth, 0.0F, 1.0F);
      absorptionValue = absorption > 0.0F ? String.format(Locale.ROOT, "%.1f", absorption) : "-";
      absorptionRatio = absorption > 0.0F ? Mth.clamp(absorption / maxHealth, 0.0F, 1.0F) : 0.0F;
      attackValue = String.format(Locale.ROOT, "%.1f", player.getAttributeValue(Attributes.ATTACK_DAMAGE));
      speedValue = String.format(Locale.ROOT, "%.2f", player.getAttributeValue(Attributes.ATTACK_SPEED));
   }

   private static void row(GuiGraphics graphics, Minecraft minecraft, int x, int y, Component label, String value, int valueColor) {
      graphics.drawString(minecraft.font, label, x + 5, y, -9444353, false);
      Component text = Component.literal(value);
      graphics.drawString(minecraft.font, text, x + 104 - 5 - minecraft.font.width(text), y, valueColor, false);
   }
}
