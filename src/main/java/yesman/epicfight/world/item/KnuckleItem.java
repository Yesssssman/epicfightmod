package yesman.epicfight.world.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;

public class KnuckleItem extends WeaponItem {
	public KnuckleItem(Item.Properties build, Tier materialIn) {
		super(materialIn, 2, 0.0F, build);
	}
}