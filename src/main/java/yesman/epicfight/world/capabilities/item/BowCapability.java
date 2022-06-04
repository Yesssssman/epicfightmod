package yesman.epicfight.world.capabilities.item;

import net.minecraft.world.item.Item;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.gameasset.Animations;

public class BowCapability extends RangedWeaponCapability {
	public BowCapability(Item item) {
		super(item, null, Animations.BIPED_BOW_AIM, Animations.BIPED_BOW_SHOT);
		this.rangeAnimationSet.put(LivingMotions.IDLE, Animations.BIPED_IDLE);
		this.rangeAnimationSet.put(LivingMotions.WALK, Animations.BIPED_WALK);
	}
}