package yesman.epicfight.world.capabilities.entitypatch.mob;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.TridentItem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.api.utils.game.ExtendedDamageSource.StunType;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.MobCombatBehaviors;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;
import yesman.epicfight.world.entity.ai.goal.AttackPatternGoal;
import yesman.epicfight.world.entity.ai.goal.ChasingGoal;

public abstract class HumanoidMobPatch<T extends Mob> extends MobPatch<T> {
	public HumanoidMobPatch(Faction faction) {
		super(faction);
	}

	@Override
	public void postInit() {
		if (!this.isLogicalClient() && !this.original.isNoAi()) {
			super.resetCombatAI();
			Item heldItem = this.original.getMainHandItem().getItem();
			
			if (heldItem instanceof ProjectileWeaponItem && this.original instanceof RangedAttackMob) {
				//this.setAIAsRanged();
			} else if(this.original.getVehicle() != null && this.original.getVehicle() instanceof Mob) {
				this.setAIAsMounted(this.original.getVehicle());
			} else if (isArmed()) {
				this.setAIAsArmed();
			} else {
				this.setAIAsUnarmed();
			}
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public abstract void initAnimator(ClientAnimator clientAnimator);

	public void setAIAsUnarmed() {
		
	}

	public void setAIAsArmed() {
		this.original.goalSelector.addGoal(0, new AttackPatternGoal(this, this.original, 0.0D, 2.0D, true, MobCombatBehaviors.BIPED_ARMED));
		this.original.goalSelector.addGoal(1, new ChasingGoal(this, this.original, 1.0D, false));
	}
	
	public void setAIAsMounted(Entity ridingEntity) {
		if (this.isArmed()) {
			this.original.goalSelector.addGoal(0, new AttackPatternGoal(this, this.original, 0.0D, 2.0D, true, MobCombatBehaviors.BIPED_MOUNT_SWORD));
			
			if (ridingEntity instanceof AbstractHorse) {
				this.original.goalSelector.addGoal(1, new ChasingGoal(this, this.original, 1.0D, false));
			}
		}
	}
	
	public boolean isArmed() {
		Item heldItem = this.original.getMainHandItem().getItem();
		return heldItem instanceof SwordItem || heldItem instanceof DiggerItem || heldItem instanceof TridentItem;
	}
	
	public void onMount(boolean isMount, Entity ridingEntity) {
		if(original == null) {
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
		if (this.original.getVehicle() != null) {
			return Animations.BIPED_HIT_ON_MOUNT;
		} else {
			switch (stunType) {
			case LONG:
				return Animations.BIPED_HIT_LONG;
			case SHORT:
				return Animations.BIPED_HIT_SHORT;
			case HOLD:
				return Animations.BIPED_HIT_SHORT;
			case KNOCKDOWN:
				return Animations.BIPED_KNOCKDOWN;
			case FALL:
				return Animations.BIPED_LANDING;
			case NONE:
				return null;
			}
		}
		
		return null;
	}
}