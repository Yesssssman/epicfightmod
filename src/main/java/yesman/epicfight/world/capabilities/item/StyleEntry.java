package yesman.epicfight.world.capabilities.item;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;

import yesman.epicfight.data.conditions.entity.LivingEntityCondition;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class StyleEntry {
	List<Pair<LivingEntityCondition, Style>> conditions = Lists.newArrayList();
	Style elseStyle;
	
	public void putNewEntry(LivingEntityCondition condition, Style style) {
		this.conditions.add(Pair.of(condition, style));
	}
	
	public Style getStyle(LivingEntityPatch<?> entitypatch) {
		for (Pair<LivingEntityCondition, Style> entry : this.conditions) {
			if (entry.getFirst().predicate(entitypatch)) {
				return entry.getSecond();
			}
		}
		
		return this.elseStyle;
	}
}