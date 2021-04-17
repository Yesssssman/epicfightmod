package maninthehouse.epicfight.capabilities.entity.mob;

import maninthehouse.epicfight.animation.types.StaticAnimation;
import maninthehouse.epicfight.capabilities.entity.MobData;
import maninthehouse.epicfight.client.animation.AnimatorClient;
import maninthehouse.epicfight.entity.ai.EntityAIArcher;
import maninthehouse.epicfight.entity.ai.EntityAIAttackPattern;
import maninthehouse.epicfight.entity.ai.EntityAIChase;
import maninthehouse.epicfight.gamedata.Animations;
import maninthehouse.epicfight.utils.game.IExtendedDamageSource.StunType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.world.EnumDifficulty;

public abstract class BipedMobData<T extends EntityCreature> extends MobData<T> {
	public BipedMobData(Faction faction) {
		super(faction);
	}

	@Override
	public void postInit() {
		if (!this.isRemote() && !this.orgEntity.isAIDisabled()) {
			super.resetCombatAI();
			Item heldItem = this.orgEntity.getHeldItemMainhand().getItem();

			if (heldItem instanceof ItemBow && this.orgEntity instanceof IRangedAttackMob) {
				this.setAIAsRange();
			} else if (this.orgEntity.getRidingEntity() != null
					&& this.orgEntity.getRidingEntity() instanceof EntityMob) {
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
		animatorClient.mixLayer.setJointMask("Root", "Torso");
	}

	public void setAIAsUnarmed() {

	}

	public void setAIAsArmed() {
		orgEntity.tasks.addTask(1, new EntityAIAttackPattern(this, this.orgEntity, 0.0D, 2.0D, true, MobAttackPatterns.BIPED_ARMED_ONEHAND));
		orgEntity.tasks.addTask(1, new EntityAIChase(this, this.orgEntity, 1.0D, false));
	}

	public void setAIAsMounted(Entity ridingEntity) {
		if (isArmed()) {
			if (isArmed()) {
				orgEntity.tasks.addTask(1, new EntityAIAttackPattern(this, this.orgEntity, 0.0D, 2.0D, true, MobAttackPatterns.BIPED_MOUNT_SWORD));

				if (ridingEntity instanceof AbstractHorse) {
					orgEntity.tasks.addTask(1, new EntityAIChase(this, this.orgEntity, 1.0D, false));
				}
			}
		}
	}

	public void setAIAsRange() {
		int cooldown = this.orgEntity.world.getDifficulty() != EnumDifficulty.HARD ? 40 : 20;
		orgEntity.tasks.addTask(1, new EntityAIArcher(this, this.orgEntity, 1.0D, cooldown, 15.0F));
	}

	public boolean isArmed() {
		Item heldItem = this.orgEntity.getHeldItemMainhand().getItem();
		return heldItem instanceof ItemSword || heldItem instanceof ItemTool;
	}

	public void onMount(boolean isMount, Entity ridingEntity) {
		if (orgEntity == null) {
			return;
		}

		this.resetCombatAI();

		if (isMount) {
			this.setAIAsMounted(ridingEntity);
		} else {
			if (this.isArmed()) {
				this.setAIAsArmed();
			} else {
				this.setAIAsUnarmed();
			}
		}
	}

	@Override
	public StaticAnimation getHitAnimation(StunType stunType) {
		if (orgEntity.getRidingEntity() != null) {
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