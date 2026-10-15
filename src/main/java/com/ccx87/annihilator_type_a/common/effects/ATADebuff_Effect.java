package com.ccx87.annihilator_type_a.common.effects;


import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;




public class ATADebuff_Effect extends MobEffect {

    private static final ResourceLocation ARMOR_MODIFIER_UUID = ResourceLocation.fromNamespaceAndPath("armorbreak", "armor_break");

    public ATADebuff_Effect(MobEffectCategory category, int color) {
        super(category, color);
        this.addAttributeModifier(Attributes.ARMOR, ARMOR_MODIFIER_UUID, -5.0, AttributeModifier.Operation.ADD_VALUE);
    }


    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return duration % 3 == 0;
    }



}

