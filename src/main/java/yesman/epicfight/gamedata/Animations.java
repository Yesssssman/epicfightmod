package yesman.epicfight.gamedata;

import net.minecraft.util.Hand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.loading.FMLEnvironment;
import yesman.epicfight.animation.AnimationManager;
import yesman.epicfight.animation.property.Property.AttackAnimationProperty;
import yesman.epicfight.animation.property.Property.AttackPhaseProperty;
import yesman.epicfight.animation.property.Property.StaticAnimationProperty;
import yesman.epicfight.animation.types.ActionAnimation;
import yesman.epicfight.animation.types.AimAnimation;
import yesman.epicfight.animation.types.AirSlashAnimation;
import yesman.epicfight.animation.types.AttackAnimation;
import yesman.epicfight.animation.types.BasicAttackAnimation;
import yesman.epicfight.animation.types.DashAttackAnimation;
import yesman.epicfight.animation.types.DodgeAnimation;
import yesman.epicfight.animation.types.GuardAnimation;
import yesman.epicfight.animation.types.HitAnimation;
import yesman.epicfight.animation.types.LongHitAnimation;
import yesman.epicfight.animation.types.MirrorAnimation;
import yesman.epicfight.animation.types.MountAttackAnimation;
import yesman.epicfight.animation.types.MovementAnimation;
import yesman.epicfight.animation.types.OffAnimation;
import yesman.epicfight.animation.types.ReboundAnimation;
import yesman.epicfight.animation.types.SpecialAttackAnimation;
import yesman.epicfight.animation.types.StaticAnimation;
import yesman.epicfight.animation.types.AttackAnimation.Phase;
import yesman.epicfight.animation.types.StaticAnimation.SoundKey;
import yesman.epicfight.client.animation.ClientAnimationProperties;
import yesman.epicfight.client.animation.Layer;
import yesman.epicfight.client.animation.PoseModifier;
import yesman.epicfight.client.model.ClientModels;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.model.Model;
import yesman.epicfight.particle.Particles;
import yesman.epicfight.utils.game.AttackResult.Priority;
import yesman.epicfight.utils.game.IExtendedDamageSource.StunType;
import yesman.epicfight.utils.math.ValueCorrector;

public class Animations {
	public static StaticAnimation DUMMY_ANIMATION = new StaticAnimation();
	public static StaticAnimation OFF_ANIMATION_HIGHEST;
	public static StaticAnimation OFF_ANIMATION_MIDDLE;
	public static StaticAnimation BIPED_IDLE;
	public static StaticAnimation BIPED_WALK;
	public static StaticAnimation BIPED_RUN;
	public static StaticAnimation BIPED_SNEAK;
	public static StaticAnimation BIPED_SWIM;
	public static StaticAnimation BIPED_FLOAT;
	public static StaticAnimation BIPED_KNEEL;
	public static StaticAnimation BIPED_FALL;
	public static StaticAnimation BIPED_FLYING;
	public static StaticAnimation BIPED_MOUNT;
	public static StaticAnimation BIPED_JUMP;
	public static StaticAnimation BIPED_DEATH;
	public static StaticAnimation BIPED_DIG;
	public static StaticAnimation BIPED_RUN_SPEAR;
	public static StaticAnimation BIPED_HOLD_GREATSWORD;
	public static StaticAnimation BIPED_HOLD_KATANA_SHEATHING;
	public static StaticAnimation BIPED_IDLE_UNSHEATHING;
	public static StaticAnimation BIPED_WALK_UNSHEATHING;
	public static StaticAnimation BIPED_RUN_UNSHEATHING;
	public static StaticAnimation BIPED_HOLD_LONGSWORD;
	public static StaticAnimation BIPED_KATANA_SCRAP;
	public static StaticAnimation BIPED_IDLE_CROSSBOW;
	public static StaticAnimation BIPED_CLIMBING;
	public static StaticAnimation BIPED_SLEEPING;
	public static StaticAnimation BIPED_BOW_AIM;
	public static StaticAnimation BIPED_BOW_SHOT;
	public static StaticAnimation BIPED_CROSSBOW_AIM;
	public static StaticAnimation BIPED_CROSSBOW_SHOT;
	public static StaticAnimation BIPED_CROSSBOW_RELOAD;
	public static StaticAnimation BIPED_JAVELIN_AIM;
	public static StaticAnimation BIPED_JAVELIN_THROW;
	public static StaticAnimation BIPED_HIT_SHORT;
	public static StaticAnimation BIPED_HIT_LONG;
	public static StaticAnimation BIPED_HIT_ON_MOUNT;
	public static StaticAnimation BIPED_LANDING;
	public static StaticAnimation BIPED_BLOCK;
	public static StaticAnimation BIPED_ROLL_FORWARD;
	public static StaticAnimation BIPED_ROLL_BACKWARD;
	public static StaticAnimation BIPED_STEP_FORWARD;
	public static StaticAnimation BIPED_STEP_BACKWARD;
	public static StaticAnimation BIPED_STEP_LEFT;
	public static StaticAnimation BIPED_STEP_RIGHT;
	public static StaticAnimation BIPED_ARMED_MOB_ATTACK1;
	public static StaticAnimation BIPED_ARMED_MOB_ATTACK2;
	public static StaticAnimation BIPED_MOB_THROW;
	public static StaticAnimation BIPED_IDLE_TACHI;
	public static StaticAnimation CREEPER_IDLE;
	public static StaticAnimation CREEPER_WALK;
	public static StaticAnimation CREEPER_HIT_LONG;
	public static StaticAnimation CREEPER_HIT_SHORT;
	public static StaticAnimation CREEPER_DEATH;
	public static StaticAnimation ENDERMAN_IDLE;
	public static StaticAnimation ENDERMAN_WALK;
	public static StaticAnimation ENDERMAN_DEATH;
	public static StaticAnimation ENDERMAN_HIT_SHORT;
	public static StaticAnimation ENDERMAN_HIT_LONG;
	public static StaticAnimation ENDERMAN_HIT_RAGE;
	public static StaticAnimation ENDERMAN_ATTACK1;
	public static StaticAnimation ENDERMAN_ATTACK2;
	public static StaticAnimation ENDERMAN_RUSH;
	public static StaticAnimation ENDERMAN_RAGE_IDLE;
	public static StaticAnimation ENDERMAN_RAGE_WALK;
	public static StaticAnimation ENDERMAN_GRASP;
	public static StaticAnimation ENDERMAN_TP_KICK1;
	public static StaticAnimation ENDERMAN_TP_KICK2;
	public static StaticAnimation ENDERMAN_KNEE;
	public static StaticAnimation ENDERMAN_KICK1;
	public static StaticAnimation ENDERMAN_KICK2;
	public static StaticAnimation ENDERMAN_KICK_COMBO;
	public static StaticAnimation ENDERMAN_TP_EMERGENCE;
	public static StaticAnimation SPIDER_IDLE;
	public static StaticAnimation SPIDER_CRAWL;
	public static StaticAnimation SPIDER_DEATH;
	public static StaticAnimation SPIDER_HIT;
	public static StaticAnimation SPIDER_ATTACK;
	public static StaticAnimation SPIDER_JUMP_ATTACK;
	public static StaticAnimation GOLEM_IDLE;
	public static StaticAnimation GOLEM_WALK;
	public static StaticAnimation GOLEM_DEATH;
	public static StaticAnimation GOLEM_ATTACK1;
	public static StaticAnimation GOLEM_ATTACK2;
	public static StaticAnimation GOLEM_ATTACK3;
	public static StaticAnimation GOLEM_ATTACK4;
	public static StaticAnimation HOGLIN_IDLE;
	public static StaticAnimation HOGLIN_WALK;
	public static StaticAnimation HOGLIN_DEATH;
	public static StaticAnimation HOGLIN_ATTACK;
	public static StaticAnimation ILLAGER_IDLE;
	public static StaticAnimation ILLAGER_WALK;
	public static StaticAnimation VINDICATOR_IDLE_AGGRESSIVE;
	public static StaticAnimation VINDICATOR_CHASE;
	public static StaticAnimation VINDICATOR_SWING_AXE1;
	public static StaticAnimation VINDICATOR_SWING_AXE2;
	public static StaticAnimation VINDICATOR_SWING_AXE3;
	public static StaticAnimation EVOKER_CAST_SPELL;
	public static StaticAnimation PIGLIN_IDLE;
	public static StaticAnimation PIGLIN_WALK;
	public static StaticAnimation PIGLIN_IDLE_ZOMBIE;
	public static StaticAnimation PIGLIN_WALK_ZOMBIE;
	public static StaticAnimation PIGLIN_CHASE_ZOMBIE;
	public static StaticAnimation PIGLIN_CELEBRATE1;
	public static StaticAnimation PIGLIN_CELEBRATE2;
	public static StaticAnimation PIGLIN_CELEBRATE3;
	public static StaticAnimation PIGLIN_ADMIRE;
	public static StaticAnimation PIGLIN_DEATH;
	public static StaticAnimation RAVAGER_IDLE;
	public static StaticAnimation RAVAGER_WALK;
	public static StaticAnimation RAVAGER_DEATH;
	public static StaticAnimation RAVAGER_STUN;
	public static StaticAnimation RAVAGER_ATTACK1;
	public static StaticAnimation RAVAGER_ATTACK2;
	public static StaticAnimation RAVAGER_ATTACK3;
	public static StaticAnimation VEX_IDLE;
	public static StaticAnimation VEX_FLIPPING;
	public static StaticAnimation VEX_DEATH;
	public static StaticAnimation VEX_HIT;
	public static StaticAnimation VEX_CHARGING;
	public static StaticAnimation WITCH_DRINKING;
	public static StaticAnimation WITHER_SKELETON_IDLE;
	public static StaticAnimation SKELETON_WALK;
	public static StaticAnimation SKELETON_CHASE;
	public static StaticAnimation WITHER_SKELETON_ATTACK1;
	public static StaticAnimation WITHER_SKELETON_ATTACK2;
	public static StaticAnimation WITHER_SKELETON_ATTACK3;
	public static StaticAnimation ZOMBIE_IDLE;
	public static StaticAnimation ZOMBIE_WALK;
	public static StaticAnimation ZOMBIE_CHASE;
	public static StaticAnimation ZOMBIE_ATTACK1;
	public static StaticAnimation ZOMBIE_ATTACK2;
	public static StaticAnimation ZOMBIE_ATTACK3;
	public static StaticAnimation AXE_AUTO1;
	public static StaticAnimation AXE_AUTO2;
	public static StaticAnimation AXE_DASH;
	public static StaticAnimation AXE_AIRSLASH;
	public static StaticAnimation FIST_AUTO_1;
	public static StaticAnimation FIST_AUTO_2;
	public static StaticAnimation FIST_AUTO_3;
	public static StaticAnimation FIST_DASH;
	public static StaticAnimation FIST_AIR_SLASH;
	public static StaticAnimation SPEAR_ONEHAND_AUTO;
	public static StaticAnimation SPEAR_ONEHAND_AIR_SLASH;
	public static StaticAnimation SPEAR_TWOHAND_AUTO_1;
	public static StaticAnimation SPEAR_TWOHAND_AUTO_2;
	public static StaticAnimation SPEAR_TWOHAND_AIR_SLASH;
	public static StaticAnimation SPEAR_DASH;
	public static StaticAnimation SPEAR_MOUNT_ATTACK;
	public static StaticAnimation SPEAR_GUARD;
	public static StaticAnimation SPEAR_GUARD_HIT;
	public static StaticAnimation SWORD_AUTO_1;
	public static StaticAnimation SWORD_AUTO_2;
	public static StaticAnimation SWORD_AUTO_3;
	public static StaticAnimation SWORD_DASH;
	public static StaticAnimation SWORD_AIR_SLASH;
	public static StaticAnimation SWORD_GUARD;
	public static StaticAnimation SWORD_GUARD_HIT;
	public static StaticAnimation SWORD_DUAL_AUTO_1;
	public static StaticAnimation SWORD_DUAL_AUTO_2;
	public static StaticAnimation SWORD_DUAL_AUTO_3;
	public static StaticAnimation SWORD_DUAL_DASH;
	public static StaticAnimation SWORD_DUAL_AIR_SLASH;
	public static StaticAnimation SWORD_DUAL_GUARD;
	public static StaticAnimation SWORD_DUAL_GUARD_HIT;
	public static StaticAnimation LONGSWORD_AUTO_1;
	public static StaticAnimation LONGSWORD_AUTO_2;
	public static StaticAnimation LONGSWORD_AUTO_3;
	public static StaticAnimation LONGSWORD_DASH;
	public static StaticAnimation LONGSWORD_AIR_SLASH;
	public static StaticAnimation LONGSWORD_GUARD;
	public static StaticAnimation LONGSWORD_GUARD_HIT;
	public static StaticAnimation TACHI_DASH;
	public static StaticAnimation TOOL_AUTO_1;
	public static StaticAnimation TOOL_AUTO_2;
	public static StaticAnimation TOOL_DASH;
	public static StaticAnimation KATANA_AUTO_1;
	public static StaticAnimation KATANA_AUTO_2;
	public static StaticAnimation KATANA_AUTO_3;
	public static StaticAnimation KATANA_AIR_SLASH;
	public static StaticAnimation KATANA_SHEATHING_AUTO;
	public static StaticAnimation KATANA_SHEATHING_DASH;
	public static StaticAnimation KATANA_SHEATH_AIR_SLASH;
	public static StaticAnimation KATANA_GUARD;
	public static StaticAnimation KATANA_GUARD_HIT;
	public static StaticAnimation SWORD_MOUNT_ATTACK;
	public static StaticAnimation GREATSWORD_AUTO_1;
	public static StaticAnimation GREATSWORD_AUTO_2;
	public static StaticAnimation GREATSWORD_DASH;
	public static StaticAnimation GREATSWORD_AIR_SLASH;
	public static StaticAnimation GREATSWORD_GUARD;
	public static StaticAnimation GREATSWORD_GUARD_HIT;
	public static StaticAnimation DAGGER_AUTO_1;
	public static StaticAnimation DAGGER_AUTO_2;
	public static StaticAnimation DAGGER_AUTO_3;
	public static StaticAnimation DAGGER_AIR_SLASH;
	public static StaticAnimation DAGGER_DUAL_AUTO_1;
	public static StaticAnimation DAGGER_DUAL_AUTO_2;
	public static StaticAnimation DAGGER_DUAL_AUTO_3;
	public static StaticAnimation DAGGER_DUAL_AUTO_4;
	public static StaticAnimation DAGGER_DUAL_DASH;
	public static StaticAnimation DAGGER_DUAL_AIR_SLASH;
	public static StaticAnimation GUILLOTINE_AXE;
	public static StaticAnimation SWEEPING_EDGE;
	public static StaticAnimation DANCING_EDGE;
	public static StaticAnimation SPEAR_THRUST;
	public static StaticAnimation SPEAR_SLASH;
	public static StaticAnimation GIANT_WHIRLWIND;
	public static StaticAnimation FATAL_DRAW;
	public static StaticAnimation FATAL_DRAW_DASH;
	public static StaticAnimation LETHAL_SLICING;
	public static StaticAnimation LETHAL_SLICING_ONCE;
	public static StaticAnimation LETHAL_SLICING_TWICE;
	public static StaticAnimation RELENTLESS_COMBO;
	public static StaticAnimation EVISCERATE_FIRST;
	public static StaticAnimation EVISCERATE_SECOND;
	public static StaticAnimation BLADE_RUSH_FIRST;
	public static StaticAnimation BLADE_RUSH_SECOND;
	public static StaticAnimation BLADE_RUSH_THIRD;
	public static StaticAnimation BLADE_RUSH_FINISHER;
	
	public static void registerAnimations(AnimationManager.AnimationRegistryEvent event) {
		event.getRegistryMap().put(EpicFightMod.MODID, Animations::build);
	}
	
	private static void build() {
		Models<?> modeldata = FMLEnvironment.dist == Dist.CLIENT ? ClientModels.LOGICAL_CLIENT : Models.LOGICAL_SERVER;
		Model biped = modeldata.biped;
		Model crepper = modeldata.creeper;
		Model enderman = modeldata.enderman;
		Model spider = modeldata.spider;
		Model hoglin = modeldata.hoglin;
		Model iron_golem = modeldata.ironGolem;
		Model piglin = modeldata.piglin;
		Model ravager = modeldata.ravager;
		Model vex = modeldata.vex;
		
		BIPED_IDLE = new StaticAnimation(true, "biped/living/idle", biped);
		BIPED_WALK = new MovementAnimation(true, "biped/living/walk", biped);
		BIPED_FLYING = new StaticAnimation(true, "biped/living/fly", biped);
		BIPED_IDLE_CROSSBOW = new StaticAnimation(true, "biped/living/hold_crossbow", biped);
		BIPED_RUN = new MovementAnimation(true, "biped/living/run", biped);
		BIPED_SNEAK = new MovementAnimation(true, "biped/living/sneak", biped);
		BIPED_SWIM = new MovementAnimation(true, "biped/living/swim", biped);
		BIPED_FLOAT = new StaticAnimation(true, "biped/living/float", biped);
		BIPED_KNEEL = new StaticAnimation(true, "biped/living/kneel", biped);
		BIPED_FALL = new StaticAnimation(false, "biped/living/fall", biped);
		BIPED_MOUNT = new StaticAnimation(true, "biped/living/mount", biped);
		BIPED_DIG = new StaticAnimation(0.11F, true, "biped/living/dig", biped);
		BIPED_BOW_AIM = new AimAnimation(0.16F, false, "biped/combat/bow_aim_mid", "biped/combat/bow_aim_up", "biped/combat/bow_aim_down", "biped/combat/bow_aim_lying", biped);
		BIPED_BOW_SHOT = new ReboundAnimation(0.04F, false, "biped/combat/bow_shot_mid", "biped/combat/bow_shot_up", "biped/combat/bow_shot_down", "biped/combat/bow_shot_lying", biped);
		BIPED_CROSSBOW_AIM = new AimAnimation(false, "biped/combat/crossbow_aim_mid", "biped/combat/crossbow_aim_up", "biped/combat/crossbow_aim_down", "biped/combat/crossbow_aim_lying", biped);
		BIPED_CROSSBOW_SHOT = new ReboundAnimation(false, "biped/combat/crossbow_shot_mid", "biped/combat/crossbow_shot_up", "biped/combat/crossbow_shot_down", "biped/combat/crossbow_shot_lying", biped);
		BIPED_CROSSBOW_RELOAD = new StaticAnimation(false, "biped/combat/crossbow_reload", biped);
		BIPED_JUMP = new StaticAnimation(0.083F, false, "biped/living/jump", biped);
		BIPED_RUN_SPEAR = new MovementAnimation(true, "biped/living/run_helding_weapon", biped);
		BIPED_BLOCK = new MirrorAnimation(0.25F, true, "biped/combat/block", "biped/combat/block_mirror", biped);
		BIPED_HOLD_GREATSWORD = new StaticAnimation(true, "biped/living/hold_greatsword", biped);
		BIPED_HOLD_KATANA_SHEATHING = new StaticAnimation(true, "biped/living/hold_katana_sheath", biped);
		BIPED_IDLE_UNSHEATHING = new StaticAnimation(true, "biped/living/idle_unsheath", biped);
		BIPED_WALK_UNSHEATHING = new MovementAnimation(true, "biped/living/walk_unsheath", biped);
		BIPED_RUN_UNSHEATHING = new MovementAnimation(true, "biped/living/run_unsheath", biped);
		
		BIPED_KATANA_SCRAP = new StaticAnimation(false, "biped/living/katana_scrap", biped)
				.addProperty(StaticAnimationProperty.SOUNDS, new SoundKey[] {SoundKey.create(0.15F, Sounds.SWORD_IN, true)});
		
		BIPED_IDLE_TACHI = new StaticAnimation(true, "biped/living/hold_tachi", biped);
		
		BIPED_HOLD_LONGSWORD = new StaticAnimation(true, "biped/living/hold_longsword", biped);
		BIPED_CLIMBING = new StaticAnimation(0.16F, true, "biped/living/climb", biped);
		BIPED_SLEEPING = new StaticAnimation(0.16F, true, "biped/living/sleep", biped);
		
		BIPED_JAVELIN_AIM = new AimAnimation(false, "biped/combat/javelin_aim_mid", "biped/combat/javelin_aim_up", "biped/combat/javelin_aim_down", "biped/combat/javelin_aim_lying", biped);
		BIPED_JAVELIN_THROW = new ReboundAnimation(0.08F, false, "biped/combat/javelin_throw_mid", "biped/combat/javelin_throw_up", "biped/combat/javelin_throw_down", "biped/combat/javelin_throw_lying", biped);
		
		OFF_ANIMATION_HIGHEST = new OffAnimation();
		OFF_ANIMATION_MIDDLE = new OffAnimation();
		
		ZOMBIE_IDLE = new StaticAnimation(true, "zombie/idle", biped);
		ZOMBIE_WALK = new MovementAnimation(true, "zombie/walk", biped);
		ZOMBIE_CHASE = new MovementAnimation(true, "zombie/chase", biped);
		
		CREEPER_IDLE = new StaticAnimation(true, "creeper/idle", crepper);
		CREEPER_WALK = new MovementAnimation(true, "creeper/walk", crepper);
		
		ENDERMAN_IDLE = new StaticAnimation(true, "enderman/idle", enderman);
		ENDERMAN_WALK = new MovementAnimation(true, "enderman/walk", enderman);
		ENDERMAN_RUSH = new StaticAnimation(false, "enderman/rush", enderman);
		ENDERMAN_RAGE_IDLE = new StaticAnimation(true, "enderman/rage_idle", enderman);
		ENDERMAN_RAGE_WALK = new MovementAnimation(true, "enderman/rage_walk", enderman);
		
		WITHER_SKELETON_IDLE = new StaticAnimation(true, "skeleton/wither_skeleton_idle", biped);
		SKELETON_WALK = new MovementAnimation(true, "skeleton/wither_skeleton_walk", biped);
		SKELETON_CHASE = new MovementAnimation(0.36F, true, "skeleton/wither_skeleton_chase", biped);
		
		SPIDER_IDLE = new StaticAnimation(true, "spider/idle", spider);
		SPIDER_CRAWL = new MovementAnimation(true, "spider/crawl", spider);
		
		GOLEM_IDLE = new StaticAnimation(true, "iron_golem/idle", iron_golem);
		GOLEM_WALK = new MovementAnimation(true, "iron_golem/walk", iron_golem);
		
		HOGLIN_IDLE = new StaticAnimation(true, "hoglin/idle", hoglin);
		HOGLIN_WALK = new MovementAnimation(true, "hoglin/walk", hoglin);
		
		ILLAGER_IDLE = new StaticAnimation(true, "illager/idle", biped);
		ILLAGER_WALK = new MovementAnimation(true, "illager/walk", biped);
		VINDICATOR_IDLE_AGGRESSIVE = new StaticAnimation(true, "illager/idle_aggressive", biped);
		VINDICATOR_CHASE = new MovementAnimation(true, "illager/chase", biped);
		EVOKER_CAST_SPELL = new StaticAnimation(true, "illager/spellcast", biped);
		
		RAVAGER_IDLE = new StaticAnimation(true, "ravager/idle", ravager);
		RAVAGER_WALK = new StaticAnimation(true, "ravager/walk", ravager);
		
		VEX_IDLE = new StaticAnimation(true, "vex/idle", vex);
		VEX_FLIPPING = new StaticAnimation(0.05F, true, "vex/flip", vex);
		
		PIGLIN_IDLE = new StaticAnimation(true, "piglin/idle", piglin);
		PIGLIN_WALK = new MovementAnimation(true, "piglin/walk", piglin);
		PIGLIN_IDLE_ZOMBIE = new StaticAnimation(true, "piglin/idle_zombie", piglin);
		PIGLIN_WALK_ZOMBIE = new MovementAnimation(true, "piglin/walk_zombie", piglin);
		PIGLIN_CHASE_ZOMBIE = new MovementAnimation(true, "piglin/chase_zombie", piglin);
		PIGLIN_CELEBRATE1 = new StaticAnimation(true, "piglin/celebrate1", piglin);
		PIGLIN_CELEBRATE2 = new StaticAnimation(true, "piglin/celebrate2", piglin);
		PIGLIN_CELEBRATE3 = new StaticAnimation(true, "piglin/celebrate3", piglin);
		PIGLIN_ADMIRE = new StaticAnimation(true, "piglin/admire", piglin);
		
		SPEAR_GUARD = new StaticAnimation(true, "biped/skill/guard_spear", biped);
		SWORD_GUARD = new StaticAnimation(true, "biped/skill/guard_sword", biped);
		SWORD_DUAL_GUARD = new StaticAnimation(true, "biped/skill/guard_dualsword", biped);
		GREATSWORD_GUARD = new StaticAnimation(0.25F, true, "biped/skill/guard_greatsword", biped);
		KATANA_GUARD = new StaticAnimation(0.25F, true, "biped/skill/guard_katana", biped);
		LONGSWORD_GUARD = new StaticAnimation(0.25F, true, "biped/skill/guard_longsword", biped);
		
		BIPED_LANDING = new LongHitAnimation(0.08F, "biped/living/landing", biped);
		BIPED_ROLL_FORWARD = new DodgeAnimation(0.1F, false, "biped/skill/roll_forward", 0.6F, 0.8F, biped);
		BIPED_ROLL_BACKWARD = new DodgeAnimation(0.1F, false, "biped/skill/roll_backward", 0.6F, 0.8F, biped);
		BIPED_STEP_FORWARD = new DodgeAnimation(0.05F, false, "biped/skill/step_forward", 0.6F, 1.65F, biped);
		BIPED_STEP_BACKWARD = new DodgeAnimation(0.05F, false, "biped/skill/step_backward", 0.6F, 1.65F, biped);
		BIPED_STEP_LEFT = new DodgeAnimation(0.05F, false, "biped/skill/step_left", 0.6F, 1.65F, biped);
		BIPED_STEP_RIGHT = new DodgeAnimation(0.05F, false, "biped/skill/step_right", 0.6F, 1.65F, biped);
		
		FIST_AUTO_1 = new BasicAttackAnimation(0.08F, 0F, 0.1F, 0.15F, Hand.OFF_HAND, null, "111313", "biped/combat/fist_auto1", biped)
				.addProperty(AttackPhaseProperty.PARTICLE, Particles.HIT_BLUNT);
		FIST_AUTO_2 = new BasicAttackAnimation(0.08F, 0F, 0.1F, 0.15F, null, "111213", "biped/combat/fist_auto2", biped)
				.addProperty(AttackPhaseProperty.PARTICLE, Particles.HIT_BLUNT);
		FIST_AUTO_3 = new BasicAttackAnimation(0.08F, 0F, 0.1F, 0.5F, Hand.OFF_HAND, null, "111313", "biped/combat/fist_auto3", biped)
				.addProperty(AttackPhaseProperty.PARTICLE, Particles.HIT_BLUNT);
		FIST_DASH = new DashAttackAnimation(0.06F, 0.05F, 0.15F, 0.3F, 0.7F, null, "213", "biped/combat/fist_dash", biped)
				.addProperty(AttackPhaseProperty.PARTICLE, Particles.HIT_BLUNT)
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.0F);
		SWORD_AUTO_1 = new BasicAttackAnimation(0.13F, 0.0F, 0.11F, 0.3F, null, "111213", "biped/combat/sword_auto1", biped);
		SWORD_AUTO_2 = new BasicAttackAnimation(0.13F, 0.0F, 0.11F, 0.3F, null, "111213", "biped/combat/sword_auto2", biped);
		SWORD_AUTO_3 = new BasicAttackAnimation(0.13F, 0.0F, 0.11F, 0.6F, null, "111213", "biped/combat/sword_auto3", biped);
		SWORD_DASH = new DashAttackAnimation(0.12F, 0.1F, 0.25F, 0.4F, 0.65F, null, "111213", "biped/combat/sword_dash", biped)
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.6F);
		GREATSWORD_AUTO_1 = new BasicAttackAnimation(0.2F, 0.4F, 0.6F, 0.8F, null, "111213", "biped/combat/greatsword_auto1", biped);
		GREATSWORD_AUTO_2 = new BasicAttackAnimation(0.2F, 0.4F, 0.6F, 0.8F, null, "111213", "biped/combat/greatsword_auto2", biped);
		GREATSWORD_DASH = new DashAttackAnimation(0.11F, 0.4F, 0.65F, 0.8F, 1.2F, null, "111213", "biped/combat/greatsword_dash", false, biped)
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true);
		SPEAR_ONEHAND_AUTO = new BasicAttackAnimation(0.16F, 0.1F, 0.2F, 0.45F, null, "111213", "biped/combat/spear_onehand_auto", biped);
		SPEAR_TWOHAND_AUTO_1 = new BasicAttackAnimation(0.25F, 0.05F, 0.15F, 0.45F, null, "111213", "biped/combat/spear_twohand_auto1", biped);
		SPEAR_TWOHAND_AUTO_2 = new BasicAttackAnimation(0.25F, 0.05F, 0.15F, 0.45F, null, "111213", "biped/combat/spear_twohand_auto2", biped);
		SPEAR_DASH = new DashAttackAnimation(0.16F, 0.05F, 0.2F, 0.3F, 0.7F, null, "111213", "biped/combat/spear_dash", biped)
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true);
		TOOL_AUTO_1 = new BasicAttackAnimation(0.13F, 0.05F, 0.15F, 0.3F, null, "111213", String.valueOf(SWORD_AUTO_1.getId()), biped);
		TOOL_AUTO_2 = new BasicAttackAnimation(0.13F, 0.05F, 0.15F, 0.4F, null, "111213", "biped/combat/sword_auto4", biped);
		TOOL_DASH = new DashAttackAnimation(0.16F, 0.08F, 0.15F, 0.25F, 0.58F, null, "111213", "biped/combat/tool_dash", biped)
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.addProperty(AttackPhaseProperty.MAX_STRIKES, ValueCorrector.getAdder(1));
		AXE_DASH = new DashAttackAnimation(0.25F, 0.08F, 0.4F, 0.46F, 0.9F, null, "111213", "biped/combat/axe_dash", biped)
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true);
		SWORD_DUAL_AUTO_1 = new BasicAttackAnimation(0.16F, 0.0F, 0.11F, 0.2F, null, "111213", "biped/combat/sword_dual_auto1", biped);
		SWORD_DUAL_AUTO_2 = new BasicAttackAnimation(0.13F, 0.0F, 0.1F, 0.1F, Hand.OFF_HAND, null, "111313", "biped/combat/sword_dual_auto2", biped);
		SWORD_DUAL_AUTO_3 = new BasicAttackAnimation(0.18F, 0.0F, 0.25F, 0.35F, 0.64F, Colliders.dualSword, "3", "biped/combat/sword_dual_auto3", biped);
		SWORD_DUAL_DASH = new DashAttackAnimation(0.16F, 0.05F, 0.05F, 0.3F, 0.75F, Colliders.dualSwordDash, "", "biped/combat/sword_dual_dash", biped)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.6F)
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true);
		KATANA_AUTO_1 = new BasicAttackAnimation(0.06F, 0.05F, 0.16F, 0.2F, null, "111213", "biped/combat/katana_auto1", biped);
		KATANA_AUTO_2 = new BasicAttackAnimation(0.16F, 0.0F, 0.11F, 0.2F, null, "111213", "biped/combat/katana_auto2", biped);
		KATANA_AUTO_3 = new BasicAttackAnimation(0.06F, 0.1F, 0.21F, 0.59F, null, "111213", "biped/combat/katana_auto3", biped);
		KATANA_SHEATHING_AUTO = new BasicAttackAnimation(0.06F, 0.0F, 0.06F, 0.65F, Colliders.fatal_draw, "", "biped/combat/katana_sheath_auto", biped)
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.addProperty(AttackPhaseProperty.ARMOR_NEGATION, ValueCorrector.getAdder(30.0F))
				.addProperty(AttackPhaseProperty.DAMAGE, ValueCorrector.getMultiplier(1.0F))
				.addProperty(AttackPhaseProperty.MAX_STRIKES, ValueCorrector.getAdder(2))
				.addProperty(AttackPhaseProperty.SWING_SOUND, Sounds.WHOOSH_SHARP);
		KATANA_SHEATHING_DASH = new DashAttackAnimation(0.06F, 0.05F, 0.05F, 0.11F, 0.65F, null, "111213", "biped/combat/katana_sheath_dash", biped)
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.addProperty(AttackPhaseProperty.ARMOR_NEGATION, ValueCorrector.getAdder(30.0F))
				.addProperty(AttackPhaseProperty.DAMAGE, ValueCorrector.getMultiplier(1.0F))
				.addProperty(AttackPhaseProperty.SWING_SOUND, Sounds.WHOOSH_SHARP);
		AXE_AUTO1 = new BasicAttackAnimation(0.16F, 0.05F, 0.16F, 0.7F, null, "111213", "biped/combat/axe_auto1", biped);
		AXE_AUTO2 = new BasicAttackAnimation(0.16F, 0.05F, 0.16F, 0.85F, null, "111213", "biped/combat/axe_auto2", biped);
		LONGSWORD_AUTO_1 = new BasicAttackAnimation(0.1F, 0.2F, 0.3F, 0.45F, null, "111213", "biped/combat/longsword_auto1", biped);
		LONGSWORD_AUTO_2 = new BasicAttackAnimation(0.15F, 0.1F, 0.21F, 0.45F, null, "111213", "biped/combat/longsword_auto2", biped);
		LONGSWORD_AUTO_3 = new BasicAttackAnimation(0.15F, 0.05F, 0.16F, 0.8F, null, "111213", "biped/combat/longsword_auto3", biped);
		LONGSWORD_DASH = new DashAttackAnimation(0.15F, 0.1F, 0.3F, 0.5F, 0.7F, null, "111213", "biped/combat/longsword_dash", biped)
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true);
		TACHI_DASH = new DashAttackAnimation(0.15F, 0.1F, 0.2F, 0.45F, 0.7F, null, "111213", "biped/combat/tachi_dash", false, biped)
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true);
		DAGGER_AUTO_1 = new BasicAttackAnimation(0.08F, 0.05F, 0.15F, 0.2F, null, "111213", "biped/combat/dagger_auto1", biped);
		DAGGER_AUTO_2 = new BasicAttackAnimation(0.08F, 0.0F, 0.1F, 0.2F, null, "111213", "biped/combat/dagger_auto2", biped);
		DAGGER_AUTO_3 = new BasicAttackAnimation(0.08F, 0.15F, 0.26F, 0.5F, null, "111213", "biped/combat/dagger_auto3", biped);
		DAGGER_DUAL_AUTO_1 = new BasicAttackAnimation(0.08F, 0.05F, 0.16F, 0.25F, null, "111213", "biped/combat/dagger_dual_auto1", biped);
		DAGGER_DUAL_AUTO_2 = new BasicAttackAnimation(0.08F, 0.0F, 0.11F, 0.16F, Hand.OFF_HAND, null, "111313", "biped/combat/dagger_dual_auto2", biped);
		DAGGER_DUAL_AUTO_3 = new BasicAttackAnimation(0.08F, 0.0F, 0.11F, 0.2F, null, "111213", "biped/combat/dagger_dual_auto3", biped);
		DAGGER_DUAL_AUTO_4 = new BasicAttackAnimation(0.13F, 0.1F, 0.21F, 0.4F, Colliders.dualDaggerDash, "", "biped/combat/dagger_dual_auto4", biped);
		DAGGER_DUAL_DASH = new DashAttackAnimation(0.1F, 0.1F, 0.25F, 0.3F, 0.65F, Colliders.dualDaggerDash, "", "biped/combat/dagger_dual_dash", biped)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.4F)
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true);
		SWORD_AIR_SLASH = new AirSlashAnimation(0.1F, 0.15F, 0.26F, 0.5F, null, "111213", "biped/combat/sword_airslash", biped);
		SWORD_DUAL_AIR_SLASH = new AirSlashAnimation(0.1F, 0.15F, 0.26F, 0.5F, Colliders.dualSwordAirslash, "3", "biped/combat/sword_dual_airslash", biped);
		KATANA_AIR_SLASH = new AirSlashAnimation(0.1F, 0.05F, 0.16F, 0.3F, null, "111213", "biped/combat/katana_airslash", biped);
		KATANA_SHEATH_AIR_SLASH = new AirSlashAnimation(0.1F, 0.1F, 0.16F, 0.3F, null, "111213", "biped/combat/katana_sheath_airslash", biped)
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.addProperty(AttackPhaseProperty.ARMOR_NEGATION, ValueCorrector.getAdder(30.0F))
				.addProperty(AttackPhaseProperty.DAMAGE, ValueCorrector.getMultiplier(1.5F))
				.addProperty(AttackPhaseProperty.MAX_STRIKES, ValueCorrector.getAdder(2))
				.addProperty(AttackPhaseProperty.SWING_SOUND, Sounds.WHOOSH_SHARP)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.0F);
		SPEAR_ONEHAND_AIR_SLASH = new AirSlashAnimation(0.1F, 0.15F, 0.26F, 0.4F, null, "111213", "biped/combat/spear_onehand_airslash", biped);
		SPEAR_TWOHAND_AIR_SLASH = new AirSlashAnimation(0.1F, 0.25F, 0.36F, 0.6F, null, "111213", "biped/combat/spear_twohand_airslash", biped);
		LONGSWORD_AIR_SLASH = new AirSlashAnimation(0.1F, 0.3F, 0.41F, 0.5F, null, "111213", "biped/combat/longsword_airslash", biped);
		GREATSWORD_AIR_SLASH = new AirSlashAnimation(0.1F, 0.5F, 0.55F, 0.71F, 0.75F, false, null, "111213", "biped/combat/greatsword_airslash", biped);
		FIST_AIR_SLASH = new AirSlashAnimation(0.1F, 0.15F, 0.26F, 0.4F, null, "111213", "biped/combat/fist_airslash", biped)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 4.0F);
		DAGGER_AIR_SLASH = new AirSlashAnimation(0.1F, 0.15F, 0.26F, 0.45F, null, "111213", "biped/combat/dagger_airslash", biped)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.4F);
		DAGGER_DUAL_AIR_SLASH = new AirSlashAnimation(0.1F, 0.15F, 0.26F, 0.4F, Colliders.dualDaggerAirslash, "3", String.valueOf(SWORD_DUAL_AIR_SLASH.getId()), biped)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.0F);
		AXE_AIRSLASH = new AirSlashAnimation(0.1F, 0.3F, 0.4F, 0.65F, null, "111213", "biped/combat/axe_airslash", biped);
		
		SWORD_MOUNT_ATTACK = new MountAttackAnimation(0.16F, 0.1F, 0.2F, 0.25F, 0.7F, null, "111213", "biped/combat/sword_mount_attack", biped);
		SPEAR_MOUNT_ATTACK = new MountAttackAnimation(0.16F, 0.38F, 0.38F, 0.45F, 0.8F, null, "111213", "biped/combat/spear_mount_attack", biped)
				.addProperty(AttackAnimationProperty.DIRECTIONAL, true);
		
		BIPED_ARMED_MOB_ATTACK1 = new AttackAnimation(0.08F, 0.45F, 0.55F, 0.66F, 0.95F, false, null, "111213", "biped/combat/armed_mob_attack1", biped)
				.addProperty(AttackAnimationProperty.DIRECTIONAL, true);
		BIPED_ARMED_MOB_ATTACK2 = new AttackAnimation(0.08F, 0.45F, 0.5F, 0.61F, 0.95F, false, null, "111213", "biped/combat/armed_mob_attack2", biped)
				.addProperty(AttackAnimationProperty.DIRECTIONAL, true);
		BIPED_MOB_THROW = new AttackAnimation(0.11F, 1.0F, 0, 0, 0, false, null, "", "biped/combat/javelin_throw_mid", biped);
		
		SWORD_GUARD_HIT = new GuardAnimation(0.05F, "biped/skill/guard_sword_hit", biped);
		SWORD_DUAL_GUARD_HIT = new GuardAnimation(0.05F, "biped/skill/guard_dualsword_hit", biped);
		LONGSWORD_GUARD_HIT = new GuardAnimation(0.05F, "biped/skill/guard_longsword_hit", biped);
		SPEAR_GUARD_HIT = new GuardAnimation(0.05F, "biped/skill/guard_spear_hit", biped);
		GREATSWORD_GUARD_HIT = new GuardAnimation(0.05F, "biped/skill/guard_greatsword_hit", biped);
		KATANA_GUARD_HIT = new GuardAnimation(0.05F, "biped/skill/guard_katana_hit", biped);
		
		BIPED_HIT_SHORT = new HitAnimation(0.05F, "biped/combat/hit_short", biped);
		BIPED_HIT_LONG = new LongHitAnimation(0.08F, "biped/combat/hit_long", biped);
		BIPED_HIT_ON_MOUNT = new LongHitAnimation(0.08F, "biped/combat/hit_on_mount", biped);
		BIPED_DEATH = new LongHitAnimation(0.16F, "biped/living/death", biped);
		
		CREEPER_HIT_SHORT = new HitAnimation(0.05F, "creeper/hit_short", crepper);
		CREEPER_HIT_LONG = new LongHitAnimation(0.08F, "creeper/hit_long", crepper);
		CREEPER_DEATH = new LongHitAnimation(0.16F, "creeper/death", crepper);
		
		ENDERMAN_HIT_SHORT = new HitAnimation(0.05F, "enderman/hit_short", enderman);
		ENDERMAN_HIT_LONG = new LongHitAnimation(0.08F, "enderman/hit_long", enderman);
		ENDERMAN_HIT_RAGE = new DodgeAnimation(0.16F, 0.0F, false, "enderman/convert_rampage", -1.0F, -1.0F, enderman);
		ENDERMAN_TP_KICK1 = new AttackAnimation(0.06F, 0.15F, 0.3F, 0.4F, 1.0F, false, Colliders.endermanStick, "11", "enderman/tp_kick1", enderman);
		ENDERMAN_TP_KICK2 = new AttackAnimation(0.16F, 0.15F, 0.25F, 0.45F, 1.0F, false, Colliders.endermanStick, "11", "enderman/tp_kick2", enderman);
		ENDERMAN_KICK1 = new AttackAnimation(0.16F, 0.66F, 0.7F, 0.81F, 1.6F, false, Colliders.endermanStick, "12", "enderman/rush_kick", enderman)
				.addProperty(AttackPhaseProperty.IMPACT, ValueCorrector.getSetter(4.0F));
		ENDERMAN_KICK2 = new AttackAnimation(0.16F, 0.8F, 0.8F, 0.9F, 1.3F, false, Colliders.endermanStick, "11", "enderman/flying_kick", enderman);
		ENDERMAN_KNEE = new AttackAnimation(0.16F, 0.25F, 0.25F, 0.31F, 1.0F, false, Colliders.fist, "11", "enderman/knee", enderman)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG);
		ENDERMAN_KICK_COMBO = new AttackAnimation(0.1F, false, "enderman/kick_double", enderman,
				new Phase(0.15F, 0.15F, 0.21F, 0.46F, "11", Colliders.endermanStick),
				new Phase(0.75F, 0.75F, 0.81F, 1.6F, "12", Colliders.endermanStick));
		ENDERMAN_GRASP = new AttackAnimation(0.06F, 0.5F, 0.45F, 1.0F, 1.0F, false, Colliders.endermanStick, "111213", "enderman/grasp", enderman)
				.addProperty(AttackAnimationProperty.DIRECTIONAL, true);
		ENDERMAN_DEATH = new LongHitAnimation(0.16F, "enderman/death", enderman);
		ENDERMAN_TP_EMERGENCE = new ActionAnimation(0.05F, true, false, "enderman/teleport_emergence", enderman);
		
		SPIDER_ATTACK = new AttackAnimation(0.16F, 0.31F, 0.31F, 0.36F, 0.44F, false, Colliders.spiderRaid, "1", "spider/attack", spider);
		SPIDER_JUMP_ATTACK = new AttackAnimation(0.16F, 0.25F, 0.25F, 0.41F, 0.8F, true, Colliders.spiderRaid, "1", "spider/jump_attack", spider);
		SPIDER_HIT = new HitAnimation(0.08F, "spider/hit", spider);
		SPIDER_DEATH = new LongHitAnimation(0.16F, "spider/death", spider);
		
		GOLEM_ATTACK1 = new AttackAnimation(0.2F, 0.1F, 0.15F, 0.25F, 0.9F, false, Colliders.headbutt, "11", "iron_golem/attack1", iron_golem);
		GOLEM_ATTACK2 = new AttackAnimation(0.34F, 0.1F, 0.4F, 0.6F, 1.15F, false, Colliders.golemSmashDown, "11121", "iron_golem/attack2", iron_golem);
		GOLEM_ATTACK3 = new AttackAnimation(0.16F, 0.4F, 0.4F, 0.5F, 0.9F, false, Colliders.golemSwingArm, "11131", "iron_golem/attack3", iron_golem)
				.addProperty(AttackAnimationProperty.DIRECTIONAL, true);
		GOLEM_ATTACK4 = new AttackAnimation(0.16F, 0.4F, 0.4F, 0.5F, 0.9F, false, Colliders.golemSwingArm, "11121", "iron_golem/attack4", iron_golem)
				.addProperty(AttackAnimationProperty.DIRECTIONAL, true);
		GOLEM_DEATH = new LongHitAnimation(0.11F, "iron_golem/death", iron_golem);
		
		VINDICATOR_SWING_AXE1 = new AttackAnimation(0.2F, 0.25F, 0.35F, 0.46F, 0.71F, false, Colliders.tools, "111213", "illager/swing_axe1", biped);
		VINDICATOR_SWING_AXE2 = new AttackAnimation(0.2F, 0.25F, 0.3F, 0.41F, 0.71F, false, Colliders.tools, "111213", "illager/swing_axe2", biped);
		VINDICATOR_SWING_AXE3 = new AttackAnimation(0.05F, 0.50F, 0.62F, 0.75F, 1F, true, Colliders.tools, "111213", "illager/swing_axe3", biped);
		
		PIGLIN_DEATH = new LongHitAnimation(0.16F, "piglin/death", piglin);
		
		HOGLIN_DEATH = new LongHitAnimation(0.16F, "hoglin/death", hoglin);
		HOGLIN_ATTACK = new AttackAnimation(0.16F, 0.25F, 0.25F, 0.45F, 1.0F, false, Colliders.golemSwingArm, "1", "hoglin/attack", hoglin);
		
		RAVAGER_DEATH = new LongHitAnimation(0.11F, "ravager/death", ravager);
		RAVAGER_STUN = new ActionAnimation(0.16F, true, false, "ravager/groggy", ravager);
		RAVAGER_ATTACK1 = new AttackAnimation(0.16F, 0.2F, 0.4F, 0.5F, 0.55F, false, Colliders.headbutt_ravager, "131", "ravager/attack1", ravager);
		RAVAGER_ATTACK2 = new AttackAnimation(0.16F, 0.2F, 0.4F, 0.5F, 1.3F, false, Colliders.headbutt_ravager, "131", "ravager/attack2", ravager);
		RAVAGER_ATTACK3 = new AttackAnimation(0.16F, 0.0F, 1.1F, 1.16F, 1.6F, false, Colliders.headbutt_ravager, "131", "ravager/attack3", ravager);
		
		VEX_HIT = new HitAnimation(0.048F, "vex/hit", vex);
		VEX_DEATH = new LongHitAnimation(0.16F, "vex/death", vex);
		VEX_CHARGING = new AttackAnimation(0.11F, 0.3F, 0.3F, 0.5F, 1.2F, true, Colliders.sword, "", "vex/charge", vex);
		
		WITCH_DRINKING = new StaticAnimation(0.16F, false, "witch/drink", biped);
		
		WITHER_SKELETON_ATTACK1 = new AttackAnimation(0.16F, 0.2F, 0.3F, 0.41F, 0.7F, false, Colliders.sword, "111213", "skeleton/wither_skeleton_attack1", biped)
				.addProperty(AttackAnimationProperty.DIRECTIONAL, true);
		WITHER_SKELETON_ATTACK2 = new AttackAnimation(0.16F, 0.25F, 0.25F, 0.36F, 0.7F, false, Colliders.sword, "111213", "skeleton/wither_skeleton_attack2", biped)
				.addProperty(AttackAnimationProperty.DIRECTIONAL, true);
		WITHER_SKELETON_ATTACK3 = new AttackAnimation(0.16F, 0.25F, 0.25F, 0.36F, 0.7F, false, Colliders.sword, "111213", "skeleton/wither_skeleton_attack3", biped)
				.addProperty(AttackAnimationProperty.DIRECTIONAL, true);
		
		ZOMBIE_ATTACK1 = new AttackAnimation(0.1F, 0.3F, 0.35F, 0.55F, 0.85F, false, Colliders.fist, "111213", "zombie/attack1", biped)
				.addProperty(AttackAnimationProperty.DIRECTIONAL, true);
		ZOMBIE_ATTACK2 = new AttackAnimation(0.1F, 0.3F, 0.33F, 0.55F, 0.85F, false, Colliders.fist, "111313", "zombie/attack2", biped)
				.addProperty(AttackAnimationProperty.DIRECTIONAL, true);
		ZOMBIE_ATTACK3 = new AttackAnimation(0.1F, 0.5F, 0.5F, 0.6F, 1.15F, false, Colliders.headbutt, "113", "zombie/attack3", biped);
		
		SWEEPING_EDGE = new SpecialAttackAnimation(0.11F, 0.1F, 0.45F, 0.6F, 0.85F, false, null, "111213", "biped/skill/sweeping_edge", biped)
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.addProperty(AttackAnimationProperty.DIRECTIONAL, true)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.6F)
				.addProperty(AttackAnimationProperty.COLLIDER_ADDER, 1);
		
		DANCING_EDGE = new SpecialAttackAnimation(0.25F, true, "biped/skill/dancing_edge", biped,
				new Phase(0.2F, 0.2F, 0.31F, 0.31F, "111213", null), new Phase(0.5F, 0.5F, 0.61F, 0.61F, Hand.OFF_HAND, "111313", null),
				new Phase(0.75F, 0.75F, 0.85F, 1.15F, "111213", null))
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.6F);
		
		GUILLOTINE_AXE = new SpecialAttackAnimation(0.08F, 0.2F, 0.5F, 0.65F, 1.0F, true, null, "111213", "biped/skill/axe_special", biped)
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.0F);
		
		SPEAR_THRUST = new SpecialAttackAnimation(0.11F, false, "biped/skill/spear_thrust", biped,
				new Phase(0.3F, 0.3F, 0.36F, 0.51F, "111213", null), new Phase(0.51F, 0.51F, 0.56F, 0.73F, "111213", null),
				new Phase(0.73F, 0.73F, 0.78F, 1.05F, "111213", null))
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.addProperty(AttackAnimationProperty.DIRECTIONAL, true)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.2F);
		
		SPEAR_SLASH = new SpecialAttackAnimation(0.1F, false, "biped/skill/spear_slash", biped,
				new Phase(0.2F, 0.2F, 0.41F, 0.5F, "111213", null), new Phase(0.5F, 0.75F, 0.95F, 1.25F, "111213", null))
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.addProperty(AttackAnimationProperty.DIRECTIONAL, true)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.2F);
		
		GIANT_WHIRLWIND = new SpecialAttackAnimation(0.41F, false, "biped/skill/giant_whirlwind", biped,
				new Phase(0.3F, 0.35F, 0.55F, 0.85F, "111213", null), new Phase(0.95F, 1.05F, 1.2F, 1.35F, "111213", null),
				new Phase(1.65F, 1.75F, 1.95F, 2.5F, "111213", null))
				.addProperty(AttackAnimationProperty.DIRECTIONAL, true)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F);
		
		FATAL_DRAW = new SpecialAttackAnimation(0.15F, 0.0F, 0.7F, 0.81F, 1.0F, false, Colliders.fatal_draw, "", "biped/skill/fatal_draw", biped)
				.addProperty(AttackPhaseProperty.SWING_SOUND, Sounds.WHOOSH_SHARP)
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.0F)
				.addProperty(StaticAnimationProperty.SOUNDS, new SoundKey[] {SoundKey.create(0.05F, Sounds.SWORD_IN, false)});
		
		FATAL_DRAW_DASH = new SpecialAttackAnimation(0.15F, 0.43F, 0.85F, 0.91F, 1.4F, false, Colliders.fatal_draw_dash, "", "biped/skill/fatal_draw_dash", biped)
				.addProperty(AttackPhaseProperty.SWING_SOUND, Sounds.WHOOSH_SHARP)
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.0F)
				.addProperty(StaticAnimationProperty.SOUNDS, new SoundKey[] {SoundKey.create(0.05F, Sounds.SWORD_IN, false)});
		
		LETHAL_SLICING = new SpecialAttackAnimation(0.15F, 0.0F, 0.0F, 0.11F, 0.38F, false, Colliders.narrowFront, "", "biped/skill/lethal_slicing_start", biped)
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.6F);
		
		LETHAL_SLICING_ONCE = new SpecialAttackAnimation(0.016F, 0.0F, 0.0F, 0.1F, 0.6F, false, Colliders.fatal_draw, "", "biped/skill/lethal_slicing_once", biped)
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.6F);
		
		LETHAL_SLICING_TWICE = new SpecialAttackAnimation(0.016F, false, "biped/skill/lethal_slicing_twice", biped,
				new Phase(0.0F, 0.0F, 0.1F, 0.15F, "", Colliders.fatal_draw), new Phase(0.15F, 0.15F, 0.25F, 0.6F, "", Colliders.fatal_draw))
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.6F);
		
		RELENTLESS_COMBO = new SpecialAttackAnimation(0.05F, false, "biped/skill/relentless_combo", biped,
				new Phase(0.016F, 0.016F, 0.066F, 0.133F, Hand.OFF_HAND, "", Colliders.narrowFront), new Phase(0.133F, 0.133F, 0.183F, 0.25F, "", Colliders.narrowFront),
				new Phase(0.25F, 0.25F, 0.3F, 0.366F, Hand.OFF_HAND, "", Colliders.narrowFront), new Phase(0.366F, 0.366F, 0.416F, 0.483F, "", Colliders.narrowFront),
				new Phase(0.483F, 0.483F, 0.533F, 0.6F, Hand.OFF_HAND, "", Colliders.narrowFront), new Phase(0.6F, 0.6F, 0.65F, 0.716F, "", Colliders.narrowFront),
				new Phase(0.716F, 0.716F, 0.766F, 0.833F, Hand.OFF_HAND, "", Colliders.narrowFront), new Phase(0.833F, 0.833F, 0.883F, 1.1F, "", Colliders.narrowFront))
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 4.0F);
		
		EVISCERATE_FIRST = new SpecialAttackAnimation(0.08F, 0.05F, 0.05F, 0.15F, 0.45F, true, null, "111213", "biped/skill/eviscerate_first", biped)
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.4F);
		
		EVISCERATE_SECOND = new SpecialAttackAnimation(0.15F, 0.0F, 0.0F, 0.0F, 0.4F, true, null, "111213", "biped/skill/eviscerate_second", biped)
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.addProperty(AttackPhaseProperty.HIT_SOUND, Sounds.EVISCERATE)
				.addProperty(AttackPhaseProperty.PARTICLE, Particles.EVISCERATE_SKILL)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.4F);
		
		BLADE_RUSH_FIRST = new SpecialAttackAnimation(0.1F, 0.0F, 0.0F, 0.06F, 0.3F, true, Colliders.bladeRush, "", "biped/skill/blade_rush_first", biped)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				.addProperty(AttackPhaseProperty.TARGET_PRIORITY, Priority.TARGET)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.0F);
		BLADE_RUSH_SECOND = new SpecialAttackAnimation(0.1F, 0.0F, 0.0F, 0.06F, 0.3F, true, Colliders.bladeRush, "", "biped/skill/blade_rush_second", biped)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				.addProperty(AttackPhaseProperty.TARGET_PRIORITY, Priority.TARGET)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.0F);
		BLADE_RUSH_THIRD = new SpecialAttackAnimation(0.1F, 0.0F, 0.0F, 0.06F, 0.3F, true, Colliders.bladeRush, "", "biped/skill/blade_rush_third", biped)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				.addProperty(AttackPhaseProperty.TARGET_PRIORITY, Priority.TARGET)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.0F);
		BLADE_RUSH_FINISHER = new SpecialAttackAnimation(0.15F, 0.0F, 0.1F, 0.16F, 0.65F, true, Colliders.bladeRush, "", "biped/skill/blade_rush_finisher", biped)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				.addProperty(AttackPhaseProperty.TARGET_PRIORITY, Priority.TARGET)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.0F);
		
		AttackCombos.setAttackCombos();
	}
	
	@OnlyIn(Dist.CLIENT)
	public static void buildClient() {
		BIPED_IDLE_UNSHEATHING
				.addProperty(ClientAnimationProperties.POSE_MODIFIER, PoseModifier.builder().setDefaultData(PoseModifier.BIPED_ARMS).create())
				.addProperty(ClientAnimationProperties.PRIORITY, Layer.Priority.MIDDLE);
		BIPED_WALK_UNSHEATHING
				.addProperty(ClientAnimationProperties.POSE_MODIFIER, PoseModifier.builder().setDefaultData(PoseModifier.BIPED_ARMS).create())
				.addProperty(ClientAnimationProperties.PRIORITY, Layer.Priority.MIDDLE);
		BIPED_RUN_UNSHEATHING
				.addProperty(ClientAnimationProperties.POSE_MODIFIER, PoseModifier.builder().setDefaultData(PoseModifier.BIPED_ARMS).create())
				.addProperty(ClientAnimationProperties.PRIORITY, Layer.Priority.MIDDLE);
		BIPED_RUN_SPEAR
				.addProperty(ClientAnimationProperties.POSE_MODIFIER, PoseModifier.builder().setDefaultData(PoseModifier.BIPED_UPPER_JOINTS_ROOT).create())
				.addProperty(ClientAnimationProperties.PRIORITY, Layer.Priority.MIDDLE);
		
		OFF_ANIMATION_HIGHEST.addProperty(ClientAnimationProperties.PRIORITY, Layer.Priority.HIGHEST);
		OFF_ANIMATION_MIDDLE.addProperty(ClientAnimationProperties.PRIORITY, Layer.Priority.MIDDLE);
		
		BIPED_LANDING
				.addProperty(ClientAnimationProperties.POSE_MODIFIER, PoseModifier.builder().setDefaultData(PoseModifier.BIPED_LOWER_JOINTS_ROOT).create())
				.addProperty(ClientAnimationProperties.PRIORITY, Layer.Priority.HIGHEST);
		
		BIPED_MOB_THROW
				.addProperty(ClientAnimationProperties.PRIORITY, Layer.Priority.HIGHEST);
	}
}