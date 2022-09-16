package yesman.epicfight.world.item;

import net.minecraft.block.BlockState;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;

public abstract class WeaponItem extends SwordItem {
	public WeaponItem(IItemTier tier, int damageIn, float speedIn, Item.Properties builder) {
		super(tier, damageIn, speedIn, builder);
	}
	
	@Override
	public boolean isCorrectToolForDrops(BlockState blockIn) {
        return false;
    }
}