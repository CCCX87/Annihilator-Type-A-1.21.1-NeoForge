package com.ccx87.annihilator_type_a.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static com.ccx87.annihilator_type_a.AnnihilatorTypeA.MOD_ID;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);
//创造物品栏添加
    public static final Supplier<CreativeModeTab> TUTORIAL_TAB =
            CREATIVE_MODE_TABS.register("annihilator_type_a_tab", ()-> CreativeModeTab.builder()
                    .icon(()-> new ItemStack(ModItems.ANNIHILATOR_TYPE_A_FULL_ICON.get()))
                    .title(Component.translatable("itemGroup.annihilator_type_a_tab"))
                    .displayItems((parameters, output) ->{
                        output.accept(ModItems.MINIATURE_UAV_EGG);
                        output.accept(ModItems.ANNIHILATOR_TYPE_A_FULL_ICON);
                        output.accept(ModItems.ENERGY_BAR);
                        output.accept(ModItems.ANNIHILATION_LIGHTSABER.get());
                        output.accept(ModItems.GN001_EGG);
                        output.accept(ModItems.FURNACE_CORE.get());
                    }).build());


    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);

    }
}
