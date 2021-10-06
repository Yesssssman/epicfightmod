package yesman.epicfight.item;

import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemTier;

public class DaggerItem extends WeaponItem {
	public DaggerItem(Item.Properties build, ItemTier materialIn) {
		super(materialIn, 1, -1.6F, build);
	}
	
	@Override
	public boolean canHarvestBlock(BlockState blockIn) {
        return false;
    }
}