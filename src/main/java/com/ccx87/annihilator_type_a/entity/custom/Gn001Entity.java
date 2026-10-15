package com.ccx87.annihilator_type_a.entity.custom;

import com.ccx87.annihilator_type_a.registry.ATAParticles;
import com.ccx87.annihilator_type_a.util.Gn001Sword;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.Goal.Flag;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.OwnerHurtByTargetGoal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class Gn001Entity extends TamableAnimal {
   private static final EntityDataAccessor<Boolean> DATA_MELEE_ATTACK = SynchedEntityData.defineId(Gn001Entity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DATA_RANGED_ATTACK = SynchedEntityData.defineId(Gn001Entity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DATA_SPIN_ATTACK = SynchedEntityData.defineId(Gn001Entity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> DATA_ATTACK_ANIM_TYPE = SynchedEntityData.defineId(Gn001Entity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> DATA_SPAWNING = SynchedEntityData.defineId(Gn001Entity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DATA_DYING = SynchedEntityData.defineId(Gn001Entity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Boolean> DATA_FLYING_MODE = SynchedEntityData.defineId(Gn001Entity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> DATA_FLY_PHASE = SynchedEntityData.defineId(Gn001Entity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> DATA_ULTIMATE_ACTIVE = SynchedEntityData.defineId(Gn001Entity.class, EntityDataSerializers.BOOLEAN);
   private static final EntityDataAccessor<Integer> DATA_ULTIMATE_ENERGY = SynchedEntityData.defineId(Gn001Entity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> DATA_ULTIMATE_ANIM_INDEX = SynchedEntityData.defineId(Gn001Entity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> DATA_ULTIMATE_SUB_PHASE = SynchedEntityData.defineId(Gn001Entity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Integer> DATA_ULTIMATE_FINAL_SUB_PHASE = SynchedEntityData.defineId(Gn001Entity.class, EntityDataSerializers.INT);
   private static final EntityDataAccessor<Boolean> DATA_ULTIMATE_FIRING = SynchedEntityData.defineId(Gn001Entity.class, EntityDataSerializers.BOOLEAN);
   private static final int MELEE_COOLDOWN = 20;
   private static final int RANGED_COOLDOWN = 10;
   private static final int ANIMATION_DURATION = 50;
   private static final double MELEE_RANGE = 6.0;
   private static final double RANGED_RANGE = 16.0;
   private static final int MAX_RANGED_BURST = 3;
   private static final float RANGED_PROJECTILE_SPEED = 2.5F;
   private static final double MOVE_SPEED = 1.2;
   private static final int FLY_SHOOTS_PER_ROUND = 2;
   private static final int FLY_MAX_ROUNDS = 3;
   private static final double BASE_FLY_ALTITUDE = 3.0;
   private static final int FLY_SHOOT_DELAY = 10;
   private static final int FLY_KICK_DELAY = 40;
   private static final float FLY_TRIGGER_CHANCE = 0.2F;
   private static final int ULTIMATE_PASS_COUNT = 7;
   private static final int ULTIMATE_FINAL_RADIUS = 5;
   private static final double ULTIMATE_PASS_EXTRA_DIST = 18.0;
   private static final double ULTIMATE_REPOSITION_DIST_MIN = 18.0;
   private static final double ULTIMATE_REPOSITION_DIST_RANDOM = 14.0;
   private static final double ULTIMATE_FINAL_HEIGHT = 8.0;
   private static final double ULTIMATE_DASH_SPEED = 4.0;
   private static final double ULTIMATE_REPOSITION_SPEED = 2.0;
   private static final int ULTIMATE_FINAL_PAUSE_TICKS = 10;
   private static final double ULTIMATE_FINAL_DIVE_SPEED = 2.2;
   private static final float SPIN_ATTACK_CHANCE = 0.2F;
   private static final int SPIN_ATTACK_DURATION = 17;
   private static final double SPIN_ATTACK_SPEED = 0.5;
   private static final double SPIN_ATTACK_RANGE = 4.0;
   private static final int SPIN_COOLDOWN = 60;
   private static final int COMBO_TIMEOUT = 40;
   private int comboStep = 0;
   private boolean hasCombo = false;
   private int idleComboTimer = 0;
   public int attackStartTick = 0;
   private int idleTimeout = 0;
   private int meleeCooldown = 0;
   private int rangedCooldown = 0;
   public int animTimer = 0;
   public boolean isFlying = false;
   private int rangedBurstCount = 0;
   private int decisionCooldown = 0;
   private int spinAttackTimer = 0;
   private int spinCooldown = 0;
   private boolean isPerformingSpinAttack = false;
   private Vec3 spinDirection = Vec3.ZERO;
   private boolean hasSpinDealtDamage = false;
   private boolean hasDealtKickDamage = false;
   private int spawnTimer = 0;
   private boolean hasInitialSpawn = false;
   private int deathTimer = 0;
   private int flyShootCount = 0;
   private int flyRoundCount = 0;
   private int flyTimer = 0;
   private int flyStallTimer = 0;
   private int flyModeTicks = 0;
   private Vec3 flyTargetPos = null;
   private LivingEntity flyTarget = null;
   private int ultimateEnergy = 0;
   private boolean isUltimateActive = false;
   private int ultimatePhase = 0;
   private int ultimateTick = 0;
   private int ultimatePassCount = 0;
   private Vec3 ultimateTargetPos = null;
   private int ultimateStallTimer = 0;
   private Vec3 ultimateStartPos = null;
   private double ultimateSpeed = 0.6;
   private LivingEntity ultimateTarget = null;
   private int ultimateSubPhase = 0;
   private int ultimateFinalSubPhase = 0;
   private boolean isUltimateFiring = false;
   private int lastUltimateAnimIndex = -1;
   private int ultimateShootCount = 0;
   private int ultimateShootTimer = 0;

   public Gn001Entity(EntityType<? extends TamableAnimal> entityType, Level level) {
      super(entityType, level);
      this.setNoGravity(false);
      this.moveControl = new MoveControl(this);
   }

   public static Builder createAttributes() {
      return PathfinderMob.createMobAttributes()
         .add(Attributes.MAX_HEALTH, 500.0)
         .add(Attributes.MOVEMENT_SPEED, 0.7)
         .add(Attributes.ATTACK_DAMAGE, 40.0)
         .add(Attributes.FOLLOW_RANGE, 32.0)
         .add(Attributes.ARMOR, 20.0)
         .add(Attributes.ARMOR_TOUGHNESS, 5.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 1.0)
         .add(Attributes.JUMP_STRENGTH, 0.5);
   }

   @Nullable
   public AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
      return null;
   }

   protected void defineSynchedData(net.minecraft.network.syncher.SynchedEntityData.Builder builder) {
      super.defineSynchedData(builder);
      builder.define(DATA_MELEE_ATTACK, false);
      builder.define(DATA_RANGED_ATTACK, false);
      builder.define(DATA_SPIN_ATTACK, false);
      builder.define(DATA_ATTACK_ANIM_TYPE, 0);
      builder.define(DATA_SPAWNING, true);
      builder.define(DATA_DYING, false);
      builder.define(DATA_FLYING_MODE, false);
      builder.define(DATA_FLY_PHASE, -1);
      builder.define(DATA_ULTIMATE_ACTIVE, false);
      builder.define(DATA_ULTIMATE_ENERGY, 0);
      builder.define(DATA_ULTIMATE_ANIM_INDEX, 0);
      builder.define(DATA_ULTIMATE_SUB_PHASE, 0);
      builder.define(DATA_ULTIMATE_FINAL_SUB_PHASE, 0);
      builder.define(DATA_ULTIMATE_FIRING, false);
   }

   public int getAttackAnimType() {
      return (Integer)this.entityData.get(DATA_ATTACK_ANIM_TYPE);
   }

   public void setAttackAnimType(int type) {
      this.entityData.set(DATA_ATTACK_ANIM_TYPE, type);
   }

   public boolean isMeleeAttacking() {
      return (Boolean)this.entityData.get(DATA_MELEE_ATTACK);
   }

   public void setMeleeAttacking(boolean val) {
      this.entityData.set(DATA_MELEE_ATTACK, val);
   }

   public boolean isRangedAttacking() {
      return (Boolean)this.entityData.get(DATA_RANGED_ATTACK);
   }

   public void setRangedAttacking(boolean val) {
      this.entityData.set(DATA_RANGED_ATTACK, val);
   }

   public boolean isSpinAttacking() {
      return (Boolean)this.entityData.get(DATA_SPIN_ATTACK);
   }

   public void setSpinAttacking(boolean val) {
      this.entityData.set(DATA_SPIN_ATTACK, val);
   }

   public boolean isSpawning() {
      return (Boolean)this.entityData.get(DATA_SPAWNING);
   }

   public void setSpawning(boolean val) {
      this.entityData.set(DATA_SPAWNING, val);
   }

   public boolean isDying() {
      return (Boolean)this.entityData.get(DATA_DYING);
   }

   public void setDying(boolean val) {
      this.entityData.set(DATA_DYING, val);
   }

   public boolean isFlyingMode() {
      return (Boolean)this.entityData.get(DATA_FLYING_MODE);
   }

   public void setFlyingMode(boolean val) {
      this.entityData.set(DATA_FLYING_MODE, val);
   }

   public int getFlyPhase() {
      return (Integer)this.entityData.get(DATA_FLY_PHASE);
   }

   public void setFlyPhase(int val) {
      this.entityData.set(DATA_FLY_PHASE, val);
   }

   public boolean isUltimateActive() {
      return (Boolean)this.entityData.get(DATA_ULTIMATE_ACTIVE);
   }

   private void setUltimateActive(boolean val) {
      this.entityData.set(DATA_ULTIMATE_ACTIVE, val);
   }

   public int getUltimateEnergy() {
      return (Integer)this.entityData.get(DATA_ULTIMATE_ENERGY);
   }

   private void setUltimateEnergy(int val) {
      this.entityData.set(DATA_ULTIMATE_ENERGY, Math.min(100, Math.max(0, val)));
   }

   private void setUltimateAnimIndex(int index) {
      this.entityData.set(DATA_ULTIMATE_ANIM_INDEX, index);
   }

   public int getUltimateAnimIndex() {
      return (Integer)this.entityData.get(DATA_ULTIMATE_ANIM_INDEX);
   }

   private void setUltimateSubPhase(int val) {
      this.ultimateSubPhase = val;
      this.entityData.set(DATA_ULTIMATE_SUB_PHASE, val);
   }

   public int getUltimateSubPhase() {
      return (Integer)this.entityData.get(DATA_ULTIMATE_SUB_PHASE);
   }

   private void setUltimateFinalSubPhase(int val) {
      this.ultimateFinalSubPhase = val;
      this.entityData.set(DATA_ULTIMATE_FINAL_SUB_PHASE, val);
   }

   public int getUltimateFinalSubPhase() {
      return (Integer)this.entityData.get(DATA_ULTIMATE_FINAL_SUB_PHASE);
   }

   private void setUltimateFiring(boolean val) {
      this.isUltimateFiring = val;
      this.entityData.set(DATA_ULTIMATE_FIRING, val);
   }

   public boolean isUltimateFiring() {
      return (Boolean)this.entityData.get(DATA_ULTIMATE_FIRING);
   }

   protected PathNavigation createNavigation(Level level) {
      return new GroundPathNavigation(this, level);
   }

   protected void registerGoals() {
      this.targetSelector.addGoal(0, new OwnerHurtByTargetGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(2, new Gn001Entity.CustomNearestAttackableTargetGoal(this, Monster.class, false));
      this.goalSelector.addGoal(0, new Gn001Entity.UAVAttackGoal(this));
      this.goalSelector.addGoal(1, new Gn001Entity.CustomFollowOwnerGoal(this));
      this.goalSelector.addGoal(3, new FloatGoal(this));
   }

   private float getScaleFactor() {
      LivingEntity owner = this.getOwner();
      float ownerHeight = owner != null ? owner.getBbHeight() : 1.8F;
      return ownerHeight / 1.8F;
   }

   private void addUltimateEnergy(int amount) {
      if (!this.level().isClientSide()) {
         int newEnergy = Math.min(100, this.ultimateEnergy + amount);
         this.ultimateEnergy = newEnergy;
         this.setUltimateEnergy(newEnergy);
      }
   }

   public void onAttackHit(boolean isSpecialMelee, boolean isRanged, boolean isKick) {
      if (!this.level().isClientSide()) {
         if (!this.isUltimateActive()) {
            int gain;
            if (isRanged) {
               gain = 2;
            } else if (isSpecialMelee) {
               gain = 15;
            } else if (isKick) {
               gain = 15;
            } else {
               gain = 1;
            }

            this.addUltimateEnergy(gain);
         }
      }
   }

   private boolean tryActivateUltimate() {
      if (this.level().isClientSide()) {
         return false;
      } else if (this.isUltimateActive()) {
         return false;
      } else if (this.ultimateEnergy < 100) {
         return false;
      } else if (this.getTarget() != null && this.getTarget().isAlive()) {
         this.startUltimate(this.getTarget());
         return true;
      } else {
         return false;
      }
   }

   private void performMeleeAttack(LivingEntity target) {
      if (!this.level().isClientSide()) {
         if (target != null && target.isAlive()) {
            if (this.meleeCooldown <= 0) {
               boolean shouldSpin = this.random.nextFloat() < 0.2F && this.spinCooldown <= 0 && (double)this.distanceTo(target) <= 4.0;
               if (shouldSpin) {
                  this.comboStep = 0;
                  this.hasCombo = false;
                  this.idleComboTimer = 0;
                  this.performSpinAttack(target);
               } else {
                  int animType;
                  int duration;
                  float damageMultiplier;
                  switch (this.comboStep) {
                     case 0:
                        animType = 0;
                        duration = 10;
                        damageMultiplier = 1.0F;
                        this.comboStep = 1;
                        this.hasCombo = true;
                        break;
                     case 1:
                        animType = 1;
                        duration = 12;
                        damageMultiplier = 0.9F;
                        this.comboStep = 2;
                        this.hasCombo = true;
                        break;
                     case 2:
                        animType = 2;
                        duration = 12;
                        damageMultiplier = 1.1F;
                        this.comboStep = 3;
                        this.hasCombo = true;
                        break;
                     case 3:
                     default:
                        if (this.ultimateEnergy >= 100 && this.tryActivateUltimate()) {
                           this.comboStep = 0;
                           this.hasCombo = false;
                           this.idleComboTimer = 0;
                           return;
                        }

                        animType = 3;
                        duration = 16;
                        damageMultiplier = 1.2F;
                        this.comboStep = 0;
                        this.hasCombo = false;
                        this.idleComboTimer = 0;
                  }

                  this.setAttackAnimType(animType);
                  this.attackStartTick = this.tickCount;
                  float baseDamage = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE) * this.getScaleFactor();
                  float finalDamage = baseDamage * damageMultiplier;
                  target.invulnerableTime = 0;
                  LivingEntity owner = this.getOwner();
                  Gn001Sword.Gn001SwordDamage(target, finalDamage, this, owner);
                  this.onAttackHit(false, false, false);
                  this.setMeleeAttacking(true);
                  this.setRangedAttacking(false);
                  this.setSpinAttacking(false);
                  this.animTimer = duration;
                  this.meleeCooldown = 20;
                  this.rangedBurstCount = 0;
               }
            }
         }
      }
   }

   private void performSpinAttack(LivingEntity target) {
      if (!this.level().isClientSide()) {
         if (target != null && target.isAlive()) {
            this.rangedBurstCount = 0;
            this.isPerformingSpinAttack = true;
            this.spinAttackTimer = 17;
            this.hasSpinDealtDamage = false;
            this.spinCooldown = 60;
            this.spinDirection = target.position().subtract(this.position()).normalize();
            this.meleeCooldown = 20;
            this.rangedCooldown = 10;
            this.setSpinAttacking(true);
            this.setMeleeAttacking(false);
            this.setRangedAttacking(false);
            this.animTimer = 17;
            if (this.level() instanceof ServerLevel serverLevel) {
               Vec3 pos = this.position();
               serverLevel.sendParticles(ATAParticles.Annihilator_Type_A_Large_Fire.get(), pos.x, pos.y + 1.0, pos.z, 15, 0.5, 0.5, 0.5, 0.2);
            }
         }
      }
   }

   private void tickSpinAttack() {
      if (this.isPerformingSpinAttack) {
         if (this.spinAttackTimer > 0) {
            this.spinAttackTimer--;
            Vec3 move = this.spinDirection.scale(0.5);
            this.setDeltaMovement(move.x, 0.0, move.z);
            this.stepTo(this.getX() + move.x, this.getY(), this.getZ() + move.z);
            if (!this.hasSpinDealtDamage) {
               AABB damageBox = this.getBoundingBox().inflate(4.0);

               for (LivingEntity entity : this.level()
                  .getEntitiesOfClass(LivingEntity.class, damageBox, entityx -> entityx != this && entityx != this.getOwner() && entityx.isAlive())) {
                  float damage = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 1.2F * this.getScaleFactor();
                  entity.invulnerableTime = 0;
                  LivingEntity owner = this.getOwner();
                  Gn001Sword.Gn001SwordDamage(entity, damage, this, owner);
                  Vec3 knockback = entity.position().subtract(this.position()).normalize().scale(1.2);
                  entity.setDeltaMovement(entity.getDeltaMovement().add(knockback));
                  this.onAttackHit(true, false, false);
               }

               this.hasSpinDealtDamage = true;
            }

            if (this.spinAttackTimer <= 0) {
               this.isPerformingSpinAttack = false;
               this.setSpinAttacking(false);
               this.setDeltaMovement(0.0, 0.0, 0.0);
            }
         } else {
            this.isPerformingSpinAttack = false;
            this.setSpinAttacking(false);
            this.setDeltaMovement(0.0, 0.0, 0.0);
         }
      }
   }

   private void shootProjectile(LivingEntity target) {
      if (!this.level().isClientSide()) {
         if (target != null && target.isAlive()) {
            float damageMultiplier = 0.7F;
            if (this.isFlyingMode() && !this.isUltimateActive()) {
               damageMultiplier = 1.5F;
            }

            float damage = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE) * damageMultiplier * this.getScaleFactor();
            Gn001Projectile projectile = new Gn001Projectile(this.level(), this, damage);
            float scale = this.getScaleFactor();
            Vec3 launchPos = this.getEyePosition().add(-0.5, -3.7 + (double)scale, 0.0);
            projectile.setPos(launchPos.x, launchPos.y, launchPos.z);
            Vec3 targetPos = target.getBoundingBox().getCenter();
            projectile.shoot(targetPos, 2.5F);
            this.level().addFreshEntity(projectile);
         }
      }
   }

   private void performRangedAttack(LivingEntity target, boolean canTriggerFlyMode) {
      if (!this.level().isClientSide()) {
         if (target != null && target.isAlive()) {
            if (this.rangedCooldown <= 0) {
               this.shootProjectile(target);
               this.setRangedAttacking(true);
               this.setMeleeAttacking(false);
               this.animTimer = 50;
               this.rangedCooldown = 10;
               this.rangedBurstCount++;
               this.onAttackHit(false, true, false);
               if (this.rangedBurstCount >= 3) {
                  this.rangedBurstCount = 0;
                  if (target.isAlive() && this.spinCooldown <= 0) {
                     this.performSpinAttack(target);
                  }
               }

               if (canTriggerFlyMode && !this.isFlyingMode() && this.random.nextFloat() < 0.2F) {
                  this.startFlyingMode(target);
               }
            }
         }
      }
   }

   private void startFlyingMode(LivingEntity target) {
      if (!this.level().isClientSide()) {
         if (target != null && target.isAlive()) {
            this.setMeleeAttacking(false);
            this.setRangedAttacking(false);
            this.setSpinAttacking(false);
            this.isPerformingSpinAttack = false;
            this.setFlyingMode(true);
            this.setFlyPhase(0);
            this.flyShootCount = 0;
            this.flyRoundCount = 0;
            this.flyTimer = 0;
            this.flyStallTimer = 0;
            this.flyModeTicks = 0;
            this.flyTarget = target;
            if (this.getTarget() != target) {
               this.setTarget(target);
            }

            float scale = this.getScaleFactor();
            double altitude = 3.0 * (double)scale;
            double angle = this.random.nextDouble() * 2.0 * Math.PI;
            double dist = 10.0 + this.random.nextDouble() * 8.0;
            double x = target.getX() + dist * Math.cos(angle);
            double z = target.getZ() + dist * Math.sin(angle);
            this.flyTargetPos = new Vec3(x, target.getY() + altitude, z);
            this.setNoAi(true);
            this.setNoGravity(true);
            this.getNavigation().stop();
            this.setDeltaMovement(0.0, 0.0, 0.0);
            if (this.level() instanceof ServerLevel serverLevel) {
               serverLevel.sendParticles(ParticleTypes.CLOUD, this.getX(), this.getY(), this.getZ(), 10, 0.5, 0.5, 0.5, 0.1);
            }
         }
      }
   }

   private void endFlyingMode() {
      if (!this.level().isClientSide()) {
         LivingEntity currentTarget = this.getTarget();
         if (currentTarget != null && !currentTarget.isAlive()) {
            this.setTarget(null);
         }

         this.setFlyingMode(false);
         this.setFlyPhase(-1);
         this.flyTarget = null;
         this.flyTargetPos = null;
         this.setNoAi(false);
         this.setNoGravity(false);
         this.setRangedAttacking(false);
         this.setMeleeAttacking(false);
         this.setDeltaMovement(0.0, 0.0, 0.0);
      }
   }

   private void faceTarget() {
      if (this.flyTarget != null) {
         Vec3 delta = this.flyTarget.position().subtract(this.position());
         double distSq = delta.lengthSqr();
         if (distSq > 0.01) {
            float yRot = (float)(Math.atan2(delta.z, delta.x) * (180.0 / Math.PI)) - 90.0F;
            this.setYRot(yRot);
            this.yRotO = yRot;
         }
      }
   }

   private void tickFlyingMode() {
      if (this.flyTarget != null && this.flyTarget.isAlive()) {
         if (++this.flyModeTicks > 600) {
            this.endFlyingMode();
         } else {
            this.setNoGravity(true);
            this.setNoAi(true);
            this.getLookControl().setLookAt(this.flyTarget, 30.0F, 30.0F);
            int phase = this.getFlyPhase();
            float scale = this.getScaleFactor();
            double altitude = 3.0 * (double)scale;
            switch (phase) {
               case 0:
                  Vec3 currentTarget = this.flyTargetPos;
                  Vec3 currentx = this.position();
                  Vec3 deltax = currentTarget.subtract(currentx);
                  double distSqx = deltax.lengthSqr();
                  if (distSqx < 0.5) {
                     this.stepTo(currentTarget.x, currentTarget.y, currentTarget.z);
                     this.setDeltaMovement(0.0, 0.0, 0.0);
                     this.flyStallTimer = 0;
                     this.setFlyPhase(1);
                     this.flyShootCount = 0;
                     this.flyTimer = 0;
                  } else {
                     double step = 1.2;
                     Vec3 move = deltax.normalize().scale(Math.min(step, deltax.length()));
                     double before = this.position().distanceToSqr(currentTarget);
                     this.stepTo(currentx.x + move.x, currentx.y + move.y, currentx.z + move.z);
                     this.setDeltaMovement(0.0, 0.0, 0.0);
                     if (this.position().distanceToSqr(currentTarget) >= before - 0.01) {
                        if (++this.flyStallTimer > 30) {
                           this.flyStallTimer = 0;
                           this.setFlyPhase(1);
                           this.flyShootCount = 0;
                           this.flyTimer = 0;
                        }
                     } else {
                        this.flyStallTimer = 0;
                     }
                  }
                  break;
               case 1:
                  this.setDeltaMovement(0.0, 0.0, 0.0);
                  this.faceTarget();
                  if (this.flyTimer <= 0) {
                     if (this.flyShootCount < 2) {
                        this.shootProjectile(this.flyTarget);
                        this.flyShootCount++;
                        this.flyTimer = 10;
                     } else {
                        this.setFlyPhase(2);
                        this.flyTimer = 0;
                        double angle = this.random.nextDouble() * 2.0 * Math.PI;
                        double dist = 14.0 + this.random.nextDouble() * 10.0;
                        double x = this.flyTarget.getX() + dist * Math.cos(angle);
                        double z = this.flyTarget.getZ() + dist * Math.sin(angle);
                        this.flyTargetPos = new Vec3(x, this.flyTarget.getY() + altitude, z);
                     }
                  } else {
                     this.flyTimer--;
                  }
                  break;
               case 2:
                  Vec3 current = this.position();
                  Vec3 targetPos = this.flyTargetPos;
                  Vec3 delta = targetPos.subtract(current);
                  double distSq = delta.lengthSqr();
                  if (distSq < 0.5) {
                     this.stepTo(targetPos.x, targetPos.y, targetPos.z);
                     this.setDeltaMovement(0.0, 0.0, 0.0);
                     this.flyRoundCount++;
                     if (this.flyRoundCount >= 3) {
                        this.setFlyPhase(3);
                        this.flyTimer = 40;
                        this.hasDealtKickDamage = false;
                     } else {
                        this.setFlyPhase(1);
                        this.flyShootCount = 0;
                        this.flyTimer = 0;
                     }
                  } else {
                     double step = 1.2;
                     Vec3 move = delta.normalize().scale(Math.min(step, delta.length()));
                     this.stepTo(current.x + move.x, current.y + move.y, current.z + move.z);
                     this.setDeltaMovement(0.0, 0.0, 0.0);
                  }
                  break;
               case 3:
                  if (this.flyTarget == null || !this.flyTarget.isAlive()) {
                     this.endFlyingMode();
                     return;
                  }

                  Vec3 kickPos = this.flyTarget.position();
                  Vec3 currentxx = this.position();
                  Vec3 deltaxx = kickPos.subtract(currentxx);
                  double distSqxx = deltaxx.lengthSqr();
                  this.faceTarget();
                  this.flyTimer--;
                  if (!this.hasDealtKickDamage) {
                     if (distSqxx > 1.0) {
                        double stepx = Math.min(2.0, Math.sqrt(distSqxx));
                        Vec3 movex = deltaxx.normalize().scale(stepx);
                        this.stepTo(currentxx.x + movex.x, currentxx.y + movex.y, currentxx.z + movex.z);
                        this.setDeltaMovement(0.0, 0.0, 0.0);
                     } else {
                        this.setDeltaMovement(0.0, 0.0, 0.0);
                        float damage = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 3.0F * this.getScaleFactor();
                        this.flyTarget.invulnerableTime = 0;
                        LivingEntity owner = this.getOwner();
                        Gn001Sword.Gn001SwordDamage(this.flyTarget, damage, this, owner);
                        this.onAttackHit(false, false, true);
                        this.hasDealtKickDamage = true;
                        if (this.level() instanceof ServerLevel serverLevel) {
                           Vec3 pos = this.flyTarget.position();
                           serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, pos.x, pos.y + 0.5, pos.z, 1, 0.0, 0.0, 0.0, 0.0);
                        }

                        Vec3 away = currentxx.subtract(kickPos);
                        if (away.lengthSqr() < 0.01) {
                           double angle = this.random.nextDouble() * 2.0 * Math.PI;
                           away = new Vec3(Math.cos(angle), 0.0, Math.sin(angle));
                        }

                        away = away.normalize().scale(2.5);
                        this.stepTo(currentxx.x + away.x, currentxx.y, currentxx.z + away.z);
                        this.setDeltaMovement(0.0, 0.0, 0.0);
                     }
                  }

                  if (this.flyTimer <= 0) {
                     this.endFlyingMode();
                  }
                  break;
               default:
                  this.endFlyingMode();
            }
         }
      } else {
         this.endFlyingMode();
      }
   }

   private void lookAtTarget(LivingEntity target) {
      if (target != null) {
         Vec3 delta = target.position().subtract(this.position());
         if (delta.lengthSqr() > 0.001) {
            float yRot = (float)(Math.atan2(delta.z, delta.x) * (180.0 / Math.PI)) - 90.0F;
            this.setYRot(yRot);
            this.yRotO = yRot;
         }
      }
   }

   private void startUltimate(LivingEntity target) {
      if (!this.level().isClientSide()) {
         if (target != null && target.isAlive()) {
            this.isUltimateActive = true;
            this.setUltimateActive(true);
            this.ultimateTarget = target;
            this.ultimatePhase = 0;
            this.setUltimateSubPhase(0);
            this.ultimatePassCount = 0;
            this.ultimateTick = 0;
            this.ultimateEnergy = 0;
            this.setUltimateEnergy(0);
            this.setUltimateAnimIndex(0);
            this.lastUltimateAnimIndex = -1;
            this.ultimateShootCount = 0;
            this.ultimateShootTimer = 0;
            this.setUltimateFinalSubPhase(-1);
            this.setUltimateFiring(false);
            this.setNoAi(true);
            this.setNoGravity(true);
            this.getNavigation().stop();
            this.setDeltaMovement(0.0, 0.0, 0.0);
            Vec3 from = this.position();
            Vec3 toTarget = target.position().subtract(from);
            double distToTarget = toTarget.length();
            if (distToTarget < 0.1) {
               distToTarget = 1.0;
            }

            Vec3 dir = toTarget.normalize();
            Vec3 endPos = target.position().add(dir.scale(18.0));
            this.ultimateTargetPos = new Vec3(endPos.x, this.getY(), endPos.z);
            if (this.level() instanceof ServerLevel serverLevel) {
               serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.HOSTILE, 1.0F, 0.8F);
            }

            this.setUltimateFinalSubPhase(-1);
         }
      }
   }

   private void tickUltimate() {
      if (this.isUltimateActive) {
         if (this.ultimateTarget != null && this.ultimateTarget.isAlive()) {
            this.setNoGravity(true);
            this.setNoAi(true);
            if (this.ultimatePhase == 0) {
               this.tickUltimatePierce();
            } else if (this.ultimatePhase == 1) {
               this.tickUltimateFinal();
            }
         } else {
            this.endUltimate();
         }
      }
   }

   private boolean ultimateStalled(double distanceBefore, Vec3 waypoint) {
      if (this.position().distanceToSqr(waypoint) < distanceBefore - 0.01) {
         this.ultimateStallTimer = 0;
         return false;
      } else {
         return ++this.ultimateStallTimer > 30;
      }
   }

   private void tickUltimatePierce() {
      Vec3 current = this.position();
      Vec3 targetPos = this.ultimateTargetPos;
      Vec3 delta = targetPos.subtract(current);
      double distSq = delta.lengthSqr();
      this.lookAtTarget(this.ultimateTarget);
      if (this.ultimateSubPhase == 0) {
         if (distSq < 0.1) {
            this.ultimatePassCount++;
            this.setUltimateAnimIndex(this.ultimatePassCount % 7);
            this.lastUltimateAnimIndex = -1;
            if (this.ultimatePassCount >= 7) {
               this.ultimatePhase = 1;
               this.ultimateTick = 25;
               this.setUltimateFinalSubPhase(0);
               this.ultimateTargetPos = new Vec3(this.ultimateTarget.getX(), this.ultimateTarget.getY() + 8.0, this.ultimateTarget.getZ());
               if (this.level() instanceof ServerLevel serverLevel) {
                  serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.HOSTILE, 1.0F, 0.5F);
               }

               return;
            }

            this.setUltimateSubPhase(1);
            this.ultimateShootCount = 0;
            this.ultimateShootTimer = 0;
            double angle = this.random.nextDouble() * 2.0 * Math.PI;
            double dist = 18.0 + this.random.nextDouble() * 14.0;
            double x = this.ultimateTarget.getX() + dist * Math.cos(angle);
            double z = this.ultimateTarget.getZ() + dist * Math.sin(angle);
            this.ultimateTargetPos = new Vec3(x, this.getY(), z);
            this.ultimateTick = 0;
         } else {
            double step = 4.0;
            Vec3 move = delta.normalize().scale(Math.min(step, delta.length()));
            double before = this.position().distanceToSqr(targetPos);
            this.stepTo(current.x + move.x, current.y + move.y, current.z + move.z);
            if (this.ultimateStalled(before, targetPos)) {
               this.ultimateStallTimer = 0;
               this.ultimateTargetPos = this.position();
            }

            AABB damageBox = this.getBoundingBox().inflate(2.0);
            List<LivingEntity> entities = this.level()
               .getEntitiesOfClass(LivingEntity.class, damageBox, e -> e != this && e != this.getOwner() && e.isAlive() && !e.isAlliedTo(this));
            if (!entities.isEmpty()) {
               float damage = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 1.5F * this.getScaleFactor();

               for (LivingEntity entity : entities) {
                  entity.invulnerableTime = 0;
                  LivingEntity owner = this.getOwner();
                  Gn001Sword.Gn001SwordDamage(entity, damage, this, owner);
               }
            }

            this.ultimateTick++;
         }
      } else if (distSq < 0.2) {
         this.stepTo(targetPos.x, targetPos.y, targetPos.z);
         this.setDeltaMovement(0.0, 0.0, 0.0);
         Vec3 dirToTarget = this.ultimateTarget.position().subtract(this.position()).normalize();
         Vec3 endPos = this.ultimateTarget.position().add(dirToTarget.scale(18.0));
         this.ultimateTargetPos = new Vec3(endPos.x, this.getY(), endPos.z);
         this.setUltimateSubPhase(0);
         this.lastUltimateAnimIndex = -1;
         this.ultimateShootCount = 0;
         this.ultimateShootTimer = 0;
         this.setUltimateFiring(false);
      } else {
         double stepx = 2.0;
         Vec3 movex = delta.normalize().scale(Math.min(stepx, delta.length()));
         double beforex = this.position().distanceToSqr(targetPos);
         this.stepTo(current.x + movex.x, current.y + movex.y, current.z + movex.z);
         if (this.ultimateStalled(beforex, targetPos)) {
            this.ultimateStallTimer = 0;
            this.ultimateTargetPos = this.position();
         }

         if (this.ultimateShootTimer <= 0) {
            if (this.ultimateShootCount < 2) {
               this.shootProjectile(this.ultimateTarget);
               this.ultimateShootCount++;
               this.ultimateShootTimer = 10;
               this.setUltimateFiring(true);
            }
         } else {
            this.ultimateShootTimer--;
            if (this.ultimateShootTimer <= 0) {
               this.setUltimateFiring(false);
            }
         }
      }
   }

   private void tickUltimateFinal() {
      Vec3 current = this.position();
      Vec3 targetPos = this.ultimateTargetPos;
      Vec3 delta = targetPos.subtract(current);
      double distSq = delta.lengthSqr();
      this.lookAtTarget(this.ultimateTarget);
      if (this.ultimateFinalSubPhase == 0) {
         this.setFlyingMode(true);
         if (this.ultimateTick > 0) {
            if (distSq > 0.5) {
               double step = 1.2;
               Vec3 move = delta.normalize().scale(Math.min(step, delta.length()));
               this.stepTo(current.x + move.x, current.y + move.y, current.z + move.z);
            } else {
               this.setUltimateFinalSubPhase(1);
               this.ultimateTick = 10;
               this.ultimateTargetPos = new Vec3(this.ultimateTarget.getX(), this.ultimateTarget.getY(), this.ultimateTarget.getZ());
               if (this.level() instanceof ServerLevel serverLevel) {
                  serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.HOSTILE, 2.0F, 0.3F);
               }
            }

            this.ultimateTick--;
         }
      } else if (this.ultimateFinalSubPhase == 1) {
         if (this.ultimateTick > 0) {
            this.ultimateTick--;
         } else {
            this.setFlyingMode(false);
            this.setUltimateFinalSubPhase(2);
            this.lastUltimateAnimIndex = -1;
            this.ultimateTargetPos = new Vec3(this.ultimateTarget.getX(), this.ultimateTarget.getY(), this.ultimateTarget.getZ());
            this.ultimateTick = 10;
         }
      } else if (this.ultimateTick > 0) {
         double step = Math.min(2.2, Math.sqrt(distSq));
         Vec3 move = delta.normalize().scale(step);
         this.stepTo(current.x + move.x, current.y + move.y, current.z + move.z);
         this.ultimateTick--;
         AABB damageBox = this.getBoundingBox().inflate(2.0);
         List<LivingEntity> entities = this.level()
            .getEntitiesOfClass(LivingEntity.class, damageBox, e -> e != this && e != this.getOwner() && e.isAlive() && !e.isAlliedTo(this));
         if (!entities.isEmpty()) {
            float damage = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 2.0F * this.getScaleFactor();

            for (LivingEntity entity : entities) {
               entity.invulnerableTime = 0;
               LivingEntity owner = this.getOwner();
               Gn001Sword.Gn001SwordDamage(entity, damage, this, owner);
            }
         }
      } else {
         if (this.level() instanceof ServerLevel serverLevel) {
            AABB finalBox = this.getBoundingBox().inflate(5.0);
            List<LivingEntity> entities = serverLevel.getEntitiesOfClass(
               LivingEntity.class, finalBox, e -> e != this && e != this.getOwner() && e.isAlive() && !e.isAlliedTo(this)
            );
            float damage = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE) * 4.0F * this.getScaleFactor();

            for (LivingEntity entity : entities) {
               entity.invulnerableTime = 0;
               LivingEntity owner = this.getOwner();
               Gn001Sword.Gn001SwordDamage(entity, damage, this, owner);
               Vec3 knockDir = entity.position().subtract(this.position()).normalize().scale(2.0);
               entity.setDeltaMovement(entity.getDeltaMovement().add(knockDir));
            }

            serverLevel.sendParticles(ParticleTypes.EXPLOSION_EMITTER, this.getX(), this.getY() + 0.5, this.getZ(), 1, 0.0, 0.0, 0.0, 0.0);
            serverLevel.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 3.0F, 0.5F);
         }

         if (this.ultimateTarget != null && this.ultimateTarget.isAlive()) {
            Vec3 away = this.position().subtract(this.ultimateTarget.position());
            if (away.lengthSqr() < 0.1) {
               double angle = this.random.nextDouble() * 2.0 * Math.PI;
               away = new Vec3(Math.cos(angle), 0.0, Math.sin(angle));
            }

            away = away.normalize().scale(3.0);
            this.stepTo(this.getX() + away.x, this.getY(), this.getZ() + away.z);
         }

         this.endUltimate();
      }
   }

   private void endUltimate() {
      if (!this.level().isClientSide()) {
         this.isUltimateActive = false;
         this.setUltimateActive(false);
         this.ultimateTarget = null;
         this.ultimateTargetPos = null;
         this.setFlyingMode(false);
         this.setFlyPhase(-1);
         this.setNoAi(false);
         this.setNoGravity(false);
         this.setDeltaMovement(0.0, 0.0, 0.0);
         this.setUltimateFiring(false);
         this.lastUltimateAnimIndex = -1;
      }
   }

   public void tick() {
      super.tick();
      if (this.isUltimateActive()) {
         if (!this.level().isClientSide) {
            this.tickUltimate();
         }
      } else if (this.isFlyingMode()) {
         if (!this.level().isClientSide) {
            this.tickFlyingMode();
         }
      } else {
         if (!this.level().isClientSide && !this.hasInitialSpawn) {
            this.spawnTimer = 80;
            this.setSpawning(true);
            this.hasInitialSpawn = true;
            this.setNoAi(true);
         }

         if (!this.level().isClientSide && this.isSpawning()) {
            ServerLevel serverLevel = (ServerLevel)this.level();
            Vec3 pos = this.position();
            if (this.spawnTimer > 0) {
               this.spawnTimer--;
               if (this.spawnTimer > 40) {
                  for (int i = 0; i < 50; i++) {
                     double height = 6.0 + this.random.nextDouble() * 14.0;
                     double radius = 0.8 + this.random.nextDouble() * 1.5;
                     double angle = this.random.nextDouble() * 2.0 * Math.PI;
                     double x = pos.x + radius * Math.cos(angle);
                     double z = pos.z + radius * Math.sin(angle);
                     double y = pos.y + height;
                     serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, x, y, z, 1, 0.0, -0.5, 0.0, 0.02);
                  }

                  for (int i = 0; i < 30; i++) {
                     double height = 4.0 + this.random.nextDouble() * 10.0;
                     double radius = 0.3 + this.random.nextDouble() * 0.6;
                     double angle = this.random.nextDouble() * 2.0 * Math.PI;
                     double x = pos.x + radius * Math.cos(angle);
                     double z = pos.z + radius * Math.sin(angle);
                     double y = pos.y + height;
                     serverLevel.sendParticles(ParticleTypes.WARPED_SPORE, x, y, z, 1, 0.0, -0.3, 0.0, 0.01);
                  }

                  for (int i = 0; i < 30; i++) {
                     double height = 2.0 + this.random.nextDouble() * 12.0;
                     double radius = 2.0 + Math.sin((double)this.tickCount * 0.1 + (double)i) * 0.5;
                     double angle = (double)this.tickCount * 0.15 + (double)i * 0.4;
                     double x = pos.x + radius * Math.cos(angle);
                     double z = pos.z + radius * Math.sin(angle);
                     double y = pos.y + height;
                     serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, x, y, z, 1, 0.0, 0.3, 0.0, 0.01);
                  }

                  for (int i = 0; i < 15; i++) {
                     double radius = 1.5 + this.random.nextDouble() * 3.0;
                     double angle = this.random.nextDouble() * 2.0 * Math.PI;
                     double x = pos.x + radius * Math.cos(angle);
                     double z = pos.z + radius * Math.sin(angle);
                     serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, x, pos.y + 0.1, z, 1, 0.0, 0.1, 0.0, 0.01);
                  }
               } else if (this.spawnTimer > 20) {
                  for (int i = 0; i < 30; i++) {
                     double height = 2.0 + this.random.nextDouble() * 8.0;
                     double radius = 0.4 + this.random.nextDouble() * 0.8;
                     double angle = this.random.nextDouble() * 2.0 * Math.PI;
                     double x = pos.x + radius * Math.cos(angle);
                     double z = pos.z + radius * Math.sin(angle);
                     double y = pos.y + height;
                     serverLevel.sendParticles(
                        this.random.nextBoolean() ? ParticleTypes.HAPPY_VILLAGER : ParticleTypes.WARPED_SPORE, x, y, z, 1, 0.0, -0.3, 0.0, 0.01
                     );
                  }

                  if (this.spawnTimer % 2 == 0) {
                     int count = 40 + (40 - this.spawnTimer) * 2;

                     for (int i = 0; i < count; i++) {
                        double radius = 2.0 + (double)(40 - this.spawnTimer) * 0.2;
                        double angle = this.random.nextDouble() * 2.0 * Math.PI;
                        double x = pos.x + radius * Math.cos(angle);
                        double z = pos.z + radius * Math.sin(angle);
                        serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, x, pos.y + 0.1, z, 1, 0.0, 0.05, 0.0, 0.01);
                     }

                     for (int i = 0; i < 20; i++) {
                        double radius = 0.5 + (double)(40 - this.spawnTimer) * 0.08;
                        double angle = (double)this.tickCount * 0.1 + (double)i * (Math.PI / 10);
                        double x = pos.x + radius * Math.cos(angle);
                        double z = pos.z + radius * Math.sin(angle);
                        serverLevel.sendParticles(ParticleTypes.WARPED_SPORE, x, pos.y + 0.3, z, 1, 0.0, 0.1, 0.0, 0.02);
                     }
                  }
               } else {
                  if (this.spawnTimer == 10) {
                     for (int i = 0; i < 200; i++) {
                        double radius = 1.5 + this.random.nextDouble() * 8.0;
                        double angle = this.random.nextDouble() * 2.0 * Math.PI;
                        double x = pos.x + radius * Math.cos(angle);
                        double z = pos.z + radius * Math.sin(angle);
                        double y = pos.y + this.random.nextDouble() * 4.0;
                        serverLevel.sendParticles(
                           (double)this.random.nextFloat() > 0.3 ? ParticleTypes.HAPPY_VILLAGER : ParticleTypes.WARPED_SPORE, x, y, z, 2, 0.3, 0.3, 0.3, 0.1
                        );
                     }
                  }

                  if (this.spawnTimer % 2 == 0 && this.spawnTimer > 0) {
                     serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, pos.x, pos.y + 1.5, pos.z, 20, 0.5, 0.5, 0.5, 0.05);
                  }

                  if (this.spawnTimer == 0) {
                     this.setSpawning(false);
                     this.setNoAi(false);

                     for (int i = 0; i < 80; i++) {
                        double radius = this.random.nextDouble() * 5.0;
                        double angle = this.random.nextDouble() * 2.0 * Math.PI;
                        double x = pos.x + radius * Math.cos(angle);
                        double z = pos.z + radius * Math.sin(angle);
                        double y = pos.y + this.random.nextDouble() * 3.0;
                        serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, x, y, z, 1, 0.1, 0.1, 0.1, 0.05);
                     }
                  }
               }
            }
         }

         if (this.isSpawning()) {
            this.getNavigation().stop();
            this.setDeltaMovement(0.0, 0.0, 0.0);
         }

         if (this.isDying()) {
            this.setNoAi(true);
            this.getNavigation().stop();
            this.setDeltaMovement(0.0, 0.0, 0.0);
            this.setNoGravity(true);
            this.setXRot(0.0F);
            this.setYRot(this.yRotO);
            this.deathTime = 0;
            if (this.deathTimer > 0) {
               this.deathTimer--;
               if (this.deathTimer == 0) {
                  this.remove(RemovalReason.KILLED);
               }
            }
         }

         if (!this.isDying() && !this.isSpawning()) {
            if (this.meleeCooldown > 0) {
               this.meleeCooldown--;
            }

            if (this.rangedCooldown > 0) {
               this.rangedCooldown--;
            }

            if (this.decisionCooldown > 0) {
               this.decisionCooldown--;
            }

            if (this.spinCooldown > 0) {
               this.spinCooldown--;
            }

            if (this.animTimer > 0) {
               this.animTimer--;
               if (this.animTimer <= 0) {
                  this.setMeleeAttacking(false);
                  this.setRangedAttacking(false);
                  if (!this.isPerformingSpinAttack) {
                     this.setSpinAttacking(false);
                  }
               }
            }

            if (this.animTimer > 0 || this.isMeleeAttacking() || this.isPerformingSpinAttack) {
               this.idleComboTimer = 0;
            } else if (this.hasCombo) {
               this.idleComboTimer++;
               if (this.idleComboTimer > 40) {
                  this.comboStep = 0;
                  this.hasCombo = false;
                  this.idleComboTimer = 0;
               }
            }

            if (this.isPerformingSpinAttack) {
               this.tickSpinAttack();
            }
         }
      }
   }

   public void die(DamageSource damageSource) {
      if (!this.isDying()) {
         if (this.isFlyingMode()) {
            this.endFlyingMode();
         }

         if (this.isUltimateActive()) {
            this.endUltimate();
         }

         this.setDying(true);
         this.deathTimer = 40;
         this.setNoAi(true);
         this.getNavigation().stop();
         this.setDeltaMovement(0.0, 0.0, 0.0);
         this.setNoGravity(true);
         super.die(damageSource);
      }
   }

   public boolean causeFallDamage(float fallDistance, float multiplier, DamageSource source) {
      return false;
   }

   public boolean isFood(ItemStack stack) {
      return false;
   }

   public void setOwner(Player player) {
      this.setOwnerUUID(player.getUUID());
   }

   private void stepTo(double x, double y, double z) {
      this.move(MoverType.SELF, new Vec3(x - this.getX(), y - this.getY(), z - this.getZ()));
   }

   private static class CustomFollowOwnerGoal extends Goal {
      private final Gn001Entity mob;
      private LivingEntity owner;
      private int teleportCooldown = 0;
      private static final int TELEPORT_INTERVAL = 20;
      private static final float BASE_MAX_DIST = 20.0F;
      private static final float DIST_SCALE = 2.0F;

      public CustomFollowOwnerGoal(Gn001Entity mob) {
         this.mob = mob;
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean canUse() {
         if (!this.mob.isSpawning() && !this.mob.isDying() && !this.mob.isFlyingMode() && !this.mob.isUltimateActive()) {
            this.owner = this.mob.getOwner();
            if (this.owner != null && this.owner.isAlive()) {
               float scale = this.mob.getScaleFactor();
               double maxDist = (double)(20.0F + 2.0F * scale);
               return this.mob.distanceToSqr(this.owner) > maxDist * maxDist;
            } else {
               return false;
            }
         } else {
            return false;
         }
      }

      public boolean canContinueToUse() {
         if (this.mob.isSpawning() || this.mob.isDying() || this.mob.isFlyingMode() || this.mob.isUltimateActive()) {
            return false;
         } else if (this.owner != null && this.owner.isAlive()) {
            float scale = this.mob.getScaleFactor();
            double maxDist = (double)(20.0F + 2.0F * scale);
            return this.mob.distanceToSqr(this.owner) > maxDist * maxDist;
         } else {
            return false;
         }
      }

      public void tick() {
         if (!this.mob.isSpawning() && !this.mob.isDying() && !this.mob.isFlyingMode() && !this.mob.isUltimateActive()) {
            if (this.owner != null) {
               float scale = this.mob.getScaleFactor();
               double maxDist = (double)(20.0F + 2.0F * scale);
               double dist = this.mob.distanceToSqr(this.owner);
               if (dist > maxDist * maxDist) {
                  if (this.teleportCooldown <= 0) {
                     Vec3 ownerPos = this.owner.position();
                     boolean left = this.mob.getRandom().nextBoolean();
                     double offsetX = left ? -2.0 : 2.0;
                     double x = ownerPos.x + offsetX * (1.0 + (double)scale * 0.5);
                     double z = ownerPos.z + (this.mob.getRandom().nextDouble() - 0.5) * 2.0;
                     double y = ownerPos.y + 0.5;
                     BlockPos pos = BlockPos.containing(x, y, z);
                     if (this.mob.level().getBlockState(pos.below()).isSolid() && this.mob.level().getBlockState(pos).isAir()) {
                        this.mob.teleportTo(x, y, z);
                        this.mob.getNavigation().stop();
                        this.teleportCooldown = 20;
                     }
                  } else {
                     this.mob.getNavigation().moveTo(this.owner, 1.0);
                  }
               } else {
                  this.mob.getNavigation().stop();
               }

               if (this.teleportCooldown > 0) {
                  this.teleportCooldown--;
               }
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
      private final Gn001Entity mob;

      public CustomNearestAttackableTargetGoal(Gn001Entity mob, Class<T> targetClass, boolean checkVisibility) {
         super(mob, targetClass, checkVisibility);
         this.mob = mob;
      }

      protected double getFollowDistance() {
         return 50.0;
      }

      public boolean canUse() {
         return !this.mob.isFlyingMode() && !this.mob.isUltimateActive() ? super.canUse() : false;
      }
   }

   private static class UAVAttackGoal extends Goal {
      private final Gn001Entity mob;
      private LivingEntity target;
      private int attackCooldown = 0;
      private static final int COOLDOWN = 10;
      private boolean isRangedPhase = false;

      public UAVAttackGoal(Gn001Entity mob) {
         this.mob = mob;
         this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
      }

      public boolean canUse() {
         if (this.mob.isSpawning() || this.mob.isDying() || this.mob.isFlyingMode() || this.mob.isUltimateActive()) {
            return false;
         } else if (this.mob.getTarget() != null && this.mob.getTarget().isAlive()) {
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
         double range = 16.0 + (double)(10.0F * scale);
         AABB aabb = this.mob.getBoundingBox().inflate(range);
         List<LivingEntity> entities = this.mob
            .level()
            .getEntitiesOfClass(
               LivingEntity.class, aabb, e -> e != this.mob && e != this.mob.getOwner() && e.isAlive() && !e.isAlliedTo(this.mob) && e instanceof Monster && isReachableTarget(this.mob, e)
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
         if (this.mob.isSpawning() || this.mob.isDying() || this.mob.isFlyingMode() || this.mob.isUltimateActive()) {
            return false;
         } else {
            return this.mob.isPerformingSpinAttack ? true : this.target != null && this.target.isAlive() && this.mob.getTarget() == this.target;
         }
      }

      public void start() {
         this.attackCooldown = 0;
         this.isRangedPhase = false;
         this.mob.getNavigation().stop();
      }

      public void stop() {
         this.target = null;
         this.mob.getNavigation().stop();
         this.mob.setTarget(null);
         this.isRangedPhase = false;
      }

      public void tick() {
         if (!this.mob.isSpawning() && !this.mob.isDying() && !this.mob.isFlyingMode() && !this.mob.isUltimateActive()) {
            if (this.target != null && this.target.isAlive()) {
               if (!this.mob.isPerformingSpinAttack) {
                  double distance = this.mob.distanceToSqr(this.target);
                  this.mob.getLookControl().setLookAt(this.target, 30.0F, 30.0F);
                  if (this.attackCooldown > 0) {
                     this.attackCooldown--;
                  }

                  if (!this.target.isAlive()) {
                     this.target = this.findNearestTarget();
                     if (this.target == null) {
                        this.mob.setTarget(null);
                        return;
                     }

                     this.mob.setTarget(this.target);
                     distance = this.mob.distanceToSqr(this.target);
                  }

                  double moveSpeed = this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED);
                  if (distance <= 36.0) {
                     this.isRangedPhase = false;
                     if (distance > 2.25) {
                        this.mob.getNavigation().moveTo(this.target, moveSpeed);
                     } else {
                        this.mob.getNavigation().stop();
                     }

                     if (this.mob.meleeCooldown <= 0 && this.attackCooldown <= 0) {
                        this.mob.performMeleeAttack(this.target);
                        this.attackCooldown = 10;
                     }
                  } else if (distance <= 256.0) {
                     this.isRangedPhase = true;
                     this.mob.getNavigation().moveTo(this.target, moveSpeed * 0.4);
                     if (this.mob.rangedCooldown <= 0 && this.attackCooldown <= 0) {
                        this.mob.performRangedAttack(this.target, true);
                        this.attackCooldown = 10;
                     }
                  } else {
                     this.isRangedPhase = false;
                     this.mob.getNavigation().moveTo(this.target, moveSpeed);
                  }
               }
            }
         }
      }
   }


   private static boolean isReachableTarget(net.minecraft.world.entity.Mob mech, LivingEntity candidate) {
      if (!mech.hasLineOfSight(candidate)) {
         return false;
      }
      double dy = candidate.getY() - mech.getY();
      return dy > -12.0 && dy < 20.0;
   }
}
