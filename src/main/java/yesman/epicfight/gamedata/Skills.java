package yesman.epicfight.gamedata;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.util.ResourceLocation;
import yesman.epicfight.animation.property.Property.AttackPhaseProperty;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.particle.Particles;
import yesman.epicfight.skill.ActiveGuardSkill;
import yesman.epicfight.skill.AirAttack;
import yesman.epicfight.skill.BasicAttack;
import yesman.epicfight.skill.BerserkerSkill;
import yesman.epicfight.skill.BladeRushSkill;
import yesman.epicfight.skill.DodgeSkill;
import yesman.epicfight.skill.EnergizingGuardSkill;
import yesman.epicfight.skill.EviscerateSkill;
import yesman.epicfight.skill.FatalDrawSkill;
import yesman.epicfight.skill.GuardSkill;
import yesman.epicfight.skill.KatanaPassive;
import yesman.epicfight.skill.LethalSlicingSkill;
import yesman.epicfight.skill.LiechtenauerSkill;
import yesman.epicfight.skill.PassiveSkill;
import yesman.epicfight.skill.SimpleSpecialAttackSkill;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.Skill.ActivateType;
import yesman.epicfight.skill.Skill.Resource;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.SpecialAttackSkill;
import yesman.epicfight.skill.StaminaPillagerSkill;
import yesman.epicfight.skill.StepSkill;
import yesman.epicfight.skill.SwordmasterSkill;
import yesman.epicfight.skill.TechnicianSkill;
import yesman.epicfight.utils.game.IExtendedDamageSource.StunType;
import yesman.epicfight.utils.math.ExtraDamageCalculator;
import yesman.epicfight.utils.math.ValueCorrector;

public class Skills {
	private static final Map<ResourceLocation, Skill> SKILLS = new HashMap<ResourceLocation, Skill> ();
	private static final Map<ResourceLocation, Skill> MODIFIABLE_SKILLS = new HashMap<ResourceLocation, Skill> ();
	private static final Random RANDOM_SEED = new Random();
	private static int LAST_PICK = 0;
	
	public static Skill findSkill(String skillName) {
		ResourceLocation rl = new ResourceLocation(EpicFightMod.MODID, skillName);
		if (SKILLS.containsKey(rl)) {
			return SKILLS.get(rl);
		} else {
			return null;
		}
	}
	
	public static Collection<Skill> getModifiableSkillCollection() {
		return MODIFIABLE_SKILLS.values();
	}
	
	public static String getRandomModifiableSkillName() {
		List<Skill> values = new ArrayList<Skill>(MODIFIABLE_SKILLS.values());
		LAST_PICK = (LAST_PICK + RANDOM_SEED.nextInt(values.size() - 1) + 1) % values.size();
		return values.get(LAST_PICK).getName();
	}
	
	public static Skill BASIC_ATTACK;
	public static Skill AIR_ATTACK;
	public static Skill ROLL;
	public static Skill STEP;
	public static Skill GUARD;
	public static Skill ACTIVE_GUARD;
	public static Skill ENERGIZING_GUARD;
	
	public static Skill BERSERKER;
	public static Skill STAMINA_PILLAGER;
	public static Skill SWORD_MASTER;
	public static Skill TECHNICIAN;
	
	public static Skill GUILLOTINE_AXE;
	public static Skill SWEEPING_EDGE;
	public static Skill DANCING_EDGE;
	public static Skill SLAUGHTER_STANCE;
	public static Skill HEARTPIERCER;
	public static Skill GIANT_WHIRLWIND;
	public static Skill FATAL_DRAW;
	public static Skill KATANA_PASSIVE;
	public static Skill LETHAL_SLICING;
	public static Skill RELENTLESS_COMBO;
	public static Skill LIECHTENAUER;
	public static Skill EVISCERATE;
	public static Skill BLADE_RUSH;
	
	public static void init() {
		BASIC_ATTACK = registerSkill(new BasicAttack(BasicAttack.createBuilder()));
		AIR_ATTACK = registerSkill(new AirAttack(AirAttack.createBuilder()));
		ROLL = registerSkill(new DodgeSkill(DodgeSkill.createBuilder(new ResourceLocation(EpicFightMod.MODID, "roll")).setConsumption(4.0F).setAnimations(Animations.BIPED_ROLL_FORWARD, Animations.BIPED_ROLL_BACKWARD)));
		STEP = registerSkill(new StepSkill(DodgeSkill.createBuilder(new ResourceLocation(EpicFightMod.MODID, "step")).setConsumption(3.0F).setAnimations(Animations.BIPED_STEP_FORWARD, Animations.BIPED_STEP_BACKWARD, Animations.BIPED_STEP_LEFT, Animations.BIPED_STEP_RIGHT)));
		
		GUARD = registerSkill(new GuardSkill(GuardSkill.createBuilder(new ResourceLocation(EpicFightMod.MODID, "guard")).setRequiredXp(5)));
		ACTIVE_GUARD = registerSkill(new ActiveGuardSkill(GuardSkill.createBuilder(new ResourceLocation(EpicFightMod.MODID, "active_guard")).setRequiredXp(8)));
		ENERGIZING_GUARD = registerSkill(new EnergizingGuardSkill(GuardSkill.createBuilder(new ResourceLocation(EpicFightMod.MODID, "energizing_guard")).setRequiredXp(8)));
		
		BERSERKER = registerSkill(new BerserkerSkill(PassiveSkill.createBuilder(new ResourceLocation(EpicFightMod.MODID, "berserker"))));
		STAMINA_PILLAGER = registerSkill(new StaminaPillagerSkill(PassiveSkill.createBuilder(new ResourceLocation(EpicFightMod.MODID, "stamina_pillager"))));
		SWORD_MASTER = registerSkill(new SwordmasterSkill(PassiveSkill.createBuilder(new ResourceLocation(EpicFightMod.MODID, "swordmaster"))));
		TECHNICIAN = registerSkill(new TechnicianSkill(PassiveSkill.createBuilder(new ResourceLocation(EpicFightMod.MODID, "technician"))));
		
		SWEEPING_EDGE = registerSkill(new SimpleSpecialAttackSkill(SimpleSpecialAttackSkill.createBuilder(new ResourceLocation(EpicFightMod.MODID, "sweeping_edge")).setConsumption(30.0F).setAnimations(Animations.SWEEPING_EDGE))
				.newPropertyLine()
				.addProperty(AttackPhaseProperty.MAX_STRIKES, ValueCorrector.getAdder(1))
				.addProperty(AttackPhaseProperty.DAMAGE, ValueCorrector.getMultiplier(1.0F))
				.addProperty(AttackPhaseProperty.ARMOR_NEGATION, ValueCorrector.getAdder(20.0F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
				.registerPropertiesToAnimation());
		
		DANCING_EDGE = registerSkill(new SimpleSpecialAttackSkill(SimpleSpecialAttackSkill.createBuilder(new ResourceLocation(EpicFightMod.MODID, "dancing_edge")).setConsumption(30.0F).setAnimations(Animations.DANCING_EDGE))
				.newPropertyLine()
				.addProperty(AttackPhaseProperty.MAX_STRIKES, ValueCorrector.getAdder(1))
				.addProperty(AttackPhaseProperty.IMPACT, ValueCorrector.getAdder(0.5F))
				.registerPropertiesToAnimation());
		
		GUILLOTINE_AXE = registerSkill(new SimpleSpecialAttackSkill(SimpleSpecialAttackSkill.createBuilder(new ResourceLocation(EpicFightMod.MODID, "guillotine_axe")).setConsumption(20.0F).setAnimations(Animations.GUILLOTINE_AXE))
				.newPropertyLine()
				.addProperty(AttackPhaseProperty.MAX_STRIKES, ValueCorrector.getSetter(1))
				.addProperty(AttackPhaseProperty.DAMAGE, ValueCorrector.getMultiplier(1.5F))
				.addProperty(AttackPhaseProperty.ARMOR_NEGATION, ValueCorrector.getAdder(20.0F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
				.registerPropertiesToAnimation());
		
		SLAUGHTER_STANCE = registerSkill(new SimpleSpecialAttackSkill(SimpleSpecialAttackSkill.createBuilder(new ResourceLocation(EpicFightMod.MODID, "slaughter_stance")).setConsumption(40.0F).setAnimations(Animations.SPEAR_SLASH))
				.newPropertyLine()
				.addProperty(AttackPhaseProperty.MAX_STRIKES, ValueCorrector.getAdder(4))
				.addProperty(AttackPhaseProperty.DAMAGE, ValueCorrector.getMultiplier(0.25F))
				.registerPropertiesToAnimation());
		
		HEARTPIERCER = registerSkill(new SimpleSpecialAttackSkill(SimpleSpecialAttackSkill.createBuilder(new ResourceLocation(EpicFightMod.MODID, "heartpiercer")).setConsumption(40.0F).setAnimations(Animations.SPEAR_THRUST))
				.newPropertyLine()
				.addProperty(AttackPhaseProperty.ARMOR_NEGATION, ValueCorrector.getAdder(10.0F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.registerPropertiesToAnimation());
		
		GIANT_WHIRLWIND = registerSkill(new SimpleSpecialAttackSkill(SimpleSpecialAttackSkill.createBuilder(new ResourceLocation(EpicFightMod.MODID, "giant_whirlwind")).setConsumption(60.0F).setAnimations(Animations.GIANT_WHIRLWIND))
				.newPropertyLine());
		
		FATAL_DRAW = registerSkill(new FatalDrawSkill(SpecialAttackSkill.createBuilder(new ResourceLocation(EpicFightMod.MODID, "fatal_draw")).setConsumption(30.0F))
				.newPropertyLine()
				.addProperty(AttackPhaseProperty.DAMAGE, ValueCorrector.getMultiplier(1.0F))
				.addProperty(AttackPhaseProperty.ARMOR_NEGATION, ValueCorrector.getAdder(50.0F))
				.addProperty(AttackPhaseProperty.MAX_STRIKES, ValueCorrector.getAdder(6))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.registerPropertiesToAnimation());
		
		KATANA_PASSIVE = registerSkill(new KatanaPassive(Skill.createBuilder(new ResourceLocation(EpicFightMod.MODID, "katana_passive"))
				.setCategory(SkillCategory.WEAPON_PASSIVE)
				.setConsumption(5.0F)
				.setActivateType(ActivateType.ONE_SHOT)
				.setResource(Resource.COOLDOWN)
		));
		
		LETHAL_SLICING = registerSkill(new LethalSlicingSkill(SpecialAttackSkill.createBuilder(new ResourceLocation(EpicFightMod.MODID, "lethal_slicing")).setConsumption(35.0F))
				.newPropertyLine()
				.addProperty(AttackPhaseProperty.MAX_STRIKES, ValueCorrector.getSetter(2))
				.addProperty(AttackPhaseProperty.IMPACT, ValueCorrector.getSetter(0.5F))
				.addProperty(AttackPhaseProperty.DAMAGE, ValueCorrector.getSetter(1.0F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
				.addProperty(AttackPhaseProperty.HIT_SOUND, Sounds.BLUNT_HIT)
				.addProperty(AttackPhaseProperty.PARTICLE, Particles.HIT_BLUNT)
				.newPropertyLine()
				.addProperty(AttackPhaseProperty.ARMOR_NEGATION, ValueCorrector.getAdder(50.0F))
				.addProperty(AttackPhaseProperty.MAX_STRIKES, ValueCorrector.getAdder(2))
				.addProperty(AttackPhaseProperty.DAMAGE, ValueCorrector.getMultiplier(0.7F))
				.addProperty(AttackPhaseProperty.SWING_SOUND, Sounds.WHOOSH_SHARP)
				.registerPropertiesToAnimation());
		
		RELENTLESS_COMBO = registerSkill(new SimpleSpecialAttackSkill(SimpleSpecialAttackSkill.createBuilder(new ResourceLocation(EpicFightMod.MODID, "relentless_combo")).setConsumption(20.0F).setAnimations(Animations.RELENTLESS_COMBO))
				.newPropertyLine()
				.addProperty(AttackPhaseProperty.MAX_STRIKES, ValueCorrector.getSetter(1))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.PARTICLE, Particles.HIT_BLUNT)
				.registerPropertiesToAnimation());
		
		LIECHTENAUER = registerSkill(new LiechtenauerSkill(SpecialAttackSkill.createBuilder(new ResourceLocation(EpicFightMod.MODID, "liechtenauer")).setConsumption(40.0F).setMaxDuration(4).setActivateType(ActivateType.DURATION_INFINITE)));
		
		EVISCERATE = registerSkill(new EviscerateSkill(SpecialAttackSkill.createBuilder(new ResourceLocation(EpicFightMod.MODID, "eviscerate")).setConsumption(25.0F))
				.newPropertyLine()
				.addProperty(AttackPhaseProperty.MAX_STRIKES, ValueCorrector.getSetter(1))
				.addProperty(AttackPhaseProperty.IMPACT, ValueCorrector.getSetter(2.0F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.newPropertyLine()
				.addProperty(AttackPhaseProperty.MAX_STRIKES, ValueCorrector.getSetter(1))
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, ExtraDamageCalculator.get(ExtraDamageCalculator.PERCENT_OF_TARGET_LOST_HEALTH, 0.5F))
				.addProperty(AttackPhaseProperty.ARMOR_NEGATION, ValueCorrector.getAdder(50.0F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
				.registerPropertiesToAnimation());
		
		BLADE_RUSH = registerSkill(new BladeRushSkill(SpecialAttackSkill.createBuilder(new ResourceLocation(EpicFightMod.MODID, "blade_rush")).setConsumption(25.0F).setMaxDuration(1).setMaxStack(4).setActivateType(ActivateType.TOGGLE))
				.newPropertyLine()
				.addProperty(AttackPhaseProperty.MAX_STRIKES, ValueCorrector.getSetter(1))
				.newPropertyLine()
				.addProperty(AttackPhaseProperty.DAMAGE, ValueCorrector.getMultiplier(1.5F))
				.addProperty(AttackPhaseProperty.ARMOR_NEGATION, ValueCorrector.getAdder(20.0F))
				.addProperty(AttackPhaseProperty.MAX_STRIKES, ValueCorrector.getSetter(1))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.HIT_SOUND, Sounds.BLADE_RUSH_FINISHER)
				.addProperty(AttackPhaseProperty.PARTICLE, Particles.BLADE_RUSH_SKILL)
				.registerPropertiesToAnimation());
	}
	
	public static Skill registerSkill(Skill skill) {
		registerIfAbsent(SKILLS, skill);
		if (skill.getCategory().modifiable()) {
			registerIfAbsent(MODIFIABLE_SKILLS, skill);
		}
		
		return skill;
	}
	
	private static void registerIfAbsent(Map<ResourceLocation, Skill> map, Skill skill) {
		ResourceLocation rl = new ResourceLocation(EpicFightMod.MODID, skill.getName());
		if (map.containsKey(rl)) {
			throw new IllegalArgumentException("Duplicated skill name " + skill.getName());
		} else {
			map.put(rl, skill);
		}
	}
}