package yesman.epicfight.item;

import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemTier;

public class SpearItem extends WeaponItem {
	public SpearItem(Item.Properties build, ItemTier materialIn) {
		super(materialIn, 3, -2.8F, build);
	}
	
	@Override
	public boolean canHarvestBlock(BlockState blockIn) {
        return false;
    }
}