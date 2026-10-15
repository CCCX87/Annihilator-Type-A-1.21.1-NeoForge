package com.ccx87.annihilator_type_a.entity.client;

import net.minecraft.world.entity.AnimationState;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public final class Gn001ClientAnimStates {

    public static final class States {
        public final AnimationState walk = new AnimationState();
        public final AnimationState idle = new AnimationState();
        public final AnimationState useItemRight = new AnimationState();
        public final AnimationState breath = new AnimationState();
        public final AnimationState fly = new AnimationState();
        public final AnimationState flySoaring = new AnimationState();
        public final AnimationState flyDive = new AnimationState();
        public final AnimationState flyDiveAlt = new AnimationState();
        public final AnimationState flySpin = new AnimationState();
        public final AnimationState flyLand = new AnimationState();
        public final AnimationState flyLandEnd = new AnimationState();
        public final AnimationState castMassBuff = new AnimationState();
        public final AnimationState selfBuff = new AnimationState();
        public final AnimationState sleep = new AnimationState();
        public final AnimationState jump = new AnimationState();
        public final AnimationState land = new AnimationState();
        public final AnimationState fallLoop = new AnimationState();
        public final AnimationState sneak = new AnimationState();
        public final AnimationState swim = new AnimationState();
        public final AnimationState swimFast = new AnimationState();
        public final AnimationState statuePose1 = new AnimationState();
        public final AnimationState attack1 = new AnimationState();
        public final AnimationState attack2 = new AnimationState();
        public final AnimationState attack3 = new AnimationState();
        public final AnimationState appearance = new AnimationState();
        public final AnimationState death = new AnimationState();
        public final AnimationState flyFire = new AnimationState();
        public final AnimationState flyKick = new AnimationState();
        public final AnimationState blitzAttack1 = new AnimationState();
        public final AnimationState blitzAttack2 = new AnimationState();
        public final AnimationState blitzAttack3 = new AnimationState();
        public final AnimationState blitzAttack4 = new AnimationState();
        public final AnimationState blitzAttack5 = new AnimationState();
        public final AnimationState blitzAttack6 = new AnimationState();
        public final AnimationState blitzAttack7 = new AnimationState();
        public final AnimationState downwardChop = new AnimationState();

        public int idleTimeout = 0;

        public int lastBlitzIndex = -1;
    }

    private static final Map<UUID, States> CACHE = new HashMap<>();

    public static States get(UUID id) {
        return CACHE.computeIfAbsent(id, k -> new States());
    }

    public static void remove(UUID id) {
        CACHE.remove(id);
    }

    private Gn001ClientAnimStates() {
    }
}