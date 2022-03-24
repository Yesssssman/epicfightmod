package yesman.epicfight.world.capabilities.entitypatch.mob;

import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.item.Items;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.MobCombatBehaviors;
import yesman.epicfight.world.entity.ai.goal.AttackPatternGoal;
import yesman.epicfight.world.entity.ai.goal.ChasingGoal;
import yesman.epicfight.world.entity.ai.goal.RangeAttackMobGoal;

public class DrownedPatch extends ZombiePatch<Drowned> {
	@Override
	public void initAnimator(ClientAnimator animator) {
		animator.addLivingAnimation(LivingMotion.IDLE, Animations.ZOMBIE_IDLE);
		animator.addLivingAnimation(LivingMotion.WALK, Animations.ZOMBIE_WALK);
		animator.addLivingAnimation(LivingMotion.FALL, Animations.BIPED_FALL);
		animator.addLivingAnimation(LivingMotion.MOUNT, Animations.BIPED_MOUNT);
		animator.addLivingAnimation(LivingMotion.DEATH, Animations.BIPED_DEATH);
	}
	
	@Override
	public void setAIAsUnarmed() {
		this.original.goalSelector.addGoal(1, new DrownedChasingGoal(this, this.original, 1.0D, false));
		this.original.goalSelector.addGoal(0, new AttackPatternGoal(this, this.original, 0.0D, 1.5D, true, MobCombatBehaviors.BIPED_UNARMED));
	}
	
	@Override
	public void setAIAsArmed() {
		if (this.original.getMainHandItem().getItem() == Items.TRIDENT) {
			this.original.goalSelector.addGoal(0, new AttackPatternGoal(this, this.original, 0.0D, 2.0D, true, MobCombatBehaviors.DROWNED_ARMED_SPEAR));
			this.original.goalSelector.addGoal(0, new TridentAttackGoal(this, this.original));
		} else {
			this.original.goalSelector.addGoal(0, new AttackPatternGoal(this, this.original, 0.0D, 2.0D, true, MobCombatBehaviors.BIPED_ARMED));
			this.original.goalSelector.addGoal(1, new DrownedChasingGoal(this, this.original, 1.0D, false));
		}
	}
	
	static class DrownedChasingGoal extends ChasingGoal {
		private final Drowned drowned;

		public DrownedChasingGoal(DrownedPatch drownedpatch, Drowned drowned, double speedIn, boolean useLongMemory) {
			super(drownedpatch, drowned, speedIn, useLongMemory);
			this.drowned = drowned;
		}
		
		@Override
		public boolean canUse() {
	        return super.canUse() && this.drowned.okTarget(this.drowned.getTarget());
	    }
		
		@Override
		public boolean canContinueToUse() {
			return super.canContinueToUse() && this.drowned.okTarget(this.drowned.getTarget());
	    }
	}
	
	class TridentAttackGoal extends RangeAttackMobGoal {
		private final Drowned drowned;
		
		public TridentAttackGoal(DrownedPatch drownedpatch, RangedAttackMob hostEntity) {
	    	super(hostEntity, drownedpatch, Animations.BIPED_MOB_THROW, 1.0, 40, 10.0F, 13);
	        this.drowned = (Drowned)hostEntity;
	    }
	    
		@Override
		public boolean canUse() {
	       return super.canUse();
	    }
	    
		@Override
		public void start() {
	       super.start();
	       this.drowned.setAggressive(true);
	    }
	    
	    @Override
		public void stop() {
	       super.stop();
	       this.drowned.setAggressive(false);
	    }
	}
}