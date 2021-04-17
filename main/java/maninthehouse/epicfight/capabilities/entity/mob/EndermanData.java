package maninthehouse.epicfight.capabilities.entity.mob;

import maninthehouse.epicfight.animation.LivingMotion;
import maninthehouse.epicfight.animation.types.StaticAnimation;
import maninthehouse.epicfight.animation.types.attack.AttackAnimation;
import maninthehouse.epicfight.capabilities.entity.DataKeys;
import maninthehouse.epicfight.client.animation.AnimatorClient;
import maninthehouse.epicfight.effects.ModEffects;
import maninthehouse.epicfight.entity.ai.EntityAIAttackPattern;
import maninthehouse.epicfight.entity.ai.EntityAIChase;
import maninthehouse.epicfight.entity.ai.EntityAIPatternWithChance;
import maninthehouse.epicfight.entity.ai.attribute.ModAttributes;
import maninthehouse.epicfight.gamedata.Animations;
import maninthehouse.epicfight.gamedata.Models;
import maninthehouse.epicfight.model.Model;
import maninthehouse.epicfight.network.ModNetworkManager;
import maninthehouse.epicfight.network.server.STCPlayAnimationTP;
import maninthehouse.epicfight.network.server.STCPlayAnimationTarget;
import maninthehouse.epicfight.utils.game.IExtendedDamageSource;
import maninthehouse.epicfight.utils.game.IExtendedDamageSource.StunType;
import maninthehouse.epicfight.utils.math.Vec3f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;

public class EndermanData extends BipedMobData<EntityEnderman> {
	private int deathTimerExt = 0;
	private int teleportCooled = 0;
	private boolean onRage;
	private EntityAIBase normalAttack1;
	private EntityAIBase normalAttack2;
	private EntityAIBase normalAttack3;
	private EntityAIBase normalAttack4;
	private EntityAIBase normalAttack5;
	private EntityAIBase rageTarget;
	private EntityAIBase rageChase;
	
	public EndermanData() {
		super(Faction.ENDERLAND);
	}
	
	@Override
	public void onEntityJoinWorld(EntityEnderman entityIn) {
		super.onEntityJoinWorld(entityIn);
		this.orgEntity.getDataManager().register(DataKeys.STUN_ARMOR, Float.valueOf(6.0F));
	}
	
	@Override
	protected void registerAttributes() {
		super.registerAttributes();
		this.registerIfAbsent(ModAttributes.MAX_STUN_ARMOR);
	}
	
	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.orgEntity.getEntityAttribute(ModAttributes.MAX_STUN_ARMOR).setBaseValue(6.0F);
	}
	
	@Override
	public void postInit() {
		super.postInit();
		
		if (this.isRemote()) {
			if (this.isRaging()) {
				this.getClientAnimator().addLivingAnimation(LivingMotion.IDLE, Animations.ENDERMAN_RAGE_IDLE);
				this.getClientAnimator().addLivingAnimation(LivingMotion.WALKING, Animations.ENDERMAN_RAGE_WALK);
				this.onRage = true;
			} else {
				this.getClientAnimator().addLivingAnimation(LivingMotion.IDLE, Animations.ENDERMAN_IDLE);
				this.getClientAnimator().addLivingAnimation(LivingMotion.WALKING, Animations.ENDERMAN_WALK);
				this.onRage = false;
			}
		}
	}
	
	@Override
	protected void initAI() {
		super.initAI();
		normalAttack1 = new EntityAIPatternWithChance(this, this.orgEntity, 4.0D, 4.5D, 0.6F, true, MobAttackPatterns.ENDERMAN_PATTERN2);
    	normalAttack2 = new EntityAIAttackPattern(this, this.orgEntity, 0.0D, 3.5D, true, MobAttackPatterns.ENDERMAN_PATTERN1);
    	normalAttack3 = new AIEndermanTeleportKick(this, this.orgEntity);
    	normalAttack1 = new EntityAIPatternWithChance(this, this.orgEntity, 0.0D, 1.23D, 0.4F, true, MobAttackPatterns.ENDERMAN_PATTERN1);
    	normalAttack2 = new EntityAIPatternWithChance(this, this.orgEntity, 0.0D, 1.9D, 0.4F, true, MobAttackPatterns.ENDERMAN_PATTERN2);
    	normalAttack3 = new EntityAIPatternWithChance(this, this.orgEntity, 3.0D, 4.0D, 0.1F, true, MobAttackPatterns.ENDERMAN_PATTERN3);
    	normalAttack4 = new EntityAIPatternWithChance(this, this.orgEntity, 0.0D, 2.0D, 0.2F, true, MobAttackPatterns.ENDERMAN_PATTERN4);
    	normalAttack5 = new AIEndermanTeleportKick(this, this.orgEntity);
    	rageTarget = new EntityAINearestAttackableTarget<>(this.orgEntity, EntityPlayer.class, true);
    	rageChase = new AIEndermanRush(this, this.orgEntity);
	}
	
	@Override
	protected void initAnimator(AnimatorClient animatorClient) {
		super.initAnimator(animatorClient);
		animatorClient.addLivingAnimation(LivingMotion.DEATH, Animations.ENDERMAN_DEATH);
		animatorClient.addLivingAnimation(LivingMotion.WALKING, Animations.ENDERMAN_WALK);
		animatorClient.addLivingAnimation(LivingMotion.IDLE, Animations.ENDERMAN_IDLE);
		animatorClient.setCurrentLivingMotionsToDefault();
	}
	
	@Override
	public void updateMotion()
	{
		super.commonCreatureUpdateMotion();
	}
	
	@Override
	public void update()
	{
		if(orgEntity.getHealth() <= 0.0F)
		{
			orgEntity.rotationPitch = 0;
			
			if(orgEntity.deathTime > 1 && this.deathTimerExt < 20)
			{
				deathTimerExt++;
				orgEntity.deathTime--;
			}
		}
		
		if(this.isRaging() && !this.onRage && this.orgEntity.ticksExisted > 5)
		{
			this.convertRage();
		}
		else if(this.onRage && !this.isRaging())
		{
			this.convertNormal();
		}
		
		if(teleportCooled > 0)
		{
			teleportCooled--;
		}
		
		super.update();
	}
	
	@Override
	public boolean attackEntityFrom(DamageSource damageSource, float amount)
	{
		if(damageSource instanceof EntityDamageSource && !this.isRaging())
		{
			IExtendedDamageSource extDamageSource = null;
			if(damageSource instanceof IExtendedDamageSource)
				extDamageSource = ((IExtendedDamageSource)damageSource);
			
			if(extDamageSource == null || extDamageSource.getStunType() != StunType.HOLD)
			{
				int percentage = this.animator.getPlayer().getPlay() instanceof AttackAnimation ? 10 : 3;
				if(orgEntity.getRNG().nextInt(percentage) == 0)
				{
					for(int i = 0; i < 9; i++)
					{
						if(teleportRandomly())
						{
							if(damageSource.getTrueSource() instanceof EntityLivingBase)
								this.orgEntity.setRevengeTarget((EntityLivingBase) damageSource.getTrueSource());
							
							if(this.inaction)
								this.playAnimationSynchronize(Animations.ENDERMAN_TP_EMERGENCE, 0.0F);
							
							return false;
						}
					}
				}
			}
		}
		
		return super.attackEntityFrom(damageSource, amount);
	}
	
	protected boolean teleportRandomly()
    {
		if (!this.isRemote() && this.orgEntity.isEntityAlive())
		{
	        double d0 = this.orgEntity.posX + (this.orgEntity.getRNG().nextDouble() - 0.5D) * 64.0D;
	        double d1 = this.orgEntity.posY + (double)(this.orgEntity.getRNG().nextInt(64) - 32);
	        double d2 = this.orgEntity.posZ + (this.orgEntity.getRNG().nextDouble() - 0.5D) * 64.0D;
	        return this.teleportTo(d0, d1, d2);
	    }
		else
			return false;
    }
	
	private boolean teleportTo(double x, double y, double z)
    {
		boolean flag = orgEntity.attemptTeleport(x, y, z);
        
        if(flag)
        {
        	orgEntity.world.playSound((EntityPlayer)null, orgEntity.prevPosX, orgEntity.prevPosY, orgEntity.prevPosZ,
        			SoundEvents.ENTITY_ENDERMEN_TELEPORT, orgEntity.getSoundCategory(), 1.0F, 1.0F);
        	orgEntity.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);
        }
        
        return flag;
    }
	
	public boolean isRaging()
	{
		return this.orgEntity.getHealth() / this.orgEntity.getMaxHealth() < 0.33F;
	}
	
	protected void convertRage()
	{
		this.onRage = true;
		this.animator.playAnimation(Animations.ENDERMAN_HIT_RAGE, 0);
		
		if(this.isRemote())
		{
			this.getClientAnimator().addLivingAnimation(LivingMotion.IDLE, Animations.ENDERMAN_RAGE_IDLE);
			this.getClientAnimator().addLivingAnimation(LivingMotion.WALKING, Animations.ENDERMAN_RAGE_WALK);
		}
		else
		{
			if(!orgEntity.isAIDisabled())
			{
				this.orgEntity.tasks.removeTask(normalAttack1);
				this.orgEntity.tasks.removeTask(normalAttack2);
				this.orgEntity.tasks.removeTask(normalAttack3);
				this.orgEntity.tasks.removeTask(normalAttack4);
				this.orgEntity.tasks.removeTask(normalAttack5);
				this.orgEntity.tasks.addTask(1, rageChase);
				this.orgEntity.targetTasks.addTask(3, rageTarget);
				this.orgEntity.getDataManager().set(EntityEnderman.SCREAMING, Boolean.valueOf(true));
				this.orgEntity.addPotionEffect(new PotionEffect(ModEffects.STUN_IMMUNITY, 120000));
			}
		}
	}
	
	protected void convertNormal() {
		this.onRage = false;
		
		if (this.isRemote()) {
			this.getClientAnimator().addLivingAnimation(LivingMotion.IDLE, Animations.ENDERMAN_IDLE);
			this.getClientAnimator().addLivingAnimation(LivingMotion.WALKING, Animations.ENDERMAN_WALK);
		} else {
			if (!orgEntity.isAIDisabled()) {
				this.orgEntity.tasks.addTask(1, normalAttack1);
				this.orgEntity.tasks.addTask(1, normalAttack2);
				this.orgEntity.tasks.addTask(1, normalAttack3);
				this.orgEntity.tasks.addTask(1, normalAttack4);
				this.orgEntity.tasks.addTask(0, normalAttack5);
				this.orgEntity.tasks.removeTask(rageChase);
				this.orgEntity.targetTasks.removeTask(rageTarget);
				
				if(this.orgEntity.getAttackTarget() == null)
				{
					this.orgEntity.getDataManager().set(EntityEnderman.SCREAMING, Boolean.valueOf(false));
				}
				this.orgEntity.removePotionEffect(ModEffects.STUN_IMMUNITY);
			}
		}
	}
	
	@Override
	public void setAIAsUnarmed() {
		if (this.isRaging()) {
			orgEntity.targetTasks.addTask(3, rageTarget);
			orgEntity.tasks.addTask(1, rageChase);
		} else {
			orgEntity.tasks.addTask(1, normalAttack1);
			orgEntity.tasks.addTask(1, normalAttack2);
			orgEntity.tasks.addTask(1, normalAttack3);
			orgEntity.tasks.addTask(1, normalAttack4);
			orgEntity.tasks.addTask(0, normalAttack5);
		}
		
		orgEntity.tasks.addTask(1, new EntityAIChase(this, this.orgEntity, 0.75D, false));
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
		orgEntity.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);

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
	
	static class AIEndermanTeleportKick extends EntityAIPatternWithChance {
		private int delayCounter;
		private int cooldownTime;
		
		public AIEndermanTeleportKick(BipedMobData<?> mobdata, EntityMob attacker) {
			super(mobdata, attacker, 8.0D, 100.0D, 0.1F, false, null);
			super.setMutexBits(11);
		}
		
		@Override
		public boolean shouldExecute() {
			boolean b = cooldownTime <= 0;
			if (!b) {
				cooldownTime--;
			}
			
			return super.shouldExecute() && b;
		}

		@Override
		public boolean shouldContinueExecuting() {
			EntityLivingBase EntityLivingBase = this.attacker.getAttackTarget();
			boolean b = cooldownTime <= 100;
			if (!b) {
				cooldownTime = 500;
			}
	    	return isValidTarget(EntityLivingBase) && isTargetInRange(EntityLivingBase) && b;
	    }
		
		@Override
		public void startExecuting() {
			delayCounter = 35 + attacker.getRNG().nextInt(10);
			cooldownTime = 0;
		}

		@Override
		public void resetTask() {
			;
		}
		
		@Override
		public void updateTask() {
			EntityLivingBase target = attacker.getAttackTarget();
	        this.attacker.getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);
	        
			if (delayCounter-- < 0 && !this.mobdata.isInaction()) {
	        	Vec3f vec = new Vec3f((float)(attacker.posX - target.posX), 0, (float)(attacker.posZ - target.posZ));
	        	vec.normalise();
	        	vec.scale(1.414F);
	        	
	        	boolean flag = this.attacker.attemptTeleport(target.posX + vec.x, target.posY, target.posZ + vec.z);
	        	
				if (flag) {
	            	this.mobdata.rotateTo(target, 360.0F, true);
	            	
	                AttackAnimation kickAnimation = attacker.getRNG().nextBoolean() ? (AttackAnimation) Animations.ENDERMAN_TP_KICK1 : (AttackAnimation) Animations.ENDERMAN_TP_KICK2;
		        	mobdata.getServerAnimator().playAnimation(kickAnimation, 0);
		        	ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCPlayAnimationTP(kickAnimation.getId(), attacker.getEntityId(), 0.0F, 
		        			attacker.getAttackTarget().getEntityId(), attacker.posX, attacker.posY, attacker.posZ, attacker.rotationYaw), attacker);
		        	
		        	attacker.world.playSound((EntityPlayer)null, attacker.prevPosX, attacker.prevPosY, attacker.prevPosZ,
		        			SoundEvents.ENTITY_ENDERMEN_TELEPORT, attacker.getSoundCategory(), 1.0F, 1.0F);
	                attacker.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);
	                cooldownTime = 0;
				} else {
	            	cooldownTime++;
	            }
	        }
	    }
	}
	
	static class AIEndermanRush extends EntityAIAttackPattern {
		private float accelator;

		public AIEndermanRush(BipedMobData<?> mobdata, EntityMob attacker) {
			super(mobdata, attacker, 0.0F, 1.8F, false, null);
		}
		
		@Override
		public boolean shouldExecute() {
			return this.isValidTarget(attacker.getAttackTarget()) && !this.mobdata.isInaction();
		}

		@Override
		public boolean shouldContinueExecuting() {
	    	return isValidTarget(attacker.getAttackTarget()) && !this.mobdata.isInaction();
	    }
		
		@Override
		public void startExecuting() {
			ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCPlayAnimationTarget(403, attacker.getEntityId(),
					-1, true, attacker.getAttackTarget().getEntityId()), attacker);

			this.accelator = 0.0F;
		}

		@Override
		public void resetTask() {
			;
		}
		
		@Override
		public void updateTask() {
			if (isTargetInRange(attacker.getAttackTarget()) && canExecuteAttack()) {
	        	mobdata.getServerAnimator().playAnimation(Animations.ENDERMAN_GRASP, 0);
	        	ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCPlayAnimationTarget(Animations.ENDERMAN_GRASP.getId(), attacker.getEntityId(), 0, 
	        			attacker.getAttackTarget().getEntityId()), attacker);
			}
			
			attacker.getNavigator().setSpeed(0.025F * accelator * accelator + 1.0F);
			accelator = accelator > 2.0F ? accelator : accelator + 0.05F;
	    }
	}
}