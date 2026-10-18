package com.ccx87.annihilator_type_a.client;

import com.ccx87.annihilator_type_a.registry.ATAEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;

public final class AnnihilationClientState {
   private static boolean active;
   private static int level = 1;
   private static int remainingTicks;
   private static int totalTicks;

   private AnnihilationClientState() {
   }

   public static void tick(Minecraft minecraft) {
      Player player = minecraft.player;
      MobEffectInstance instance = player == null ? null : player.getEffect(ATAEffects.IGNORE_INVINCIBILITY);
      if (instance == null) {
         active = false;
         level = 1;
         remainingTicks = 0;
         totalTicks = 0;
      } else {
         int duration = instance.getDuration();
         if (active && duration <= remainingTicks) {
            totalTicks = Math.max(totalTicks, duration);
         } else {
            totalTicks = Math.max(duration, 1);
         }

         active = true;
         remainingTicks = duration;
         level = instance.getAmplifier() + 1;
      }
   }

   public static boolean isActive() {
      return active;
   }

   public static boolean isMechanicalDragon(Player player) {
      return player != null && player.hasEffect(ATAEffects.RANDOM_FAULT);
   }

   public static int getLevel() {
      return level;
   }

   public static int getRemainingTicks() {
      return remainingTicks;
   }

   public static int getTotalTicks() {
      return totalTicks;
   }

   public static float getRemainingSeconds() {
      return (float)remainingTicks / 20.0F;
   }

   public static float getProgress() {
      return totalTicks <= 0 ? 0.0F : Mth.clamp((float)remainingTicks / (float)totalTicks, 0.0F, 1.0F);
   }
}
