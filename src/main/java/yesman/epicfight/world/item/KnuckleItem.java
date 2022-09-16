package yesman.epicfight.world.item;

import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;

public class KnuckleItem extends WeaponItem {
	public KnuckleItem(Item.Properties build, IItemTier materialIn) {
		super(materialIn, 2, 0.0F, build);
	}
}