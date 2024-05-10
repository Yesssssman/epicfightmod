package yesman.epicfight.data.conditions.entity;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.data.conditions.Condition.LivingEntityCondition;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class TargetInDistance extends LivingEntityCondition {
	private double min;
	private double max;
	
	public TargetInDistance() {
	}
	
	public TargetInDistance(double min, double max) {
		this.min = min;
		this.max = max;
	}
	
	@Override
	public TargetInDistance read(CompoundTag tag) {
		if (!tag.contains("min")) {
			throw new IllegalArgumentException("TargetInDistance condition error: min distance not specified!");
		}
		
		if (!tag.contains("max")) {
			throw new IllegalArgumentException("TargetInDistance condition error: max distance not specified!");
		}
		
		this.min = tag.getDouble("min");
		this.max = tag.getDouble("max");
		
		return this;
	}
	
	@Override
	public CompoundTag serializePredicate() {
		CompoundTag tag = new CompoundTag();
		tag.putDouble("min", this.min);
		tag.putDouble("max", this.max);
		
		return tag;
	}
	
	@Override
	public boolean predicate(LivingEntityPatch<?> target) {
		double distanceSqr = target.getOriginal().distanceToSqr(target.getTarget());
		return this.min * this.min < distanceSqr && distanceSqr < this.max * this.max;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public Set<Map.Entry<String, Object>> getAcceptingParameters() {
		return ImmutableMap.of("min", (Object)"", "max", (Object)"").entrySet();
	}
}
