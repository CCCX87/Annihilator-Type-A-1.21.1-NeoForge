package com.ccx87.annihilator_type_a.common.handler;

import com.ccx87.annihilator_type_a.damage.ModDamageTypes;
import com.ccx87.annihilator_type_a.registry.ATAEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.ProjectileImpactEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.ccx87.annihilator_type_a.AnnihilatorTypeA.MOD_ID;

@EventBusSubscriber(modid = MOD_ID)
public class LightningStormHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(LightningStormHandler.class);
    private static final ConcurrentHashMap<UUID, Boolean> activeStorms = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private static final List<ScheduledFuture<?>> PENDING = new CopyOnWriteArrayList<>();

    @SubscribeEvent
    public static void onServerStopping(net.neoforged.neoforge.event.server.ServerStoppingEvent event) {
        for (ScheduledFuture<?> future : PENDING) {
            future.cancel(false);
        }
        PENDING.clear();
        activeStorms.clear();
    }

    private static void scheduleTask(Runnable body, long delayMillis) {
        PENDING.removeIf(ScheduledFuture::isDone);
        PENDING.add(scheduler.schedule(() -> {
            try {
                body.run();
            } catch (Throwable throwable) {
                LOGGER.warn("Lightning storm task failed", throwable);
            }
        }, delayMillis, TimeUnit.MILLISECONDS));
    }

    private static void runOnServer(ServerLevel level, Runnable body) {
        var server = level.getServer();
        if (server == null || !server.isRunning()) {
            return;
        }
        server.execute(() -> {
            if (!server.isRunning()) {
                return;
            }
            try {
                body.run();
            } catch (Throwable throwable) {
                LOGGER.warn("Lightning storm task failed on the server thread", throwable);
            }
        });
    }

    @SubscribeEvent
    public static void onProjectileHit(ProjectileImpactEvent event) {


        Projectile projectile = event.getProjectile();

        String entityTypeKey = EntityType.getKey(projectile.getType()).toString();
        if (!(projectile.getOwner() instanceof Player player)) {
            return;
        }
        if (player.level().isClientSide()) {
            return;
        }
        MobEffectInstance effect = player.getEffect(ATAEffects.LIGHTNING_STORM);
        if (effect == null) {
            return;
        }
        if (!"dragonsurvival:generic_ball_entity".equals(entityTypeKey)) {
            return;
        }
        projectile.discard();
        event.setCanceled(true);


        int levelScore = 0;
        effect = player.getEffect(ATAEffects.LIGHTNING_STORM);
        if (effect != null) {
            levelScore = effect.getAmplifier() + 1;
        }

        Vec3 center = projectile.position();


        // 根据等级调整参数
        float radius = 16.0f + (levelScore - 1) * 2.0f;//范围
        float baseDamage = 0.0f ;//基础伤害
        float attackDamage = (float) ((float) player.getAttributeValue(Attributes.ATTACK_DAMAGE) * 0.35);// 玩家攻击力
        float attackBonus = (levelScore - 1) * attackDamage * 0.05f;//攻击力额外加成
        float damage = baseDamage + attackDamage + attackBonus;
        int duration = 200  + (levelScore - 1) * 20;//时长
        double frequency = 5 ;//频率

        startStorm((ServerLevel) player.level(), center, duration, (int) frequency, radius, damage, player.getUUID());
    }
    public static void startStorm(ServerLevel level, Vec3 center, int duration, int interval,
                                  float radius, float damage, UUID casterUUID) {
        activeStorms.put(casterUUID, true);

        //天气
        runOnServer(level, () -> level.setWeatherParameters(10, duration + 20, true, true));

        Player caster = level.getPlayerByUUID(casterUUID);
        DamageSource damageSource;
        if (caster != null) {
            damageSource = buildDamageSource(level, caster);
        } else {
            damageSource = level.damageSources().lightningBolt();
        }

        int strikes = duration / interval;
        for (int i = 0; i < strikes; i++) {
            int delay = 30 + i * interval;
            scheduleTask(() -> {
                if (!activeStorms.containsKey(casterUUID)) return;

                runOnServer(level, () -> {
                    double angle = level.random.nextDouble() * 2 * Math.PI;
                    double dist = level.random.nextDouble() * radius;
                    double dx = Math.cos(angle) * dist;
                    double dz = Math.sin(angle) * dist;
                    BlockPos strikePos = new BlockPos(
                            (int) (center.x + dx),
                            (int) (center.y-2),
                            (int) (center.z + dz)
                    );
                    //生成闪电
                    LightningBolt bolt = EntityType.LIGHTNING_BOLT.create(level);
                    if (bolt != null) {
                        bolt.setPos(strikePos.getX(), strikePos.getY(), strikePos.getZ());
                        bolt.setVisualOnly(true);
                        level.addFreshEntity(bolt);
                    }
                    //粒子效果
                    for (int j = 0; j < 10; j++) {
                        double height = j * 1.5;
                        level.sendParticles(ParticleTypes.END_ROD,
                                strikePos.getX(), center.y + height, strikePos.getZ(),
                                2, 0.3, 0.3, 0.3, 0.05);
                    }
                    int ringParticles = 16;
                    for (int j = 0; j < ringParticles; j++) {
                        double ringAngle = (j / (double) ringParticles) * 2 * Math.PI + level.random.nextDouble() * 0.5;
                        double ringRadius = 2.0 + level.random.nextDouble() * 1.5;
                        double rx = Math.cos(ringAngle) * ringRadius;
                        double rz = Math.sin(ringAngle) * ringRadius;
                        level.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                                strikePos.getX() + rx, center.y + 1 + level.random.nextDouble() * 4,
                                strikePos.getZ() + rz,
                                3, 0.2, 0.2, 0.2, 0.02);
                    }
                    level.sendParticles(ParticleTypes.FIREWORK,
                            strikePos.getX(), center.y + 0.5, strikePos.getZ(),
                            5, 1.0, 0.5, 1.0, 0.1);

                    //造成伤害
                    AABB aabb = new AABB(
                            center.x - radius, center.y - radius, center.z - radius,
                            center.x + radius, center.y + radius, center.z + radius
                    );
                    List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, aabb,
                            e -> !e.getUUID().equals(casterUUID) && e.isAlive());
                    for (LivingEntity entity : entities) {
                        entity.hurt(damageSource, damage);
                    }
                });
            }, delay * 50L);
        }

        long cleanupDelay = duration * 50L + 1000;
        scheduleTask(() -> {
            activeStorms.remove(casterUUID);
            runOnServer(level, () -> level.setWeatherParameters(0, 0, false, false));
            }, cleanupDelay);
    }
    private static DamageSource buildDamageSource(ServerLevel level, Player caster) {
        var damageTypeHolder = level.registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolder(ModDamageTypes.LIGHTNING_STORM);

        if (damageTypeHolder.isPresent()) {
            return new DamageSource(damageTypeHolder.get(), caster);
        } else {
            return level.damageSources().lightningBolt();
        }
    }
}