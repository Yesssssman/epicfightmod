package yesman.epicfight.world.capabilities.entitypatch.mob;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.task.AttackStrafingTask;
import net.minecraft.entity.ai.brain.task.AttackTargetTask;
import net.minecraft.entity.ai.brain.task.FirstShuffledTask;
import net.minecraft.entity.ai.brain.task.SupplementedTask;
import net.minecraft.entity.ai.brain.task.WalkToTargetTask;
import net.minecraft.entity.monster.piglin.PiglinEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.Models;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.server.SPSpawnData;
import yesman.epicfight.world.capabilities.entitypatch.Faction;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;
import yesman.epicfight.world.entity.ai.brain.BrainRecomposer;
import yesman.epicfight.world.entity.ai.brain.task.AnimatedCombatBehavior;
import yesman.epicfight.world.entity.ai.brain.task.MoveToTargetSinkStopInaction;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors;

public class PiglinPatch extends HumanoidMobPatch<PiglinEntity> {
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
	public void onStartTracking(ServerPlayerEntity trackingPlayer) {
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
		if (this.getOriginal().getOffhandItem().getItem().is(ItemTags.PIGLIN_LOVED))
			this.currentLivingMotion = LivingMotions.ADMIRE;
		else if (this.original.isDancing())
			this.currentLivingMotion = LivingMotions.CELEBRATE;
		else
			super.commonAggressiveRangedMobUpdateMotion(considerInaction);
	}

	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return modelDB.piglin;
	}
	
	@Override
	public void setAIAsInfantry(boolean holdingRanedWeapon) {
		CombatBehaviors.Builder<HumanoidMobPatch<?>> builder = this.getHoldingItemWeaponMotionBuilder();
		
		if (builder != null) {
			BrainRecomposer.replaceBehavior(this.original.getBrain(), Activity.FIGHT, 13, AttackTargetTask.class, new AnimatedCombatBehavior<>(this, builder.build(this)));
		}
		
		BrainRecomposer.replaceBehavior(this.original.getBrain(), Activity.FIGHT, 11, SupplementedTask.class, new SupplementedTask<>((e) -> e.isHolding(item -> item instanceof CrossbowItem), new AttackStrafingTask<>(5, 0.75F)));
		BrainRecomposer.replaceBehavior(this.original.getBrain(), Activity.CORE, 1, WalkToTargetTask.class, new MoveToTargetSinkStopInaction());
		BrainRecomposer.removeBehavior(this.original.getBrain(), Activity.CELEBRATE, 15, FirstShuffledTask.class);
	}
	
	@Override
	public void setAIAsMounted(Entity ridingEntity) {
		
	}
}