package com.ccx87.annihilator_type_a.registry;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ATAParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES;
    public static final Supplier<SimpleParticleType> Annihilator_Type_A_Fire;
    public static final Supplier<SimpleParticleType> Annihilator_Type_A_Large_Fire;


    public static void register(IEventBus eventBus) {
        PARTICLE_TYPES.register(eventBus);
    }

    static {
        PARTICLE_TYPES = DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, "annihilator_type_a");
        Annihilator_Type_A_Fire = PARTICLE_TYPES.register("annihilator_type_a_fire", () -> new SimpleParticleType(true));
        Annihilator_Type_A_Large_Fire = PARTICLE_TYPES.register("annihilator_type_a_large_fire", () -> new SimpleParticleType(true));
    }
}
