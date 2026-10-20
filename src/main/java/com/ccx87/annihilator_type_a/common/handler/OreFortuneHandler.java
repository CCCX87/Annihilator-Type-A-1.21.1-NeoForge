package com.ccx87.annihilator_type_a.common.handler;

import com.ccx87.annihilator_type_a.registry.ATAEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.BlockDropsEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.ccx87.annihilator_type_a.AnnihilatorTypeA.MOD_ID;

@EventBusSubscriber(modid = MOD_ID)
public class OreFortuneHandler {

    private static final Random RANDOM = new Random();

    private static final List<Block> ORE_BLOCKS = new ArrayList<>();

    static {
        ORE_BLOCKS.add(Blocks.COAL_ORE);
        ORE_BLOCKS.add(Blocks.DEEPSLATE_COAL_ORE);
        ORE_BLOCKS.add(Blocks.IRON_ORE);
        ORE_BLOCKS.add(Blocks.DEEPSLATE_IRON_ORE);
        ORE_BLOCKS.add(Blocks.COPPER_ORE);
        ORE_BLOCKS.add(Blocks.DEEPSLATE_COPPER_ORE);
        ORE_BLOCKS.add(Blocks.GOLD_ORE);
        ORE_BLOCKS.add(Blocks.DEEPSLATE_GOLD_ORE);
        ORE_BLOCKS.add(Blocks.REDSTONE_ORE);
        ORE_BLOCKS.add(Blocks.DEEPSLATE_REDSTONE_ORE);
        ORE_BLOCKS.add(Blocks.EMERALD_ORE);
        ORE_BLOCKS.add(Blocks.DEEPSLATE_EMERALD_ORE);
        ORE_BLOCKS.add(Blocks.LAPIS_ORE);
        ORE_BLOCKS.add(Blocks.DEEPSLATE_LAPIS_ORE);
        ORE_BLOCKS.add(Blocks.DIAMOND_ORE);
        ORE_BLOCKS.add(Blocks.DEEPSLATE_DIAMOND_ORE);
        ORE_BLOCKS.add(Blocks.NETHER_QUARTZ_ORE);
        ORE_BLOCKS.add(Blocks.NETHER_GOLD_ORE);
        ORE_BLOCKS.add(Blocks.ANCIENT_DEBRIS);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onBlockDrops(BlockDropsEvent event) {
        if (!(event.getBreaker() instanceof Player player)) {
            return;
        }

        BlockState state = event.getState();
        if (!ORE_BLOCKS.contains(state.getBlock())) return;

        if (isSilkTouchDrop(event, state)) return;

        MobEffectInstance effect = player.getEffect(ATAEffects.ORE_FORTUNE);
        if (effect == null) return;

        int amplifier = effect.getAmplifier();
        int levelBonus = amplifier + 1;
        float chance = levelBonus * 0.20f;
        if (chance <= 0 || RANDOM.nextFloat() >= chance) return;

        List<ItemEntity> drops = event.getDrops();
        List<ItemEntity> extraDrops = new ArrayList<>();

        for (ItemEntity dropEntity : drops) {
            ItemStack stack = dropEntity.getItem();
            if (!stack.isEmpty()) {
                ItemStack extra = stack.copy();
                ItemEntity newEntity = new ItemEntity(
                        player.level(),
                        dropEntity.getX(),
                        dropEntity.getY(),
                        dropEntity.getZ(),
                        extra
                );
                extraDrops.add(newEntity);
            }
        }

        drops.addAll(extraDrops);
    }


    private static boolean isSilkTouchDrop(BlockDropsEvent event, BlockState state) {
        ItemStack blockItem = new ItemStack(state.getBlock().asItem());
        if (blockItem.isEmpty()) return false;

        for (ItemEntity dropEntity : event.getDrops()) {
            ItemStack stack = dropEntity.getItem();
            if (!stack.isEmpty() && ItemStack.isSameItem(stack, blockItem)) {
                return true;
            }
        }
        return false;
    }
}