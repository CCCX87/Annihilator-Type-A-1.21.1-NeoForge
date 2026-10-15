package com.ccx87.annihilator_type_a;

import com.ccx87.annihilator_type_a.client.ClientConfig;
import com.ccx87.annihilator_type_a.entity.ModEntities;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;


@Mod(value = AnnihilatorTypeA.MOD_ID, dist = Dist.CLIENT)

@EventBusSubscriber(modid = AnnihilatorTypeA.MOD_ID, value = Dist.CLIENT)
public class AnnihilatorTypeAClient {
    public AnnihilatorTypeAClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        container.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {


    }
}
