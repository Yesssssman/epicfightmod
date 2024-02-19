package yesman.epicfight.world.capabilities.item;

import java.util.List;

import com.mojang.datafixers.util.Pair;

import net.minecraft.world.item.ItemStack;
import yesman.epicfight.data.conditions.itemstack.ItemStackCondition;

public class TagBasedSeparativeCapability extends CapabilityItem {
	private final List<Pair<ItemStackCondition, CapabilityItem>> variations;
	private final CapabilityItem defaultCapability;
	
	public TagBasedSeparativeCapability(List<Pair<ItemStackCondition, CapabilityItem>> variations, CapabilityItem defaultCapability) {
		super(CapabilityItem.builder().category(WeaponCategories.NOT_WEAPON));
		this.variations = variations;
		this.defaultCapability = defaultCapability;
	}
	
	@Override
	public CapabilityItem getResult(ItemStack itemstack) {
		for (Pair<ItemStackCondition, CapabilityItem> pair : this.variations) {
			if (pair.getFirst().predicate(itemstack)) {
				return pair.getSecond().getResult(itemstack);
			}
		}
		
		return this.defaultCapability;
	}
}