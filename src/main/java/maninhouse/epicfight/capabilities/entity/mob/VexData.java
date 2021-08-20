package maninhouse.epicfight.capabilities.entity.mob;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;

import maninhouse.epicfight.animation.LivingMotion;
import maninhouse.epicfight.animation.types.AttackAnimation;
import maninhouse.epicfight.animation.types.StaticAnimation;
import maninhouse.epicfight.capabilities.entity.MobData;
import maninhouse.epicfight.client.animation.AnimatorClient;
import maninhouse.epicfight.gamedata.Animations;
import maninhouse.epicfight.gamedata.Models;
import maninhouse.epicfight.model.Model;
import maninhouse.epicfight.network.ModNetworkManager;
import maninhouse.epicfight.network.server.STCPlayAnimationTarget;
import maninhouse.epicfight.utils.game.IExtendedDamageSource.StunType;
import maninhouse.epicfight.utils.math.MathUtils;
import maninhouse.epicfight.utils.math.Vec3f;
import maninhouse.epicfight.utils.math.OpenMatrix4f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.PrioritizedGoal;
import net.minecraft.entity.monster.VexEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;

public class VexData extends MobData<VexEntity> {
	private float prevPitchToTarget;
	private float pitchToTarget;

	public VexData() {
		super(Faction.ILLAGER);
	}

	@Override
	protected void initAI() {
		super.initAI();
		
		Set<PrioritizedGoal> goals = this.orgEntity.goalSelector.goals;
		Iterator<PrioritizedGoal> iterator = goals.iterator();
		Goal toRemove = null;
		int iterCount = 0;
		while (iterator.hasNext()) {
			PrioritizedGoal goal = iterator.next();
			Goal inner = goal.getGoal();

			if (iterCount == 1) {
				toRemove = inner;
				break;
			}
			iterCount++;
        }
        
        if(toRemove != null) {
        	orgEntity.goalSelector.removeGoal(toRemove);
        }
        
        orgEntity.goalSelector.addGoal(0, new ChargeAttackGoal());
		orgEntity.goalSelector.addGoal(1, new StopStandGoal());
	}
	
	@Override
	protected void initAnimator(AnimatorClient animatorClient) {
		animatorClient.addLivingAnimation(LivingMotion.FLOAT, Animations.VEX_IDLE);
		animatorClient.addLivingAnimation(LivingMotion.DEATH, Animations.VEX_DEATH);
		animatorClient.addOverridenLivingMotion(LivingMotion.IDLE, Animations.VEX_FLIPPING);
	}
	
	@Override
	public void update() {
		this.prevPitchToTarget = this.pitchToTarget;
		super.update();
	}
	
	@Override
	public void updateMotion() {
		if (this.state.isInaction()) {
			currentMotion = LivingMotion.IDLE;
		} else {
			if (this.orgEntity.getHealth() <= 0.0F) {
				currentMotion = LivingMotion.DEATH;
			} else {
				currentMotion = LivingMotion.FLOAT;
				currentOverridenMotion = LivingMotion.IDLE;
			}
		}
	}
	
	@Override
	public void playAnimationSynchronize(StaticAnimation animation, float modifyTime) {
		if (animation instanceof AttackAnimation && this.getAttackTarget() != null) {
			this.animator.playAnimation(animation, modifyTime);
			ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCPlayAnimationTarget(animation.getId(), this.orgEntity.getEntityId(), modifyTime,
					this.getAttackTarget().getEntityId()), this.orgEntity);
		} else {
			super.playAnimationSynchronize(animation, modifyTime);
		}
	}
	
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return modelDB.ENTITY_VEX;
	}
	
	@Override
	public StaticAnimation getHitAnimation(StunType stunType) {
		return Animations.VEX_HIT;
	}
	
	@Override
	public OpenMatrix4f getModelMatrix(float partialTicks) {
		OpenMatrix4f mat = super.getModelMatrix(partialTicks);
		
		if (this.orgEntity.isCharging()) {
			if (this.pitchToTarget == 0.0F && this.getAttackTarget() != null) {
				Entity target = this.getAttackTarget();
				double d0 = VexData.this.orgEntity.getPosX() - target.getPosX();
		        double d1 = VexData.this.orgEntity.getPosY() - (target.getPosY() + (double)target.getHeight() * 0.5D);
		        double d2 = VexData.this.orgEntity.getPosZ() - target.getPosZ();
		        double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
		        this.pitchToTarget = (float)(-(MathHelper.atan2(d1, d3) * (double)(180F / (float)Math.PI)));
			}
		} else {
			this.pitchToTarget = 0.0F;
		}
		
		OpenMatrix4f.rotate((float)Math.toRadians(MathUtils.interpolateRotation(this.prevPitchToTarget, this.pitchToTarget, partialTicks)),
				new Vec3f(1, 0, 0), mat, mat);
		
		return mat;
	}
	
	class StopStandGoal extends Goal {
		public StopStandGoal() {
			this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE));
		}

		@Override
		public boolean shouldExecute() {
			return VexData.this.getEntityState().isInaction();
		}

		@Override
		public void startExecuting() {
			VexData.this.orgEntity.setMotion(0.0, 0.0, 0.0);
			VexData.this.orgEntity.getNavigator().clearPath();
		}
	}
	
	class ChargeAttackGoal extends Goal {
		private int chargingCounter;

		public ChargeAttackGoal() {
			this.setMutexFlags(EnumSet.noneOf(Flag.class));
		}

		@Override
		public boolean shouldExecute() {
			if (VexData.this.orgEntity.getAttackTarget() != null && !VexData.this.getEntityState().isInaction()
					&& VexData.this.orgEntity.getRNG().nextInt(10) == 0) {
				double distance = VexData.this.orgEntity.getDistanceSq(VexData.this.orgEntity.getAttackTarget());
				return distance < 50.0D;
			} else {
				return false;
			}
		}
	    
		@Override
		public boolean shouldContinueExecuting() {
			return chargingCounter > 0;
		}

		@Override
		public void startExecuting() {
	    	Entity target = VexData.this.getAttackTarget();
	    	VexData.this.playAnimationSynchronize(Animations.VEX_CHARGING, 0.0F);
	    	VexData.this.orgEntity.playSound(SoundEvents.ENTITY_VEX_CHARGE, 1.0F, 1.0F);
	    	VexData.this.orgEntity.setCharging(true);
	    	
	    	double d0 = VexData.this.orgEntity.getPosX() - target.getPosX();
	        double d1 = VexData.this.orgEntity.getPosY() - (target.getPosY() + (double)target.getHeight() * 0.5D);
	        double d2 = VexData.this.orgEntity.getPosZ() - target.getPosZ();
	        double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
	        VexData.this.pitchToTarget = (float)(-(MathHelper.atan2(d1, d3) * (double)(180F / (float)Math.PI)));
	    	this.chargingCounter = 20;
	    }
	    
		@Override
		public void resetTask() {
			VexData.this.orgEntity.setCharging(false);
			VexData.this.pitchToTarget = 0;
		}

		@Override
		public void tick() {
			--this.chargingCounter;
		}
	}
}