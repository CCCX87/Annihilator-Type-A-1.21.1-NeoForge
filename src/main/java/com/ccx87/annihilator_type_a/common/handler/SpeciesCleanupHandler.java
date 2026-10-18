package com.ccx87.annihilator_type_a.common.handler;

import com.ccx87.annihilator_type_a.common.DragonSpecies;
import com.ccx87.annihilator_type_a.registry.ATAEffects;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import static com.ccx87.annihilator_type_a.AnnihilatorTypeA.MOD_ID;


@EventBusSubscriber(modid = MOD_ID)
public final class SpeciesCleanupHandler {
    private static final Set<UUID> WAS_ANNIHILATOR = new HashSet<>();

    private SpeciesCleanupHandler() {}

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        boolean carries = player.hasEffect(ATAEffects.ORE_FORTUNE)
                || player.hasEffect(ATAEffects.NO_FALL_DAMAGE)
                || player.hasEffect(ATAEffects.PARAMETER_OVERLOAD)
                || player.hasEffect(ATAEffects.RANDOM_FAULT);
        boolean was = WAS_ANNIHILATOR.contains(player.getUUID());
        if (!carries && !was) {
            return;
        }
        if (player.tickCount % 5 != 0) {
            return;
        }
        boolean now = DragonSpecies.isAnnihilator(player);
        if (!now) {
            player.removeEffect(ATAEffects.ORE_FORTUNE);
            player.removeEffect(ATAEffects.NO_FALL_DAMAGE);
            player.removeEffect(ATAEffects.PARAMETER_OVERLOAD);
            player.removeEffect(ATAEffects.RANDOM_FAULT);
            if (was) {
                player.removeEffect(MobEffects.NIGHT_VISION);
            }
        }
        if (now) {
            WAS_ANNIHILATOR.add(player.getUUID());
        } else {
            WAS_ANNIHILATOR.remove(player.getUUID());
        }
    }

    @SubscribeEvent
    public static void onLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        WAS_ANNIHILATOR.remove(event.getEntity().getUUID());
    }
}
