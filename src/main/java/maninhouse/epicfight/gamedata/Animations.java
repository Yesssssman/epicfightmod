package maninhouse.epicfight.gamedata;

import java.util.Map;

import com.google.common.collect.Maps;

import maninhouse.epicfight.animation.property.Property.AttackAnimationProperty;
import maninhouse.epicfight.animation.property.Property.AttackPhaseProperty;
import maninhouse.epicfight.animation.property.Property.StaticAnimationProperty;
import maninhouse.epicfight.animation.types.ActionAnimation;
import maninhouse.epicfight.animation.types.AimAnimation;
import maninhouse.epicfight.animation.types.AirSlashAnimation;
import maninhouse.epicfight.animation.types.AttackAnimation;
import maninhouse.epicfight.animation.types.AttackAnimation.Phase;
import maninhouse.epicfight.animation.types.BasicAttackAnimation;
import maninhouse.epicfight.animation.types.DashAttackAnimation;
import maninhouse.epicfight.animation.types.DodgeAnimation;
import maninhouse.epicfight.animation.types.GuardAnimation;
import maninhouse.epicfight.animation.types.HitAnimation;
import maninhouse.epicfight.animation.types.LongHitAnimation;
import maninhouse.epicfight.animation.types.MirrorAnimation;
import maninhouse.epicfight.animation.types.MountAttackAnimation;
import maninhouse.epicfight.animation.types.MovementAnimation;
import maninhouse.epicfight.animation.types.OffAnimation;
import maninhouse.epicfight.animation.types.ReboundAnimation;
import maninhouse.epicfight.animation.types.SpecialAttackAnimation;
import maninhouse.epicfight.animation.types.StaticAnimation;
import maninhouse.epicfight.animation.types.StaticAnimation.SoundKey;
import maninhouse.epicfight.capabilities.entity.mob.MobAttackPatterns;
import maninhouse.epicfight.client.model.ClientModels;
import maninhouse.epicfight.model.Armature;
import maninhouse.epicfight.particle.Particles;
import maninhouse.epicfight.utils.game.AttackResult.Priority;
import maninhouse.epicfight.utils.game.IExtendedDamageSource.StunType;
import maninhouse.epicfight.utils.math.ValueCorrector;
import net.minecraft.util.Hand;
import net.minecraftforge.api.distmarker.Dist;

public final class Animations {
	public static final Map<Integer, StaticAnimation> ANIMATIONS = Maps.<Integer, StaticAnimation>newHashMap();
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
	public static StaticAnimation BIPED_IDLE_GREATSWORD;
	public static StaticAnimation BIPED_IDLE_SHEATHING;
	public static StaticAnimation BIPED_IDLE_SHEATHING_MIX;
	public static StaticAnimation BIPED_MOVE_SHEATHING;
	public static StaticAnimation BIPED_IDLE_UNSHEATHING;
	public static StaticAnimation BIPED_WALK_UNSHEATHING;
	public static StaticAnimation BIPED_RUN_UNSHEATHING;
	public static StaticAnimation BIPED_IDLE_LONGSWORD;
	public static StaticAnimation BIPED_WALK_LONGSWORD;
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
	public static StaticAnimation BIPED_LAND_DAMAGE;
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
	
	public static StaticAnimation findAnimationDataById(int id) {
		return ANIMATIONS.get(id);
	}
	
	public static void registerAnimations(Dist dist) {
		Models<?> modeldata = dist == Dist.CLIENT ? ClientModels.LOGICAL_CLIENT : Models.LOGICAL_SERVER;
		Armature biped = modeldata.ENTITY_BIPED.getArmature();
		Armature crepper = modeldata.ENTITY_CREEPER.getArmature();
		Armature enderman = modeldata.ENTITY_ENDERMAN.getArmature();
		Armature spider = modeldata.ENTITY_SPIDER.getArmature();
		Armature hoglin = modeldata.ENTITY_HOGLIN.getArmature();
		Armature iron_golem = modeldata.ENTITY_GOLEM.getArmature();
		Armature piglin = modeldata.ENTITY_PIGLIN.getArmature();
		Armature ravager = modeldata.ENTITY_RAVAGER.getArmature();
		Armature vex = modeldata.ENTITY_VEX.getArmature();
		
		BIPED_IDLE = new StaticAnimation(0, true, "biped/living/idle.dae").loadAnimation(biped, dist);
		BIPED_WALK = new MovementAnimation(1, true, "biped/living/walk.dae").loadAnimation(biped, dist);
		BIPED_FLYING = new StaticAnimation(2, true, "biped/living/fly.dae").loadAnimation(biped, dist);
		BIPED_IDLE_CROSSBOW = new StaticAnimation(3, true, "biped/living/idle_crossbow.dae").loadAnimation(biped, dist);
		BIPED_RUN = new MovementAnimation(5, true, "biped/living/run.dae").loadAnimation(biped, dist);
		BIPED_SNEAK = new MovementAnimation(6, true, "biped/living/sneak.dae").loadAnimation(biped, dist);
		BIPED_SWIM = new MovementAnimation(7, true, "biped/living/swim.dae").loadAnimation(biped, dist);
		BIPED_FLOAT = new StaticAnimation(8, true, "biped/living/float.dae").loadAnimation(biped, dist);
		BIPED_KNEEL = new StaticAnimation(9, true, "biped/living/kneel.dae").loadAnimation(biped, dist);
		BIPED_FALL = new StaticAnimation(10, false, "biped/living/fall.dae").loadAnimation(biped, dist);
		BIPED_MOUNT = new StaticAnimation(11, true, "biped/living/mount.dae").loadAnimation(biped, dist);
		BIPED_DIG = new StaticAnimation(12, 0.11F, true, "biped/living/dig.dae").loadAnimation(biped, dist);
		BIPED_BOW_AIM = new AimAnimation(13, 0.16F, false, "biped/combat/bow_aim_mid.dae", "biped/combat/bow_aim_up.dae", "biped/combat/bow_aim_down.dae", "biped/combat/bow_aim_lying.dae").loadAnimation(biped, dist);
		BIPED_BOW_SHOT = new ReboundAnimation(14, 0.04F, false, "biped/combat/bow_shot_mid.dae", "biped/combat/bow_shot_up.dae", "biped/combat/bow_shot_down.dae", "biped/combat/bow_shot_lying.dae").loadAnimation(biped, dist);
		BIPED_CROSSBOW_AIM = new AimAnimation(15, false, "biped/combat/crossbow_aim_mid.dae", "biped/combat/crossbow_aim_up.dae", "biped/combat/crossbow_aim_down.dae", "biped/combat/crossbow_aim_lying.dae").loadAnimation(biped, dist);
		BIPED_CROSSBOW_SHOT = new ReboundAnimation(16, false, "biped/combat/crossbow_shot_mid.dae", "biped/combat/crossbow_shot_up.dae", "biped/combat/crossbow_shot_down.dae", "biped/combat/crossbow_shot_lying.dae").loadAnimation(biped, dist);
		BIPED_CROSSBOW_RELOAD = new StaticAnimation(17, false, "biped/combat/crossbow_reload.dae").loadAnimation(biped, dist);
		BIPED_JUMP = new StaticAnimation(18, 0.083F, false, "biped/living/jump.dae").loadAnimation(biped, dist);
		BIPED_RUN_SPEAR = new MovementAnimation(19, true, "biped/living/run_helding_weapon.dae").loadAnimation(biped, dist);
		BIPED_BLOCK = new MirrorAnimation(20, 0.25F, true, "biped/combat/block.dae", "biped/combat/block_mirror.dae").loadAnimation(biped, dist);
		BIPED_IDLE_GREATSWORD = new StaticAnimation(21, true, "biped/living/idle_greatsword.dae").loadAnimation(biped, dist);
		BIPED_IDLE_SHEATHING = new StaticAnimation(24, true, "biped/living/idle_sheath.dae").loadAnimation(biped, dist);
		BIPED_IDLE_SHEATHING_MIX = new StaticAnimation(25, true, null).loadFrom(BIPED_IDLE_SHEATHING).loadAnimation(biped, dist);
		BIPED_MOVE_SHEATHING = new MovementAnimation(26, true, null).loadFrom(BIPED_IDLE_SHEATHING).loadAnimation(biped, dist);
		BIPED_IDLE_UNSHEATHING = new StaticAnimation(27, true, "biped/living/idle_unsheath.dae").loadAnimation(biped, dist);
		BIPED_WALK_UNSHEATHING = new MovementAnimation(28, true, "biped/living/walk_unsheath.dae").loadAnimation(biped, dist);
		BIPED_RUN_UNSHEATHING = new MovementAnimation(29, true, "biped/living/run_unsheath.dae").loadAnimation(biped, dist);
		
		BIPED_KATANA_SCRAP = new StaticAnimation(30, false, "biped/living/katana_scrap.dae")
				.addProperty(StaticAnimationProperty.SOUNDS, new SoundKey[] {SoundKey.create(0.15F, Sounds.SWORD_IN, true)})
				.loadAnimation(biped, dist);
		
		BIPED_IDLE_TACHI = new StaticAnimation(37, true, "biped/living/idle_tachi.dae").loadAnimation(biped, dist);
		
		BIPED_IDLE_LONGSWORD = new StaticAnimation(42, true, "biped/living/idle_longsword.dae").loadAnimation(biped, dist);
		BIPED_WALK_LONGSWORD = new MovementAnimation(43, true, "biped/living/walk_longsword.dae").loadAnimation(biped, dist);
		BIPED_CLIMBING = new StaticAnimation(45, 0.16F, true, "biped/living/climb.dae").loadAnimation(biped, dist);
		BIPED_SLEEPING = new StaticAnimation(46, 0.16F, true, "biped/living/sleep.dae").loadAnimation(biped, dist);
		
		BIPED_JAVELIN_AIM = new AimAnimation(47, false, "biped/combat/javelin_aim_mid.dae", "biped/combat/javelin_aim_up.dae", "biped/combat/javelin_aim_down.dae", "biped/combat/javelin_aim_lying.dae").loadAnimation(biped, dist);
		BIPED_JAVELIN_THROW = new ReboundAnimation(48, 0.08F, false, "biped/combat/javelin_throw_mid.dae", "biped/combat/javelin_throw_up.dae", "biped/combat/javelin_throw_down.dae", "biped/combat/javelin_throw_lying.dae").loadAnimation(biped, dist);
		
		OFF_ANIMATION_HIGHEST = new OffAnimation(50);
		OFF_ANIMATION_MIDDLE = new OffAnimation(51);
		
		ZOMBIE_IDLE = new StaticAnimation(100, true, "zombie/idle.dae").loadAnimation(biped, dist);
		ZOMBIE_WALK = new MovementAnimation(101, true, "zombie/walk.dae").loadAnimation(biped, dist);
		ZOMBIE_CHASE = new MovementAnimation(102, true, "zombie/chase.dae").loadAnimation(biped, dist);
		
		CREEPER_IDLE = new StaticAnimation(300, true, "creeper/idle.dae").loadAnimation(crepper, dist);
		CREEPER_WALK = new MovementAnimation(301, true, "creeper/walk.dae").loadAnimation(crepper, dist);
		
		ENDERMAN_IDLE = new StaticAnimation(400, true, "enderman/idle.dae").loadAnimation(enderman, dist);
		ENDERMAN_WALK = new MovementAnimation(401, true, "enderman/walk.dae").loadAnimation(enderman, dist);
		ENDERMAN_RUSH = new StaticAnimation(403, false, "enderman/rush.dae").loadAnimation(enderman, dist);
		ENDERMAN_RAGE_IDLE = new StaticAnimation(404, true, "enderman/rage_idle.dae").loadAnimation(enderman, dist);
		ENDERMAN_RAGE_WALK = new MovementAnimation(405, true, "enderman/rage_walk.dae").loadAnimation(enderman, dist);
		
		WITHER_SKELETON_IDLE = new StaticAnimation(500, true, "skeleton/wither_skeleton_idle.dae").loadAnimation(biped, dist);
		SKELETON_WALK = new MovementAnimation(501, true, "skeleton/wither_skeleton_walk.dae").loadAnimation(biped, dist);
		SKELETON_CHASE = new MovementAnimation(502, 0.36F, true, "skeleton/wither_skeleton_chase.dae").loadAnimation(biped, dist);
		
		SPIDER_IDLE = new StaticAnimation(600, true, "spider/idle.dae").loadAnimation(spider, dist);
		SPIDER_CRAWL = new MovementAnimation(601, true, "spider/crawl.dae").loadAnimation(spider, dist);
		
		GOLEM_IDLE = new StaticAnimation(700, true, "iron_golem/idle.dae").loadAnimation(iron_golem, dist);
		GOLEM_WALK = new MovementAnimation(701, true, "iron_golem/walk.dae").loadAnimation(iron_golem, dist);
		
		HOGLIN_IDLE = new StaticAnimation(750, true, "hoglin/idle.dae").loadAnimation(hoglin, dist);
		HOGLIN_WALK = new MovementAnimation(751,  true, "hoglin/walk.dae").loadAnimation(hoglin, dist);
		
		ILLAGER_IDLE = new StaticAnimation(800, true, "illager/idle.dae").loadAnimation(biped, dist);
		ILLAGER_WALK = new MovementAnimation(801, true, "illager/walk.dae").loadAnimation(biped, dist);
		VINDICATOR_IDLE_AGGRESSIVE = new StaticAnimation(802, true, "illager/idle_aggressive.dae").loadAnimation(biped, dist);
		VINDICATOR_CHASE = new MovementAnimation(803, true, "illager/chase.dae").loadAnimation(biped, dist);
		EVOKER_CAST_SPELL = new StaticAnimation(804, true, "illager/spellcast.dae").loadAnimation(biped, dist);
		
		RAVAGER_IDLE = new StaticAnimation(900, true, "ravager/idle.dae").loadAnimation(ravager, dist);
		RAVAGER_WALK = new StaticAnimation(901, true, "ravager/walk.dae").loadAnimation(ravager, dist);
		
		VEX_IDLE = new StaticAnimation(902, true, "vex/idle.dae").loadAnimation(vex, dist);
		VEX_FLIPPING = new StaticAnimation(903, 0.05F, true, "vex/flip.dae").loadAnimation(vex, dist);
		
		PIGLIN_IDLE = new StaticAnimation(1000, true, "piglin/idle.dae").loadAnimation(piglin, dist);
		PIGLIN_WALK = new MovementAnimation(1001, true, "piglin/walk.dae").loadAnimation(piglin, dist);
		PIGLIN_IDLE_ZOMBIE = new StaticAnimation(1002, true, "piglin/idle_zombie.dae").loadAnimation(piglin, dist);
		PIGLIN_WALK_ZOMBIE = new MovementAnimation(1003, true, "piglin/walk_zombie.dae").loadAnimation(piglin, dist);
		PIGLIN_CHASE_ZOMBIE = new MovementAnimation(1004, true, "piglin/chase_zombie.dae").loadAnimation(piglin, dist);
		PIGLIN_CELEBRATE1 = new StaticAnimation(1005, true, "piglin/celebrate1.dae").loadAnimation(piglin, dist);
		PIGLIN_CELEBRATE2 = new StaticAnimation(1006, true, "piglin/celebrate2.dae").loadAnimation(piglin, dist);
		PIGLIN_CELEBRATE3 = new StaticAnimation(1007, true, "piglin/celebrate3.dae").loadAnimation(piglin, dist);
		PIGLIN_ADMIRE = new StaticAnimation(1008, true, "piglin/admire.dae").loadAnimation(piglin, dist);
		
		SPEAR_GUARD = new StaticAnimation(1800, true, "biped/skill/guard_spear.dae").loadAnimation(biped, dist);
		SWORD_GUARD = new StaticAnimation(1801, true, "biped/skill/guard_sword.dae").loadAnimation(biped, dist);
		SWORD_DUAL_GUARD = new StaticAnimation(1802, true, "biped/skill/guard_dualsword.dae").loadAnimation(biped, dist);
		GREATSWORD_GUARD = new StaticAnimation(1803, 0.25F, true, "biped/skill/guard_greatsword.dae").loadAnimation(biped, dist);
		KATANA_GUARD = new StaticAnimation(1804, 0.25F, true, "biped/skill/guard_katana.dae").loadAnimation(biped, dist);
		LONGSWORD_GUARD = new StaticAnimation(1805, 0.25F, true, "biped/skill/guard_longsword.dae").loadAnimation(biped, dist);
		
		BIPED_LAND_DAMAGE = new LongHitAnimation(1900, 0.08F, "biped/living/land_damage.dae").loadAnimation(biped, dist);
		BIPED_ROLL_FORWARD = new DodgeAnimation(1901, 0.1F, false, "biped/skill/roll_forward.dae", 0.6F, 0.8F).loadAnimation(biped, dist);
		BIPED_ROLL_BACKWARD = new DodgeAnimation(1902, 0.1F, false, "biped/skill/roll_backward.dae", 0.6F, 0.8F).loadAnimation(biped, dist);
		BIPED_STEP_FORWARD = new DodgeAnimation(1903, 0.05F, false, "biped/skill/step_forward.dae", 0.6F, 1.65F).loadAnimation(biped, dist);
		BIPED_STEP_BACKWARD = new DodgeAnimation(1904, 0.05F, false, "biped/skill/step_backward.dae", 0.6F, 1.65F).loadAnimation(biped, dist);
		BIPED_STEP_LEFT = new DodgeAnimation(1905, 0.05F, false, "biped/skill/step_left.dae", 0.6F, 1.65F).loadAnimation(biped, dist);
		BIPED_STEP_RIGHT = new DodgeAnimation(1906, 0.05F, false, "biped/skill/step_right.dae", 0.6F, 1.65F).loadAnimation(biped, dist);
		
		FIST_AUTO_1 = new BasicAttackAnimation(2001, 0.08F, 0F, 0.1F, 0.15F, Hand.OFF_HAND, null, "111313", "biped/combat/fist_auto1.dae")
				.addProperty(AttackPhaseProperty.PARTICLE, Particles.HIT_BLUNT)
				.loadAnimation(biped, dist);
		FIST_AUTO_2 = new BasicAttackAnimation(2002, 0.08F, 0F, 0.1F, 0.15F, null, "111213", "biped/combat/fist_auto2.dae")
				.addProperty(AttackPhaseProperty.PARTICLE, Particles.HIT_BLUNT)
				.loadAnimation(biped, dist);
		FIST_AUTO_3 = new BasicAttackAnimation(2003, 0.08F, 0F, 0.1F, 0.5F, Hand.OFF_HAND, null, "111313", "biped/combat/fist_auto3.dae")
				.addProperty(AttackPhaseProperty.PARTICLE, Particles.HIT_BLUNT)
				.loadAnimation(biped, dist);
		FIST_DASH = new DashAttackAnimation(2004, 0.06F, 0.05F, 0.15F, 0.3F, 0.7F, null, "213", "biped/combat/fist_dash.dae")
				.addProperty(AttackPhaseProperty.PARTICLE, Particles.HIT_BLUNT)
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.0F)
				.loadAnimation(biped, dist);
		SWORD_AUTO_1 = new BasicAttackAnimation(2005, 0.13F, 0.0F, 0.11F, 0.3F, null, "111213", "biped/combat/sword_auto1.dae").loadAnimation(biped, dist);
		SWORD_AUTO_2 = new BasicAttackAnimation(2006, 0.13F, 0.0F, 0.11F, 0.3F, null, "111213", "biped/combat/sword_auto2.dae").loadAnimation(biped, dist);
		SWORD_AUTO_3 = new BasicAttackAnimation(2007, 0.13F, 0.0F, 0.11F, 0.6F, null, "111213", "biped/combat/sword_auto3.dae").loadAnimation(biped, dist);
		SWORD_DASH = new DashAttackAnimation(2008, 0.12F, 0.1F, 0.25F, 0.4F, 0.65F, null, "111213", "biped/combat/sword_dash.dae")
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.6F)
				.loadAnimation(biped, dist);
		GREATSWORD_AUTO_1 = new BasicAttackAnimation(2009, 0.2F, 0.4F, 0.6F, 0.8F, null, "111213", "biped/combat/greatsword_auto1.dae").loadAnimation(biped, dist);
		GREATSWORD_AUTO_2 = new BasicAttackAnimation(2010, 0.2F, 0.4F, 0.6F, 0.8F, null, "111213", "biped/combat/greatsword_auto2.dae").loadAnimation(biped, dist);
		GREATSWORD_DASH = new DashAttackAnimation(2011, 0.11F, 0.4F, 0.65F, 0.8F, 1.2F, null, "111213", "biped/combat/greatsword_dash.dae", false)
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.loadAnimation(biped, dist);
		SPEAR_ONEHAND_AUTO = new BasicAttackAnimation(2012, 0.16F, 0.1F, 0.2F, 0.45F, null, "111213", "biped/combat/spear_onehand_auto.dae").loadAnimation(biped, dist);
		SPEAR_TWOHAND_AUTO_1 = new BasicAttackAnimation(2013, 0.25F, 0.05F, 0.15F, 0.45F, null, "111213", "biped/combat/spear_twohand_auto1.dae").loadAnimation(biped, dist);
		SPEAR_TWOHAND_AUTO_2 = new BasicAttackAnimation(2014, 0.25F, 0.05F, 0.15F, 0.45F, null, "111213", "biped/combat/spear_twohand_auto2.dae").loadAnimation(biped, dist);
		SPEAR_DASH = new DashAttackAnimation(2015, 0.16F, 0.05F, 0.2F, 0.3F, 0.7F, null, "111213", "biped/combat/spear_dash.dae")
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.loadAnimation(biped, dist);
		TOOL_AUTO_1 = new BasicAttackAnimation(2016, 0.13F, 0.05F, 0.15F, 0.3F, null, "111213", null).loadFrom(SWORD_AUTO_1).loadAnimation(biped, dist);
		TOOL_AUTO_2 = new BasicAttackAnimation(2017, 0.13F, 0.05F, 0.15F, 0.4F, null, "111213", "biped/combat/sword_auto4.dae")
				.loadAnimation(biped, dist);
		TOOL_DASH = new DashAttackAnimation(2018, 0.16F, 0.08F, 0.15F, 0.25F, 0.58F, null, "111213", "biped/combat/tool_dash.dae")
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.addProperty(AttackPhaseProperty.MAX_STRIKES, ValueCorrector.getAdder(1))
				.loadAnimation(biped, dist);
		AXE_DASH = new DashAttackAnimation(2019, 0.25F, 0.08F, 0.4F, 0.46F, 0.9F, null, "111213", "biped/combat/axe_dash.dae")
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.loadAnimation(biped, dist);
		SWORD_DUAL_AUTO_1 = new BasicAttackAnimation(2020, 0.16F, 0.0F, 0.11F, 0.2F, null, "111213", "biped/combat/sword_dual_auto1.dae").loadAnimation(biped, dist);
		SWORD_DUAL_AUTO_2 = new BasicAttackAnimation(2021, 0.13F, 0.0F, 0.1F, 0.1F, Hand.OFF_HAND, null, "111313", "biped/combat/sword_dual_auto2.dae").loadAnimation(biped, dist);
		SWORD_DUAL_AUTO_3 = new BasicAttackAnimation(2022, 0.18F, 0.0F, 0.25F, 0.35F, 0.64F, Colliders.dualSword, "3", "biped/combat/sword_dual_auto3.dae").loadAnimation(biped, dist);
		SWORD_DUAL_DASH = new DashAttackAnimation(2023, 0.16F, 0.05F, 0.05F, 0.3F, 0.75F, Colliders.dualSwordDash, "", "biped/combat/sword_dual_dash.dae")
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.6F)
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.loadAnimation(biped, dist);
		KATANA_AUTO_1 = new BasicAttackAnimation(2024, 0.06F, 0.05F, 0.16F, 0.2F, null, "111213", "biped/combat/katana_auto1.dae").loadAnimation(biped, dist);
		KATANA_AUTO_2 = new BasicAttackAnimation(2025, 0.16F, 0.0F, 0.11F, 0.2F, null, "111213", "biped/combat/katana_auto2.dae").loadAnimation(biped, dist);
		KATANA_AUTO_3 = new BasicAttackAnimation(2026, 0.06F, 0.1F, 0.21F, 0.59F, null, "111213", "biped/combat/katana_auto3.dae").loadAnimation(biped, dist);
		KATANA_SHEATHING_AUTO = new BasicAttackAnimation(2027, 0.06F, 0.0F, 0.06F, 0.65F, Colliders.fatal_draw, "", "biped/combat/katana_sheath_auto.dae")
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.addProperty(AttackPhaseProperty.ARMOR_NEGATION, ValueCorrector.getAdder(30.0F))
				.addProperty(AttackPhaseProperty.DAMAGE, ValueCorrector.getMultiplier(1.0F))
				.addProperty(AttackPhaseProperty.MAX_STRIKES, ValueCorrector.getAdder(2))
				.addProperty(AttackPhaseProperty.SWING_SOUND, Sounds.WHOOSH_SHARP)
				.loadAnimation(biped, dist);
		KATANA_SHEATHING_DASH = new DashAttackAnimation(2028, 0.06F, 0.05F, 0.05F, 0.11F, 0.65F, null, "111213", "biped/combat/katana_sheath_dash.dae")
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.addProperty(AttackPhaseProperty.ARMOR_NEGATION, ValueCorrector.getAdder(30.0F))
				.addProperty(AttackPhaseProperty.DAMAGE, ValueCorrector.getMultiplier(1.0F))
				.addProperty(AttackPhaseProperty.SWING_SOUND, Sounds.WHOOSH_SHARP)
				.loadAnimation(biped, dist);
		AXE_AUTO1 = new BasicAttackAnimation(2029, 0.16F, 0.05F, 0.16F, 0.7F, null, "111213", "biped/combat/axe_auto1.dae").loadAnimation(biped, dist);
		AXE_AUTO2 = new BasicAttackAnimation(2030, 0.16F, 0.05F, 0.16F, 0.85F, null, "111213", "biped/combat/axe_auto2.dae").loadAnimation(biped, dist);
		LONGSWORD_AUTO_1 = new BasicAttackAnimation(2031, 0.1F, 0.2F, 0.3F, 0.45F, null, "111213", "biped/combat/longsword_auto1.dae").loadAnimation(biped, dist);
		LONGSWORD_AUTO_2 = new BasicAttackAnimation(2032, 0.15F, 0.1F, 0.21F, 0.45F, null, "111213", "biped/combat/longsword_auto2.dae").loadAnimation(biped, dist);
		LONGSWORD_AUTO_3 = new BasicAttackAnimation(2033, 0.15F, 0.05F, 0.16F, 0.8F, null, "111213", "biped/combat/longsword_auto3.dae").loadAnimation(biped, dist);
		LONGSWORD_DASH = new DashAttackAnimation(2034, 0.15F, 0.1F, 0.3F, 0.5F, 0.7F, null, "111213", "biped/combat/longsword_dash.dae")
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.loadAnimation(biped, dist);
		TACHI_DASH = new DashAttackAnimation(2035, 0.15F, 0.1F, 0.2F, 0.45F, 0.7F, null, "111213", "biped/combat/tachi_dash.dae", false)
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.loadAnimation(biped, dist);
		DAGGER_AUTO_1 = new BasicAttackAnimation(2036, 0.08F, 0.05F, 0.15F, 0.3F, null, "111213", "biped/combat/dagger_auto1.dae").loadAnimation(biped, dist);
		DAGGER_AUTO_2 = new BasicAttackAnimation(2037, 0.08F, 0.0F, 0.1F, 0.2F, null, "111213", "biped/combat/dagger_auto2.dae").loadAnimation(biped, dist);
		DAGGER_AUTO_3 = new BasicAttackAnimation(2038, 0.08F, 0.15F, 0.26F, 0.5F, null, "111213", "biped/combat/dagger_auto3.dae").loadAnimation(biped, dist);
		DAGGER_DUAL_AUTO_1 = new BasicAttackAnimation(2039, 0.08F, 0.05F, 0.16F, 0.25F, null, "111213", "biped/combat/dagger_dual_auto1.dae").loadAnimation(biped, dist);
		DAGGER_DUAL_AUTO_2 = new BasicAttackAnimation(2040, 0.08F, 0.0F, 0.11F, 0.16F, Hand.OFF_HAND, null, "111313", "biped/combat/dagger_dual_auto2.dae").loadAnimation(biped, dist);
		DAGGER_DUAL_AUTO_3 = new BasicAttackAnimation(2041, 0.08F, 0.0F, 0.11F, 0.2F, null, "111213", "biped/combat/dagger_dual_auto3.dae").loadAnimation(biped, dist);
		DAGGER_DUAL_AUTO_4 = new BasicAttackAnimation(2042, 0.13F, 0.1F, 0.21F, 0.4F, Colliders.dualDaggerDash, "", "biped/combat/dagger_dual_auto4.dae").loadAnimation(biped, dist);
		DAGGER_DUAL_DASH = new DashAttackAnimation(2043, 0.1F, 0.1F, 0.25F, 0.3F, 0.65F, Colliders.dualDaggerDash, "", "biped/combat/dagger_dual_dash.dae")
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.4F)
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.loadAnimation(biped, dist);
		
		SWORD_AIR_SLASH = new AirSlashAnimation(2044, 0.1F, 0.15F, 0.26F, 0.5F, null, "111213", "biped/combat/sword_airslash.dae").loadAnimation(biped, dist);
		SWORD_DUAL_AIR_SLASH = new AirSlashAnimation(2045, 0.1F, 0.15F, 0.26F, 0.5F, Colliders.dualSwordAirslash, "3", "biped/combat/sword_dual_airslash.dae").loadAnimation(biped, dist);
		KATANA_AIR_SLASH = new AirSlashAnimation(2046, 0.1F, 0.05F, 0.16F, 0.3F, null, "111213", "biped/combat/katana_airslash.dae").loadAnimation(biped, dist);
		KATANA_SHEATH_AIR_SLASH = new AirSlashAnimation(2047, 0.1F, 0.1F, 0.16F, 0.3F, null, "111213", "biped/combat/katana_sheath_airslash.dae")
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.addProperty(AttackPhaseProperty.ARMOR_NEGATION, ValueCorrector.getAdder(30.0F))
				.addProperty(AttackPhaseProperty.DAMAGE, ValueCorrector.getMultiplier(1.5F))
				.addProperty(AttackPhaseProperty.MAX_STRIKES, ValueCorrector.getAdder(2))
				.addProperty(AttackPhaseProperty.SWING_SOUND, Sounds.WHOOSH_SHARP)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.0F)
				.loadAnimation(biped, dist);
		SPEAR_ONEHAND_AIR_SLASH = new AirSlashAnimation(2048, 0.1F, 0.15F, 0.26F, 0.4F, null, "111213", "biped/combat/spear_onehand_airslash.dae").loadAnimation(biped, dist);
		SPEAR_TWOHAND_AIR_SLASH = new AirSlashAnimation(2049, 0.1F, 0.25F, 0.36F, 0.6F, null, "111213", "biped/combat/spear_twohand_airslash.dae").loadAnimation(biped, dist);
		LONGSWORD_AIR_SLASH = new AirSlashAnimation(2050, 0.1F, 0.3F, 0.41F, 0.5F, null, "111213", "biped/combat/longsword_airslash.dae").loadAnimation(biped, dist);
		GREATSWORD_AIR_SLASH = new AirSlashAnimation(2051, 0.1F, 0.5F, 0.55F, 0.71F, 0.75F, false, null, "111213", "biped/combat/greatsword_airslash.dae").loadAnimation(biped, dist);
		FIST_AIR_SLASH = new AirSlashAnimation(2052, 0.1F, 0.15F, 0.26F, 0.4F, null, "111213", "biped/combat/fist_airslash.dae")
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 4.0F)
				.loadAnimation(biped, dist);
		DAGGER_AIR_SLASH = new AirSlashAnimation(2053, 0.1F, 0.15F, 0.26F, 0.45F, null, "111213", "biped/combat/dagger_airslash.dae")
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.4F)
				.loadAnimation(biped, dist);
		DAGGER_DUAL_AIR_SLASH = new AirSlashAnimation(2054, 0.1F, 0.15F, 0.26F, 0.4F, Colliders.dualDaggerAirslash, "3", null)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.0F).loadFrom(SWORD_DUAL_AIR_SLASH).loadAnimation(biped, dist);
		AXE_AIRSLASH = new AirSlashAnimation(2055, 0.1F, 0.3F, 0.4F, 0.65F, null, "111213", "biped/combat/axe_airslash.dae").loadAnimation(biped, dist);
		
		SWORD_MOUNT_ATTACK = new MountAttackAnimation(2099, 0.16F, 0.1F, 0.2F, 0.25F, 0.7F, null, "111213", "biped/combat/sword_mount_attack.dae").loadAnimation(biped, dist);
		SPEAR_MOUNT_ATTACK = new MountAttackAnimation(2245, 0.16F, 0.38F, 0.38F, 0.45F, 0.8F, null, "111213", "biped/combat/spear_mount_attack.dae")
				.addProperty(AttackAnimationProperty.DIRECTIONAL, true)
				.loadAnimation(biped, dist);
		
		BIPED_ARMED_MOB_ATTACK1 = new AttackAnimation(2900, 0.08F, 0.45F, 0.55F, 0.66F, 0.95F, false, null, "111213", "biped/combat/armed_mob_attack1.dae")
				.addProperty(AttackAnimationProperty.DIRECTIONAL, true)
				.loadAnimation(biped, dist);
		BIPED_ARMED_MOB_ATTACK2 = new AttackAnimation(2901, 0.08F, 0.45F, 0.5F, 0.61F, 0.95F, false, null, "111213", "biped/combat/armed_mob_attack2.dae")
				.addProperty(AttackAnimationProperty.DIRECTIONAL, true)
				.loadAnimation(biped, dist);
		BIPED_MOB_THROW = new AttackAnimation(2902, 0.11F, 1.0F, 0, 0, 0, false, null, "", "biped/combat/javelin_throw_mid.dae").loadAnimation(biped, dist);
		
		SWORD_GUARD_HIT = new GuardAnimation(2990, 0.05F, "biped/skill/guard_sword_hit.dae").loadAnimation(biped, dist);
		SWORD_DUAL_GUARD_HIT = new GuardAnimation(2991, 0.05F, "biped/skill/guard_sword_hit.dae").loadAnimation(biped, dist);
		LONGSWORD_GUARD_HIT = new GuardAnimation(2992, 0.05F, "biped/skill/guard_longsword_hit.dae").loadAnimation(biped, dist);
		SPEAR_GUARD_HIT = new GuardAnimation(2993, 0.05F, "biped/skill/guard_spear_hit.dae").loadAnimation(biped, dist);
		GREATSWORD_GUARD_HIT = new GuardAnimation(2994, 0.05F, "biped/skill/guard_greatsword_hit.dae").loadAnimation(biped, dist);
		KATANA_GUARD_HIT = new GuardAnimation(2995, 0.05F, "biped/skill/guard_katana_hit.dae").loadAnimation(biped, dist);
		
		BIPED_HIT_SHORT = new HitAnimation(3000, 0.05F, "biped/combat/hit_short.dae").loadAnimation(biped, dist);
		BIPED_HIT_LONG = new LongHitAnimation(3001, 0.08F, "biped/combat/hit_long.dae").loadAnimation(biped, dist);
		BIPED_HIT_ON_MOUNT = new LongHitAnimation(3002, 0.08F, "biped/combat/hit_on_mount.dae").loadAnimation(biped, dist);
		BIPED_DEATH = new LongHitAnimation(3003, 0.16F, "biped/living/death.dae").loadAnimation(biped, dist);
		
		CREEPER_HIT_SHORT = new HitAnimation(3400, 0.05F, "creeper/hit_short.dae").loadAnimation(crepper, dist);
		CREEPER_HIT_LONG = new LongHitAnimation(3401, 0.08F, "creeper/hit_long.dae").loadAnimation(crepper, dist);
		CREEPER_DEATH = new LongHitAnimation(3402, 0.16F, "creeper/death.dae").loadAnimation(crepper, dist);
		
		ENDERMAN_HIT_SHORT = new HitAnimation(3004, 0.05F, "enderman/hit_short.dae").loadAnimation(enderman, dist);
		ENDERMAN_HIT_LONG = new LongHitAnimation(3005, 0.08F, "enderman/hit_long.dae").loadAnimation(enderman, dist);
		ENDERMAN_HIT_RAGE = new DodgeAnimation(3006, 0.16F, 0.0F, false, "enderman/convert_rampage.dae", -1.0F, -1.0F).loadAnimation(enderman, dist);
		ENDERMAN_TP_KICK1 = new AttackAnimation(3007, 0.06F, 0.15F, 0.3F, 0.4F, 1.0F, false, Colliders.endermanStick, "11", "enderman/tp_kick1.dae").loadAnimation(enderman, dist);
		ENDERMAN_TP_KICK2 = new AttackAnimation(3008, 0.16F, 0.15F, 0.25F, 0.45F, 1.0F, false, Colliders.endermanStick, "11", "enderman/tp_kick2.dae").loadAnimation(enderman, dist);
		ENDERMAN_KICK1 = new AttackAnimation(3009, 0.16F, 0.66F, 0.7F, 0.81F, 1.6F, false, Colliders.endermanStick, "12", "enderman/rush_kick.dae")
				.addProperty(AttackPhaseProperty.IMPACT, ValueCorrector.getSetter(4.0F))
				.loadAnimation(enderman, dist);
		ENDERMAN_KICK2 = new AttackAnimation(3010, 0.16F, 0.8F, 0.8F, 0.9F, 1.3F, false, Colliders.endermanStick, "11", "enderman/flying_kick.dae").loadAnimation(enderman, dist);
		ENDERMAN_KNEE = new AttackAnimation(3011, 0.16F, 0.25F, 0.25F, 0.31F, 1.0F, false, Colliders.fist, "11", "enderman/knee.dae")
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG)
				.loadAnimation(enderman, dist);
		ENDERMAN_KICK_COMBO = new AttackAnimation(3012, 0.1F, false, "enderman/kick_double.dae",
				new Phase(0.15F, 0.15F, 0.21F, 0.46F, "11", Colliders.endermanStick),
				new Phase(0.75F, 0.75F, 0.81F, 1.6F, "12", Colliders.endermanStick))
				.loadAnimation(enderman, dist);
		ENDERMAN_GRASP = new AttackAnimation(3015, 0.06F, 0.5F, 0.45F, 1.0F, 1.0F, false, Colliders.endermanStick, "111213", "enderman/grasp.dae")
				.addProperty(AttackAnimationProperty.DIRECTIONAL, true)
				.loadAnimation(enderman, dist);
		ENDERMAN_DEATH = new LongHitAnimation(3016, 0.16F, "enderman/death.dae").loadAnimation(enderman, dist);
		ENDERMAN_TP_EMERGENCE = new ActionAnimation(3017, 0.05F, true, false, "enderman/teleport_emergence.dae").loadAnimation(enderman, dist);
		
		SPIDER_ATTACK = new AttackAnimation(3100, 0.16F, 0.31F, 0.31F, 0.36F, 0.44F, false, Colliders.spiderRaid, "1", "spider/attack.dae").loadAnimation(spider, dist);
		SPIDER_JUMP_ATTACK = new AttackAnimation(3101, 0.16F, 0.25F, 0.25F, 0.41F, 0.8F, true, Colliders.spiderRaid, "1", "spider/jump_attack.dae").loadAnimation(spider, dist);
		SPIDER_HIT = new HitAnimation(3102, 0.08F, "spider/hit.dae").loadAnimation(spider, dist);
		SPIDER_DEATH = new LongHitAnimation(3103, 0.16F, "spider/death.dae").loadAnimation(spider, dist);
		
		GOLEM_ATTACK1 = new AttackAnimation(3200, 0.2F, 0.1F, 0.15F, 0.25F, 0.9F, false, Colliders.headbutt, "11", "iron_golem/attack1.dae").loadAnimation(iron_golem, dist);
		GOLEM_ATTACK2 = new AttackAnimation(3201, 0.34F, 0.1F, 0.4F, 0.6F, 1.15F, false, Colliders.golemSmashDown, "11121", "iron_golem/attack2.dae").loadAnimation(iron_golem, dist);
		GOLEM_ATTACK3 = new AttackAnimation(3202, 0.16F, 0.4F, 0.4F, 0.5F, 0.9F, false, Colliders.golemSwingArm, "11131", "iron_golem/attack3.dae")
				.addProperty(AttackAnimationProperty.DIRECTIONAL, true)
				.loadAnimation(iron_golem, dist);
		GOLEM_ATTACK4 = new AttackAnimation(3203, 0.16F, 0.4F, 0.4F, 0.5F, 0.9F, false, Colliders.golemSwingArm, "11121", "iron_golem/attack4.dae")
				.addProperty(AttackAnimationProperty.DIRECTIONAL, true)
				.loadAnimation(iron_golem, dist);
		GOLEM_DEATH = new LongHitAnimation(3204, 0.11F, "iron_golem/death.dae").loadAnimation(iron_golem, dist);
		
		VINDICATOR_SWING_AXE1 = new AttackAnimation(3300, 0.2F, 0.25F, 0.35F, 0.46F, 0.71F, false, Colliders.tools, "111213", "illager/swing_axe1.dae").loadAnimation(biped, dist);
		VINDICATOR_SWING_AXE2 = new AttackAnimation(3301, 0.2F, 0.25F, 0.3F, 0.41F, 0.71F, false, Colliders.tools, "111213", "illager/swing_axe2.dae").loadAnimation(biped, dist);
		VINDICATOR_SWING_AXE3 = new AttackAnimation(3302, 0.05F, 0.50F, 0.62F, 0.75F, 1F, true, Colliders.tools, "111213", "illager/swing_axe3.dae").loadAnimation(biped, dist);
		
		PIGLIN_DEATH = new LongHitAnimation(6000, 0.16F, "piglin/death.dae").loadAnimation(piglin, dist);
		
		HOGLIN_DEATH = new LongHitAnimation(6050, 0.16F, "hoglin/death.dae").loadAnimation(hoglin, dist);
		HOGLIN_ATTACK = new AttackAnimation(6052, 0.16F, 0.25F, 0.25F, 0.45F, 1.0F, false, Colliders.golemSwingArm, "1", "hoglin/attack.dae").loadAnimation(hoglin, dist);
		
		RAVAGER_DEATH = new LongHitAnimation(3600, 0.11F, "ravager/death.dae").loadAnimation(ravager, dist);
		RAVAGER_STUN = new ActionAnimation(3601, 0.16F, true, false, "ravager/groggy.dae").loadAnimation(ravager, dist);
		RAVAGER_ATTACK1 = new AttackAnimation(3602, 0.16F, 0.2F, 0.4F, 0.5F, 0.55F, false, Colliders.headbutt_ravager, "131", "ravager/attack1.dae").loadAnimation(ravager, dist);
		RAVAGER_ATTACK2 = new AttackAnimation(3603, 0.16F, 0.2F, 0.4F, 0.5F, 1.3F, false, Colliders.headbutt_ravager, "131", "ravager/attack2.dae").loadAnimation(ravager, dist);
		RAVAGER_ATTACK3 = new AttackAnimation(3604, 0.16F, 0.0F, 1.1F, 1.16F, 1.6F, false, Colliders.headbutt_ravager, "131", "ravager/attack3.dae").loadAnimation(ravager, dist);
		
		VEX_HIT = new HitAnimation(3308, 0.048F, "vex/hit.dae").loadAnimation(vex, dist);
		VEX_DEATH = new LongHitAnimation(3309, 0.16F, "vex/death.dae").loadAnimation(vex, dist);
		VEX_CHARGING = new AttackAnimation(3310, 0.11F, 0.3F, 0.3F, 0.5F, 1.2F, true, Colliders.sword, "", "vex/charge.dae").loadAnimation(vex, dist);
		
		WITCH_DRINKING = new StaticAnimation(3306, 0.16F, false, "witch/drink.dae").loadAnimation(biped, dist);
		
		WITHER_SKELETON_ATTACK1 = new AttackAnimation(3500, 0.16F, 0.2F, 0.3F, 0.41F, 0.7F, false, Colliders.sword, "111213", "skeleton/wither_skeleton_attack1.dae")
				.addProperty(AttackAnimationProperty.DIRECTIONAL, true)
				.loadAnimation(biped, dist);
		WITHER_SKELETON_ATTACK2 = new AttackAnimation(3501, 0.16F, 0.25F, 0.25F, 0.36F, 0.7F, false, Colliders.sword, "111213", "skeleton/wither_skeleton_attack2.dae")
				.addProperty(AttackAnimationProperty.DIRECTIONAL, true)
				.loadAnimation(biped, dist);
		WITHER_SKELETON_ATTACK3 = new AttackAnimation(3502, 0.16F, 0.25F, 0.25F, 0.36F, 0.7F, false, Colliders.sword, "111213", "skeleton/wither_skeleton_attack3.dae")
				.addProperty(AttackAnimationProperty.DIRECTIONAL, true)
				.loadAnimation(biped, dist);
		
		ZOMBIE_ATTACK1 = new AttackAnimation(4000, 0.1F, 0.3F, 0.35F, 0.55F, 0.85F, false, Colliders.fist, "111213", "zombie/attack1.dae")
				.addProperty(AttackAnimationProperty.DIRECTIONAL, true)
				.loadAnimation(biped, dist);
		ZOMBIE_ATTACK2 = new AttackAnimation(4001, 0.1F, 0.3F, 0.33F, 0.55F, 0.85F, false, Colliders.fist, "111313", "zombie/attack2.dae")
				.addProperty(AttackAnimationProperty.DIRECTIONAL, true)
				.loadAnimation(biped, dist);
		ZOMBIE_ATTACK3 = new AttackAnimation(4002, 0.1F, 0.5F, 0.5F, 0.6F, 1.15F, false, Colliders.headbutt, "113", "zombie/attack3.dae").loadAnimation(biped, dist);
		
		SWEEPING_EDGE = new SpecialAttackAnimation(4111, 0.11F, 0.1F, 0.45F, 0.6F, 0.85F, false, null, "111213", "biped/skill/sweeping_edge.dae")
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.addProperty(AttackAnimationProperty.DIRECTIONAL, true)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.6F)
				.addProperty(AttackAnimationProperty.COLLIDER_BONUS, 1)
				.loadAnimation(biped, dist);
		
		DANCING_EDGE = new SpecialAttackAnimation(4112, 0.25F, true, "biped/skill/dancing_edge.dae",
				new Phase(0.2F, 0.2F, 0.31F, 0.31F, "111213", null), new Phase(0.5F, 0.5F, 0.61F, 0.61F, Hand.OFF_HAND, "111313", null),
				new Phase(0.75F, 0.75F, 0.85F, 1.15F, "111213", null))
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.6F)
				.loadAnimation(biped, dist);
		
		GUILLOTINE_AXE = new SpecialAttackAnimation(5000, 0.08F, 0.2F, 0.5F, 0.65F, 1.0F, true, null, "111213", "biped/skill/axe_special.dae")
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.0F)
				.loadAnimation(biped, dist);
		
		SPEAR_THRUST = new SpecialAttackAnimation(5001, 0.11F, false, "biped/skill/spear_thrust.dae",
				new Phase(0.3F, 0.3F, 0.36F, 0.51F, "111213", null), new Phase(0.51F, 0.51F, 0.56F, 0.73F, "111213", null),
				new Phase(0.73F, 0.73F, 0.78F, 1.05F, "111213", null))
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.addProperty(AttackAnimationProperty.DIRECTIONAL, true)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.2F)
				.loadAnimation(biped, dist);
		
		SPEAR_SLASH = new SpecialAttackAnimation(5002, 0.1F, false, "biped/skill/spear_slash.dae",
				new Phase(0.2F, 0.2F, 0.41F, 0.5F, "111213", null), new Phase(0.5F, 0.75F, 0.95F, 1.25F, "111213", null))
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.addProperty(AttackAnimationProperty.DIRECTIONAL, true)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.2F)
				.loadAnimation(biped, dist);
		
		GIANT_WHIRLWIND = new SpecialAttackAnimation(5003, 0.41F, false, "biped/skill/giant_whirlwind.dae",
				new Phase(0.3F, 0.35F, 0.55F, 0.85F, "111213", null), new Phase(0.95F, 1.05F, 1.2F, 1.35F, "111213", null),
				new Phase(1.65F, 1.75F, 1.95F, 2.5F, "111213", null))
				.addProperty(AttackAnimationProperty.DIRECTIONAL, true)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F)
				.loadAnimation(biped, dist);
		
		FATAL_DRAW = new SpecialAttackAnimation(5004, 0.15F, 0.0F, 0.7F, 0.81F, 1.0F, false, Colliders.fatal_draw, "", "biped/skill/fatal_draw.dae")
				.addProperty(AttackPhaseProperty.SWING_SOUND, Sounds.WHOOSH_SHARP)
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.0F)
				.addProperty(StaticAnimationProperty.SOUNDS, new SoundKey[] {SoundKey.create(0.05F, Sounds.SWORD_IN, false)})
				.loadAnimation(biped, dist);
		
		FATAL_DRAW_DASH = new SpecialAttackAnimation(5005, 0.15F, 0.43F, 0.85F, 0.91F, 1.4F, false, Colliders.fatal_draw_dash, "", "biped/skill/fatal_draw_dash.dae")
				.addProperty(AttackPhaseProperty.SWING_SOUND, Sounds.WHOOSH_SHARP)
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.0F)
				.addProperty(StaticAnimationProperty.SOUNDS, new SoundKey[] {SoundKey.create(0.05F, Sounds.SWORD_IN, false)})
				.loadAnimation(biped, dist);
		
		LETHAL_SLICING = new SpecialAttackAnimation(5006, 0.15F, 0.0F, 0.0F, 0.11F, 0.38F, false, Colliders.narrowFront, "", "biped/skill/lethal_slicing_start.dae")
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.6F)
				.loadAnimation(biped, dist);
		
		LETHAL_SLICING_ONCE = new SpecialAttackAnimation(5007, 0.016F, 0.0F, 0.0F, 0.1F, 0.6F, false, Colliders.fatal_draw, "", "biped/skill/lethal_slicing_once.dae")
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.6F)
				.loadAnimation(biped, dist);
		
		LETHAL_SLICING_TWICE = new SpecialAttackAnimation(5008, 0.016F, false, "biped/skill/lethal_slicing_twice.dae",
				new Phase(0.0F, 0.0F, 0.1F, 0.15F, "", Colliders.fatal_draw), new Phase(0.15F, 0.15F, 0.25F, 0.6F, "", Colliders.fatal_draw))
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.6F)
				.loadAnimation(biped, dist);
		
		RELENTLESS_COMBO = new SpecialAttackAnimation(5009, 0.05F, false, "biped/skill/relentless_combo.dae",
				new Phase(0.016F, 0.016F, 0.066F, 0.133F, Hand.OFF_HAND, "", Colliders.narrowFront), new Phase(0.133F, 0.133F, 0.183F, 0.25F, "", Colliders.narrowFront),
				new Phase(0.25F, 0.25F, 0.3F, 0.366F, Hand.OFF_HAND, "", Colliders.narrowFront), new Phase(0.366F, 0.366F, 0.416F, 0.483F, "", Colliders.narrowFront),
				new Phase(0.483F, 0.483F, 0.533F, 0.6F, Hand.OFF_HAND, "", Colliders.narrowFront), new Phase(0.6F, 0.6F, 0.65F, 0.716F, "", Colliders.narrowFront),
				new Phase(0.716F, 0.716F, 0.766F, 0.833F, Hand.OFF_HAND, "", Colliders.narrowFront), new Phase(0.833F, 0.833F, 0.883F, 1.1F, "", Colliders.narrowFront))
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 4.0F)
				.loadAnimation(biped, dist);
		
		EVISCERATE_FIRST = new SpecialAttackAnimation(5010, 0.08F, 0.05F, 0.05F, 0.15F, 0.45F, true, null, "111213", "biped/skill/eviscerate_first.dae")
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.4F)
				.loadAnimation(biped, dist);
		
		EVISCERATE_SECOND = new SpecialAttackAnimation(5011, 0.15F, -0.1F, -0.1F, 0.0F, 0.4F, true, null, "111213", "biped/skill/eviscerate_second.dae")
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.addProperty(AttackPhaseProperty.HIT_SOUND, Sounds.EVISCERATE)
				.addProperty(AttackPhaseProperty.PARTICLE, Particles.EVISCERATE_SKILL)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.4F)
				.loadAnimation(biped, dist);
		
		BLADE_RUSH_FIRST = new SpecialAttackAnimation(5012, 0.1F, 0.0F, 0.0F, 0.06F, 0.3F, true, Colliders.bladeRush, "", "biped/skill/blade_rush_first.dae")
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				.addProperty(AttackPhaseProperty.TARGET_PRIORITY, Priority.TARGET)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.0F)
				.loadAnimation(biped, dist);
		BLADE_RUSH_SECOND = new SpecialAttackAnimation(5013, 0.1F, 0.0F, 0.0F, 0.06F, 0.3F, true, Colliders.bladeRush, "", "biped/skill/blade_rush_second.dae")
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				.addProperty(AttackPhaseProperty.TARGET_PRIORITY, Priority.TARGET)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.0F)
				.loadAnimation(biped, dist);
		BLADE_RUSH_THIRD = new SpecialAttackAnimation(5014, 0.1F, 0.0F, 0.0F, 0.06F, 0.3F, true, Colliders.bladeRush, "", "biped/skill/blade_rush_third.dae")
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				.addProperty(AttackPhaseProperty.TARGET_PRIORITY, Priority.TARGET)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.0F)
				.loadAnimation(biped, dist);
		BLADE_RUSH_FINISHER = new SpecialAttackAnimation(5015, 0.15F, 0.0F, 0.1F, 0.16F, 0.65F, true, Colliders.bladeRush, "", "biped/skill/blade_rush_finisher.dae")
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				.addProperty(AttackPhaseProperty.TARGET_PRIORITY, Priority.TARGET)
				.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.0F)
				.loadAnimation(biped, dist);
		
		MobAttackPatterns.setMobAttackPatterns();
	}
}