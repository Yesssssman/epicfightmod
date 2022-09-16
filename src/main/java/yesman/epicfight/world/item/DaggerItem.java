package yesman.epicfight.world.item;

import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;

public class DaggerItem extends WeaponItem {
	public DaggerItem(Item.Properties build, IItemTier materialIn) {
		super(materialIn, 1, -1.6F, build);
	}
}