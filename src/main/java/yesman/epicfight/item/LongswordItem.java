package yesman.epicfight.item;

import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemTier;

public class LongswordItem extends WeaponItem {
	public LongswordItem(Item.Properties build, ItemTier materialIn) {
		super(materialIn, 4, -2.6F, build);
	}
	
	@Override
	public boolean canHarvestBlock(BlockState blockIn) {
        return false;
    }
}