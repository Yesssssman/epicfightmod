package yesman.epicfight.gameasset;

import java.util.ArrayList;
import java.util.List;

import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.network.server.SPPlayAnimationAndSyncTransform;
import yesman.epicfight.world.capabilities.entitypatch.boss.WitherPatch;
import yesman.epicfight.world.capabilities.entitypatch.boss.enderdragon.DragonFlyingPhase;
import yesman.epicfight.world.capabilities.entitypatch.boss.enderdragon.EnderDragonPatch;
import yesman.epicfight.world.capabilities.entitypatch.boss.enderdragon.PatchedPhases;
import yesman.epicfight.world.capabilities.entitypatch.mob.EndermanPatch;
import yesman.epicfight.world.capabilities.entitypatch.mob.HumanoidMobPatch;
import yesman.epicfight.world.capabilities.entitypatch.mob.IronGolemPatch;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors.Behavior;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors.BehaviorSeries;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors.Health;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors.Health.Comparator;

public class MobCombatBehaviors {
	public static List<AttackAnimation> BIPED_ARMED = new ArrayList<AttackAnimation> ();
	public static List<AttackAnimation> BIPED_UNARMED = new ArrayList<AttackAnimation> ();
	public static List<AttackAnimation> DROWNED_ARMED_SPEAR = new ArrayList<AttackAnimation> ();
	public static List<AttackAnimation> BIPED_MOUNT_SWORD = new ArrayList<AttackAnimation> ();
	public static List<AttackAnimation> HOGLIN_HEADBUTT = new ArrayList<AttackAnimation> ();
	public static List<AttackAnimation> RAVAGER_HEADBUTT = new ArrayList<AttackAnimation> ();
	public static List<AttackAnimation> RAVAGER_SMASHING_GROUND = new ArrayList<AttackAnimation> ();
	public static List<AttackAnimation> SPIDER = new ArrayList<AttackAnimation> ();
	public static List<AttackAnimation> SPIDER_JUMP = new ArrayList<AttackAnimation> ();
	public static List<AttackAnimation> VINDICATOR_AXE = new ArrayList<AttackAnimation> ();
	
	public static void setAttackCombos() {
		BIPED_ARMED.add((AttackAnimation)Animations.BIPED_ARMED_MOB_ATTACK1);
		BIPED_ARMED.add((AttackAnimation)Animations.BIPED_ARMED_MOB_ATTACK2);
		DROWNED_ARMED_SPEAR.add((AttackAnimation)Animations.SPEAR_ONEHAND_AUTO);
		BIPED_MOUNT_SWORD.add((AttackAnimation)Animations.SWORD_MOUNT_ATTACK);
		RAVAGER_HEADBUTT.add((AttackAnimation)Animations.RAVAGER_ATTACK1);
		RAVAGER_HEADBUTT.add((AttackAnimation)Animations.RAVAGER_ATTACK2);
		RAVAGER_SMASHING_GROUND.add((AttackAnimation)Animations.RAVAGER_ATTACK3);
		SPIDER.add((AttackAnimation) Animations.SPIDER_ATTACK);
		SPIDER_JUMP.add((AttackAnimation) Animations.SPIDER_JUMP_ATTACK);
		VINDICATOR_AXE.add((AttackAnimation) Animations.VINDICATOR_SWING_AXE1);
		VINDICATOR_AXE.add((AttackAnimation) Animations.VINDICATOR_SWING_AXE2);
		VINDICATOR_AXE.add((AttackAnimation) Animations.VINDICATOR_SWING_AXE3);
		BIPED_UNARMED.add((AttackAnimation) Animations.ZOMBIE_ATTACK1);
		BIPED_UNARMED.add((AttackAnimation) Animations.ZOMBIE_ATTACK2);
		BIPED_UNARMED.add((AttackAnimation) Animations.ZOMBIE_ATTACK3);
		HOGLIN_HEADBUTT.add((AttackAnimation) Animations.HOGLIN_ATTACK);
	}
	
	public static final CombatBehaviors.Builder<HumanoidMobPatch<?>> BIPED_ARMED_BEHAVIORS = CombatBehaviors.<HumanoidMobPatch<?>>builder()
		.newBehaviorSeries(
			BehaviorSeries.<HumanoidMobPatch<?>>builder().weight(100.0F).canBeInterrupted(true).volatileBehaviorPointer(false)
				.nextBehavior(Behavior.<HumanoidMobPatch<?>>builder().animationBehavior(Animations.BIPED_ARMED_MOB_ATTACK1).withinEyeHeight().withinDistance(0.0D, 2.0D))
				.nextBehavior(Behavior.<HumanoidMobPatch<?>>builder().animationBehavior(Animations.BIPED_ARMED_MOB_ATTACK2).withinEyeHeight().withinDistance(0.0D, 2.0D))
		);
	
	public static final CombatBehaviors.Builder<EnderDragonPatch> ENDER_DRAGON_BEHAVIORS = CombatBehaviors.<EnderDragonPatch>builder()
		.newBehaviorSeries(
			BehaviorSeries.<EnderDragonPatch>builder().weight(50.0F).volatileBehaviorPointer(true).canBeInterrupted(false)
				.nextBehavior(Behavior.<EnderDragonPatch>builder().animationBehavior(Animations.DRAGON_ATTACK1).randomChance(0.1F).withinDistance(0.0D, 7.0D).withinAngle(0.0F, 60.0F))
				.nextBehavior(Behavior.<EnderDragonPatch>builder().animationBehavior(Animations.DRAGON_ATTACK3).withinDistance(0.0D, 7.0D))
				.nextBehavior(Behavior.<EnderDragonPatch>builder().animationBehavior(Animations.DRAGON_ATTACK2))
		).newBehaviorSeries(
			BehaviorSeries.<EnderDragonPatch>builder().weight(50.0F).volatileBehaviorPointer(true).canBeInterrupted(false)
				.nextBehavior(Behavior.<EnderDragonPatch>builder().animationBehavior(Animations.DRAGON_ATTACK2).withinDistance(0.0D, 5.0D).withinAngle(0.0F, 60.0F))
				.nextBehavior(Behavior.<EnderDragonPatch>builder().animationBehavior(Animations.DRAGON_ATTACK3))
				.nextBehavior(Behavior.<EnderDragonPatch>builder().animationBehavior(Animations.DRAGON_ATTACK1).randomChance(0.4F).withinDistance(0.0D, 7.0D))
		).newBehaviorSeries(
			BehaviorSeries.<EnderDragonPatch>builder().weight(50.0F).cooldown(200).simultaneousCooldown(3).volatileBehaviorPointer(true)
				.nextBehavior(Behavior.<EnderDragonPatch>builder().animationBehavior(Animations.DRAGON_ATTACK4).withinDistance(10.0D, 15.0D).withinAngle(0.0F, 40.0F))
		).newBehaviorSeries(
			BehaviorSeries.<EnderDragonPatch>builder().weight(100.0F).cooldown(100).simultaneousCooldown(2).volatileBehaviorPointer(true)
				.nextBehavior(Behavior.<EnderDragonPatch>builder().animationBehavior(Animations.DRAGON_BACKJUMP_PREPARE).withinDistance(0.0D, 4.0D).withinAngle(90.0F, 180.0F))
		).newBehaviorSeries(
			BehaviorSeries.<EnderDragonPatch>builder().weight(100.0F).cooldown(240).volatileBehaviorPointer(true)
				.nextBehavior(Behavior.<EnderDragonPatch>builder().animationBehavior(Animations.DRAGON_FIREBALL).withinDistance(15.0D, 30.0D).withinAngleHorizontal(0.0F, 10.0F))
		).newBehaviorSeries(
			BehaviorSeries.<EnderDragonPatch>builder().weight(1000.0F).cooldown(0).volatileBehaviorPointer(true)
				.nextBehavior(Behavior.<EnderDragonPatch>builder().health(0.3F, Health.Comparator.LESS_RATIO).custom((mobpatch) -> mobpatch.getOriginal().getDragonFight().getCrystalsAlive() > 0)
				.behavior((mobpatch) -> {
					mobpatch.getOriginal().getPhaseManager().setPhase(PatchedPhases.CRYSTAL_LINK);
				}))
		).newBehaviorSeries(
			BehaviorSeries.<EnderDragonPatch>builder().weight(10.0F).cooldown(1600).volatileBehaviorPointer(true)
				.nextBehavior(Behavior.<EnderDragonPatch>builder().health(0.5F, Health.Comparator.LESS_RATIO).custom((mobpatch) -> mobpatch.getOriginal().getDragonFight().getCrystalsAlive() > 0)
				.behavior((mobpatch) -> {
					mobpatch.playAnimationSynchronized(Animations.DRAGON_GROUND_TO_FLY, 0.0F);
					mobpatch.getOriginal().getPhaseManager().setPhase(PatchedPhases.FLYING);
					((DragonFlyingPhase)mobpatch.getOriginal().getPhaseManager().getCurrentPhase()).enableAirstrike();
				}))
		);
	
	public static final CombatBehaviors.Builder<EndermanPatch> ENDERMAN_NORMAL_BEHAVIORS = CombatBehaviors.<EndermanPatch>builder()
		.newBehaviorSeries(
			BehaviorSeries.<EndermanPatch>builder().weight(40.0F).volatileBehaviorPointer(true)
				.nextBehavior(Behavior.<EndermanPatch>builder().animationBehavior(Animations.ENDERMAN_KNEE).withinEyeHeight().withinDistance(0.0D, 1.6D))
		).newBehaviorSeries(
			BehaviorSeries.<EndermanPatch>builder().weight(40.0F).volatileBehaviorPointer(true)
				.nextBehavior(Behavior.<EndermanPatch>builder().animationBehavior(Animations.ENDERMAN_KICK_COMBO).withinEyeHeight().withinDistance(0.0D, 2.0D))
		).newBehaviorSeries(
			BehaviorSeries.<EndermanPatch>builder().weight(10.0F).cooldown(60).volatileBehaviorPointer(true)
				.nextBehavior(Behavior.<EndermanPatch>builder().animationBehavior(Animations.ENDERMAN_KICK1).randomChance(0.08F).withinEyeHeight().withinDistance(3.0D, 4.0D))
		).newBehaviorSeries(
			BehaviorSeries.<EndermanPatch>builder().weight(10.0F).cooldown(30).volatileBehaviorPointer(true)
				.nextBehavior(Behavior.<EndermanPatch>builder().animationBehavior(Animations.ENDERMAN_KICK2).randomChance(0.16F).withinEyeHeight().withinDistance(0.0D, 4.0D))
		);
	
	public static final CombatBehaviors.Builder<EndermanPatch> ENDERMAN_TELEPORT_BEHAVIORS = CombatBehaviors.<EndermanPatch>builder()
		.newBehaviorSeries(
			BehaviorSeries.<EndermanPatch>builder().weight(50.0F).volatileBehaviorPointer(true)
				.nextBehavior(Behavior.<EndermanPatch>builder().animationBehavior(Animations.ENDERMAN_TP_KICK1).randomChance(0.1F).withinEyeHeight().withinDistance(8.0F, 100.0F).packetProvider(SPPlayAnimationAndSyncTransform::new))
		).newBehaviorSeries(
			BehaviorSeries.<EndermanPatch>builder().weight(50.0F).volatileBehaviorPointer(true)
				.nextBehavior(Behavior.<EndermanPatch>builder().animationBehavior(Animations.ENDERMAN_TP_KICK2).randomChance(0.1F).withinEyeHeight().withinDistance(8.0F, 100.0F).packetProvider(SPPlayAnimationAndSyncTransform::new))
		);
	
	public static final CombatBehaviors.Builder<EndermanPatch> ENDERMAN_RAGE_BEHAVIORS = CombatBehaviors.<EndermanPatch>builder()
		.newBehaviorSeries(
			BehaviorSeries.<EndermanPatch>builder().weight(10.0F).volatileBehaviorPointer(false)
				.nextBehavior(Behavior.<EndermanPatch>builder().animationBehavior(Animations.ENDERMAN_GRASP).withinEyeHeight().withinDistance(0.0D, 2.0D))
		);
	
	public static final CombatBehaviors.Builder<IronGolemPatch> IRON_GOLEM_BEHAVIORS = CombatBehaviors.<IronGolemPatch>builder()
		.newBehaviorSeries(
			BehaviorSeries.<IronGolemPatch>builder().weight(10.0F).volatileBehaviorPointer(true)
				.nextBehavior(Behavior.<IronGolemPatch>builder().animationBehavior(Animations.GOLEM_ATTACK2).withinEyeHeight().withinDistance(0.0D, 2.0D))
		).newBehaviorSeries(
			BehaviorSeries.<IronGolemPatch>builder().weight(10.0F).volatileBehaviorPointer(true)
				.nextBehavior(Behavior.<IronGolemPatch>builder().animationBehavior(Animations.GOLEM_ATTACK3).withinEyeHeight().withinDistance(0.0D, 2.0D))
				.nextBehavior(Behavior.<IronGolemPatch>builder().animationBehavior(Animations.GOLEM_ATTACK4).withinEyeHeight())
				.nextBehavior(Behavior.<IronGolemPatch>builder().animationBehavior(Animations.GOLEM_ATTACK1).randomChance(0.3F).withinEyeHeight().withinDistance(0.0D, 2.0D))
		);
	
	public static final CombatBehaviors.Builder<HumanoidMobPatch<?>> SKELETON_ARMED_BEHAVIORS = CombatBehaviors.<HumanoidMobPatch<?>>builder()
		.newBehaviorSeries(
			BehaviorSeries.<HumanoidMobPatch<?>>builder().weight(100.0F).canBeInterrupted(true).volatileBehaviorPointer(false)
				.nextBehavior(Behavior.<HumanoidMobPatch<?>>builder().animationBehavior(Animations.WITHER_SKELETON_ATTACK1).withinEyeHeight().withinDistance(0.0D, 1.8D))
				.nextBehavior(Behavior.<HumanoidMobPatch<?>>builder().animationBehavior(Animations.WITHER_SKELETON_ATTACK2).withinEyeHeight().withinDistance(0.0D, 1.8D))
				.nextBehavior(Behavior.<HumanoidMobPatch<?>>builder().animationBehavior(Animations.WITHER_SKELETON_ATTACK3).withinEyeHeight().withinDistance(0.0D, 1.8D))
		);
	
	public static final CombatBehaviors.Builder<WitherPatch> WITHER_BEHAVIORS = CombatBehaviors.<WitherPatch>builder()
		.newBehaviorSeries(
			BehaviorSeries.<WitherPatch>builder().weight(3000.0F).volatileBehaviorPointer(true)
				.nextBehavior(Behavior.<WitherPatch>builder().emptyBehavior().health(150.0F, Comparator.GREATER_ABSOLUTE))
		).newBehaviorSeries(
			BehaviorSeries.<WitherPatch>builder().weight(100.0F).cooldown(200).volatileBehaviorPointer(true)
				.nextBehavior(Behavior.<WitherPatch>builder().animationBehavior(Animations.WITHER_CHARGE).randomChance(1.0F).withinDistance(6.0D, 10.0D).withinAngleHorizontal(0.0D, 30.0D))
		).newBehaviorSeries(
			BehaviorSeries.<WitherPatch>builder().weight(500.0F).cooldown(60).volatileBehaviorPointer(true)
				.nextBehavior(Behavior.<WitherPatch>builder().animationBehavior(Animations.WITHER_SWIRL).health(150.0F, Comparator.LESS_ABSOLUTE).withinDistance(0.0D, 2.5D).withinAngle(0.0D, 60.0D))
		).newBehaviorSeries(
			BehaviorSeries.<WitherPatch>builder().weight(500.0F).cooldown(180).volatileBehaviorPointer(true)
				.nextBehavior(Behavior.<WitherPatch>builder().animationBehavior(Animations.WITHER_BACKFLIP).health(150.0F, Comparator.LESS_ABSOLUTE).withinDistance(0.0D, 2.5D).withinAngle(0.0D, 20.0D))
		).newBehaviorSeries(
			BehaviorSeries.<WitherPatch>builder().weight(50.0F).cooldown(200).volatileBehaviorPointer(true)
				.nextBehavior(Behavior.<WitherPatch>builder().animationBehavior(Animations.WITHER_BEAM).withinDistance(3.0D, 20.0D).withinAngleHorizontal(0.0D, 20.0D))
		);
	
	public static final CombatBehaviors.Builder<HumanoidMobPatch<?>> VINDICATOR_BEHAVIORS = CombatBehaviors.<HumanoidMobPatch<?>>builder()
		.newBehaviorSeries(
			BehaviorSeries.<HumanoidMobPatch<?>>builder().weight(100.0F).canBeInterrupted(true).volatileBehaviorPointer(false)
				.nextBehavior(Behavior.<HumanoidMobPatch<?>>builder().animationBehavior(Animations.VINDICATOR_SWING_AXE1).withinEyeHeight().withinDistance(0.0D, 3.0D))
				.nextBehavior(Behavior.<HumanoidMobPatch<?>>builder().animationBehavior(Animations.VINDICATOR_SWING_AXE2).withinEyeHeight().withinDistance(0.0D, 3.0D))
				.nextBehavior(Behavior.<HumanoidMobPatch<?>>builder().animationBehavior(Animations.VINDICATOR_SWING_AXE3).withinEyeHeight().withinDistance(0.0D, 3.0D))
		);
	
	public static final CombatBehaviors.Builder<HumanoidMobPatch<?>> ZOMBIE_BEHAVIORS = CombatBehaviors.<HumanoidMobPatch<?>>builder()
			.newBehaviorSeries(
				BehaviorSeries.<HumanoidMobPatch<?>>builder().weight(100.0F).canBeInterrupted(true).volatileBehaviorPointer(false)
					.nextBehavior(Behavior.<HumanoidMobPatch<?>>builder().animationBehavior(Animations.ZOMBIE_ATTACK1).withinEyeHeight().withinDistance(0.0D, 1.8D))
					.nextBehavior(Behavior.<HumanoidMobPatch<?>>builder().animationBehavior(Animations.ZOMBIE_ATTACK2).withinEyeHeight().withinDistance(0.0D, 1.8D))
					.nextBehavior(Behavior.<HumanoidMobPatch<?>>builder().animationBehavior(Animations.ZOMBIE_ATTACK3).withinEyeHeight().withinDistance(0.0D, 1.8D))
			);
}