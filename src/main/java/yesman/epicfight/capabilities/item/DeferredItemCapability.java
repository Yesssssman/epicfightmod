package yesman.epicfight.capabilities.item;

import net.minecraft.item.ItemStack;

public class DeferredItemCapability extends CapabilityItem {
	private ItemStack stack;
	
	public DeferredItemCapability(ItemStack stack) {
		super(stack.getItem(), WeaponCategory.NOT_WEAON);
		this.stack = stack;
	}
	
	public ItemStack getStack() {
		return this.stack;
	}
}