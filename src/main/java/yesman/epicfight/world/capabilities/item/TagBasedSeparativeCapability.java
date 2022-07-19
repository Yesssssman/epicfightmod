package yesman.epicfight.world.capabilities.item;

import java.util.List;
import java.util.function.Predicate;

import com.mojang.datafixers.util.Pair;

import net.minecraft.world.item.ItemStack;

public class TagBasedSeparativeCapability extends CapabilityItem {
	private List<Pair<Predicate<ItemStack>, CapabilityItem>> variables;
	private CapabilityItem defaultCapability;
	
	public TagBasedSeparativeCapability(List<Pair<Predicate<ItemStack>, CapabilityItem>> variables, CapabilityItem defaultCapability) {
		super(WeaponCategories.NOT_WEAON);
		this.variables = variables;
		this.defaultCapability = defaultCapability;
	}
	
	@Override
	public CapabilityItem getResult(ItemStack item) {
		for (Pair<Predicate<ItemStack>, CapabilityItem> pair : this.variables) {
			if (pair.getFirst().test(item)) {
				return pair.getSecond().getResult(item);
			}
		}
		
		return this.defaultCapability;
	}
}