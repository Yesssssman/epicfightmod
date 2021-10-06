package yesman.epicfight.capabilities.entity.mob;

import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.AbstractRaiderEntity;
import net.minecraft.entity.monster.WitchEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.PotionEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Effects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.animation.LivingMotion;
import yesman.epicfight.client.animation.AnimatorClient;
import yesman.epicfight.entity.ai.RangeAttackMobGoal;
import yesman.epicfight.gamedata.Animations;
import yesman.epicfight.gamedata.Models;
import yesman.epicfight.model.Model;
import yesman.epicfight.utils.math.OpenMatrix4f;

public class WitchData extends BipedMobData<WitchEntity> {
	public WitchData() {
		super(Faction.NATURAL);
	}

	@Override
	public void postInit() {
		super.resetCombatAI();
		this.orgEntity.goalSelector.addGoal(0, new WitchThrowPotionGoal(this.orgEntity, this, 1.0D, 60, 10.0F, 5));
	}
	
	@Override
	public void setAIAsUnarmed() {

	}
	
	@Override
	public void setAIAsArmed() {

	}

	public void setAIAsMounted() {

	}

	public void setAIAsRanged() {

	}
	
	@Override
	protected void initAnimator(AnimatorClient animatorClient) {
		super.initAnimator(animatorClient);
		animatorClient.addLivingAnimation(LivingMotion.DEATH, Animations.BIPED_DEATH);
		animatorClient.addLivingAnimation(LivingMotion.IDLE, Animations.ILLAGER_IDLE);
		animatorClient.addLivingAnimation(LivingMotion.WALK, Animations.ILLAGER_WALK);
		animatorClient.addOverwritingLivingMotion(LivingMotion.DRINK, Animations.WITCH_DRINKING);
	}
	
	@Override
	public void updateMotion(boolean considerInaction) {
		super.commonCreatureUpdateMotion(considerInaction);
		if (this.orgEntity.isDrinkingPotion()) {
			this.currentOverwritingMotion = LivingMotion.DRINK;
		}
	}
	
	public Potion getPotionTypeWithTarget(LivingEntity target) {
		Vector3d vec3d = target.getMotion();
        double d0 = target.getPosX() + vec3d.x - this.orgEntity.getPosX();
        double d2 = target.getPosZ() + vec3d.z - this.orgEntity.getPosZ();
        float f = MathHelper.sqrt(d0 * d0 + d2 * d2);
        Potion potion = Potions.HARMING;
        if (target instanceof AbstractRaiderEntity){
           if (target.getHealth() <= 4.0F) {
              potion = Potions.HEALING;
           } else {
              potion = Potions.REGENERATION;
           }

           this.orgEntity.setAttackTarget((LivingEntity)null);
        } else if (f >= 8.0F && !target.isPotionActive(Effects.SLOWNESS)) {
           potion = Potions.SLOWNESS;
        } else if (target.getHealth() >= 8.0F && !target.isPotionActive(Effects.POISON)) {
           potion = Potions.POISON;
        } else if (f <= 3.0F && !target.isPotionActive(Effects.WEAKNESS) && this.orgEntity.getRNG().nextFloat() < 0.25F) {
           potion = Potions.WEAKNESS;
        }
        
        return potion;
	}

	public void throwPotion(LivingEntity target, float distanceFactor) {
		Vector3d vec3d = target.getMotion();
        double d0 = target.getPosX() + vec3d.x - this.orgEntity.getPosX();
        double d1 = target.getPosY() + (double)target.getEyeHeight() - (double)1.1F - this.orgEntity.getPosY();
        double d2 = target.getPosZ() + vec3d.z - this.orgEntity.getPosZ();
        float f = MathHelper.sqrt(d0 * d0 + d2 * d2);
        
        PotionEntity potionEntity = new PotionEntity(this.orgEntity.world, this.orgEntity);
        potionEntity.setItem(PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION), this.getPotionTypeWithTarget(target)));
        potionEntity.rotationPitch -= -20.0F;
        potionEntity.shoot(d0, d1 + (double)(f * 0.2F), d2, 0.75F, 8.0F);
        
        this.orgEntity.world.playSound((PlayerEntity)null, this.orgEntity.getPosX(), this.orgEntity.getPosY(), this.orgEntity.getPosZ(),
        		SoundEvents.ENTITY_WITCH_THROW, this.orgEntity.getSoundCategory(), 1.0F, 0.8F + this.orgEntity.getRNG().nextFloat() * 0.4F);
        this.orgEntity.world.addEntity(potionEntity);
        
        this.orgEntity.setItemStackToSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
    }
	
	@Override
	public <M extends Model> M getEntityModel(Models<M> modelDB) {
		return modelDB.witch;
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public OpenMatrix4f getHeadMatrix(float partialTicks) {
		if (this.orgEntity.isDrinkingPotion()) {
			return new OpenMatrix4f();
		} else {
			return super.getHeadMatrix(partialTicks);
		}
	}
	
	class WitchThrowPotionGoal extends RangeAttackMobGoal {
		public WitchThrowPotionGoal(IRangedAttackMob attacker, BipedMobData<?> entitydata, double movespeed, int maxAttackTime, float maxAttackDistanceIn, int animationFrame) {
			super(attacker, entitydata, Animations.BIPED_MOB_THROW, movespeed, maxAttackTime, maxAttackDistanceIn, animationFrame);
	    }
	    
		@Override
		public void resetTask() {
	    	this.entityHost.setItemStackToSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
	    }
	    
	    @Override
		public void tick() {
	        double d0 = this.entityHost.getDistanceSq(this.attackTarget.getPosX(), this.attackTarget.getBoundingBox().minY, this.attackTarget.getPosZ());
	        boolean flag = this.entityHost.getEntitySenses().canSee(this.attackTarget);
	        
	        if (flag) {
	            ++this.seeTime;
	        } else {
	            this.seeTime = 0;
	        }

	        if (d0 <= (double)this.maxAttackDistance && this.seeTime >= 20) {
	            this.entityHost.getNavigator().clearPath();
	        } else {
	            this.entityHost.getNavigator().tryMoveToEntityLiving(this.attackTarget, this.entityMoveSpeed);
	        }
	        
	        this.entityHost.getLookController().setLookPositionWithEntity(this.attackTarget, 30.0F, 30.0F);
	        
			if (WitchData.this.orgEntity.isDrinkingPotion()) {
				float f2 = MathHelper.sqrt(d0) / this.attackRadius;
	            this.rangedAttackTime = MathHelper.floor(f2 * (float)(this.maxRangedAttackTime - this.attackIntervalMin) + (float)this.attackIntervalMin);
			} else if (--this.rangedAttackTime == this.animationFrame && !this.entitydata.getEntityState().isInaction()) {
	        	this.entityHost.setItemStackToSlot(EquipmentSlotType.MAINHAND, PotionUtils.addPotionToItemStack(new ItemStack(Items.SPLASH_POTION),
	        			WitchData.this.getPotionTypeWithTarget(this.attackTarget)));
	        	this.entitydata.playAnimationSynchronize(this.shotAnimation, 0);
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
}