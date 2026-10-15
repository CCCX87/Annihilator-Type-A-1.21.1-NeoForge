package com.ccx87.annihilator_type_a.client.hud;

import com.ccx87.annihilator_type_a.client.AnnihilationClientState;
import com.ccx87.annihilator_type_a.client.ClientConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public final class TargetHud {
    private static final int WIDTH = 118;
    private static final int DETAIL_HEIGHT = 30;
    private static final int COMPACT_HEIGHT = 22;
    private static final int CHIP = 7;
    private static final int CHIP_STRIDE = 8;

    private TargetHud() {}

    public static void render(GuiGraphics graphics) {
        if (!ClientConfig.HUD_ENABLED.get() || !ClientConfig.HUD_TARGET_ENABLED.get()) {
            return;
        }
        if (ClientConfig.HUD_TARGET_ONLY_WHILE_ACTIVE.get() && !AnnihilationClientState.isActive()) {
            return;
        }
        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null || minecraft.level == null) {
            return;
        }

        LivingEntity target = findTarget(minecraft, player);
        if (target == null) {
            return;
        }

        String attributeText = ClientConfig.HUD_TARGET_ATTRIBUTES.get() ? attributeText(target) : "";
        List<MobEffectInstance> effects = ClientConfig.HUD_TARGET_BUFFS.get()
                ? new ArrayList<>(target.getActiveEffects())
                : List.of();
        boolean detailed = !attributeText.isEmpty() || !effects.isEmpty();

        int height = detailed ? DETAIL_HEIGHT : COMPACT_HEIGHT;
        int screenWidth = minecraft.getWindow().getGuiScaledWidth();
        int screenHeight = minecraft.getWindow().getGuiScaledHeight();
        int x = screenWidth - WIDTH - ClientConfig.HUD_RIGHT_OFFSET_X.get();
        int y = (screenHeight - height) / 2 + ClientConfig.HUD_RIGHT_OFFSET_Y.get();

        HudTheme.panel(graphics, x, y, WIDTH, height);
        HudTheme.sweep(graphics, x + 1, y + 1, WIDTH - 2, height - 2,
                (net.minecraft.Util.getMillis() % 2400L) / 2400.0F);

        float health = target.getHealth();
        float maxHealth = Math.max((float) attributeOrZero(target, Attributes.MAX_HEALTH), 1.0F);
        float ratio = Mth.clamp(health / maxHealth, 0.0F, 1.0F);

        String name = minecraft.font.plainSubstrByWidth(target.getDisplayName().getString(), WIDTH - 12);
        graphics.drawString(minecraft.font, name, x + 5, y + 3, HudTheme.TEXT, false);
        Component healthText = Component.literal(
                String.format(Locale.ROOT, "%.1f/%.1f", health, maxHealth));
        graphics.drawString(minecraft.font, healthText,
                x + WIDTH - 5 - minecraft.font.width(healthText), y + 3, HudTheme.WARN, false);

        HudTheme.bar(graphics, x + 5, y + 13, WIDTH - 10, 4, ratio,
                0xFFFF3B30, HudTheme.lerpColor(0xFFFF3B30, 0xFFFF9A6B, ratio), HudTheme.TRACK);

        if (detailed) {
            drawDetailRow(graphics, minecraft, effects, attributeText, x, y + 19);
        }
    }

    private static String attributeText(LivingEntity target) {
        StringBuilder text = new StringBuilder();
        int armor = Mth.floor(attributeOrZero(target, Attributes.ARMOR));
        if (armor > 0) {
            text.append(Component.translatable("hud.annihilator_type_a.armor_short", armor).getString());
        }
        double damage = attributeOrZero(target, Attributes.ATTACK_DAMAGE);
        if (damage > 0.0D) {
            if (text.length() > 0) {
                text.append(' ');
            }
            text.append(Component.translatable("hud.annihilator_type_a.attack_short",
                    String.format(Locale.ROOT, "%.1f", damage)).getString());
        }
        return text.toString();
    }

    private static double attributeOrZero(LivingEntity entity, Holder<Attribute> attribute) {
        return entity.getAttributes().hasAttribute(attribute)
                ? entity.getAttributeValue(attribute)
                : 0.0D;
    }

    private static void drawDetailRow(GuiGraphics graphics, Minecraft minecraft,
                                      List<MobEffectInstance> effects, String attributeText,
                                      int x, int rowY) {
        int cursor = x + 5;
        if (!attributeText.isEmpty()) {
            graphics.drawString(minecraft.font, attributeText, cursor, rowY, HudTheme.TEXT_DIM, false);
            cursor += minecraft.font.width(attributeText) + 6;
        }
        if (effects.isEmpty()) {
            return;
        }

        int available = x + WIDTH - 5 - cursor;
        int capacity = Math.max(0, available / CHIP_STRIDE);
        int shown = Math.min(effects.size(), capacity);
        int hidden = effects.size() - shown;
        while (hidden > 0 && shown > 0
                && shown * CHIP_STRIDE + minecraft.font.width("+" + hidden) > available) {
            shown--;
            hidden++;
        }

        for (int index = 0; index < shown; index++) {
            int color = 0xFF000000 | effects.get(index).getEffect().value().getColor();
            int chipX = cursor + index * CHIP_STRIDE;
            graphics.fill(chipX, rowY, chipX + CHIP, rowY + CHIP, color);
            HudTheme.outline(graphics, chipX, rowY, CHIP, CHIP, 0x60FFFFFF);
        }
        if (hidden > 0) {
            Component more = Component.literal("+" + hidden);
            int moreX = Math.min(cursor + shown * CHIP_STRIDE, x + WIDTH - 5 - minecraft.font.width(more));
            graphics.drawString(minecraft.font, more, moreX, rowY, HudTheme.TEXT_DIM, false);
        }
    }

    private static LivingEntity findTarget(Minecraft minecraft, Player player) {
        double range = ClientConfig.HUD_TARGET_RANGE.get();
        float partialTick = 1.0F;
        Vec3 eye = player.getEyePosition(partialTick);
        Vec3 look = player.getViewVector(partialTick);
        Vec3 reach = eye.add(look.scale(range));

        BlockHitResult blockHit = minecraft.level.clip(
                new ClipContext(eye, reach, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, player));
        Vec3 end = blockHit.getType() == HitResult.Type.MISS ? reach : blockHit.getLocation();

        AABB searchBox = player.getBoundingBox().expandTowards(look.scale(range)).inflate(1.0D);
        EntityHitResult hit = ProjectileUtil.getEntityHitResult(minecraft.level, player, eye, end,
                searchBox, entity -> entity instanceof LivingEntity
                        && entity.isAlive()
                        && entity != player
                        && !entity.isSpectator());
        if (hit != null && hit.getEntity() instanceof LivingEntity living) {
            return living;
        }
        return null;
    }
}
