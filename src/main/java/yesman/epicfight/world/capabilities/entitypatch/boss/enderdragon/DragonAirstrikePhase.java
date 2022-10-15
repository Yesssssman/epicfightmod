package yesman.epicfight.world.capabilities.entitypatch.boss.enderdragon;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.client.model.ClientModels;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.gameasset.EpicFightSounds;
import yesman.epicfight.particle.EpicFightParticles;
import yesman.epicfight.world.entity.AreaEffectBreath;

public class DragonAirstrikePhase extends PatchedDragonPhase {
	private Vector3d startpos;
	private boolean isActuallyAttacking;
	
	public DragonAirstrikePhase(EnderDragonEntity dragon) {
		super(dragon);
	}
	
	@Override
	public void begin() {
		this.startpos = this.dragon.position();
		this.isActuallyAttacking = false;
		this.dragon.level.playLocalSound(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ(), SoundEvents.ENDER_DRAGON_GROWL, this.dragon.getSoundSource(), 5.0F, 0.8F + this.dragon.getRandom().nextFloat() * 0.3F, false);
	}
	
	@Override
	public void end() {
		this.dragonpatch.setAttakTargetSync(null);
		
		if (this.dragonpatch.isLogicalClient()) {
			Minecraft.getInstance().getSoundManager().stop(EpicFightSounds.ENDER_DRAGON_BREATH.getLocation(), SoundCategory.HOSTILE);
			this.dragon.level.playLocalSound(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ(), EpicFightSounds.ENDER_DRAGON_BREATH_FINALE, this.dragon.getSoundSource(), 5.0F, 1.0F, false);
		}
	}
	
	@Override
	public void doClientTick() {
		super.doClientTick();
		
		Vector3d dragonpos = this.dragon.position();
		OpenMatrix4f mouthpos = Animator.getBindedJointTransformByName(this.dragonpatch.getAnimator().getPose(1.0F), this.dragonpatch.getEntityModel(ClientModels.LOGICAL_CLIENT).getArmature(), "Mouth_Upper");
		
		float f = (float)this.dragon.getLatencyPos(7, 1.0F)[0];
		float f1 = (float)(this.dragon.getLatencyPos(5, 1.0F)[1] - this.dragon.getLatencyPos(10, 1.0F)[1]);
		@SuppressWarnings("deprecation")
		float f2 = (float)MathHelper.rotWrap((this.dragon.getLatencyPos(5, 1.0F)[0] - this.dragon.getLatencyPos(10, 1.0F)[0]));
		OpenMatrix4f modelMatrix = MathUtils.getModelMatrixIntegral(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, f1, f1, f, f, 1.0F, 1.0F, 1.0F, 1.0F).rotateDeg(-f2 * 1.5F, Vec3f.Z_AXIS);
		mouthpos.mulFront(modelMatrix);
		
		if (this.dragon.getTarget() != null) {
			Vector3d vec31 = this.dragon.getTarget().position().add(0.0D, 12.0D, 0.0D);
			
			if (!this.isActuallyAttacking && vec31.subtract(this.dragon.position()).lengthSqr() < 900.0F) {
				this.dragon.level.playLocalSound(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ(), EpicFightSounds.ENDER_DRAGON_BREATH, this.dragon.getSoundSource(), 5.0F, 1.0F, false);
				this.isActuallyAttacking = true;
			}
		}
		
		if (this.isActuallyAttacking) {
			for (int i = 0; i < 60; i++) {
				Vec3f particleDelta = new Vec3f(0.0F, -1.0F, 0.0F);
				float xDeg = this.dragon.getRandom().nextFloat() * 60.0F - 30.0F;
				float zDeg = this.dragon.getRandom().nextFloat() * 60.0F - 30.0F;
				float speed = Math.min((60.0F - (Math.abs(xDeg) + Math.abs(zDeg))) / 20.0F, 1.0F);
				
				OpenMatrix4f.transform3v(OpenMatrix4f.createRotatorDeg(xDeg, Vec3f.X_AXIS), particleDelta, particleDelta);
				OpenMatrix4f.transform3v(OpenMatrix4f.createRotatorDeg(zDeg, Vec3f.Z_AXIS), particleDelta, particleDelta);
				particleDelta.scale(speed);
				
				this.dragon.level.addAlwaysVisibleParticle(EpicFightParticles.BREATH_FLAME.get(), mouthpos.m30 + dragonpos.x, mouthpos.m31 + dragonpos.y, mouthpos.m32 + dragonpos.z, particleDelta.x, particleDelta.y, particleDelta.z);
			}
		}
	}
	
	@Override
	public void doServerTick() {
		LivingEntity target = this.dragon.getTarget();
		
		if (target == null) {
			this.dragon.getPhaseManager().setPhase(PatchedPhases.FLYING);
		} else {
			if (isValidTarget(target)) {
				Vector3d startToDragon = this.dragon.position().subtract(this.startpos);
				Vector3d startToTarget = target.position().subtract(this.startpos);
				//System.out.println(this.startpos);
				if (MathUtils.horizontalDistanceSqr(startToDragon) < MathUtils.horizontalDistanceSqr(startToTarget)) {
					Vector3d vec31 = target.position().add(0.0D, 12.0D, 0.0D);
					
					if (!this.isActuallyAttacking && vec31.subtract(this.dragon.position()).lengthSqr() < 900.0F) {
						this.isActuallyAttacking = true;
					}
					
					double d8 = vec31.x - this.dragon.getX();
					double d9 = vec31.y - this.dragon.getY();
					double d10 = vec31.z - this.dragon.getZ();
					float f5 = this.getFlySpeed();
					double d4 = Math.sqrt(d8 * d8 + d10 * d10);
					
					if (d4 > 0.0D) {
						d9 = MathHelper.clamp(d9 / d4, (double)-f5, (double)f5);
					}
					
					this.dragon.setDeltaMovement(this.dragon.getDeltaMovement().add(0.0D, d9 * 0.1D, 0.0D));
					this.dragon.yRot = (MathHelper.wrapDegrees(this.dragon.yRot));
					Vector3d vec32 = vec31.subtract(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ()).normalize();
					Vector3d vec33 = (new Vector3d((double)MathHelper.sin(this.dragon.yRot * ((float) Math.PI / 180F)), this.dragon.getDeltaMovement().y, (double) (-MathHelper.cos(this.dragon.yRot * ((float) Math.PI / 180F))))).normalize();
					float f6 = Math.max(((float)vec33.dot(vec32) + 0.5F) / 1.5F, 0.0F);
					
					if (Math.abs(d8) > (double)1.0E-5F || Math.abs(d10) > (double)1.0E-5F) {
						double dx = target.getX() - this.dragon.getX();
						double dz = target.getZ() - this.dragon.getZ();
						float yRot = 180.0F - (float)Math.toDegrees(MathHelper.atan2(dx, dz));
						this.dragon.yRot = (MathUtils.rotlerp(this.dragon.yRot, yRot, 6.0F));
						double speed = (-0.5D - 1.0D / (1.0D + Math.pow(Math.E, -(d4 / 10.0D - 4.0F)))) * f6;
						Vector3d forward = Vector3d.directionFromRotation(this.dragon.getRotationVector()).scale(speed);
						//System.out.println(forward);
						this.dragon.move(MoverType.SELF, forward);
					}	
					
					if (this.dragon.inWall) {
						this.dragon.move(MoverType.SELF, this.dragon.getDeltaMovement().scale((double) 0.8F));
					} else {
						this.dragon.move(MoverType.SELF, this.dragon.getDeltaMovement());
					}
					
					Vector3d vec34 = this.dragon.getDeltaMovement().normalize();
					double d6 = 0.8D + 0.15D * (vec34.dot(vec33) + 1.0D) / 2.0D;
					this.dragon.setDeltaMovement(this.dragon.getDeltaMovement().multiply(d6, (double) 0.91F, d6));
					
					if (this.isActuallyAttacking) {
						if (this.dragon.tickCount % 5 == 0) {
							Vector3d createpos = this.dragon.position().add(this.dragon.getLookAngle().scale(-4.5D));
							AreaEffectBreath breatharea = new AreaEffectBreath(this.dragon.level, createpos.x, createpos.y, createpos.z);
							breatharea.setOwner((LivingEntity)this.dragon);
							breatharea.setWaitTime(0);
							breatharea.setRadius(0.5F);
							breatharea.setDuration(15);
							breatharea.setRadiusPerTick(0.2F);
							breatharea.addEffect(new EffectInstance(Effects.HARM, 1, 1));
							breatharea.setDeltaMovement(0, -1, 0);
							this.dragon.level.addFreshEntity(breatharea);
						}
					}
				} else {
					this.dragon.getPhaseManager().setPhase(PatchedPhases.FLYING);
				}
			} else {
				this.dragon.getPhaseManager().setPhase(PatchedPhases.FLYING);
			}
		}
	}
	
	public boolean isActuallyAttacking() {
		return this.isActuallyAttacking;
	}
	
	@Override
	public float getFlySpeed() {
		return 2.0F;
	}
	
	@Override
	public PhaseType<DragonAirstrikePhase> getPhase() {
		return PatchedPhases.AIRSTRIKE;
	}
}