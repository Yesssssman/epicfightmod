package yesman.epicfight.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;

public abstract class WeaponItem extends SwordItem {
	public WeaponItem(IItemTier tier, int damageIn, float speedIn, Item.Properties builder) {
		super(tier, damageIn, speedIn, builder);
	}
	
	@Override
	public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damageItem(1, attacker, (entity) -> {
        	entity.sendBreakAnimation(EquipmentSlotType.MAINHAND);
        });
        return true;
    }
}