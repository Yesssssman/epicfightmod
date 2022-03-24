package yesman.epicfight.world.capabilities.item;

import net.minecraft.world.item.Item;
import yesman.epicfight.gameasset.Animations;

public class BowCapability extends RangedWeaponCapability {
	public BowCapability(Item item) {
		super(item, null, Animations.BIPED_BOW_AIM, Animations.BIPED_BOW_SHOT);
	}
}