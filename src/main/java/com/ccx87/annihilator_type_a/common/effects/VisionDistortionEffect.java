package com.ccx87.annihilator_type_a.common.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

public class VisionDistortionEffect extends MobEffect {
    public VisionDistortionEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {

        return duration % 20 == 0;
    }

    @Override
    public boolean applyEffectTick(LivingEntity entity, int amplifier) {
        if (!entity.level().isClientSide) {

            AABB area = entity.getBoundingBox().inflate(64);
            entity.level().getEntitiesOfClass(LivingEntity.class, area).forEach(target -> {
                if (target != entity) {
                    target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 60, 0));
                }
            });
        }

        return true;
    }
}