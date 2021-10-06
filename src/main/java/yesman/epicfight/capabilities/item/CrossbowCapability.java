package yesman.epicfight.capabilities.item;

import net.minecraft.item.Item;
import yesman.epicfight.animation.LivingMotion;
import yesman.epicfight.gamedata.Animations;

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