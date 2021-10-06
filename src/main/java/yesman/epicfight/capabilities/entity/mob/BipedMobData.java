package yesman.epicfight.capabilities.entity.mob;

import net.minecraft.entity.Entity;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ShootableItem;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import net.minecraft.item.TridentItem;
import yesman.epicfight.animation.types.StaticAnimation;
import yesman.epicfight.capabilities.entity.MobData;
import yesman.epicfight.client.animation.AnimatorClient;
import yesman.epicfight.entity.ai.AttackPatternGoal;
import yesman.epicfight.entity.ai.ChasingGoal;
import yesman.epicfight.gamedata.Animations;
import yesman.epicfight.gamedata.AttackCombos;
import yesman.epicfight.utils.game.IExtendedDamageSource.StunType;

public abstract class BipedMobData<T extends MobEntity> extends MobData<T> {
	public BipedMobData(Faction faction) {
		super(faction);
	}

	@Override
	public void postInit() {
		if (!this.isRemote() && !this.orgEntity.isAIDisabled()) {
			super.resetCombatAI();
			Item heldItem = this.orgEntity.getHeldItemMainhand().getItem();
			
			if (heldItem instanceof ShootableItem && this.orgEntity instanceof IRangedAttackMob) {
				//this.setAIAsRanged();
			} else if(this.orgEntity.getRidingEntity() != null && this.orgEntity.getRidingEntity() instanceof MobEntity) {
				this.setAIAsMounted(this.orgEntity.getRidingEntity());
			} else if (isArmed()) {
				this.setAIAsArmed();
			} else {
				this.setAIAsUnarmed();
			}
		}
	}
	
	@Override
	protected void initAnimator(AnimatorClient animatorClient) {
		
	}

	public void setAIAsUnarmed() {
		
	}

	public void setAIAsArmed() {
		this.orgEntity.goalSelector.addGoal(0, new AttackPatternGoal(this, this.orgEntity, 0.0D, 2.0D, true, AttackCombos.BIPED_ARMED));
		this.orgEntity.goalSelector.addGoal(1, new ChasingGoal(this, this.orgEntity, 1.0D, false));
	}
	
	public void setAIAsMounted(Entity ridingEntity) {
		if (this.isArmed()) {
			this.orgEntity.goalSelector.addGoal(0, new AttackPatternGoal(this, this.orgEntity, 0.0D, 2.0D, true, AttackCombos.BIPED_MOUNT_SWORD));
			
			if (ridingEntity instanceof AbstractHorseEntity) {
				this.orgEntity.goalSelector.addGoal(1, new ChasingGoal(this, this.orgEntity, 1.0D, false));
			}
		}
	}
	
	public boolean isArmed() {
		Item heldItem = this.orgEntity.getHeldItemMainhand().getItem();
		return heldItem instanceof SwordItem || heldItem instanceof ToolItem || heldItem instanceof TridentItem;
	}
	
	public void onMount(boolean isMount, Entity ridingEntity) {
		if(orgEntity == null) {
			return;
		}
		
		this.resetCombatAI();
		
		if(isMount) {
			this.setAIAsMounted(ridingEntity);
		} else {
			if(this.isArmed()) {
				this.setAIAsArmed();
			} else {
				this.setAIAsUnarmed();
			}
		}
	}
	
	@Override
	public StaticAnimation getHitAnimation(StunType stunType) {
		if (this.orgEntity.getRidingEntity() != null) {
			return Animations.BIPED_HIT_ON_MOUNT;
		} else {
			switch (stunType) {
			case LONG:
				return Animations.BIPED_HIT_LONG;
			case SHORT:
				return Animations.BIPED_HIT_SHORT;
			case HOLD:
				return Animations.BIPED_HIT_SHORT;
			default:
				return null;
			}
		}
	}
}