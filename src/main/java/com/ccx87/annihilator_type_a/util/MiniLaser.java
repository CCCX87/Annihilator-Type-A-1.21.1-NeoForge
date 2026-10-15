package com.ccx87.annihilator_type_a.util;

import com.ccx87.annihilator_type_a.damage.ModDamageTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;

public class MiniLaser {
    public static void MiniLaserDamage(LivingEntity target, float amount, Entity source, @Nullable Entity owner) {
        if (target.level() instanceof ServerLevel level) {
            DamageSources damageSources = level.damageSources();
            Entity attacker = (owner != null) ? owner : source;
            DamageSource damageSource;
            if (attacker instanceof Player player) {
                damageSource = damageSources.playerAttack(player);
            } else if (attacker instanceof LivingEntity living) {
                damageSource = damageSources.mobAttack(living);
            } else {
                var holder = level.registryAccess()
                        .registryOrThrow(Registries.DAMAGE_TYPE)
                        .getHolderOrThrow(ModDamageTypes.MINI_LASER);
                damageSource = new DamageSource(holder, source, attacker);
            }
            target.hurt(damageSource, amount);
        }
    }
}
