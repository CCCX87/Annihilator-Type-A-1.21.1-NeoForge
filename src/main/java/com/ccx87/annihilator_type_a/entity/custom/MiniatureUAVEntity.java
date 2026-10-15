package com.ccx87.annihilator_type_a.entity.custom;

import com.ccx87.annihilator_type_a.entity.ModEntities;
import com.ccx87.annihilator_type_a.registry.ATAParticles;
import com.ccx87.annihilator_type_a.util.MiniLaser;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.SynchedEntityData.Builder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class MiniatureUAVEntity extends TamableAnimal implements RangedAttackMob {
   private static final EntityDataAccessor<Boolean> DATA_ATTACKING = SynchedEntityData.defineId(MiniatureUAVEntity.class, EntityDataSerializers.BOOLEAN);
   private static final int GROUND_CHECK_INTERVAL = 20;
   private static final float MIN_FLIGHT_HEIGHT = 1.5F;
   private static final float LASER_RANGE = 100.0F;
   private static final int ATTACK_ANIM_DURATION = 10;
   private int groundCheckCooldown = 0;
   private float cachedGroundY = Float.NaN;
   private int shootCooldown = 0;
   private int attackAnimTimer = 0;

   public MiniatureUAVEntity(EntityType<? extends MiniatureUAVEntity> entityType, Level level) {
      super(entityType, level);
      this.setNoGravity(true);
      this.moveControl = new FlyingMoveControl(this, 20, true);
   }

   protected void defineSynchedData(Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DATA_ATTACKING, false);
   }

   public boolean isAttacking() {
      return (Boolean)this.entityData.get(DATA_ATTACKING);
   }

   public void setAttacking(boolean attacking) {
      this.entityData.set(DATA_ATTACKING, attacking);
   }

   private float getScaleFactor() {
      LivingEntity owner = this.getOwner();
      float ownerHeight = owner != null ? owner.getBbHeight() : 1.8F;
      return ownerHeight / 1.8F;
   }

   private void faceTarget(LivingEntity target) {
      if (target != null) {
         double dx = target.getX() - this.getX();
         double dz = target.getZ() - this.getZ();
         double dy = target.getEyeY() - this.getEyeY();
         float yaw = (float)(Math.atan2(dz, dx) * (180.0 / Math.PI)) - 90.0F;
         this.setYRot(yaw);
         this.yRotO = yaw;
         this.setYHeadRot(yaw);
         double horizontal = Math.sqrt(dx * dx + dz * dz);
         float pitch = (float)(-Math.atan2(dy, horizontal) * (180.0 / Math.PI));
         this.setXRot(pitch);
         this.xRotO = pitch;
      }
   }

   private void lookAtTarget(LivingEntity target) {
      if (target != null) {
         this.getLookControl().setLookAt(target, 30.0F, 30.0F);
      }
   }

   protected PathNavigation createNavigation(Level level) {
      return new FlyingPathNavigation(this, level);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(0, new FloatGoal(this));
      this.targetSelector.addGoal(0, new OwnerHurtByTargetGoal(this));
      this.targetSelector.addGoal(2, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(3, new MiniatureUAVEntity.CustomNearestAttackableTargetGoal(this, Monster.class, false));
      this.goalSelector.addGoal(1, new MiniatureUAVEntity.UAVAttackGoal(this));
      this.goalSelector.addGoal(1, new MiniatureUAVEntity.CustomFollowOwnerGoal(this));
      this.goalSelector.addGoal(2, new MiniatureUAVEntity.CustomRandomFlyGoal(this));
   }

   public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
      return PathfinderMob.createMobAttributes()
         .add(Attributes.MAX_HEALTH, 20.0)
         .add(Attributes.MOVEMENT_SPEED, 1.0)
         .add(Attributes.ATTACK_DAMAGE, 3.0)
         .add(Attributes.FOLLOW_RANGE, 16.0)
         .add(Attributes.FLYING_SPEED, 1.0)
         .add(Attributes.ARMOR, 2.0)
         .add(Attributes.ARMOR_TOUGHNESS, 2.0);
   }

   public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
      return false;
   }

   public boolean isFood(ItemStack itemStack) {
      return false;
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel level, AgeableMob otherParent) {
      return (AgeableMob)ModEntities.MINIATUREUAV.get().create(level);
   }

   public void performRangedAttack(LivingEntity target, float distanceFactor) {
      if (!this.level().isClientSide()) {
         if (this.shootCooldown <= 0) {
            if (target != null && target.isAlive()) {
               this.faceTarget(target);
               Vec3 start = new Vec3(this.getX(), this.getEyeY() - 0.1, this.getZ());
               Vec3 direction = new Vec3(target.getX() - this.getX(), target.getY(0.5) - this.getEyeY(), target.getZ() - this.getZ()).normalize();
               Vec3 end = start.add(direction.scale(100.0));
               AABB aabb = new AABB(start, end).inflate(1.0);
               List<LivingEntity> entities = this.level()
                  .getEntitiesOfClass(LivingEntity.class, aabb, e -> e != this && e != this.getOwner() && !e.isAlliedTo(this) && e.isAlive());
               LivingEntity hitEntity = null;
               double minDist = 100.0;

               for (LivingEntity entity : entities) {
                  Vec3 entityPos = entity.getBoundingBox().getCenter();
                  Vec3 toEntity = entityPos.subtract(start);
                  double dot = toEntity.dot(direction);
                  if (!(dot < 0.0) && !(dot > 300.0)) {
                     Vec3 projected = start.add(direction.scale(dot));
                     double distSq = entityPos.distanceToSqr(projected);
                     if (distSq < 0.25) {
                        double dist = entityPos.distanceTo(start);
                        if (dist < minDist) {
                           minDist = dist;
                           hitEntity = entity;
                        }
                     }
                  }
               }

               if (hitEntity != null) {
                  if (this.level() instanceof ServerLevel serverLevel) {
                     SimpleParticleType fireParticle = ATAParticles.Annihilator_Type_A_Fire.get();
                     SimpleParticleType largeFireParticle = ATAParticles.Annihilator_Type_A_Large_Fire.get();
                     if (fireParticle != null && largeFireParticle != null) {
                        Vec3 hitPos = hitEntity.getBoundingBox().getCenter();
                        double steps = 20.0;

                        for (double i = 0.0; i <= 1.0; i += 1.0 / steps) {
                           Vec3 pos = start.lerp(hitPos, i);
                           serverLevel.sendParticles(fireParticle, pos.x, pos.y, pos.z, 2, 0.0, 0.0, 0.0, 0.0);
                        }

                        serverLevel.sendParticles(largeFireParticle, hitPos.x, hitPos.y, hitPos.z, 2, 0.05, 0.05, 0.05, 0.05);
                     }
                  }

                  float scale = this.getScaleFactor();
                  float damage = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE) * Math.max(1.0F, scale);
                  hitEntity.invulnerableTime = 0;
                  LivingEntity owner = this.getOwner();
                  MiniLaser.MiniLaserDamage(hitEntity, damage, this, owner);
               }

               this.shootCooldown = 10;
               this.attackAnimTimer = 10;
               this.setAttacking(true);
            }
         }
      }
   }

   public static float getGroundHeight(Level level, BlockPos pos) {
      int topY = level.getHeight(Types.MOTION_BLOCKING, pos.getX(), pos.getZ());
      return (float)(topY + 1);
   }

   public void setOwner(Player player) {
      this.setOwnerUUID(player.getUUID());
   }

   public void tick() {
      super.tick();
      float scale = this.getScaleFactor();
      if (this.groundCheckCooldown <= 0) {
         this.cachedGroundY = getGroundHeight(this.level(), this.blockPosition());
         this.groundCheckCooldown = 20;
      } else {
         this.groundCheckCooldown--;
      }

      if (!Float.isNaN(this.cachedGroundY)) {
         float targetMinY = this.cachedGroundY + 1.5F * scale;
         if (this.getY() < (double)targetMinY) {
            this.stepTo(this.getX(), (double)targetMinY, this.getZ());
            this.fallDistance = 0.0F;
         }
      }

      LivingEntity owner = this.getOwner();
      if (owner != null) {
         float maxAllowedY = (float)(owner.getY() + 20.0 * (double)scale);
         if (this.getY() > (double)maxAllowedY) {
            this.stepTo(this.getX(), (double)maxAllowedY, this.getZ());
         }
      }

      if (this.shootCooldown > 0) {
         this.shootCooldown--;
      }

      if (this.attackAnimTimer > 0) {
         this.attackAnimTimer--;
         if (this.attackAnimTimer <= 0) {
            this.setAttacking(false);
         }
      }
   }

   private void stepTo(double x, double y, double z) {
      this.move(MoverType.SELF, new Vec3(x - this.getX(), y - this.getY(), z - this.getZ()));
   }

   private static class CustomFollowOwnerGoal extends Goal {
      private final MiniatureUAVEntity mob;
      private LivingEntity owner;
      private int teleportCooldown = 0;
      private static final int TELEPORT_INTERVAL = 20;
      private static final float BASE_MAX_DIST = 30.0F;
      private static final float DIST_SCALE = 10.0F;

      public CustomFollowOwnerGoal(MiniatureUAVEntity mob) {
         this.mob = mob;
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean canUse() {
         this.owner = this.mob.getOwner();
         if (this.owner != null && this.owner.isAlive()) {
            float scale = this.mob.getScaleFactor();
            double maxDist = (double)(30.0F + 10.0F * scale);
            return this.mob.distanceToSqr(this.owner) > maxDist * maxDist;
         } else {
            return false;
         }
      }

      public boolean canContinueToUse() {
         if (this.owner != null && this.owner.isAlive()) {
            float scale = this.mob.getScaleFactor();
            double maxDist = (double)(30.0F + 10.0F * scale);
            return this.mob.distanceToSqr(this.owner) > maxDist * maxDist;
         } else {
            return false;
         }
      }

      public void tick() {
         if (this.owner != null) {
            float scale = this.mob.getScaleFactor();
            double maxDist = (double)(30.0F + 10.0F * scale);
            double dist = this.mob.distanceToSqr(this.owner);
            if (dist > maxDist * maxDist && this.teleportCooldown <= 0) {
               Vec3 ownerPos = this.owner.position();
               boolean left = this.mob.getRandom().nextBoolean();
               double offsetX = left ? -2.0 : 2.0;
               double x = ownerPos.x + offsetX * (1.0 + (double)scale * 0.5);
               double z = ownerPos.z + (this.mob.getRandom().nextDouble() - 0.5) * 2.0;
               double y = ownerPos.y + 1.0 + this.mob.getRandom().nextDouble() * 2.0;
               BlockPos pos = BlockPos.containing(x, y, z);
               if (this.mob.level().getBlockState(pos).isAir()) {
                  this.mob.teleportTo(x, y, z);
                  this.mob.getNavigation().stop();
                  this.teleportCooldown = 20;
               }
            }

            if (this.teleportCooldown > 0) {
               this.teleportCooldown--;
            }
         }
      }

      public void stop() {
         this.owner = null;
         this.mob.getNavigation().stop();
         this.teleportCooldown = 0;
      }
   }

   private static class CustomNearestAttackableTargetGoal<T extends LivingEntity> extends NearestAttackableTargetGoal<T> {
      public CustomNearestAttackableTargetGoal(MiniatureUAVEntity mob, Class<T> targetClass, boolean checkVisibility) {
         super(mob, targetClass, checkVisibility);
      }

      protected double getFollowDistance() {
         return 150.0;
      }
   }

   private static class CustomRandomFlyGoal extends Goal {
      private final MiniatureUAVEntity mob;
      private int cooldown = 0;
      private Vec3 targetPos;
      private static final float BASE_RANGE = 20.0F;
      private static final float RANGE_SCALE = 20.0F;

      public CustomRandomFlyGoal(MiniatureUAVEntity mob) {
         this.mob = mob;
         this.setFlags(EnumSet.of(Flag.MOVE));
      }

      public boolean canUse() {
         return this.mob.getTarget() == null;
      }

      public boolean canContinueToUse() {
         return this.mob.getTarget() == null;
      }

      public void start() {
         this.chooseTarget();
      }

      public void tick() {
         if (this.cooldown-- <= 0) {
            this.chooseTarget();
            this.cooldown = 60 + this.mob.getRandom().nextInt(60);
         }

         if (this.targetPos != null) {
            if (this.mob.distanceToSqr(this.targetPos) < 1.0) {
               this.chooseTarget();
            }

            if (this.targetPos != null) {
               float scale = this.mob.getScaleFactor();
               this.mob.getNavigation().moveTo(this.targetPos.x, this.targetPos.y, this.targetPos.z, 1.0 * (double)scale);
            }
         }
      }

      private void chooseTarget() {
         LivingEntity owner = this.mob.getOwner();
         Vec3 center = owner != null ? owner.position() : this.mob.position();
         float scale = this.mob.getScaleFactor();
         double range = (double)(20.0F + 20.0F * scale);
         double angle;
         if (this.mob.getRandom().nextBoolean()) {
            angle = (Math.PI / 2) + this.mob.getRandom().nextDouble() * Math.PI;
         } else {
            angle = (-Math.PI / 2) + this.mob.getRandom().nextDouble() * Math.PI;
         }

         double radius = range * (0.7 + this.mob.getRandom().nextDouble() * 0.3);
         double dx = radius * Math.cos(angle);
         double dz = radius * Math.sin(angle);
         double dy = 1.0 + this.mob.getRandom().nextDouble() * 2.0;
         double targetX = center.x + dx;
         double targetZ = center.z + dz;
         double targetY = center.y + dy;
         double groundY = (double)this.getGroundHeight(center);
         double minY = groundY + 1.5;
         double maxY = center.y + 20.0;
         if (targetY < minY) {
            targetY = minY;
         }

         if (targetY > maxY) {
            targetY = maxY;
         }

         BlockPos targetBlock = BlockPos.containing(targetX, targetY, targetZ);
         if (this.mob.level().getBlockState(targetBlock).isAir()) {
            this.targetPos = Vec3.atCenterOf(targetBlock);
         } else {
            for (int i = 1; i <= 3; i++) {
               BlockPos up = targetBlock.above(i);
               if (this.mob.level().getBlockState(up).isAir()) {
                  this.targetPos = Vec3.atCenterOf(up);
                  return;
               }
            }

            this.targetPos = null;
         }
      }

      private float getGroundHeight(Vec3 pos) {
         BlockPos blockPos = BlockPos.containing(pos.x, pos.y, pos.z);
         int topY = this.mob.level().getHeight(Types.MOTION_BLOCKING, blockPos.getX(), blockPos.getZ());
         return (float)(topY + 1);
      }

      public void stop() {
         this.targetPos = null;
         this.mob.getNavigation().stop();
      }
   }

   private static class UAVAttackGoal extends Goal {
      private final MiniatureUAVEntity mob;
      private LivingEntity target;
      private int strafingTime = -1;
      private boolean strafingClockwise;
      private boolean strafingBackwards;
      private int seeTime;

      public UAVAttackGoal(MiniatureUAVEntity mob) {
         this.mob = mob;
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean canUse() {
         if (this.mob.getTarget() != null && this.mob.getTarget().isAlive()) {
            this.target = this.mob.getTarget();
            return true;
         } else {
            this.target = this.findNearestTarget();
            if (this.target != null) {
               this.mob.setTarget(this.target);
               return true;
            } else {
               return false;
            }
         }
      }

      private LivingEntity findNearestTarget() {
         float scale = this.mob.getScaleFactor();
         double range = (double)(24.0F + 20.0F * scale);
         AABB aabb = this.mob.getBoundingBox().inflate(range);
         List<LivingEntity> entities = this.mob
            .level()
            .getEntitiesOfClass(
               LivingEntity.class, aabb, e -> e != this.mob && e != this.mob.getOwner() && e.isAlive() && !e.isAlliedTo(this.mob) && e instanceof Monster
            );
         LivingEntity nearest = null;
         double nearestDist = Double.MAX_VALUE;

         for (LivingEntity entity : entities) {
            double dist = this.mob.distanceToSqr(entity);
            if (dist < nearestDist) {
               nearestDist = dist;
               nearest = entity;
            }
         }

         return nearest;
      }

      public boolean canContinueToUse() {
         return this.target != null && this.target.isAlive() && this.mob.getTarget() == this.target;
      }

      public void start() {
         this.strafingTime = -1;
         this.seeTime = 0;
         this.mob.getNavigation().stop();
      }

      public void stop() {
         this.target = null;
         this.mob.getNavigation().stop();
         this.mob.setTarget(null);
      }

      public void tick() {
         if (this.target != null && this.target.isAlive()) {
            double distance = this.mob.distanceToSqr(this.target);
            boolean canSee = this.mob.getSensing().hasLineOfSight(this.target);
            if (canSee) {
               this.seeTime++;
            } else {
               this.seeTime = 0;
            }

            this.mob.faceTarget(this.target);
            this.mob.lookAtTarget(this.target);
            if (!(distance > 225.0) && this.seeTime >= 20) {
               this.mob.getNavigation().stop();
               this.strafingTime++;
            } else {
               this.mob.getNavigation().moveTo(this.target, 1.0);
               this.strafingTime = -1;
            }

            if (this.strafingTime >= 20) {
               if (this.strafingTime == 20) {
                  this.strafingClockwise = this.mob.getRandom().nextBoolean();
                  this.strafingBackwards = this.mob.getRandom().nextBoolean();
               }

               this.mob.getMoveControl().strafe(this.strafingBackwards ? -0.6F : 0.6F, this.strafingClockwise ? 0.6F : -0.6F);
               this.mob.faceTarget(this.target);
            }

            if (this.mob.shootCooldown <= 0) {
               this.mob.performRangedAttack(this.target, 1.0F);
            }
         }
      }
   }
}
