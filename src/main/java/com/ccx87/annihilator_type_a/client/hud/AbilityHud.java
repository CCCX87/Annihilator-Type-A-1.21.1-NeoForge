package com.ccx87.annihilator_type_a.client.hud;

import com.ccx87.annihilator_type_a.client.AnnihilationClientState;
import com.ccx87.annihilator_type_a.client.ClientConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;


public final class AbilityHud {
    private static final ResourceLocation ICON = ResourceLocation.fromNamespaceAndPath(
            "dragonsurvival", "abilities/annihilator_type_a/annihilation_formula");
    private static final Component TITLE =
            Component.translatable("dragon_ability.dragonsurvival.annihilation_moment");

    private static final int WIDTH = 162;
    private static final int HEIGHT = 26;
    private static final int ICON_SIZE = 16;

    private static boolean iconUnavailable;

    private AbilityHud() {}

    public static void render(GuiGraphics graphics) {
        if (!ClientConfig.HUD_ENABLED.get() || !AnnihilationClientState.isActive()) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int x = (screenWidth - WIDTH) / 2 + ClientConfig.HUD_TOP_OFFSET_X.get();
        int y = ClientConfig.HUD_TOP_OFFSET_Y.get();

        float progress = AnnihilationClientState.getProgress();
        HudTheme.panel(graphics, x, y, WIDTH, HEIGHT);
        HudTheme.sweep(graphics, x + 1, y + 1, WIDTH - 2, HEIGHT - 2,
                (net.minecraft.Util.getMillis() % 2000L) / 1000.0F);
        drawIcon(graphics, x + 4, y + 5, ICON_SIZE);

        graphics.drawString(minecraft.font, TITLE, x + 24, y + 5, HudTheme.TEXT, true);
        Component level = Component.translatable("hud.annihilator_type_a.level",
                AnnihilationClientState.getLevel());
        graphics.drawString(minecraft.font, level, x + 28 + minecraft.font.width(TITLE), y + 5,
                HudTheme.ACCENT, true);

        Component remaining = Component.translatable("hud.annihilator_type_a.remaining",
                HudTheme.seconds(AnnihilationClientState.getRemainingTicks()));
        graphics.drawString(minecraft.font, remaining,
                x + WIDTH - 5 - minecraft.font.width(remaining), y + 5,
                progress < 0.25F ? HudTheme.WARN : HudTheme.TEXT, true);

        HudTheme.bar(graphics, x + 24, y + 18, WIDTH - 30, 3, progress,
                HudTheme.ACCENT, HudTheme.lerpColor(HudTheme.ACCENT, HudTheme.WARN, 1.0F - progress),
                HudTheme.TRACK);
    }

    private static void drawIcon(GuiGraphics graphics, int x, int y, int size) {
        if (iconUnavailable) {
            return;
        }
        try {
            graphics.blitSprite(ICON, x, y, size, size);
        } catch (RuntimeException exception) {
            iconUnavailable = true;
        }
    }
}
