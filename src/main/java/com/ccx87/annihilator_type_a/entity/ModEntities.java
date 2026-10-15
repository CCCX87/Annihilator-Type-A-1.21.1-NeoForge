package com.ccx87.annihilator_type_a.entity;

import com.ccx87.annihilator_type_a.entity.custom.Gn001Entity;
import com.ccx87.annihilator_type_a.entity.custom.Gn001Projectile;
import com.ccx87.annihilator_type_a.entity.custom.MiniatureUAVEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;

import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static com.ccx87.annihilator_type_a.AnnihilatorTypeA.MOD_ID;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE,MOD_ID);

    public static final Supplier<EntityType<MiniatureUAVEntity>> MINIATUREUAV =
            ENTITY_TYPES.register("miniature_uav",
                    () -> EntityType.Builder.of(MiniatureUAVEntity::new, MobCategory.CREATURE)
                            .sized(0.5f, 0.5f).build("miniature_uav"));


    public static final Supplier<EntityType<Gn001Entity>> GN001 =
            ENTITY_TYPES.register("gn_001",
                    () -> EntityType.Builder.of(Gn001Entity::new, MobCategory.CREATURE)
                            .sized(2f, 5.0f).build("gn_001"));

    public static final Supplier<EntityType<Gn001Projectile>> GN001_PROJECTILE =
            ENTITY_TYPES.register("gn001_projectile",
                    () -> EntityType.Builder.<Gn001Projectile>of(Gn001Projectile::new, MobCategory.MISC)
                            .sized(0.3F, 0.3F)
                            .clientTrackingRange(64)
                            .updateInterval(1)
                            .build("gn001_projectile"));

    public static void register(IEventBus eventBus){
        ENTITY_TYPES.register(eventBus);
    }
}
