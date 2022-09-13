package yesman.epicfight.gameasset;

import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Lists;
import com.mojang.math.Quaternion;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.DragonFireball;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.loading.FMLEnvironment;
import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.Keyframe;
import yesman.epicfight.api.animation.TransformSheet;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackPhaseProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.types.ActionAnimation;
import yesman.epicfight.api.animation.types.ActionAnimation.ActionTime;
import yesman.epicfight.api.animation.types.AimAnimation;
import yesman.epicfight.api.animation.types.AirSlashAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.AttackAnimation.Phase;
import yesman.epicfight.api.animation.types.BasicAttackAnimation;
import yesman.epicfight.api.animation.types.DashAttackAnimation;
import yesman.epicfight.api.animation.types.DodgeAnimation;
import yesman.epicfight.api.animation.types.GuardAnimation;
import yesman.epicfight.api.animation.types.HitAnimation;
import yesman.epicfight.api.animation.types.InvincibleAnimation;
import yesman.epicfight.api.animation.types.KnockdownAnimation;
import yesman.epicfight.api.animation.types.LongHitAnimation;
import yesman.epicfight.api.animation.types.MirrorAnimation;
import yesman.epicfight.api.animation.types.MountAttackAnimation;
import yesman.epicfight.api.animation.types.MovementAnimation;
import yesman.epicfight.api.animation.types.OffAnimation;
import yesman.epicfight.api.animation.types.RangedAttackAnimation;
import yesman.epicfight.api.animation.types.ReboundAnimation;
import yesman.epicfight.api.animation.types.SpecialAttackAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation.Event;
import yesman.epicfight.api.animation.types.StaticAnimation.Event.Side;
import yesman.epicfight.api.animation.types.procedural.EnderDragonActionAnimation;
import yesman.epicfight.api.animation.types.procedural.EnderDragonAttackAnimation;
import yesman.epicfight.api.animation.types.procedural.EnderDragonDeathAnimation;
import yesman.epicfight.api.animation.types.procedural.EnderDragonDynamicActionAnimation;
import yesman.epicfight.api.animation.types.procedural.EnderDraonWalkAnimation;
import yesman.epicfight.api.animation.types.procedural.IKInfo;
import yesman.epicfight.api.client.animation.ClientAnimationProperties;
import yesman.epicfight.api.client.animation.Layer;
import yesman.epicfight.api.client.model.ClientModels;
import yesman.epicfight.api.collider.OBBCollider;
import yesman.epicfight.api.forgeevent.AnimationRegistryEvent;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.api.utils.ExtendedDamageSource;
import yesman.epicfight.api.utils.ExtendedDamageSource.StunType;
import yesman.epicfight.api.utils.HitEntityList.Priority;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.ValueCorrector;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.boss.WitherPatch;
import yesman.epicfight.world.capabilities.entitypatch.boss.enderdragon.EnderDragonPatch;
import yesman.epicfight.world.capabilities.entitypatch.boss.enderdragon.PatchedPhases;

public class Animations {
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
	public static StaticAnimation BIPED_RUN_SPEAR;
	public static StaticAnimation BIPED_HOLD_GREATSWORD;
	public static StaticAnimation BIPED_HOLD_KATANA_SHEATHING;
	public static StaticAnimation BIPED_HOLD_KATANA;
	public static StaticAnimation BIPED_HOLD_TACHI;
	public static StaticAnimation BIPED_HOLD_LONGSWORD;
	public static StaticAnimation BIPED_HOLD_SPEAR;
	public static StaticAnimation BIPED_HOLD_CROSSBOW;
	public static StaticAnimation BIPED_WALK_UNSHEATHING;
	public static StaticAnimation BIPED_WALK_TWOHAND;
	public static StaticAnimation BIPED_RUN_UNSHEATHING;
	public static StaticAnimation BIPED_KATANA_SCRAP;
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
	public static StaticAnimation BIPED_KNOCKDOWN;
	public static StaticAnimation BIPED_BLOCK;
	public static StaticAnimation BIPED_ROLL_FORWARD;
	public static StaticAnimation BIPED_ROLL_BACKWARD;
	public static StaticAnimation BIPED_STEP_FORWARD;
	public static StaticAnimation BIPED_STEP_BACKWARD;
	public static StaticAnimation BIPED_STEP_LEFT;
	public static StaticAnimation BIPED_STEP_RIGHT;
	public static StaticAnimation BIPED_KNOCKDOWN_WAKEUP_LEFT;
	public static StaticAnimation BIPED_KNOCKDOWN_WAKEUP_RIGHT;
	public static StaticAnimation BIPED_MOB_ONEHAND1;
	public static StaticAnimation BIPED_MOB_ONEHAND2;
	public static StaticAnimation BIPED_MOB_GREATSWORD;
	public static StaticAnimation BIPED_MOB_TACHI;
	public static StaticAnimation BIPED_MOB_SPEAR_ONEHAND;
	public static StaticAnimation BIPED_MOB_SPEAR_TWOHAND1;
	public static StaticAnimation BIPED_MOB_SPEAR_TWOHAND2;
	public static StaticAnimation BIPED_MOB_SPEAR_TWOHAND3;
	public static StaticAnimation BIPED_MOB_SWORD_DUAL1;
	public static StaticAnimation BIPED_MOB_SWORD_DUAL2;
	public static StaticAnimation BIPED_MOB_SWORD_DUAL3;
	public static StaticAnimation BIPED_MOB_LONGSWORD1;
	public static StaticAnimation BIPED_MOB_LONGSWORD2;
	public static StaticAnimation BIPED_MOB_KATANA1;
	public static StaticAnimation BIPED_MOB_KATANA2;
	public static StaticAnimation BIPED_MOB_KATANA3;
	public static StaticAnimation BIPED_MOB_DAGGER_ONEHAND1;
	public static StaticAnimation BIPED_MOB_DAGGER_ONEHAND2;
	public static StaticAnimation BIPED_MOB_DAGGER_ONEHAND3;
	public static StaticAnimation BIPED_MOB_DAGGER_TWOHAND1;
	public static StaticAnimation BIPED_MOB_DAGGER_TWOHAND2;
	public static StaticAnimation BIPED_MOB_THROW;
	public static StaticAnimation CREEPER_IDLE;
	public static StaticAnimation CREEPER_WALK;
	public static StaticAnimation CREEPER_HIT_LONG;
	public static StaticAnimation CREEPER_HIT_SHORT;
	public static StaticAnimation CREEPER_DEATH;
	public static StaticAnimation DRAGON_IDLE;
	public static StaticAnimation DRAGON_WALK;
	public static StaticAnimation DRAGON_WALK_PROCEDURAL;
	public static StaticAnimation DRAGON_FLY;
	public static StaticAnimation DRAGON_DEATH;
	public static StaticAnimation DRAGON_GROUND_TO_FLY;
	public static StaticAnimation DRAGON_FLY_TO_GROUND;
	public static StaticAnimation DRAGON_ATTACK1;
	public static StaticAnimation DRAGON_ATTACK2;
	public static StaticAnimation DRAGON_ATTACK3;
	public static StaticAnimation DRAGON_ATTACK4;
	public static StaticAnimation DRAGON_ATTACK4_RECOVERY;
	public static StaticAnimation DRAGON_FIREBALL;
	public static StaticAnimation DRAGON_AIRSTRIKE;
	public static StaticAnimation DRAGON_BACKJUMP_PREPARE;
	public static StaticAnimation DRAGON_BACKJUMP_MOVE;
	public static StaticAnimation DRAGON_BACKJUMP_RECOVERY;
	public static StaticAnimation DRAGON_CRYSTAL_LINK;
	public static StaticAnimation DRAGON_NEUTRALIZED;
	public static StaticAnimation DRAGON_NEUTRALIZED_RECOVERY;
	public static StaticAnimation ENDERMAN_IDLE;
	public static StaticAnimation ENDERMAN_WALK;
	public static StaticAnimation ENDERMAN_DEATH;
	public static StaticAnimation ENDERMAN_HIT_SHORT;
	public static StaticAnimation ENDERMAN_HIT_LONG;
	public static StaticAnimation ENDERMAN_CONVERT_RAGE;
	public static StaticAnimation ENDERMAN_ATTACK1;
	public static StaticAnimation ENDERMAN_ATTACK2;
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
	public static StaticAnimation PIGLIN_ZOMBIFIED_IDLE;
	public static StaticAnimation PIGLIN_ZOMBIFIED_WALK;
	public static StaticAnimation PIGLIN_ZOMBIFIED_CHASE;
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
	public static StaticAnimation VEX_CHARGE;
	public static StaticAnimation VEX_NEUTRALIZED;
	public static StaticAnimation WITCH_DRINKING;
	public static StaticAnimation WITHER_SKELETON_IDLE;
	public static StaticAnimation WITHER_SKELETON_SPECIAL_SPAWN;
	public static StaticAnimation WITHER_SKELETON_WALK;
	public static StaticAnimation WITHER_SKELETON_CHASE;
	public static StaticAnimation WITHER_SKELETON_ATTACK1;
	public static StaticAnimation WITHER_SKELETON_ATTACK2;
	public static StaticAnimation WITHER_SKELETON_ATTACK3;
	public static StaticAnimation WITHER_IDLE;
	public static StaticAnimation WITHER_CHARGE;
	public static StaticAnimation WITHER_DEATH;
	public static StaticAnimation WITHER_NEUTRALIZED;
	public static StaticAnimation WITHER_SPELL_ARMOR;
	public static StaticAnimation WITHER_BLOCKED;
	public static StaticAnimation WITHER_GHOST_STANDBY;
	public static StaticAnimation WITHER_SWIRL;
	public static StaticAnimation WITHER_BEAM;
	public static StaticAnimation WITHER_BACKFLIP;
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
	public static StaticAnimation FIST_AUTO1;
	public static StaticAnimation FIST_AUTO2;
	public static StaticAnimation FIST_AUTO3;
	public static StaticAnimation FIST_DASH;
	public static StaticAnimation FIST_AIR_SLASH;
	public static StaticAnimation SPEAR_ONEHAND_AUTO;
	public static StaticAnimation SPEAR_ONEHAND_AIR_SLASH;
	public static StaticAnimation SPEAR_TWOHAND_AUTO1;
	public static StaticAnimation SPEAR_TWOHAND_AUTO2;
	public static StaticAnimation SPEAR_TWOHAND_AIR_SLASH;
	public static StaticAnimation SPEAR_DASH;
	public static StaticAnimation SPEAR_MOUNT_ATTACK;
	public static StaticAnimation SPEAR_GUARD;
	public static StaticAnimation SPEAR_GUARD_HIT;
	public static StaticAnimation SWORD_AUTO1;
	public static StaticAnimation SWORD_AUTO2;
	public static StaticAnimation SWORD_AUTO3;
	public static StaticAnimation SWORD_DASH;
	public static StaticAnimation SWORD_AIR_SLASH;
	public static StaticAnimation SWORD_GUARD;
	public static StaticAnimation SWORD_GUARD_HIT;
	public static StaticAnimation SWORD_GUARD_ACTIVE_HIT1;
	public static StaticAnimation SWORD_GUARD_ACTIVE_HIT2;
	public static StaticAnimation SWORD_GUARD_ACTIVE_HIT3;
	public static StaticAnimation LONGSWORD_GUARD_ACTIVE_HIT1;
	public static StaticAnimation LONGSWORD_GUARD_ACTIVE_HIT2;
	public static StaticAnimation SWORD_DUAL_AUTO1;
	public static StaticAnimation SWORD_DUAL_AUTO2;
	public static StaticAnimation SWORD_DUAL_AUTO3;
	public static StaticAnimation SWORD_DUAL_DASH;
	public static StaticAnimation SWORD_DUAL_AIR_SLASH;
	public static StaticAnimation SWORD_DUAL_GUARD;
	public static StaticAnimation SWORD_DUAL_GUARD_HIT;
	public static StaticAnimation COMMON_GUARD_BREAK;
	public static StaticAnimation GREATSWORD_GUARD_BREAK;
	public static StaticAnimation LONGSWORD_AUTO1;
	public static StaticAnimation LONGSWORD_AUTO2;
	public static StaticAnimation LONGSWORD_AUTO3;
	public static StaticAnimation LONGSWORD_DASH;
	public static StaticAnimation LONGSWORD_AIR_SLASH;
	public static StaticAnimation LONGSWORD_GUARD;
	public static StaticAnimation LONGSWORD_GUARD_HIT;
	public static StaticAnimation TACHI_DASH;
	public static StaticAnimation TOOL_AUTO1;
	public static StaticAnimation TOOL_AUTO2;
	public static StaticAnimation TOOL_DASH;
	public static StaticAnimation KATANA_AUTO1;
	public static StaticAnimation KATANA_AUTO2;
	public static StaticAnimation KATANA_AUTO3;
	public static StaticAnimation KATANA_AIR_SLASH;
	public static StaticAnimation KATANA_SHEATHING_AUTO;
	public static StaticAnimation KATANA_SHEATHING_DASH;
	public static StaticAnimation KATANA_SHEATH_AIR_SLASH;
	public static StaticAnimation KATANA_GUARD;
	public static StaticAnimation KATANA_GUARD_HIT;
	public static StaticAnimation SWORD_MOUNT_ATTACK;
	public static StaticAnimation GREATSWORD_AUTO1;
	public static StaticAnimation GREATSWORD_AUTO2;
	public static StaticAnimation GREATSWORD_DASH;
	public static StaticAnimation GREATSWORD_AIR_SLASH;
	public static StaticAnimation GREATSWORD_GUARD;
	public static StaticAnimation GREATSWORD_GUARD_HIT;
	public static StaticAnimation DAGGER_AUTO1;
	public static StaticAnimation DAGGER_AUTO2;
	public static StaticAnimation DAGGER_AUTO3;
	public static StaticAnimation DAGGER_AIR_SLASH;
	public static StaticAnimation DAGGER_DUAL_AUTO1;
	public static StaticAnimation DAGGER_DUAL_AUTO2;
	public static StaticAnimation DAGGER_DUAL_AUTO3;
	public static StaticAnimation DAGGER_DUAL_AUTO4;
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
	public static StaticAnimation OFF_ANIMATION_HIGHEST;
	public static StaticAnimation OFF_ANIMATION_MIDDLE;
	
	public static void registerAnimations(AnimationRegistryEvent event) {
		event.getRegistryMap().put(EpicFightMod.MODID, Animations::build);
	}
	
	private static void build() {
		Models<?> models = FMLEnvironment.dist == Dist.CLIENT ? ClientModels.LOGICAL_CLIENT : Models.LOGICAL_SERVER;
		Model biped = models.biped;
		Model crepper = models.creeper;
		Model enderman = models.enderman;
		Model spider = models.spider;
		Model hoglin = models.hoglin;
		Model iron_golem = models.ironGolem;
		Model piglin = models.piglin;
		Model ravager = models.ravager;
		Model vex = models.vex;
		Model dragon = models.dragon;
		Model wither = models.wither;
		
		BIPED_IDLE = new StaticAnimation(true, "biped/living/idle", biped);
		BIPED_WALK = new MovementAnimation(true, "biped/living/walk", biped);
		BIPED_FLYING = new StaticAnimation(true, "biped/living/fly", biped);
		BIPED_HOLD_CROSSBOW = new StaticAnimation(true, "biped/living/hold_crossbow", biped);
		BIPED_RUN = new MovementAnimation(true, "biped/living/run", biped);
		BIPED_SNEAK = new MovementAnimation(true, "biped/living/sneak", biped);
		BIPED_SWIM = new MovementAnimation(true, "biped/living/swim", biped);
		BIPED_FLOAT = new StaticAnimation(true, "biped/living/float", biped);
		BIPED_KNEEL = new StaticAnimation(true, "biped/living/kneel", biped);
		BIPED_FALL = new StaticAnimation(false, "biped/living/fall", biped);
		BIPED_MOUNT = new StaticAnimation(true, "biped/living/mount", biped);
		BIPED_DIG = new StaticAnimation(0.11F, true, "biped/living/dig", biped);
		BIPED_BOW_AIM = new AimAnimation(false, "biped/combat/bow_aim_mid", "biped/combat/bow_aim_up", "biped/combat/bow_aim_down", "biped/combat/bow_aim_lying", biped);
		BIPED_BOW_SHOT = new ReboundAnimation(0.04F, false, "biped/combat/bow_shot_mid", "biped/combat/bow_shot_up", "biped/combat/bow_shot_down", "biped/combat/bow_shot_lying", biped);
		BIPED_CROSSBOW_AIM = new AimAnimation(false, "biped/combat/crossbow_aim_mid", "biped/combat/crossbow_aim_up", "biped/combat/crossbow_aim_down", "biped/combat/crossbow_aim_lying", biped);
		BIPED_CROSSBOW_SHOT = new ReboundAnimation(false, "biped/combat/crossbow_shot_mid", "biped/combat/crossbow_shot_up", "biped/combat/crossbow_shot_down", "biped/combat/crossbow_shot_lying", biped);
		BIPED_CROSSBOW_RELOAD = new StaticAnimation(false, "biped/combat/crossbow_reload", biped);
		BIPED_JUMP = new StaticAnimation(0.083F, false, "biped/living/jump", biped);
		BIPED_RUN_SPEAR = new MovementAnimation(true, "biped/living/run_holding_weapon", biped);
		BIPED_BLOCK = new MirrorAnimation(0.25F, true, "biped/living/shield", "biped/living/shield_mirror", biped);
		BIPED_HOLD_GREATSWORD = new StaticAnimation(true, "biped/living/hold_greatsword", biped);
		BIPED_HOLD_KATANA_SHEATHING = new StaticAnimation(true, "biped/living/hold_katana_sheath", biped);
		BIPED_HOLD_KATANA = new StaticAnimation(true, "biped/living/hold_katana", biped);
		BIPED_WALK_UNSHEATHING = new MovementAnimation(true, "biped/living/walk_unsheath", biped);
		BIPED_WALK_TWOHAND = new MovementAnimation(true, "biped/living/walk_twohand", biped);
		BIPED_RUN_UNSHEATHING = new MovementAnimation(true, "biped/living/run_katana", biped);
		
		BIPED_KATANA_SCRAP = new StaticAnimation(false, "biped/living/katana_scrap", biped)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[] {Event.create(0.15F, ReuseableEvents.KATANA_IN, Event.Side.CLIENT)});
		
		BIPED_HOLD_TACHI = new StaticAnimation(true, "biped/living/hold_tachi", biped);
		
		BIPED_HOLD_LONGSWORD = new StaticAnimation(true, "biped/living/hold_longsword", biped);
		BIPED_HOLD_SPEAR = new StaticAnimation(true, "biped/living/hold_spear", biped);
		BIPED_CLIMBING = new MovementAnimation(0.16F, true, "biped/living/climb", biped)
				.addProperty(StaticAnimationProperty.PLAY_SPEED, 1.0F);
		BIPED_SLEEPING = new StaticAnimation(0.16F, true, "biped/living/sleep", biped);
		
		BIPED_JAVELIN_AIM = new AimAnimation(false, "biped/combat/javelin_aim_mid", "biped/combat/javelin_aim_up", "biped/combat/javelin_aim_down", "biped/combat/javelin_aim_lying", biped);
		BIPED_JAVELIN_THROW = new ReboundAnimation(0.08F, false, "biped/combat/javelin_throw_mid", "biped/combat/javelin_throw_up", "biped/combat/javelin_throw_down", "biped/combat/javelin_throw_lying", biped);
		
		OFF_ANIMATION_HIGHEST = new OffAnimation("off_highest");
		OFF_ANIMATION_MIDDLE = new OffAnimation("off_middle");
		
		ZOMBIE_IDLE = new StaticAnimation(true, "zombie/idle", biped);
		ZOMBIE_WALK = new MovementAnimation(true, "zombie/walk", biped);
		ZOMBIE_CHASE = new MovementAnimation(true, "zombie/chase", biped);
		
		CREEPER_IDLE = new StaticAnimation(true, "creeper/idle", crepper);
		CREEPER_WALK = new MovementAnimation(true, "creeper/walk", crepper);
		
		ENDERMAN_IDLE = new StaticAnimation(true, "enderman/idle", enderman);
		ENDERMAN_WALK = new MovementAnimation(true, "enderman/walk", enderman);
		ENDERMAN_RAGE_IDLE = new StaticAnimation(true, "enderman/rage_idle", enderman);
		ENDERMAN_RAGE_WALK = new MovementAnimation(true, "enderman/rage_walk", enderman);
		
		WITHER_SKELETON_WALK = new MovementAnimation(true, "wither_skeleton/walk", biped);
		WITHER_SKELETON_CHASE = new MovementAnimation(0.36F, true, "wither_skeleton/chase", biped);
		WITHER_SKELETON_IDLE = new StaticAnimation(true, "wither_skeleton/idle", biped);
		WITHER_SKELETON_SPECIAL_SPAWN = new InvincibleAnimation(0.0F, "wither_skeleton/special_spawn", biped);
		
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
		RAVAGER_WALK = new MovementAnimation(true, "ravager/walk", ravager);
		
		VEX_IDLE = new StaticAnimation(true, "vex/idle", vex);
		VEX_FLIPPING = new StaticAnimation(0.05F, true, "vex/flip", vex);
		
		PIGLIN_IDLE = new StaticAnimation(true, "piglin/idle", piglin);
		PIGLIN_WALK = new MovementAnimation(true, "piglin/walk", piglin);
		PIGLIN_ZOMBIFIED_IDLE = new StaticAnimation(true, "piglin/zombified_idle", piglin);
		PIGLIN_ZOMBIFIED_WALK = new MovementAnimation(true, "piglin/zombified_walk", piglin);
		PIGLIN_ZOMBIFIED_CHASE = new MovementAnimation(true, "piglin/zombified_chase", piglin);
		PIGLIN_CELEBRATE1 = new StaticAnimation(true, "piglin/celebrate1", piglin);
		PIGLIN_CELEBRATE2 = new StaticAnimation(true, "piglin/celebrate2", piglin);
		PIGLIN_CELEBRATE3 = new StaticAnimation(true, "piglin/celebrate3", piglin);
		PIGLIN_ADMIRE = new StaticAnimation(true, "piglin/admire", piglin);
		
		WITHER_IDLE = new StaticAnimation(true, "wither/idle", wither);
		
		SPEAR_GUARD = new StaticAnimation(true, "biped/skill/guard_spear", biped);
		SWORD_GUARD = new StaticAnimation(true, "biped/skill/guard_sword", biped);
		SWORD_DUAL_GUARD = new StaticAnimation(true, "biped/skill/guard_dualsword", biped);
		GREATSWORD_GUARD = new StaticAnimation(0.25F, true, "biped/skill/guard_greatsword", biped);
		KATANA_GUARD = new StaticAnimation(0.25F, true, "biped/skill/guard_katana", biped);
		LONGSWORD_GUARD = new StaticAnimation(0.25F, true, "biped/skill/guard_longsword", biped);
		
		BIPED_ROLL_FORWARD = new DodgeAnimation(0.1F, "biped/skill/roll_forward", 0.6F, 0.8F, biped);
		BIPED_ROLL_BACKWARD = new DodgeAnimation(0.1F, "biped/skill/roll_backward", 0.6F, 0.8F, biped);
		BIPED_STEP_FORWARD = new DodgeAnimation(0.05F, "biped/skill/step_forward", 0.6F, 1.65F, biped);
		BIPED_STEP_BACKWARD = new DodgeAnimation(0.05F, "biped/skill/step_backward", 0.6F, 1.65F, biped);
		BIPED_STEP_LEFT = new DodgeAnimation(0.05F, "biped/skill/step_left", 0.6F, 1.65F, biped);
		BIPED_STEP_RIGHT = new DodgeAnimation(0.05F, "biped/skill/step_right", 0.6F, 1.65F, biped);
		
		BIPED_KNOCKDOWN_WAKEUP_LEFT = new DodgeAnimation(0.1F, "biped/skill/knockdown_wakeup_left", 0.8F, 0.6F, biped);
		BIPED_KNOCKDOWN_WAKEUP_RIGHT = new DodgeAnimation(0.1F, "biped/skill/knockdown_wakeup_right", 0.8F, 0.6F, biped);
		
		FIST_AUTO1 = new BasicAttackAnimation(0.08F, 0.0F, 0.11F, 0.16F, InteractionHand.OFF_HAND, null, "Tool_L", "biped/combat/fist_auto1", biped)
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT);
		FIST_AUTO2 = new BasicAttackAnimation(0.08F, 0.0F, 0.11F, 0.16F, null, "Tool_R", "biped/combat/fist_auto2", biped)
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT);
		FIST_AUTO3 = new BasicAttackAnimation(0.08F, 0.05F, 0.16F, 0.5F, InteractionHand.OFF_HAND, null, "Tool_L", "biped/combat/fist_auto3", biped)
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT);
		FIST_DASH = new DashAttackAnimation(0.06F, 0.05F, 0.15F, 0.3F, 0.7F, null, "Shoulder_R", "biped/combat/fist_dash", biped)
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.HIT_BLUNT)
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.addProperty(StaticAnimationProperty.PLAY_SPEED, 1.0F);
		SWORD_AUTO1 = new BasicAttackAnimation(0.13F, 0.0F, 0.11F, 0.3F, null, "Tool_R", "biped/combat/sword_auto1", biped);
		SWORD_AUTO2 = new BasicAttackAnimation(0.13F, 0.0F, 0.11F, 0.3F, null, "Tool_R", "biped/combat/sword_auto2", biped);
		SWORD_AUTO3 = new BasicAttackAnimation(0.13F, 0.0F, 0.11F, 0.6F, null, "Tool_R", "biped/combat/sword_auto3", biped);
		SWORD_DASH = new DashAttackAnimation(0.12F, 0.1F, 0.25F, 0.4F, 0.65F, null, "Tool_R", "biped/combat/sword_dash", biped)
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.6F);
		GREATSWORD_AUTO1 = new BasicAttackAnimation(0.2F, 0.4F, 0.6F, 0.8F, null, "Tool_R", "biped/combat/greatsword_auto1", biped);
		GREATSWORD_AUTO2 = new BasicAttackAnimation(0.2F, 0.4F, 0.6F, 0.8F, null, "Tool_R", "biped/combat/greatsword_auto2", biped);
		GREATSWORD_DASH = new DashAttackAnimation(0.11F, 0.4F, 0.65F, 0.8F, 1.2F, null, "Tool_R", "biped/combat/greatsword_dash", false, biped)
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.addProperty(AttackPhaseProperty.FINISHER, true);
		SPEAR_ONEHAND_AUTO = new BasicAttackAnimation(0.16F, 0.1F, 0.2F, 0.45F, null, "Tool_R", "biped/combat/spear_onehand_auto", biped);
		SPEAR_TWOHAND_AUTO1 = new BasicAttackAnimation(0.25F, 0.05F, 0.15F, 0.45F, null, "Tool_R", "biped/combat/spear_twohand_auto1", biped);
		SPEAR_TWOHAND_AUTO2 = new BasicAttackAnimation(0.25F, 0.05F, 0.15F, 0.45F, null, "Tool_R", "biped/combat/spear_twohand_auto2", biped);
		SPEAR_DASH = new DashAttackAnimation(0.16F, 0.05F, 0.2F, 0.3F, 0.7F, null, "Tool_R", "biped/combat/spear_dash", biped)
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true);
		TOOL_AUTO1 = new BasicAttackAnimation(0.13F, 0.05F, 0.15F, 0.3F, null, "Tool_R", String.valueOf(SWORD_AUTO1.getId()), biped);
		TOOL_AUTO2 = new BasicAttackAnimation(0.13F, 0.05F, 0.15F, 0.4F, null, "Tool_R", "biped/combat/sword_auto4", biped);
		TOOL_DASH = new DashAttackAnimation(0.16F, 0.08F, 0.15F, 0.25F, 0.58F, null, "Tool_R", "biped/combat/tool_dash", biped)
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.addProperty(AttackPhaseProperty.MAX_STRIKES, ValueCorrector.adder(1));
		AXE_DASH = new DashAttackAnimation(0.25F, 0.08F, 0.4F, 0.46F, 0.9F, null, "Tool_R", "biped/combat/axe_dash", biped)
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true);
		SWORD_DUAL_AUTO1 = new BasicAttackAnimation(0.16F, 0.0F, 0.11F, 0.2F, null, "Tool_R", "biped/combat/sword_dual_auto1", biped);
		SWORD_DUAL_AUTO2 = new BasicAttackAnimation(0.13F, 0.0F, 0.11F, 0.15F, InteractionHand.OFF_HAND, null, "Tool_L", "biped/combat/sword_dual_auto2", biped);
		SWORD_DUAL_AUTO3 = new BasicAttackAnimation(0.18F, 0.0F, 0.25F, 0.35F, 0.6F, ColliderPreset.DUAL_SWORD, "Torso", "biped/combat/sword_dual_auto3", biped);
		SWORD_DUAL_DASH = new DashAttackAnimation(0.16F, 0.05F, 0.05F, 0.3F, 0.75F, ColliderPreset.DUAL_SWORD_DASH, "Root", "biped/combat/sword_dual_dash", biped)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.6F)
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true);
		KATANA_AUTO1 = new BasicAttackAnimation(0.06F, 0.05F, 0.16F, 0.2F, null, "Tool_R", "biped/combat/katana_auto1", biped);
		KATANA_AUTO2 = new BasicAttackAnimation(0.16F, 0.0F, 0.11F, 0.2F, null, "Tool_R", "biped/combat/katana_auto2", biped);
		KATANA_AUTO3 = new BasicAttackAnimation(0.06F, 0.1F, 0.21F, 0.59F, null, "Tool_R", "biped/combat/katana_auto3", biped);
		KATANA_SHEATHING_AUTO = new BasicAttackAnimation(0.06F, 0.0F, 0.06F, 0.65F, ColliderPreset.FATAL_DRAW, "Root", "biped/combat/katana_sheath_auto", biped)
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.addProperty(AttackPhaseProperty.ARMOR_NEGATION, ValueCorrector.adder(30.0F))
				.addProperty(AttackPhaseProperty.DAMAGE, ValueCorrector.multiplier(2.0F))
				.addProperty(AttackPhaseProperty.MAX_STRIKES, ValueCorrector.adder(2))
				.addProperty(AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_SHARP);
		KATANA_SHEATHING_DASH = new DashAttackAnimation(0.06F, 0.05F, 0.05F, 0.11F, 0.65F, null, "Tool_R", "biped/combat/katana_sheath_dash", biped)
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.addProperty(AttackPhaseProperty.ARMOR_NEGATION, ValueCorrector.adder(30.0F))
				.addProperty(AttackPhaseProperty.DAMAGE, ValueCorrector.multiplier(2.0F))
				.addProperty(AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_SHARP);
		AXE_AUTO1 = new BasicAttackAnimation(0.16F, 0.05F, 0.16F, 0.7F, null, "Tool_R", "biped/combat/axe_auto1", biped);
		AXE_AUTO2 = new BasicAttackAnimation(0.16F, 0.05F, 0.16F, 0.85F, null, "Tool_R", "biped/combat/axe_auto2", biped);
		LONGSWORD_AUTO1 = new BasicAttackAnimation(0.1F, 0.2F, 0.3F, 0.45F, null, "Tool_R", "biped/combat/longsword_auto1", biped);
		LONGSWORD_AUTO2 = new BasicAttackAnimation(0.15F, 0.1F, 0.21F, 0.45F, null, "Tool_R", "biped/combat/longsword_auto2", biped);
		LONGSWORD_AUTO3 = new BasicAttackAnimation(0.15F, 0.05F, 0.16F, 0.8F, null, "Tool_R", "biped/combat/longsword_auto3", biped);
		LONGSWORD_DASH = new DashAttackAnimation(0.15F, 0.1F, 0.3F, 0.5F, 0.7F, null, "Tool_R", "biped/combat/longsword_dash", biped)
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true);
		TACHI_DASH = new DashAttackAnimation(0.15F, 0.1F, 0.2F, 0.45F, 0.7F, null, "Tool_R", "biped/combat/tachi_dash", false, biped)
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true);
		DAGGER_AUTO1 = new BasicAttackAnimation(0.08F, 0.05F, 0.15F, 0.2F, null, "Tool_R", "biped/combat/dagger_auto1", biped);
		DAGGER_AUTO2 = new BasicAttackAnimation(0.08F, 0.0F, 0.1F, 0.2F, null, "Tool_R", "biped/combat/dagger_auto2", biped);
		DAGGER_AUTO3 = new BasicAttackAnimation(0.08F, 0.15F, 0.26F, 0.5F, null, "Tool_R", "biped/combat/dagger_auto3", biped);
		DAGGER_DUAL_AUTO1 = new BasicAttackAnimation(0.08F, 0.05F, 0.16F, 0.25F, null, "Tool_R", "biped/combat/dagger_dual_auto1", biped);
		DAGGER_DUAL_AUTO2 = new BasicAttackAnimation(0.08F, 0.0F, 0.11F, 0.16F, InteractionHand.OFF_HAND, null, "Tool_L", "biped/combat/dagger_dual_auto2", biped);
		DAGGER_DUAL_AUTO3 = new BasicAttackAnimation(0.08F, 0.0F, 0.11F, 0.2F, null, "Tool_R", "biped/combat/dagger_dual_auto3", biped);
		DAGGER_DUAL_AUTO4 = new BasicAttackAnimation(0.13F, 0.1F, 0.21F, 0.4F, ColliderPreset.DUAL_DAGGER_DASH, "Root", "biped/combat/dagger_dual_auto4", biped);
		DAGGER_DUAL_DASH = new DashAttackAnimation(0.1F, 0.1F, 0.25F, 0.3F, 0.65F, ColliderPreset.DUAL_DAGGER_DASH, "Root", "biped/combat/dagger_dual_dash", biped)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.4F)
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true);
		SWORD_AIR_SLASH = new AirSlashAnimation(0.1F, 0.15F, 0.26F, 0.5F, null, "Tool_R", "biped/combat/sword_airslash", biped);
		SWORD_DUAL_AIR_SLASH = new AirSlashAnimation(0.1F, 0.15F, 0.26F, 0.5F, ColliderPreset.DUAL_SWORD_AIR_SLASH, "Torso", "biped/combat/sword_dual_airslash", biped);
		KATANA_AIR_SLASH = new AirSlashAnimation(0.1F, 0.05F, 0.16F, 0.3F, null, "Tool_R", "biped/combat/katana_airslash", biped);
		KATANA_SHEATH_AIR_SLASH = new AirSlashAnimation(0.1F, 0.1F, 0.16F, 0.3F, null, "Tool_R", "biped/combat/katana_sheath_airslash", biped)
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.addProperty(AttackPhaseProperty.ARMOR_NEGATION, ValueCorrector.adder(30.0F))
				.addProperty(AttackPhaseProperty.MAX_STRIKES, ValueCorrector.adder(2))
				.addProperty(AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_SHARP)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.0F);
		SPEAR_ONEHAND_AIR_SLASH = new AirSlashAnimation(0.1F, 0.15F, 0.26F, 0.4F, null, "Tool_R", "biped/combat/spear_onehand_airslash", biped);
		SPEAR_TWOHAND_AIR_SLASH = new AirSlashAnimation(0.1F, 0.25F, 0.36F, 0.6F, null, "Tool_R", "biped/combat/spear_twohand_airslash", biped)
				.addProperty(AttackPhaseProperty.FINISHER, true);
		LONGSWORD_AIR_SLASH = new AirSlashAnimation(0.1F, 0.3F, 0.41F, 0.5F, null, "Tool_R", "biped/combat/longsword_airslash", biped);
		GREATSWORD_AIR_SLASH = new AirSlashAnimation(0.1F, 0.5F, 0.55F, 0.71F, 0.75F, false, null, "Tool_R", "biped/combat/greatsword_airslash", biped)
				.addProperty(AttackPhaseProperty.FINISHER, true);
		FIST_AIR_SLASH = new AirSlashAnimation(0.1F, 0.15F, 0.26F, 0.4F, null, "Tool_R", "biped/combat/fist_airslash", biped)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 4.0F);
		DAGGER_AIR_SLASH = new AirSlashAnimation(0.1F, 0.15F, 0.26F, 0.45F, null, "Tool_R", "biped/combat/dagger_airslash", biped)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.4F);
		DAGGER_DUAL_AIR_SLASH = new AirSlashAnimation(0.1F, 0.15F, 0.26F, 0.4F, ColliderPreset.DUAL_DAGGER_AIR_SLASH, "Torso", String.valueOf(SWORD_DUAL_AIR_SLASH.getId()), biped)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.0F);
		AXE_AIRSLASH = new AirSlashAnimation(0.1F, 0.3F, 0.4F, 0.65F, null, "Tool_R", "biped/combat/axe_airslash", biped);
		
		SWORD_MOUNT_ATTACK = new MountAttackAnimation(0.16F, 0.1F, 0.2F, 0.25F, 0.7F, null, "Tool_R", "biped/combat/sword_mount_attack", biped);
		SPEAR_MOUNT_ATTACK = new MountAttackAnimation(0.16F, 0.38F, 0.38F, 0.45F, 0.8F, null, "Tool_R", "biped/combat/spear_mount_attack", biped)
				.addProperty(AttackAnimationProperty.ROTATE_X, true);
		
		BIPED_MOB_ONEHAND1 = new AttackAnimation(0.08F, 0.45F, 0.55F, 0.66F, 0.95F, null, "Tool_R", "biped/combat/mob_onehand1", biped)
				.addProperty(AttackAnimationProperty.ROTATE_X, true);
		BIPED_MOB_ONEHAND2 = new AttackAnimation(0.08F, 0.45F, 0.5F, 0.61F, 0.95F, null, "Tool_R", "biped/combat/mob_onehand2", biped)
				.addProperty(AttackAnimationProperty.ROTATE_X, true);
		BIPED_MOB_GREATSWORD = new AttackAnimation(0.15F, 0.45F, 0.85F, 0.95F, 2.2F, null, "Tool_R", "biped/combat/mob_greatsword1", biped)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN)
				.addProperty(AttackAnimationProperty.ROTATE_X, true);
		BIPED_MOB_TACHI = new AttackAnimation(0.15F, 0.15F, 0.25F, 0.35F, 1.0F, null, "Tool_R", "biped/combat/mob_tachi_special", biped)
				.addProperty(AttackAnimationProperty.ROTATE_X, true);
		BIPED_MOB_SPEAR_ONEHAND = new AttackAnimation(0.15F, 0.15F, 0.4F, 0.5F, 1.1F, null, "Tool_R", "biped/combat/mob_spear_onehand", biped)
				.addProperty(AttackAnimationProperty.ROTATE_X, true);
		BIPED_MOB_SPEAR_TWOHAND1 = new AttackAnimation(0.15F, 0.15F, 0.4F, 0.5F, 1.0F, null, "Tool_R", "biped/combat/mob_spear_twohand1", biped)
				.addProperty(AttackAnimationProperty.ROTATE_X, true);
		BIPED_MOB_SPEAR_TWOHAND2 = new AttackAnimation(0.15F, 0.15F, 0.4F, 0.5F, 1.0F, null, "Tool_R", "biped/combat/mob_spear_twohand2", biped)
				.addProperty(AttackAnimationProperty.ROTATE_X, true);
		BIPED_MOB_SPEAR_TWOHAND3 = new AttackAnimation(0.15F, 0.15F, 0.4F, 0.5F, 1.0F, null, "Tool_R", "biped/combat/mob_spear_twohand3", biped)
				.addProperty(AttackAnimationProperty.ROTATE_X, true);
		BIPED_MOB_SWORD_DUAL1 = new AttackAnimation(0.1F, "biped/combat/mob_sword_dual1", biped,
				new Phase(0.0F, 0.35F, 0.4F, 0.5F, 0.55F, 0.55F, InteractionHand.OFF_HAND, "Tool_L", null), new Phase(0.55F, 0.55F, 0.65F, 0.75F, 1.15F, Float.MAX_VALUE, "Tool_R", null))
				.addProperty(AttackAnimationProperty.ROTATE_X, true);
		BIPED_MOB_SWORD_DUAL2 = new AttackAnimation(0.1F, "biped/combat/mob_sword_dual2", biped,
				new Phase(0.0F, 0.3F, 0.3F, 0.45F, 0.55F, 0.55F, InteractionHand.OFF_HAND, "Tool_L", null), new Phase(0.55F, 0.55F, 0.65F, 0.75F, 1.15F, Float.MAX_VALUE, "Tool_R", null))
				.addProperty(AttackAnimationProperty.ROTATE_X, true);
		BIPED_MOB_SWORD_DUAL3 = new AttackAnimation(0.1F, 0.25F, 0.85F, 0.95F, 1.4F, null, "Tool_R", "biped/combat/mob_sword_dual3", biped)
				.addProperty(ActionAnimationProperty.MOVE_VERTICAL, true);
		BIPED_MOB_LONGSWORD1 = new AttackAnimation(0.15F, "biped/combat/mob_longsword1", biped,
				new Phase(0.0F, 0.15F, 0.25F, 0.35F, 0.45F, 0.65F, "Tool_R", null), new Phase(0.65F, 0.85F, 1.0F, 1.1F, 1.55F, Float.MAX_VALUE, "Tool_R", null))
				.addProperty(AttackAnimationProperty.ROTATE_X, true);
		BIPED_MOB_LONGSWORD2 = new AttackAnimation(0.25F, 0.3F, 0.45F, 0.55F, 1.0F, null, "Tool_R", "biped/combat/mob_longsword2", biped)
				.addProperty(AttackAnimationProperty.ROTATE_X, true);
		BIPED_MOB_KATANA1 = new AttackAnimation(0.05F, 0.3F, 0.2F, 0.3F, 0.7F, null, "Tool_R", "biped/combat/mob_katana1", biped)
				.addProperty(AttackAnimationProperty.ROTATE_X, true);
		BIPED_MOB_KATANA2 = new AttackAnimation(0.15F, 0.01F, 0.01F, 0.1F, 0.55F, null, "Tool_R", "biped/combat/mob_katana2", biped)
				.addProperty(AttackAnimationProperty.ROTATE_X, true);
		BIPED_MOB_KATANA3 = new AttackAnimation(0.15F, 0.01F, 0.1F, 0.2F, 0.7F, null, "Tool_R", "biped/combat/mob_katana3", biped)
				.addProperty(AttackAnimationProperty.ROTATE_X, true);
		BIPED_MOB_DAGGER_ONEHAND1 = new AttackAnimation(0.1F, 0.05F, 0.15F, 0.25F, 0.4F, null, "Tool_R", "biped/combat/mob_dagger_onehand1", biped)
				.addProperty(AttackAnimationProperty.ROTATE_X, true);
		BIPED_MOB_DAGGER_ONEHAND2 = new AttackAnimation(0.1F, 0.05F, 0.01F, 0.1F, 0.45F, null, "Tool_R", "biped/combat/mob_dagger_onehand2", biped)
				.addProperty(AttackAnimationProperty.ROTATE_X, true);
		BIPED_MOB_DAGGER_ONEHAND3 = new AttackAnimation(0.1F, 0.3F, 0.5F, 0.6F, 0.9F, null, "Tool_R", "biped/combat/mob_dagger_onehand3", biped)
				.addProperty(AttackAnimationProperty.ROTATE_X, true);
		BIPED_MOB_DAGGER_TWOHAND1 = new AttackAnimation(0.15F, "biped/combat/mob_dagger_twohand1", biped,
				new Phase(0.0F, 0.0F, 0.05F, 0.15F, 0.3F, 0.3F, "Tool_R", null), new Phase(0.3F, 0.3F, 0.3F, 0.4F, 0.5F, 0.5F, InteractionHand.OFF_HAND, "Tool_L", null)
				,new Phase(0.5F, 0.5F, 0.55F, 0.65F, 1.0F, Float.MAX_VALUE, "Tool_R", null))
				.addProperty(AttackAnimationProperty.ROTATE_X, true);
		BIPED_MOB_DAGGER_TWOHAND2 = new AttackAnimation(0.1F, 0.25F, 0.75F, 0.85F, 1.0F, null, "Tool_R", "biped/combat/mob_dagger_twohand2", biped)
				.addProperty(AttackAnimationProperty.ROTATE_X, true);
		BIPED_MOB_THROW = new RangedAttackAnimation(0.11F, 0.1F, 0.45F, 0.49F, 0.95F, null, "Root", "biped/combat/mob_throw", biped);
		
		SWORD_GUARD_HIT = new GuardAnimation(0.05F, "biped/skill/guard_sword_hit", biped);
		SWORD_GUARD_ACTIVE_HIT1 = new GuardAnimation(0.05F, 0.2F, "biped/skill/guard_sword_hit_active1", biped);
		SWORD_GUARD_ACTIVE_HIT2 = new GuardAnimation(0.05F, 0.2F, "biped/skill/guard_sword_hit_active2", biped);
		SWORD_GUARD_ACTIVE_HIT3 = new GuardAnimation(0.05F, 0.2F, "biped/skill/guard_sword_hit_active3", biped);
		
		LONGSWORD_GUARD_ACTIVE_HIT1 = new GuardAnimation(0.05F, 0.2F, "biped/skill/guard_longsword_hit_active1", biped);
		LONGSWORD_GUARD_ACTIVE_HIT2 = new GuardAnimation(0.05F, 0.2F, "biped/skill/guard_longsword_hit_active2", biped);
		
		SWORD_DUAL_GUARD_HIT = new GuardAnimation(0.05F, "biped/skill/guard_dualsword_hit", biped);
		COMMON_GUARD_BREAK = new LongHitAnimation(0.05F, "biped/skill/guard_break1", biped);
		GREATSWORD_GUARD_BREAK = new LongHitAnimation(0.05F, "biped/skill/guard_break2", biped);
		
		LONGSWORD_GUARD_HIT = new GuardAnimation(0.05F, "biped/skill/guard_longsword_hit", biped);
		SPEAR_GUARD_HIT = new GuardAnimation(0.05F, "biped/skill/guard_spear_hit", biped);
		GREATSWORD_GUARD_HIT = new GuardAnimation(0.05F, "biped/skill/guard_greatsword_hit", biped);
		KATANA_GUARD_HIT = new GuardAnimation(0.05F, "biped/skill/guard_katana_hit", biped);
		
		BIPED_HIT_SHORT = new HitAnimation(0.05F, "biped/combat/hit_short", biped);
		BIPED_HIT_LONG = new LongHitAnimation(0.08F, "biped/combat/hit_long", biped);
		BIPED_HIT_ON_MOUNT = new LongHitAnimation(0.08F, "biped/combat/hit_on_mount", biped);
		BIPED_LANDING = new LongHitAnimation(0.08F, "biped/living/landing", biped);
		BIPED_KNOCKDOWN = new KnockdownAnimation(0.08F, 2.1F, "biped/combat/knockdown", biped);
		BIPED_DEATH = new LongHitAnimation(0.16F, "biped/living/death", biped);
		
		CREEPER_HIT_SHORT = new HitAnimation(0.05F, "creeper/hit_short", crepper);
		CREEPER_HIT_LONG = new LongHitAnimation(0.08F, "creeper/hit_long", crepper);
		CREEPER_DEATH = new LongHitAnimation(0.16F, "creeper/death", crepper);
		
		ENDERMAN_HIT_SHORT = new HitAnimation(0.05F, "enderman/hit_short", enderman);
		ENDERMAN_HIT_LONG = new LongHitAnimation(0.08F, "enderman/hit_long", enderman);
		ENDERMAN_CONVERT_RAGE = new DodgeAnimation(0.16F, 0.0F, "enderman/convert_rage", -1.0F, -1.0F, enderman);
		ENDERMAN_TP_KICK1 = new AttackAnimation(0.06F, 0.15F, 0.3F, 0.4F, 1.0F, ColliderPreset.ENDERMAN_LIMB, "Leg_R", "enderman/tp_kick1", enderman);
		ENDERMAN_TP_KICK2 = new AttackAnimation(0.16F, 0.15F, 0.25F, 0.45F, 1.0F, ColliderPreset.ENDERMAN_LIMB, "Leg_R", "enderman/tp_kick2", enderman);
		ENDERMAN_KICK1 = new AttackAnimation(0.16F, 0.66F, 0.7F, 0.81F, 1.6F, ColliderPreset.ENDERMAN_LIMB, "Leg_L", "enderman/rush_kick", enderman)
				.addProperty(AttackPhaseProperty.IMPACT, ValueCorrector.setter(4.0F));
		ENDERMAN_KICK2 = new AttackAnimation(0.16F, 0.8F, 0.8F, 0.9F, 1.3F, ColliderPreset.ENDERMAN_LIMB, "Leg_R", "enderman/jump_kick", enderman);
		ENDERMAN_KNEE = new AttackAnimation(0.16F, 0.25F, 0.25F, 0.31F, 1.0F, ColliderPreset.FIST, "Leg_R", "enderman/knee", enderman)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.LONG);
		ENDERMAN_KICK_COMBO = new AttackAnimation(0.1F, "enderman/kick_twice", enderman,
					new Phase(0.0F, 0.15F, 0.15F, 0.21F, 0.46F, 0.6F, "Leg_R", ColliderPreset.ENDERMAN_LIMB),
					new Phase(0.6F, 0.75F, 0.75F, 0.81F, 1.6F, Float.MAX_VALUE, "Leg_L", ColliderPreset.ENDERMAN_LIMB))
				.addProperty(ActionAnimationProperty.STOP_MOVEMENT, true);
		ENDERMAN_GRASP = new AttackAnimation(0.06F, 0.5F, 0.45F, 1.0F, 1.0F, ColliderPreset.ENDERMAN_LIMB, "Tool_R", "enderman/grasp", enderman)
				.addProperty(AttackAnimationProperty.ROTATE_X, true);
		ENDERMAN_DEATH = new LongHitAnimation(0.16F, "enderman/death", enderman);
		ENDERMAN_TP_EMERGENCE = new ActionAnimation(0.05F, "enderman/teleport", enderman)
				.addProperty(ActionAnimationProperty.STOP_MOVEMENT, true);
		
		DRAGON_IDLE = new StaticAnimation(0.6F, true, "dragon/idle", dragon);
		DRAGON_WALK = new EnderDraonWalkAnimation(0.35F, "dragon/walk", dragon,
				new IKInfo[] {
					IKInfo.make("Leg_Front_L1", "Leg_Front_L3", "Leg_Front_R3", Pair.of(0, 3), 0.12F, 0, new boolean[] {true, true, true}),
					IKInfo.make("Leg_Front_R1", "Leg_Front_R3", "Leg_Front_L3", Pair.of(2, 5), 0.12F, 2, new boolean[] {true, true, true}),
					IKInfo.make("Leg_Back_L1", "Leg_Back_L3", "Leg_Back_R3", Pair.of(2, 5), 0.1344F, 4, new boolean[] {true, true, true}),
					IKInfo.make("Leg_Back_R1", "Leg_Back_R3", "Leg_Back_L3", Pair.of(0, 3), 0.1344F, 2, new boolean[] {true, true, true})
				});
		
		DRAGON_FLY = new StaticAnimation(0.35F, true, "dragon/fly", dragon)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[] {Event.create(0.4F, ReuseableEvents.WING_FLAP, Event.Side.CLIENT)});
		
		DRAGON_DEATH = new EnderDragonDeathAnimation(1.0F, "dragon/death", dragon);
		
		DRAGON_GROUND_TO_FLY = new EnderDragonActionAnimation(0.25F, "dragon/ground_to_fly", dragon, new IKInfo[] {
					IKInfo.make("Leg_Front_L1", "Leg_Front_L3", null, Pair.of(3, 7), 0.12F, 0, new boolean[] {true, false, false, false}),
					IKInfo.make("Leg_Front_R1", "Leg_Front_R3", null, Pair.of(3, 7), 0.12F, 0, new boolean[] {true, false, false, false}),
					IKInfo.make("Leg_Back_L1", "Leg_Back_L3", null, Pair.of(4, 7), 0.1344F, 0, new boolean[] {true, false, false, false}),
					IKInfo.make("Leg_Back_R1", "Leg_Back_R3", null, Pair.of(4, 7), 0.1344F, 0, new boolean[] {true, false, false, false})
				})
				.addProperty(ActionAnimationProperty.STOP_MOVEMENT, true)
				.addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[] {Event.create(0.25F, ReuseableEvents.WING_FLAP, Event.Side.CLIENT), Event.create(1.05F, ReuseableEvents.WING_FLAP, Event.Side.CLIENT), Event.create(1.45F, (entitypatch) -> {
					if (entitypatch instanceof EnderDragonPatch) {
						((EnderDragonPatch)entitypatch).setFlyingPhase();
					}
				}, Event.Side.BOTH)});
		
		DRAGON_FLY_TO_GROUND = new EnderDragonDynamicActionAnimation(0.35F, "dragon/fly_to_ground", dragon, new IKInfo[] {
					IKInfo.make("Leg_Front_L1", "Leg_Front_L3", null, Pair.of(0, 4), 0.12F, 9, new boolean[] {false, false, false, true}),
					IKInfo.make("Leg_Front_R1", "Leg_Front_R3", null, Pair.of(0, 4), 0.12F, 9, new boolean[] {false, false, false, true}),
					IKInfo.make("Leg_Back_L1", "Leg_Back_L3", null, Pair.of(0, 4), 0.1344F, 7, new boolean[] {false, false, false, true}),
					IKInfo.make("Leg_Back_R1", "Leg_Back_R3", null, Pair.of(0, 4), 0.1344F, 7, new boolean[] {false, false, false, true})
				})
				.addProperty(ActionAnimationProperty.STOP_MOVEMENT, true)
				.addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
				.addProperty(ActionAnimationProperty.MOVE_ON_LINK, false)
				.addProperty(ActionAnimationProperty.MOVE_TIME, new ActionTime[] {ActionTime.crate(0.0F, 1.35F)})
				.addProperty(ActionAnimationProperty.COORD_SET_BEGIN, (self, entitypatch, transformSheet) -> {
					if (entitypatch instanceof EnderDragonPatch) {
						TransformSheet transform = self.getTransfroms().get("Root").copyAll();
						Vec3 dragonpos = entitypatch.getOriginal().position();
						Vec3 targetpos = ((EnderDragon)entitypatch.getOriginal()).getPhaseManager().getPhase(PatchedPhases.LANDING).getLandingPosition();
						float horizontalDistance = (float) dragonpos.subtract(0, dragonpos.y, 0).distanceTo(targetpos.subtract(0, targetpos.y, 0));
						float verticalDistance = (float) Math.abs(dragonpos.y - targetpos.y);
						JointTransform jt0 = transform.getKeyframes()[0].transform();
						JointTransform jt1 = transform.getKeyframes()[1].transform();
						JointTransform jt2 = transform.getKeyframes()[2].transform();
						OpenMatrix4f coordReverse = OpenMatrix4f.createRotatorDeg(90F, Vec3f.X_AXIS);
						Vec3f jointCoord = OpenMatrix4f.transform3v(coordReverse, new Vec3f(jt0.translation().x, verticalDistance, horizontalDistance), null);
						jt0.translation().set(jointCoord);
						jt1.translation().set(MathUtils.lerpVector(jt0.translation(), jt2.translation(), transform.getKeyframes()[1].time()));
						transformSheet.readFrom(transform);
					}
				})
				.addProperty(StaticAnimationProperty.EVENTS, new Event[] {Event.create(0.3F, ReuseableEvents.WING_FLAP, Event.Side.CLIENT), Event.create(1.1F, (entitypatch) -> {
					entitypatch.playSound(EpicFightSounds.GROUND_SLAM, 0, 0);
					LivingEntity original = entitypatch.getOriginal();
					BlockPos blockpos = original.level.getHeightmapPos(Heightmap.Types.WORLD_SURFACE, original.blockPosition());
					original.level.addParticle(EpicFightParticles.GROUND_SLAM.get(), blockpos.getX(), blockpos.getY(), blockpos.getZ(), 3.0D, 0.0D, 1.0D);
				}, Event.Side.CLIENT), Event.create(1.1F, (entitypatch) -> {
					LivingEntity original = entitypatch.getOriginal();
					DamageSource extDamageSource = ExtendedDamageSource.causeMobDamage(original, StunType.KNOCKDOWN, DRAGON_FLY_TO_GROUND);
					
					for (Entity entity : original.level.getEntities(original, original.getBoundingBox().deflate(3.0D, 0.0D, 3.0D))) {
						entity.hurt(extDamageSource, 6.0F);
					}
				}, Event.Side.SERVER)});
		
		DRAGON_ATTACK1 = new EnderDragonAttackAnimation(0.35F, 0.4F, 0.65F, 0.76F, 1.9F, ColliderPreset.DRAGON_LEG, "Leg_Front_R3", "dragon/attack1", dragon, new IKInfo[] {
				IKInfo.make("Leg_Front_L1", "Leg_Front_L3", null, Pair.of(2, 4), 0.12F, 0, new boolean[] {true, true}),
				IKInfo.make("Leg_Front_R1", "Leg_Front_R3", null, Pair.of(0, 5), 0.12F, 0, new boolean[] {false, false, false, false, true}),
				IKInfo.make("Leg_Back_L1", "Leg_Back_L3", null, null, 0.1344F, 0, new boolean[] {}),
				IKInfo.make("Leg_Back_R1", "Leg_Back_R3", null, Pair.of(1, 4), 0.1344F, 0, new boolean[] {true, false, true})
			})
			.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN)
			.addProperty(StaticAnimationProperty.EVENTS, new Event[] {Event.create(0.65F, (entitypatch) -> {
				entitypatch.playSound(EpicFightSounds.GROUND_SLAM, 0, 0);
				
				if (entitypatch instanceof EnderDragonPatch) {
					EnderDragonPatch dragonpatch = ((EnderDragonPatch)entitypatch);
					Vec3f tipPosition = dragonpatch.getTipPointAnimation("Leg_Front_R3").getTargetPosition();
					LivingEntity original = entitypatch.getOriginal();
					original.level.addParticle(EpicFightParticles.GROUND_SLAM.get(), tipPosition.x, tipPosition.y, tipPosition.z, 0.5D, 0.0D, 0.5D);
				}
			}, Event.Side.CLIENT)});
		
		DRAGON_ATTACK2 = new EnderDragonAttackAnimation(0.35F, 0.25F, 0.45F, 0.66F, 0.75F, ColliderPreset.DRAGON_LEG, "Leg_Front_R3", "dragon/attack2", dragon, new IKInfo[] {
				IKInfo.make("Leg_Front_L1", "Leg_Front_L3", null, Pair.of(1, 4), 0.12F, 0, new boolean[] {true, true, true}),
				IKInfo.make("Leg_Back_L1", "Leg_Back_L3", null, null, 0.1344F, 0, new boolean[] {}),
				IKInfo.make("Leg_Back_R1", "Leg_Back_R3", null, null, 0.1344F, 0, new boolean[] {})
			});
		
		DRAGON_ATTACK3 = new EnderDragonAttackAnimation(0.35F, 0.25F, 0.45F, 0.66F, 0.75F, ColliderPreset.DRAGON_LEG, "Leg_Front_L3", "dragon/attack3", dragon, new IKInfo[] {
				IKInfo.make("Leg_Front_R1", "Leg_Front_R3", null, Pair.of(1, 4), 0.12F, 0, new boolean[] {true, true, true}),
				IKInfo.make("Leg_Back_L1", "Leg_Back_L3", null, null, 0.1344F, 0, new boolean[] {}),
				IKInfo.make("Leg_Back_R1", "Leg_Back_R3", null, null, 0.1344F, 0, new boolean[] {})
			});
		
		DRAGON_ATTACK4 = new EnderDragonAttackAnimation(0.35F, 0.5F, 1.15F, 1.26F, 1.9F, ColliderPreset.DRAGON_BODY, "Root", "dragon/attack4", dragon, new IKInfo[] {
				IKInfo.make("Leg_Front_L1", "Leg_Front_L3", null, Pair.of(0, 7), 0.12F, 0, new boolean[] {false, false, false, false, true, true, true}),
				IKInfo.make("Leg_Front_R1", "Leg_Front_R3", null, Pair.of(0, 7), 0.12F, 0, new boolean[] {false, false, false, false, true, true, true}),
				IKInfo.make("Leg_Back_L1", "Leg_Back_L3", null, Pair.of(3, 8), 0.1344F, 0, new boolean[] {false, false, false, false, true}),
				IKInfo.make("Leg_Back_R1", "Leg_Back_R3", null, Pair.of(3, 8), 0.1344F, 0, new boolean[] {false, false, false, false, true})
			})
			.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN)
			.addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
			.addProperty(StaticAnimationProperty.EVENTS, new Event[] {Event.create(1.2F, (entitypatch) -> {
				entitypatch.playSound(EpicFightSounds.GROUND_SLAM, 0, 0);
				
				if (entitypatch instanceof EnderDragonPatch) {
					EnderDragonPatch dragonpatch = ((EnderDragonPatch)entitypatch);
					Vec3f tipPosition = dragonpatch.getTipPointAnimation("Leg_Front_R3").getTargetPosition();
					LivingEntity original = entitypatch.getOriginal();
					original.level.addParticle(EpicFightParticles.GROUND_SLAM.get(), tipPosition.x, tipPosition.y, tipPosition.z, 3.0D, 0.0D, 1.0D);
				}
			}, Event.Side.CLIENT), Event.create(1.85F, (entitypatch) -> {
				entitypatch.getAnimator().reserveAnimation(DRAGON_ATTACK4_RECOVERY);
			}, Event.Side.BOTH)});
		
		DRAGON_ATTACK4_RECOVERY = new EnderDragonActionAnimation(0.35F, "dragon/attack4_recovery", dragon, new IKInfo[] {
				IKInfo.make("Leg_Front_L1", "Leg_Front_L3", null, Pair.of(0, 4), 0.12F, 0, new boolean[] {true, false, true, true}),
				IKInfo.make("Leg_Front_R1", "Leg_Front_R3", null, Pair.of(0, 3), 0.12F, 0, new boolean[] {true, true, true}),
				IKInfo.make("Leg_Back_L1", "Leg_Back_L3", null, Pair.of(0, 5), 0.1344F, 0, new boolean[] {true, true, false, false, true}),
				IKInfo.make("Leg_Back_R1", "Leg_Back_R3", null, Pair.of(0, 4), 0.1344F, 0, new boolean[] {true, true, false, false})
		});
		
		DRAGON_FIREBALL = new EnderDragonActionAnimation(0.16F, "dragon/fireball", dragon, new IKInfo[] {
				IKInfo.make("Leg_Front_L1", "Leg_Front_L3", null, Pair.of(0, 5), 0.12F, 0, new boolean[] {true, true, true, true, true}),
				IKInfo.make("Leg_Front_R1", "Leg_Front_R3", null, Pair.of(0, 5), 0.12F, 0, new boolean[] {true, true, true, true, true}),
				IKInfo.make("Leg_Back_L1", "Leg_Back_L3", null, Pair.of(0, 5), 0.1344F, 0, new boolean[] {true, true, true, true, true}),
				IKInfo.make("Leg_Back_R1", "Leg_Back_R3", null, Pair.of(0, 5), 0.1344F, 0, new boolean[] {true, true, true, true, true})
		}).addProperty(StaticAnimationProperty.EVENTS, new Event[] {Event.create(0.65F, (entitypatch) -> {
			LivingEntity original = entitypatch.getOriginal();
			Entity target = entitypatch.getTarget();
            Vec3 pos = original.position();
            Vec3 toTarget = target.position().subtract(original.position()).normalize().scale(original.getBbWidth() * 0.5D);
            
            double d6 = (float)(pos.x + toTarget.x);
            double d7 = (float)(pos.y + 2.0F);
            double d8 = (float)(pos.z + toTarget.z);
            double d9 = target.getX() - d6;
            double d10 = target.getY(0.5D) - d7;
            double d11 = target.getZ() - d8;
            
            if (!original.isSilent()) {
               original.level.levelEvent((Player)null, 1017, original.blockPosition(), 0);
            }
            
            DragonFireball dragonfireball = new DragonFireball(original.level, original, d9, d10, d11);
            dragonfireball.moveTo(d6, d7, d8, 0.0F, 0.0F);
            original.level.addFreshEntity(dragonfireball);
		}, Side.SERVER)});
		
		DRAGON_AIRSTRIKE = new StaticAnimation(0.35F, true, "dragon/airstrike", dragon)
			.addProperty(StaticAnimationProperty.EVENTS, new Event[] {Event.create(0.3F, ReuseableEvents.WING_FLAP, Event.Side.CLIENT)});
		
		DRAGON_BACKJUMP_PREPARE = new EnderDragonActionAnimation(0.35F, "dragon/backjump_prepare", dragon, new IKInfo[] {
				IKInfo.make("Leg_Front_L1", "Leg_Front_L3", null, Pair.of(0, 4), 0.12F, 0, new boolean[] {true, true, true, true}),
				IKInfo.make("Leg_Front_R1", "Leg_Front_R3", null, Pair.of(0, 4), 0.12F, 0, new boolean[] {true, true, true, true}),
				IKInfo.make("Leg_Back_L1", "Leg_Back_L3", null, Pair.of(0, 4), 0.1344F, 0, new boolean[] {true, true, true, true}),
				IKInfo.make("Leg_Back_R1", "Leg_Back_R3", null, Pair.of(0, 4), 0.1344F, 0, new boolean[] {true, true, true, true})
		}).addProperty(StaticAnimationProperty.EVENTS, new Event[] {Event.create(0.3F, (entitypatch) -> {
			entitypatch.getAnimator().reserveAnimation(DRAGON_BACKJUMP_MOVE);
		}, Side.BOTH)});
		
		DRAGON_BACKJUMP_MOVE = new AttackAnimation(0.0F, 10.0F, 10.0F, 10.0F, 10.0F, ColliderPreset.FIST, "Root", "dragon/backjump_move", dragon)
			.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
			.addProperty(StaticAnimationProperty.EVENTS, new Event[] {Event.create(1.0F, (entitypatch) -> {
				entitypatch.getAnimator().reserveAnimation(DRAGON_BACKJUMP_RECOVERY);
			}, Side.BOTH)});
		
		DRAGON_BACKJUMP_RECOVERY = new EnderDragonActionAnimation(0.0F, "dragon/backjump_recovery", dragon, new IKInfo[] {
				IKInfo.make("Leg_Front_L1", "Leg_Front_L3", null, Pair.of(0, 4), 0.12F, 0, new boolean[] {false, true, true, true}),
				IKInfo.make("Leg_Front_R1", "Leg_Front_R3", null, Pair.of(0, 4), 0.12F, 0, new boolean[] {false, true, true, true}),
				IKInfo.make("Leg_Back_L1", "Leg_Back_L3", null, Pair.of(0, 4), 0.1344F, 0, new boolean[] {true, true, true, true}),
				IKInfo.make("Leg_Back_R1", "Leg_Back_R3", null, Pair.of(0, 4), 0.1344F, 0, new boolean[] {true, true, true, true})
			})
			.addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
			.addProperty(StaticAnimationProperty.EVENTS, new Event[] {Event.create(0.15F, (entitypatch) -> {
				entitypatch.playSound(EpicFightSounds.GROUND_SLAM, 0, 0);
				
				if (entitypatch instanceof EnderDragonPatch) {
					EnderDragonPatch dragonpatch = ((EnderDragonPatch)entitypatch);
					Vec3f tipPosition = dragonpatch.getTipPointAnimation("Leg_Front_R3").getTargetPosition();
					LivingEntity original = entitypatch.getOriginal();
					original.level.addParticle(EpicFightParticles.GROUND_SLAM.get(), tipPosition.x, tipPosition.y, tipPosition.z, 3.0D, 0.0D, 1.0D);
				}
			}, Event.Side.CLIENT)});
		
		DRAGON_CRYSTAL_LINK = new EnderDragonActionAnimation(0.5F, "dragon/crystal_link", dragon, new IKInfo[] {
				IKInfo.make("Leg_Front_L1", "Leg_Front_L3", null, Pair.of(0, 2), 0.12F, 0, new boolean[] {true, true}),
				IKInfo.make("Leg_Front_R1", "Leg_Front_R3", null, Pair.of(0, 2), 0.12F, 0, new boolean[] {true, true}),
				IKInfo.make("Leg_Back_L1", "Leg_Back_L3", null, Pair.of(0, 2), 0.1344F, 0, new boolean[] {true, true}),
				IKInfo.make("Leg_Back_R1", "Leg_Back_R3", null, Pair.of(0, 2), 0.1344F, 0, new boolean[] {true, true})
			})
			.addProperty(StaticAnimationProperty.EVENTS, new Event[] {Event.create(7.0F, (entitypatch) -> {
				entitypatch.getOriginal().playSound(SoundEvents.ENDER_DRAGON_GROWL, 7.0F, 0.8F + entitypatch.getOriginal().getRandom().nextFloat() * 0.3F);
				entitypatch.getOriginal().setHealth(entitypatch.getOriginal().getMaxHealth());
				
				if (entitypatch instanceof EnderDragonPatch) {
					((EnderDragonPatch)entitypatch).getOriginal().getPhaseManager().setPhase(PatchedPhases.GROUND_BATTLE);
					entitypatch.setStunShield(0.0F);
				}
			}, Event.Side.SERVER), Event.create(7.0F, (entitypatch) -> {
				Entity original = entitypatch.getOriginal();
				original.level.addParticle(EpicFightParticles.FORCE_FIELD_END.get(), original.getX(), original.getY() + 2.0D, original.getZ(), 0, 0, 0);
			}, Event.Side.CLIENT)});
		
		DRAGON_NEUTRALIZED = new EnderDragonActionAnimation(0.1F, "dragon/neutralized", dragon, new IKInfo[] {
				IKInfo.make("Leg_Front_L1", "Leg_Front_L3", null, Pair.of(0, 4), 0.12F, 0, new boolean[] {true, true, true, true}),
				IKInfo.make("Leg_Front_R1", "Leg_Front_R3", null, Pair.of(0, 4), 0.12F, 0, new boolean[] {true, true, true, true}),
				IKInfo.make("Leg_Back_L1", "Leg_Back_L3", null, Pair.of(0, 4), 0.1344F, 0, new boolean[] {true, true, true, true}),
				IKInfo.make("Leg_Back_R1", "Leg_Back_R3", null, Pair.of(0, 4), 0.1344F, 0, new boolean[] {true, true, true, true})
			})
			.addProperty(StaticAnimationProperty.EVENTS, new Event[] {Event.create(3.95F, (entitypatch) -> {
				entitypatch.getAnimator().playAnimation(DRAGON_NEUTRALIZED_RECOVERY, 0);
			}, Event.Side.BOTH)});
		
		DRAGON_NEUTRALIZED_RECOVERY = new EnderDragonActionAnimation(0.05F, "dragon/neutralized_recovery", dragon, new IKInfo[] {
				IKInfo.make("Leg_Front_L1", "Leg_Front_L3", null, Pair.of(0, 5), 0.12F, 0, new boolean[] {true, true, true, false, true}),
				IKInfo.make("Leg_Front_R1", "Leg_Front_R3", null, Pair.of(0, 5), 0.12F, 0, new boolean[] {true, false, true, true, true}),
				IKInfo.make("Leg_Back_L1", "Leg_Back_L3", null, Pair.of(0, 5), 0.1344F, 0, new boolean[] {true, true, true, true, true}),
				IKInfo.make("Leg_Back_R1", "Leg_Back_R3", null, Pair.of(0, 4), 0.1344F, 0, new boolean[] {true, true, true, true})
			})
			.addProperty(StaticAnimationProperty.EVENTS, new Event[] {Event.create(1.6F, (entitypatch) -> {
				if (entitypatch instanceof EnderDragonPatch) {
					((EnderDragonPatch)entitypatch).getOriginal().getPhaseManager().getPhase(PatchedPhases.GROUND_BATTLE).fly();
				}
			}, Event.Side.SERVER)});
		
		SPIDER_ATTACK = new AttackAnimation(0.16F, 0.31F, 0.31F, 0.36F, 0.44F, ColliderPreset.SPIDER, "Head", "spider/attack", spider);
		SPIDER_JUMP_ATTACK = new AttackAnimation(0.16F, 0.25F, 0.25F, 0.41F, 0.8F,  ColliderPreset.SPIDER, "Head", "spider/jump_attack", spider)
				.addProperty(ActionAnimationProperty.MOVE_VERTICAL, true);
		SPIDER_HIT = new HitAnimation(0.08F, "spider/hit", spider);
		SPIDER_DEATH = new LongHitAnimation(0.16F, "spider/death", spider);
		
		GOLEM_ATTACK1 = new AttackAnimation(0.2F, 0.1F, 0.15F, 0.25F, 0.9F, ColliderPreset.HEAD, "Head", "iron_golem/attack1", iron_golem)
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN);
		GOLEM_ATTACK2 = new AttackAnimation(0.34F, 0.1F, 0.4F, 0.6F, 1.3F, ColliderPreset.GOLEM_SMASHDOWN, "LA4", "iron_golem/attack2", iron_golem)
				.addProperty(AttackPhaseProperty.FINISHER, true);
		GOLEM_ATTACK3 = new AttackAnimation(0.16F, 0.4F, 0.4F, 0.5F, 0.9F, ColliderPreset.GOLEM_SWING_ARM, "RA4", "iron_golem/attack3", iron_golem)
				.addProperty(AttackAnimationProperty.ROTATE_X, true);
		GOLEM_ATTACK4 = new AttackAnimation(0.16F, 0.4F, 0.4F, 0.5F, 0.9F, ColliderPreset.GOLEM_SWING_ARM, "LA4", "iron_golem/attack4", iron_golem)
				.addProperty(AttackAnimationProperty.ROTATE_X, true);
		GOLEM_DEATH = new LongHitAnimation(0.11F, "iron_golem/death", iron_golem);
		
		VINDICATOR_SWING_AXE1 = new AttackAnimation(0.2F, 0.25F, 0.35F, 0.46F, 0.71F, ColliderPreset.TOOLS, "Tool_R", "illager/swing_axe1", biped);
		VINDICATOR_SWING_AXE2 = new AttackAnimation(0.2F, 0.25F, 0.3F, 0.41F, 0.71F, ColliderPreset.TOOLS, "Tool_R", "illager/swing_axe2", biped);
		VINDICATOR_SWING_AXE3 = new AttackAnimation(0.05F, 0.50F, 0.62F, 0.75F, 1F, ColliderPreset.TOOLS, "Tool_R", "illager/swing_axe3", biped)
				.addProperty(ActionAnimationProperty.MOVE_VERTICAL, true);
		
		PIGLIN_DEATH = new LongHitAnimation(0.16F, "piglin/death", piglin);
		
		HOGLIN_DEATH = new LongHitAnimation(0.16F, "hoglin/death", hoglin);
		HOGLIN_ATTACK = new AttackAnimation(0.16F, 0.25F, 0.25F, 0.45F, 1.0F, ColliderPreset.GOLEM_SWING_ARM, "Head", "hoglin/attack", hoglin);
		
		RAVAGER_DEATH = new LongHitAnimation(0.11F, "ravager/death", ravager);
		RAVAGER_STUN = new ActionAnimation(0.16F, "ravager/groggy", ravager)
				.addProperty(ActionAnimationProperty.STOP_MOVEMENT, true);
		RAVAGER_ATTACK1 = new AttackAnimation(0.16F, 0.2F, 0.4F, 0.5F, 0.55F, ColliderPreset.HEADBUTT_RAVAGER, "Head", "ravager/attack1", ravager);
		RAVAGER_ATTACK2 = new AttackAnimation(0.16F, 0.2F, 0.4F, 0.5F, 1.3F, ColliderPreset.HEADBUTT_RAVAGER, "Head", "ravager/attack2", ravager);
		RAVAGER_ATTACK3 = new AttackAnimation(0.16F, 0.0F, 1.1F, 1.16F, 1.6F, ColliderPreset.HEADBUTT_RAVAGER, "Head", "ravager/attack3", ravager);
		
		VEX_HIT = new HitAnimation(0.048F, "vex/hit", vex);
		VEX_DEATH = new LongHitAnimation(0.16F, "vex/death", vex);
		VEX_CHARGE = new AttackAnimation(0.11F, 0.3F, 0.3F, 0.5F, 1.2F, ColliderPreset.VEX_CHARGE, "Root", "vex/charge", vex)
				.addProperty(AttackPhaseProperty.SOURCE_LOCATION_PROVIDER, (entitypatch) -> entitypatch.getLastAttackPosition())
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				.addProperty(ActionAnimationProperty.COORD_SET_BEGIN, (self, entitypatch, transformSheet) -> {
					TransformSheet transform = self.getTransfroms().get("Root").copyAll();
					Keyframe[] keyframes = transform.getKeyframes();
					int startFrame = 0;
					int endFrame = 6;
					Vec3 pos = entitypatch.getOriginal().position();
					Vec3 targetpos = entitypatch.getTarget().position();
					float verticalDistance = (float) (targetpos.y - pos.y);
					Quaternion rotator = Vec3f.getRotatorBetween(new Vec3f(0.0F, verticalDistance, -(float)targetpos.subtract(pos).horizontalDistance()), new Vec3f(0.0F, 0.0F, -1.0F));
					
					for (int i = startFrame; i <= endFrame; i++) {
						Vec3f translation = keyframes[i].transform().translation();
						OpenMatrix4f.transform3v(OpenMatrix4f.fromQuaternion(rotator), translation, translation);
					}
					
					transformSheet.readFrom(transform);
				})
				.addProperty(ActionAnimationProperty.COORD_SET_TICK, (self, entitypatch, transformSheet) -> {})
				.addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[] {
					Event.create(0.0F, (entitypatch) -> entitypatch.setLastAttackPosition(), Side.SERVER)
				});
		
		VEX_NEUTRALIZED = new LongHitAnimation(0.1F, "vex/neutralized", vex);
		
		WITCH_DRINKING = new StaticAnimation(0.16F, false, "witch/drink", biped);
		
		WITHER_SKELETON_ATTACK1 = new AttackAnimation(0.16F, 0.2F, 0.3F, 0.41F, 0.7F, ColliderPreset.SWORD, "Tool_R", "wither_skeleton/sword_attack1", biped)
				.addProperty(AttackAnimationProperty.ROTATE_X, true);
		WITHER_SKELETON_ATTACK2 = new AttackAnimation(0.16F, 0.25F, 0.25F, 0.36F, 0.7F, ColliderPreset.SWORD, "Tool_R", "wither_skeleton/sword_attack2", biped)
				.addProperty(AttackAnimationProperty.ROTATE_X, true);
		WITHER_SKELETON_ATTACK3 = new AttackAnimation(0.16F, 0.25F, 0.25F, 0.36F, 0.7F, ColliderPreset.SWORD, "Tool_R", "wither_skeleton/sword_attack3", biped)
				.addProperty(AttackAnimationProperty.ROTATE_X, true);
		
		WITHER_CHARGE = new AttackAnimation(0.35F, 0.35F, 0.35F, 0.66F, 2.05F, ColliderPreset.WITHER_CHARGE, "Root", "wither/rush", wither)
				.addProperty(AttackPhaseProperty.SWING_SOUND, EpicFightSounds.FAST_MOVE)
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLUNT_HIT_HARD)
				.addProperty(AttackPhaseProperty.SOURCE_LOCATION_PROVIDER, (entitypatch) -> entitypatch.getLastAttackPosition())
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN)
				.addProperty(AttackPhaseProperty.MAX_STRIKES, ValueCorrector.adder(100))
				.addProperty(AttackPhaseProperty.DAMAGE, ValueCorrector.setter(15.0F))
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				.addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
				.addProperty(ActionAnimationProperty.COORD_SET_BEGIN, (self, entitypatch, transformSheet) -> {
					if (entitypatch instanceof WitherPatch && ((WitherPatch)entitypatch).getOriginal().getAlternativeTarget(0) > 0) {
						WitherPatch witherpatch = (WitherPatch)entitypatch;
						TransformSheet transform = self.getTransfroms().get("Root").copyAll();
						Keyframe[] keyframes = transform.getKeyframes();
						int startFrame = 1;
						int endFrame = 5;
						Vec3f keyOrigin = keyframes[startFrame].transform().translation().multiply(1.0F, 1.0F, 0.0F);
						Vec3f keyLast = keyframes[3].transform().translation();
						Vec3 pos = entitypatch.getOriginal().getEyePosition();
						Vec3 targetpos = entitypatch.getOriginal().level.getEntity(witherpatch.getOriginal().getAlternativeTarget(0)).position();
						float horizontalDistance = (float)targetpos.subtract(pos).length();
						float verticalDistance = (float)(targetpos.y - pos.y);
						Vec3f prevPosition = Vec3f.sub(keyLast, keyOrigin, null);
						Vec3f newPosition = new Vec3f(keyLast.x, verticalDistance, -horizontalDistance);
						float scale = Math.min(newPosition.length() / prevPosition.length(), 5.0F);
						Quaternion rotator = Vec3f.getRotatorBetween(newPosition, keyLast);
						
						for (int i = startFrame; i <= endFrame; i++) {
							Vec3f translation = keyframes[i].transform().translation();
							translation.z *= scale;
							OpenMatrix4f.transform3v(OpenMatrix4f.fromQuaternion(rotator), translation, translation);
						}
						
						transformSheet.readFrom(transform);
					} else {
						transformSheet.readFrom(self.getTransfroms().get("Root").copyAll());
					}
				}).addProperty(ActionAnimationProperty.COORD_SET_TICK, (self, entitypatch, transformSheet) -> {
					
				}).addProperty(StaticAnimationProperty.EVENTS, new Event[] {Event.create(0.4F, (entitypatch) -> {
						if (entitypatch instanceof WitherPatch) {
							((WitherPatch)entitypatch).startCharging();
						} else {
							entitypatch.setLastAttackPosition();
						}
					}, Side.SERVER), Event.create(0.4F, (entitypatch) -> {
						Entity entity = entitypatch.getOriginal();
						entitypatch.getOriginal().level.addParticle(EpicFightParticles.ENTITY_AFTER_IMAGE.get(), entity.getX(), entity.getY(), entity.getZ(), Double.longBitsToDouble(entity.getId()), 0, 0);
					}, Side.CLIENT), Event.create(Event.ON_BEGIN, (entitypatch) -> {
						if (entitypatch instanceof WitherPatch) {
							WitherPatch witherpatch = ((WitherPatch)entitypatch);
							
							if (!witherpatch.getOriginal().isPowered()) {
								((WitherPatch)entitypatch).setArmorActivated(true);
							}
						}
					}, Side.CLIENT), Event.create(Event.ON_END, (entitypatch) -> {
						if (entitypatch instanceof WitherPatch) {
							WitherPatch witherpatch = ((WitherPatch)entitypatch);
							
							if (!witherpatch.getOriginal().isPowered()) {
								((WitherPatch)entitypatch).setArmorActivated(false);
							}
						}
					}, Side.CLIENT)}
				);
		
		WITHER_DEATH = new LongHitAnimation(0.16F, "wither/death", wither);
		WITHER_NEUTRALIZED = new LongHitAnimation(0.05F, "wither/neutralized", wither)
				.addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[] {Event.create(0.0F, (entitypatch) -> {
					Entity entity = entitypatch.getOriginal();
					entity.level.addParticle(EpicFightParticles.NEUTRALIZE.get(), entity.getX(), entity.getEyeY(), entity.getZ(), 3.0D, Double.longBitsToDouble(15), Double.NaN);
				}, Side.CLIENT)}
			);
		
		WITHER_SPELL_ARMOR = new InvincibleAnimation(0.35F, "wither/spell_wither_armor", wither)
				.addProperty(ActionAnimationProperty.MOVE_VERTICAL, false)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[] {Event.create(0.0F, (entitypatch) -> {
					entitypatch.playSound(EpicFightSounds.WITHER_SPELL_ARMOR, 5.0F, 0.0F, 0.0F);
					Entity entity = entitypatch.getOriginal();
					entity.level.addParticle(EpicFightParticles.BOSS_CASTING.get(), entity.getX(), entity.getEyeY(), entity.getZ(), 5.0D, Double.longBitsToDouble(20), Double.longBitsToDouble(4));
				}, Side.CLIENT), Event.create(0.5F, (entitypatch) -> {
					((WitherPatch)entitypatch).setArmorActivated(true);
				}, Side.SERVER)}
			);
		
		WITHER_BLOCKED = new ActionAnimation(0.05F, "wither/charging_blocked", wither)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[] {Event.create(Event.ON_BEGIN, (entitypatch) -> {
					if (entitypatch instanceof WitherPatch) {
						WitherPatch witherpatch = ((WitherPatch)entitypatch);
						
						if (!witherpatch.getOriginal().isPowered()) {
							((WitherPatch)entitypatch).setArmorActivated(true);
						}
					}
				}, Side.SERVER), Event.create(Event.ON_END, (entitypatch) -> {
					if (entitypatch instanceof WitherPatch) {
						WitherPatch witherpatch = ((WitherPatch)entitypatch);
						
						if (!witherpatch.getOriginal().isPowered()) {
							((WitherPatch)entitypatch).setArmorActivated(false);
						}
					}
				}, Side.SERVER)
			});
		
		WITHER_GHOST_STANDBY = new InvincibleAnimation(0.16F, "wither/ghost_stand", wither);
		
		WITHER_SWIRL = new AttackAnimation(0.2F, 0.05F, 0.4F, 0.51F, 1.6F, ColliderPreset.WITHER_CHARGE, "Torso", "wither/swirl", wither)
				.addProperty(AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_BIG)
				.addProperty(AttackPhaseProperty.MAX_STRIKES, ValueCorrector.setter(3))
				.addProperty(AttackPhaseProperty.DAMAGE, ValueCorrector.setter(6.0F));
		
		WITHER_BEAM = new ActionAnimation(0.05F, "wither/laser", wither)
				.addProperty(ActionAnimationProperty.MOVE_VERTICAL, false)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[] {Event.create(0.0F, (entitypatch) -> {
					entitypatch.playSound(EpicFightSounds.BUZZ, 0.0F, 0.0F);
					
					if (entitypatch instanceof WitherPatch) {
						WitherPatch witherpatch = ((WitherPatch)entitypatch);
						for (int i = 0; i < 3; i++) {
							Entity headTarget = witherpatch.getAlternativeTargetEntity(i);
							
							if (headTarget == null) {
								headTarget = witherpatch.getAlternativeTargetEntity(0);
							}
							
							if (headTarget != null) {
								witherpatch.setLaserTarget(i, headTarget);
							}
						}
					}
				}, Side.SERVER), Event.create(0.7F, (entitypatch) -> {
					if (entitypatch instanceof WitherPatch) {
						WitherPatch witherpatch = ((WitherPatch)entitypatch);
						
						for (int i = 0; i < 3; i++) {
							Entity headTarget = witherpatch.getLaserTargetEntity(i);
							
							if (headTarget != null) {
								Vec3 pos = headTarget.position().add(0.0D, headTarget.getBbHeight() * 0.5D, 0.0D);
								witherpatch.setLaserTargetPosition(i, pos);
								witherpatch.setLaserTarget(i, null);
							}
						}
					}
				}, Side.SERVER), Event.create(0.9F, (entitypatch) -> {
					if (entitypatch instanceof WitherPatch) {
						WitherPatch witherpatch = ((WitherPatch)entitypatch);
						WitherBoss witherboss = witherpatch.getOriginal();
						witherboss.level.playLocalSound(witherboss.getX(), witherboss.getY(), witherboss.getZ(), EpicFightSounds.LASER_BLAST, SoundSource.HOSTILE, 1.0F, 1.0F, false);
						
						for (int i = 0; i < 3; i++) {
							Vec3 laserDestination = witherpatch.getLaserTargetPosition(i);
							Entity headTarget = witherpatch.getAlternativeTargetEntity(i);
							
							if (headTarget != null) {
								witherpatch.getOriginal().level.addAlwaysVisibleParticle(EpicFightParticles.LASER.get(), witherboss.getHeadX(i), witherboss.getHeadY(i), witherboss.getHeadZ(i), laserDestination.x, laserDestination.y, laserDestination.z);
							}
						}
					}
				}, Side.CLIENT), Event.create(0.9F, (entitypatch) -> {
					if (entitypatch instanceof WitherPatch) {
						WitherPatch witherpatch = ((WitherPatch)entitypatch);
						WitherBoss witherboss = witherpatch.getOriginal();
						List<Entity> hurted = Lists.newArrayList();
						
						for (int i = 0; i < 3; i++) {
							Vec3 laserDestination = witherpatch.getLaserTargetPosition(i);
							Entity headTarget = witherpatch.getAlternativeTargetEntity(i);
							
							if (headTarget != null) {
								double x = witherboss.getHeadX(i);
								double y = witherboss.getHeadY(i);
								double z = witherboss.getHeadZ(i);
								Vec3 direction = laserDestination.subtract(x, y, z);
								Vec3 start = new Vec3(x, y, z);
								Vec3 destination = start.add(direction.normalize().scale(200.0D));
								BlockHitResult hitResult = witherboss.level.clip(new ClipContext(start, destination, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));
								Vec3 hitLocation = hitResult.getLocation();
								double xLength = hitLocation.x - x;
								double yLength = hitLocation.y - y;
								double zLength = hitLocation.z - z;
								double horizontalDistance = Math.sqrt(xLength * xLength + zLength * zLength);
								double length = Math.sqrt(xLength * xLength + yLength * yLength + zLength * zLength);
								float yRot = (float)(-Math.atan2(zLength, xLength) * (180D / Math.PI)) - 90.0F;
								float xRot = (float)(Math.atan2(yLength, horizontalDistance) * (180D / Math.PI));
								OBBCollider collider = new OBBCollider(0.25D, 0.25D, length * 0.5D, 0.0D, 0.0D, length * 0.5D);
								collider.transform(OpenMatrix4f.createTranslation((float)-x, (float)y, (float)-z).rotateDeg(yRot, Vec3f.Y_AXIS).rotateDeg(-xRot, Vec3f.X_AXIS));
								List<Entity> hitEntities = collider.getCollideEntities(witherboss);
								
								DamageSource damagesource = ExtendedDamageSource.causeDamage("wither_beam", witherboss, StunType.SHORT, WITHER_BEAM);
								damagesource.setMagic();
								
								hitEntities.forEach((entity) -> {
									if (!hurted.contains(entity)) {
										hurted.add(entity);
										entity.hurt(damagesource, 12.0F);
									}
								});
								
								Explosion.BlockInteraction explosion$blockinteraction = ForgeEventFactory.getMobGriefingEvent(witherboss.level, witherboss) ? Explosion.BlockInteraction.DESTROY : Explosion.BlockInteraction.NONE;
								witherboss.level.explode(witherboss, hitLocation.x, hitLocation.y, hitLocation.z, 0.0F, false, explosion$blockinteraction);
							}
						}
					}
				}, Side.SERVER), Event.create(2.3F, (entitypatch) -> {
					if (entitypatch instanceof WitherPatch) {
						WitherPatch witherpatch = ((WitherPatch)entitypatch);
						
						for (int i = 0; i < 3; i++) {
							witherpatch.setLaserTargetPosition(i, new Vec3(Double.NaN, Double.NaN, Double.NaN));
						}
					}
				}, Side.SERVER)}
			);
		
		WITHER_BACKFLIP = new AttackAnimation(0.2F, 0.3F, 0.5F, 0.66F, 2.1F, ColliderPreset.WITHER_CHARGE, "Torso", "wither/backflip", wither)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				.addProperty(AttackPhaseProperty.SWING_SOUND, EpicFightSounds.FAST_MOVE)
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.BLUNT_HIT_HARD)
				.addProperty(AttackPhaseProperty.MAX_STRIKES, ValueCorrector.adder(100))
				.addProperty(AttackPhaseProperty.DAMAGE, ValueCorrector.setter(10.0F))
				.addProperty(AttackPhaseProperty.STUN_TYPE, StunType.KNOCKDOWN);
		
		ZOMBIE_ATTACK1 = new AttackAnimation(0.1F, 0.3F, 0.4F, 0.6F, 0.85F, ColliderPreset.FIST, "Tool_R", "zombie/attack1", biped)
				.addProperty(AttackAnimationProperty.ROTATE_X, true);
		ZOMBIE_ATTACK2 = new AttackAnimation(0.1F, 0.3F, 0.4F, 0.6F, 0.85F, ColliderPreset.FIST, "Tool_L", "zombie/attack2", biped)
				.addProperty(AttackAnimationProperty.ROTATE_X, true);
		ZOMBIE_ATTACK3 = new AttackAnimation(0.1F, 0.5F, 0.5F, 0.6F, 1.15F, ColliderPreset.HEAD, "Head", "zombie/attack3", biped);
		
		SWEEPING_EDGE = new SpecialAttackAnimation(0.16F, 0.1F, 0.35F, 0.46F, 0.79F, null, "Tool_R", "biped/skill/sweeping_edge", biped)
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.addProperty(AttackAnimationProperty.ROTATE_X, true)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.6F)
				.addProperty(AttackAnimationProperty.COLLIDER_ADDER, 1);
		
		DANCING_EDGE = new SpecialAttackAnimation(0.25F, "biped/skill/dancing_edge", biped,
				new Phase(0.0F, 0.2F, 0.31F, 0.4F, 0.4F, "Tool_R", null), new Phase(0.4F, 0.5F, 0.61F, 0.65F, 0.65F, InteractionHand.OFF_HAND, "Tool_L", null),
				new Phase(0.65F, 0.75F, 0.85F, 1.15F, Float.MAX_VALUE, "Tool_R", null))
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.6F)
				.addProperty(ActionAnimationProperty.MOVE_VERTICAL, true);
		
		GUILLOTINE_AXE = new SpecialAttackAnimation(0.08F, 0.2F, 0.5F, 0.65F, 1.0F, null, "Tool_R", "biped/skill/guillotine_axe", biped)
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.addProperty(ActionAnimationProperty.MOVE_VERTICAL, true)
				.addProperty(StaticAnimationProperty.PLAY_SPEED, 1.0F);
		
		SPEAR_THRUST = new SpecialAttackAnimation(0.11F, "biped/skill/spear_thrust", biped,
				new Phase(0.0F, 0.3F, 0.36F, 0.5F, 0.5F, "Tool_R", null), new Phase(0.5F, 0.5F, 0.56F, 0.75F, 0.75F, "Tool_R", null),
				new Phase(0.75F, 0.75F, 0.81F, 1.05F, Float.MAX_VALUE, "Tool_R", null))
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.addProperty(AttackAnimationProperty.ROTATE_X, true)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.2F);
		
		SPEAR_SLASH = new SpecialAttackAnimation(0.1F, "biped/skill/spear_slash", biped,
				new Phase(0.0F, 0.2F, 0.41F, 0.5F, 0.5F, "Tool_R", null), new Phase(0.5F, 0.5F, 0.75F, 0.95F, 1.25F, Float.MAX_VALUE, "Tool_R", null))
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.addProperty(AttackAnimationProperty.ROTATE_X, true)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.2F);
		
		GIANT_WHIRLWIND = new SpecialAttackAnimation(0.41F, "biped/skill/giant_whirlwind", biped,
				new Phase(0.0F, 0.3F, 0.35F, 0.55F, 0.9F, 0.9F, "Tool_R", null), new Phase(0.9F, 0.95F, 1.05F, 1.2F, 1.5F, 1.5F, "Tool_R", null),
				new Phase(1.5F, 1.65F, 1.75F, 1.95F, 2.5F, Float.MAX_VALUE, "Tool_R", null))
				.addProperty(AttackAnimationProperty.ROTATE_X, true)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.0F);
		
		FATAL_DRAW = new SpecialAttackAnimation(0.15F, 0.0F, 0.7F, 0.81F, 1.0F, ColliderPreset.FATAL_DRAW, "Root", "biped/skill/fatal_draw", biped)
				.addProperty(AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_SHARP)
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.addProperty(StaticAnimationProperty.PLAY_SPEED, 1.0F)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[] {Event.create(0.05F, ReuseableEvents.KATANA_IN, Event.Side.SERVER)});
		
		FATAL_DRAW_DASH = new SpecialAttackAnimation(0.15F, 0.43F, 0.85F, 0.851F, 1.4F, ColliderPreset.FATAL_DRAW_DASH, "Root", "biped/skill/fatal_draw_dash", biped)
				.addProperty(AttackPhaseProperty.SWING_SOUND, EpicFightSounds.WHOOSH_SHARP)
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				.addProperty(StaticAnimationProperty.PLAY_SPEED, 1.0F)
				.addProperty(StaticAnimationProperty.EVENTS, new Event[] {Event.create(0.05F, ReuseableEvents.KATANA_IN, Event.Side.SERVER)})
				.addProperty(StaticAnimationProperty.EVENTS, new Event[] {Event.create(0.85F, (entitypatch) -> {
					Entity entity = entitypatch.getOriginal();
					entitypatch.getOriginal().level.addParticle(EpicFightParticles.ENTITY_AFTER_IMAGE.get(), entity.getX(), entity.getY(), entity.getZ(), Double.longBitsToDouble(entity.getId()), 0, 0);
				}, Side.CLIENT)});
		
		LETHAL_SLICING = new SpecialAttackAnimation(0.15F, 0.0F, 0.0F, 0.11F, 0.38F, ColliderPreset.FIST_FIXED, "Root", "biped/skill/lethal_slicing_start", biped)
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.addProperty(StaticAnimationProperty.PLAY_SPEED, 1.0F);
		
		LETHAL_SLICING_ONCE = new SpecialAttackAnimation(0.016F, 0.0F, 0.0F, 0.1F, 0.6F, ColliderPreset.FATAL_DRAW, "Root", "biped/skill/lethal_slicing_once", biped)
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.6F);
		
		LETHAL_SLICING_TWICE = new SpecialAttackAnimation(0.016F, "biped/skill/lethal_slicing_twice", biped,
				new Phase(0.0F, 0.0F, 0.1F, 0.15F, 0.15F, "Root", ColliderPreset.FATAL_DRAW), new Phase(0.15F, 0.15F, 0.25F, 0.6F, Float.MAX_VALUE, "Root", ColliderPreset.FATAL_DRAW))
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 1.6F);
		
		RELENTLESS_COMBO = new SpecialAttackAnimation(0.05F, "biped/skill/relentless_combo", biped,
				new Phase(0.0F, 0.016F, 0.066F, 0.133F, 0.133F, InteractionHand.OFF_HAND, "Root", ColliderPreset.FIST_FIXED), new Phase(0.133F, 0.133F, 0.183F, 0.25F, 0.25F, "Root", ColliderPreset.FIST_FIXED),
				new Phase(0.25F, 0.25F, 0.3F, 0.366F, 0.366F, InteractionHand.OFF_HAND, "Root", ColliderPreset.FIST_FIXED), new Phase(0.366F, 0.366F, 0.416F, 0.483F, 0.483F, "Root", ColliderPreset.FIST_FIXED),
				new Phase(0.483F, 0.483F, 0.533F, 0.6F, 0.6F, InteractionHand.OFF_HAND, "Root", ColliderPreset.FIST_FIXED), new Phase(0.6F, 0.6F, 0.65F, 0.716F, 0.716F, "Root", ColliderPreset.FIST_FIXED),
				new Phase(0.716F, 0.716F, 0.766F, 0.833F, 0.833F, InteractionHand.OFF_HAND, "Root", ColliderPreset.FIST_FIXED), new Phase(0.833F, 0.833F, 0.883F, 1.1F, 1.1F, "Root", ColliderPreset.FIST_FIXED))
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 4.0F);
		
		EVISCERATE_FIRST = new SpecialAttackAnimation(0.08F, 0.05F, 0.05F, 0.15F, 0.45F, null, "Tool_R", "biped/skill/eviscerate_first", biped)
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.4F);
		
		EVISCERATE_SECOND = new SpecialAttackAnimation(0.15F, 0.0F, 0.0F, 0.0F, 0.4F, null, "Tool_R", "biped/skill/eviscerate_second", biped)
				.addProperty(AttackAnimationProperty.LOCK_ROTATION, true)
				.addProperty(AttackPhaseProperty.HIT_SOUND, EpicFightSounds.EVISCERATE)
				.addProperty(AttackPhaseProperty.PARTICLE, EpicFightParticles.EVISCERATE)
				.addProperty(AttackAnimationProperty.BASIS_ATTACK_SPEED, 2.4F);
		
		BLADE_RUSH_FIRST = new SpecialAttackAnimation(0.1F, 0.0F, 0.0F, 0.06F, 0.3F, ColliderPreset.BLADE_RUSH, "Root", "biped/skill/blade_rush_first", biped)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				.addProperty(AttackPhaseProperty.HIT_PRIORITY, Priority.TARGET)
				.addProperty(StaticAnimationProperty.PLAY_SPEED, 1.0F);
		BLADE_RUSH_SECOND = new SpecialAttackAnimation(0.1F, 0.0F, 0.0F, 0.06F, 0.3F, ColliderPreset.BLADE_RUSH, "Root", "biped/skill/blade_rush_second", biped)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				.addProperty(AttackPhaseProperty.HIT_PRIORITY, Priority.TARGET)
				.addProperty(StaticAnimationProperty.PLAY_SPEED, 1.0F);
		BLADE_RUSH_THIRD = new SpecialAttackAnimation(0.1F, 0.0F, 0.0F, 0.06F, 0.3F, ColliderPreset.BLADE_RUSH, "Root", "biped/skill/blade_rush_third", biped)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				.addProperty(AttackPhaseProperty.HIT_PRIORITY, Priority.TARGET)
				.addProperty(StaticAnimationProperty.PLAY_SPEED, 1.0F);
		BLADE_RUSH_FINISHER = new SpecialAttackAnimation(0.15F, 0.0F, 0.1F, 0.16F, 0.65F, ColliderPreset.BLADE_RUSH, "Root", "biped/skill/blade_rush_finisher", biped)
				.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true)
				.addProperty(AttackPhaseProperty.HIT_PRIORITY, Priority.TARGET)
				.addProperty(StaticAnimationProperty.PLAY_SPEED, 1.0F);
	}
	
	private static class ReuseableEvents {
		private static final Consumer<LivingEntityPatch<?>> WING_FLAP = (entitypatch) -> {
			if (entitypatch instanceof EnderDragonPatch) {
				((EnderDragonPatch)entitypatch).getOriginal().onFlap();
			}
		};
		private static final Consumer<LivingEntityPatch<?>> KATANA_IN = (entitypatch) -> entitypatch.playSound(EpicFightSounds.SWORD_IN, 0, 0);
	}
	
	@OnlyIn(Dist.CLIENT)
	public static void buildClient() {
		OFF_ANIMATION_HIGHEST.addProperty(ClientAnimationProperties.PRIORITY, Layer.Priority.HIGHEST);
		OFF_ANIMATION_MIDDLE.addProperty(ClientAnimationProperties.PRIORITY, Layer.Priority.MIDDLE);
	}
}