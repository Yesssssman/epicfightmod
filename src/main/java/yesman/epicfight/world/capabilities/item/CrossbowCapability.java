package yesman.epicfight.world.capabilities.item;

import net.minecraft.world.item.Item;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.gameasset.Animations;

public class CrossbowCapability extends RangedWeaponCapability {
	public CrossbowCapability(Item item) {
		super(item, Animations.BIPED_CROSSBOW_RELOAD, Animations.BIPED_CROSSBOW_AIM, Animations.BIPED_CROSSBOW_SHOT);
		this.rangeAnimationSet.put(LivingMotion.IDLE, Animations.BIPED_IDLE_CROSSBOW);
		this.rangeAnimationSet.put(LivingMotion.KNEEL, Animations.BIPED_IDLE_CROSSBOW);
		this.rangeAnimationSet.put(LivingMotion.WALK, Animations.BIPED_IDLE_CROSSBOW);
		this.rangeAnimationSet.put(LivingMotion.RUN, Animations.BIPED_IDLE_CROSSBOW);
		this.rangeAnimationSet.put(LivingMotion.SNEAK, Animations.BIPED_IDLE_CROSSBOW);
		this.rangeAnimationSet.put(LivingMotion.SWIM, Animations.BIPED_IDLE_CROSSBOW);
		this.rangeAnimationSet.put(LivingMotion.FLOAT, Animations.BIPED_IDLE_CROSSBOW);
		this.rangeAnimationSet.put(LivingMotion.FALL, Animations.BIPED_IDLE_CROSSBOW);
	}
}