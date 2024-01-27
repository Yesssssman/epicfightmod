package yesman.epicfight.world.capabilities.item;

import java.util.List;
import java.util.function.Predicate;

import com.mojang.datafixers.util.Pair;

import net.minecraft.world.item.ItemStack;

public class TagBasedSeparativeCapability extends CapabilityItem {
	private final List<Pair<Predicate<ItemStack>, CapabilityItem>> variations;
	private final CapabilityItem defaultCapability;
	
	public TagBasedSeparativeCapability(List<Pair<Predicate<ItemStack>, CapabilityItem>> variations, CapabilityItem defaultCapability) {
		super(CapabilityItem.builder().category(WeaponCategories.NOT_WEAPON));
		this.variations = variations;
		this.defaultCapability = defaultCapability;
	}
	
	@Override
	public CapabilityItem getResult(ItemStack item) {
		for (Pair<Predicate<ItemStack>, CapabilityItem> pair : this.variations) {
			if (pair.getFirst().test(item)) {
				return pair.getSecond().getResult(item);
			}
		}
		
		return this.defaultCapability;
	}
}