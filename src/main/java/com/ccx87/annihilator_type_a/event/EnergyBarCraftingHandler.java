package com.ccx87.annihilator_type_a.event;

import com.ccx87.annihilator_type_a.item.EnergyBarItem;
import com.ccx87.annihilator_type_a.item.EnergySwordItem;
import com.ccx87.annihilator_type_a.item.ModItems;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;


import java.util.HashMap;
import java.util.Map;

import static com.ccx87.annihilator_type_a.AnnihilatorTypeA.MOD_ID;

@EventBusSubscriber(modid = MOD_ID)
public class EnergyBarCraftingHandler {



    private static final Map<Item, Integer> MINERAL_ENERGY = new HashMap<>();
    static {
        MINERAL_ENERGY.put(Items.IRON_INGOT, 150);
        MINERAL_ENERGY.put(Items.GOLD_INGOT, 200);
        MINERAL_ENERGY.put(Items.DIAMOND, 600);
        MINERAL_ENERGY.put(Items.NETHERITE_INGOT, 1000);
        MINERAL_ENERGY.put(Items.EMERALD, 200);
        MINERAL_ENERGY.put(Items.LAPIS_LAZULI, 125);
        MINERAL_ENERGY.put(Items.REDSTONE, 50);
        MINERAL_ENERGY.put(Items.COAL, 100);
        MINERAL_ENERGY.put(Items.QUARTZ, 75);
        MINERAL_ENERGY.put(Items.COPPER_INGOT, 100);
        MINERAL_ENERGY.put(Items.AMETHYST_SHARD, 250);

        MINERAL_ENERGY.put(Items.IRON_BLOCK, 1350);
        MINERAL_ENERGY.put(Items.GOLD_BLOCK, 1800);
        MINERAL_ENERGY.put(Items.DIAMOND_BLOCK, 5400);
        MINERAL_ENERGY.put(Items.NETHERITE_BLOCK, 9000);
        MINERAL_ENERGY.put(Items.EMERALD_BLOCK, 1800);
        MINERAL_ENERGY.put(Items.LAPIS_BLOCK, 1125);
        MINERAL_ENERGY.put(Items.REDSTONE_BLOCK, 450);
        MINERAL_ENERGY.put(Items.COAL_BLOCK, 900);
        MINERAL_ENERGY.put(Items.QUARTZ_BLOCK, 300);
        MINERAL_ENERGY.put(Items.COPPER_BLOCK, 900);
        MINERAL_ENERGY.put(Items.AMETHYST_BLOCK, 1000);
    }
    private static final Map<ResourceLocation, Integer> EXTRA_MINERAL_ENERGY = new HashMap<>();
    static {
        EXTRA_MINERAL_ENERGY.put(ResourceLocation.parse("dragonsurvival:heart_element"), 400);
        EXTRA_MINERAL_ENERGY.put(ResourceLocation.parse("dragonsurvival:weak_dragon_heart"), 800);
        EXTRA_MINERAL_ENERGY.put(ResourceLocation.parse("dragonsurvival:elder_dragon_heart"), 2000);
    }

    @SubscribeEvent
    public static void onItemCrafted(PlayerEvent.ItemCraftedEvent event) {
        ItemStack result = event.getCrafting();
        //能量剑与能量棒合成
        if (result.getItem() instanceof EnergySwordItem) {
            CraftingContainer crafting = (CraftingContainer) event.getInventory();
            int weaponEnergy = 0;
            int totalBarEnergy = 0;
            boolean hasEnergyBar = false;

            for (int i = 0; i < crafting.getContainerSize(); i++) {
                ItemStack stack = crafting.getItem(i);
                if (stack.isEmpty()) continue;

                if (stack.is(ModItems.ENERGY_BAR.get())) {
                    hasEnergyBar = true;
                    totalBarEnergy += EnergyBarItem.getEnergy(stack);
                } else if (stack.is(ModItems.ANNIHILATION_LIGHTSABER.get())) {

                    weaponEnergy = EnergySwordItem.getEnergy(stack);
                }
            }



            if (hasEnergyBar) {
                EnergySwordItem.setEnergy(result, weaponEnergy + totalBarEnergy);
            }
            return;
        }


        //能量棒与矿物合成
        if (!result.is(ModItems.ENERGY_BAR.get())) return;
        Container container = event.getInventory();
        if (!(container instanceof CraftingContainer crafting)) return;

        int totalEnergyGain = 0;
        boolean hasMineral = false;
        ItemStack energyBarStack = ItemStack.EMPTY;

        for (int i = 0; i < crafting.getContainerSize(); i++) {
            ItemStack stack = crafting.getItem(i);
            if (stack.isEmpty()) continue;

            if (stack.is(ModItems.ENERGY_BAR.get())) {
                energyBarStack = stack;
            } else if (!stack.is(ModItems.ENERGY_BAR.get())) {
                hasMineral = true;
                int energy = 0;
                energy = MINERAL_ENERGY.getOrDefault(stack.getItem(), 0);

                if (energy == 0) {
                    ResourceLocation regName = BuiltInRegistries.ITEM.getKey(stack.getItem());
                    energy = EXTRA_MINERAL_ENERGY.getOrDefault(regName, 0);
                }
                totalEnergyGain += energy;
            } else{
                return;
            }
        }

        if (!hasMineral || energyBarStack.isEmpty()) return;

        int originalEnergy = EnergyBarItem.getEnergy(energyBarStack);
        EnergyBarItem.setEnergy(result, originalEnergy + totalEnergyGain);


    }

    private static boolean isMineral(ItemStack stack) {return MINERAL_ENERGY.containsKey(stack.getItem());}

}