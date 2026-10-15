package com.ccx87.annihilator_type_a.item;

import com.ccx87.annihilator_type_a.component.ModDataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

public class EnergyBarItem extends Item {
    private static final int ENERGY_COST_PER_EAT = 200;
    private static final int MAX_ENERGY = Integer.MAX_VALUE;

    public EnergyBarItem() {
        super(new Properties()
                .stacksTo(1)
                .food(new FoodProperties.Builder()
                        .nutrition(2)
                        .saturationModifier(2f)
                        .build())
        );
    }


    public int getUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (getEnergy(stack) >= ENERGY_COST_PER_EAT) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        } else {
            return InteractionResultHolder.fail(stack);
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide && entity instanceof Player) {
            int current = getEnergy(stack);
            if (current < ENERGY_COST_PER_EAT) {
                return stack;
            }


            setEnergy(stack, current - ENERGY_COST_PER_EAT);


            ItemStack foodCopy = stack.copy();
            foodCopy.setCount(1);
            super.finishUsingItem(foodCopy, level, entity);
        }
        return stack;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        int energy = getEnergySafe(stack);
        tooltip.add(Component.translatable("tooltip.annihilator_type_a.energy", energy));
    }

    private static int getEnergySafe(ItemStack stack) {
        try {
            return stack.getOrDefault(ModDataComponents.ENERGY.get(), 0);
        } catch (Exception e) {
            return 0;
        }
    }

    public static int getEnergy(ItemStack stack) {
        return stack.getOrDefault(ModDataComponents.ENERGY.get(), 0);
    }

    public static void setEnergy(ItemStack stack, int energy) {
        stack.set(ModDataComponents.ENERGY.get(), Math.max(0, Math.min(energy, MAX_ENERGY)));
    }

    public static void addEnergy(ItemStack stack, int amount) {
        long newEnergy = (long) getEnergy(stack) + amount;
        newEnergy = Math.max(0, Math.min(newEnergy, MAX_ENERGY));
        setEnergy(stack, (int) newEnergy);
    }
}