package yesman.epicfight.data.conditions.itemstack;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import yesman.epicfight.data.conditions.Condition;

public abstract class ItemStackCondition implements Condition<ItemStack> {
	public ItemStackCondition(CompoundTag tag) {
		this.read(tag);
	}
}