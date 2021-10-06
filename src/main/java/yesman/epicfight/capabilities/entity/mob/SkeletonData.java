package yesman.epicfight.capabilities.entity.mob;

import net.minecraft.entity.MobEntity;
import yesman.epicfight.animation.LivingMotion;
import yesman.epicfight.client.animation.AnimatorClient;
import yesman.epicfight.entity.ai.AttackPatternGoal;
import yesman.epicfight.entity.ai.ChasingGoal;
import yesman.epicfight.gamedata.Animations;
import yesman.epicfight.gamedata.AttackCombos;
import yesman.epicfight.gamedata.Models;
import yesman.epicfight.model.Model;

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
		animatorClient.addOverwritingLivingMotion(LivingMotion.AIM, Animations.BIPED_BOW_AIM);
		animatorClient.addOverwritingLivingMotion(LivingMotion.SHOT, Animations.BIPED_BOW_SHOT);
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
		super.commonRangedAttackCreatureUpdateMotion(considerInaction);
	}
	
	@Override
	public void setAIAsArmed() {
		this.orgEntity.goalSelector.addGoal(0, new AttackPatternGoal(this, this.orgEntity, 0.0D, 1.8D, true, AttackCombos.SKELETON_ARMED));
		this.orgEntity.goalSelector.addGoal(1, new ChasingGoal(this, this.orgEntity, 1.2D, true, Animations.SKELETON_CHASE, Animations.SKELETON_WALK));
	}
	
	@Override
	public void setAIAsUnarmed() {
		this.orgEntity.goalSelector.addGoal(1, new ChasingGoal(this, this.orgEntity, 1.0D, false, null, null, false));
		this.orgEntity.goalSelector.addGoal(0, new AttackPatternGoal(this, this.orgEntity, 0.0D, 1.8D, true, AttackCombos.BIPED_UNARMED));
	}
	
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return modelDB.skeleton;
	}
}