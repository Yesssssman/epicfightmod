package yesman.epicfight.capabilities.entity.mob;

import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.monster.DrownedEntity;
import net.minecraft.item.Items;
import yesman.epicfight.animation.LivingMotion;
import yesman.epicfight.client.animation.AnimatorClient;
import yesman.epicfight.entity.ai.AttackPatternGoal;
import yesman.epicfight.entity.ai.ChasingGoal;
import yesman.epicfight.entity.ai.RangeAttackMobGoal;
import yesman.epicfight.gamedata.Animations;
import yesman.epicfight.gamedata.AttackCombos;

public class DrownedData extends ZombieData<DrownedEntity> {
	@Override
	protected void initAnimator(AnimatorClient animator) {
		animator.addLivingAnimation(LivingMotion.IDLE, Animations.ZOMBIE_IDLE);
		animator.addLivingAnimation(LivingMotion.WALK, Animations.ZOMBIE_WALK);
		animator.addLivingAnimation(LivingMotion.FALL, Animations.BIPED_FALL);
		animator.addLivingAnimation(LivingMotion.MOUNT, Animations.BIPED_MOUNT);
		animator.addLivingAnimation(LivingMotion.DEATH, Animations.BIPED_DEATH);
	}
	
	@Override
	public void setAIAsUnarmed() {
		this.orgEntity.goalSelector.addGoal(1, new DrownedChasingGoal(this, this.orgEntity, 1.0D, false));
		this.orgEntity.goalSelector.addGoal(0, new AttackPatternGoal(this, this.orgEntity, 0.0D, 1.5D, true, AttackCombos.BIPED_UNARMED));
	}
	
	@Override
	public void setAIAsArmed() {
		if (this.orgEntity.getHeldItemMainhand().getItem() == Items.TRIDENT) {
			this.orgEntity.goalSelector.addGoal(0, new AttackPatternGoal(this, this.orgEntity, 0.0D, 2.0D, true, AttackCombos.DROWNED_ARMED_SPEAR));
			this.orgEntity.goalSelector.addGoal(0, new TridentAttackGoal(this, this.orgEntity));
		} else {
			this.orgEntity.goalSelector.addGoal(0, new AttackPatternGoal(this, this.orgEntity, 0.0D, 2.0D, true, AttackCombos.BIPED_ARMED));
			this.orgEntity.goalSelector.addGoal(1, new DrownedChasingGoal(this, this.orgEntity, 1.0D, false));
		}
	}
	
	static class DrownedChasingGoal extends ChasingGoal {
		private final DrownedEntity drownedEntity;

		public DrownedChasingGoal(DrownedData mobdata, DrownedEntity host, double speedIn, boolean useLongMemory) {
			super(mobdata, host, speedIn, useLongMemory);
			this.drownedEntity = host;
		}
		
		@Override
		public boolean shouldExecute() {
	        return super.shouldExecute() && this.drownedEntity.shouldAttack(this.drownedEntity.getAttackTarget());
	    }

		@Override
		public boolean shouldContinueExecuting() {
			return super.shouldContinueExecuting() && this.drownedEntity.shouldAttack(this.drownedEntity.getAttackTarget());
	    }
	}
	
	class TridentAttackGoal extends RangeAttackMobGoal {
		private final DrownedEntity hostEntity;
		
		public TridentAttackGoal(DrownedData entitydata, IRangedAttackMob hostEntity) {
	    	super(hostEntity, entitydata, Animations.BIPED_MOB_THROW, 1.0, 40, 10.0F, 3);
	        this.hostEntity = (DrownedEntity)hostEntity;
	    }
	    
		@Override
		public boolean shouldExecute() {
	       return super.shouldExecute();
	    }
	    
		@Override
		public void startExecuting() {
	       super.startExecuting();
	       this.hostEntity.setAggroed(true);
	    }
	    
	    @Override
		public void resetTask() {
	       super.resetTask();
	       this.hostEntity.setAggroed(false);
	    }
	}
}