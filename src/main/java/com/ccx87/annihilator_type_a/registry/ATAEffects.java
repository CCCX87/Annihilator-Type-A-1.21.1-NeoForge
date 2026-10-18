package com.ccx87.annihilator_type_a.registry;

import com.ccx87.annihilator_type_a.common.effects.ATADebuffStack_Effect;
import com.ccx87.annihilator_type_a.common.effects.ATADebuff_Effect;
import com.ccx87.annihilator_type_a.common.effects.ArmorBreak;
import com.ccx87.annihilator_type_a.common.effects.DamageCapEffect;
import com.ccx87.annihilator_type_a.common.effects.HealthReductionEffect;
import com.ccx87.annihilator_type_a.common.effects.IgnoreInvincibilityEffect;
import com.ccx87.annihilator_type_a.common.effects.LifeStealEffect;
import com.ccx87.annihilator_type_a.common.effects.LostHealthBoostEffect;
import com.ccx87.annihilator_type_a.common.effects.MineralSacrificeEffect;
import com.ccx87.annihilator_type_a.common.effects.OreFortuneEffect;
import com.ccx87.annihilator_type_a.common.effects.ParameterOverloadEffect;
import com.ccx87.annihilator_type_a.common.effects.StarvationEffect;
import com.ccx87.annihilator_type_a.common.effects.TrueDamageConversionEffect;
import com.ccx87.annihilator_type_a.common.effects.VisionDistortionEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ATAEffects {
   public static final DeferredRegister<MobEffect> MOB_Effects = DeferredRegister.create(BuiltInRegistries.MOB_EFFECT, "annihilator_type_a");
   public static final Holder<MobEffect> ATADebuff = MOB_Effects.register("ata_debuff", () -> new ATADebuff_Effect(MobEffectCategory.HARMFUL, -1));
   public static final Holder<MobEffect> ATADebuffStack = MOB_Effects.register(
      "ata_debuff_stack", () -> new ATADebuffStack_Effect(MobEffectCategory.HARMFUL, -1)
   );
   public static final Holder<MobEffect> ARMOR_BREAK = MOB_Effects.register("armor_break", () -> new ArmorBreak(MobEffectCategory.HARMFUL, 15246103));
   public static final Holder<MobEffect> HEALTH_REDUCTION = MOB_Effects.register(
      "health_reduction", () -> new HealthReductionEffect(MobEffectCategory.HARMFUL, 11141120)
   );
   public static final Holder<MobEffect> STARVATION = MOB_Effects.register("starvation", () -> new StarvationEffect(MobEffectCategory.HARMFUL, 9127187));
   public static final Holder<MobEffect> TRUE_DAMAGE_CONVERSION = MOB_Effects.register(
      "true_damage_conversion", () -> new TrueDamageConversionEffect(MobEffectCategory.BENEFICIAL, 16777215)
   );
   public static final Holder<MobEffect> LOST_HEALTH_BOOST = MOB_Effects.register(
      "lost_health_boost", () -> new LostHealthBoostEffect(MobEffectCategory.BENEFICIAL, 16755200)
   );
   public static final Holder<MobEffect> VISION_DISTORTION = MOB_Effects.register(
      "vision_distortion", () -> new VisionDistortionEffect(MobEffectCategory.NEUTRAL, 5592405)
   );
   public static final Holder<MobEffect> IGNORE_INVINCIBILITY = MOB_Effects.register(
      "ignore_invincibility", () -> new IgnoreInvincibilityEffect(MobEffectCategory.BENEFICIAL, 65280)
   );
   public static final Holder<MobEffect> LIFE_STEAL = MOB_Effects.register("life_steal", () -> new LifeStealEffect(MobEffectCategory.BENEFICIAL, 16711935));
   public static final Holder<MobEffect> MINERAL_SACRIFICE = MOB_Effects.register(
      "mineral_sacrifice", () -> new MineralSacrificeEffect(MobEffectCategory.BENEFICIAL, 16766720)
   );
   public static final Holder<MobEffect> DAMAGE_CAP = MOB_Effects.register("damage_cap", () -> new DamageCapEffect(MobEffectCategory.BENEFICIAL, 43775));
   public static final Holder<MobEffect> PARAMETER_OVERLOAD = MOB_Effects.register(
      "parameter_overload", () -> new ParameterOverloadEffect(MobEffectCategory.BENEFICIAL, 65450)
   );
   public static final Holder<MobEffect> RANDOM_FAULT = MOB_Effects.register("random_fault", () -> new MobEffect(MobEffectCategory.NEUTRAL, 8421504) {
      });
   public static final Holder<MobEffect> ORE_FORTUNE = MOB_Effects.register("ore_fortune", () -> new OreFortuneEffect(MobEffectCategory.BENEFICIAL, 16766720));
   public static final Holder<MobEffect> NO_FALL_DAMAGE = MOB_Effects.register("no_fall_damage", () -> new MobEffect(MobEffectCategory.BENEFICIAL, 43775) {
      });
   public static final Holder<MobEffect> LIGHTNING_STORM = MOB_Effects.register("lightning_storm", () -> new MobEffect(MobEffectCategory.BENEFICIAL, 65535) {
      });
   public static final Holder<MobEffect> LIGHTNING_CHARGE = MOB_Effects.register("lightning_charge", () -> new MobEffect(MobEffectCategory.NEUTRAL, 5888255) {
      });
   public static final Holder<MobEffect> AUTO_SMELT = MOB_Effects.register("auto_smelt", () -> new MobEffect(MobEffectCategory.BENEFICIAL, 16737792) {
      });

   public static void register(IEventBus eventBus) {
      MOB_Effects.register(eventBus);
   }

   /** Annihilation moment charge marker: applied only while its cast bar runs, no icon, pure marker. */
   public static final net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> ANNIHILATION_CHARGE =
      MOB_Effects.register("annihilation_charge",
         () -> new net.minecraft.world.effect.MobEffect(net.minecraft.world.effect.MobEffectCategory.NEUTRAL, 0x6FE3FF) {});

   /** Mark Adjudication charge marker: applied only while its cast bar runs, no icon, pure marker. */
   public static final net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> MARK_ADJUDICATE_CHARGE =
      MOB_Effects.register("mark_adjudicate_charge",
         () -> new net.minecraft.world.effect.MobEffect(net.minecraft.world.effect.MobEffectCategory.NEUTRAL, 0xFF4040) {});

   /**
    * Mark Adjudication impact marker: applied for a couple of ticks when the cast completes.
    *
    * <p>It exists so the JSON can hand the ability level to the server: the effect's amplifier is
    * {@code level - 1}, taken from the same {@code LevelBasedValue} the damage used to use. The
    * strike handler reads it, removes it, and lands the damage when the lance actually hits.
    */
   public static final net.minecraft.core.Holder<net.minecraft.world.effect.MobEffect> MARK_ADJUDICATE_IMPACT =
      MOB_Effects.register("mark_adjudicate_impact",
         () -> new net.minecraft.world.effect.MobEffect(net.minecraft.world.effect.MobEffectCategory.NEUTRAL, 0xFF4040) {});
}
