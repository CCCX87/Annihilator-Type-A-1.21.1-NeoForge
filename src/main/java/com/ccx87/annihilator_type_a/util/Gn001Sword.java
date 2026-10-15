package com.ccx87.annihilator_type_a.util;

import com.ccx87.annihilator_type_a.damage.ModDamageTypes;
import javax.annotation.Nullable;
import net.minecraft.core.Holder.Reference;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class Gn001Sword {
   public static void Gn001SwordDamage(LivingEntity target, float amount, Entity source, @Nullable Entity owner) {
      if (!(target instanceof Player)) {
         if (target.level() instanceof ServerLevel level) {
            DamageSources damageSources = level.damageSources();
            Entity attacker = owner != null ? owner : source;
            DamageSource damageSource;
            if (attacker instanceof Player player) {
               damageSource = damageSources.playerAttack(player);
            } else if (attacker instanceof LivingEntity living) {
               damageSource = damageSources.mobAttack(living);
            } else {
               Reference<DamageType> holder = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE).getHolderOrThrow(ModDamageTypes.GN001_SWORD);
               damageSource = new DamageSource(holder, source, attacker);
            }

            target.hurt(damageSource, amount);
         }
      }
   }
}
