package yesman.epicfight.world.capabilities.entitypatch.mob;

import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.behavior.MeleeAttack;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.MobCombatBehaviors;
import yesman.epicfight.gameasset.Models;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.client.CPReqSpawnInfo;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;
import yesman.epicfight.world.entity.ai.brain.BrainRemodeler;
import yesman.epicfight.world.entity.ai.brain.task.AnimatedFightBehavior;

public class PiglinPatch extends HumanoidMobPatch<Piglin> {
	public PiglinPatch() {
		super(Faction.PIGLIN_ARMY);
	}
	
	@Override
	public void onJoinWorld(Piglin entityIn, EntityJoinWorldEvent event) {
		super.onJoinWorld(entityIn, event);
		
		BrainRemodeler.replaceBehavior(this.original.getBrain(), Activity.FIGHT, 13, MeleeAttack.class, new AnimatedFightBehavior(this, MobCombatBehaviors.BIPED_ARMED_ATTACKS.build(this)));
		BrainRemodeler.removeBehavior(this.original.getBrain(), Activity.CELEBRATE, 15, RunOne.class);
	}
	
	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.original.getAttribute(EpicFightAttributes.IMPACT.get()).setBaseValue(1.0F);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void initAnimator(ClientAnimator clientAnimator) {
		clientAnimator.addLivingAnimation(LivingMotion.IDLE, Animations.PIGLIN_IDLE);
		clientAnimator.addLivingAnimation(LivingMotion.FALL, Animations.BIPED_FALL);
		clientAnimator.addLivingAnimation(LivingMotion.MOUNT, Animations.BIPED_MOUNT);
		clientAnimator.addLivingAnimation(LivingMotion.CELEBRATE, EpicFightMod.getInstance().animationManager.findAnimation(EpicFightMod.MODID.hashCode(), Animations.PIGLIN_CELEBRATE1.getId() + this.original.getRandom().nextInt(3)));
		clientAnimator.addLivingAnimation(LivingMotion.ADMIRE, Animations.PIGLIN_ADMIRE);
		clientAnimator.addLivingAnimation(LivingMotion.WALK, Animations.PIGLIN_WALK);
		clientAnimator.addLivingAnimation(LivingMotion.DEATH, Animations.PIGLIN_DEATH);
		clientAnimator.addCompositeAnimation(LivingMotion.RELOAD, Animations.BIPED_CROSSBOW_RELOAD);
		clientAnimator.addCompositeAnimation(LivingMotion.AIM, Animations.BIPED_CROSSBOW_AIM);
		clientAnimator.addCompositeAnimation(LivingMotion.SHOT, Animations.BIPED_CROSSBOW_SHOT);
	}
	
	@Override
	public void postInit() {
		super.postInit();

		if (this.isLogicalClient()) {
			ClientAnimator animator = this.getClientAnimator();
			if (this.original.isBaby()) {
				animator.addLivingAnimation(LivingMotion.WALK, Animations.BIPED_RUN);
			}
			EpicFightNetworkManager.sendToServer(new CPReqSpawnInfo(this.original.getId()));
		}
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
		if (this.getOriginal().getOffhandItem().is(ItemTags.PIGLIN_LOVED))
			this.currentMotion = LivingMotion.ADMIRE;
		else if (this.original.isDancing())
			this.currentMotion = LivingMotion.CELEBRATE;
		else
			super.humanoidRangedEntityUpdateMotion(considerInaction);
	}

	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return modelDB.piglin;
	}

	public void setAIAsUnarmed() {

	}

	public void setAIAsArmed() {

	}

	public void setAIAsMounted(Entity ridingEntity) {

	}

	public void setAIAsRanged() {

	}
}