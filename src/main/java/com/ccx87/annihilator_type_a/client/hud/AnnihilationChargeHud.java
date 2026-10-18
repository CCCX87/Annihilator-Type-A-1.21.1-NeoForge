package com.ccx87.annihilator_type_a.client.hud;

import com.ccx87.annihilator_type_a.client.ClientConfig;
import com.ccx87.annihilator_type_a.registry.ATAEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;

public final class AnnihilationChargeHud {
    private static final int CAST_TICKS = 40;
    private static final float REFERENCE_HEIGHT = 720.0F;

    private static final int WHITE = 0xFFFFFFFF;
    private static final int CYAN = 0xFF6FE3FF;
    private static final int RED = 0xFFFF4040;

    private static int chargeTicks;
    private static float shatter;

    private AnnihilationChargeHud() {}

    public static void tick(Minecraft minecraft) {
        Player player = minecraft.player;
        if (player != null && player.hasEffect(ATAEffects.ANNIHILATION_CHARGE)) {
            chargeTicks = Math.min(chargeTicks + 1, CAST_TICKS);
        } else {
            chargeTicks = 0;
        }

        boolean shattered = player != null && player.hasEffect(ATAEffects.IGNORE_INVINCIBILITY);
        shatter = net.minecraft.util.Mth.clamp(shatter + (shattered ? 0.16F : -0.09F), 0.0F, 1.0F);
    }

    public static void render(GuiGraphics graphics, int screenWidth, int screenHeight) {
        if (chargeTicks <= 0 || !ClientConfig.loaded() || !ClientConfig.HUD_ENABLED.get()) {
            return;
        }
        float progress = chargeTicks / (float) CAST_TICKS;
        long time = Minecraft.getInstance().level == null
                ? 0L
                : Minecraft.getInstance().level.getGameTime();

        drawLetterbox(graphics, screenWidth, screenHeight, progress);
        drawScanlines(graphics, screenWidth, screenHeight, progress, time);
        drawRedPulse(graphics, screenWidth, screenHeight, progress, time);
    }

    public static void renderSustainDim(GuiGraphics graphics, int screenWidth, int screenHeight) {
        if (shatter <= 0.01F || !ClientConfig.loaded() || !ClientConfig.HUD_ENABLED.get()) {
            return;
        }
        float scale = screenHeight / REFERENCE_HEIGHT;
        int bands = 10;
        int bandHeight = Math.max(2, Math.round(6.0F * scale));
        for (int band = 0; band < bands; band++) {
            float falloff = 1.0F - band / (float) bands;
            int alpha = Math.round(70.0F * shatter * falloff);
            if (alpha <= 1) {
                continue;
            }
            int colour = alpha << 24;
            int offset = band * bandHeight;
            int thickness = Math.min(bandHeight, screenHeight / 2);
            graphics.fill(0, offset, screenWidth, Math.min(screenHeight, offset + thickness), colour);
            graphics.fill(0, Math.max(0, screenHeight - offset - thickness), screenWidth,
                    screenHeight - offset, colour);
        }
    }

    private static void drawEdgeCircuits(GuiGraphics graphics, int width, int height, float progress,
                                         long time) {
        float scale = height / REFERENCE_HEIGHT;
        int thickness = Math.max(1, Math.round(2.0F * scale));
        int dash = Math.max(8, Math.round(26.0F * scale));
        int gap = Math.max(24, Math.round(90.0F * scale));
        int alpha = Math.round(40.0F + 90.0F * progress);
        int colour = (alpha << 24) | (CYAN & 0x00FFFFFF);
        int travel = (int) (time * (2L + (long) (progress * 4.0F)) % gap);

        for (int edge = 0; edge < 2; edge++) {
            int y = edge == 0 ? 0 : height - thickness;
            for (int x = -gap + travel; x < width; x += gap) {
                int from = Math.max(0, x);
                int to = Math.min(width, x + dash);
                if (to > from) {
                    graphics.fill(from, y, to, y + thickness, colour);
                }
            }
        }
        for (int edge = 0; edge < 2; edge++) {
            int x = edge == 0 ? 0 : width - thickness;
            for (int y = -gap + travel; y < height; y += gap) {
                int from = Math.max(0, y);
                int to = Math.min(height, y + dash);
                if (to > from) {
                    graphics.fill(x, from, x + thickness, to, colour);
                }
            }
        }
    }

    private static void drawCornerBrackets(GuiGraphics graphics, int width, int height, float progress) {
        float scale = height / REFERENCE_HEIGHT;
        int inset = Math.round((10.0F + 26.0F * progress) * scale);
        int arm = Math.max(6, Math.round(34.0F * scale));
        int thickness = Math.max(1, Math.round(2.0F * scale));
        int alpha = Math.round(110.0F + 120.0F * progress);
        int colour = (alpha << 24) | (CYAN & 0x00FFFFFF);
        for (int corner = 0; corner < 4; corner++) {
            int signX = (corner & 1) == 0 ? 1 : -1;
            int signY = (corner & 2) == 0 ? 1 : -1;
            int x = signX > 0 ? inset : width - inset;
            int y = signY > 0 ? inset : height - inset;
            graphics.fill(Math.min(x, x + signX * arm), y, Math.max(x, x + signX * arm), y + thickness,
                    colour);
            graphics.fill(x, Math.min(y, y + signY * arm), x + thickness, Math.max(y, y + signY * arm),
                    colour);
        }
    }

    private static void drawLetterbox(GuiGraphics graphics, int width, int height, float progress) {
        int bar = Math.round(height * 0.16F * progress);
        if (bar <= 0) {
            return;
        }
        int alpha = Math.round(200.0F * Math.min(1.0F, progress * 2.0F));
        int colour = (alpha << 24);
        graphics.fill(0, 0, width, bar, colour);
        graphics.fill(0, height - bar, width, height, colour);
    }

    private static void drawScanlines(GuiGraphics graphics, int width, int height, float progress,
                                      long time) {
        float scale = height / REFERENCE_HEIGHT;
        int spacing = Math.max(6, Math.round(11.0F * scale));
        int offset = (int) (time % spacing);
        int alpha = Math.round(20.0F + 40.0F * progress);
        int colour = (alpha << 24) | 0x00C8F0FF;
        for (int y = offset; y < height; y += spacing) {
            graphics.fill(0, y, width, y + 1, colour);
        }
        int tears = 2;
        for (int index = 0; index < tears; index++) {
            long seed = time / 2L + index * 37L;
            int y = (int) ((seed * 2654435761L >>> 9) % Math.max(1, height));
            int sliceHeight = Math.max(2, Math.round(3.0F * scale));
            int left = (int) ((seed * 40503L >>> 7) % Math.max(1, width / 3));
            int right = left + width / 2 + (int) (seed % Math.max(1, width / 3));
            int tearAlpha = Math.round(30.0F + 70.0F * progress);
            graphics.fill(left, y, Math.min(width, right), y + sliceHeight,
                    (tearAlpha << 24) | 0x006FE3FF);
        }
    }

    private static void drawRedPulse(GuiGraphics graphics, int width, int height, float progress,
                                     long time) {
        float scale = height / REFERENCE_HEIGHT;
        int bands = 6;
        int bandWidth = Math.max(2, Math.round(6.0F * scale));
        float pulse = 0.75F + 0.25F * (float) Math.sin(time * (0.25D + progress * 0.55D));
        for (int band = 0; band < bands; band++) {
            float falloff = 1.0F - band / (float) bands;
            int alpha = Math.round(90.0F * progress * pulse * falloff * falloff);
            if (alpha <= 2) {
                continue;
            }
            int colour = (alpha << 24) | (RED & 0x00FFFFFF);
            int offset = band * bandWidth;
            int thickness = Math.min(bandWidth, width / 2);
            graphics.fill(offset, 0, Math.min(width, offset + thickness), height, colour);
            graphics.fill(Math.max(0, width - offset - thickness), 0, width - offset, height, colour);
            graphics.fill(0, offset, width, Math.min(height, offset + thickness), colour);
            graphics.fill(0, Math.max(0, height - offset - thickness), width, height - offset, colour);
        }
    }

    private static void drawReticle(GuiGraphics graphics, int width, int height, float progress,
                                    long time) {
        float scale = height / REFERENCE_HEIGHT;
        int centreX = width / 2;
        int centreY = height / 2;
        int bracketReach = Math.round(64.0F * scale * (1.0F - 0.55F * progress));
        int armLength = Math.max(3, Math.round(10.0F * scale));
        int thickness = Math.max(1, Math.round(2.0F * scale));
        int bracketAlpha = Math.round(120.0F + 135.0F * progress);
        int bracketColour = (bracketAlpha << 24) | (CYAN & 0x00FFFFFF);

        for (int corner = 0; corner < 4; corner++) {
            int signX = (corner & 1) == 0 ? -1 : 1;
            int signY = (corner & 2) == 0 ? -1 : 1;
            int x = centreX + signX * bracketReach;
            int y = centreY + signY * bracketReach;
            int armX = Math.min(armLength, width / 2);
            int armY = Math.min(armLength, height / 2);
            graphics.fill(Math.min(x, x + signX * armX), y, Math.max(x, x + signX * armX), y + thickness,
                    bracketColour);
            graphics.fill(x, Math.min(y, y + signY * armY), x + thickness, Math.max(y, y + signY * armY),
                    bracketColour);
        }

        int dots = 28;
        float radius = (74.0F - 46.0F * progress) * scale;
        double spin = time * (0.04D + progress * 0.10D);
        int dotAlpha = Math.round(100.0F + 130.0F * progress);
        int dotColour = (dotAlpha << 24) | (WHITE & 0x00FFFFFF);
        int dotSize = Math.max(1, Math.round(2.0F * scale));
        for (int index = 0; index < dots; index++) {
            double angle = Math.PI * 2.0D * index / dots + spin;
            int x = centreX + (int) Math.round(Math.cos(angle) * radius);
            int y = centreY + (int) Math.round(Math.sin(angle) * radius);
            graphics.fill(x, y, x + dotSize, y + dotSize, dotColour);
        }
    }
}
