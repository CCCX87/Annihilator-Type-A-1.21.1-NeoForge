package com.ccx87.annihilator_type_a.client.hud;

import com.ccx87.annihilator_type_a.client.AnnihilationClientState;
import com.ccx87.annihilator_type_a.client.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;

public final class HudFrame {
    private static final int CONTOUR_THICKNESS = 2;
    private static final int ECHO_THICKNESS = 1;
    private static final int TICK_THICKNESS = 1;
    private static final int MARKER_THICKNESS = 1;

    private static final int ECHO_OFFSET = 10;
    private static final int TICK_HALF = 5;
    private static final int MARKER_SIZE = 6;
    private static final float[] TICK_POSITIONS = {0.34F, 0.66F};

    private static final int MARKER_RGB = 0xFF4A2B;
    private static final int FALLBACK_ACCENT_RGB = 0x4FD8FF;
    private static final float ECHO_ALPHA_FACTOR = 0.40F;
    private static final float TICK_ALPHA_FACTOR = 0.80F;
    private static final float MARKER_ALPHA_FACTOR = 1.15F;

    private static String cachedColorSource = "";
    private static int cachedColorRgb = FALLBACK_ACCENT_RGB;

    private HudFrame() {}

    public static void render(GuiGraphics graphics) {
        if (!ClientConfig.HUD_ENABLED.get() || !ClientConfig.HUD_FRAME.get()
                || !AnnihilationClientState.isActive()) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        int width = minecraft.getWindow().getGuiScaledWidth();
        int height = minecraft.getWindow().getGuiScaledHeight();
        if (width < 64 || height < 64) {
            return;
        }

        int insetX = (int) (width * Mth.clamp(ClientConfig.HUD_FRAME_INSET_X.get().floatValue(),
                0.0F, 0.45F));
        int insetY = (int) (height * Mth.clamp(ClientConfig.HUD_FRAME_INSET_Y.get().floatValue(),
                0.0F, 0.45F));
        int x0 = insetX;
        int x1 = width - insetX - 1;
        int y0 = insetY;
        int y1 = height - insetY - 1;
        int chamfer = (int) (height * Mth.clamp(ClientConfig.HUD_FRAME_CHAMFER.get().floatValue(),
                0.0F, 0.60F));
        chamfer = Math.min(chamfer, Math.min((x1 - x0) / 2 - 1, (y1 - y0) / 2 - 1));
        if (chamfer < 1) {
            return;
        }

        int[][] ring = {
                {x0 + chamfer, y0}, {x1 - chamfer, y0},
                {x1, y0 + chamfer}, {x1, y1 - chamfer},
                {x1 - chamfer, y1}, {x0 + chamfer, y1},
                {x0, y1 - chamfer}, {x0, y0 + chamfer},
        };

        int accent = accentRgb();
        int alpha = (int) (Mth.clamp(ClientConfig.HUD_FRAME_ALPHA.get().floatValue(), 0.0F, 1.0F)
                * 255.0F);
        int contourColor = argb(alpha, accent);
        int echoColor = argb((int) (alpha * ECHO_ALPHA_FACTOR), accent);
        int tickColor = argb((int) (alpha * TICK_ALPHA_FACTOR), accent);
        int markerColor = argb((int) Math.min(255.0F, alpha * MARKER_ALPHA_FACTOR), MARKER_RGB);

        int centreX = (x0 + x1) / 2;
        int centreY = (y0 + y1) / 2;
        int[][] echo = new int[ring.length][];
        for (int index = 0; index < ring.length; index++) {
            echo[index] = pushedOutward(ring[index], centreX, centreY, ECHO_OFFSET);
        }

        stroke(graphics, echo, ECHO_THICKNESS, echoColor);
        stroke(graphics, ring, CONTOUR_THICKNESS, contourColor);

        for (int index = 0; index < ring.length; index++) {
            int[] a = ring[index];
            int[] b = ring[(index + 1) % ring.length];
            if (a[0] != b[0] && a[1] != b[1]) {
                continue;
            }
            double nx = -(b[1] - a[1]);
            double ny = b[0] - a[0];
            double length = Math.hypot(nx, ny);
            if (length < 1.0E-4D) {
                continue;
            }
            nx /= length;
            ny /= length;
            for (float position : TICK_POSITIONS) {
                int px = Math.round(a[0] + (b[0] - a[0]) * position);
                int py = Math.round(a[1] + (b[1] - a[1]) * position);
                drawLine(graphics,
                        px - (int) Math.round(nx * TICK_HALF), py - (int) Math.round(ny * TICK_HALF),
                        px + (int) Math.round(nx * TICK_HALF), py + (int) Math.round(ny * TICK_HALF),
                        TICK_THICKNESS, tickColor);
            }
        }

        drawMarker(graphics, ring, 0, 0.42F, markerColor);
        drawMarker(graphics, ring, 4, 0.58F, markerColor);
        drawMarker(graphics, ring, 2, 0.50F, markerColor);
    }

    private static void stroke(GuiGraphics graphics, int[][] ring, int thickness, int color) {
        for (int index = 0; index < ring.length; index++) {
            int[] a = ring[index];
            int[] b = ring[(index + 1) % ring.length];
            drawLine(graphics, a[0], a[1], b[0], b[1], thickness, color);
        }
    }

    private static void drawMarker(GuiGraphics graphics, int[][] ring, int edge, float position,
                                   int color) {
        int[] a = ring[edge];
        int[] b = ring[(edge + 1) % ring.length];
        int cx = Math.round(a[0] + (b[0] - a[0]) * position);
        int cy = Math.round(a[1] + (b[1] - a[1]) * position);
        drawLine(graphics, cx, cy - MARKER_SIZE, cx - MARKER_SIZE, cy + MARKER_SIZE,
                MARKER_THICKNESS, color);
        drawLine(graphics, cx - MARKER_SIZE, cy + MARKER_SIZE, cx + MARKER_SIZE, cy + MARKER_SIZE,
                MARKER_THICKNESS, color);
        drawLine(graphics, cx + MARKER_SIZE, cy + MARKER_SIZE, cx, cy - MARKER_SIZE,
                MARKER_THICKNESS, color);
    }

    private static void drawLine(GuiGraphics graphics, int x0, int y0, int x1, int y1,
                                 int thickness, int color) {
        int half = thickness / 2;
        if (y0 == y1) {
            graphics.fill(Math.min(x0, x1), y0 - half, Math.max(x0, x1) + 1, y0 - half + thickness,
                    color);
            return;
        }
        if (x0 == x1) {
            graphics.fill(x0 - half, Math.min(y0, y1), x0 - half + thickness, Math.max(y0, y1) + 1,
                    color);
            return;
        }
        int top = Math.min(y0, y1);
        int bottom = Math.max(y0, y1);
        for (int y = top; y <= bottom; y++) {
            float t = (float) (y - y0) / (float) (y1 - y0);
            int x = Math.round(x0 + (x1 - x0) * t);
            graphics.fill(x - half, y, x - half + thickness, y + 1, color);
        }
    }

    private static int[] pushedOutward(int[] point, int centreX, int centreY, int distance) {
        double dx = point[0] - centreX;
        double dy = point[1] - centreY;
        double length = Math.hypot(dx, dy);
        if (length < 1.0E-4D) {
            return new int[] {point[0], point[1]};
        }
        return new int[] {
                (int) Math.round(point[0] + dx / length * distance),
                (int) Math.round(point[1] + dy / length * distance),
        };
    }

    private static int argb(int alpha, int rgb) {
        return (Mth.clamp(alpha, 0, 255) << 24) | (rgb & 0xFFFFFF);
    }

    private static int accentRgb() {
        String value = ClientConfig.HUD_FRAME_COLOR.get();
        if (!value.equals(cachedColorSource)) {
            cachedColorSource = value;
            cachedColorRgb = parseHex(value);
        }
        return cachedColorRgb;
    }

    private static int parseHex(String value) {
        try {
            String cleaned = value.startsWith("#") ? value.substring(1) : value;
            return Integer.parseInt(cleaned.trim(), 16) & 0xFFFFFF;
        } catch (RuntimeException exception) {
            return FALLBACK_ACCENT_RGB;
        }
    }
}
