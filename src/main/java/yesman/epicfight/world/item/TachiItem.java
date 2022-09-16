package yesman.epicfight.world.item;

import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;

public class TachiItem extends WeaponItem {
	public TachiItem(Item.Properties build, IItemTier materialIn) {
		super(materialIn, 4, -2.8F, build);
	}
}