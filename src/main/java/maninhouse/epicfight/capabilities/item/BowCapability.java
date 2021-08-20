package maninhouse.epicfight.capabilities.item;

import maninhouse.epicfight.gamedata.Animations;
import net.minecraft.item.Item;

public class BowCapability extends RangedWeaponCapability {
	public BowCapability(Item item) {
		super(item, null, Animations.BIPED_BOW_AIM, Animations.BIPED_BOW_SHOT);
	}
}