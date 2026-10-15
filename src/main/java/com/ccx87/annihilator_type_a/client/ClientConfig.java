package com.ccx87.annihilator_type_a.client;

import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.ModConfigSpec.BooleanValue;
import net.neoforged.neoforge.common.ModConfigSpec.Builder;
import net.neoforged.neoforge.common.ModConfigSpec.ConfigValue;
import net.neoforged.neoforge.common.ModConfigSpec.DoubleValue;
import net.neoforged.neoforge.common.ModConfigSpec.IntValue;

public final class ClientConfig {
   private static final Builder BUILDER = new Builder();
   public static final BooleanValue RING_ENABLED = BUILDER.comment("Draw the machined dial around every entity affected by the annihilation moment.")
      .translation("annihilator_type_a.configuration.ring.enabled")
      .define("ring.enabled", true);
   public static final DoubleValue RING_OPACITY = BUILDER.comment("Brightness multiplier of the dial, 0.0 - 2.0.")
      .translation("annihilator_type_a.configuration.ring.opacity")
      .defineInRange("ring.opacity", 0.85, 0.0, 2.0);
   public static final DoubleValue RING_VISIBLE_DISTANCE = BUILDER.comment("Affected entities farther away than this (in blocks) are not drawn.")
      .translation("annihilator_type_a.configuration.ring.visibleDistance")
      .defineInRange("ring.visibleDistance", 64.0, 8.0, 256.0);
   public static final BooleanValue RING_FOLLOW_SIZE = BUILDER.comment(
         new String[]{
            "Scale the dial and its sparks with the entity's body height, measured against",
            "one block - the height the baseline radii are calibrated for. Taller entities",
            "get a proportionally larger ring, shorter ones a proportionally smaller one."
         }
      )
      .translation("annihilator_type_a.configuration.ring.followEntitySize")
      .define("ring.followEntitySize", true);
   public static final DoubleValue RING_SIZE_MULTIPLIER = BUILDER.comment(
         new String[]{"Overall size multiplier applied on top of the entity scaling, 0.25 - 4.0.", "Use it to fine-tune the whole effect up or down."}
      )
      .translation("annihilator_type_a.configuration.ring.sizeMultiplier")
      .defineInRange("ring.sizeMultiplier", 1.0, 0.25, 4.0);
   public static final DoubleValue RING_SIZE_FOLLOW_STRENGTH = BUILDER.comment(
         new String[]{
            "How strongly the body height is followed, 0.0 - 1.0.",
            "1.0 = the ring scales in proportion to the height measured against one block;",
            "lower values damp the change in either direction."
         }
      )
      .translation("annihilator_type_a.configuration.ring.sizeFollowStrength")
      .defineInRange("ring.sizeFollowStrength", 1.0, 0.0, 1.0);
   public static final DoubleValue RING_MAX_SCALE = BUILDER.comment(
         new String[]{
            "Hard cap on the final ring scale, 0.35 - 4.0.",
            "Cost grows with the square of this. Dropping it from 4.0 to 2.5 removes about",
            "60% of the blended pixels the dial covers, which matters because the dial is",
            "four large additive quads."
         }
      )
      .translation("annihilator_type_a.configuration.ring.maxScale")
      .defineInRange("ring.maxScale", 2.5, 0.35, 4.0);
   public static final BooleanValue RING_DISTANCE_LOD = BUILDER.comment(
         new String[]{
            "Draw fewer dial layers for distant rings: past 24 blocks the encoder and index",
            "dials are only a few pixels across, and past 40 only the outer gear is drawn."
         }
      )
      .translation("annihilator_type_a.configuration.ring.distanceLod")
      .define("ring.distanceLod", true);
   public static final BooleanValue RING_CULL_OFFSCREEN = BUILDER.comment("Skip rings whose area lies entirely outside the view frustum.")
      .translation("annihilator_type_a.configuration.ring.cullOffscreen")
      .define("ring.cullOffscreen", true);
   public static final BooleanValue HUD_ENABLED = BUILDER.comment("Draw the annihilation moment HUD panels.")
      .translation("annihilator_type_a.configuration.hud.enabled")
      .define("hud.enabled", true);
   public static final BooleanValue HUD_DECORATIONS = BUILDER.comment("Draw the decorative corner brackets and the sweeping highlight.")
      .translation("annihilator_type_a.configuration.hud.decorations")
      .define("hud.decorations", true);
   public static final BooleanValue HUD_SELF_ONLY_WHILE_ACTIVE = BUILDER.comment(
         new String[]{
            "Only show the left self-status panel while the annihilation moment is active.",
            "Set to false to keep your own attributes on screen at all times.",
            "Either way the panel is exclusive to the mechanical dragon - it is identified by",
            "that species' permanent random fault marker - so no other species and no human",
            "ever sees it."
         }
      )
      .translation("annihilator_type_a.configuration.hud.selfPanelOnlyWhileActive")
      .define("hud.selfPanelOnlyWhileActive", true);
   public static final BooleanValue HUD_TARGET_ENABLED = BUILDER.comment("Show the right target panel for the creature under the crosshair.")
      .translation("annihilator_type_a.configuration.hud.targetPanel")
      .define("hud.targetPanel", true);
   public static final BooleanValue HUD_TARGET_ONLY_WHILE_ACTIVE = BUILDER.comment(
         new String[]{
            "Only show the right target panel while the annihilation moment is active.",
            "Set to false to keep it up whenever something is targeted.",
            "Either way the panel is exclusive to the mechanical dragon, so no other species",
            "and no human ever sees it."
         }
      )
      .translation("annihilator_type_a.configuration.hud.targetOnlyWhileActive")
      .define("hud.targetOnlyWhileActive", true);
   public static final BooleanValue HUD_TARGET_ATTRIBUTES = BUILDER.comment("Include the target's armour and attack damage in the right panel.")
      .translation("annihilator_type_a.configuration.hud.targetAttributes")
      .define("hud.targetAttributes", true);
   public static final BooleanValue HUD_TARGET_BUFFS = BUILDER.comment("Include the target's active effects in the right panel.")
      .translation("annihilator_type_a.configuration.hud.targetBuffs")
      .define("hud.targetBuffs", true);
   public static final DoubleValue HUD_TARGET_RANGE = BUILDER.comment("How far (in blocks) the crosshair looks for a creature to inspect.")
      .translation("annihilator_type_a.configuration.hud.targetRange")
      .defineInRange("hud.targetRange", 24.0, 4.0, 64.0);
   public static final BooleanValue HUD_FRAME = BUILDER.comment("Draw the full-screen visor frame while the annihilation moment is running.")
      .translation("annihilator_type_a.configuration.hud.frame")
      .define("hud.frame", true);
   public static final DoubleValue HUD_FRAME_INSET_X = BUILDER.comment(
         new String[]{"How far the frame sits in from the left and right edges, as a fraction,", "0.0 - 0.45."}
      )
      .translation("annihilator_type_a.configuration.hud.frameInsetX")
      .defineInRange("hud.frameInsetX", 0.14, 0.0, 0.45);
   public static final DoubleValue HUD_FRAME_INSET_Y = BUILDER.comment("How far it sits in from the top and bottom edges, 0.0 - 0.45.")
      .translation("annihilator_type_a.configuration.hud.frameInsetY")
      .defineInRange("hud.frameInsetY", 0.12, 0.0, 0.45);
   public static final DoubleValue HUD_FRAME_CHAMFER = BUILDER.comment(
         new String[]{
            "Length of the diagonal corner cuts, as a fraction of the screen height,", "0.0 - 0.60. Larger values read more like a hexagon than a box."
         }
      )
      .translation("annihilator_type_a.configuration.hud.frameChamfer")
      .defineInRange("hud.frameChamfer", 0.26, 0.0, 0.6);
   public static final DoubleValue HUD_FRAME_ALPHA = BUILDER.comment("Opacity of the frame's main contour, 0.0 - 1.0.")
      .translation("annihilator_type_a.configuration.hud.frameAlpha")
      .defineInRange("hud.frameAlpha", 0.59, 0.0, 1.0);
   public static final ConfigValue<String> HUD_FRAME_COLOR = BUILDER.comment("Accent colour of the frame, as a hex RGB string such as \"4FD8FF\".")
      .translation("annihilator_type_a.configuration.hud.frameColor")
      .define("hud.frameColor", "4FD8FF");
   public static final BooleanValue LIGHTNING_WARNING = BUILDER.comment(
         new String[]{
            "Show the WARNING: LIGHTNING STORM CREATED banner the moment the storm is",
            "released. An interrupted cast never reaches that point, so it shows nothing."
         }
      )
      .translation("annihilator_type_a.configuration.lightning.warning")
      .define("lightning.warning", true);
   public static final BooleanValue LIGHTNING_HUD = BUILDER.comment(
         new String[]{"Show the on-screen panel with the cast progress and the release state, so the", "effect stays readable even in first person."}
      )
      .translation("annihilator_type_a.configuration.lightning.hud")
      .define("lightning.hud", true);
   public static final IntValue HUD_TOP_OFFSET_X = BUILDER.comment("Horizontal offset of the top ability panel, in GUI pixels.")
      .translation("annihilator_type_a.configuration.hud.top.offsetX")
      .defineInRange("hud.top.offsetX", 0, -4000, 4000);
   public static final IntValue HUD_TOP_OFFSET_Y = BUILDER.comment("Vertical offset of the top ability panel, in GUI pixels.")
      .translation("annihilator_type_a.configuration.hud.top.offsetY")
      .defineInRange("hud.top.offsetY", 6, -4000, 4000);
   public static final IntValue HUD_LEFT_OFFSET_X = BUILDER.comment("Horizontal offset of the left self-status panel, in GUI pixels.")
      .translation("annihilator_type_a.configuration.hud.left.offsetX")
      .defineInRange("hud.left.offsetX", 8, -4000, 4000);
   public static final IntValue HUD_LEFT_OFFSET_Y = BUILDER.comment("Vertical offset of the left self-status panel, in GUI pixels.")
      .translation("annihilator_type_a.configuration.hud.left.offsetY")
      .defineInRange("hud.left.offsetY", 0, -4000, 4000);
   public static final IntValue HUD_RIGHT_OFFSET_X = BUILDER.comment("Horizontal offset of the right target panel, in GUI pixels.")
      .translation("annihilator_type_a.configuration.hud.right.offsetX")
      .defineInRange("hud.right.offsetX", 8, -4000, 4000);
   public static final IntValue HUD_RIGHT_OFFSET_Y = BUILDER.comment("Vertical offset of the right target panel, in GUI pixels.")
      .translation("annihilator_type_a.configuration.hud.right.offsetY")
      .defineInRange("hud.right.offsetY", 0, -4000, 4000);
   public static final ModConfigSpec SPEC = BUILDER.build();

   public static boolean loaded() {
      return SPEC.isLoaded();
   }

   private ClientConfig() {
   }
}
