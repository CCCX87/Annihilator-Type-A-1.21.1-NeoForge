package com.ccx87.annihilator_type_a.damage;


import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;


import static com.ccx87.annihilator_type_a.AnnihilatorTypeA.MOD_ID;

public class ModDamageTypes {


    public static final ResourceKey<DamageType> MY_LASER = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "laser")
    );

    public static final ResourceKey<DamageType> ANNIHILATOR_BREATH = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "annihilator_breath")
    );

    public static final ResourceKey<DamageType> MINI_LASER = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "mini_laser")
    );

    public static final ResourceKey<DamageType> LIGHTNING_STORM = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "lightning_storm"));

    public static final ResourceKey<DamageType> GN001_SWORD = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "gn001_sword"));

    public static final ResourceKey<DamageType> MARK_ADJUDICATE = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(MOD_ID, "mark_adjudicate"));


}
