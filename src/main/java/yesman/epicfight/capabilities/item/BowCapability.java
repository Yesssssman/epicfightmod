package yesman.epicfight.capabilities.item;

import net.minecraft.item.Item;
import yesman.epicfight.gamedata.Animations;

public class BowCapability extends RangedWeaponCapability {
	public BowCapability(Item item) {
		super(item, null, Animations.BIPED_BOW_AIM, Animations.BIPED_BOW_SHOT);
	}
}