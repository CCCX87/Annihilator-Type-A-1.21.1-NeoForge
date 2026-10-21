package com.ccx87.annihilator_type_a.common.handler;

import com.ccx87.annihilator_type_a.registry.ATAEffects;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.level.BlockDropsEvent;

import java.util.Random;

import static com.ccx87.annihilator_type_a.AnnihilatorTypeA.MOD_ID;


@EventBusSubscriber(modid = MOD_ID)
public final class OreFortuneHandler {
    private static final Random RANDOM = new Random();

    private OreFortuneHandler() {}

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onBlockDrops(BlockDropsEvent event) {
        if (!(event.getBreaker() instanceof Player player)) {
            return;
        }

        BlockState state = event.getState();
        if (!isOre(state)) {
            return;
        }
        if (isSilkTouchDrop(event, state)) {
            return;
        }

        MobEffectInstance effect = player.getEffect(ATAEffects.ORE_FORTUNE);
        if (effect == null) {
            return;
        }

        int level = Math.max(1, effect.getAmplifier() + 1);
        float chance = Math.min(1.0F, level * 0.20F);
        if (RANDOM.nextFloat() >= chance) {
            return;
        }

        for (ItemEntity drop : event.getDrops()) {
            ItemStack stack = drop.getItem();
            if (stack.isEmpty()) {
                continue;
            }
            int extra = 1 + RANDOM.nextInt(level);
            stack.grow(stack.getCount() * extra);
        }
    }

    private static boolean isOre(BlockState state) {
        if (state.is(Tags.Blocks.ORES)) {
            return true;
        }
        if (state.is(BlockTags.COAL_ORES) || state.is(BlockTags.IRON_ORES)
                || state.is(BlockTags.COPPER_ORES) || state.is(BlockTags.GOLD_ORES)
                || state.is(BlockTags.REDSTONE_ORES) || state.is(BlockTags.EMERALD_ORES)
                || state.is(BlockTags.LAPIS_ORES) || state.is(BlockTags.DIAMOND_ORES)) {
            return true;
        }
        return BuiltInRegistries.BLOCK.getKey(state.getBlock()).getPath().contains("_ore");
    }

    private static boolean isSilkTouchDrop(BlockDropsEvent event, BlockState state) {
        ItemStack blockItem = new ItemStack(state.getBlock().asItem());
        if (blockItem.isEmpty()) {
            return false;
        }
        for (ItemEntity drop : event.getDrops()) {
            ItemStack stack = drop.getItem();
            if (!stack.isEmpty() && ItemStack.isSameItem(stack, blockItem)) {
                return true;
            }
        }
        return false;
    }
}
