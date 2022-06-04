package yesman.epicfight.world.capabilities.item;

import net.minecraft.world.item.Item;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.gameasset.Animations;

public class CrossbowCapability extends RangedWeaponCapability {
	public CrossbowCapability(Item item) {
		super(item, Animations.BIPED_CROSSBOW_RELOAD, Animations.BIPED_CROSSBOW_AIM, Animations.BIPED_CROSSBOW_SHOT);
		this.rangeAnimationSet.put(LivingMotions.IDLE, Animations.BIPED_HOLD_CROSSBOW);
		this.rangeAnimationSet.put(LivingMotions.KNEEL, Animations.BIPED_HOLD_CROSSBOW);
		this.rangeAnimationSet.put(LivingMotions.WALK, Animations.BIPED_HOLD_CROSSBOW);
		this.rangeAnimationSet.put(LivingMotions.RUN, Animations.BIPED_HOLD_CROSSBOW);
		this.rangeAnimationSet.put(LivingMotions.SNEAK, Animations.BIPED_HOLD_CROSSBOW);
		this.rangeAnimationSet.put(LivingMotions.SWIM, Animations.BIPED_HOLD_CROSSBOW);
		this.rangeAnimationSet.put(LivingMotions.FLOAT, Animations.BIPED_HOLD_CROSSBOW);
		this.rangeAnimationSet.put(LivingMotions.FALL, Animations.BIPED_HOLD_CROSSBOW);
	}
}