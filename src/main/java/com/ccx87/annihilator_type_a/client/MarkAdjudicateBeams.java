package com.ccx87.annihilator_type_a.client;

import com.ccx87.annihilator_type_a.AnnihilatorTypeA;
import com.ccx87.annihilator_type_a.registry.ATAEffects;
import com.ccx87.annihilator_type_a.util.MarkAdjudicateAim;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;
import org.joml.Matrix4f;


public final class MarkAdjudicateBeams {
    private static final int START_DELAY = 56;
    private static final int FORM_END = 5;
    private static final int HOLD_END = 16;
    private static final int PIVOT_END = 26;
    private static final int IMPACT_TICK = 40;
    private static final int LASER_START = 70;
    private static final int LASER_HOLD = 100;
    private static final int LASER_FADE = 24;
    private static final int DURATION = 230;

    private static final double MERGE_BASE = 0.9D;
    private static final double MERGE_RISE = 4.6D;
    private static final double ORBITAL_HEIGHT = 120.0D;
    private static final float LASER_WIDTH = 8.0F;
    private static final double ARC_RADIUS = 9.0D;
    private static final int SHAKE_TICKS = 26;

    private static final ResourceLocation BEAM_TEXTURE =
            AnnihilatorTypeA.id("textures/effect/orb_glow.png");
    private static final RenderType BEAM = RenderType.energySwirl(BEAM_TEXTURE, 0.0F, 0.0F);
    private static final ResourceLocation LASER_TEXTURE =
            AnnihilatorTypeA.id("textures/effect/beam.png");
    private static final RenderType LASER = RenderType.energySwirl(LASER_TEXTURE, 0.0F, 0.0F);
    private static final ResourceLocation LASER_SOLID_TEXTURE =
            AnnihilatorTypeA.id("textures/effect/laser_solid.png");
    private static final RenderType LASER_SOLID =
            RenderType.energySwirl(LASER_SOLID_TEXTURE, 0.0F, 0.0F);

    private static final int ARROW_CORE = 0xFFFFFFFF;
    private static final int ARROW_BODY = 0xFFD8F4FF;
    private static final int ARROW_GLOW = 0xFF5FD8FF;
    private static final int ARROW_FIN = 0xFF9FE8FF;
    private static final int STRIKE = 0xFFFF3838;
    private static final int STRIKE_HOT = 0xFFFFD8C8;
    private static final int MARK = 0xFFFF5050;
    private static final int ARC = 0xFFA8D4FF;

    private static int ticks;
    private static int startDelay;
    private static int armedTicks;
    private static boolean wasCharging;
    private static Vec3 lockedTarget;
    private static Vec3 lockedGround;

    private static int shakeTicks;
    private static float punch;
    private static Vec3 renderCamera;
    private static boolean markFired;
    private static boolean laserFired;
    private static boolean loggedCameraShake;
    private static int shakeFrames;

    private MarkAdjudicateBeams() {}

    public static void reset() {
        ticks = 0;
        startDelay = 0;
        armedTicks = 0;
        wasCharging = false;
        lockedTarget = null;
        lockedGround = null;
        shakeTicks = 0;
        punch = 0.0F;
        markFired = false;
        laserFired = false;
        loggedCameraShake = false;
        shakeFrames = 0;
    }

    public static void tick(Minecraft minecraft) {
        Player player = minecraft.player;
        boolean charging = player != null && player.hasEffect(ATAEffects.MARK_ADJUDICATE_CHARGE);
        if (charging) {
            armedTicks++;
        } else {
            if (wasCharging && armedTicks >= 50 && player != null) {
                captureTarget(player);
                startDelay = START_DELAY;
                ticks = 0;
                markFired = false;
                laserFired = false;
            }
            armedTicks = 0;
        }
        wasCharging = charging;

        if (startDelay > 0) {
            startDelay--;
            if (startDelay == 0) {
                ticks = 1;
                markFired = false;
                laserFired = false;
            }
        } else if (ticks > 0) {
            ticks++;
            if (ticks > DURATION) {
                ticks = 0;
            }
            if (ticks == 1) {
                whoosh(0.85F);
            } else if (ticks == PIVOT_END + 1) {
                whoosh(1.25F);
            } else if (ticks == IMPACT_TICK) {
                fireMark();
            } else if (ticks == LASER_START) {
                fireLaser();
            } else if (ticks == LASER_START + LASER_HOLD) {
                powerDown();
            } else if (ticks > LASER_START && ticks < LASER_START + LASER_HOLD + LASER_FADE) {
                int age = ticks - LASER_START;
                shakeTicks = Math.max(shakeTicks,
                        age < LASER_HOLD ? SHAKE_TICKS : Math.round(SHAKE_TICKS * 0.55F));
                if (age < LASER_HOLD) {
                    rainSparks();
                }
            }
        }
        if (shakeTicks > 0) {
            shakeTicks--;
        }
        if (punch > 0.0F) {
            punch *= 0.72F;
            if (punch < 0.02F) {
                punch = 0.0F;
            }
        }
    }

    private static void captureTarget(Player player) {
        lockedTarget = MarkAdjudicateAim.resolve(player.level(), player);
        Vec3 ground = MarkAdjudicateAim.ground(player.level(), player, lockedTarget);
        lockedGround = ground != null ? ground : lockedTarget;
    }

    public static int laserAge() {
        return ticks >= LASER_START ? ticks - LASER_START : -1;
    }

    public static int arrowImpactTick() {
        return IMPACT_TICK;
    }

    public static int arrowLaserTick() {
        return LASER_START;
    }

    public static void render(RenderLevelStageEvent event) {
        if (ticks <= 0 || lockedTarget == null || !ClientConfig.loaded()
                || event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null) {
            return;
        }

        float partialTick = event.getPartialTick().getGameTimeDeltaPartialTick(false);
        Vec3 caster = new Vec3(
                Mth.lerp(partialTick, player.xo, player.getX()),
                Mth.lerp(partialTick, player.yo, player.getY()),
                Mth.lerp(partialTick, player.zo, player.getZ()));
        float size = player instanceof AbstractClientPlayer local
                ? AnnihilationRingRenderer.ringScale(local)
                : 1.0F;
        renderAt(event, caster, size, ticks, lockedTarget, lockedGround);
    }

    public static void renderAt(RenderLevelStageEvent event, Vec3 caster, float size, int arrowTick,
                                Vec3 target, Vec3 ground) {
        if (arrowTick <= 0 || target == null
                || event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        MultiBufferSource.BufferSource buffers = minecraft.renderBuffers().bufferSource();

        int savedTicks = ticks;
        Vec3 savedTarget = lockedTarget;
        Vec3 savedGround = lockedGround;
        ticks = arrowTick;
        lockedTarget = target;
        lockedGround = ground;

        Vec3 cameraPos = event.getCamera().getPosition();
        renderCamera = cameraPos;
        PoseStack poseStack = event.getPoseStack();

        poseStack.pushPose();
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        Matrix4f matrix = poseStack.last().pose();

        if (ticks <= IMPACT_TICK) {
            VertexConsumer arrow = buffers.getBuffer(BEAM);
            Vec3 merge = new Vec3(caster.x, caster.y + MERGE_BASE + MERGE_RISE * size, caster.z);
            Vec3 toTarget = target.subtract(merge);
            double distance = toTarget.length();
            if (distance >= 0.05D) {
                float arrowLength = (float) Math.min(distance * 0.40D, 2.2D * size);
                drawTargetMark(arrow, matrix);
                drawArrowPhase(arrow, matrix, merge, toTarget.scale(1.0D / distance), arrowLength, size);
            }
        }

        VertexConsumer laser = buffers.getBuffer(LASER);
        int markAge = ticks - IMPACT_TICK;
        if (markAge >= 0 && ticks < LASER_START + 8) {
            drawMark(laser, matrix, markAge);
        }
        int laserAge = ticks - LASER_START;
        if (ticks >= IMPACT_TICK + 18) {
            float charge = Mth.clamp((ticks - (IMPACT_TICK + 18)) / 12.0F, 0.0F, 1.0F);
            drawOrbitalPoint(laser, matrix, charge, laserAge);
        }
        if (ticks > LASER_START - 12 && ticks < LASER_START) {
            drawGuideBeam(laser, matrix, (ticks - (LASER_START - 12)) / 12.0F);
        }
        if (laserAge >= 0) {
            drawLaser(laser, matrix, size, laserAge);
            VertexConsumer core = buffers.getBuffer(LASER_SOLID);
            drawLaserCore(core, matrix, laserAge);
        }

        poseStack.popPose();
        buffers.endBatch();

        ticks = savedTicks;
        lockedTarget = savedTarget;
        lockedGround = savedGround;
    }

    public static void playRemoteMark(Vec3 point) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return;
        }
        minecraft.level.playLocalSound(point.x, point.y, point.z, SoundEvents.AMETHYST_BLOCK_CHIME,
                SoundSource.PLAYERS, 1.2F, 1.6F, false);
        minecraft.level.playLocalSound(point.x, point.y, point.z, SoundEvents.BREEZE_SHOOT,
                SoundSource.PLAYERS, 0.8F, 0.55F, false);
    }

    public static void playRemoteLaser(Vec3 point) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) {
            return;
        }
        minecraft.player.playSound(SoundEvents.CONDUIT_ATTACK_TARGET, 1.0F, 0.85F);
        minecraft.level.playLocalSound(point.x, point.y, point.z, SoundEvents.BEACON_ACTIVATE,
                SoundSource.PLAYERS, 3.0F, 0.65F, false);
        minecraft.level.playLocalSound(point.x, point.y, point.z, SoundEvents.BEACON_AMBIENT,
                SoundSource.PLAYERS, 2.0F, 0.70F, false);
    }

    public static void remoteShake(double distance) {
        float scale = (float) Mth.clamp(1.0D - distance / 96.0D, 0.15D, 1.0D);
        shakeTicks = Math.max(shakeTicks, Math.round(SHAKE_TICKS * scale));
        punch = Math.max(punch, 0.7F * scale);
    }

    private static void drawTargetMark(VertexConsumer consumer, Matrix4f matrix) {
        if (lockedGround == null) {
            return;
        }
        float tighten = Mth.clamp(ticks / (float) IMPACT_TICK, 0.0F, 1.0F);
        float radius = 2.6F - 0.7F * tighten;
        float alpha = 0.22F + 0.33F * tighten;
        Vec3 centre = lockedGround.add(0.0D, 0.05D, 0.0D);
        ring(consumer, matrix, centre, radius * 0.90F, radius, withAlpha(MARK, alpha), 36);
        ring(consumer, matrix, centre, radius * 1.18F, radius * 1.24F, withAlpha(MARK, alpha * 0.5F), 36);
        float tickLength = radius * 0.42F;
        for (int corner = 0; corner < 4; corner++) {
            double angle = Math.PI * 0.25D + corner * Math.PI * 0.5D;
            Vec3 outward = new Vec3(Math.cos(angle), 0.0D, Math.sin(angle));
            Vec3 across = new Vec3(-Math.sin(angle), 0.0D, Math.cos(angle));
            Vec3 base = centre.add(outward.scale(radius));
            gradient(consumer, matrix,
                    base.subtract(across.scale(0.10F)), base.add(across.scale(0.10F)),
                    base.add(outward.scale(tickLength)).add(across.scale(0.04F)),
                    base.add(outward.scale(tickLength)).subtract(across.scale(0.04F)),
                    withAlpha(MARK, alpha), withAlpha(MARK, alpha),
                    withAlpha(MARK, 0.0F), withAlpha(MARK, 0.0F));
        }
    }

    private static void drawMark(VertexConsumer consumer, Matrix4f matrix, int age) {
        if (lockedGround == null) {
            return;
        }
        Vec3 centre = lockedGround.add(0.0D, 0.06D, 0.0D);
        float pulse = 0.70F + 0.30F * Mth.sin(age * 0.55F);
        float fade = age > LASER_START - IMPACT_TICK ? 1.0F - (age - (LASER_START - IMPACT_TICK)) / 8.0F : 1.0F;
        fade = Mth.clamp(fade, 0.0F, 1.0F);

        if (age < 5) {
            float flash = 1.0F - age / 5.0F;
            float radius = 0.6F + 1.2F * (1.0F - flash);
            for (int plane = 0; plane < 3; plane++) {
                Vec3 a = plane == 0 ? new Vec3(1, 0, 0) : (plane == 1 ? new Vec3(0, 1, 0) : new Vec3(0, 0, 1));
                Vec3 b = plane == 0 ? new Vec3(0, 1, 0) : (plane == 1 ? new Vec3(0, 0, 1) : new Vec3(1, 0, 0));
                quad(consumer, matrix,
                        centre.add(a.scale(radius)).subtract(b.scale(radius)),
                        centre.add(a.scale(radius)).add(b.scale(radius)),
                        centre.subtract(a.scale(radius)).add(b.scale(radius)),
                        centre.subtract(a.scale(radius)).subtract(b.scale(radius)),
                        withAlpha(STRIKE_HOT, 0.7F * flash));
            }
        }

        ring(consumer, matrix, centre, 1.85F, 2.10F, withAlpha(MARK, 0.60F * pulse * fade), 40);
        ring(consumer, matrix, centre, 2.42F, 2.52F, withAlpha(MARK, 0.32F * pulse * fade), 40);

        for (int axis = 0; axis < 2; axis++) {
            Vec3 along = axis == 0 ? new Vec3(1, 0, 0) : new Vec3(0, 0, 1);
            Vec3 across = axis == 0 ? new Vec3(0, 0, 1) : new Vec3(1, 0, 0);
            gradient(consumer, matrix,
                    centre.subtract(along.scale(2.2F)).subtract(across.scale(0.05F)),
                    centre.add(along.scale(2.2F)).subtract(across.scale(0.05F)),
                    centre.add(along.scale(2.2F)).add(across.scale(0.05F)),
                    centre.subtract(along.scale(2.2F)).add(across.scale(0.05F)),
                    withAlpha(MARK, 0.45F * pulse * fade), withAlpha(MARK, 0.45F * pulse * fade),
                    withAlpha(MARK, 0.45F * pulse * fade), withAlpha(MARK, 0.45F * pulse * fade));
        }

        int count = 4;
        for (int index = 0; index < count; index++) {
            double angle = index * (Math.PI * 2.0D / count) + age * 0.05D;
            Vec3 outward = new Vec3(Math.cos(angle), 0.0D, Math.sin(angle));
            Vec3 across = new Vec3(-Math.sin(angle), 0.0D, Math.cos(angle));
            Vec3 base = centre.add(outward.scale(3.0F));
            gradient(consumer, matrix,
                    base.subtract(across.scale(0.12F)), base.add(across.scale(0.12F)),
                    base.add(outward.scale(0.55F)).add(across.scale(0.05F)),
                    base.add(outward.scale(0.55F)).subtract(across.scale(0.05F)),
                    withAlpha(MARK, 0.75F * fade), withAlpha(MARK, 0.75F * fade),
                    withAlpha(MARK, 0.0F), withAlpha(MARK, 0.0F));
        }

        float launch = Mth.clamp(age / 18.0F, 0.0F, 1.0F);
        float uplinkFade = age > LASER_START - IMPACT_TICK
                ? Mth.clamp(1.0F - (age - (LASER_START - IMPACT_TICK)) / 6.0F, 0.0F, 1.0F)
                : 1.0F;
        Vec3 head = lockedTarget.add(0.0D, ORBITAL_HEIGHT * launch, 0.0D);
        beam(consumer, matrix, lockedTarget, head, 0.20F, withAlpha(0xFFFF7050, 0.55F * uplinkFade));
        beam(consumer, matrix, lockedTarget, head, 0.07F, withAlpha(0xFFFFFFFF, 0.9F * uplinkFade));
        for (int rung = 1; rung <= 8; rung++) {
            double at = rung / 8.0D;
            if (at > launch + 1.0E-3D) {
                break;
            }
            Vec3 rungPoint = lockedTarget.add(0.0D, ORBITAL_HEIGHT * at, 0.0D);
            float rungFade = (float) Math.max(0.0D, 1.0D - (launch - at) * 2.5D);
            ring(consumer, matrix, rungPoint, 0.35F, 0.55F,
                    withAlpha(0xFFFFD0C0, 0.30F * rungFade * uplinkFade), 16);
        }
        if (launch < 1.0F) {
            float headAlpha = 0.55F + 0.35F * (float) Math.sin(launch * Math.PI);
            beam(consumer, matrix, head.add(0.0D, -1.0D, 0.0D), head.add(0.0D, 1.0D, 0.0D),
                    0.45F, withAlpha(0xFFFFFFFF, headAlpha));
        } else {
            for (int index = 0; index < 2; index++) {
                double fraction = ((age - 18) * 0.04D + index * 0.5D) % 1.0D;
                Vec3 energyPulse = lockedTarget.add(0.0D, ORBITAL_HEIGHT * fraction, 0.0D);
                beam(consumer, matrix, energyPulse.add(0.0D, -1.2D, 0.0D), energyPulse.add(0.0D, 1.2D, 0.0D),
                        0.30F, withAlpha(0xFFFFE0D0, 0.35F * uplinkFade));
            }
        }
    }

    private static void drawOrbitalPoint(VertexConsumer consumer, Matrix4f matrix, float charge, int laserAge) {
        Vec3 sky = lockedTarget.add(0.0D, ORBITAL_HEIGHT, 0.0D);
        float fade = laserAge < 0 ? 1.0F : Mth.clamp(1.0F - laserAge / 10.0F, 0.0F, 1.0F);
        if (fade <= 0.0F) {
            return;
        }
        float pulse = 0.85F + 0.15F * Mth.sin((ticks + 7) * 0.6F);
        float radius = (1.0F + 3.5F * charge) * pulse;
        for (int plane = 0; plane < 3; plane++) {
            Vec3 a = plane == 0 ? new Vec3(1, 0, 0) : (plane == 1 ? new Vec3(0, 1, 0) : new Vec3(0, 0, 1));
            Vec3 b = plane == 0 ? new Vec3(0, 1, 0) : (plane == 1 ? new Vec3(0, 0, 1) : new Vec3(1, 0, 0));
            quad(consumer, matrix,
                    sky.add(a.scale(radius)).subtract(b.scale(radius)),
                    sky.add(a.scale(radius)).add(b.scale(radius)),
                    sky.subtract(a.scale(radius)).add(b.scale(radius)),
                    sky.subtract(a.scale(radius)).subtract(b.scale(radius)),
                    withAlpha(0xFFFFE0D0, 0.55F * charge * fade));
        }
        ring(consumer, matrix, sky, radius * 1.30F, radius * 1.50F,
                withAlpha(0xFFFFB090, 0.35F * charge * fade), 24);
        ring(consumer, matrix, sky, radius * 2.00F, radius * 2.10F,
                withAlpha(MARK, 0.20F * charge * fade), 24);
    }

    private static void drawArrowPhase(VertexConsumer consumer, Matrix4f matrix, Vec3 merge, Vec3 aim,
                                       float length, float size) {
        Vec3 dir;
        Vec3 centre;
        float arrowLength = length;
        boolean thrusting = false;
        float thrust = 0.0F;

        if (ticks <= FORM_END + 3) {
            float fade = 1.0F - (ticks - 1) / (float) (FORM_END + 3);
            float radius = (0.7F + 1.7F * (1.0F - fade)) * size;
            int colour = withAlpha(ARROW_CORE, 0.55F * fade);
            quad(consumer, matrix,
                    merge.add(-radius, -radius, 0.0F), merge.add(radius, -radius, 0.0F),
                    merge.add(radius, radius, 0.0F), merge.add(-radius, radius, 0.0F), colour);
            quad(consumer, matrix,
                    merge.add(0.0F, -radius, -radius), merge.add(0.0F, -radius, radius),
                    merge.add(0.0F, radius, radius), merge.add(0.0F, radius, -radius), colour);
            quad(consumer, matrix,
                    merge.add(-radius, 0.0F, -radius), merge.add(radius, 0.0F, -radius),
                    merge.add(radius, 0.0F, radius), merge.add(-radius, 0.0F, radius), colour);
        }

        if (ticks <= FORM_END) {
            dir = new Vec3(0.0D, 1.0D, 0.0D);
            arrowLength = length * Mth.clamp(ticks / (float) FORM_END, 0.0F, 1.0F);
            centre = merge.add(dir.scale(arrowLength * 0.5D));
        } else if (ticks <= HOLD_END) {
            dir = new Vec3(0.0D, 1.0D, 0.0D);
            centre = merge.add(dir.scale(length * 0.5D));
        } else if (ticks <= PIVOT_END) {
            float swing = Mth.clamp((ticks - HOLD_END) / (float) (PIVOT_END - HOLD_END), 0.0F, 1.0F);
            float eased = smoothstep(swing);
            dir = slerp(new Vec3(0.0D, 1.0D, 0.0D), aim, eased);
            centre = merge.add(0.0D, length * 0.5D, 0.0D);
            Vec3 ribbonPrevious = null;
            for (int step = 0; step <= 8; step++) {
                float before = Math.max(0.0F, eased - step * 0.035F);
                Vec3 arm = slerp(new Vec3(0.0D, 1.0D, 0.0D), aim, before);
                Vec3 tipPoint = centre.add(arm.scale(length * 0.5D));
                if (ribbonPrevious != null) {
                    float ribbonFade = 1.0F - step / 9.0F;
                    beam(consumer, matrix, ribbonPrevious, tipPoint, 0.12F * ribbonFade,
                            withAlpha(ARROW_GLOW, 0.30F * ribbonFade));
                }
                ribbonPrevious = tipPoint;
            }
        } else {
            thrust = Mth.clamp((ticks - PIVOT_END) / (float) (IMPACT_TICK - PIVOT_END), 0.0F, 1.0F);
            float eased = smoothstep(thrust);
            dir = aim;
            float stretched = length * (1.0F + 0.9F * eased);
            Vec3 start = merge.add(0.0D, length * 0.5D, 0.0D);
            Vec3 end = lockedTarget.subtract(aim.scale(stretched * 0.5D));
            centre = start.add(end.subtract(start).scale(eased));
            centre = centre.add(0.0D, Math.sin(Math.PI * eased) * 2.6D, 0.0D);
            arrowLength = stretched;
            thrusting = true;
        }

        if (arrowLength <= 0.01F) {
            return;
        }
        Vec3 tail = centre.subtract(dir.scale(arrowLength * 0.5D));
        drawArrow(consumer, matrix, tail, dir, arrowLength);
        if (thrusting) {
            Vec3 tip = tail.add(dir.scale(arrowLength));
            Vec3 helper = Math.abs(dir.y) > 0.98D ? new Vec3(1.0D, 0.0D, 0.0D) : new Vec3(0.0D, 1.0D, 0.0D);
            Vec3 side = dir.cross(helper).normalize();
            Vec3 up2 = side.cross(dir).normalize();
            float cone = (1.0F + 1.8F * thrust) * size;
            int coneColour = withAlpha(0xFFFFFFFF, 0.30F * thrust);
            blade(consumer, matrix, tip, dir, side, 0.0F, cone, 0.40F * size, 0.02F, coneColour);
            blade(consumer, matrix, tip, dir, up2, 0.0F, cone, 0.40F * size, 0.02F, coneColour);
            drawTrail(consumer, matrix, tail, dir, size, thrust);
        }
    }

    private static void drawArrow(VertexConsumer consumer, Matrix4f matrix, Vec3 tail, Vec3 dir,
                                  float length) {
        Vec3 helper = Math.abs(dir.y) > 0.98D ? new Vec3(1.0D, 0.0D, 0.0D) : new Vec3(0.0D, 1.0D, 0.0D);
        Vec3 right = dir.cross(helper).normalize();
        Vec3 up = right.cross(dir).normalize();

        double spin = ticks * 0.11D;
        double cos = Math.cos(spin);
        double sin = Math.sin(spin);
        Vec3 spunRight = right.scale(cos).add(up.scale(sin));
        Vec3 spunUp = up.scale(cos).subtract(right.scale(sin));

        float headLength = length * 0.30F;
        float headRadius = Math.max(0.26F, length * 0.10F);
        float shaftRadius = Math.max(0.07F, length * 0.028F);
        float finLength = length * 0.26F;
        float finHeight = Math.max(0.34F, length * 0.13F);
        float back = length * 0.03F;
        float shaftEnd = length - headLength;

        prism(consumer, matrix, tail, dir, spunRight, spunUp, back, shaftEnd,
                shaftRadius * 3.0F, withAlpha(ARROW_GLOW, 0.10F));
        pyramid(consumer, matrix, tail, dir, spunRight, spunUp, shaftEnd, length,
                headRadius * 1.8F, withAlpha(ARROW_GLOW, 0.14F));

        prism(consumer, matrix, tail, dir, spunRight, spunUp, back, shaftEnd,
                shaftRadius, withAlpha(ARROW_BODY, 0.95F));
        prism(consumer, matrix, tail, dir, spunRight, spunUp, back, shaftEnd,
                shaftRadius * 0.45F, withAlpha(ARROW_CORE, 1.0F));

        pyramid(consumer, matrix, tail, dir, spunRight, spunUp, shaftEnd, length,
                headRadius, withAlpha(ARROW_BODY, 1.0F));
        pyramid(consumer, matrix, tail, dir, spunRight, spunUp, shaftEnd, length,
                headRadius * 0.55F, withAlpha(ARROW_CORE, 1.0F));

        for (int index = 0; index < 3; index++) {
            double angle = index * (Math.PI * 2.0D / 3.0D) + Math.PI * 0.5D;
            fin(consumer, matrix, tail, dir, spunRight, spunUp, back + 0.01F, back + finLength,
                    shaftRadius, finHeight, angle);
        }
    }

    private static void drawTrail(VertexConsumer consumer, Matrix4f matrix, Vec3 tail, Vec3 dir,
                                  float size, float thrust) {
        float trailLength = 2.8F * size * (0.35F + 0.65F * thrust);
        Vec3 helper = Math.abs(dir.y) > 0.98D ? new Vec3(1.0D, 0.0D, 0.0D) : new Vec3(0.0D, 1.0D, 0.0D);
        Vec3 side = dir.cross(helper).normalize();
        Vec3 up = side.cross(dir).normalize();
        Vec3 start = tail.subtract(dir.scale(trailLength));
        float halfWidth = 0.30F * size;
        int bright = withAlpha(ARROW_BODY, 0.55F * thrust);
        int clear = withAlpha(ARROW_BODY, 0.0F);
        for (int plane = 0; plane < 2; plane++) {
            Vec3 across = plane == 0 ? side : up;
            gradient(consumer, matrix,
                    start.subtract(across.scale(halfWidth)), start.add(across.scale(halfWidth)),
                    tail.add(across.scale(halfWidth * 0.2F)), tail.subtract(across.scale(halfWidth * 0.2F)),
                    clear, clear, withAlpha(ARROW_GLOW, 0.35F * thrust), withAlpha(ARROW_GLOW, 0.35F * thrust));
            gradient(consumer, matrix,
                    start.subtract(across.scale(halfWidth * 0.45F)), start.add(across.scale(halfWidth * 0.45F)),
                    tail.add(across.scale(halfWidth * 0.1F)), tail.subtract(across.scale(halfWidth * 0.1F)),
                    clear, clear, bright, bright);
        }
        for (int plane = 0; plane < 2; plane++) {
            Vec3 across = plane == 0 ? side : up;
            gradient(consumer, matrix,
                    start.subtract(across.scale(halfWidth * 0.16F)), start.add(across.scale(halfWidth * 0.16F)),
                    tail.add(across.scale(halfWidth * 0.05F)), tail.subtract(across.scale(halfWidth * 0.05F)),
                    clear, clear, withAlpha(0xFFFFFFFF, 0.55F * thrust), withAlpha(0xFFFFFFFF, 0.55F * thrust));
        }
        for (int spark = 0; spark < 7; spark++) {
            double along = 0.12D + 0.8D * hash(spark * 3.1D + ticks * 0.7D);
            double lateral = (hash(spark * 7.7D + ticks * 1.3D) - 0.5D) * halfWidth * 1.7D;
            Vec3 point = start.add(dir.scale(along * trailLength)).add(side.scale(lateral));
            float sparkSize = (0.03F + 0.05F * (float) hash(spark * 2.3D)) * size;
            quad(consumer, matrix,
                    point.add(-sparkSize, -sparkSize, 0.0F), point.add(sparkSize, -sparkSize, 0.0F),
                    point.add(sparkSize, sparkSize, 0.0F), point.add(-sparkSize, sparkSize, 0.0F),
                    withAlpha(0xFFFFE0D0, 0.45F * thrust));
        }
    }

    private static double hash(double value) {
        double scrambled = Math.sin(value * 12.9898D) * 43758.5453D;
        return scrambled - Math.floor(scrambled);
    }

    private static void drawGuideBeam(VertexConsumer consumer, Matrix4f matrix, float charge) {
        float eased = Mth.clamp(charge, 0.0F, 1.0F);
        float alpha = 0.10F + 0.55F * eased;
        Vec3 top = lockedTarget.add(0.0D, ORBITAL_HEIGHT, 0.0D);
        beam(consumer, matrix, top, lockedTarget, 0.10F + 0.10F * eased, withAlpha(0xFFFF4040, alpha));
        beam(consumer, matrix, top, lockedTarget, 0.03F + 0.03F * eased, withAlpha(0xFFFFE0D0, alpha * 0.9F));
        Vec3 centre = (lockedGround == null ? lockedTarget : lockedGround).add(0.0D, 0.06D, 0.0D);
        ring(consumer, matrix, centre, 0.22F, 0.42F + 0.5F * eased,
                withAlpha(0xFFFF5050, 0.30F + 0.55F * eased), 24);
    }

    private static void drawLaser(VertexConsumer consumer, Matrix4f matrix, float size, int age) {
        Vec3 point = lockedTarget;
        Vec3 ground = lockedGround == null ? point : lockedGround;
        Vec3 top = point.add(0.0D, ORBITAL_HEIGHT, 0.0D);

        float fade = 1.0F;
        if (age > LASER_HOLD) {
            fade = 1.0F - Mth.clamp((age - LASER_HOLD) / (float) LASER_FADE, 0.0F, 1.0F);
        }
        float pulse = 1.0F + 0.04F * Mth.sin(age * 1.1F);
        float width = LASER_WIDTH * pulse;
        float retract = age > LASER_HOLD
                ? Mth.clamp((age - LASER_HOLD) / (float) LASER_FADE, 0.0F, 1.0F) * (float) ORBITAL_HEIGHT * 0.9F
                : 0.0F;

        int endAge = age - (LASER_HOLD + LASER_FADE);
        if (endAge >= -6 && endAge < 22) {
            float endFade = 1.0F - Mth.clamp((endAge + 6) / 28.0F, 0.0F, 1.0F);
            float flashRadius = 2.5F + 7.0F * (1.0F - endFade);
            for (int plane = 0; plane < 3; plane++) {
                Vec3 a = plane == 0 ? new Vec3(1, 0, 0) : (plane == 1 ? new Vec3(0, 1, 0) : new Vec3(0, 0, 1));
                Vec3 b = plane == 0 ? new Vec3(0, 1, 0) : (plane == 1 ? new Vec3(0, 0, 1) : new Vec3(1, 0, 0));
                quad(consumer, matrix,
                        top.add(a.scale(flashRadius)).subtract(b.scale(flashRadius)),
                        top.add(a.scale(flashRadius)).add(b.scale(flashRadius)),
                        top.subtract(a.scale(flashRadius)).add(b.scale(flashRadius)),
                        top.subtract(a.scale(flashRadius)).subtract(b.scale(flashRadius)),
                        withAlpha(STRIKE_HOT, 0.45F * endFade));
            }
            float endRing = 4.0F + 18.0F * (1.0F - endFade);
            ring(consumer, matrix, top, endRing * 0.82F, endRing, withAlpha(MARK, 0.40F * endFade), 32);
        }
        if (fade <= 0.0F) {
            return;
        }

        if (age < 8) {
            float flash = 1.0F - age / 8.0F;
            float radius = 2.0F + 5.0F * (1.0F - flash);
            for (int plane = 0; plane < 3; plane++) {
                Vec3 a = plane == 0 ? new Vec3(1, 0, 0) : (plane == 1 ? new Vec3(0, 1, 0) : new Vec3(0, 0, 1));
                Vec3 b = plane == 0 ? new Vec3(0, 1, 0) : (plane == 1 ? new Vec3(0, 0, 1) : new Vec3(1, 0, 0));
                quad(consumer, matrix,
                        point.add(a.scale(radius)).subtract(b.scale(radius)),
                        point.add(a.scale(radius)).add(b.scale(radius)),
                        point.subtract(a.scale(radius)).add(b.scale(radius)),
                        point.subtract(a.scale(radius)).subtract(b.scale(radius)),
                        withAlpha(STRIKE_HOT, 0.8F * flash));
            }
        }

        float descend = Mth.clamp(age / 2.0F, 0.0F, 1.0F);
        Vec3 bottom = top.add(point.subtract(top).scale(descend))
                .add(0.0D, -5.0D * descend + retract, 0.0D);
        beam(consumer, matrix, top, bottom, width * 1.7F, withAlpha(STRIKE, 0.10F * fade));

        if (age < 55) {
            float coneFade = (1.0F - age / 55.0F) * fade;
            Vec3 up = top.subtract(bottom);
            double coneLength = up.length();
            if (coneLength > 0.01D) {
                Vec3 dir = up.scale(1.0D / coneLength);
                Vec3 side = billboardSide(dir, bottom.add(top).scale(0.5D));
                int coneColour = withAlpha(0xFFFF8050, 0.05F * coneFade);
                blade(consumer, matrix, bottom, dir, side, 0.0F, (float) coneLength,
                        width * 1.35F, width * 0.22F, coneColour);
                blade(consumer, matrix, bottom, dir, side.cross(dir).normalize(), 0.0F, (float) coneLength,
                        width * 1.35F, width * 0.22F, coneColour);
            }
        }

        for (int arc = 0; arc < 4; arc++) {
            double phase = age * 0.35D + arc * Math.PI * 0.5D;
            Vec3 previous = null;
            for (int segment = 0; segment <= 12; segment++) {
                double fraction = segment / 12.0D;
                Vec3 centre = top.add(point.subtract(top).scale(fraction));
                double angle = phase + fraction * 7.5D;
                Vec3 position = centre.add(Math.cos(angle) * ARC_RADIUS, 0.0D,
                        Math.sin(angle) * ARC_RADIUS);
                if (previous != null) {
                    beam(consumer, matrix, previous, position, 0.22F,
                            withAlpha(ARC, 0.32F * fade));
                }
                previous = position;
            }
        }

        for (int index = 0; index < 4; index++) {
            double fraction = (age * 0.045D + index / 4.0D) % 1.0D;
            Vec3 pulseCentre = top.add(point.subtract(top).scale(fraction));
            beam(consumer, matrix, pulseCentre.add(0.0D, 3.2D, 0.0D),
                    pulseCentre.subtract(0.0D, 3.2D, 0.0D),
                    width * 0.42F, withAlpha(0xFFFFB090, 0.40F * fade));
        }

        for (int index = 0; index < 3; index++) {
            double fraction = (age * 0.02D + index / 3.0D) % 1.0D;
            Vec3 ringCentre = point.add(0.0D, fraction * ORBITAL_HEIGHT, 0.0D);
            float ringRadius = width * (1.10F - 0.30F * (float) fraction);
            ring(consumer, matrix, ringCentre, ringRadius * 0.80F, ringRadius,
                    withAlpha(STRIKE_HOT, 0.40F * (1.0F - (float) fraction) * fade), 28);
        }

        float pool = 0.30F + 0.12F * Mth.sin(age * 0.45F);
        ring(consumer, matrix, ground.add(0.0D, 0.05D, 0.0D), 0.01F, width * 1.2F,
                withAlpha(0xFFFF6030, pool * fade), 32);

        if (age < 10) {
            float flare = 1.0F - age / 10.0F;
            float arm = 9.0F + 7.0F * (1.0F - flare);
            int armColour = withAlpha(0xFFFFE0D0, 0.30F * flare);
            quad(consumer, matrix,
                    top.add(-arm, -0.3F, 0.0F), top.add(arm, -0.3F, 0.0F),
                    top.add(arm, 0.3F, 0.0F), top.add(-arm, 0.3F, 0.0F), armColour);
            quad(consumer, matrix,
                    top.add(0.0F, -0.3F, -arm), top.add(0.0F, -0.3F, arm),
                    top.add(0.0F, 0.3F, arm), top.add(0.0F, 0.3F, -arm), armColour);
        }

        if (age < 14) {
            float flare = 1.0F - age / 14.0F;
            float radius = 6.0F + 4.0F * (1.0F - flare);
            for (int plane = 0; plane < 3; plane++) {
                Vec3 a = plane == 0 ? new Vec3(1, 0, 0) : (plane == 1 ? new Vec3(0, 1, 0) : new Vec3(0, 0, 1));
                Vec3 b = plane == 0 ? new Vec3(0, 1, 0) : (plane == 1 ? new Vec3(0, 0, 1) : new Vec3(1, 0, 0));
                quad(consumer, matrix,
                        top.add(a.scale(radius)).subtract(b.scale(radius)),
                        top.add(a.scale(radius)).add(b.scale(radius)),
                        top.subtract(a.scale(radius)).add(b.scale(radius)),
                        top.subtract(a.scale(radius)).subtract(b.scale(radius)),
                        withAlpha(0xFFFFE0D0, 0.5F * flare));
            }
        }

        if (age < 10) {
            float splash = 1.0F - age / 10.0F;
            float radius = 3.0F + 9.0F * (age / 10.0F);
            for (int plane = 0; plane < 3; plane++) {
                Vec3 a = plane == 0 ? new Vec3(1, 0, 0) : (plane == 1 ? new Vec3(0, 1, 0) : new Vec3(0, 0, 1));
                Vec3 b = plane == 0 ? new Vec3(0, 1, 0) : (plane == 1 ? new Vec3(0, 0, 1) : new Vec3(1, 0, 0));
                quad(consumer, matrix,
                        point.add(a.scale(radius)).subtract(b.scale(radius)),
                        point.add(a.scale(radius)).add(b.scale(radius)),
                        point.subtract(a.scale(radius)).add(b.scale(radius)),
                        point.subtract(a.scale(radius)).subtract(b.scale(radius)),
                        withAlpha(0xFFFFC0A0, 0.6F * splash));
            }
        }
        if (age < 34) {
            float grow = Mth.clamp(age / 26.0F, 0.0F, 1.0F);
            float eased = 1.0F - (1.0F - grow) * (1.0F - grow);
            float alpha = (1.0F - grow) * 0.8F;
            float radius = 1.5F + 13.5F * eased;
            Vec3 centre = ground.add(0.0D, 0.06D, 0.0D);
            ring(consumer, matrix, centre, radius * 0.85F, radius, withAlpha(STRIKE, alpha), 40);
            ring(consumer, matrix, centre, radius * 1.10F, radius * 1.18F, withAlpha(MARK, alpha * 0.5F), 40);
        }

        if (age < LASER_HOLD + LASER_FADE) {
            float collar = 0.45F + 0.25F * Mth.sin(age * 0.8F);
            Vec3 centre = ground.add(0.0D, 0.08D, 0.0D);
            ring(consumer, matrix, centre, 2.2F, 2.9F, withAlpha(0xFFFF7040, collar * fade), 30);
            ring(consumer, matrix, centre, 3.4F, 3.6F, withAlpha(MARK, collar * 0.5F * fade), 30);
        }
    }

    private static void drawLaserCore(VertexConsumer consumer, Matrix4f matrix, int age) {
        Vec3 point = lockedTarget;
        Vec3 top = point.add(0.0D, ORBITAL_HEIGHT, 0.0D);
        float fade = 1.0F;
        if (age > LASER_HOLD) {
            fade = 1.0F - Mth.clamp((age - LASER_HOLD) / (float) LASER_FADE, 0.0F, 1.0F);
        }
        if (fade <= 0.0F) {
            return;
        }
        float pulse = 1.0F + 0.04F * Mth.sin(age * 1.1F);
        float width = LASER_WIDTH * pulse;
        float descend = Mth.clamp(age / 2.0F, 0.0F, 1.0F);
        float retract = age > LASER_HOLD
                ? Mth.clamp((age - LASER_HOLD) / (float) LASER_FADE, 0.0F, 1.0F) * (float) ORBITAL_HEIGHT * 0.9F
                : 0.0F;
        Vec3 bottom = top.add(point.subtract(top).scale(descend))
                .add(0.0D, -5.0D * descend + retract, 0.0D);
        cylinder(consumer, matrix, top, bottom, width, withAlpha(STRIKE, 0.34F * fade), 24);
        cylinder(consumer, matrix, top, bottom, width * 0.55F, withAlpha(0xFFFF6030, 0.60F * fade), 20);
        cylinder(consumer, matrix, top, bottom, width * 0.22F, withAlpha(STRIKE_HOT, 0.95F * fade), 16);
        cylinder(consumer, matrix, top, bottom, width * 0.075F, withAlpha(0xFFFFFFFF, 1.0F * fade), 12);
    }

    private static void whoosh(float pitch) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) {
            return;
        }
        Vec3 at = minecraft.player.position().add(0.0D, 1.0D, 0.0D);
        minecraft.level.playLocalSound(at.x, at.y, at.z, SoundEvents.BREEZE_SHOOT,
                SoundSource.PLAYERS, 1.4F, pitch, false);
        minecraft.level.playLocalSound(at.x, at.y, at.z, SoundEvents.PLAYER_ATTACK_SWEEP,
                SoundSource.PLAYERS, 1.1F, pitch * 0.85F, false);
        minecraft.level.playLocalSound(at.x, at.y, at.z, SoundEvents.TRIDENT_RIPTIDE_2.value(),
                SoundSource.PLAYERS, 0.8F, pitch * 1.35F, false);
    }

    private static void powerDown() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || lockedTarget == null) {
            return;
        }
        Vec3 point = lockedTarget;
        minecraft.level.playLocalSound(point.x, point.y, point.z, SoundEvents.BEACON_DEACTIVATE,
                SoundSource.PLAYERS, 2.2F, 0.60F, false);
    }

    private static void fireMark() {
        Minecraft minecraft = Minecraft.getInstance();
        if (markFired || minecraft.level == null || lockedTarget == null) {
            return;
        }
        markFired = true;
        Vec3 point = lockedTarget;
        for (int i = 0; i < 14; i++) {
            double angle = minecraft.level.random.nextDouble() * Math.PI * 2.0D;
            double radius = minecraft.level.random.nextDouble() * 1.2D;
            minecraft.level.addParticle(ParticleTypes.END_ROD,
                    point.x + Math.cos(angle) * radius, point.y + 0.4D + minecraft.level.random.nextDouble(),
                    point.z + Math.sin(angle) * radius,
                    Math.cos(angle) * 0.12D, 0.16D, Math.sin(angle) * 0.12D);
        }
        minecraft.level.playLocalSound(point.x, point.y, point.z, SoundEvents.AMETHYST_BLOCK_CHIME,
                SoundSource.PLAYERS, 1.4F, 1.6F, false);
        minecraft.level.playLocalSound(point.x, point.y, point.z, SoundEvents.BREEZE_SHOOT,
                SoundSource.PLAYERS, 0.9F, 0.55F, false);
        shakeTicks = 6;
        punch = 0.18F;
    }

    private static void fireLaser() {
        Minecraft minecraft = Minecraft.getInstance();
        if (laserFired || minecraft.level == null || minecraft.player == null || lockedTarget == null) {
            return;
        }
        laserFired = true;
        loggedCameraShake = false;
        shakeFrames = 0;
        Vec3 point = lockedTarget;
        shakeTicks = SHAKE_TICKS;
        punch = 1.0F;
        for (int i = 0; i < 140; i++) {
            double angle = minecraft.level.random.nextDouble() * Math.PI * 2.0D;
            double radius = minecraft.level.random.nextDouble() * 6.0D;
            double lift = minecraft.level.random.nextDouble() * 6.0D;
            minecraft.level.addParticle(ParticleTypes.END_ROD,
                    point.x + Math.cos(angle) * radius, point.y + lift, point.z + Math.sin(angle) * radius,
                    Math.cos(angle) * 0.35D, 0.4D + minecraft.level.random.nextDouble() * 0.7D,
                    Math.sin(angle) * 0.35D);
        }
        for (int i = 0; i < 70; i++) {
            double angle = minecraft.level.random.nextDouble() * Math.PI * 2.0D;
            double radius = minecraft.level.random.nextDouble() * 4.5D;
            minecraft.level.addParticle(ParticleTypes.ELECTRIC_SPARK,
                    point.x + Math.cos(angle) * radius, point.y + 0.4D + minecraft.level.random.nextDouble() * 8.0D,
                    point.z + Math.sin(angle) * radius,
                    Math.cos(angle) * 0.9D, 0.6D, Math.sin(angle) * 0.9D);
        }
        minecraft.player.playSound(SoundEvents.CONDUIT_ATTACK_TARGET, 1.4F, 0.85F);
        minecraft.level.playLocalSound(point.x, point.y, point.z, SoundEvents.BEACON_ACTIVATE,
                SoundSource.PLAYERS, 4.0F, 0.65F, false);
        minecraft.level.playLocalSound(point.x, point.y, point.z, SoundEvents.BEACON_AMBIENT,
                SoundSource.PLAYERS, 2.5F, 0.70F, false);
    }

    private static void rainSparks() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || lockedGround == null) {
            return;
        }
        for (int i = 0; i < 4; i++) {
            double dx = (minecraft.level.random.nextDouble() - 0.5D) * 5.0D;
            double dz = (minecraft.level.random.nextDouble() - 0.5D) * 5.0D;
            minecraft.level.addParticle(ParticleTypes.ELECTRIC_SPARK,
                    lockedGround.x + dx, lockedGround.y + 10.0D + minecraft.level.random.nextDouble() * 8.0D,
                    lockedGround.z + dz, 0.0D, -0.5D, 0.0D);
        }
    }

    public static void onComputeFov(ViewportEvent.ComputeFov event) {
        if (punch > 0.0F) {
            event.setFOV(event.getFOV() * (1.0D + punch * 0.18D));
        }
        float beam = beamStrength();
        if (beam > 0.0F) {
            Minecraft minecraft = Minecraft.getInstance();
            float time = minecraft.level == null ? 0.0F : minecraft.level.getGameTime();
            float phase = (float) (time % 3600L) + (float) event.getPartialTick();
            float noise = Mth.sin(phase * 6.1F) + 0.5F * Mth.sin(phase * 9.3F);
            event.setFOV(event.getFOV() * (1.0D + 0.003D * noise * beam));
        }
    }

    private static float beamStrength() {
        if (ticks <= LASER_START) {
            return 0.0F;
        }
        int age = ticks - LASER_START;
        if (age >= LASER_HOLD + LASER_FADE) {
            return 0.0F;
        }
        if (age < LASER_HOLD) {
            return 1.0F;
        }
        return 1.0F - (age - LASER_HOLD) / (float) LASER_FADE;
    }

    public static void onComputeCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        float strength = Math.max(shakeTicks / (float) SHAKE_TICKS, beamStrength());
        if (strength <= 0.0F && punch <= 0.0F) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        long time = minecraft.level == null ? 0L : minecraft.level.getGameTime();
        shakeFrames++;
        float phase = shakeFrames * 0.41F;
        if (strength > 0.0F) {
            float yawNoise = Mth.sin(phase * 5.7F) + 0.5F * Mth.sin(phase * 8.9F);
            float pitchNoise = Mth.cos(phase * 6.3F) + 0.5F * Mth.cos(phase * 9.7F);
            float rollNoise = Mth.sin(phase * 4.9F) + 0.5F * Mth.cos(phase * 8.1F);
            event.setYaw(event.getYaw() + yawNoise * 0.4F * strength);
            event.setPitch(event.getPitch() + pitchNoise * 0.33F * strength);
            event.setRoll(event.getRoll() + rollNoise * 0.22F * strength);
        }
        if (punch > 0.0F) {
            event.setPitch(event.getPitch() + punch * 9.0F);
        }
    }

    private static void blade(VertexConsumer consumer, Matrix4f matrix, Vec3 tail, Vec3 dir, Vec3 across,
                              float z0, float z1, float w0, float w1, int colour) {
        Vec3 start = tail.add(dir.scale(z0));
        Vec3 end = tail.add(dir.scale(z1));
        quad(consumer, matrix,
                start.subtract(across.scale(w0)), start.add(across.scale(w0)),
                end.add(across.scale(w1)), end.subtract(across.scale(w1)), colour);
    }

    private static Vec3 billboardSide(Vec3 dir, Vec3 midpoint) {
        Vec3 toCamera = renderCamera == null ? new Vec3(0.0D, 0.0D, 1.0D) : renderCamera.subtract(midpoint);
        Vec3 side = dir.cross(toCamera);
        if (side.lengthSqr() < 1.0E-6D) {
            side = dir.cross(new Vec3(0.0D, 1.0D, 0.0D));
        }
        if (side.lengthSqr() < 1.0E-6D) {
            side = dir.cross(new Vec3(1.0D, 0.0D, 0.0D));
        }
        return side.normalize();
    }

    private static void beam(VertexConsumer consumer, Matrix4f matrix, Vec3 from, Vec3 to,
                             float halfWidth, int colour) {
        Vec3 delta = to.subtract(from);
        double length = delta.length();
        if (length < 0.01D) {
            return;
        }
        Vec3 dir = delta.scale(1.0D / length);
        Vec3 side = billboardSide(dir, from.add(to).scale(0.5D));
        blade(consumer, matrix, from, dir, side, 0.0F, (float) length, halfWidth, halfWidth, colour);
        Vec3 across = dir.cross(side).normalize();
        blade(consumer, matrix, from, dir, across, 0.0F, (float) length, halfWidth, halfWidth, colour);
    }

    private static void cylinder(VertexConsumer consumer, Matrix4f matrix, Vec3 from, Vec3 to,
                                 float radius, int colour, int sides) {
        Vec3 delta = to.subtract(from);
        double length = delta.length();
        if (length < 0.01D || radius <= 0.0F) {
            return;
        }
        Vec3 dir = delta.scale(1.0D / length);
        Vec3 helper = Math.abs(dir.y) > 0.98D ? new Vec3(1.0D, 0.0D, 0.0D) : new Vec3(0.0D, 1.0D, 0.0D);
        Vec3 right = dir.cross(helper).normalize();
        Vec3 up = right.cross(dir).normalize();
        Vec3 midpoint = from.add(to).scale(0.5D);
        Vec3 toCamera = renderCamera == null
                ? new Vec3(0.0D, 0.0D, 1.0D)
                : renderCamera.subtract(midpoint).normalize();

        for (int index = 0; index < sides; index++) {
            double a0 = Math.PI * 2.0D * index / sides;
            double a1 = Math.PI * 2.0D * (index + 1) / sides;
            double am = (a0 + a1) * 0.5D;
            Vec3 n0 = right.scale(Math.cos(a0)).add(up.scale(Math.sin(a0)));
            Vec3 n1 = right.scale(Math.cos(a1)).add(up.scale(Math.sin(a1)));
            Vec3 normal = right.scale(Math.cos(am)).add(up.scale(Math.sin(am)));
            float facing = (float) Math.abs(normal.dot(toCamera));
            int shaded = withAlpha(colour, 0.22F + 0.78F * facing);
            quadSolid(consumer, matrix,
                    from.add(n0.scale(radius)), from.add(n1.scale(radius)),
                    to.add(n1.scale(radius)), to.add(n0.scale(radius)), shaded);
        }
    }

    private static void quadSolid(VertexConsumer consumer, Matrix4f matrix, Vec3 a, Vec3 b, Vec3 c, Vec3 d,
                                  int colour) {
        vertex(consumer, matrix, a, colour, 0.5F, 1.0F);
        vertex(consumer, matrix, b, colour, 0.5F, 1.0F);
        vertex(consumer, matrix, c, colour, 0.5F, 0.0F);
        vertex(consumer, matrix, d, colour, 0.5F, 0.0F);
        vertex(consumer, matrix, d, colour, 0.5F, 0.0F);
        vertex(consumer, matrix, c, colour, 0.5F, 0.0F);
        vertex(consumer, matrix, b, colour, 0.5F, 1.0F);
        vertex(consumer, matrix, a, colour, 0.5F, 1.0F);
    }

    private static void ring(VertexConsumer consumer, Matrix4f matrix, Vec3 centre, float inner, float outer,
                             int colour, int segments) {
        for (int index = 0; index < segments; index++) {
            double a0 = Math.PI * 2.0D * index / segments;
            double a1 = Math.PI * 2.0D * (index + 1) / segments;
            Vec3 i0 = centre.add(Math.cos(a0) * inner, 0.0D, Math.sin(a0) * inner);
            Vec3 o0 = centre.add(Math.cos(a0) * outer, 0.0D, Math.sin(a0) * outer);
            Vec3 o1 = centre.add(Math.cos(a1) * outer, 0.0D, Math.sin(a1) * outer);
            Vec3 i1 = centre.add(Math.cos(a1) * inner, 0.0D, Math.sin(a1) * inner);
            quad(consumer, matrix, i0, o0, o1, i1, colour);
        }
    }

    private static Vec3 radial(Vec3 tail, Vec3 dir, Vec3 right, Vec3 up,
                               float along, float radius, double angle) {
        return tail.add(dir.scale(along))
                .add(right.scale(radius * Math.cos(angle)))
                .add(up.scale(radius * Math.sin(angle)));
    }

    private static void prism(VertexConsumer consumer, Matrix4f matrix, Vec3 tail, Vec3 dir,
                              Vec3 right, Vec3 up, float from, float to, float radius, int colour) {
        for (int index = 0; index < 4; index++) {
            double a0 = Math.PI * 0.5D * index;
            double a1 = a0 + Math.PI * 0.5D;
            quad(consumer, matrix,
                    radial(tail, dir, right, up, from, radius, a0),
                    radial(tail, dir, right, up, from, radius, a1),
                    radial(tail, dir, right, up, to, radius, a1),
                    radial(tail, dir, right, up, to, radius, a0), colour);
        }
    }

    private static void pyramid(VertexConsumer consumer, Matrix4f matrix, Vec3 tail, Vec3 dir,
                                Vec3 right, Vec3 up, float base, float tip, float radius, int colour) {
        Vec3 point = tail.add(dir.scale(tip));
        for (int index = 0; index < 4; index++) {
            double a0 = Math.PI * 0.5D * index;
            double a1 = a0 + Math.PI * 0.5D;
            quad(consumer, matrix,
                    radial(tail, dir, right, up, base, radius, a0),
                    radial(tail, dir, right, up, base, radius, a1),
                    point, point, colour);
        }
    }

    private static void fin(VertexConsumer consumer, Matrix4f matrix, Vec3 tail, Vec3 dir,
                            Vec3 right, Vec3 up, float from, float to, float shaftRadius,
                            float finHeight, double angle) {
        Vec3 top = radial(tail, dir, right, up, from, finHeight, angle);
        Vec3 root = radial(tail, dir, right, up, from, shaftRadius * 0.8F, angle);
        Vec3 front = radial(tail, dir, right, up, to, shaftRadius * 0.8F, angle);
        gradient(consumer, matrix, top, root, front, front,
                withAlpha(ARROW_FIN, 0.65F), withAlpha(ARROW_FIN, 0.40F),
                withAlpha(ARROW_FIN, 0.0F), withAlpha(ARROW_FIN, 0.0F));
    }

    private static void gradient(VertexConsumer consumer, Matrix4f matrix, Vec3 a, Vec3 b, Vec3 c, Vec3 d,
                                 int ca, int cb, int cc, int cd) {
        vertex(consumer, matrix, a, ca, 0.0F, 1.0F);
        vertex(consumer, matrix, b, cb, 1.0F, 1.0F);
        vertex(consumer, matrix, c, cc, 1.0F, 0.0F);
        vertex(consumer, matrix, d, cd, 0.0F, 0.0F);
        vertex(consumer, matrix, d, cd, 0.0F, 0.0F);
        vertex(consumer, matrix, c, cc, 1.0F, 0.0F);
        vertex(consumer, matrix, b, cb, 1.0F, 1.0F);
        vertex(consumer, matrix, a, ca, 0.0F, 1.0F);
    }

    private static void quad(VertexConsumer consumer, Matrix4f matrix, Vec3 a, Vec3 b, Vec3 c, Vec3 d,
                             int colour) {
        gradient(consumer, matrix, a, b, c, d, colour, colour, colour, colour);
    }

    private static void vertex(VertexConsumer consumer, Matrix4f matrix, Vec3 point, int colour, float u, float v) {
        consumer.addVertex(matrix, (float) point.x, (float) point.y, (float) point.z)
                .setColor(colour)
                .setUv(u, v)
                .setOverlay(OverlayTexture.NO_OVERLAY)
                .setLight(LightTexture.FULL_BRIGHT)
                .setNormal(0.0F, 1.0F, 0.0F);
    }


    private static Vec3 slerp(Vec3 from, Vec3 to, float t) {
        double dot = Mth.clamp(from.dot(to), -1.0D, 1.0D);
        double theta = Math.acos(dot);
        if (theta < 1.0E-4D) {
            return to;
        }
        Vec3 axis = from.cross(to);
        if (axis.lengthSqr() < 1.0E-6D) {
            axis = Math.abs(from.y) > 0.9D
                    ? from.cross(new Vec3(1.0D, 0.0D, 0.0D))
                    : from.cross(new Vec3(0.0D, 1.0D, 0.0D));
        }
        axis = axis.normalize();
        double angle = theta * t;
        return from.scale(Math.cos(angle)).add(axis.cross(from).scale(Math.sin(angle))).normalize();
    }

    private static float smoothstep(float value) {
        return value * value * (3.0F - 2.0F * value);
    }

    private static int withAlpha(int argb, float factor) {
        int alpha = Math.round(((argb >>> 24) & 0xFF) * Mth.clamp(factor, 0.0F, 1.0F));
        return (alpha << 24) | (argb & 0x00FFFFFF);
    }
}

