package maninthehouse.epicfight.capabilities.entity.mob;

import java.util.Iterator;

import maninthehouse.epicfight.animation.LivingMotion;
import maninthehouse.epicfight.animation.types.StaticAnimation;
import maninthehouse.epicfight.capabilities.entity.MobData;
import maninthehouse.epicfight.client.animation.AnimatorClient;
import maninthehouse.epicfight.gamedata.Animations;
import maninthehouse.epicfight.gamedata.Models;
import maninthehouse.epicfight.model.Model;
import maninthehouse.epicfight.network.ModNetworkManager;
import maninthehouse.epicfight.network.server.STCPlayAnimationTarget;
import maninthehouse.epicfight.utils.game.IExtendedDamageSource.StunType;
import maninthehouse.epicfight.utils.math.MathUtils;
import maninthehouse.epicfight.utils.math.Vec3f;
import maninthehouse.epicfight.utils.math.VisibleMatrix4f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.monster.EntityVex;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.MathHelper;

public class VexData extends MobData<EntityVex> {
	private float prevPitchToTarget;
	private float pitchToTarget;

	public VexData() {
		super(Faction.ILLAGER);
	}
	
	@Override
	protected void initAI() {
		super.initAI();
		Iterator<EntityAITasks.EntityAITaskEntry> iterator = orgEntity.tasks.taskEntries.iterator();
		int count = 0;
		
		while (iterator.hasNext()) {
			iterator.next();
			if (count == 1) {
				iterator.remove();
				break;
			}
			
			count++;
		}
        
        orgEntity.tasks.addTask(0, new ChargeAttackGoal());
		orgEntity.tasks.addTask(1, new StopStandGoal());
	}
	
	@Override
	protected void initAnimator(AnimatorClient animatorClient) {
		animatorClient.mixLayer.setJointMask("Chest", "Wing_L", "Wing_R");
		animatorClient.addLivingAnimation(LivingMotion.FLOATING, Animations.VEX_IDLE);
		animatorClient.addLivingAnimation(LivingMotion.DEATH, Animations.VEX_DEATH);
		animatorClient.addLivingMixAnimation(LivingMotion.IDLE, Animations.VEX_FLIPPING);
		animatorClient.setCurrentLivingMotionsToDefault();
	}
	
	@Override
	public void update() {
		this.prevPitchToTarget = this.pitchToTarget;
		super.update();
	}

	@Override
	public void updateMotion() {
		currentMotion = LivingMotion.FLOATING;
		currentMixMotion = LivingMotion.IDLE;
	}
	
	@Override
	public void playAnimationSynchronize(int id, float modifyTime) {
		this.animator.playAnimation(id, modifyTime);
		ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCPlayAnimationTarget(id, this.orgEntity.getEntityId(),
				modifyTime, this.getAttackTarget().getEntityId()), this.orgEntity);
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
	public VisibleMatrix4f getHeadMatrix(float partialTicks) {
		return super.getHeadMatrix(partialTicks);
	}

	@Override
	public VisibleMatrix4f getModelMatrix(float partialTicks) {
		VisibleMatrix4f mat = super.getModelMatrix(partialTicks);

		if (this.orgEntity.isCharging()) {
			if (this.pitchToTarget == 0.0F && this.getAttackTarget() != null) {
				Entity target = this.getAttackTarget();
				double d0 = VexData.this.orgEntity.posX - target.posX;
		        double d1 = VexData.this.orgEntity.posY - (target.posY + (double)target.height * 0.5D);
		        double d2 = VexData.this.orgEntity.posZ - target.posZ;
		        double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
				this.pitchToTarget = (float) (-(MathHelper.atan2(d1, d3) * (double) (180F / (float) Math.PI)));
			}
		} else {
			this.pitchToTarget = 0.0F;
		}
		
		VisibleMatrix4f.rotate((float)Math.toRadians(MathUtils.interpolateRotation(this.prevPitchToTarget, this.pitchToTarget, partialTicks)),
				new Vec3f(1, 0, 0), mat, mat);
		
		return mat;
	}
	
	class StopStandGoal extends EntityAIBase {
		public StopStandGoal() {
			this.setMutexBits(1);
		}
		
		@Override
		public boolean shouldExecute() {
			return VexData.this.inaction;
		}

		@Override
		public void startExecuting() {
			VexData.this.orgEntity.motionX = 0.0D;
			VexData.this.orgEntity.motionY = 0.0D;
			VexData.this.orgEntity.motionZ = 0.0D;
			VexData.this.orgEntity.getNavigator().clearPath();
		}
	}
	
	class ChargeAttackGoal extends EntityAIBase {
		private int chargingCounter;

		public ChargeAttackGoal() {
			this.setMutexBits(3);
		}

		@Override
		public boolean shouldExecute() {
			if (VexData.this.orgEntity.getAttackTarget() != null && !VexData.this.inaction
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
	    	
	    	double d0 = VexData.this.orgEntity.posX - target.posX;
	        double d1 = VexData.this.orgEntity.posY - (target.posY + (double)target.height * 0.5D);
	        double d2 = VexData.this.orgEntity.posZ - target.posZ;
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
		public void updateTask() {
			--this.chargingCounter;
		}
	}
}