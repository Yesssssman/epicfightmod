package yesman.epicfight.world.item;

import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;

public class SpearItem extends WeaponItem {
	public SpearItem(Item.Properties build, IItemTier materialIn) {
		super(materialIn, 3, -2.8F, build);
	}
}