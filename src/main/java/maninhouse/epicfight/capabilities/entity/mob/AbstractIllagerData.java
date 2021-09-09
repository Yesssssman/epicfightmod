package maninhouse.epicfight.capabilities.entity.mob;

import maninhouse.epicfight.animation.LivingMotion;
import maninhouse.epicfight.animation.types.StaticAnimation;
import maninhouse.epicfight.client.animation.AnimatorClient;
import maninhouse.epicfight.gamedata.Animations;
import maninhouse.epicfight.gamedata.Models;
import maninhouse.epicfight.model.Model;
import maninhouse.epicfight.utils.game.IExtendedDamageSource.StunType;
import net.minecraft.entity.monster.AbstractIllagerEntity;

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
		return modelDB.ENTITY_ILLAGER;
	}
}