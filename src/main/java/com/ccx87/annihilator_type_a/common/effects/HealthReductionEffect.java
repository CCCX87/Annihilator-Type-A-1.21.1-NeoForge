package com.ccx87.annihilator_type_a.common.effects;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class HealthReductionEffect extends MobEffect {
    private static final ResourceLocation MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath("annihilator_type_a", "health_reduction");

    public HealthReductionEffect(MobEffectCategory category, int color) {
        super(category, color);
        this.addAttributeModifier(Attributes.MAX_HEALTH, MODIFIER_ID,
                -4.0, AttributeModifier.Operation.ADD_VALUE);
    }
}