package maninthehouse.epicfight.capabilities.entity.mob;

import maninthehouse.epicfight.animation.LivingMotion;
import maninthehouse.epicfight.client.animation.AnimatorClient;
import maninthehouse.epicfight.entity.ai.EntityAIRangeAttack;
import maninthehouse.epicfight.gamedata.Animations;
import maninthehouse.epicfight.gamedata.Models;
import maninthehouse.epicfight.model.Model;
import maninthehouse.epicfight.network.ModNetworkManager;
import maninthehouse.epicfight.network.server.STCPlayAnimationTarget;
import maninthehouse.epicfight.utils.math.VisibleMatrix4f;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.math.MathHelper;

public class WitchData extends BipedMobData<EntityWitch> {
	private boolean isDrinking;

	public WitchData() {
		super(Faction.NATURAL);
		isDrinking = false;
	}

	@Override
	public void postInit() {
		super.resetCombatAI();
		orgEntity.tasks.addTask(0, new WitchThrowPotionGoal(this.orgEntity, this, 1.0D, 60, 10.0F, 13));
	}

	@Override
	protected void initAI() {
		super.initAI();
	}

	public void setAIAsUnarmed() {

	}

	public void setAIAsArmed() {

	}

	public void setAIAsMounted() {

	}

	public void setAIAsRange() {

	}

	@Override
	protected void initAnimator(AnimatorClient animatorClient) {
		super.initAnimator(animatorClient);
		animatorClient.addLivingAnimation(LivingMotion.DEATH, Animations.BIPED_DEATH);
		animatorClient.addLivingAnimation(LivingMotion.IDLE, Animations.ILLAGER_IDLE);
		animatorClient.addLivingAnimation(LivingMotion.WALKING, Animations.ILLAGER_WALK);
		animatorClient.setCurrentLivingMotionsToDefault();
	}

	@Override
	public void updateMotion() {
		super.commonCreatureUpdateMotion();
	}
	
	@Override
	protected void updateOnClient() {
		super.updateOnClient();

		if (this.isDrinking != orgEntity.isDrinkingPotion()) {
			if (!this.isDrinking && this.orgEntity.getHealth() > 0) {
				this.getClientAnimator().playMixLayerAnimation(Animations.WITCH_DRINKING);
			}

			this.isDrinking = orgEntity.isDrinkingPotion();
		}
	}
	
	@Override
	protected void updateOnServer() {
		super.updateOnServer();

		if (this.isDrinking != orgEntity.isDrinkingPotion()) {
			if (!this.isDrinking && this.orgEntity.getHealth() > 0) {
				this.getServerAnimator().playAnimation(Animations.DUMMY_ANIMATION, 0);
			}

			this.isDrinking = orgEntity.isDrinkingPotion();
		}
	}

	public PotionType getPotionTypeWithTarget(EntityLivingBase target) {
		PotionType potiontype = PotionTypes.HARMING;
		
        double d1 = target.posX + target.motionX - orgEntity.posX;
        double d3 = target.posZ + target.motionZ - orgEntity.posZ;
        float f = MathHelper.sqrt(d1 * d1 + d3 * d3);
		
		if (f >= 8.0F && !target.isPotionActive(MobEffects.SLOWNESS)) {
			potiontype = PotionTypes.SLOWNESS;
		} else if (target.getHealth() >= 8.0F && !target.isPotionActive(MobEffects.POISON)) {
			potiontype = PotionTypes.POISON;
		} else if (f <= 3.0F && !target.isPotionActive(MobEffects.WEAKNESS) && orgEntity.getRNG().nextFloat() < 0.25F) {
			potiontype = PotionTypes.WEAKNESS;
		}
        
        return potiontype;
	}
	
	public void throwPotion(EntityLivingBase target, float distanceFactor) {
		double d0 = target.posY + (double)target.getEyeHeight() - 1.100000023841858D;
        double d1 = target.posX + target.motionX - orgEntity.posX;
        double d2 = d0 - orgEntity.posY;
        double d3 = target.posZ + target.motionZ - orgEntity.posZ;
        float f = MathHelper.sqrt(d1 * d1 + d3 * d3);
        
        EntityPotion entitypotion = new EntityPotion(orgEntity.world, orgEntity, this.orgEntity.getHeldItemMainhand());
        entitypotion.rotationPitch -= -20.0F;
        entitypotion.shoot(d1, d2 + (double)(f * 0.2F), d3, 0.75F, 8.0F);
        orgEntity.world.playSound((EntityPlayer)null, orgEntity.posX, orgEntity.posY, orgEntity.posZ, SoundEvents.ENTITY_WITCH_THROW, orgEntity.getSoundCategory(), 1.0F, 0.8F + orgEntity.getRNG().nextFloat() * 0.4F);
        orgEntity.world.spawnEntity(entitypotion);
        
        this.orgEntity.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
    }
	
	@Override
	public VisibleMatrix4f getHeadMatrix(float partialTicks) {
		if (orgEntity.isDrinkingPotion()) {
			return new VisibleMatrix4f();
		} else {
			return super.getHeadMatrix(partialTicks);
		}
	}
	
	class WitchThrowPotionGoal extends EntityAIRangeAttack {
		public WitchThrowPotionGoal(IRangedAttackMob attacker, BipedMobData<?> entitydata, double movespeed, int maxAttackTime, float maxAttackDistanceIn, int animationFrame) {
			super(attacker, entitydata, Animations.BIPED_MOB_THROW, movespeed, maxAttackTime, maxAttackDistanceIn, animationFrame);
	    }
	    
	    @Override
		public void resetTask() {
	    	this.entityHost.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
	    }
	    
	    @Override
		public void updateTask() {
	        double d0 = this.entityHost.getDistanceSq(this.attackTarget.posX, this.attackTarget.getEntityBoundingBox().minY, this.attackTarget.posZ);
	        boolean flag = this.entityHost.getEntitySenses().canSee(this.attackTarget);
	        
	        if(flag) {
	            ++this.seeTime;
	        } else {
	            this.seeTime = 0;
	        }

	        if (d0 <= (double)this.maxAttackDistance && this.seeTime >= 20) {
	            this.entityHost.getNavigator().clearPath();
	        } else {
	            this.entityHost.getNavigator().tryMoveToEntityLiving(this.attackTarget, this.entityMoveSpeed);
	        }
	        
	        this.entityHost.getLookHelper().setLookPositionWithEntity(this.attackTarget, 30.0F, 30.0F);
	        
			if (WitchData.this.orgEntity.isDrinkingPotion()) {
        		float f2 = MathHelper.sqrt(d0) / this.attackRadius;
	            this.rangedAttackTime = MathHelper.floor(f2 * (float)(this.maxRangedAttackTime - this.attackIntervalMin) + (float)this.attackIntervalMin);
			} else if (--this.rangedAttackTime == this.animationFrame && !this.entitydata.isInaction()) {
	        	this.entityHost.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), WitchData.this.getPotionTypeWithTarget(this.attackTarget)));
	        	entitydata.getServerAnimator().playAnimation(rangeAttackAnimation, 0);
	        	ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCPlayAnimationTarget(rangeAttackAnimation.getId(), entityHost.getEntityId(), 0, attackTarget.getEntityId()), entityHost);
			} else if (this.rangedAttackTime == 0) {
	            if (!flag) return;
	            float f = MathHelper.sqrt(d0) / this.attackRadius;
	            float lvt_5_1_ = MathHelper.clamp(f, 0.1F, 1.0F);
	            WitchData.this.throwPotion(this.attackTarget, lvt_5_1_);
	            this.rangedAttackTime = MathHelper.floor(f * (float)(this.maxRangedAttackTime - this.attackIntervalMin) + (float)this.attackIntervalMin);
			} else if (this.rangedAttackTime < 0) {
	            float f2 = MathHelper.sqrt(d0) / this.attackRadius;
	            this.rangedAttackTime = MathHelper.floor(f2 * (float)(this.maxRangedAttackTime - this.attackIntervalMin) + (float)this.attackIntervalMin);
	        }
	    }
	}
	
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return modelDB.ENTITY_WITCH;
	}
}