package yesman.epicfight.world.capabilities.entitypatch.mob;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.monster.Vex;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.entitypatch.Faction;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;
import yesman.epicfight.world.damagesource.StunType;

public class VexPatch extends MobPatch<Vex> {
	public VexPatch() {
		super(Faction.ILLAGER);
	}
	
	@Override
	protected void initAI() {
		super.initAI();
		
        this.original.goalSelector.addGoal(0, new ChargeAttackGoal());
        this.original.goalSelector.addGoal(1, new StopStandGoal());
	}
	
	@Override
	protected void selectGoalToRemove(Set<Goal> toRemove) {
		super.selectGoalToRemove(toRemove);
		
		Iterator<WrappedGoal> iterator = this.original.goalSelector.getAvailableGoals().iterator();
		
		int index = 0;
		while (iterator.hasNext()) {
			WrappedGoal goal = iterator.next();
			Goal inner = goal.getGoal();
			
			if (index == 1) {
				toRemove.add(inner);
				break;
			}
			
			index++;
        }
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void initAnimator(ClientAnimator clientAnimator) {
		clientAnimator.addLivingAnimation(LivingMotions.IDLE, Animations.VEX_IDLE);
		clientAnimator.addLivingAnimation(LivingMotions.DEATH, Animations.VEX_DEATH);
		clientAnimator.addLivingAnimation(LivingMotions.IDLE, Animations.VEX_FLIPPING);
		clientAnimator.setCurrentMotionsAsDefault();
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
		if (this.original.getHealth() <= 0.0F) {
			currentLivingMotion = LivingMotions.DEATH;
		} else if (this.state.inaction() && considerInaction) {
			currentLivingMotion = LivingMotions.INACTION;
		} else {
			currentLivingMotion = LivingMotions.IDLE;
			currentCompositeMotion = LivingMotions.IDLE;
		}
	}
	
	@Override
	public void onAttackBlocked(DamageSource damageSource, LivingEntityPatch<?> opponent) {
		this.original.setPos(opponent.getOriginal().getEyePosition().add(opponent.getOriginal().getLookAngle()));
		this.playAnimationSynchronized(Animations.VEX_NEUTRALIZED, 0.0F);
	}
	
	@Override
	public StaticAnimation getHitAnimation(StunType stunType) {
		return Animations.VEX_HIT;
	}
	
	@Override
	public OpenMatrix4f getModelMatrix(float partialTicks) {
		return super.getModelMatrix(partialTicks).scale(0.4F, 0.4F, 0.4F);
	}
	
	class StopStandGoal extends Goal {
		public StopStandGoal() {
			this.setFlags(EnumSet.of(Goal.Flag.MOVE));
		}

		@Override
		public boolean canUse() {
			return VexPatch.this.getEntityState().inaction();
		}

		@Override
		public void start() {
			VexPatch.this.original.getMoveControl().setWantedPosition(VexPatch.this.original.getX(), VexPatch.this.original.getY(), VexPatch.this.original.getZ(), 0.25F);
		}
	}
	
	class ChargeAttackGoal extends Goal {
		private int chargingCounter;
		
		public ChargeAttackGoal() {
			this.setFlags(EnumSet.of(Flag.MOVE));
		}
		
		@Override
		public boolean canUse() {
			if (VexPatch.this.original.getTarget() != null && !VexPatch.this.getEntityState().inaction() && VexPatch.this.original.getRandom().nextInt(10) == 0) {
				double distance = VexPatch.this.original.distanceToSqr(VexPatch.this.original.getTarget());
				return distance < 50.0D;
			} else {
				return false;
			}
		}
	    
		@Override
		public boolean canContinueToUse() {
			return this.chargingCounter > 0;
		}
		
		@Override
		public void start() {
			VexPatch.this.original.getMoveControl().setWantedPosition(VexPatch.this.original.getX(), VexPatch.this.original.getY(), VexPatch.this.original.getZ(), 0.25F);
	    	VexPatch.this.playAnimationSynchronized(Animations.VEX_CHARGE, 0.0F);
	    	VexPatch.this.original.playSound(SoundEvents.VEX_CHARGE, 1.0F, 1.0F);
	    	VexPatch.this.original.setIsCharging(true);
	    	this.chargingCounter = 20;
	    }
	    
		@Override
		public void stop() {
			VexPatch.this.original.setIsCharging(false);
		}
		
		@Override
		public void tick() {
			--this.chargingCounter;
		}
	}
}