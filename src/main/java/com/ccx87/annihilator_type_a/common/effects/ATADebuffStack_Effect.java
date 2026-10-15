package com.ccx87.annihilator_type_a.common.effects;


import com.ccx87.annihilator_type_a.registry.ATAEffects;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;


public class ATADebuffStack_Effect extends MobEffect {
    public ATADebuffStack_Effect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void onEffectStarted(@NotNull LivingEntity entity, int amplifier) {
        if (entity.level().isClientSide()) {
            super.onEffectStarted(entity, amplifier);
        } else {
            MobEffectInstance ATADebuffEffect = entity.getEffect(ATAEffects.ATADebuff);
            MobEffectInstance ATADebuff_Effect = entity.getEffect(ATAEffects.ATADebuffStack);
            MobEffectInstance NewATADebuffEffect = UpgradeATADebuffMovEffect(ATADebuff_Effect, ATADebuffEffect);
            entity.addEffect(NewATADebuffEffect);
            entity.removeEffect(ATAEffects.ATADebuffStack);
            super.onEffectStarted(entity, amplifier);
        }
    }


    private static @NotNull MobEffectInstance UpgradeATADebuffMovEffect(MobEffectInstance ATADebuff_Effect, MobEffectInstance ATADebuffEffect) {

        int newAmplifier = 0;
        if (ATADebuff_Effect != null) {
            newAmplifier += ATADebuff_Effect.getAmplifier();
        }
        if (ATADebuffEffect != null) {
            newAmplifier += ATADebuffEffect.getAmplifier() + 1;
        }

        newAmplifier = Math.min(newAmplifier, 3);

        int newDuration = 100;
        if (ATADebuff_Effect != null) {
            newDuration = ATADebuff_Effect.getDuration();
        }

        return new MobEffectInstance(ATAEffects.ATADebuff, newDuration, newAmplifier, false,true);
    }



}
