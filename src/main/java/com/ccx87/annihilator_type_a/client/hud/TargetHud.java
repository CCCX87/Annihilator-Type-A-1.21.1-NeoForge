package com.ccx87.annihilator_type_a.client.hud;

import com.ccx87.annihilator_type_a.client.AnnihilationClientState;
import com.ccx87.annihilator_type_a.client.ClientConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public final class TargetHud {
   private static final int WIDTH = 118;
   private static final int DETAIL_HEIGHT = 30;
   private static final int COMPACT_HEIGHT = 22;
   private static final int CHIP = 7;
   private static final int CHIP_STRIDE = 8;
   private static LivingEntity cachedTarget;
   private static String cachedName = "";
   private static String cachedHealth = "";
   private static float cachedRatio;
   private static String cachedAttributes = "";
   private static List<MobEffectInstance> cachedEffects = List.of();
   private static boolean cachedValid;
   private static long cachedTick = Long.MIN_VALUE;

   private TargetHud() {
   }

   public static void render(GuiGraphics graphics) {
      if (ClientConfig.loaded() && (Boolean)ClientConfig.HUD_ENABLED.get() && (Boolean)ClientConfig.HUD_TARGET_ENABLED.get()) {
         if (!(Boolean)ClientConfig.HUD_TARGET_ONLY_WHILE_ACTIVE.get() || AnnihilationClientState.isActive()) {
            Minecraft minecraft = Minecraft.getInstance();
            Player player = minecraft.player;
            if (player != null && minecraft.level != null) {
               if (AnnihilationClientState.isMechanicalDragon(player)) {
                  long tick = minecraft.level.getGameTime();
                  if (tick != cachedTick || !cachedValid) {
                     refresh(minecraft, player, tick);
                  }

                  if (cachedValid) {
                     boolean detailed = !cachedAttributes.isEmpty() || !cachedEffects.isEmpty();
                     int height = detailed ? 30 : 22;
                     int screenWidth = minecraft.getWindow().getGuiScaledWidth();
                     int screenHeight = minecraft.getWindow().getGuiScaledHeight();
                     int x = screenWidth - 118 - (Integer)ClientConfig.HUD_RIGHT_OFFSET_X.get();
                     int y = (screenHeight - height) / 2 + (Integer)ClientConfig.HUD_RIGHT_OFFSET_Y.get();
                     draw(graphics, x, y, detailed);
                  }
               }
            }
         }
      }
   }

   private static void draw(GuiGraphics graphics, int x, int y, boolean detailed) {
      Minecraft minecraft = Minecraft.getInstance();
      int height = detailed ? 30 : 22;
      HudTheme.panel(graphics, x, y, 118, height);
      HudTheme.sweep(graphics, x + 1, y + 1, 116, height - 2, (float)(Util.getMillis() % 2400L) / 2400.0F);
      graphics.drawString(minecraft.font, cachedName, x + 5, y + 3, -1378561, false);
      Component healthText = Component.literal(cachedHealth);
      graphics.drawString(minecraft.font, healthText, x + 118 - 5 - minecraft.font.width(healthText), y + 3, -42419, false);
      HudTheme.bar(graphics, x + 5, y + 13, 108, 4, cachedRatio, -50384, HudTheme.lerpColor(-50384, -26005, cachedRatio), -15853536);
      if (detailed) {
         drawDetailRow(graphics, minecraft, cachedEffects, cachedAttributes, x, y + 19);
      }
   }

   private static void refresh(Minecraft minecraft, Player player, long tick) {
      cachedTick = tick;
      LivingEntity target = findTarget(minecraft, player);
      if (target == null) {
         cachedTarget = null;
         cachedValid = false;
      } else {
         cachedTarget = target;
         cachedName = minecraft.font.plainSubstrByWidth(target.getDisplayName().getString(), 106);
         float maxHealth = Math.max((float)attributeOrZero(target, Attributes.MAX_HEALTH), 1.0F);
         cachedHealth = String.format(Locale.ROOT, "%.1f/%.1f", target.getHealth(), maxHealth);
         cachedRatio = Mth.clamp(target.getHealth() / maxHealth, 0.0F, 1.0F);
         cachedAttributes = ClientConfig.HUD_TARGET_ATTRIBUTES.get() ? attributeText(target) : "";
         cachedEffects = (List<MobEffectInstance>)(ClientConfig.HUD_TARGET_BUFFS.get() ? new ArrayList<>(target.getActiveEffects()) : List.of());
         cachedValid = true;
      }
   }

   private static String attributeText(LivingEntity target) {
      StringBuilder text = new StringBuilder();
      int armor = Mth.floor(attributeOrZero(target, Attributes.ARMOR));
      if (armor > 0) {
         text.append(Component.translatable("hud.annihilator_type_a.armor_short", new Object[]{armor}).getString());
      }

      double damage = attributeOrZero(target, Attributes.ATTACK_DAMAGE);
      if (damage > 0.0) {
         if (text.length() > 0) {
            text.append(' ');
         }

         text.append(Component.translatable("hud.annihilator_type_a.attack_short", new Object[]{String.format(Locale.ROOT, "%.1f", damage)}).getString());
      }

      return text.toString();
   }

   private static double attributeOrZero(LivingEntity entity, Holder<Attribute> attribute) {
      return entity.getAttributes().hasAttribute(attribute) ? entity.getAttributeValue(attribute) : 0.0;
   }

   private static void drawDetailRow(GuiGraphics graphics, Minecraft minecraft, List<MobEffectInstance> effects, String attributeText, int x, int rowY) {
      int cursor = x + 5;
      if (!attributeText.isEmpty()) {
         graphics.drawString(minecraft.font, attributeText, cursor, rowY, -6310204, false);
         cursor += minecraft.font.width(attributeText) + 6;
      }

      if (!effects.isEmpty()) {
         int available = x + 118 - 5 - cursor;
         int capacity = Math.max(0, available / 8);
         int shown = Math.min(effects.size(), capacity);

         int hidden;
         for (hidden = effects.size() - shown; hidden > 0 && shown > 0 && shown * 8 + minecraft.font.width("+" + hidden) > available; hidden++) {
            shown--;
         }

         for (int index = 0; index < shown; index++) {
            int color = 0xFF000000 | ((MobEffect)effects.get(index).getEffect().value()).getColor();
            int chipX = cursor + index * 8;
            graphics.fill(chipX, rowY, chipX + 7, rowY + 7, color);
            HudTheme.outline(graphics, chipX, rowY, 7, 7, 1627389951);
         }

         if (hidden > 0) {
            Component more = Component.literal("+" + hidden);
            int moreX = Math.min(cursor + shown * 8, x + 118 - 5 - minecraft.font.width(more));
            graphics.drawString(minecraft.font, more, moreX, rowY, -6310204, false);
         }
      }
   }

   private static LivingEntity findTarget(Minecraft minecraft, Player player) {
      double range = (Double)ClientConfig.HUD_TARGET_RANGE.get();
      float partialTick = 1.0F;
      Vec3 eye = player.getEyePosition(partialTick);
      Vec3 look = player.getViewVector(partialTick);
      Vec3 reach = eye.add(look.scale(range));
      BlockHitResult blockHit = minecraft.level.clip(new ClipContext(eye, reach, Block.COLLIDER, Fluid.NONE, player));
      Vec3 end = blockHit.getType() == Type.MISS ? reach : blockHit.getLocation();
      AABB searchBox = player.getBoundingBox().expandTowards(look.scale(range)).inflate(1.0);
      EntityHitResult hit = ProjectileUtil.getEntityHitResult(
         minecraft.level,
         player,
         eye,
         end,
         searchBox,
         entity -> entity instanceof LivingEntity && entity.isAlive() && entity != player && !entity.isSpectator()
      );
      if (hit != null) {
         Entity var13 = hit.getEntity();
         if (var13 instanceof LivingEntity) {
            return (LivingEntity)var13;
         }
      }

      return null;
   }
}
