package com.ccx87.annihilator_type_a.item;

import com.ccx87.annihilator_type_a.entity.ModEntities;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.DeferredSpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

import static com.ccx87.annihilator_type_a.AnnihilatorTypeA.MOD_ID;

public class ModItems {
    public final static DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(MOD_ID);

    public static final DeferredItem<Item> MINIATURE_UAV_EGG = ITEMS.register("miniature_uav_egg",
            () -> new DeferredSpawnEggItem(ModEntities.MINIATUREUAV, 0xbadff2, 0x90b3c4, new Item.Properties()));
    public static final DeferredItem<Item> GN001_EGG = ITEMS.register("gn001_egg",
            () -> new DeferredSpawnEggItem(ModEntities.GN001, 0xbadff3, 0x90b3c5, new Item.Properties()));
    public static final DeferredItem<Item> ANNIHILATOR_TYPE_A_FULL_ICON =
            ITEMS.register("annihilator_type_a_full_icon", ()-> new Item(new Item.Properties()));
    public static final DeferredItem<Item> ENERGY_BAR =
            ITEMS.register("energy_bar", EnergyBarItem::new);
    public static final Supplier<EnergySwordItem> ANNIHILATION_LIGHTSABER =
            ITEMS.register("annihilation_lightsaber",
                    () -> new EnergySwordItem(ModToolTiers.ENERGY_BAR,
                            new Item.Properties()
                                    .attributes(SwordItem.createAttributes(ModToolTiers.ENERGY_BAR, 9, -2.4f))
                                    .fireResistant()
                                    .stacksTo(1)
                    ));
    public static final DeferredItem<Item> FURNACE_CORE =
            ITEMS.register("furnace_core",
                    () -> new Item(new Item.Properties().food(
                            new FoodProperties.Builder()
                                    .nutrition(4)
                                    .saturationModifier(4.0f)
                                    .alwaysEdible()
                                    .build()
                        )
                    ));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }


}

