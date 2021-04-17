package maninthehouse.epicfight.gamedata;

import java.util.HashMap;
import java.util.Map;

import maninthehouse.epicfight.animation.types.AimingAnimation;
import maninthehouse.epicfight.animation.types.AnimationProperty;
import maninthehouse.epicfight.animation.types.DodgingAnimation;
import maninthehouse.epicfight.animation.types.HitAnimation;
import maninthehouse.epicfight.animation.types.ImmovableAnimation;
import maninthehouse.epicfight.animation.types.MirrorAnimation;
import maninthehouse.epicfight.animation.types.MovementAnimation;
import maninthehouse.epicfight.animation.types.ReboundAnimation;
import maninthehouse.epicfight.animation.types.StaticAnimation;
import maninthehouse.epicfight.animation.types.VariableHitAnimation;
import maninthehouse.epicfight.animation.types.attack.AAAnimation;
import maninthehouse.epicfight.animation.types.attack.AADashAnimation;
import maninthehouse.epicfight.animation.types.attack.AttackAnimation;
import maninthehouse.epicfight.animation.types.attack.MountAttackAnimation;
import maninthehouse.epicfight.animation.types.attack.SpecialAttackAnimation;
import maninthehouse.epicfight.animation.types.attack.TargetTraceAnimation;
import maninthehouse.epicfight.animation.types.attack.AttackAnimation.Phase;
import maninthehouse.epicfight.capabilities.entity.mob.MobAttackPatterns;
import maninthehouse.epicfight.client.model.ClientModels;
import maninthehouse.epicfight.model.Armature;
import maninthehouse.epicfight.utils.game.IExtendedDamageSource.StunType;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.relauncher.Side;

public final class Animations {
	public static final Map<Integer, StaticAnimation> animationTable = new HashMap<Integer, StaticAnimation> ();
	public static StaticAnimation DUMMY_ANIMATION = new StaticAnimation();
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
	public static StaticAnimation BIPED_RUN_HELDING_WEAPON;
	public static StaticAnimation BIPED_IDLE_MASSIVE_HELD;
	public static StaticAnimation BIPED_WALK_MASSIVE_HELD;
	public static StaticAnimation BIPED_RUN_MASSIVE_HELD;
	public static StaticAnimation BIPED_JUMP_MASSIVE_HELD;
	public static StaticAnimation BIPED_KNEEL_MASSIVE_HELD;
	public static StaticAnimation BIPED_SNEAK_MASSIVE_HELD;
	public static StaticAnimation BIPED_IDLE_SHEATHING;
	public static StaticAnimation BIPED_WALK_SHEATHING;
	public static StaticAnimation BIPED_RUN_SHEATHING;
	public static StaticAnimation BIPED_JUMP_SHEATHING;
	public static StaticAnimation BIPED_KNEEL_SHEATHING;
	public static StaticAnimation BIPED_SNEAK_SHEATHING;
	public static StaticAnimation BIPED_IDLE_UNSHEATHING;
	public static StaticAnimation BIPED_WALK_UNSHEATHING;
	public static StaticAnimation BIPED_RUN_UNSHEATHING;
	public static StaticAnimation BIPED_KATANA_SCRAP;
	public static StaticAnimation BIPED_IDLE_CROSSBOW;
	public static StaticAnimation BIPED_WALK_CROSSBOW;
	public static StaticAnimation BIPED_BOW_AIM;
	public static StaticAnimation BIPED_BOW_REBOUND;
	public static StaticAnimation BIPED_CROSSBOW_AIM;
	public static StaticAnimation BIPED_CROSSBOW_SHOT;
	public static StaticAnimation BIPED_CROSSBOW_RELOAD;
	public static StaticAnimation BIPED_JAVELIN_AIM;
	public static StaticAnimation BIPED_JAVELIN_REBOUND;
	public static StaticAnimation BIPED_HIT_SHORT;
	public static StaticAnimation BIPED_HIT_LONG;
	public static StaticAnimation BIPED_HIT_ON_MOUNT;
	public static StaticAnimation BIPED_LAND_DAMAGE;
	public static StaticAnimation BIPED_BLOCK;
	public static StaticAnimation BIPED_ROLL_FORWARD;
	public static StaticAnimation BIPED_ROLL_BACKWARD;
	public static StaticAnimation BIPED_ARMED_MOB_ATTACK1;
	public static StaticAnimation BIPED_ARMED_MOB_ATTACK2;
	public static StaticAnimation BIPED_MOB_THROW;
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
	public static StaticAnimation ILLAGER_IDLE;
	public static StaticAnimation ILLAGER_WALK;
	public static StaticAnimation VINDICATOR_IDLE_AGGRESSIVE;
	public static StaticAnimation VINDICATOR_CHASE;
	public static StaticAnimation VINDICATOR_SWING_AXE1;
	public static StaticAnimation VINDICATOR_SWING_AXE2;
	public static StaticAnimation VINDICATOR_SWING_AXE3;
	public static StaticAnimation EVOKER_CAST_SPELL;
	public static StaticAnimation VEX_IDLE;
	public static StaticAnimation VEX_FLIPPING;
	public static StaticAnimation VEX_DEATH;
	public static StaticAnimation VEX_HIT;
	public static StaticAnimation VEX_CHARGING;
	public static StaticAnimation WITCH_DRINKING;
	public static StaticAnimation WITHER_SKELETON_IDLE;
	public static StaticAnimation WITHER_SKELETON_WALK;
	public static StaticAnimation WITHER_SKELETON_CHASE;
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
	public static StaticAnimation FIST_AUTO_1;
	public static StaticAnimation FIST_AUTO_2;
	public static StaticAnimation FIST_AUTO_3;
	public static StaticAnimation FIST_DASH;
	public static StaticAnimation SPEAR_ONEHAND_AUTO;
	public static StaticAnimation SPEAR_TWOHAND_AUTO_1;
	public static StaticAnimation SPEAR_TWOHAND_AUTO_2;
	public static StaticAnimation SPEAR_DASH;
	public static StaticAnimation SPEAR_MOUNT_ATTACK;
	public static StaticAnimation SWORD_AUTO_1;
	public static StaticAnimation SWORD_AUTO_2;
	public static StaticAnimation SWORD_AUTO_3;
	public static StaticAnimation SWORD_DASH;
	public static StaticAnimation SWORD_DUAL_AUTO_1;
	public static StaticAnimation SWORD_DUAL_AUTO_2;
	public static StaticAnimation SWORD_DUAL_AUTO_3;
	public static StaticAnimation SWORD_DUAL_DASH;
	public static StaticAnimation TOOL_AUTO_1;
	public static StaticAnimation TOOL_AUTO_2;
	public static StaticAnimation TOOL_DASH;
	public static StaticAnimation KATANA_AUTO_1;
	public static StaticAnimation KATANA_AUTO_2;
	public static StaticAnimation KATANA_AUTO_3;
	public static StaticAnimation KATANA_SHEATHING_AUTO;
	public static StaticAnimation KATANA_SHEATHING_DASH;
	public static StaticAnimation SWORD_MOUNT_ATTACK;
	public static StaticAnimation GREATSWORD_AUTO_1;
	public static StaticAnimation GREATSWORD_AUTO_2;
	public static StaticAnimation GREATSWORD_DASH;
	public static StaticAnimation GUILLOTINE_AXE;
	public static StaticAnimation SWEEPING_EDGE;
	public static StaticAnimation DANCING_EDGE;
	public static StaticAnimation SPEAR_THRUST;
	public static StaticAnimation SPEAR_SLASH;
	public static StaticAnimation GIANT_WHIRLWIND;
	public static StaticAnimation FATAL_DRAW;
	public static StaticAnimation FATAL_DRAW_DASH;
	
	public static int BASIC_ATTACK_MIN;
	public static int BASIC_ATTACK_MAX;
	
	public static StaticAnimation findAnimationDataById(int id) {
		return animationTable.get(id);
	}

	public static void registerAnimations(Side dist) {
		Models<?> modeldata = dist == Side.CLIENT ? ClientModels.LOGICAL_CLIENT : Models.LOGICAL_SERVER;
		
		Armature biped = modeldata.ENTITY_BIPED.getArmature();
		Armature crepper = modeldata.ENTITY_CREEPER.getArmature();
		Armature enderman = modeldata.ENTITY_ENDERMAN.getArmature();
		Armature spider = modeldata.ENTITY_SPIDER.getArmature();
		Armature iron_golem = modeldata.ENTITY_GOLEM.getArmature();
		Armature vex = modeldata.ENTITY_VEX.getArmature();
		
		BIPED_IDLE = new StaticAnimation(0, true, "biped/living/idle.dae").bindOnlyClient(biped, dist);
		BIPED_WALK = new MovementAnimation(1, true, "biped/living/walk.dae").bindOnlyClient(biped, dist);
		BIPED_FLYING = new StaticAnimation(2, true, "biped/living/fly.dae").bindOnlyClient(biped, dist);
		BIPED_IDLE_CROSSBOW = new StaticAnimation(3, true, "biped/living/idle_crossbow.dae").bindOnlyClient(biped, dist);
		BIPED_WALK_CROSSBOW = new MovementAnimation(4, true, "biped/living/walk_crossbow.dae").bindOnlyClient(biped, dist);
		BIPED_RUN = new MovementAnimation(5, true, "biped/living/run.dae").bindOnlyClient(biped, dist);
		BIPED_SNEAK = new MovementAnimation(7, true, "biped/living/sneak.dae").bindOnlyClient(biped, dist);
		BIPED_SWIM = new MovementAnimation(8, true, "biped/living/swim.dae").bindOnlyClient(biped, dist);
		BIPED_FLOAT = new StaticAnimation(9, true, "biped/living/float.dae").bindOnlyClient(biped, dist);
		BIPED_KNEEL = new StaticAnimation(10, true, "biped/living/kneel.dae").bindOnlyClient(biped, dist);
		BIPED_FALL = new StaticAnimation(11, false, "biped/living/fall.dae").bindOnlyClient(biped, dist);
		BIPED_MOUNT = new StaticAnimation(12, true, "biped/living/mount.dae").bindOnlyClient(biped, dist);
		BIPED_DIG = new StaticAnimation(15, 0.11F, true, "biped/living/dig.dae").bindOnlyClient(biped, dist);
		BIPED_BOW_AIM = new AimingAnimation(16, 0.16F, false, "biped/combat/bow_aim_mid.dae", "biped/combat/bow_aim_up.dae", "biped/combat/bow_aim_down.dae").bindOnlyClient(biped, dist);
		BIPED_BOW_REBOUND = new ReboundAnimation(17, 0.04F, false, "biped/combat/bow_shot_mid.dae", "biped/combat/bow_shot_up.dae", "biped/combat/bow_shot_down.dae").bindOnlyClient(biped, dist);
		BIPED_CROSSBOW_AIM = new AimingAnimation(18, 0.16F, false, "biped/combat/crossbow_aim_mid.dae", "biped/combat/crossbow_aim_up.dae", "biped/combat/crossbow_aim_down.dae").bindOnlyClient(biped, dist);
		BIPED_CROSSBOW_SHOT = new ReboundAnimation(19, 0.16F, false, "biped/combat/crossbow_shot_mid.dae", "biped/combat/crossbow_shot_up.dae", "biped/combat/crossbow_shot_down.dae").bindOnlyClient(biped, dist);
		BIPED_CROSSBOW_RELOAD = new StaticAnimation(20, 0.16F, false, "biped/combat/crossbow_reload.dae").bindOnlyClient(biped, dist);
		BIPED_JUMP = new StaticAnimation(21, 0.083F, false, "biped/living/jump.dae").bindOnlyClient(biped, dist);
		BIPED_RUN_HELDING_WEAPON = new MovementAnimation(22, true, "biped/living/run_helding_weapon.dae").bindOnlyClient(biped, dist);
		BIPED_BLOCK = new MirrorAnimation(23, 0.25F, true, "biped/combat/block.dae", "biped/combat/block_mirror.dae").bindOnlyClient(biped, dist);
		BIPED_IDLE_MASSIVE_HELD = new StaticAnimation(24, true, "biped/living/idle_massiveheld.dae").bindOnlyClient(biped, dist);
		BIPED_WALK_MASSIVE_HELD = new MovementAnimation(25, true, "biped/living/walk_massiveheld.dae").bindOnlyClient(biped, dist);
		BIPED_RUN_MASSIVE_HELD = new MovementAnimation(26, true, "biped/living/run_massiveheld.dae").bindOnlyClient(biped, dist);
		BIPED_IDLE_SHEATHING = new StaticAnimation(27, true, "biped/living/idle_sheath.dae").bindOnlyClient(biped, dist);
		BIPED_WALK_SHEATHING = new MovementAnimation(28, true, "biped/living/walk_sheath.dae").bindOnlyClient(biped, dist);
		BIPED_RUN_SHEATHING = new MovementAnimation(29, true, "biped/living/run_sheath.dae").bindOnlyClient(biped, dist);
		BIPED_IDLE_UNSHEATHING = new StaticAnimation(30, true, "biped/living/idle_unsheath.dae").bindOnlyClient(biped, dist);
		BIPED_WALK_UNSHEATHING = new MovementAnimation(31, true, "biped/living/walk_unsheath.dae").bindOnlyClient(biped, dist);
		BIPED_RUN_UNSHEATHING = new MovementAnimation(32, true, "biped/living/run_unsheath.dae").bindOnlyClient(biped, dist);
		BIPED_KATANA_SCRAP = new StaticAnimation(33, false, "biped/living/katana_scrap.dae")
				.registerSound(0.15F, Sounds.SWORD_IN, true).bindOnlyClient(biped, dist);
		
		BIPED_JUMP_SHEATHING = new StaticAnimation(34, 0.083F, false, "biped/living/jump_sheath.dae").bindOnlyClient(biped, dist);
		BIPED_JUMP_MASSIVE_HELD = new StaticAnimation(35, 0.083F, false, "biped/living/jump_massiveheld.dae").bindOnlyClient(biped, dist);
		BIPED_KNEEL_SHEATHING = new StaticAnimation(36, true, "biped/living/kneel_sheath.dae").bindOnlyClient(biped, dist);
		BIPED_KNEEL_MASSIVE_HELD = new StaticAnimation(37, true, "biped/living/kneel_massiveheld.dae").bindOnlyClient(biped, dist);
		BIPED_SNEAK_SHEATHING = new MovementAnimation(38, true, "biped/living/sneak_sheath.dae").bindOnlyClient(biped, dist);
		BIPED_SNEAK_MASSIVE_HELD = new MovementAnimation(39, true, "biped/living/sneak_massiveheld.dae").bindOnlyClient(biped, dist);
		
		BIPED_JAVELIN_AIM = new AimingAnimation(40, 0.16F, false, "biped/combat/javelin_aim_mid.dae", "biped/combat/javelin_aim_up.dae", "biped/combat/javelin_aim_down.dae").bindOnlyClient(biped, dist);
		BIPED_JAVELIN_REBOUND = new ReboundAnimation(41, 0.08F, false, "biped/combat/javelin_throw_mid.dae", "biped/combat/javelin_throw_up.dae", "biped/combat/javelin_throw_down.dae").bindOnlyClient(biped, dist);
		
		ZOMBIE_IDLE = new StaticAnimation(100, true, "zombie/idle.dae").bindOnlyClient(biped, dist);
		ZOMBIE_WALK = new MovementAnimation(102, true, "zombie/walk.dae").bindOnlyClient(biped, dist);
		ZOMBIE_CHASE = new MovementAnimation(103, true, "zombie/chase.dae").bindOnlyClient(biped, dist);
		
		CREEPER_IDLE = new StaticAnimation(300, 0.16F, true, "creeper/idle.dae").bindOnlyClient(crepper, dist);
		CREEPER_WALK = new MovementAnimation(301, 0.16F, true, "creeper/walk.dae").bindOnlyClient(crepper, dist);
		
		ENDERMAN_IDLE = new StaticAnimation(400, true, "enderman/idle.dae").bindOnlyClient(enderman, dist);
		ENDERMAN_WALK = new MovementAnimation(401, true, "enderman/walk.dae").bindOnlyClient(enderman, dist);
		ENDERMAN_RUSH = new StaticAnimation(403, false, "enderman/rush.dae").bindOnlyClient(enderman, dist);
		ENDERMAN_RAGE_IDLE = new StaticAnimation(404, true, "enderman/rage_idle.dae").bindOnlyClient(enderman, dist);
		ENDERMAN_RAGE_WALK = new MovementAnimation(405, true, "enderman/rage_walk.dae").bindOnlyClient(enderman, dist);
		
		WITHER_SKELETON_IDLE = new StaticAnimation(500, true, "skeleton/wither_skeleton_idle.dae").bindOnlyClient(biped, dist);
		WITHER_SKELETON_WALK = new MovementAnimation(501, true, "skeleton/wither_skeleton_walk.dae").bindOnlyClient(biped, dist);
		WITHER_SKELETON_CHASE = new MovementAnimation(502, 0.36F, true, "skeleton/wither_skeleton_chase.dae").bindOnlyClient(biped, dist);
		
		SPIDER_IDLE = new StaticAnimation(600, true, "spider/idle.dae").bindOnlyClient(spider, dist);
		SPIDER_CRAWL = new MovementAnimation(601, true, "spider/crawl.dae").bindOnlyClient(spider, dist);
		
		GOLEM_IDLE = new StaticAnimation(700, true, "iron_golem/idle.dae").bindOnlyClient(iron_golem, dist);
		GOLEM_WALK = new MovementAnimation(701, true, "iron_golem/walk.dae").bindOnlyClient(iron_golem, dist);
		
		ILLAGER_IDLE = new StaticAnimation(800, 0.16F, true, "illager/idle.dae").bindOnlyClient(biped, dist);
		ILLAGER_WALK = new MovementAnimation(801, 0.16F, true, "illager/walk.dae").bindOnlyClient(biped, dist);
		VINDICATOR_IDLE_AGGRESSIVE = new StaticAnimation(802, true, "illager/idle_aggressive.dae").bindOnlyClient(biped, dist);
		VINDICATOR_CHASE = new MovementAnimation(803, true, "illager/chase.dae").bindOnlyClient(biped, dist);
		EVOKER_CAST_SPELL = new StaticAnimation(804, 0.16F, true, "illager/spellcast.dae").bindOnlyClient(biped, dist);
		
		VEX_IDLE = new StaticAnimation(902, 0.16F, true, "vex/idle.dae").bindOnlyClient(vex, dist);
		VEX_FLIPPING = new StaticAnimation(903, 0.05F, true, "vex/flip.dae").bindOnlyClient(vex, dist);
		
		BIPED_LAND_DAMAGE = new HitAnimation(1997, 0.08F, "biped/living/land_damage.dae").bindFull(biped);
		BIPED_ROLL_FORWARD = new DodgingAnimation(1998, 0.09F, false, "biped/combat/roll_forward.dae", 0.6F, 0.5F).bindFull(biped);
		BIPED_ROLL_BACKWARD = new DodgingAnimation(1999, 0.09F, false, "biped/combat/roll_backward.dae", 0.6F, 0.5F).bindFull(biped);
		
		BASIC_ATTACK_MIN = 2000;
		FIST_AUTO_1 = new AAAnimation(2001, 0.08F, 0F, 0.1F, 0.15F, 4F, null, "111313", "biped/combat/fist_auto1.dae").bindFull(biped);
		FIST_AUTO_2 = new AAAnimation(2002, 0.08F, 0F, 0.1F, 0.15F, 4F, null, "111213", "biped/combat/fist_auto2.dae").bindFull(biped);
		FIST_AUTO_3 = new AAAnimation(2003, 0.08F, 0F, 0.1F, 0.5F, 4F, null, "111313", "biped/combat/fist_auto3.dae").bindFull(biped);
		FIST_DASH = new AADashAnimation(2004, 0.06F, 0.05F, 0.15F, 0.3F, 0.7F, null, "213", "biped/combat/fist_dash.dae")
				.addProperty(AnimationProperty.LOCK_ROTATION, true).bindFull(biped);
		SWORD_AUTO_1 = new AAAnimation(2005, 0.13F, 0.0F, 0.11F, 0.3F, 1.6F, null, "111213", "biped/combat/sword_auto1.dae").bindFull(biped);
		SWORD_AUTO_2 = new AAAnimation(2006, 0.13F, 0.0F, 0.11F, 0.3F, 1.6F, null, "111213", "biped/combat/sword_auto2.dae").bindFull(biped);
		SWORD_AUTO_3 = new AAAnimation(2007, 0.13F, 0.0F, 0.11F, 0.6F, 1.6F, null, "111213", "biped/combat/sword_auto3.dae").bindFull(biped);
		SWORD_DASH = new AADashAnimation(2008, 0.12F, 0.1F, 0.25F, 0.4F, 0.65F, Colliders.swordDash, "111213", "biped/combat/sword_dash.dae")
				.addProperty(AnimationProperty.LOCK_ROTATION, true).bindFull(biped);
		GREATSWORD_AUTO_1 = new AAAnimation(2009, 0.41F, 0.4F, 0.6F, 0.9F, 0.8F, null, "111213", "biped/combat/greatsword_auto1.dae").bindFull(biped);
		GREATSWORD_AUTO_2 = new AAAnimation(2010, 0.2F, 0.55F, 0.75F, 1.05F, 0.8F, null, "111213", "biped/combat/greatsword_auto2.dae").bindFull(biped);
		GREATSWORD_DASH = new AADashAnimation(2011, 0.11F, 0.4F, 0.65F, 0.8F, 1.2F, null, "111213", "biped/combat/greatsword_dash.dae", false)
				.addProperty(AnimationProperty.LOCK_ROTATION, true).bindFull(biped);
		SPEAR_ONEHAND_AUTO = new AAAnimation(2012, 0.16F, 0.1F, 0.2F, 0.45F, 1.2F, null, "111213", "biped/combat/spear_onehand_auto.dae").bindFull(biped);
		SPEAR_TWOHAND_AUTO_1 = new AAAnimation(2013, 0.25F, 0.05F, 0.15F, 0.45F, 1.2F, Colliders.spearSwing, "111213", "biped/combat/spear_twohand_auto1.dae").bindFull(biped);
		SPEAR_TWOHAND_AUTO_2 = new AAAnimation(2014, 0.25F, 0.05F, 0.15F, 0.45F, 1.2F, Colliders.spearSwing, "111213", "biped/combat/spear_twohand_auto2.dae").bindFull(biped);
		SPEAR_DASH = new AADashAnimation(2015, 0.16F, 0.05F, 0.2F, 0.3F, 0.7F, null, "111213", "biped/combat/spear_dash.dae")
				.addProperty(AnimationProperty.LOCK_ROTATION, true).bindFull(biped);
		TOOL_AUTO_1 = new AAAnimation(2016, 0.13F, 0.05F, 0.15F, 0.3F, 1.6F, null, "111213", "biped/combat/sword_auto3.dae").bindFull(biped);
		TOOL_AUTO_2 = new AAAnimation(2017, 0.13F, 0.05F, 0.15F, 0.6F, 1.6F, null, "111213", "biped/combat/sword_auto4.dae").bindFull(biped);
		TOOL_DASH = new AADashAnimation(2018, 0.16F, 0.08F, 0.15F, 0.25F, 0.58F, null, "111213", "biped/combat/tool_dash.dae")
				.addProperty(AnimationProperty.LOCK_ROTATION, true)
				.addProperty(AnimationProperty.HIT_AT_ONCE, 2).bindFull(biped);
		AXE_DASH = new AADashAnimation(2019, 0.25F, 0.08F, 0.4F, 0.46F, 0.9F, null, "111213", "biped/combat/axe_dash.dae")
				.addProperty(AnimationProperty.LOCK_ROTATION, true).bindFull(biped);
		SWORD_DUAL_AUTO_1 = new AAAnimation(2020, 0.16F, 0.0F, 0.11F, 0.2F, 1.6F, null, "111213", "biped/combat/dual_auto1.dae").bindFull(biped);
		SWORD_DUAL_AUTO_2 = new AAAnimation(2021, 0.13F, 0.0F, 0.1F, 0.1F, 1.6F, EnumHand.OFF_HAND, null, "111313", "biped/combat/dual_auto2.dae").bindFull(biped);
		SWORD_DUAL_AUTO_3 = new AAAnimation(2022, 0.18F, 0.0F, 0.25F, 0.35F, 0.65F, 1.6F, Colliders.dualSwordDash, "", "biped/combat/dual_auto3.dae").bindFull(biped);
		SWORD_DUAL_DASH = new AADashAnimation(2023, 0.16F, 0.1F, 0.1F, 0.3F, 0.65F, Colliders.dualSwordDash, "", "biped/combat/dual_dash.dae")
				.addProperty(AnimationProperty.LOCK_ROTATION, true).bindFull(biped);
		KATANA_AUTO_1 = new AAAnimation(2024, 0.06F, 0.05F, 0.15F, 0.3F, 2.0F, null, "111213", "biped/combat/katana_auto1.dae").bindFull(biped);
		KATANA_AUTO_2 = new AAAnimation(2025, 0.16F, 0.0F, 0.1F, 0.2F, 2.0F, null, "111213", "biped/combat/katana_auto2.dae").bindFull(biped);
		KATANA_AUTO_3 = new AAAnimation(2026, 0.06F, 0.1F, 0.2F, 0.5F, 2.0F, null, "111213", "biped/combat/katana_auto3.dae").bindFull(biped);
		KATANA_SHEATHING_AUTO = new AAAnimation(2027, 0.06F, 0.0F, 0.06F, 0.65F, 2.0F, Colliders.fatal_draw, "", "biped/combat/katana_reinforce_auto.dae")
				.addProperty(AnimationProperty.LOCK_ROTATION, true)
				.addProperty(AnimationProperty.ARMOR_NEGATION, 30.0F)
				.addProperty(AnimationProperty.DAMAGE_MULTIPLIER, 1.5F)
				.addProperty(AnimationProperty.SWING_SOUND, Sounds.WHOOSH_SHARP).bindFull(biped);
		KATANA_SHEATHING_DASH = new AAAnimation(2028, 0.06F, 0.05F, 0.11F, 0.65F, 2.0F, null, "111213", "biped/combat/katana_reinforce_dash.dae")
				.addProperty(AnimationProperty.LOCK_ROTATION, true)
				.addProperty(AnimationProperty.ARMOR_NEGATION, 30.0F)
				.addProperty(AnimationProperty.DAMAGE_MULTIPLIER, 1.5F)
				.addProperty(AnimationProperty.SWING_SOUND, Sounds.WHOOSH_SHARP).bindFull(biped);
		AXE_AUTO1 = new AAAnimation(2029, 0.16F, 0.25F, 0.35F, 0.7F, 1.0F, null, "111213", "biped/combat/axe_auto1.dae").bindFull(biped);
		AXE_AUTO2 = new AAAnimation(2030, 0.16F, 0.25F, 0.35F, 0.85F, 1.0F, null, "111213", "biped/combat/axe_auto2.dae").bindFull(biped);
		BASIC_ATTACK_MAX = 2031;
		
		SWORD_MOUNT_ATTACK = new MountAttackAnimation(2099, 0.16F, 0.1F, 0.2F, 0.25F, 0.7F, null, "111213", "biped/combat/sword_mount_attack.dae").bindFull(biped);
		SPEAR_MOUNT_ATTACK = new MountAttackAnimation(2245, 0.16F, 0.38F, 0.38F, 0.45F, 0.8F, null, "111213", "biped/combat/spear_mount_attack.dae")
				.addProperty(AnimationProperty.DIRECTIONAL, true).bindFull(biped);
		
		BIPED_ARMED_MOB_ATTACK1 = new TargetTraceAnimation(2900, 0.08F, 0.45F, 0.55F, 0.65F, 0.95F, false, null, "111213", "biped/combat/armed_mob_attack1.dae")
				.addProperty(AnimationProperty.DIRECTIONAL, true).bindFull(biped);
		BIPED_ARMED_MOB_ATTACK2 = new TargetTraceAnimation(2901, 0.08F, 0.45F, 0.55F, 0.65F, 0.95F, false, null, "111213", "biped/combat/armed_mob_attack2.dae")
				.addProperty(AnimationProperty.DIRECTIONAL, true).bindFull(biped);
		BIPED_MOB_THROW = new AttackAnimation(2902, 0.11F, 1.0F, 0, 0, 0, false, null, "", "biped/combat/javelin_throw_mid.dae").bindFull(biped);
		
		BIPED_HIT_SHORT = new VariableHitAnimation(3000, 0.05F, "biped/combat/hit_short.dae").bindFull(biped);
		BIPED_HIT_LONG = new HitAnimation(3001, 0.08F, "biped/combat/hit_long.dae").bindFull(biped);
		BIPED_HIT_ON_MOUNT = new HitAnimation(3002, 0.08F, "biped/combat/hit_on_mount.dae").bindFull(biped);
		BIPED_DEATH = new HitAnimation(3003, 0.16F, "biped/living/death.dae").bindFull(biped);
		
		CREEPER_HIT_SHORT = new VariableHitAnimation(3400, 0.05F, "creeper/hit_short.dae").bindFull(crepper);
		CREEPER_HIT_LONG = new HitAnimation(3401, 0.08F, "creeper/hit_long.dae").bindFull(crepper);
		CREEPER_DEATH = new HitAnimation(3402, 0.16F, "creeper/death.dae").bindFull(crepper);
		
		ENDERMAN_HIT_SHORT = new VariableHitAnimation(3004, 0.05F, "enderman/hit_short.dae").bindFull(enderman);
		ENDERMAN_HIT_LONG = new HitAnimation(3005, 0.08F, "enderman/hit_long.dae").bindFull(enderman);
		ENDERMAN_HIT_RAGE = new DodgingAnimation(3006, 0.16F, 0.0F, false, "enderman/convert_rampage.dae", -1.0F, -1.0F).bindFull(enderman);
		ENDERMAN_TP_KICK1 = new AttackAnimation(3007, 0.06F, 0.15F, 0.3F, 0.4F, 1.0F, false, Colliders.endermanStick, "11", "enderman/tp_kick1.dae").bindFull(enderman);
		ENDERMAN_TP_KICK2 = new AttackAnimation(3008, 0.16F, 0.15F, 0.25F, 0.45F, 1.0F, false, Colliders.endermanStick, "11", "enderman/tp_kick2.dae").bindFull(enderman);
		ENDERMAN_KICK1 = new TargetTraceAnimation(3009, 0.16F, 0.66F, 0.7F, 0.81F, 1.6F, false, Colliders.endermanStick, "12", "enderman/rush_kick.dae")
				.addProperty(AnimationProperty.IMPACT, 4.0F).bindFull(enderman);
		ENDERMAN_KICK2 = new TargetTraceAnimation(3010, 0.16F, 0.8F, 0.8F, 0.9F, 1.3F, false, Colliders.endermanStick, "11", "enderman/flying_kick.dae")
				.bindFull(enderman);
		ENDERMAN_KNEE = new TargetTraceAnimation(3011, 0.16F, 0.25F, 0.25F, 0.31F, 1.0F, false, Colliders.endermanStick, "12", "enderman/knee.dae")
				.addProperty(AnimationProperty.STUN_TYPE, StunType.LONG).bindFull(enderman);
		ENDERMAN_KICK_COMBO = new TargetTraceAnimation(3012, 0.1F, false, "enderman/kick_double.dae",
				new Phase(0.15F, 0.15F, 0.21F, 0.46F, "11", Colliders.endermanStick),
				new Phase(0.75F, 0.75F, 0.81F, 1.6F, "12", Colliders.endermanStick)).bindFull(enderman);
		ENDERMAN_GRASP = new TargetTraceAnimation(3015, 0.06F, 0.5F, 0.45F, 1.0F, 1.0F, false, Colliders.endermanStick, "111213", "enderman/grasp.dae")
				.addProperty(AnimationProperty.DIRECTIONAL, true).bindFull(enderman);
		ENDERMAN_DEATH = new HitAnimation(3016, 0.16F, "enderman/death.dae").bindFull(enderman);
		ENDERMAN_TP_EMERGENCE = new ImmovableAnimation(3017, 0.05F, "enderman/teleport_emergence.dae").bindFull(enderman);
		
		SPIDER_ATTACK = new AttackAnimation(3100, 0.16F, 0.31F, 0.31F, 0.36F, 0.44F, false, Colliders.spiderRaid, "1", "spider/attack.dae").bindFull(spider);
		SPIDER_JUMP_ATTACK = new TargetTraceAnimation(3101, 0.16F, 0.25F, 0.25F, 0.41F, 0.8F, true, Colliders.spiderRaid, "1", "spider/jump_attack.dae").bindFull(spider);
		SPIDER_HIT = new VariableHitAnimation(3102, 0.08F, "spider/hit.dae").bindFull(spider);
		SPIDER_DEATH = new HitAnimation(3103, 0.16F, "spider/death.dae").bindFull(spider);
		
		GOLEM_ATTACK1 = new AttackAnimation(3200, 0.2F, 0.1F, 0.15F, 0.25F, 0.9F, false, Colliders.headbutt, "11", "iron_golem/attack1.dae").bindFull(iron_golem);
		GOLEM_ATTACK2 = new AttackAnimation(3201, 0.34F, 0.1F, 0.4F, 0.6F, 1.15F, false, Colliders.golemSmashDown, "11121", "iron_golem/attack2.dae").bindFull(iron_golem);
		GOLEM_ATTACK3 = new TargetTraceAnimation(3202, 0.16F, 0.4F, 0.4F, 0.5F, 0.9F, false, Colliders.golemSwingArm, "11131", "iron_golem/attack3.dae")
				.addProperty(AnimationProperty.DIRECTIONAL, true).addProperty(AnimationProperty.DIRECTIONAL, true).bindFull(iron_golem);
		GOLEM_ATTACK4 = new TargetTraceAnimation(3203, 0.16F, 0.4F, 0.4F, 0.5F, 0.9F, false, Colliders.golemSwingArm, "11121", "iron_golem/attack4.dae")
				.addProperty(AnimationProperty.DIRECTIONAL, true).bindFull(iron_golem);
		GOLEM_DEATH = new HitAnimation(3204, 0.11F, "iron_golem/death.dae").bindFull(iron_golem);
		
		VINDICATOR_SWING_AXE1 = new TargetTraceAnimation(3300, 0.2F, 0.25F, 0.35F, 0.46F, 0.71F, false, Colliders.tools, "111213", "illager/swing_axe1.dae").bindFull(biped);
		VINDICATOR_SWING_AXE2 = new TargetTraceAnimation(3301, 0.2F, 0.25F, 0.35F, 0.46F, 0.71F, false, Colliders.tools, "111213", "illager/swing_axe2.dae").bindFull(biped);
		VINDICATOR_SWING_AXE3 = new TargetTraceAnimation(3302, 0.05F, 0.50F, 0.62F, 0.75F, 1F, true, Colliders.tools, "111213", "illager/swing_axe3.dae").bindFull(biped);
		
		VEX_HIT = new VariableHitAnimation(3308, 0.048F, "vex/hit.dae").bindFull(vex);
		VEX_DEATH = new HitAnimation(3309, 0.16F, "vex/death.dae").bindFull(vex);
		VEX_CHARGING = new AttackAnimation(3310, 0.11F, 0.3F, 0.3F, 0.5F, 1.2F, true, Colliders.swordDash, "", "vex/charge.dae").bindFull(vex);
		
		WITCH_DRINKING = new StaticAnimation(3306, 0.16F, false, "witch/drink.dae").bindFull(biped);
		
		WITHER_SKELETON_ATTACK1 = new TargetTraceAnimation(3500, 0.16F, 0.31F, 0.31F, 0.41F, 0.7F, false, Colliders.swordDash, "111213", "skeleton/wither_skeleton_attack1.dae")
				.addProperty(AnimationProperty.DIRECTIONAL, true).bindFull(biped);
		WITHER_SKELETON_ATTACK2 = new TargetTraceAnimation(3501, 0.16F, 0.31F, 0.31F, 0.41F, 0.7F, false, Colliders.sword, "111213", "skeleton/wither_skeleton_attack2.dae")
				.addProperty(AnimationProperty.DIRECTIONAL, true).bindFull(biped);
		WITHER_SKELETON_ATTACK3 = new TargetTraceAnimation(3502, 0.16F, 0.31F, 0.31F, 0.41F, 0.7F, false, Colliders.sword, "111213", "skeleton/wither_skeleton_attack3.dae")
				.addProperty(AnimationProperty.DIRECTIONAL, true).addProperty(AnimationProperty.DIRECTIONAL, true).bindFull(biped);
		
		ZOMBIE_ATTACK1 = new TargetTraceAnimation(4000, 0.1F, 0.3F, 0.35F, 0.55F, 0.85F, false, Colliders.fist, "111213", "zombie/attack1.dae").bindFull(biped);
		ZOMBIE_ATTACK2 = new TargetTraceAnimation(4001, 0.1F, 0.3F, 0.33F, 0.55F, 0.85F, false, Colliders.fist, "111313", "zombie/attack2.dae").bindFull(biped);
		ZOMBIE_ATTACK3 = new TargetTraceAnimation(4002, 0.1F, 0.5F, 0.5F, 0.6F, 1.15F, false, Colliders.headbutt, "113", "zombie/attack3.dae").bindFull(biped);
		
		SWEEPING_EDGE = new SpecialAttackAnimation(4111, 0.11F, 0.1F, 0.5F, 0.6F, 0.9F, false, Colliders.swordSwingFast, "111213", "biped/skill/sweeping_edge.dae")
				.addProperty(AnimationProperty.LOCK_ROTATION, true).addProperty(AnimationProperty.DIRECTIONAL, true).bindFull(biped);
		
		DANCING_EDGE = new SpecialAttackAnimation(4112, 0.25F, true, "biped/skill/dancing_edge.dae",
				new Phase(0.2F, 0.2F, 0.26F, 0.3F, "111213", Colliders.sword), new Phase(0.5F, 0.5F, 0.56F, 0.6F, EnumHand.OFF_HAND, "111313", Colliders.sword),
				new Phase(0.75F, 0.75F, 0.8F, 1.15F, "111213", Colliders.sword)).bindFull(biped);
		
		GUILLOTINE_AXE = new SpecialAttackAnimation(5000, 0.08F, 0.2F, 0.5F, 0.65F, 1.0F, true, null, "111213", "biped/skill/axe_special.dae")
				.addProperty(AnimationProperty.LOCK_ROTATION, true).bindFull(biped);
		
		SPEAR_THRUST = new SpecialAttackAnimation(5001, 0.11F, false, "biped/skill/spear_thrust.dae",
				new Phase(0.3F, 0.3F, 0.36F, 0.51F, "111213", Colliders.spearNarrow), new Phase(0.51F, 0.51F, 0.56F, 0.73F, "111213", Colliders.spearNarrow),
				new Phase(0.73F, 0.73F, 0.78F, 1.05F, "111213", Colliders.spearNarrow))
				.addProperty(AnimationProperty.LOCK_ROTATION, true)
				.addProperty(AnimationProperty.DIRECTIONAL, true).bindFull(biped);
		
		SPEAR_SLASH = new SpecialAttackAnimation(5002, 0.1F, false, "biped/skill/spear_slash.dae",
				new Phase(0.24F, 0.24F, 0.36F, 0.5F, "111213", Colliders.spearSwing), new Phase(0.5F, 0.75F, 0.9F, 1.25F, "111213", Colliders.spearSwing))
				.addProperty(AnimationProperty.LOCK_ROTATION, true)
				.addProperty(AnimationProperty.DIRECTIONAL, true).bindFull(biped);
		
		GIANT_WHIRLWIND = new SpecialAttackAnimation(5003, 0.41F, false, "biped/skill/giant_whirlwind.dae",
				new Phase(0.3F, 0.35F, 0.5F, 0.85F, "111213", Colliders.greatSword), new Phase(0.95F, 1.05F, 1.2F, 1.2F, "111213", Colliders.greatSword),
				new Phase(1.65F, 1.75F, 1.9F, 2.5F, "111213", Colliders.greatSword))
				.addProperty(AnimationProperty.DIRECTIONAL, true).bindFull(biped);
				
		FATAL_DRAW = new SpecialAttackAnimation(5004, 0.15F, 0.0F, 0.7F, 0.8F, 1.0F, false, Colliders.fatal_draw, "", "biped/skill/fatal_draw.dae")
				.addProperty(AnimationProperty.SWING_SOUND, Sounds.WHOOSH_SHARP)
				.addProperty(AnimationProperty.LOCK_ROTATION, true)
				.registerSound(0.05F, Sounds.SWORD_IN, false).bindFull(biped);
		
		FATAL_DRAW_DASH = new SpecialAttackAnimation(5005, 0.15F, 0.43F, 0.85F, 0.91F, 1.4F, false, Colliders.fatal_draw_dash, "", "biped/skill/fatal_draw_dash.dae")
				.addProperty(AnimationProperty.SWING_SOUND, Sounds.WHOOSH_SHARP)
				.addProperty(AnimationProperty.LOCK_ROTATION, true)
				.registerSound(0.05F, Sounds.SWORD_IN, false)
				.bindFull(biped);
		
		MobAttackPatterns.setVariousMobAttackPatterns();
	}
}