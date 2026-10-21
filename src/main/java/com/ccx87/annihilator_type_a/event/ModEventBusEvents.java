package com.ccx87.annihilator_type_a.event;

import com.ccx87.annihilator_type_a.entity.ModEntities;
import com.ccx87.annihilator_type_a.entity.client.Gn001Model;
import com.ccx87.annihilator_type_a.entity.client.MiniatureUAVModel;
import com.ccx87.annihilator_type_a.entity.custom.Gn001Entity;
import com.ccx87.annihilator_type_a.entity.custom.MiniatureUAVEntity;
import com.ccx87.annihilator_type_a.item.ModItems;
import com.ccx87.annihilator_type_a.registry.ATAEffects;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.event.ModifyDefaultComponentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.level.BlockDropsEvent;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.ccx87.annihilator_type_a.AnnihilatorTypeA.MOD_ID;

@EventBusSubscriber(modid = MOD_ID)
public class ModEventBusEvents {

    private static final ResourceLocation PARAMETER_OVERLOAD_ID =
            ResourceLocation.fromNamespaceAndPath("annihilator_type_a", "parameter_overload_bonus");



    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event){
        event.registerLayerDefinition(MiniatureUAVModel.LAYER_LOCATION,MiniatureUAVModel::createBodyLayer);
        event.registerLayerDefinition(Gn001Model.LAYER_GN,Gn001Model::createBodyLayer);
    }

    private static void registerAttributes(EntityAttributeCreationEvent event){
        event.put(ModEntities.MINIATUREUAV.get(), MiniatureUAVEntity.createAttributes().build());
        event.put(ModEntities.GN001.get(), Gn001Entity.createAttributes().build());
    }

    @SubscribeEvent
    public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(ModEntities.MINIATUREUAV.get(), MiniatureUAVEntity.createAttributes().build());
        event.put(ModEntities.GN001.get(), Gn001Entity.createAttributes().build());
    }


    @SubscribeEvent
    public static void onPlayerAttack(AttackEntityEvent event) {
        Player player = event.getEntity();
        if (player == null) return;

        if (player.isDeadOrDying() || player.getHealth() <= 0) return;

        MobEffectInstance effect = player.getEffect(ATAEffects.PARAMETER_OVERLOAD);
        if (effect == null) {
            removeOverloadModifier(player);
        } else {
            int amplifier = effect.getAmplifier();  // 技能等级-1
            int buffCount = player.getActiveEffects().size();

            float bonusPerBuff = 0.014f + (amplifier * 0.004f);
            float damageBonus = buffCount * bonusPerBuff;

            if (damageBonus <= 0) {
                removeOverloadModifier(player);
            } else {
                updateOverloadModifier(player, damageBonus);
            }
        }

        if (player.hasEffect(ATAEffects.IGNORE_INVINCIBILITY)) {
            if (event.getTarget() instanceof LivingEntity target
                    && target.isAlive()
                    && !target.isDeadOrDying()) {
                target.invulnerableTime = 0;
            }
        }
    }

    private static void updateOverloadModifier(Player player, float bonus) {
        var attribute = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attribute == null) return;

        removeOverloadModifier(player);

        AttributeModifier modifier = new AttributeModifier(
                PARAMETER_OVERLOAD_ID,
                bonus,
                AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
        );
        attribute.addTransientModifier(modifier);
    }

    private static void removeOverloadModifier(Player player) {
        var attribute = player.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attribute == null) return;

        AttributeModifier oldModifier = attribute.getModifier(PARAMETER_OVERLOAD_ID);
        if (oldModifier != null) {
            attribute.removeModifier(oldModifier);
        }
    }

    @SubscribeEvent
    public static void onLivingDamage(LivingDamageEvent.Pre event) {

        LivingEntity target = event.getEntity();
        if (!target.isAlive() || target.isDeadOrDying()) {
            return;
        }

        DamageSource source = event.getSource();
        float originalDamage = event.getNewDamage();
        float modifiedDamage = originalDamage;

        Player player = null;


        if (source.getEntity() instanceof Player p) {
            player = p;


            MobEffectInstance lostHealthInstance = player.getEffect(ATAEffects.LOST_HEALTH_BOOST);
            if (lostHealthInstance != null) {
                int amplifier = lostHealthInstance.getAmplifier();
                float maxHealth = player.getMaxHealth();
                float currentHealth = player.getHealth();
                float lostPercent = (maxHealth - currentHealth) / maxHealth;
                float baseBoostPer10Percent = 0.10f + (amplifier * 0.02f);
                float boostPercent = lostPercent / 0.1f;
                float boost = originalDamage * boostPercent * baseBoostPer10Percent;
                modifiedDamage += boost;
            }


            MobEffectInstance mineralInstance = player.getEffect(ATAEffects.MINERAL_SACRIFICE);
            float mineralBonus = 0.0f;
            if (mineralInstance != null) {
                int amplifier = mineralInstance.getAmplifier();
                ItemStack offhand = player.getOffhandItem();
                if (!offhand.isEmpty() && offhand.getCount() > 0) {
                    float bonusPercent = getOreDamageBonus(offhand, amplifier);
                    if (bonusPercent > 0) {
                        mineralBonus = originalDamage * bonusPercent;
                        modifiedDamage += mineralBonus;
                    }
                }
            }

            event.setNewDamage(modifiedDamage);

            if (event.getNewDamage() > 0 && player != null && mineralInstance != null) {
                ItemStack offhand = player.getOffhandItem();
                if (!offhand.isEmpty() && offhand.getCount() > 0) {
                    int amplifier = mineralInstance.getAmplifier();
                    float bonusPercent = getOreDamageBonus(offhand, amplifier);
                    if (bonusPercent > 0) {
                        offhand.shrink(1);
                    }
                }
            }
        }

        MobEffectInstance damageCapInstance = target.getEffect(ATAEffects.DAMAGE_CAP);
        if (damageCapInstance != null) {
            int amplifier = damageCapInstance.getAmplifier();
            float maxHealth = target.getMaxHealth();
            float capPercent = Math.max(0.50f - (amplifier * 0.05f), 0.05f);
            float maxAllowed = maxHealth * capPercent;

            if (event.getNewDamage() > maxAllowed) {
                event.setNewDamage(maxAllowed);
            }
        }
    }

    @SubscribeEvent
    public static void onLivingDamagePost(LivingDamageEvent.Post event) {
        DamageSource source = event.getSource();
        if (!(source.getEntity() instanceof Player player)) {
            return;
        }

        LivingEntity target = event.getEntity();
        if (!target.isAlive() || target.isDeadOrDying()) {
            return;
        }

        MobEffectInstance trueDamageInstance = player.getEffect(ATAEffects.TRUE_DAMAGE_CONVERSION);
        if (trueDamageInstance != null) {
            int amplifier = trueDamageInstance.getAmplifier();
            float attackDamage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
            float trueDamagePercent = 0.10f + (amplifier * 0.05f);
            float trueDamageAmount = attackDamage * trueDamagePercent;

            MobEffectInstance capInstance = target.getEffect(ATAEffects.DAMAGE_CAP);
            if (capInstance != null) {
                int capAmp = capInstance.getAmplifier();
                float capPercent = Math.max(0.50f - capAmp * 0.05f, 0.05f);
                trueDamageAmount = Math.min(trueDamageAmount, target.getMaxHealth() * capPercent);
            }

            if (trueDamageAmount > 0) {
                float newHealth = target.getHealth() - trueDamageAmount;
                if (newHealth <= 0) {
                    target.setHealth(0);
                    target.die(target.damageSources().generic());
                } else {
                    target.setHealth(newHealth);
                }
            }
        }

        if (!target.isAlive()) {
            return;
        }

        float actualDamage = event.getNewDamage();
        if (actualDamage > 0) {
            MobEffectInstance lifeStealInstance = player.getEffect(ATAEffects.LIFE_STEAL);
            if (lifeStealInstance != null) {
                int amplifier = lifeStealInstance.getAmplifier();
                float healPercent = 0.05f + (amplifier * 0.01f);
                float heal = player.getMaxHealth() * healPercent;
                player.heal(heal);
            }
        }
    }

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onBlockDrops(BlockDropsEvent event) {
        if (!(event.getBreaker() instanceof Player player)) {
            return;
        }
        if (player.isCreative()) return;
        if (!player.hasEffect(ATAEffects.AUTO_SMELT)) return;
        if (!(event.getLevel() instanceof ServerLevel level)) return;

        List<ItemEntity> drops = event.getDrops();
        if (drops.isEmpty()) return;

        BlockState state = event.getState();
        ItemStack blockItem = new ItemStack(state.getBlock().asItem());
        if (!blockItem.isEmpty()) {
            for (ItemEntity dropEntity : drops) {
                ItemStack stack = dropEntity.getItem();
                if (!stack.isEmpty() && ItemStack.isSameItem(stack, blockItem)) {
                    return;
                }
            }
        }

        for (ItemEntity dropEntity : drops) {
            ItemStack original = dropEntity.getItem();
            if (original.isEmpty()) continue;
            ItemStack smelted = smelt(level, original);
            if (!smelted.isEmpty()) {
                dropEntity.setItem(smelted);
            }
        }
    }

    private static ItemStack smelt(ServerLevel level, ItemStack input) {
        SingleRecipeInput recipeInput = new SingleRecipeInput(input);
        Optional<RecipeHolder<SmeltingRecipe>> smelting = level.getRecipeManager()
                .getRecipeFor(RecipeType.SMELTING, recipeInput, level);
        if (smelting.isPresent()) {
            return scaleResult(smelting.get().value().getResultItem(level.registryAccess()), input.getCount());
        }
        Optional<RecipeHolder<net.minecraft.world.item.crafting.BlastingRecipe>> blasting =
                level.getRecipeManager().getRecipeFor(RecipeType.BLASTING, recipeInput, level);
        if (blasting.isPresent()) {
            return scaleResult(blasting.get().value().getResultItem(level.registryAccess()), input.getCount());
        }
        return ItemStack.EMPTY;
    }

    private static ItemStack scaleResult(ItemStack result, int inputCount) {
        if (result.isEmpty()) {
            return ItemStack.EMPTY;
        }
        return result.copyWithCount(result.getCount() * inputCount);
    }

    private static float getOreDamageBonus(ItemStack stack, int amplifier) {
        if (stack.is(Items.COAL) || stack.is(Items.CHARCOAL)) {
            return 0.02f + (amplifier * 0.01f);
        }
        if (stack.is(Items.IRON_INGOT) || stack.is(Items.COPPER_INGOT)) {
            return 0.04f + (amplifier * 0.02f);
        }
        if (stack.is(Items.QUARTZ)) {
            return 0.05f + (amplifier * 0.02f);
        }
        if (stack.is(Items.GOLD_INGOT)) {
            return 0.06f + (amplifier * 0.03f);
        }
        if (stack.is(Items.REDSTONE) || stack.is(Items.LAPIS_LAZULI)) {
            return 0.08f + (amplifier * 0.04f);
        }
        if (stack.is(Items.EMERALD)) {
            return 0.10f + (amplifier * 0.05f);
        }
        if (stack.is(Items.DIAMOND)) {
            return 0.15f + (amplifier * 0.07f);
        }
        if (stack.is(Items.NETHERITE_INGOT)) {
            return 0.4f + (amplifier * 0.10f);
        }
        return 0.0f;
    }



}
