package com.ccx87.annihilator_type_a.common.effects;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class ArmorBreak extends MobEffect {

    private static final ResourceLocation ARMOR_MODIFIER_ID =
            ResourceLocation.fromNamespaceAndPath("annihilator_type_a", "armor_break");

    public ArmorBreak(MobEffectCategory category, int color) {
        super(category, color);
        this.addAttributeModifier(Attributes.ARMOR, ARMOR_MODIFIER_ID,
                -10.0, AttributeModifier.Operation.ADD_VALUE);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 20 == 0;
    }
}