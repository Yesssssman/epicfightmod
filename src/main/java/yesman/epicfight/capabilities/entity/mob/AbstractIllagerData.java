package yesman.epicfight.capabilities.entity.mob;

import net.minecraft.entity.monster.AbstractIllagerEntity;
import yesman.epicfight.animation.LivingMotion;
import yesman.epicfight.animation.types.StaticAnimation;
import yesman.epicfight.client.animation.AnimatorClient;
import yesman.epicfight.gamedata.Animations;
import yesman.epicfight.gamedata.Models;
import yesman.epicfight.model.Model;
import yesman.epicfight.utils.game.IExtendedDamageSource.StunType;

public abstract class AbstractIllagerData<T extends AbstractIllagerEntity> extends BipedMobData<T> {
	public AbstractIllagerData(Faction faction) {
		super(faction);
	}
	
	@Override
	protected void initAnimator(AnimatorClient animatorClient) {
		super.initAnimator(animatorClient);
		animatorClient.addLivingAnimation(LivingMotion.IDLE, Animations.ILLAGER_IDLE);
		animatorClient.addLivingAnimation(LivingMotion.WALK, Animations.ILLAGER_WALK);
		animatorClient.addLivingAnimation(LivingMotion.DEATH, Animations.BIPED_DEATH);
	}
	
	@Override
	public StaticAnimation getHitAnimation(StunType stunType) {
		if(stunType == StunType.LONG)
			return Animations.BIPED_HIT_LONG;
		else
			return Animations.BIPED_HIT_SHORT;
	}
	
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return modelDB.illager;
	}
}