package com.ccx87.annihilator_type_a.client;

import com.ccx87.annihilator_type_a.item.EnergySwordItem;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

import java.util.List;

public class EnergySwordTooltip {

    public static void addScaleTooltip(List<Component> tooltip) {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            float scale = EnergySwordItem.getScaleFactor(player);
            tooltip.add(Component.translatable(
                    "tooltip.annihilator_type_a.scale_factor",
                    String.format("%.2f", scale)));
        }
    }
}