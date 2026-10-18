package com.ccx87.annihilator_type_a.common.handler;

import com.ccx87.annihilator_type_a.damage.ModDamageTypes;
import com.ccx87.annihilator_type_a.network.MarkAdjudicatePayload;
import com.ccx87.annihilator_type_a.registry.ATAEffects;
import com.ccx87.annihilator_type_a.util.MarkAdjudicateAim;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import static com.ccx87.annihilator_type_a.AnnihilatorTypeA.MOD_ID;

@EventBusSubscriber(modid = MOD_ID)
public final class MarkAdjudicateStrikeHandler {
    private static final int ARROW_DELAY = 100;
    private static final int LASER_DELAY = 130;
    private static final int LASER_PULSES = 50;
    private static final int LASER_INTERVAL = 2;

    private static final double ARROW_RADIUS = 3.0D;
    private static final double DAMAGE_RADIUS = 8.0D;
    private static final double PULSE_RADIUS = 7.0D;
    private static final double BLOCK_RADIUS = 4.5D;
    private static final double CRATER_DEPTH = 8.0D;
    private static final double BEAM_CLEAR_HEIGHT = 120.0D;
    private static final double BEAM_CLEAR_RADIUS = 4.5D;
    private static final int MAX_BLOCKS = 6000;
    private static final int BLOCKS_PER_TICK = 120;
    private static final int SCAN_PER_TICK = 1200;

    //攻击面板
    private static final float ARROW_DAMAGE = 30.0F;
    private static final float ARROW_PER_LEVEL = 15.0F;
    private static final float LASER_DAMAGE = 50.0F;
    private static final float LASER_PER_LEVEL = 20.0F;
    private static final float PULSE_DAMAGE = 4F;
    private static final float PULSE_PER_LEVEL = 1.5F;

    private static final int STAGE_ARROW = 0;
    private static final int STAGE_LASER = 1;
    private static final int STAGE_PULSE = 2;

    private static final List<Pending> PENDING = new ArrayList<>();
    private static final List<Crater> CRATERS = new ArrayList<>();

    private MarkAdjudicateStrikeHandler() {}


    @SubscribeEvent
    public static void onServerStopping(net.neoforged.neoforge.event.server.ServerStoppingEvent event) {
        PENDING.clear();
        CRATERS.clear();
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        MinecraftServer server = event.getServer();
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            MobEffectInstance marker = player.getEffect(ATAEffects.MARK_ADJUDICATE_IMPACT);
            if (marker == null) {
                continue;
            }
            player.removeEffect(ATAEffects.MARK_ADJUDICATE_IMPACT);
            queue(player, marker.getAmplifier() + 1);
        }
        tickPending();
        tickCraters();
    }

    private static void queue(ServerPlayer player, int abilityLevel) {
        ServerLevel world = player.serverLevel();
        Vec3 point = MarkAdjudicateAim.resolve(world, player);
        Vec3 ground = groundUnder(world, point);
        PacketDistributor.sendToPlayersNear(world, player, point.x, point.y, point.z, 128.0D,
                new MarkAdjudicatePayload(player.getId(), point.x, point.y, point.z,
                        ground.x, ground.y, ground.z));
        PENDING.add(new Pending(world, player.getUUID(), point, Math.max(1, abilityLevel), ARROW_DELAY));
    }

    private static void tickPending() {
        if (PENDING.isEmpty()) {
            return;
        }
        for (Iterator<Pending> iterator = PENDING.iterator(); iterator.hasNext(); ) {
            Pending pending = iterator.next();
            if (--pending.ticks > 0) {
                continue;
            }
            switch (pending.stage) {
                case STAGE_ARROW -> {
                    arrowStrike(pending);
                    pending.stage = STAGE_LASER;
                    pending.ticks = LASER_DELAY - ARROW_DELAY;
                }
                case STAGE_LASER -> {
                    laserStrike(pending);
                    pending.stage = STAGE_PULSE;
                    pending.pulses = LASER_PULSES;
                    pending.ticks = LASER_INTERVAL;
                }
                default -> {
                    pulseStrike(pending);
                    if (--pending.pulses <= 0) {
                        iterator.remove();
                        continue;
                    }
                    pending.ticks = LASER_INTERVAL;
                }
            }
        }
    }

    private static void arrowStrike(Pending pending) {
        ServerLevel level = pending.world;
        float damage = ARROW_DAMAGE + ARROW_PER_LEVEL * Math.max(0, pending.level - 1);
        hurt(level, pending, pending.point, ARROW_RADIUS, damage, 0.08D);
        level.sendParticles(ParticleTypes.END_ROD,
                pending.point.x, pending.point.y + 0.6D, pending.point.z, 18, 1.2D, 1.2D, 1.2D, 0.2D);
        level.sendParticles(ParticleTypes.CRIT,
                pending.point.x, pending.point.y + 0.6D, pending.point.z, 16, 1.0D, 1.0D, 1.0D, 0.3D);
    }

    private static void laserStrike(Pending pending) {
        ServerLevel level = pending.world;
        float damage = LASER_DAMAGE + LASER_PER_LEVEL * Math.max(0, pending.level - 1);
        hurt(level, pending, pending.point, DAMAGE_RADIUS, damage, 0.15D);

        Vec3 ground = groundUnder(level, pending.point);
        if (com.ccx87.annihilator_type_a.common.ServerConfig.markAdjudicateDestroysBlocks()) {
            CRATERS.add(new Crater(level, ground, pending.caster));
        }

        level.sendParticles(ParticleTypes.END_ROD,
                ground.x, ground.y + 1.0D, ground.z, 70, 4.0D, 4.0D, 4.0D, 0.35D);
        level.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                ground.x, ground.y + 2.0D, ground.z, 60, 3.0D, 4.0D, 3.0D, 0.5D);
    }

    private static void pulseStrike(Pending pending) {
        ServerLevel level = pending.world;
        float damage = PULSE_DAMAGE + PULSE_PER_LEVEL * Math.max(0, pending.level - 1);
        hurt(level, pending, pending.point, PULSE_RADIUS, damage, 0.05D);
        level.sendParticles(ParticleTypes.ELECTRIC_SPARK,
                pending.point.x, pending.point.y + 1.0D, pending.point.z, 8, 1.6D, 2.0D, 1.6D, 0.25D);
    }

    private static void hurt(ServerLevel level, Pending pending, Vec3 centre, double radius,
                             float damage, double push) {
        Player caster = level.getPlayerByUUID(pending.caster);
        DamageSource source = damageSource(level, caster);
        AABB area = new AABB(centre, centre).inflate(radius);
        for (LivingEntity target : level.getEntitiesOfClass(LivingEntity.class, area,
                entity -> entity.isAlive() && !entity.isSpectator()
                        && !entity.getUUID().equals(pending.caster))) {
            Vec3 direction = target.position().subtract(centre);
            if (direction.lengthSqr() > 1.0E-4D) {
                direction = direction.normalize().scale(push);
                target.push(direction.x, push * 0.4D, direction.z);
            }
            target.hurt(source, damage);
        }
    }

    private static Vec3 groundUnder(ServerLevel level, Vec3 from) {
        HitResult hit = level.clip(new ClipContext(from, from.add(0.0D, -96.0D, 0.0D),
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, CollisionContext.empty()));
        return hit.getType() == HitResult.Type.MISS ? from : hit.getLocation();
    }

    private static void tickCraters() {
        if (CRATERS.isEmpty()) {
            return;
        }
        for (Iterator<Crater> iterator = CRATERS.iterator(); iterator.hasNext(); ) {
            Crater crater = iterator.next();
            crater.scan(SCAN_PER_TICK);
            Player caster = crater.caster == null ? null : crater.world.getPlayerByUUID(crater.caster);
            int budget = BLOCKS_PER_TICK;
            while (budget-- > 0 && crater.carve < crater.positions.size()) {
                BlockPos pos = crater.positions.get(crater.carve++);
                if (!canBreak(crater.world, pos, caster)) {
                    crater.abandon();
                    break;
                }
                crater.world.destroyBlock(pos, false);
            }
            if (crater.isDone()) {
                iterator.remove();
            }
        }
    }

    private static boolean canBreak(ServerLevel level, BlockPos pos, Player caster) {
        if (caster == null) {
            return false;
        }
        BlockState state = level.getBlockState(pos);
        if (state.isAir()) {
            return false;
        }
        return !net.neoforged.neoforge.common.NeoForge.EVENT_BUS
                .post(new net.neoforged.neoforge.event.level.BlockEvent.BreakEvent(level, pos, state, caster))
                .isCanceled();
    }

    private static DamageSource damageSource(ServerLevel level, Player caster) {
        var holder = level.registryAccess()
                .registryOrThrow(Registries.DAMAGE_TYPE)
                .getHolder(ModDamageTypes.MARK_ADJUDICATE);
        if (holder.isPresent()) {
            return new DamageSource(holder.get(), caster);
        }
        return level.damageSources().magic();
    }

    private static final class Crater {
        private static final int PHASE_CRATER = 0;
        private static final int PHASE_SHAFT = 1;
        private static final int PHASE_DONE = 2;

        private final ServerLevel world;
        private final BlockPos centre;
        private final UUID caster;
        private final List<BlockPos> positions = new ArrayList<>();
        private int carve;
        private int phase = PHASE_CRATER;
        private int cursor;

        private Crater(ServerLevel world, Vec3 point, UUID caster) {
            this.world = world;
            this.centre = BlockPos.containing(point);
            this.caster = caster;
        }

        private void scan(int budget) {
            int radius = (int) Math.ceil(BLOCK_RADIUS);
            int depth = (int) Math.ceil(CRATER_DEPTH);
            int side = 2 * radius + 1;
            int craterTotal = (depth + 2) * side * side;
            int shaftRadius = (int) Math.ceil(BEAM_CLEAR_RADIUS);
            int shaftSide = 2 * shaftRadius + 1;
            int shaftHeight = (int) Math.ceil(BEAM_CLEAR_HEIGHT);
            int shaftTotal = Math.max(0, shaftHeight - 1) * shaftSide * shaftSide;

            while (budget-- > 0 && phase != PHASE_DONE) {
                if (positions.size() >= MAX_BLOCKS) {
                    phase = PHASE_DONE;
                    return;
                }
                if (phase == PHASE_CRATER) {
                    if (cursor >= craterTotal) {
                        phase = PHASE_SHAFT;
                        cursor = 0;
                        continue;
                    }
                    int index = cursor++;
                    int dy = 1 - index / (side * side);
                    int rest = index % (side * side);
                    int dx = -radius + rest / side;
                    int dz = -radius + rest % side;
                    double below = Math.max(0.0D, -dy);
                    double allowed = BLOCK_RADIUS * (1.0D - 0.55D * below / CRATER_DEPTH);
                    if (dx * dx + dz * dz > allowed * allowed) {
                        continue;
                    }
                    tryAdd(dx, dy, dz);
                } else {
                    if (cursor >= shaftTotal) {
                        phase = PHASE_DONE;
                        continue;
                    }
                    int index = cursor++;
                    int dy = shaftHeight - index / (shaftSide * shaftSide);
                    int rest = index % (shaftSide * shaftSide);
                    int dx = -shaftRadius + rest / shaftSide;
                    int dz = -shaftRadius + rest % shaftSide;
                    if (dx * dx + dz * dz > BEAM_CLEAR_RADIUS * BEAM_CLEAR_RADIUS) {
                        continue;
                    }
                    tryAdd(dx, dy, dz);
                }
            }
        }

        private void tryAdd(int dx, int dy, int dz) {
            BlockPos pos = centre.offset(dx, dy, dz);
            if (world.isOutsideBuildHeight(pos)) {
                return;
            }
            BlockState state = world.getBlockState(pos);
            if (state.isAir() || state.hasBlockEntity()) {
                return;
            }
            if (state.getDestroySpeed(world, pos) < 0.0F) {
                return;
            }
            positions.add(pos.immutable());
        }

        private void abandon() {
            positions.clear();
            carve = 0;
            cursor = 0;
            phase = PHASE_DONE;
        }

        private boolean isDone() {
            return phase == PHASE_DONE && carve >= positions.size();
        }
    }

    private static final class Pending {
        private final ServerLevel world;
        private final UUID caster;
        private final Vec3 point;
        private final int level;
        private int stage = STAGE_ARROW;
        private int pulses;
        private int ticks;

        private Pending(ServerLevel world, UUID caster, Vec3 point, int level, int ticks) {
            this.world = world;
            this.caster = caster;
            this.point = point;
            this.level = level;
            this.ticks = ticks;
        }
    }
}
