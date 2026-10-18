package com.ccx87.annihilator_type_a.util;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public final class MarkAdjudicateAim {
    public static final double REACH = 30.0D;

    private MarkAdjudicateAim() {}

    public static Vec3 resolve(Level level, Player player) {
        Vec3 eye = player.getEyePosition(1.0F);
        Vec3 look = player.getLookAngle();
        Vec3 end = eye.add(look.scale(REACH));

        HitResult block = level.clip(new ClipContext(eye, end, ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE, player));
        double blockDistance = block.getType() == HitResult.Type.MISS
                ? REACH
                : eye.distanceTo(block.getLocation());

        EntityHitResult entity = ProjectileUtil.getEntityHitResult(level, player, eye, end,
                new AABB(eye, end).inflate(1.0D),
                candidate -> !candidate.isSpectator() && candidate.isAlive() && candidate != player);
        if (entity != null && eye.distanceTo(entity.getLocation()) <= blockDistance) {
            return entity.getLocation();
        }
        if (block.getType() != HitResult.Type.MISS) {
            return block.getLocation();
        }

        Vec3 ground = ground(level, player, end);
        return ground != null ? ground : end;
    }

    public static Vec3 ground(Level level, Player player, Vec3 from) {
        HitResult result = level.clip(new ClipContext(from, from.add(0.0D, -128.0D, 0.0D),
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        return result.getType() == HitResult.Type.MISS ? null : result.getLocation();
    }
}
