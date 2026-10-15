package com.ccx87.annihilator_type_a.item;

import com.ccx87.annihilator_type_a.client.EnergySwordTooltip;
import com.ccx87.annihilator_type_a.component.ModDataComponents;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.loading.FMLEnvironment;
import org.joml.Vector3f;

import java.util.List;


public class EnergySwordItem extends SwordItem {

    private static final int MAX_ENERGY = Integer.MAX_VALUE;
    private static final float BASE_DAMAGE = 0.0f;

    private static final float STAGE1_SLOPE  = 0.0002f;
    private static final float STAGE1_DAMAGE_CAP = 20.0f;
    private static final long ENERGY_INCREMENT_PER_LEVEL = 500;

    private static final int PLASMA_COST = 100;
    private static final float PLASMA_RADIUS = 12.0f;
    private static final float PLASMA_ANGLE = 120.0f;
    private static final float PLASMA_BASE_DAMAGE  = 10.0f;
    private static final float PLASMA_DAMAGE_PER_ENERGY = 0.0002f;
    private static final float PLASMA_MAX_BONUS = 10.0f;
    private static final int COOLDOWN = 20;

    public EnergySwordItem(Tier tier, Properties properties) {
        super(tier, properties);
    }



    //能量存取
    public static int getEnergy(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.ENERGY.get(), 0);
    }

    public static void setEnergy(ItemStack stack, int energy) {
        stack.set(ModDataComponents.ENERGY.get(), Math.max(0, Math.min(energy, MAX_ENERGY)));
    }

    public static float getScaleFactor(LivingEntity entity) {
        if (entity == null) return 1.0f;
        float height = entity.getBbHeight();
        float scale = height / 1.8f;
        return 1.0f + (scale - 1.0f) / 4.0f;
    }
     public static int getExtraDamage(int energy) {

         long stageEnergyLimit = (long) Math.ceil(STAGE1_DAMAGE_CAP / STAGE1_SLOPE);
         if (energy <= stageEnergyLimit) {
             return (int) (energy * STAGE1_SLOPE);
         }

         double remaining  = energy - stageEnergyLimit;
         int level = 1;
         int extra = (int) STAGE1_DAMAGE_CAP;
         while (true) {
             double cost = (1.0 / STAGE1_SLOPE) + ENERGY_INCREMENT_PER_LEVEL * level;
             if (remaining < cost) break;
             remaining -= cost;
             extra++;
             level++;
         }
             return extra;
    }

    public static float getBonusDamage(ItemStack stack) {
        return getExtraDamage(getEnergy(stack));
    }

    public static float getBonusDamage(ItemStack stack, LivingEntity holder) {
        return getBonusDamage(stack) * getScaleFactor(holder);
    }

    public static int getOPlasmaExtraDamage(int energy) {

        long stagePlasmaEnergyLimit = (long) Math.ceil(PLASMA_MAX_BONUS / PLASMA_DAMAGE_PER_ENERGY);
        if (energy <= stagePlasmaEnergyLimit) {
            return (int) (energy * PLASMA_DAMAGE_PER_ENERGY);
        }
        double Plasmaremaining = energy;
        int level = 1;
        int extra = (int)PLASMA_MAX_BONUS;
        while (true) {
            double cost = (1.0 / PLASMA_DAMAGE_PER_ENERGY) + ENERGY_INCREMENT_PER_LEVEL * level;
            if (Plasmaremaining < cost) break;
            Plasmaremaining -= cost;
            extra++;
            level++;
        }
        return extra;
    }

     public static float getPlasmaDamage(ItemStack stack) {return  PLASMA_BASE_DAMAGE + getOPlasmaExtraDamage(getEnergy(stack));}

    public static float getPlasmaDamage(ItemStack stack, LivingEntity holder) {
        return getPlasmaDamage(stack) * getScaleFactor(holder);
    }




    //右键使用
    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(this)) {
            return InteractionResultHolder.fail(stack);
        }

        int energy = getEnergy(stack);
        if (energy < PLASMA_COST) {
            if (!level.isClientSide) {
                player.displayClientMessage(
                        Component.translatable("message.annihilator_type_a.energy_insufficient", PLASMA_COST), true);
            }
            return InteractionResultHolder.fail(stack);
        }
        if (!level.isClientSide) {
            setEnergy(stack, energy - PLASMA_COST);
            player.getCooldowns().addCooldown(this, COOLDOWN);

            float damage = getPlasmaDamage(stack, player);

            ServerLevel serverLevel = (ServerLevel) level;
            Vec3 eyePos = player.position().add(0, 1.0, 0);
            Vec3 forward = player.getLookAngle();

            AABB searchBox = player.getBoundingBox().inflate(PLASMA_RADIUS);
            List<LivingEntity> targets = level.getEntitiesOfClass(
                    LivingEntity.class,
                    searchBox,
                    entity -> entity != player && entity.isAlive()
            );

            for (LivingEntity target : targets) {
                Vec3 toTarget = target.position()
                        .add(0, target.getBbHeight() / 2, 0)
                        .subtract(eyePos);
                double distance = toTarget.length();
                if (distance > PLASMA_RADIUS) continue;


                double angle = Math.toDegrees(Math.acos(
                        forward.dot(toTarget.normalize())
                ));
                if (angle > PLASMA_ANGLE / 2) continue;

                // 造成伤害
                target.hurt(player.damageSources().playerAttack(player), damage);
            }
            //特效
            Vec3 startPos = player.position().add(0, 1.2, 0).add(forward.scale(1.5));
            int steps = 120;
            double halfAngle = Math.toRadians(PLASMA_ANGLE / 2); // 60°

            float r = 102 / 255.0f;
            float g = 204 / 255.0f;
            float b = 255 / 255.0f;

            for (int step = 0; step < steps; step++) {
                double progress = step / (double) steps;
                double radius = Math.sin(progress * Math.PI) * (double) PLASMA_RADIUS;
                double angleWave = Math.sin(progress * 8 * Math.PI) * halfAngle * 0.6;
                double angleOffset = (progress - 0.5) * 2 * halfAngle + angleWave;

                Vec3 right = new Vec3(forward.z, 0, -forward.x).normalize();
                double cosA = Math.cos(angleOffset);
                double sinA = Math.sin(angleOffset);
                Vec3 dir = forward.scale(cosA).add(right.scale(sinA)).normalize();

                Vec3 pos = startPos.add(dir.scale(radius))
                        .add((level.random.nextDouble() - 0.5) * 0.4,
                                (level.random.nextDouble() - 0.5) * 0.4,
                                (level.random.nextDouble() - 0.5) * 0.4);


                Vec3 coreVel = dir.scale(0.2 + 0.3 * progress);
                serverLevel.sendParticles(
                        new DustParticleOptions(new Vector3f(r, g, b), 1.5f),
                        pos.x, pos.y, pos.z,
                        1,
                        coreVel.x, coreVel.y, coreVel.z,
                        0.1
                );


                for (int j = 0; j < 2; j++) {
                    double spread = 0.15;
                    double subAngle = angleOffset + (j - 0.5) * spread;
                    Vec3 subDir = forward.scale(Math.cos(subAngle)).add(right.scale(Math.sin(subAngle))).normalize();
                    Vec3 subPos = startPos.add(subDir.scale(radius * (0.9 + 0.2 * level.random.nextDouble())))
                            .add((level.random.nextDouble() - 0.5) * 0.2,
                                    (level.random.nextDouble() - 0.5) * 0.2,
                                    (level.random.nextDouble() - 0.5) * 0.2);
                    Vec3 subVel = subDir.scale(0.4 + 0.4 * progress);
                    serverLevel.sendParticles(
                            new DustParticleOptions(new Vector3f(r, g, b), 0.8f),
                            subPos.x, subPos.y, subPos.z,
                            1,
                            subVel.x, subVel.y, subVel.z,
                            0.1
                    );
                }
            }
        }

            return InteractionResultHolder.success(stack);
        }



    @Override
    public int getEnchantmentValue() {
        return 22;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        int energy = getEnergy(stack);
        float bonus = getBonusDamage(stack);
        float totalAttack = BASE_DAMAGE + bonus;
        float plasmaDamage = getPlasmaDamage(stack) + totalAttack - BASE_DAMAGE;

        tooltip.add(Component.translatable("tooltip.annihilator_type_a.condition"));
        tooltip.add(Component.translatable("tooltip.annihilator_type_a.energy_sword", energy));
        tooltip.add(Component.translatable("tooltip.annihilator_type_a.attack", totalAttack, BASE_DAMAGE));
        tooltip.add(Component.translatable("tooltip.annihilator_type_a.plasma_damage", plasmaDamage));
        tooltip.add(Component.translatable("tooltip.annihilator_type_a.plasma_cost", PLASMA_COST));
        tooltip.add(Component.translatable("tooltip.annihilator_type_a.charge_hint"));

        if (context.level() != null && context.level().isClientSide) {
            if (FMLEnvironment.dist == Dist.CLIENT) {
                EnergySwordTooltip.addScaleTooltip(tooltip);
            }
        }
    }
}