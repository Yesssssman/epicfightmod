package yesman.epicfight.gameasset;

import java.util.Set;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.data.reloader.SkillManager;
import yesman.epicfight.api.forgeevent.SkillBuildEvent;
import yesman.epicfight.api.utils.math.ValueModifier;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.skill.AirAttack;
import yesman.epicfight.skill.BasicAttack;
import yesman.epicfight.skill.BattojutsuPassive;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.Skill.ActivateType;
import yesman.epicfight.skill.Skill.Resource;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.dodge.DodgeSkill;
import yesman.epicfight.skill.dodge.KnockdownWakeupSkill;
import yesman.epicfight.skill.dodge.StepSkill;
import yesman.epicfight.skill.guard.GuardSkill;
import yesman.epicfight.skill.guard.ImpactGuardSkill;
import yesman.epicfight.skill.guard.ParryingSkill;
import yesman.epicfight.skill.identity.RevelationSkill;
import yesman.epicfight.skill.identity.MeteorSlamSkill;
import yesman.epicfight.skill.mover.DemolitionLeapSkill;
import yesman.epicfight.skill.mover.PhantomAscentSkill;
import yesman.epicfight.skill.passive.BerserkerSkill;
import yesman.epicfight.skill.passive.DeathHarvestSkill;
import yesman.epicfight.skill.passive.EmergencyEscapeSkill;
import yesman.epicfight.skill.passive.EnduranceSkill;
import yesman.epicfight.skill.passive.ForbiddenStrengthSkill;
import yesman.epicfight.skill.passive.HyperVitalitySkill;
import yesman.epicfight.skill.passive.PassiveSkill;
import yesman.epicfight.skill.passive.StaminaPillagerSkill;
import yesman.epicfight.skill.passive.SwordmasterSkill;
import yesman.epicfight.skill.passive.TechnicianSkill;
import yesman.epicfight.skill.weaponinnate.BladeRushSkill;
import yesman.epicfight.skill.weaponinnate.ConditionalWeaponInnateSkill;
import yesman.epicfight.skill.weaponinnate.EverlastingAllegiance;
import yesman.epicfight.skill.weaponinnate.EviscerateSkill;
import yesman.epicfight.skill.weaponinnate.BattojutsuSkill;
import yesman.epicfight.skill.weaponinnate.SteelWhirlwindSkill;
import yesman.epicfight.skill.weaponinnate.GraspingSpireSkill;
import yesman.epicfight.skill.weaponinnate.GuillotineAxeSkill;
import yesman.epicfight.skill.weaponinnate.RushingTempoSkill;
import yesman.epicfight.skill.weaponinnate.LiechtenauerSkill;
import yesman.epicfight.skill.weaponinnate.SimpleWeaponInnateSkill;
import yesman.epicfight.skill.weaponinnate.WrathfulLightingSkill;
import yesman.epicfight.skill.weaponinnate.WeaponInnateSkill;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategories;
import yesman.epicfight.world.damagesource.ExtraDamageInstance;
import yesman.epicfight.world.damagesource.EpicFightDamageType;
import yesman.epicfight.world.damagesource.StunType;

@Mod.EventBusSubscriber(modid = EpicFightMod.MODID, bus=EventBusSubscriber.Bus.FORGE)
public class EpicFightSkills {
	/** Default skills **/
	public static Skill BASIC_ATTACK;
	public static Skill AIR_ATTACK;
	public static Skill KNOCKDOWN_WAKEUP;
	/** Dodging skills **/
	public static Skill ROLL;
	public static Skill STEP;
	/** Guard skills **/
	public static Skill GUARD;
	public static Skill PARRYING;
	public static Skill IMPACT_GUARD;
	/** Passive skills **/
	public static Skill BERSERKER;
	public static Skill DEATH_HARVEST;
	public static Skill EMERGENCY_ESCAPE;
	public static Skill ENDURANCE;
	public static Skill FORBIDDEN_STRENGTH;
	public static Skill HYPERVITALITY;
	public static Skill STAMINA_PILLAGER;
	public static Skill SWORD_MASTER;
	public static Skill TECHNICIAN;
	/** Weapon innate skills**/
	public static Skill GUILLOTINE_AXE;
	public static Skill SWEEPING_EDGE;
	public static Skill DANCING_EDGE;
	public static Skill GRASPING_SPIRE;
	public static Skill HEARTPIERCER;
	public static Skill STEEL_WHIRLWIND;
	public static Skill BATTOJUTSU;
	public static Skill BATTOJUTSU_PASSIVE;
	public static Skill RUSHING_TEMPO;
	public static Skill RELENTLESS_COMBO;
	public static Skill SHARP_STAB;
	public static Skill LIECHTENAUER;
	public static Skill EVISCERATE;
	public static Skill BLADE_RUSH;
	public static Skill WRATHFUL_LIGHTING;
	public static Skill TSUNAMI;
	public static Skill EVERLASTING_ALLEGIANCE;
	/** Battle style skills **/
	public static Skill METEOR_STRIKE;
	public static Skill REVELATION;
	/** Mover skills **/
	public static Skill DEMOLITION_LEAP;
	public static Skill PHANTOM_ASCENT;
	
	public static void registerSkills() {
		SkillManager.register(BasicAttack::new, BasicAttack.createBasicAttackBuilder(), EpicFightMod.MODID, "basic_attack");
		SkillManager.register(AirAttack::new, AirAttack.createAirAttackBuilder(), EpicFightMod.MODID, "air_attack");
		SkillManager.register(DodgeSkill::new, DodgeSkill.createDodgeBuilder().setAnimations(new ResourceLocation(EpicFightMod.MODID, "biped/skill/roll_forward"), new ResourceLocation(EpicFightMod.MODID, "biped/skill/roll_backward")), EpicFightMod.MODID, "roll");
		SkillManager.register(StepSkill::new, DodgeSkill.createDodgeBuilder().setAnimations(new ResourceLocation(EpicFightMod.MODID, "biped/skill/step_forward"), new ResourceLocation(EpicFightMod.MODID, "biped/skill/step_backward"), new ResourceLocation(EpicFightMod.MODID, "biped/skill/step_left"), new ResourceLocation(EpicFightMod.MODID, "biped/skill/step_right")), EpicFightMod.MODID, "step");
		SkillManager.register(KnockdownWakeupSkill::new, DodgeSkill.createDodgeBuilder().setAnimations(new ResourceLocation(EpicFightMod.MODID, "biped/skill/knockdown_wakeup_left"), new ResourceLocation(EpicFightMod.MODID, "biped/skill/knockdown_wakeup_right")).setCategory(SkillCategories.KNOCKDOWN_WAKEUP), EpicFightMod.MODID, "knockdown_wakeup");
		
		SkillManager.register(GuardSkill::new, GuardSkill.createGuardBuilder(), EpicFightMod.MODID, "guard");
		SkillManager.register(ParryingSkill::new, ParryingSkill.createActiveGuardBuilder(), EpicFightMod.MODID, "parrying");
		SkillManager.register(ImpactGuardSkill::new, ImpactGuardSkill.createEnergizingGuardBuilder(), EpicFightMod.MODID, "impact_guard");
		
		SkillManager.register(BerserkerSkill::new, PassiveSkill.createPassiveBuilder(), EpicFightMod.MODID, "berserker");
		SkillManager.register(DeathHarvestSkill::new, PassiveSkill.createPassiveBuilder(), EpicFightMod.MODID, "death_harvest");
		SkillManager.register(EmergencyEscapeSkill::new, EmergencyEscapeSkill.createEmergencyEscapeBuilder().addAvailableWeaponCategory(WeaponCategories.SWORD, WeaponCategories.UCHIGATANA, WeaponCategories.DAGGER), EpicFightMod.MODID, "emergency_escape");
		SkillManager.register(EnduranceSkill::new, PassiveSkill.createPassiveBuilder().setResource(Resource.COOLDOWN).setActivateType(ActivateType.DURATION), EpicFightMod.MODID, "endurance");
		SkillManager.register(ForbiddenStrengthSkill::new, PassiveSkill.createPassiveBuilder(), EpicFightMod.MODID, "forbidden_strength");
		SkillManager.register(HyperVitalitySkill::new, PassiveSkill.createPassiveBuilder().setResource(Resource.COOLDOWN).setActivateType(ActivateType.TOGGLE), EpicFightMod.MODID, "hypervitality");
		SkillManager.register(StaminaPillagerSkill::new, PassiveSkill.createPassiveBuilder(), EpicFightMod.MODID, "stamina_pillager");
		SkillManager.register(SwordmasterSkill::new, PassiveSkill.createPassiveBuilder(), EpicFightMod.MODID, "swordmaster");
		SkillManager.register(TechnicianSkill::new, PassiveSkill.createPassiveBuilder(), EpicFightMod.MODID, "technician");
		
		SkillManager.register(SimpleWeaponInnateSkill::new, SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setAnimations(new ResourceLocation(EpicFightMod.MODID, "biped/skill/sweeping_edge")), EpicFightMod.MODID, "sweeping_edge");
		SkillManager.register(SimpleWeaponInnateSkill::new, SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setAnimations(new ResourceLocation(EpicFightMod.MODID, "biped/skill/dancing_edge")), EpicFightMod.MODID, "dancing_edge");
		SkillManager.register(GuillotineAxeSkill::new, SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setAnimations(new ResourceLocation(EpicFightMod.MODID, "biped/skill/the_guillotine")), EpicFightMod.MODID, "the_guillotine");
		SkillManager.register(GraspingSpireSkill::new, WeaponInnateSkill.createWeaponInnateBuilder(), EpicFightMod.MODID, "grasping_spire");
		SkillManager.register(SimpleWeaponInnateSkill::new, SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setAnimations(new ResourceLocation(EpicFightMod.MODID, "biped/skill/heartpiercer")), EpicFightMod.MODID, "heartpiercer");
		SkillManager.register(SteelWhirlwindSkill::new, WeaponInnateSkill.createWeaponInnateBuilder().setActivateType(ActivateType.CHARGING), EpicFightMod.MODID, "steel_whirlwind");
		SkillManager.register(BattojutsuSkill::new, ConditionalWeaponInnateSkill.createConditionalWeaponInnateBuilder().setSelector((executer) -> executer.getOriginal().isSprinting() ? 1 : 0).setAnimations(new ResourceLocation(EpicFightMod.MODID, "biped/skill/battojutsu"), new ResourceLocation(EpicFightMod.MODID, "biped/skill/battojutsu_dash")), EpicFightMod.MODID, "battojutsu");
		SkillManager.register(BattojutsuPassive::new, Skill.createBuilder().setCategory(SkillCategories.WEAPON_PASSIVE).setActivateType(ActivateType.ONE_SHOT).setResource(Resource.COOLDOWN), EpicFightMod.MODID, "battojutsu_passive");
		SkillManager.register(RushingTempoSkill::new, WeaponInnateSkill.createWeaponInnateBuilder(), EpicFightMod.MODID, "rushing_tempo");
		SkillManager.register(SimpleWeaponInnateSkill::new, SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setAnimations(new ResourceLocation(EpicFightMod.MODID, "biped/skill/relentless_combo")), EpicFightMod.MODID, "relentless_combo");
		SkillManager.register(LiechtenauerSkill::new, WeaponInnateSkill.createWeaponInnateBuilder().setActivateType(ActivateType.DURATION), EpicFightMod.MODID, "liechtenauer");
		SkillManager.register(SimpleWeaponInnateSkill::new, SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setAnimations(new ResourceLocation(EpicFightMod.MODID, "biped/skill/sharp_stab")), EpicFightMod.MODID, "sharp_stab");
		SkillManager.register(EviscerateSkill::new, WeaponInnateSkill.createWeaponInnateBuilder(), EpicFightMod.MODID, "eviscerate");
		SkillManager.register(BladeRushSkill::new, BladeRushSkill.createBladeRushBuilder(), EpicFightMod.MODID, "blade_rush");
		SkillManager.register(WrathfulLightingSkill::new, SimpleWeaponInnateSkill.createSimpleWeaponInnateBuilder().setAnimations(new ResourceLocation(EpicFightMod.MODID, "biped/skill/wrathful_lighting")), EpicFightMod.MODID, "wrathful_lighting");
		SkillManager.register(ConditionalWeaponInnateSkill::new, ConditionalWeaponInnateSkill.createConditionalWeaponInnateBuilder().setSelector((executer) ->executer.getOriginal().isInWaterOrRain() ? 1 : 0).setAnimations(new ResourceLocation(EpicFightMod.MODID, "biped/skill/tsunami"), new ResourceLocation(EpicFightMod.MODID, "biped/skill/tsunami_reinforced")), EpicFightMod.MODID, "tsunami");
		SkillManager.register(EverlastingAllegiance::new, WeaponInnateSkill.createWeaponInnateBuilder(), EpicFightMod.MODID, "everlasting_allegiance");
		
		SkillManager.register(MeteorSlamSkill::new, MeteorSlamSkill.createMeteorSlamBuilder(), EpicFightMod.MODID, "meteor_slam");
		SkillManager.register(RevelationSkill::new, RevelationSkill.createRevelationSkillBuilder(), EpicFightMod.MODID, "revelation");
		
		SkillManager.register(DemolitionLeapSkill::new, Skill.createMoverBuilder().setActivateType(ActivateType.CHARGING), EpicFightMod.MODID, "demolition_leap");
		SkillManager.register(PhantomAscentSkill::new, Skill.createMoverBuilder().setResource(Resource.COOLDOWN), EpicFightMod.MODID, "phantom_ascent");
	}
	
	@SubscribeEvent
	public static void buildSkillEvent(SkillBuildEvent onBuild) {
		BASIC_ATTACK = onBuild.build(EpicFightMod.MODID, "basic_attack");
		AIR_ATTACK = onBuild.build(EpicFightMod.MODID, "air_attack");
		ROLL = onBuild.build(EpicFightMod.MODID, "roll");
		STEP = onBuild.build(EpicFightMod.MODID, "step");
		KNOCKDOWN_WAKEUP = onBuild.build(EpicFightMod.MODID, "knockdown_wakeup");
		
		GUARD = onBuild.build(EpicFightMod.MODID, "guard");
		PARRYING = onBuild.build(EpicFightMod.MODID, "parrying");
		IMPACT_GUARD = onBuild.build(EpicFightMod.MODID, "impact_guard");
		
		BERSERKER = onBuild.build(EpicFightMod.MODID, "berserker");
		DEATH_HARVEST = onBuild.build(EpicFightMod.MODID, "death_harvest");
		EMERGENCY_ESCAPE = onBuild.build(EpicFightMod.MODID, "emergency_escape");
		ENDURANCE = onBuild.build(EpicFightMod.MODID, "endurance");
		FORBIDDEN_STRENGTH = onBuild.build(EpicFightMod.MODID, "forbidden_strength");
		HYPERVITALITY = onBuild.build(EpicFightMod.MODID, "hypervitality");
		STAMINA_PILLAGER = onBuild.build(EpicFightMod.MODID, "stamina_pillager");
		SWORD_MASTER = onBuild.build(EpicFightMod.MODID, "swordmaster");
		TECHNICIAN = onBuild.build(EpicFightMod.MODID, "technician");
		
		METEOR_STRIKE = onBuild.build(EpicFightMod.MODID, "meteor_slam");
		REVELATION = onBuild.build(EpicFightMod.MODID, "revelation");
		
		DEMOLITION_LEAP = onBuild.build(EpicFightMod.MODID, "demolition_leap");
		PHANTOM_ASCENT = onBuild.build(EpicFightMod.MODID, "phantom_ascent");
		
		WeaponInnateSkill sweepingEdge = onBuild.build(EpicFightMod.MODID, "sweeping_edge");
		sweepingEdge.newProperty()
					.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(1))
					.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.0F))
					.addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(20.0F))
					.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.6F))
					.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
					.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
					.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageType.WEAPON_INNATE))
					.registerPropertiesToAnimation();
		SWEEPING_EDGE = sweepingEdge;
		
		WeaponInnateSkill dancingEdge = onBuild.build(EpicFightMod.MODID, "dancing_edge");
		dancingEdge.newProperty()
					.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(1))
					.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.2F))
					.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
					.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageType.WEAPON_INNATE))
					.newProperty()
					.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(1))
					.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.2F))
					.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
					.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageType.WEAPON_INNATE))
					.newProperty()
					.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(1))
					.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.2F))
					.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
					.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageType.WEAPON_INNATE))
					.registerPropertiesToAnimation();
		DANCING_EDGE = dancingEdge;
		
		WeaponInnateSkill theGuillotine = onBuild.build(EpicFightMod.MODID, "the_guillotine");
		theGuillotine.newProperty()
					.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(1))
					.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.5F))
					.addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(20.0F))
					.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(2.0F))
					.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
					.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
					.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageType.WEAPON_INNATE))
					.registerPropertiesToAnimation();
		GUILLOTINE_AXE = theGuillotine;
		
		WeaponInnateSkill graspingSpire = onBuild.build(EpicFightMod.MODID, "grasping_spire");
		graspingSpire.newProperty()
					.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(3))
					.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
					.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageType.WEAPON_INNATE))
					.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
					.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(4.0F))
					.newProperty()
					.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(4))
					.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.25F))
					.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.2F))
					.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
					.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageType.WEAPON_INNATE))
					.registerPropertiesToAnimation();
		GRASPING_SPIRE = graspingSpire;
		
		WeaponInnateSkill heartpiercer = onBuild.build(EpicFightMod.MODID, "heartpiercer");
		heartpiercer.newProperty()
					.addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(10.0F))
					.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
					.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
					.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageType.WEAPON_INNATE))
					.registerPropertiesToAnimation();
		HEARTPIERCER = heartpiercer;
		
		WeaponInnateSkill steelWhirlwind = onBuild.build(EpicFightMod.MODID, "steel_whirlwind");
		steelWhirlwind.newProperty()
					  .addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(1.4F))
					  .addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
					  .addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageType.WEAPON_INNATE))
					  .registerPropertiesToAnimation();
		STEEL_WHIRLWIND = steelWhirlwind;
		
		BATTOJUTSU_PASSIVE = onBuild.build(EpicFightMod.MODID, "battojutsu_passive");
		
		WeaponInnateSkill battojutsu = onBuild.build(EpicFightMod.MODID, "battojutsu");
		battojutsu.newProperty()
					.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(2.0F))
					.addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(50.0F))
					.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(6))
					.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
					.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
					.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageType.WEAPON_INNATE))
					.registerPropertiesToAnimation();
		BATTOJUTSU = battojutsu;
		
		WeaponInnateSkill rushingTempo = onBuild.build(EpicFightMod.MODID, "rushing_tempo");
		rushingTempo.newProperty()
					.addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(50.0F))
					.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.adder(2))
					.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.7F))
					.addProperty(AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_SHARP.get())
					.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
					.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageType.WEAPON_INNATE))
					.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
					.registerPropertiesToAnimation();
		RUSHING_TEMPO = rushingTempo;
		
		WeaponInnateSkill relentlessCombo = onBuild.build(EpicFightMod.MODID, "relentless_combo");
		relentlessCombo.newProperty()
					.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.6F))
					.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(1))
					.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
					.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
					.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
					.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageType.WEAPON_INNATE))
					.registerPropertiesToAnimation();
		RELENTLESS_COMBO = relentlessCombo;
		
		WeaponInnateSkill sharpStab = onBuild.build(EpicFightMod.MODID, "sharp_stab");
		sharpStab.newProperty()
					.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(0.4F))
					.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.multiplier(0.5F))
					.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
					.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
					.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageType.WEAPON_INNATE, EpicFightDamageType.GUARD_PUNCTURE))
					.registerPropertiesToAnimation();
		SHARP_STAB = sharpStab;
		
		LIECHTENAUER = onBuild.build(EpicFightMod.MODID, "liechtenauer");
		
		WeaponInnateSkill eviscerate = onBuild.build(EpicFightMod.MODID, "eviscerate");
		eviscerate.newProperty()
					.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(1))
					.addProperty(AttackPhaseProperty.IMPACT_MODIFIER, ValueModifier.setter(2.0F))
					.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
					.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageType.WEAPON_INNATE))
					.newProperty()
					.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(1))
					.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create(), ExtraDamageInstance.TARGET_LOST_HEALTH.create(0.5F)))
					.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageType.WEAPON_INNATE))
					.addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(50.0F))
					.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
				.registerPropertiesToAnimation();
		EVISCERATE = eviscerate;
		
		WeaponInnateSkill bladeRush = onBuild.build(EpicFightMod.MODID, "blade_rush");
		bladeRush.newProperty()
					.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(1))
					.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
					.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageType.WEAPON_INNATE))
					.newProperty()
					.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageType.EXECUTION, EpicFightDamageType.WEAPON_INNATE))
					.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.NONE)
					.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLADE_RUSH_FINISHER.get())
				.registerPropertiesToAnimation();
		BLADE_RUSH = bladeRush;
		
		WeaponInnateSkill wrathfulLighting = onBuild.build(EpicFightMod.MODID, "wrathful_lighting");
		wrathfulLighting.newProperty()
					.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(1))
					.newProperty()
					.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.setter(8.0F))
					.addProperty(AttackPhaseProperty.MAX_STRIKES_MODIFIER, ValueModifier.setter(3))
					.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
					.addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(100.0F))
					.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN)
					.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageType.WEAPON_INNATE))
				.registerPropertiesToAnimation();
		WRATHFUL_LIGHTING = wrathfulLighting;
		
		WeaponInnateSkill tsunami = onBuild.build(EpicFightMod.MODID, "tsunami");
		tsunami.newProperty()
					.addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(100.0F))
					.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageType.WEAPON_INNATE))
					.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN)
				.registerPropertiesToAnimation();
		TSUNAMI = tsunami;
		
		WeaponInnateSkill everlastAllegiance = onBuild.build(EpicFightMod.MODID, "everlasting_allegiance");
		everlastAllegiance.newProperty()
					.addProperty(AttackPhaseProperty.ARMOR_NEGATION_MODIFIER, ValueModifier.adder(30.0F))
					.addProperty(AttackPhaseProperty.DAMAGE_MODIFIER, ValueModifier.multiplier(1.4F))
					.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, Set.of(ExtraDamageInstance.SWEEPING_EDGE_ENCHANTMENT.create()))
					.addProperty(AttackPhaseProperty.SOURCE_TAG, Set.of(EpicFightDamageType.WEAPON_INNATE))
					.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.registerPropertiesToAnimation();
		EVERLASTING_ALLEGIANCE = everlastAllegiance;
		
	}
}