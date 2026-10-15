package com.ccx87.annihilator_type_a.entity.custom;

import com.ccx87.annihilator_type_a.entity.ModEntities;
import com.ccx87.annihilator_type_a.registry.ATAParticles;
import com.ccx87.annihilator_type_a.util.Gn001Sword;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class Gn001Projectile extends Projectile {
   private LivingEntity owner;
   private float damage;

   public Gn001Projectile(EntityType<? extends Projectile> entityType, Level level) {
      super(entityType, level);
   }

   public Gn001Projectile(Level level, LivingEntity owner, float damage) {
      super(ModEntities.GN001_PROJECTILE.get(), level);
      this.owner = owner;
      this.damage = damage;
      this.setPos(owner.getX(), owner.getEyeY() - 0.1, owner.getZ());
      this.setNoGravity(true);
   }

   protected void defineSynchedData(Builder builder) {
   }

   public void tick() {
      super.tick();
      if (this.level().isClientSide()) {
         for (int i = 0; i < 3; i++) {
            this.level()
               .addParticle(
                  (ParticleOptions)ATAParticles.Annihilator_Type_A_Large_Fire.get(),
                  this.getX() + (this.random.nextDouble() - 0.5) * 0.3,
                  this.getY() + (this.random.nextDouble() - 0.5) * 0.3,
                  this.getZ() + (this.random.nextDouble() - 0.5) * 0.3,
                  0.0,
                  0.0,
                  0.0
               );
         }
      }

      HitResult hitResult = this.findHitResult();
      if (hitResult.getType() != Type.MISS) {
         this.onHit(hitResult);
      }

      Vec3 delta = this.getDeltaMovement();
      this.setPos(this.getX() + delta.x, this.getY() + delta.y, this.getZ() + delta.z);
      this.setDeltaMovement(delta.x, delta.y, delta.z);
      if (this.tickCount > 60) {
         this.remove(RemovalReason.DISCARDED);
      }
   }

   private HitResult findHitResult() {
      Vec3 start = this.position();
      Vec3 end = start.add(this.getDeltaMovement());
      EntityHitResult entityHit = ProjectileUtil.getEntityHitResult(
         this.level(),
         this,
         start,
         end,
         this.getBoundingBox().expandTowards(this.getDeltaMovement()),
         entity -> !entity.isSpectator() && entity.isAlive() && entity != this.owner && !(entity instanceof Player)
      );
      return (HitResult)(entityHit != null ? entityHit : this.level().clip(new ClipContext(start, end, Block.COLLIDER, Fluid.NONE, this)));
   }

   protected void onHitEntity(EntityHitResult result) {
      if (!(result.getEntity() instanceof Player)) {
         if (result.getEntity() instanceof LivingEntity target && this.owner != null) {
            Gn001Sword.Gn001SwordDamage(target, this.damage, this.owner, this.owner);
            Vec3 knockback = target.position().subtract(this.position()).normalize().scale(0.5);
            target.setDeltaMovement(target.getDeltaMovement().add(knockback));
            if (this.level() instanceof ServerLevel serverLevel) {
               Vec3 pos = target.getBoundingBox().getCenter();
               serverLevel.sendParticles(ATAParticles.Annihilator_Type_A_Large_Fire.get(), pos.x, pos.y, pos.z, 15, 0.5, 0.5, 0.5, 0.2);
            }
         }

         this.remove(RemovalReason.DISCARDED);
      }
   }

   protected void onHitBlock(BlockHitResult result) {
      if (this.level() instanceof ServerLevel serverLevel) {
         serverLevel.sendParticles(
            ATAParticles.Annihilator_Type_A_Large_Fire.get(),
            (double)result.getBlockPos().getX() + 0.5,
            (double)result.getBlockPos().getY() + 0.5,
            (double)result.getBlockPos().getZ() + 0.5,
            5,
            0.3,
            0.3,
            0.3,
            0.1
         );
      }

      this.remove(RemovalReason.DISCARDED);
   }

   public void shoot(Vec3 targetPos, float speed) {
      Vec3 direction = targetPos.subtract(this.position()).normalize();
      this.setDeltaMovement(direction.scale((double)speed));
   }

   protected void defineSynchedData() {
   }

   public boolean isNoGravity() {
      return false;
   }
}
