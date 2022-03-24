package yesman.epicfight.world.capabilities.item;

import java.util.List;
import java.util.function.Predicate;

import com.mojang.datafixers.util.Pair;

import net.minecraft.world.item.ItemStack;

public class NBTSeparativeCapability extends CapabilityItem {
	private List<Pair<Predicate<ItemStack>, CapabilityItem>> variables;
	private CapabilityItem defaultCapability;
	
	public NBTSeparativeCapability(List<Pair<Predicate<ItemStack>, CapabilityItem>> variables, CapabilityItem defaultCapability) {
		super(WeaponCategory.NOT_WEAON);
		this.variables = variables;
		this.defaultCapability = defaultCapability;
	}
	
	@Override
	public CapabilityItem getFinal(ItemStack item) {
		for (Pair<Predicate<ItemStack>, CapabilityItem> pair : this.variables) {
			if (pair.getFirst().test(item)) {
				return pair.getSecond().getFinal(item);
			}
		}
		return this.defaultCapability;
	}
}