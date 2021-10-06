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
import yesman.epicfight.skill.*;
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
		return values.get(LAST_PICK).getSkillName();
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
	public static Skill TECHINICIAN;
	
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
		BASIC_ATTACK = registerSkill(new BasicAttack(), false);
		AIR_ATTACK = registerSkill(new AirAttack(), false);
		
		ROLL = registerSkill(new DodgeSkill(4.0F, "roll", Animations.BIPED_ROLL_FORWARD, Animations.BIPED_ROLL_BACKWARD), true);
		STEP = registerSkill(new StepSkill(3.0F, "step", Animations.BIPED_STEP_FORWARD, Animations.BIPED_STEP_BACKWARD,
				Animations.BIPED_STEP_LEFT, Animations.BIPED_STEP_RIGHT), true);
		
		GUARD = registerSkill(new GuardSkill("guard"), true);
		ACTIVE_GUARD = registerSkill(new ActiveGuardSkill(), true);
		ENERGIZING_GUARD = registerSkill(new EnergizingGuardSkill(), true);
		
		BERSERKER = registerSkill(new BerserkerSkill(), true);
		STAMINA_PILLAGER = registerSkill(new StaminaPillagerSkill(), true);
		SWORD_MASTER = registerSkill(new SwordmasterSkill(), true);
		TECHINICIAN = registerSkill(new TechnicianSkill(), true);
		
		SWEEPING_EDGE = registerSkill(new SimpleSpecialAttackSkill(30.0F, "sweeping_edge", Animations.SWEEPING_EDGE)
				.newPropertyLine()
				.addProperty(AttackPhaseProperty.MAX_STRIKES, ValueCorrector.getAdder(1))
				.addProperty(AttackPhaseProperty.DAMAGE, ValueCorrector.getMultiplier(1.0F))
				.addProperty(AttackPhaseProperty.ARMOR_NEGATION, ValueCorrector.getAdder(20.0F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
				.registerPropertiesToAnimation(), false);
		
		DANCING_EDGE = registerSkill(new SimpleSpecialAttackSkill(30.0F, "dancing_edge", Animations.DANCING_EDGE)
				.newPropertyLine()
				.addProperty(AttackPhaseProperty.MAX_STRIKES, ValueCorrector.getAdder(1))
				.addProperty(AttackPhaseProperty.IMPACT, ValueCorrector.getAdder(0.5F))
				.registerPropertiesToAnimation(), false);
		
		GUILLOTINE_AXE = registerSkill(new SimpleSpecialAttackSkill(20.0F, "guillotine_axe", Animations.GUILLOTINE_AXE)
				.newPropertyLine()
				.addProperty(AttackPhaseProperty.MAX_STRIKES, ValueCorrector.getSetter(1))
				.addProperty(AttackPhaseProperty.DAMAGE, ValueCorrector.getMultiplier(1.5F))
				.addProperty(AttackPhaseProperty.ARMOR_NEGATION, ValueCorrector.getAdder(20.0F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
				.registerPropertiesToAnimation(), false);
		
		SLAUGHTER_STANCE = registerSkill(new SimpleSpecialAttackSkill(40.0F, "slaughter_stance", Animations.SPEAR_SLASH)
				.newPropertyLine()
				.addProperty(AttackPhaseProperty.MAX_STRIKES, ValueCorrector.getAdder(4))
				.addProperty(AttackPhaseProperty.DAMAGE, ValueCorrector.getMultiplier(0.25F))
				.registerPropertiesToAnimation(), false);
		
		HEARTPIERCER = registerSkill(new SimpleSpecialAttackSkill(40.0F, "heartpiercer", Animations.SPEAR_THRUST)
				.newPropertyLine()
				.addProperty(AttackPhaseProperty.ARMOR_NEGATION, ValueCorrector.getAdder(10.0F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.registerPropertiesToAnimation(), false);
		
		GIANT_WHIRLWIND = registerSkill(new SimpleSpecialAttackSkill(60.0F, "giant_whirlwind", Animations.GIANT_WHIRLWIND)
				.newPropertyLine(), false);
		
		FATAL_DRAW = registerSkill(new FatalDrawSkill("fatal_draw")
				.newPropertyLine()
				.addProperty(AttackPhaseProperty.DAMAGE, ValueCorrector.getMultiplier(1.0F))
				.addProperty(AttackPhaseProperty.ARMOR_NEGATION, ValueCorrector.getAdder(50.0F))
				.addProperty(AttackPhaseProperty.MAX_STRIKES, ValueCorrector.getAdder(6))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.registerPropertiesToAnimation(), false);
		
		KATANA_PASSIVE = registerSkill(new KatanaPassive(), false);
		
		LETHAL_SLICING = registerSkill(new LethalSlicingSkill(35.0F, "lethal_slicing")
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
				.registerPropertiesToAnimation(), false);
		
		RELENTLESS_COMBO = registerSkill(new SimpleSpecialAttackSkill(20.0F, "relentless_combo", Animations.RELENTLESS_COMBO)
				.newPropertyLine()
				.addProperty(AttackPhaseProperty.MAX_STRIKES, ValueCorrector.getSetter(1))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.PARTICLE, Particles.HIT_BLUNT)
				.registerPropertiesToAnimation(), false);
		
		LIECHTENAUER = registerSkill(new LiechtenauerSkill("liechtenauer"), false);
		
		EVISCERATE = registerSkill(new EviscerateSkill(25.0F, "eviscerate")
				.newPropertyLine()
				.addProperty(AttackPhaseProperty.MAX_STRIKES, ValueCorrector.getSetter(1))
				.addProperty(AttackPhaseProperty.IMPACT, ValueCorrector.getSetter(2.0F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.newPropertyLine()
				.addProperty(AttackPhaseProperty.MAX_STRIKES, ValueCorrector.getSetter(1))
				.addProperty(AttackPhaseProperty.EXTRA_DAMAGE, ExtraDamageCalculator.get(ExtraDamageCalculator.PERCENT_OF_TARGET_LOST_HEALTH, 0.5F))
				.addProperty(AttackPhaseProperty.ARMOR_NEGATION, ValueCorrector.getAdder(50.0F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
				.registerPropertiesToAnimation(), false);
		
		BLADE_RUSH = registerSkill(new BladeRushSkill(25.0F, "blade_rush")
				.newPropertyLine()
				.addProperty(AttackPhaseProperty.MAX_STRIKES, ValueCorrector.getSetter(1))
				.newPropertyLine()
				.addProperty(AttackPhaseProperty.DAMAGE, ValueCorrector.getMultiplier(1.5F))
				.addProperty(AttackPhaseProperty.ARMOR_NEGATION, ValueCorrector.getAdder(20.0F))
				.addProperty(AttackPhaseProperty.MAX_STRIKES, ValueCorrector.getSetter(1))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.HOLD)
				.addProperty(AttackPhaseProperty.HIT_SOUND, Sounds.BLADE_RUSH_FINISHER)
				.addProperty(AttackPhaseProperty.PARTICLE, Particles.BLADE_RUSH_SKILL)
				.registerPropertiesToAnimation(), false);
	}
	
	public static Skill registerSkill(Skill skill, boolean creativeTabSkill) {
		registerIfAbsent(SKILLS, skill);
		if (creativeTabSkill) {
			registerIfAbsent(MODIFIABLE_SKILLS, skill);
		}
		
		return skill;
	}
	
	private static void registerIfAbsent(Map<ResourceLocation, Skill> map, Skill skill) {
		ResourceLocation rl = new ResourceLocation(EpicFightMod.MODID, skill.getSkillName());
		if (map.containsKey(rl)) {
			throw new IllegalArgumentException("Duplicated skill name " + skill.getSkillName());
		} else {
			map.put(rl, skill);
		}
	}
}