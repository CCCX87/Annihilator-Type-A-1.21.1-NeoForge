package com.ccx87.annihilator_type_a.common;

import java.lang.reflect.Method;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;


public final class DragonSpecies {
    public static final ResourceLocation ANNIHILATOR =
            ResourceLocation.fromNamespaceAndPath("dragonsurvival", "annihilator_type_a");

    private static Method getData;
    private static Method isDragon;
    private static Method speciesId;
    private static boolean available;
    private static boolean initialised;

    private DragonSpecies() {}

    private static synchronized void init() {
        if (initialised) {
            return;
        }
        initialised = true;
        try {
            Class<?> provider = Class.forName(
                    "by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateProvider");
            Class<?> handler = Class.forName(
                    "by.dragonsurvivalteam.dragonsurvival.common.capability.DragonStateHandler");
            getData = provider.getMethod("getData", Player.class);
            isDragon = handler.getMethod("isDragon");
            speciesId = handler.getMethod("speciesId");
            available = true;
        } catch (Throwable ignored) {
            available = false;
        }
    }

    public static boolean isAnnihilator(Player player) {
        init();
        if (!available) {
            return true;
        }
        try {
            Object data = getData.invoke(null, player);
            if (data == null) {
                return false;
            }
            if (!(Boolean) isDragon.invoke(data)) {
                return false;
            }
            Object id = speciesId.invoke(data);
            return ANNIHILATOR.equals(id);
        } catch (Throwable ignored) {
            return true;
        }
    }
}
