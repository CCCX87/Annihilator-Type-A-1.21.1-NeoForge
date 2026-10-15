package com.ccx87.annihilator_type_a.client.hud;

import com.ccx87.annihilator_type_a.client.AnnihilationClientState;
import com.ccx87.annihilator_type_a.client.ClientConfig;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

public final class SelfHud {
    private static final int WIDTH = 104;
    private static final int ROW = 9;

    private SelfHud() {}

    public static void render(GuiGraphics graphics) {
        if (!ClientConfig.HUD_ENABLED.get()) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null) {
            return;
        }
        if (!AnnihilationClientState.isActive() && ClientConfig.HUD_SELF_ONLY_WHILE_ACTIVE.get()) {
            return;
        }

        final int height = 48;
        int screenHeight = minecraft.getWindow().getGuiScaledHeight();
        int x = ClientConfig.HUD_LEFT_OFFSET_X.get();
        int y = (screenHeight - height) / 2 + ClientConfig.HUD_LEFT_OFFSET_Y.get();

        HudTheme.panel(graphics, x, y, WIDTH, height);
        HudTheme.sweep(graphics, x + 1, y + 1, WIDTH - 2, height - 2,
                (net.minecraft.Util.getMillis() % 2400L) / 2400.0F);

        float health = player.getHealth();
        float maxHealth = Math.max(player.getMaxHealth(), 1.0F);
        float absorption = player.getAbsorptionAmount();
        double attackDamage = player.getAttributeValue(Attributes.ATTACK_DAMAGE);
        double attackSpeed = player.getAttributeValue(Attributes.ATTACK_SPEED);

        int rowY = y + 4;
        row(graphics, minecraft, x, rowY, "hud.annihilator_type_a.health",
                String.format(Locale.ROOT, "%.1f/%.1f", health, maxHealth), HudTheme.TEXT);

        HudTheme.bar(graphics, x + 5, rowY + 9, WIDTH - 10, 3,
                Mth.clamp(health / maxHealth, 0.0F, 1.0F),
                0xFFFF4D4D, 0xFFFFA36B, HudTheme.TRACK);
        if (absorption > 0.0F) {
            int absorptionWidth = Math.round((WIDTH - 10) * Mth.clamp(absorption / maxHealth, 0.0F, 1.0F));
            graphics.fill(x + 5, rowY + 9, x + 5 + absorptionWidth, rowY + 11, 0xFFFFD54A);
        }

        rowY += 14;
        row(graphics, minecraft, x, rowY, "hud.annihilator_type_a.absorption",
                absorption > 0.0F ? String.format(Locale.ROOT, "%.1f", absorption) : "-",
                absorption > 0.0F ? 0xFFFFD54A : HudTheme.TEXT_DIM);

        rowY += ROW;
        row(graphics, minecraft, x, rowY, "hud.annihilator_type_a.attack",
                String.format(Locale.ROOT, "%.1f", attackDamage), HudTheme.TEXT);

        rowY += ROW;
        row(graphics, minecraft, x, rowY, "hud.annihilator_type_a.attack_speed",
                String.format(Locale.ROOT, "%.2f", attackSpeed), HudTheme.TEXT);
    }

    private static void row(GuiGraphics graphics, Minecraft minecraft, int x, int y,
                            String labelKey, String value, int valueColor) {
        graphics.drawString(minecraft.font, Component.translatable(labelKey), x + 5, y,
                HudTheme.ACCENT, false);
        Component text = Component.literal(value);
        graphics.drawString(minecraft.font, text, x + WIDTH - 5 - minecraft.font.width(text), y,
                valueColor, false);
    }
}
