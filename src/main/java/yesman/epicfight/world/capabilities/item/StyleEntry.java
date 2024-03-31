package yesman.epicfight.world.capabilities.item;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;

import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class StyleEntry {
	List<Pair<Condition<LivingEntityPatch<?>>, Style>> conditions = Lists.newArrayList();
	Style elseStyle;
	
	public void putNewEntry(Condition<LivingEntityPatch<?>> condition, Style style) {
		this.conditions.add(Pair.of(condition, style));
	}
	
	public Style getStyle(LivingEntityPatch<?> entitypatch) {
		for (Pair<Condition<LivingEntityPatch<?>>, Style> entry : this.conditions) {
			if (entry.getFirst().predicate(entitypatch)) {
				return entry.getSecond();
			}
		}
		
		return this.elseStyle;
	}
}