package yesman.epicfight.data.conditions.entity;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.data.conditions.Condition.LivingEntityCondition;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class RandomChance extends LivingEntityCondition {
	private float chance;
	
	public RandomChance() {
		this.chance = 0.0F;
	}
	
	public RandomChance(float chance) {
		this.chance = chance;
	}
	
	@Override
	public RandomChance read(CompoundTag tag) {
		this.chance = tag.getFloat("chance");
		
		if (!tag.contains("chance")) {
			throw new IllegalArgumentException("Random condition error: chancec not specified!");
		}
		
		return this;
	}
	
	@Override
	public CompoundTag serializePredicate() {
		CompoundTag tag = new CompoundTag();
		tag.putFloat("chance", this.chance);
		
		return tag;
	}
	
	@Override
	public boolean predicate(LivingEntityPatch<?> target) {
		return target.getOriginal().getRandom().nextFloat() < this.chance;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public Set<Map.Entry<String, Object>> getAcceptingParameters() {
		return ImmutableMap.of("chance", (Object)"").entrySet();
	}
}
