package yesman.epicfight.world.capabilities.entitypatch.mob;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.monster.Vex;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.api.utils.game.ExtendedDamageSource.StunType;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.Models;
import yesman.epicfight.network.server.SPPlayAnimationAndSetTarget;
import yesman.epicfight.world.capabilities.entitypatch.Faction;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;
import yesman.epicfight.world.entity.eventlistener.HurtEventPre;

public class VexPatch extends MobPatch<Vex> {
	private float targetXRotO;
	private float targetXRot;

	public VexPatch() {
		super(Faction.ILLAGER);
	}

	@Override
	protected void initAI() {
		super.initAI();
		
		Set<WrappedGoal> goals = this.original.goalSelector.getAvailableGoals();
		Iterator<WrappedGoal> iterator = goals.iterator();
		Goal toRemove = null;
		int iterCount = 0;
		while (iterator.hasNext()) {
			WrappedGoal goal = iterator.next();
			Goal inner = goal.getGoal();

			if (iterCount == 1) {
				toRemove = inner;
				break;
			}
			
			iterCount++;
        }
        
        if (toRemove != null) {
        	this.original.goalSelector.removeGoal(toRemove);
        }
        
        this.original.goalSelector.addGoal(0, new ChargeAttackGoal());
        this.original.goalSelector.addGoal(1, new StopStandGoal());
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void initAnimator(ClientAnimator clientAnimator) {
		clientAnimator.addLivingAnimation(LivingMotion.IDLE, Animations.VEX_IDLE);
		clientAnimator.addLivingAnimation(LivingMotion.DEATH, Animations.VEX_DEATH);
		clientAnimator.addLivingAnimation(LivingMotion.IDLE, Animations.VEX_FLIPPING);
		clientAnimator.setCurrentMotionsAsDefault();
	}
	
	@Override
	public void tick(LivingUpdateEvent event) {
		this.targetXRotO = this.targetXRot;
		super.tick(event);
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
		if (this.state.inaction() && considerInaction) {
			currentLivingMotion = LivingMotion.INACTION;
		} else {
			if (this.original.getHealth() <= 0.0F) {
				currentLivingMotion = LivingMotion.DEATH;
			} else {
				currentLivingMotion = LivingMotion.IDLE;
				currentCompositeMotion = LivingMotion.IDLE;
			}
		}
	}
	
	@Override
	public void playAnimationSynchronized(StaticAnimation animation, float convertTimeModifier) {
		if (animation instanceof AttackAnimation && this.getAttackTarget() != null) {
			this.animator.playAnimation(animation, convertTimeModifier);
			this.playAnimationSynchronized(animation, convertTimeModifier, SPPlayAnimationAndSetTarget::new);	
		} else {
			super.playAnimationSynchronized(animation, convertTimeModifier);
		}
	}
	
	@Override
	public void onAttackBlocked(HurtEventPre hurtEvent, LivingEntityPatch<?> opponent) {
		this.original.setPos(opponent.getOriginal().getEyePosition().add(opponent.getOriginal().getLookAngle()));
		this.playAnimationSynchronized(Animations.VEX_NEUTRALIZED, 0.0F);
	}
	
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return modelDB.vex;
	}
	
	@Override
	public StaticAnimation getHitAnimation(StunType stunType) {
		return Animations.VEX_HIT;
	}
	
	@Override
	public OpenMatrix4f getModelMatrix(float partialTicks) {
		OpenMatrix4f mat = super.getModelMatrix(partialTicks);
		
		if (this.original.isCharging()) {
			if (this.targetXRot == 0.0F && this.getAttackTarget() != null) {
				Entity target = this.getAttackTarget();
				double d0 = VexPatch.this.original.getX() - target.getX();
		        double d1 = VexPatch.this.original.getY() - (target.getY() + (double)target.getBbHeight() * 0.5D);
		        double d2 = VexPatch.this.original.getZ() - target.getZ();
		        double d3 = Math.sqrt(d0 * d0 + d2 * d2);
		        this.targetXRot = (float)(-(Mth.atan2(d1, d3) * (double)(180F / (float)Math.PI)));
			}
		} else {
			this.targetXRot = 0.0F;
		}
		
		mat.rotateDeg(MathUtils.lerpBetween(this.targetXRotO, this.targetXRot, partialTicks), Vec3f.X_AXIS);
		
		return mat;
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
			//VexPatch.this.original.setDeltaMovement(0, 0, 0);
			//VexPatch.this.original.getNavigation().stop();
		}
		
		@Override
		public void tick() {
			VexPatch.this.original.getMoveControl().setWantedPosition(VexPatch.this.original.getX(), VexPatch.this.original.getY(), VexPatch.this.original.getZ(), 0.0F);
		}
	}
	
	class ChargeAttackGoal extends Goal {
		private int chargingCounter;

		public ChargeAttackGoal() {
			this.setFlags(EnumSet.noneOf(Flag.class));
		}

		@Override
		public boolean canUse() {
			if (VexPatch.this.original.getTarget() != null && !VexPatch.this.getEntityState().inaction()
					&& VexPatch.this.original.getRandom().nextInt(10) == 0) {
				double distance = VexPatch.this.original.distanceToSqr(VexPatch.this.original.getTarget());
				return distance < 50.0D;
			} else {
				return false;
			}
		}
	    
		@Override
		public boolean canContinueToUse() {
			return chargingCounter > 0;
		}

		@Override
		public void start() {
	    	Entity target = VexPatch.this.getAttackTarget();
	    	VexPatch.this.playAnimationSynchronized(Animations.VEX_CHARGE, 0.0F);
	    	VexPatch.this.original.playSound(SoundEvents.VEX_CHARGE, 1.0F, 1.0F);
	    	VexPatch.this.original.setIsCharging(true);
	    	
	    	double d0 = VexPatch.this.original.getX() - target.getX();
	        double d1 = VexPatch.this.original.getY() - (target.getY() + (double)target.getBbHeight() * 0.5D);
	        double d2 = VexPatch.this.original.getZ() - target.getZ();
	        double d3 = (double)Math.sqrt(d0 * d0 + d2 * d2);
	        VexPatch.this.targetXRot = (float)(-(Mth.atan2(d1, d3) * (double)(180F / (float)Math.PI)));
	    	this.chargingCounter = 20;
	    }
	    
		@Override
		public void stop() {
			VexPatch.this.original.setIsCharging(false);
			VexPatch.this.targetXRot = 0;
		}

		@Override
		public void tick() {
			--this.chargingCounter;
		}
	}
}