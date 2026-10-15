package com.ccx87.annihilator_type_a;

import com.ccx87.annihilator_type_a.client.particles.Annihilator_Type_A_Fire_Particle;
import com.ccx87.annihilator_type_a.client.particles.Annihilator_Type_A_Large_Fire_Particle;
import com.ccx87.annihilator_type_a.common.handler.LightningStormHandler;
import com.ccx87.annihilator_type_a.common.handler.RandomFaultHandler;
import com.ccx87.annihilator_type_a.component.ModDataComponents;
import com.ccx87.annihilator_type_a.entity.ModEntities;
import com.ccx87.annihilator_type_a.entity.client.Gn001ProjectileRenderer;
import com.ccx87.annihilator_type_a.entity.client.Gn001Renderer;
import com.ccx87.annihilator_type_a.entity.client.MiniatureUAVRenderer;
import com.ccx87.annihilator_type_a.item.ModCreativeModeTabs;
import com.ccx87.annihilator_type_a.item.ModItems;
import com.ccx87.annihilator_type_a.registry.ATAEffects;
import com.ccx87.annihilator_type_a.registry.ATAParticles;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
        // The Common config that used to be registered here was the NeoForge example
        // mod's demo file; it has been removed along with its example options.
        // import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;



@Mod(AnnihilatorTypeA.MOD_ID)
public class AnnihilatorTypeA {

    public static final String MOD_ID = "annihilator_type_a";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, MOD_ID);



    public AnnihilatorTypeA(IEventBus modEventBus, ModContainer modContainer) {
        modEventBus.addListener(this::commonSetup);
        NeoForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);


        ModCreativeModeTabs.register(modEventBus);
        ATAParticles.register(modEventBus);
        ATAEffects.register(modEventBus);
        ModEntities.register(modEventBus);
        NeoForge.EVENT_BUS.register(RandomFaultHandler.class);
        NeoForge.EVENT_BUS.addListener(LightningStormHandler::onProjectileHit);
        ModItems.register(modEventBus);
        ModDataComponents.COMPONENTS.register(modEventBus);




    }



    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    private void commonSetup(FMLCommonSetupEvent event) {


    }


    private void addCreative(BuildCreativeModeTabContentsEvent event) {

    }


    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {


    }
    @EventBusSubscriber(
            modid = "annihilator_type_a",
            value = {Dist.CLIENT}
    )
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {}

        @SubscribeEvent
        public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
            event.registerSpriteSet(ATAParticles.Annihilator_Type_A_Fire.get(), Annihilator_Type_A_Fire_Particle.Provider::new);
            event.registerSpriteSet(ATAParticles.Annihilator_Type_A_Large_Fire.get(), Annihilator_Type_A_Large_Fire_Particle.Provider::new);

            EntityRenderers.register(ModEntities.GN001_PROJECTILE.get(), Gn001ProjectileRenderer::new);
            EntityRenderers.register(ModEntities.MINIATUREUAV.get(), MiniatureUAVRenderer::new);
            EntityRenderers.register(ModEntities.GN001.get(), Gn001Renderer::new);

        }

    }


}
