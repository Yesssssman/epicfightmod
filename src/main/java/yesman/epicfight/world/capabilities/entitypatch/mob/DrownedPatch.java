package yesman.epicfight.world.capabilities.entitypatch.mob;

import com.google.common.collect.ImmutableMap;

import net.minecraft.world.entity.monster.Drowned;
import yesman.epicfight.gameasset.MobCombatBehaviors;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategory;
import yesman.epicfight.world.entity.ai.goal.ChasingGoal;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviorGoal;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors;

public class DrownedPatch extends ZombiePatch<Drowned> {
	@Override
	protected void setWeaponMotions() {
		super.setWeaponMotions();
		this.weaponAttackMotions.put(WeaponCategory.TRIDENT, ImmutableMap.of(CapabilityItem.Style.COMMON, MobCombatBehaviors.DROWNED_TRIDENT));
	}
	
	@Override
	public void setAIAsInfantry(boolean holdingRanedWeapon) {
		CombatBehaviors.Builder<HumanoidMobPatch<?>> builder = this.getHoldingItemWeaponMotionBuilder();
		
		if (builder != null) {
			this.original.goalSelector.addGoal(0, new CombatBehaviorGoal<>(this, builder.build(this)));
		}
		
		this.original.goalSelector.addGoal(1, new DrownedChasingGoal(this, this.original, 1.0D, true));
	}
	
	static class DrownedChasingGoal extends ChasingGoal {
		private final Drowned drowned;
		
		public DrownedChasingGoal(DrownedPatch drownedpatch, Drowned drowned, double speedIn, boolean useLongMemory) {
			super(drownedpatch, drowned, speedIn, 0.0D, useLongMemory);
			this.drowned = drowned;
		}
		
		@Override
		public boolean canUse() {
	        return super.canUse() && this.drowned.okTarget(this.drowned.getTarget());
	    }
		
		@Override
		public boolean canContinueToUse() {
			return super.canContinueToUse() && this.drowned.okTarget(this.drowned.getTarget());
	    }
	}
}