package yesman.epicfight.world.capabilities.entitypatch.mob;

import java.util.Set;

import io.netty.buffer.ByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.behavior.MeleeAttack;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.behavior.RunIf;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.server.SPSpawnData;
import yesman.epicfight.world.capabilities.entitypatch.Faction;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;
import yesman.epicfight.world.entity.ai.brain.BrainRecomposer;
import yesman.epicfight.world.entity.ai.brain.task.AnimatedCombatBehavior;
import yesman.epicfight.world.entity.ai.brain.task.BackUpIfTooCloseStopInaction;
import yesman.epicfight.world.entity.ai.brain.task.MoveToTargetSinkStopInaction;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors;

public class PiglinPatch extends HumanoidMobPatch<Piglin> {
	public PiglinPatch() {
		super(Faction.PIGLINS);
	}
	
	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.original.getAttribute(EpicFightAttributes.IMPACT.get()).setBaseValue(1.0F);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void initAnimator(ClientAnimator clientAnimator) {
		clientAnimator.addLivingAnimation(LivingMotions.IDLE, Animations.PIGLIN_IDLE);
		clientAnimator.addLivingAnimation(LivingMotions.FALL, Animations.BIPED_FALL);
		clientAnimator.addLivingAnimation(LivingMotions.MOUNT, Animations.BIPED_MOUNT);
		clientAnimator.addLivingAnimation(LivingMotions.CELEBRATE, EpicFightMod.getInstance().animationManager.findAnimationById(EpicFightMod.MODID.hashCode(), Animations.PIGLIN_CELEBRATE1.getId() + this.original.getRandom().nextInt(3)));
		clientAnimator.addLivingAnimation(LivingMotions.ADMIRE, Animations.PIGLIN_ADMIRE);
		clientAnimator.addLivingAnimation(LivingMotions.WALK, Animations.PIGLIN_WALK);
		clientAnimator.addLivingAnimation(LivingMotions.CHASE, Animations.PIGLIN_WALK);
		clientAnimator.addLivingAnimation(LivingMotions.DEATH, Animations.PIGLIN_DEATH);
		clientAnimator.addLivingAnimation(LivingMotions.RELOAD, Animations.BIPED_CROSSBOW_RELOAD);
		clientAnimator.addLivingAnimation(LivingMotions.AIM, Animations.BIPED_CROSSBOW_AIM);
		clientAnimator.addLivingAnimation(LivingMotions.SHOT, Animations.BIPED_CROSSBOW_SHOT);
		clientAnimator.setCurrentMotionsAsDefault();
	}
	
	@Override
	public void onStartTracking(ServerPlayer trackingPlayer) {
		if (this.original.isBaby()) {
			SPSpawnData packet = new SPSpawnData(this.original.getId());
			EpicFightNetworkManager.sendToPlayer(packet, trackingPlayer);
		}
		
		super.onStartTracking(trackingPlayer);
	}
	
	@Override
	public void processSpawnData(ByteBuf buf) {
		ClientAnimator animator = this.getClientAnimator();
		animator.addLivingAnimation(LivingMotions.WALK, Animations.BIPED_RUN);
		animator.setCurrentMotionsAsDefault();
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
		if (this.getOriginal().getOffhandItem().is(ItemTags.PIGLIN_LOVED))
			this.currentLivingMotion = LivingMotions.ADMIRE;
		else if (this.original.isDancing())
			this.currentLivingMotion = LivingMotions.CELEBRATE;
		else
			super.commonAggressiveRangedMobUpdateMotion(considerInaction);
	}
	
	@Override
	protected void selectGoalToRemove(Set<Goal> toRemove) {
		BrainRecomposer.removeBehavior(this.original.getBrain(), Activity.FIGHT, 13, MeleeAttack.class);
	}
	
	@Override
	public void setAIAsInfantry(boolean holdingRanedWeapon) {
		CombatBehaviors.Builder<HumanoidMobPatch<?>> builder = this.getHoldingItemWeaponMotionBuilder();
		
		if (builder != null) {
			BrainRecomposer.replaceBehavior(this.original.getBrain(), Activity.FIGHT, 13, AnimatedCombatBehavior.class, new AnimatedCombatBehavior<>(this, builder.build(this)));
		}
		
		BrainRecomposer.replaceBehavior(this.original.getBrain(), Activity.FIGHT, 11, RunIf.class, new RunIf<>((entity) -> entity.isHolding(is -> is.getItem() instanceof CrossbowItem), new BackUpIfTooCloseStopInaction<>(5, 0.75F)));
		BrainRecomposer.replaceBehavior(this.original.getBrain(), Activity.CORE, 1, MoveToTargetSink.class, new MoveToTargetSinkStopInaction());
		BrainRecomposer.removeBehavior(this.original.getBrain(), Activity.CELEBRATE, 15, RunOne.class);
	}
	
	@Override
	public void setAIAsMounted(Entity ridingEntity) {
		
	}
}