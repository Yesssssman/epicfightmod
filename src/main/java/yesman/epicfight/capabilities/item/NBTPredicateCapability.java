package yesman.epicfight.capabilities.item;

import java.util.List;
import java.util.function.Predicate;

import com.mojang.datafixers.util.Pair;

import net.minecraft.item.ItemStack;

public class NBTPredicateCapability extends CapabilityItem {
	private List<Pair<Predicate<ItemStack>, CapabilityItem>> variables;
	private CapabilityItem defaultCapability;
	
	public NBTPredicateCapability(List<Pair<Predicate<ItemStack>, CapabilityItem>> variables, CapabilityItem defaultCapability) {
		super(WeaponCategory.NOT_WEAON);
		this.variables = variables;
		this.defaultCapability = defaultCapability;
	}
	
	@Override
	public CapabilityItem get(ItemStack item) {
		for (Pair<Predicate<ItemStack>, CapabilityItem> pair : this.variables) {
			if (pair.getFirst().test(item)) {
				return pair.getSecond().get(item);
			}
		}
		return this.defaultCapability;
	}
}