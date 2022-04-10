package yesman.epicfight.world.capabilities.entitypatch.mob;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.behavior.MeleeAttack;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.MobCombatBehaviors;
import yesman.epicfight.gameasset.Models;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;
import yesman.epicfight.world.entity.ai.brain.BrainRemodeler;
import yesman.epicfight.world.entity.ai.brain.task.AnimatedFightBehavior;

public class PiglinBrutePatch extends HumanoidMobPatch<PiglinBrute> {
	public PiglinBrutePatch() {
		super(Faction.PIGLIN_ARMY);
	}
	
	@Override
	public void onJoinWorld(PiglinBrute entityIn, EntityJoinWorldEvent event) {
		super.onJoinWorld(entityIn, event);
		BrainRemodeler.replaceBehavior(this.original.getBrain(), Activity.FIGHT, 12, MeleeAttack.class, new AnimatedFightBehavior<>(this, MobCombatBehaviors.BIPED_ARMED_BEHAVIORS.build(this)));
	}
	
	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.original.getAttribute(EpicFightAttributes.STUN_ARMOR.get()).setBaseValue(8.0F);
		this.original.getAttribute(EpicFightAttributes.IMPACT.get()).setBaseValue(3.0F);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void initAnimator(ClientAnimator clientAnimator) {
		clientAnimator.addLivingMotion(LivingMotion.IDLE, Animations.PIGLIN_IDLE);
		clientAnimator.addLivingMotion(LivingMotion.WALK, Animations.PIGLIN_WALK);
		clientAnimator.addLivingMotion(LivingMotion.FALL, Animations.BIPED_FALL);
		clientAnimator.addLivingMotion(LivingMotion.MOUNT, Animations.BIPED_MOUNT);
		clientAnimator.addLivingMotion(LivingMotion.DEATH, Animations.PIGLIN_DEATH);
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
		super.humanoidEntityUpdateMotion(considerInaction);
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
	
	@Override
	public OpenMatrix4f getModelMatrix(float partialTicks) {
		return super.getModelMatrix(partialTicks).scale(1.1F, 1.1F, 1.1F);
	}
}