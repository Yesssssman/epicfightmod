package yesman.epicfight.item;

import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemTier;

public class TachiItem extends WeaponItem {
	public TachiItem(Item.Properties build, ItemTier materialIn) {
		super(materialIn, 4, -2.4F, build);
	}
	
	@Override
	public boolean canHarvestBlock(BlockState blockIn) {
        return false;
    }
}