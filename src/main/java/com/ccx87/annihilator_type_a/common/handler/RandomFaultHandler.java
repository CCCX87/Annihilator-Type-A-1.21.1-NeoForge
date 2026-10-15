package com.ccx87.annihilator_type_a.common.handler;

import com.ccx87.annihilator_type_a.registry.ATAEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.ccx87.annihilator_type_a.AnnihilatorTypeA.MOD_ID;

@EventBusSubscriber(modid = MOD_ID)
public class RandomFaultHandler {

    private static final Random RANDOM = new Random();
    private static final int CHECK_INTERVAL = 1200;
    private static final int INTERVAL_PER_LEVEL = 200;
    private static final float BASE_CHANCE = 0.30f;

    private static final Map<Player, Integer> tickCounter = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player == null || player.level().isClientSide) {
            return;
        }


        MobEffectInstance effect = player.getEffect(ATAEffects.RANDOM_FAULT);
        if (effect == null) {
            tickCounter.remove(player);
            return;
        }


        int amplifier = effect.getAmplifier();

        int checkInterval = CHECK_INTERVAL + amplifier * INTERVAL_PER_LEVEL;

        float finalChance = Math.max(0, BASE_CHANCE - amplifier * 0.03f);

        if (finalChance <= 0) {
            tickCounter.remove(player);
            return;
        }

        // 计时
        int count = tickCounter.getOrDefault(player, 0) + 1;
        tickCounter.put(player, count);

        if (count >= checkInterval) {
            tickCounter.put(player, 0);

            if (RANDOM.nextFloat() < finalChance) {
                triggerRandomFault(player);
            }
        }
    }

    private static void triggerRandomFault(Player player) {
        int faultIndex = RANDOM.nextInt(5); // 0-4
        switch (faultIndex) {
            case 0 -> applyJointStuck(player);
            case 1 -> applyBlurredVision(player);
            case 2 -> applySystemRestart(player);
            case 3 -> applyShieldOverload(player);
            case 4 -> applyCoreOverheat(player);
        }
    }

    // 故障1：关节卡顿
    private static void applyJointStuck(Player player) {
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 400, 1));
        player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 400, 1));
    }

    // 故障2：视野模糊
    private static void applyBlurredVision(Player player) {
        player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 400, 0));
    }

    // 故障3：系统重启
    private static void applySystemRestart(Player player) {
        player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 400, 0));


    }

    // 故障4：护盾过载
    private static void applyShieldOverload(Player player) {
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 400, 4));
        player.getFoodData().addExhaustion(5.0f);
        player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 400, 9));
    }

    // 故障5：核心过热
    private static void applyCoreOverheat(Player player) {
        player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 400, 0));
        player.addEffect(new MobEffectInstance(ATAEffects.ARMOR_BREAK, 400, 0));

    }
}