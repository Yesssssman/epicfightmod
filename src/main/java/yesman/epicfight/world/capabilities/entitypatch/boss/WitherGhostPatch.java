package yesman.epicfight.world.capabilities.entitypatch.boss;

import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.api.utils.ExtendedDamageSource.StunType;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.Models;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;
import yesman.epicfight.world.entity.WitherGhostClone;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public class WitherGhostPatch extends MobPatch<WitherGhostClone> {
	@Override
	public void onJoinWorld(WitherGhostClone original, EntityJoinWorldEvent event) {
		super.onJoinWorld(original, event);
		this.getAnimator().playAnimation(Animations.WITHER_CHARGE, 0.0F);
		
		if (this.isLogicalClient()) {
			this.playSound(SoundEvents.WITHER_AMBIENT, -0.1F, 0.1F);
		}
	}
	
	@Override
	public void initAnimator(ClientAnimator clientAnimator) {
		clientAnimator.addLivingAnimation(LivingMotions.IDLE, Animations.WITHER_IDLE);
		clientAnimator.addLivingAnimation(LivingMotions.DEATH, Animations.WITHER_IDLE);
		clientAnimator.setCurrentMotionsAsDefault();
	}
	
	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.original.getAttribute(EpicFightAttributes.IMPACT.get()).setBaseValue(3.0F);
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
		this.currentLivingMotion = LivingMotions.IDLE;
	}
	
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return modelDB.wither;
	}
	
	@Override
	public StaticAnimation getHitAnimation(StunType stunType) {
		return null;
	}
}