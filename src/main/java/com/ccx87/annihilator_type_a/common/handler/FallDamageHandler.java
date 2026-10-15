package com.ccx87.annihilator_type_a.common.handler;

import com.ccx87.annihilator_type_a.registry.ATAEffects;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingFallEvent;

import static com.ccx87.annihilator_type_a.AnnihilatorTypeA.MOD_ID;

@EventBusSubscriber(modid = MOD_ID)
public class FallDamageHandler {

    @SubscribeEvent
    public static void onLivingFall(LivingFallEvent event) {
        var entity = event.getEntity();
        if (entity.hasEffect(ATAEffects.NO_FALL_DAMAGE)) {
            event.setCanceled(true);
        }
    }
}