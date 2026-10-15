package com.ccx87.annihilator_type_a.client;

import com.ccx87.annihilator_type_a.AnnihilatorTypeA;
import com.ccx87.annihilator_type_a.client.hud.AbilityHud;
import com.ccx87.annihilator_type_a.client.hud.HudFrame;
import com.ccx87.annihilator_type_a.client.hud.LightningStormHud;
import com.ccx87.annihilator_type_a.client.hud.LightningStormWarning;
import com.ccx87.annihilator_type_a.client.hud.SelfHud;
import com.ccx87.annihilator_type_a.client.hud.TargetHud;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.event.ClientTickEvent.Post;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(
   modid = "annihilator_type_a",
   value = {Dist.CLIENT}
)
public final class ATAClientEvents {
   private static final ResourceLocation LAYER_LEFT = AnnihilatorTypeA.id("hud/annihilation_left");
   private static final ResourceLocation LAYER_RIGHT = AnnihilatorTypeA.id("hud/annihilation_right");
   private static final ResourceLocation LAYER_TOP = AnnihilatorTypeA.id("hud/annihilation_top");
   private static final ResourceLocation LAYER_FRAME = AnnihilatorTypeA.id("hud/annihilation_frame");

   private ATAClientEvents() {
   }

   @SubscribeEvent
   public static void onRegisterGuiLayers(RegisterGuiLayersEvent event) {
      event.registerAbove(VanillaGuiLayers.HOTBAR, LAYER_LEFT, (graphics, deltaTracker) -> timed("hud.self", SelfHud::render, graphics));
      event.registerAbove(VanillaGuiLayers.HOTBAR, LAYER_RIGHT, (graphics, deltaTracker) -> timed("hud.target", TargetHud::render, graphics));
      event.registerAbove(VanillaGuiLayers.BOSS_OVERLAY, LAYER_TOP, (graphics, deltaTracker) -> timed("hud.ability", AbilityHud::render, graphics));
      event.registerAbove(VanillaGuiLayers.HOTBAR, LAYER_FRAME, (graphics, deltaTracker) -> timed("hud.frame", HudFrame::render, graphics));
   }

   private static void timed(String section, Consumer<GuiGraphics> renderer, GuiGraphics graphics) {
      long startedAt = DebugTimings.begin();
      renderer.accept(graphics);
      DebugTimings.end(section, startedAt);
   }

   @SubscribeEvent
   public static void onClientTick(Post event) {
      AnnihilationClientState.tick(Minecraft.getInstance());
      LightningStormWarning.tick(Minecraft.getInstance());
      LightningStormPanels.tick(Minecraft.getInstance());
   }

   @SubscribeEvent
   public static void onRenderLevelStage(RenderLevelStageEvent event) {
      AnnihilationRingRenderer.onRenderLevelStage(event);
   }

   @SubscribeEvent
   public static void onRenderLightningWarning(net.neoforged.neoforge.client.event.RenderGuiEvent.Post event) {
      Minecraft minecraft = Minecraft.getInstance();
      LightningStormWarning.render(event.getGuiGraphics(), minecraft.getWindow().getGuiScaledWidth(), minecraft.getWindow().getGuiScaledHeight());
      LightningStormHud.render(event.getGuiGraphics(), minecraft.getWindow().getGuiScaledWidth(), minecraft.getWindow().getGuiScaledHeight());
   }

   @SubscribeEvent
   public static void onRenderStormPanels(RenderLevelStageEvent event) {
      LightningStormPanels.render(event);
   }
}
