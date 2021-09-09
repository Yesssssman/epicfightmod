package maninhouse.epicfight.capabilities.entity.mob;

import java.util.EnumSet;
import java.util.Random;

import maninhouse.epicfight.animation.LivingMotion;
import maninhouse.epicfight.animation.types.AttackAnimation;
import maninhouse.epicfight.animation.types.StaticAnimation;
import maninhouse.epicfight.client.animation.AnimatorClient;
import maninhouse.epicfight.effects.ModEffects;
import maninhouse.epicfight.entity.ai.AttackPatternGoal;
import maninhouse.epicfight.entity.ai.AttackPatternPercentGoal;
import maninhouse.epicfight.entity.ai.ChasingGoal;
import maninhouse.epicfight.entity.ai.attribute.ModAttributes;
import maninhouse.epicfight.gamedata.Animations;
import maninhouse.epicfight.gamedata.Models;
import maninhouse.epicfight.model.Model;
import maninhouse.epicfight.network.ModNetworkManager;
import maninhouse.epicfight.network.server.STCPlayAnimationTP;
import maninhouse.epicfight.network.server.STCPlayAnimationTarget;
import maninhouse.epicfight.particle.Particles;
import maninhouse.epicfight.utils.game.IExtendedDamageSource;
import maninhouse.epicfight.utils.game.IExtendedDamageSource.StunType;
import maninhouse.epicfight.utils.math.Vec3f;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public class EndermanData extends BipedMobData<EndermanEntity> {
	private int deathTimerExt = 0;
	private boolean onRage;
	private Goal normalAttack1;
	private Goal normalAttack2;
	private Goal normalAttack3;
	private Goal normalAttack4;
	private Goal normalAttack5;
	private Goal rageTarget;
	private Goal rageChase;
	
	public EndermanData() {
		super(Faction.ENDERLAND);
	}
	
	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.orgEntity.getAttribute(ModAttributes.STUN_ARMOR.get()).setBaseValue(8.0F);
		this.orgEntity.getAttribute(ModAttributes.IMPACT.get()).setBaseValue(1.8F);
	}
	
	@Override
	public void postInit() {
		super.postInit();
		if (this.isRemote()) {
			if (this.isRaging()) {
				this.getClientAnimator().addLivingAnimation(LivingMotion.IDLE, Animations.ENDERMAN_RAGE_IDLE);
				this.getClientAnimator().addLivingAnimation(LivingMotion.WALK, Animations.ENDERMAN_RAGE_WALK);
				this.onRage = true;
			} else {
				this.getClientAnimator().addLivingAnimation(LivingMotion.IDLE, Animations.ENDERMAN_IDLE);
				this.getClientAnimator().addLivingAnimation(LivingMotion.WALK, Animations.ENDERMAN_WALK);
				this.onRage = false;
			}
		}
	}
	
	@Override
	protected void initAI() {
		super.initAI();
		this.normalAttack1 = new AttackPatternPercentGoal(this, this.orgEntity, 0.0D, 1.23D, 0.4F, true, MobAttackPatterns.ENDERMAN_KNEE);
		this.normalAttack2 = new AttackPatternPercentGoal(this, this.orgEntity, 0.0D, 1.9D, 0.4F, true, MobAttackPatterns.ENDERMAN_KICK_COMBO);
		this.normalAttack3 = new AttackPatternPercentGoal(this, this.orgEntity, 3.0D, 4.0D, 0.1F, true, MobAttackPatterns.ENDERMAN_SPINKICK);
		this.normalAttack4 = new AttackPatternPercentGoal(this, this.orgEntity, 0.0D, 2.0D, 0.2F, true, MobAttackPatterns.ENDERMAN_JUMPKICK);
		this.normalAttack5 = new AIEndermanTeleportKick(this, this.orgEntity);
		this.rageTarget = new NearestAttackableTargetGoal<>(this.orgEntity, PlayerEntity.class, true);
		this.rageChase = new AIEndermanRush(this, this.orgEntity);
	}
	
	@Override
	protected void initAnimator(AnimatorClient animatorClient) {
		super.initAnimator(animatorClient);
		animatorClient.addLivingAnimation(LivingMotion.DEATH, Animations.ENDERMAN_DEATH);
		animatorClient.addLivingAnimation(LivingMotion.WALK, Animations.ENDERMAN_WALK);
		animatorClient.addLivingAnimation(LivingMotion.IDLE, Animations.ENDERMAN_IDLE);
	}
	
	@Override
	public void updateMotion() {
		super.commonCreatureUpdateMotion();
	}
	
	@Override
	public void updateOnServer() {
		super.updateOnServer();
		if (this.isRaging() && !this.onRage && this.orgEntity.ticksExisted > 5) {
			this.convertRage();
		} else if (this.onRage && !this.isRaging()) {
			this.convertNormal();
		}
	}
	
	@Override
	public void update() {
		if (this.orgEntity.getHealth() <= 0.0F) {
			this.orgEntity.rotationPitch = 0;
			if (this.orgEntity.deathTime > 1 && this.deathTimerExt < 20) {
				this.deathTimerExt++;
				this.orgEntity.deathTime--;
			}
		}
		super.update();
	}
	
	@Override
	public boolean hurtBy(LivingAttackEvent event) {
		if (!this.orgEntity.world.isRemote) {
			if (event.getSource() instanceof EntityDamageSource && !this.isRaging()) {
				IExtendedDamageSource extDamageSource = null;
				if (event.getSource() instanceof IExtendedDamageSource) {
					extDamageSource = ((IExtendedDamageSource)event.getSource());
				}
				
				if (extDamageSource == null || extDamageSource.getStunType() != StunType.HOLD) {
					int percentage = this.getServerAnimator().getPlayerFor(null).getPlay() instanceof AttackAnimation ? 10 : 3;
					if (this.orgEntity.getRNG().nextInt(percentage) == 0) {
						for (int i = 0; i < 9; i++) {
							if (this.teleportRandomly()) {
								if (event.getSource().getTrueSource() instanceof LivingEntity) {
									this.orgEntity.setRevengeTarget((LivingEntity) event.getSource().getTrueSource());
								}

								if (this.state.isInaction()) {
									this.playAnimationSynchronize(Animations.ENDERMAN_TP_EMERGENCE, 0.0F);
								}
								
								return false;
							}
						}
					}
				}
			}
		}
		
		return super.hurtBy(event);
	}
	
	protected boolean teleportRandomly() {
		if (!this.isRemote() && this.orgEntity.isAlive()) {
	        double d0 = this.orgEntity.getPosX() + (this.orgEntity.getRNG().nextDouble() - 0.5D) * 64.0D;
	        double d1 = this.orgEntity.getPosY() + (double)(this.orgEntity.getRNG().nextInt(64) - 32);
	        double d2 = this.orgEntity.getPosZ() + (this.orgEntity.getRNG().nextDouble() - 0.5D) * 64.0D;
	        return this.teleportTo(d0, d1, d2);
		} else {
			return false;
		}
    }
	
	private boolean teleportTo(double x, double y, double z) {
		BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(x, y, z);
	    while(blockpos$mutable.getY() > 0 && !this.orgEntity.world.getBlockState(blockpos$mutable).getMaterial().blocksMovement())
	    	blockpos$mutable.move(Direction.DOWN);
	    
	    BlockState blockstate = this.orgEntity.world.getBlockState(blockpos$mutable);
	    boolean flag = blockstate.getMaterial().blocksMovement();
	    boolean flag1 = blockstate.getFluidState().isTagged(FluidTags.WATER);
		if (flag && !flag1) {
	    	boolean flag2 = this.orgEntity.attemptTeleport(x, y, z, true);
			if (flag2 && !this.orgEntity.isSilent()) {
	        	this.orgEntity.world.playSound((PlayerEntity)null, this.orgEntity.prevPosX, this.orgEntity.prevPosY, this.orgEntity.prevPosZ,
	        			SoundEvents.ENTITY_ENDERMAN_TELEPORT, this.orgEntity.getSoundCategory(), 1.0F, 1.0F);
	        	this.orgEntity.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
	        }
	        return flag2;
	    }
        return false;
    }
	
	public boolean isRaging() {
		return this.orgEntity.getHealth() / this.orgEntity.getMaxHealth() < 0.33F;
	}
	
	protected void convertRage() {
		this.onRage = true;
		this.playAnimationSynchronize(Animations.ENDERMAN_HIT_RAGE, 0);
		
		if (this.isRemote()) {
			this.getClientAnimator().addLivingAnimation(LivingMotion.IDLE, Animations.ENDERMAN_RAGE_IDLE);
			this.getClientAnimator().addLivingAnimation(LivingMotion.WALK, Animations.ENDERMAN_RAGE_WALK);
		} else {
			if (!this.orgEntity.isAIDisabled()) {
				this.orgEntity.goalSelector.removeGoal(this.normalAttack1);
				this.orgEntity.goalSelector.removeGoal(this.normalAttack2);
				this.orgEntity.goalSelector.removeGoal(this.normalAttack3);
				this.orgEntity.goalSelector.removeGoal(this.normalAttack4);
				this.orgEntity.goalSelector.removeGoal(this.normalAttack5);
				this.orgEntity.goalSelector.addGoal(1, this.rageChase);
				this.orgEntity.targetSelector.addGoal(3, this.rageTarget);
				this.orgEntity.getDataManager().set(EndermanEntity.SCREAMING, Boolean.valueOf(true));
				this.orgEntity.addPotionEffect(new EffectInstance(ModEffects.STUN_IMMUNITY.get(), 120000));
			}
		}
	}
	
	protected void convertNormal() {
		this.onRage = false;
		
		if (this.isRemote()) {
			this.getClientAnimator().addLivingAnimation(LivingMotion.IDLE, Animations.ENDERMAN_IDLE);
			this.getClientAnimator().addLivingAnimation(LivingMotion.WALK, Animations.ENDERMAN_WALK);
		} else {
			if (!orgEntity.isAIDisabled()) {
				this.orgEntity.goalSelector.addGoal(1, this.normalAttack1);
				this.orgEntity.goalSelector.addGoal(1, this.normalAttack2);
				this.orgEntity.goalSelector.addGoal(1, this.normalAttack3);
				this.orgEntity.goalSelector.addGoal(1, this.normalAttack4);
				this.orgEntity.goalSelector.addGoal(0, this.normalAttack5);
				this.orgEntity.goalSelector.removeGoal(this.rageChase);
				this.orgEntity.targetSelector.removeGoal(this.rageTarget);

				if (this.orgEntity.getAttackTarget() == null) {
					this.orgEntity.getDataManager().set(EndermanEntity.SCREAMING, Boolean.valueOf(false));
				}
				this.orgEntity.removePotionEffect(ModEffects.STUN_IMMUNITY.get());
			}
		}
	}

	@Override
	public void setAIAsUnarmed() {
		if (this.isRaging()) {
			this.orgEntity.targetSelector.addGoal(3, this.rageTarget);
			this.orgEntity.goalSelector.addGoal(1, this.rageChase);
		} else {
			this.orgEntity.goalSelector.addGoal(1, this.normalAttack1);
			this.orgEntity.goalSelector.addGoal(1, this.normalAttack2);
			this.orgEntity.goalSelector.addGoal(1, this.normalAttack3);
			this.orgEntity.goalSelector.addGoal(1, this.normalAttack4);
			this.orgEntity.goalSelector.addGoal(0, this.normalAttack5);
		}
		
		this.orgEntity.goalSelector.addGoal(1, new ChasingGoal(this, this.orgEntity, 0.75D, false));
	}
	
	@Override
	public void setAIAsArmed() {
		this.setAIAsUnarmed();
	}
	
	@Override
	public void setAIAsMounted(Entity ridingEntity) {
		
	}
	
	@Override
	public void aboutToDeath() {
		this.orgEntity.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);

		if (this.isRemote()) {
			for (int i = 0; i < 100; i++) {
				Random rand = orgEntity.getRNG();
				Vec3f vec = new Vec3f(rand.nextInt(), rand.nextInt(), rand.nextInt());
				vec.normalise();
				vec.scale(0.5F);
				Minecraft.getInstance().particles.addParticle(Particles.PORTAL_STRAIGHT.get(), this.orgEntity.getPosX(),
						this.orgEntity.getPosY() + this.orgEntity.getSize(Pose.STANDING).height / 2, this.orgEntity.getPosZ(), vec.x, vec.y, vec.z);
			}
		}
		
		super.aboutToDeath();
	}
	
	@Override
	public StaticAnimation getHitAnimation(StunType stunType) {
		if (stunType == StunType.LONG) {
			return Animations.ENDERMAN_HIT_LONG;
		} else {
			return Animations.ENDERMAN_HIT_SHORT;
		}
	}
	
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return modelDB.ENTITY_ENDERMAN;
	}

	static class AIEndermanTeleportKick extends AttackPatternPercentGoal {
		private int delayCounter;
		private int cooldownTime;

		public AIEndermanTeleportKick(BipedMobData<?> mobdata, MobEntity attacker) {
			super(mobdata, attacker, 8.0F, 100.0F, 0.1F, false, null);
			super.setMutexFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
		}
		
		@Override
		public boolean shouldExecute() {
			boolean b = this.cooldownTime <= 0;
			if (!b) {
				this.cooldownTime--;
			}
			
			return super.shouldExecute() && b;
		}

		@Override
		public boolean shouldContinueExecuting() {
			LivingEntity LivingEntity = this.attacker.getAttackTarget();
			boolean b = cooldownTime <= 100;
			if(!b) cooldownTime = 500;
	    	return isValidTarget(LivingEntity) && isTargetInRange(LivingEntity) && b;
	    }

		@Override
		public void startExecuting() {
			this.delayCounter = 35 + this.attacker.getRNG().nextInt(10);
			this.cooldownTime = 0;
		}

		@Override
		public void resetTask() {
			;
	    }
		
		@Override
		public void tick() {
			LivingEntity target = attacker.getAttackTarget();
	        this.attacker.getLookController().setLookPositionWithEntity(target, 30.0F, 30.0F);

			if (this.delayCounter-- < 0 && !this.mobdata.getEntityState().isInaction()) {
	        	Vec3f vec = new Vec3f((float)(this.attacker.getPosX() - target.getPosX()), 0, (float)(this.attacker.getPosZ() - target.getPosZ()));
	        	vec.normalise();
	        	vec.scale(1.414F);
	        	
	        	boolean flag = this.attacker.attemptTeleport(target.getPosX() + vec.x, target.getPosY(), target.getPosZ() + vec.z, true);

				if (flag) {
	            	this.mobdata.rotateTo(target, 360.0F, true);
	            	
	                AttackAnimation kickAnimation = this.attacker.getRNG().nextBoolean() ? (AttackAnimation) Animations.ENDERMAN_TP_KICK1 : (AttackAnimation) Animations.ENDERMAN_TP_KICK2;
	                this.mobdata.getServerAnimator().playAnimation(kickAnimation, 0);
		        	ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCPlayAnimationTP(kickAnimation.getId(), this.attacker.getEntityId(), 0.0F, 
		        			this.attacker.getAttackTarget().getEntityId(), this.attacker.getPosX(), this.attacker.getPosY(), this.attacker.getPosZ(), this.attacker.rotationYaw), attacker);
		        	
		        	this.attacker.world.playSound((PlayerEntity)null, this.attacker.prevPosX, this.attacker.prevPosY, this.attacker.prevPosZ,
		        			SoundEvents.ENTITY_ENDERMAN_TELEPORT, this.attacker.getSoundCategory(), 1.0F, 1.0F);
		        	this.attacker.playSound(SoundEvents.ENTITY_ENDERMAN_TELEPORT, 1.0F, 1.0F);
		        	this.cooldownTime = 0;
				} else {
	            	this.cooldownTime++;
				}
	        }
	    }
	}
	
	static class AIEndermanRush extends AttackPatternGoal {
		private float accelator;

		public AIEndermanRush(BipedMobData<?> mobdata, MobEntity attacker) {
			super(mobdata, attacker, 0.0F, 1.8F, false, null);
		}
		
		@Override
		public boolean shouldExecute() {
			return this.isValidTarget(this.attacker.getAttackTarget()) && !this.mobdata.getEntityState().isInaction();
	    }
		
		@Override
		public boolean shouldContinueExecuting() {
	    	return isValidTarget(this.attacker.getAttackTarget()) && !this.mobdata.getEntityState().isInaction();
	    }
		
		@Override
		public void startExecuting() {
			ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCPlayAnimationTarget(Animations.ENDERMAN_RUSH.getId(), this.attacker.getEntityId(),
					0.0F, this.attacker.getAttackTarget().getEntityId()), this.attacker);
			
			this.accelator = 0.0F;
		}

		@Override
		public void resetTask() {
			;
	    }
		
		@Override
		public void tick() {
			if (isTargetInRange(attacker.getAttackTarget()) && canExecuteAttack()) {
				this.mobdata.getServerAnimator().playAnimation(Animations.ENDERMAN_GRASP, 0);
	        	ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCPlayAnimationTarget(Animations.ENDERMAN_GRASP.getId(), this.attacker.getEntityId(), 0, 
	        			this.attacker.getAttackTarget().getEntityId()), this.attacker);
			}
			
			this.attacker.getNavigator().setSpeed(0.025F * this.accelator * this.accelator + 1.0F);
			this.accelator = this.accelator > 2.0F ? this.accelator : this.accelator + 0.05F;
	    }
	}
}