package maninhouse.epicfight.capabilities.entity.mob;

import maninhouse.epicfight.animation.LivingMotion;
import maninhouse.epicfight.client.animation.AnimatorClient;
import maninhouse.epicfight.entity.ai.AttackPatternGoal;
import maninhouse.epicfight.entity.ai.ChasingGoal;
import maninhouse.epicfight.gamedata.Animations;
import maninhouse.epicfight.gamedata.Models;
import maninhouse.epicfight.model.Model;
import net.minecraft.entity.MobEntity;

public class SkeletonData<T extends MobEntity> extends BipedMobData<T> {
	public SkeletonData() {
		super(Faction.UNDEAD);
	}
	
	public SkeletonData(Faction faction) {
		super(faction);
	}
	
	@Override
	protected void initAnimator(AnimatorClient animatorClient) {
		super.commonBipedCreatureAnimatorInit(animatorClient);
		super.initAnimator(animatorClient);
		animatorClient.addOverridenLivingMotion(LivingMotion.AIM, Animations.BIPED_BOW_AIM);
		animatorClient.addOverridenLivingMotion(LivingMotion.SHOT, Animations.BIPED_BOW_SHOT);
	}
	
	@Override
	public void updateMotion() {
		super.commonRangedAttackCreatureUpdateMotion();
	}
	
	@Override
	public void setAIAsArmed() {
		this.orgEntity.goalSelector.addGoal(0, new AttackPatternGoal(this, this.orgEntity, 0.0D, 1.8D, true, MobAttackPatterns.SKELETON_ARMED));
		this.orgEntity.goalSelector.addGoal(1, new ChasingGoal(this, this.orgEntity, 1.2D, true, Animations.SKELETON_CHASE, Animations.SKELETON_WALK));
	}
	
	@Override
	public void setAIAsUnarmed() {
		this.orgEntity.goalSelector.addGoal(1, new ChasingGoal(this, this.orgEntity, 1.0D, false, null, null, false));
		this.orgEntity.goalSelector.addGoal(0, new AttackPatternGoal(this, this.orgEntity, 0.0D, 1.8D, true, MobAttackPatterns.BIPED_UNARMED));
	}
	
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return modelDB.ENTITY_SKELETON;
	}
}