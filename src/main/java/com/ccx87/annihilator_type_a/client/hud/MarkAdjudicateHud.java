package com.ccx87.annihilator_type_a.client.hud;

import com.ccx87.annihilator_type_a.client.ClientConfig;
import com.ccx87.annihilator_type_a.client.MarkAdjudicateBeams;
import com.ccx87.annihilator_type_a.registry.ATAEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public final class MarkAdjudicateHud {
    private static final int CAST_TICKS = 60;

    private static final String[] LINES = {
            "ENERGY ACCUMULATING",
            "DATA VERIFICATION",
            "CALCULATING TARGET VECTOR",
            "ACCESS GRANTED",
            "ALL SYSTEMS NOMINAL",
            "FULL RELEASE"
    };

    private static final float[][] SLOTS = {
            {-0.34F, -0.40F},
            {0.34F, -0.28F},
            {-0.60F, -0.02F},
            {0.60F, 0.06F},
            {-0.32F, 0.32F},
            {0.32F, 0.44F}
    };

    private static final int CYAN = 0xFF6FE3FF;
    private static final int SUBTITLE = 0xFF2E6EFF;
    private static final int RED = 0xFFFF4040;
    private static final int WHITE = 0xFFFFFFFF;
    private static final float REFERENCE_HEIGHT = 720.0F;
    private static final float TEXT_SCALE = 1.0F;
    private static final int FIRE_HOLD_TICKS = 30;

    private static int chargeTicks;
    private static int holdTicks;
    private static boolean released;
    private static int armedTicks;
    private static int shownLines;
    private static int fireBeeps;
    private static int fireBeepTimer;

    private MarkAdjudicateHud() {}

    public static void tick(Minecraft minecraft) {
        Player player = minecraft.player;
        boolean charging = player != null && player.hasEffect(ATAEffects.MARK_ADJUDICATE_CHARGE);
        if (charging) {
            chargeTicks = Math.min(chargeTicks + 1, CAST_TICKS);
            armedTicks++;
            released = false;
            int shown = Math.min(LINES.length, Math.max(0, (chargeTicks - 4) / 8));
            if (shown > shownLines) {
                shownLines = shown;
                blip(player, 1.15F, 0.95F + shown * 0.10F);
            }
        } else {
            if (armedTicks >= 50) {
                released = true;
                holdTicks = FIRE_HOLD_TICKS;
                fireBeeps = 3;
                fireBeepTimer = 0;
            }
            armedTicks = 0;
            if (holdTicks > 0) {
                holdTicks--;
            } else {
                released = false;
                chargeTicks = 0;
                shownLines = 0;
            }
        }
        if (fireBeeps > 0) {
            if (fireBeepTimer <= 0) {
                fireBeeps--;
                blip(player, 1.2F, 1.55F + (3 - fireBeeps) * 0.10F);
                fireBeepTimer = 3;
            } else {
                fireBeepTimer--;
            }
        }
    }

    private static void blip(Player player, float volume, float pitch) {
        if (player == null) {
            return;
        }
        player.playSound(SoundEvents.NOTE_BLOCK_BIT.value(), volume, pitch);
        player.playSound(SoundEvents.NOTE_BLOCK_BASS.value(), volume * 0.95F, pitch * 0.5F);
    }

    public static void render(GuiGraphics graphics, int screenWidth, int screenHeight) {
        if (!ClientConfig.loaded() || !ClientConfig.HUD_ENABLED.get()) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        if (chargeTicks > 0 || holdTicks > 0) {
            long time = minecraft.level == null ? 0L : minecraft.level.getGameTime();
            float progress = chargeTicks / (float) CAST_TICKS;
            drawSequence(graphics, minecraft, screenWidth, screenHeight, progress);
            drawReticle(graphics, screenWidth, screenHeight, progress, time);
            drawFire(graphics, minecraft, screenWidth, screenHeight, progress);
        }
        int laserAge = MarkAdjudicateBeams.laserAge();
        if (laserAge >= 0) {
            drawLaserEdges(graphics, minecraft, screenWidth, screenHeight, laserAge);
            drawImpactBanner(graphics, minecraft, screenWidth, screenHeight, laserAge);
        }
    }

    private static void drawLaserEdges(GuiGraphics graphics, Minecraft minecraft, int width, int height,
                                       int age) {
        float scale = height / REFERENCE_HEIGHT;
        float fade = 1.0F - Mth.clamp(age / 34.0F, 0.0F, 1.0F);
        if (fade <= 0.0F) {
            return;
        }
        int depth = Math.round(64.0F * scale);
        if (age < 5) {
            int alpha = 230 - age * 42;
            int white = (alpha << 24) | 0xFFFFFF;
            graphics.fillGradient(0, 0, width, depth, white, 0);
            graphics.fillGradient(0, height - depth, width, height, 0, white);
        }
        int band = Math.round(85.0F * fade);
        int glow = (band << 24) | 0xFF5A38;
        int clear = 0;
        graphics.fillGradient(0, 0, width, depth, glow, clear);
        graphics.fillGradient(0, height - depth, width, height, clear, glow);
        int strips = 6;
        int stripWidth = Math.max(2, Math.round(7.0F * scale));
        for (int index = 0; index < strips; index++) {
            int alpha = Math.round(band * (1.0F - index / (float) strips));
            int colour = (alpha << 24) | 0xFF5A38;
            graphics.fill(index * stripWidth, 0, (index + 1) * stripWidth, height, colour);
            graphics.fill(width - (index + 1) * stripWidth, 0, width - index * stripWidth, height, colour);
        }
    }

    private static void drawSequence(GuiGraphics graphics, Minecraft minecraft, int width, int height,
                                     float progress) {
        float scale = height / REFERENCE_HEIGHT;
        int centreX = width / 2;
        int centreY = height / 2;
        int halfWidth = width / 2;
        int halfHeight = height / 2;
        int shown = Math.min(LINES.length, Math.max(0, (chargeTicks - 4) / 8));
        for (int index = 0; index < shown; index++) {
            String text = LINES[index];
            int textWidth = minecraft.font.width(text);
            int slotX = centreX + Math.round(SLOTS[index][0] * halfWidth);
            int slotY = centreY + Math.round(SLOTS[index][1] * halfHeight);
            int boxWidth = textWidth + Math.round(8.0F * scale);
            int boxHeight = Math.round(4.0F * scale) + 9;
            int left = slotX - boxWidth / 2;
            int top = slotY - boxHeight / 2;
            boolean newest = index == shown - 1;
            int alpha = newest ? 0xFF : 0x66;
            int edge = (alpha << 24) | (SUBTITLE & 0x00FFFFFF);
            int band = (Math.min(0xC0, (alpha >> 1) + 0x40) << 24);
            int textWidthScaled = Math.round(textWidth * TEXT_SCALE);
            int textHeightScaled = Math.round(9.0F * TEXT_SCALE);
            int boxW = textWidthScaled + Math.round(10.0F * scale);
            int boxH = textHeightScaled + Math.round(6.0F * scale);
            int l = slotX - boxW / 2;
            int tp = slotY - boxH / 2;
            graphics.fill(l, tp, l + boxW, tp + boxH, band);
            graphics.fill(l, tp, l + boxW, tp + 1, edge);
            graphics.fill(l, tp + boxH - 1, l + boxW, tp + boxH, edge);
            graphics.pose().pushPose();
            graphics.pose().scale(TEXT_SCALE, TEXT_SCALE, 1.0F);
            graphics.drawString(minecraft.font, text, Math.round((l + 5.0F * scale) / TEXT_SCALE),
                    Math.round((tp + 3.0F * scale) / TEXT_SCALE), edge, true);
            graphics.pose().popPose();
        }
    }

    private static void drawReticle(GuiGraphics graphics, int width, int height, float progress,
                                    long time) {
        float scale = height / REFERENCE_HEIGHT;
        int centreX = width / 2;
        int centreY = height / 2;
        int reach = Math.round(64.0F * scale * (1.0F - 0.55F * progress));
        int arm = Math.max(3, Math.round(10.0F * scale));
        int thickness = Math.max(1, Math.round(2.0F * scale));
        int alpha = Math.round(120.0F + 135.0F * progress);
        int colour = (alpha << 24) | (CYAN & 0x00FFFFFF);

        for (int corner = 0; corner < 4; corner++) {
            int signX = (corner & 1) == 0 ? -1 : 1;
            int signY = (corner & 2) == 0 ? -1 : 1;
            int x = centreX + signX * reach;
            int y = centreY + signY * reach;
            int armX = Math.min(arm, width / 2);
            int armY = Math.min(arm, height / 2);
            graphics.fill(Math.min(x, x + signX * armX), y, Math.max(x, x + signX * armX), y + thickness,
                    colour);
            graphics.fill(x, Math.min(y, y + signY * armY), x + thickness,
                    Math.max(y, y + signY * armY), colour);
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

    private static void drawFire(GuiGraphics graphics, Minecraft minecraft, int width, int height,
                                 float progress) {
        if (!released) {
            return;
        }
        float scale = height / REFERENCE_HEIGHT;
        int centreX = width / 2;
        int centreY = height / 2;
        String text = "FIRE";
        int textWidth = minecraft.font.width(text);
        int boxWidth = textWidth + Math.round(16.0F * scale);
        int boxHeight = Math.round(6.0F * scale) + 11;
        int left = centreX - boxWidth / 2;
        int top = centreY - boxHeight / 2;
        int alpha = chargeTicks >= 56 ? 0xFF : 0xAA;
        int edge = (alpha << 24) | (RED & 0x00FFFFFF);
        int band = Math.max(2, Math.round(2.0F * scale));
        graphics.fill(left, top, left + boxWidth, top + band, edge);
        graphics.fill(left, top + boxHeight - band, left + boxWidth, top + boxHeight, edge);
        graphics.fill(left, top, left + band, top + boxHeight, edge);
        graphics.fill(left + boxWidth - band, top, left + boxWidth, top + boxHeight, edge);
        graphics.pose().pushPose();
        graphics.pose().scale(TEXT_SCALE, TEXT_SCALE, 1.0F);
        graphics.drawString(minecraft.font, text, Math.round((left + 8.0F * scale) / TEXT_SCALE),
                Math.round((top + band + 1) / TEXT_SCALE), edge, true);
        graphics.pose().popPose();
        int glow = (alpha >> 1) << 24;
        int reach = Math.round(28.0F * scale);
        graphics.fill(centreX - reach, centreY + reach, centreX + reach, centreY + reach + band,
                glow | (RED & 0x00FFFFFF));
    }

    private static void drawImpactBanner(GuiGraphics graphics, Minecraft minecraft, int width, int height,
                                         int age) {
        float scale = height / REFERENCE_HEIGHT;
        float fade = 1.0F - Mth.clamp(age / 30.0F, 0.0F, 1.0F);
        int alpha = Math.round(255.0F * fade);
        if (alpha <= 2) {
            return;
        }
        if (age < 12) {
            int edgeAlpha = Math.round(80.0F * (1.0F - age / 12.0F));
            int glow = (edgeAlpha << 24) | (RED & 0x00FFFFFF);
            int clear = 0;
            int band = Math.round(52.0F * scale);
            graphics.fillGradient(0, 0, width, band, glow, clear);
            graphics.fillGradient(0, height - band, width, height, clear, glow);
        }

        String text = "JUDGMENT";
        int textWidth = minecraft.font.width(text);
        int centreX = width / 2;
        int centreY = height / 2 + Math.round(58.0F * scale);
        int boxWidth = textWidth + Math.round(30.0F * scale);
        int boxHeight = Math.round(6.0F * scale) + 11;
        int left = centreX - boxWidth / 2;
        int top = centreY - boxHeight / 2;
        int edge = (alpha << 24) | (RED & 0x00FFFFFF);
        int band = (Math.min(0xB4, alpha / 2) << 24);
        graphics.fill(left, top, left + boxWidth, top + boxHeight, band);
        graphics.fill(left, top, left + boxWidth, top + 1, edge);
        graphics.fill(left, top + boxHeight - 1, left + boxWidth, top + boxHeight, edge);
        graphics.drawString(minecraft.font, text, left + Math.round(15.0F * scale),
                top + Math.round(3.0F * scale) + 1, edge, true);

        int reach = boxWidth / 2 + Math.round(14.0F * scale) + Math.round(age * 4.0F * scale);
        int arm = Math.max(4, Math.round(12.0F * scale));
        int thickness = Math.max(1, Math.round(2.0F * scale));
        for (int corner = 0; corner < 4; corner++) {
            int signX = (corner & 1) == 0 ? -1 : 1;
            int signY = (corner & 2) == 0 ? -1 : 1;
            int x = centreX + signX * reach;
            int y = centreY + signY * Math.round(16.0F * scale);
            graphics.fill(Math.min(x, x + signX * arm), y, Math.max(x, x + signX * arm), y + thickness,
                    edge);
            graphics.fill(x, Math.min(y, y + signY * arm), x + thickness,
                    Math.max(y, y + signY * arm), edge);
        }
    }
}
