package com.ccx87.annihilator_type_a.client;

import com.ccx87.annihilator_type_a.network.MarkAdjudicatePayload;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;

public final class MarkAdjudicateRemote {
    private static final int LIFETIME = 300;
    private static final int ORB_START = 19;
    private static final int ORB_LENGTH = 45;
    private static final int ARROW_START = 60;
    private static final int BEAM_TICKS = 124;

    private static final List<Cast> CASTS = new ArrayList<>();

    private MarkAdjudicateRemote() {}

    public static void add(MarkAdjudicatePayload payload) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player != null && minecraft.player.getId() == payload.casterId()) {
            return;
        }
        CASTS.add(new Cast(payload.casterId(),
                new Vec3(payload.targetX(), payload.targetY(), payload.targetZ()),
                new Vec3(payload.groundX(), payload.groundY(), payload.groundZ())));
    }

    public static void clear() {
        CASTS.clear();
    }

    public static void tick(Minecraft minecraft) {
        if (CASTS.isEmpty()) {
            return;
        }
        for (Iterator<Cast> iterator = CASTS.iterator(); iterator.hasNext(); ) {
            Cast cast = iterator.next();
            cast.age++;
            int arrowTick = cast.age - ARROW_START;
            if (arrowTick == MarkAdjudicateBeams.arrowImpactTick()) {
                MarkAdjudicateBeams.playRemoteMark(cast.target);
            } else if (arrowTick == MarkAdjudicateBeams.arrowLaserTick()) {
                MarkAdjudicateBeams.playRemoteLaser(cast.target);
                if (minecraft.player != null) {
                    MarkAdjudicateBeams.remoteShake(minecraft.player.position().distanceTo(cast.target));
                }
            } else if (arrowTick > MarkAdjudicateBeams.arrowLaserTick()
                    && arrowTick < MarkAdjudicateBeams.arrowLaserTick() + BEAM_TICKS) {
                if (minecraft.player != null) {
                    MarkAdjudicateBeams.remoteShake(minecraft.player.position().distanceTo(cast.target));
                }
            }
            if (cast.age > LIFETIME) {
                iterator.remove();
            }
        }
    }

    public static void render(RenderLevelStageEvent event) {
        if (CASTS.isEmpty() || event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES
                || !ClientConfig.loaded()) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null) {
            return;
        }
        for (Cast cast : CASTS) {
            Entity entity = minecraft.level.getEntity(cast.casterId);
            if (!(entity instanceof Player caster)) {
                continue;
            }
            Vec3 position = caster.position();
            float size = caster instanceof AbstractClientPlayer local
                    ? AnnihilationRingRenderer.ringScale(local)
                    : 1.0F;
            int orbTick = cast.age - ORB_START;
            if (orbTick >= 1 && orbTick <= ORB_LENGTH) {
                MarkAdjudicateOrbs.renderAt(event, position, size, orbTick / (float) ORB_LENGTH);
            }
            int arrowTick = cast.age - ARROW_START;
            if (arrowTick >= 1) {
                MarkAdjudicateBeams.renderAt(event, position, size, arrowTick, cast.target, cast.ground);
            }
        }
    }

    private static final class Cast {
        private final int casterId;
        private final Vec3 target;
        private final Vec3 ground;
        private int age;

        private Cast(int casterId, Vec3 target, Vec3 ground) {
            this.casterId = casterId;
            this.target = target;
            this.ground = ground;
        }
    }
}
