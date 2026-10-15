package com.ccx87.annihilator_type_a.event;

import com.ccx87.annihilator_type_a.item.EnergySwordItem;
import com.ccx87.annihilator_type_a.registry.ATAEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

import static com.ccx87.annihilator_type_a.AnnihilatorTypeA.MOD_ID;

@EventBusSubscriber(modid = MOD_ID)
public class WeaponDamageHandler {

    @SubscribeEvent
    public static void onLivingDamagePre(LivingDamageEvent.Pre event) {

        if (!(event.getSource().getEntity() instanceof Player player)) return;
        ItemStack weapon = player.getMainHandItem();
        if (!(weapon.getItem() instanceof EnergySwordItem)) return;

        MobEffectInstance effect = player.getEffect(ATAEffects.RANDOM_FAULT);
        if (effect == null) {return;}

        float bonus = EnergySwordItem.getBonusDamage(weapon, player);
        event.setNewDamage(event.getNewDamage() + bonus);
    }
}