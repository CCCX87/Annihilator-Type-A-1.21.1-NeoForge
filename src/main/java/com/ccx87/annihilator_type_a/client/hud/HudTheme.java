package com.ccx87.annihilator_type_a.client.hud;

import com.ccx87.annihilator_type_a.client.ClientConfig;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;


public final class HudTheme {
    public static final int ACCENT = 0xFF6FE3FF;
    public static final int ACCENT_DIM = 0x7048A9C4;
    public static final int WARN = 0xFFFF5A4D;
    public static final int TEXT = 0xFFEAF6FF;
    public static final int TEXT_DIM = 0xFF9FB6C4;
    public static final int PANEL_BG = 0xC8060C14;
    public static final int PANEL_SHEEN = 0x22FFFFFF;
    public static final int TRACK = 0xFF0E1820;
    public static final int HAIRLINE = 0x50203040;

    private HudTheme() {}

    public static boolean decorationsEnabled() {
        return ClientConfig.HUD_DECORATIONS.get();
    }

    public static void panel(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.fill(x, y, x + width, y + height, PANEL_BG);
        graphics.fillGradient(x + 1, y + 1, x + width - 1, y + height / 2, PANEL_SHEEN, 0x00FFFFFF);
        outline(graphics, x, y, width, height, ACCENT_DIM);
        if (decorationsEnabled()) {
            cornerBrackets(graphics, x, y, width, height);
        }
    }

    public static void outline(GuiGraphics graphics, int x, int y, int width, int height, int color) {
        if (width <= 0 || height <= 0) {
            return;
        }
        graphics.fill(x, y, x + width, y + 1, color);
        graphics.fill(x, y + height - 1, x + width, y + height, color);
        graphics.fill(x, y + 1, x + 1, y + height - 1, color);
        graphics.fill(x + width - 1, y + 1, x + width, y + height - 1, color);
    }

    private static void cornerBrackets(GuiGraphics graphics, int x, int y, int width, int height) {
        final int arm = 5;
        for (int corner = 0; corner < 4; corner++) {
            boolean right = (corner & 1) != 0;
            boolean bottom = (corner & 2) != 0;
            int cx = right ? x + width - 1 : x;
            int cy = bottom ? y + height - 1 : y;
            int dx = right ? -1 : 1;
            int dy = bottom ? -1 : 1;
            for (int step = 0; step < arm; step++) {
                graphics.fill(cx + dx * step, cy, cx + dx * step + 1, cy + 1, ACCENT);
                graphics.fill(cx, cy + dy * step, cx + 1, cy + dy * step + 1, ACCENT);
            }
        }
    }

    public static void sweep(GuiGraphics graphics, int x, int y, int width, int height, float phase) {
        if (!decorationsEnabled() || height <= 2 || width <= 2) {
            return;
        }
        float cycle = phase - (float) Math.floor(phase);
        int offset = (int) (cycle * (height - 2));
        graphics.fill(x + 1, y + 1 + offset, x + width - 1, y + 2 + offset, 0x1EFFFFFF);
    }

    public static void bar(GuiGraphics graphics, int x, int y, int width, int height,
                           float progress, int colorStart, int colorEnd, int trackColor) {
        if (width <= 0 || height <= 0) {
            return;
        }
        graphics.fill(x, y, x + width, y + height, trackColor);
        int filled = Math.round(width * Mth.clamp(progress, 0.0F, 1.0F));
        if (filled > 0) {
            graphics.fillGradient(x, y, x + filled, y + height, colorStart, colorEnd);
            graphics.fill(x + filled - 1, y, x + filled, y + height, 0xFFFFFFFF);
        }
        outline(graphics, x - 1, y - 1, width + 2, height + 2, HAIRLINE);
    }

    public static int lerpColor(int from, int to, float delta) {
        float t = Mth.clamp(delta, 0.0F, 1.0F);
        int a = (int) Mth.lerp(t, (from >>> 24) & 0xFF, (to >>> 24) & 0xFF);
        int r = (int) Mth.lerp(t, (from >> 16) & 0xFF, (to >> 16) & 0xFF);
        int g = (int) Mth.lerp(t, (from >> 8) & 0xFF, (to >> 8) & 0xFF);
        int b = (int) Mth.lerp(t, from & 0xFF, to & 0xFF);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    public static String seconds(int ticks) {
        return String.format(java.util.Locale.ROOT, "%.1fs", Math.max(ticks, 0) / 20.0F);
    }
}
