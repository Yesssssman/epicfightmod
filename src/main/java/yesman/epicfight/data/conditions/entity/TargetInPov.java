package yesman.epicfight.data.conditions.entity;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.data.conditions.Condition.LivingEntityCondition;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class TargetInPov extends LivingEntityCondition {
	protected double min;
	protected double max;
	
	public TargetInPov() {
	}
	
	public TargetInPov(double min, double max) {
		this.min = min;
		this.max = max;
	}
	
	@Override
	public TargetInPov read(CompoundTag tag) {
		if (!tag.contains("min")) {
			throw new IllegalArgumentException("TargetInPov condition error: min degree not specified!");
		}
		
		if (!tag.contains("max")) {
			throw new IllegalArgumentException("TargetInPov condition error: max degree not specified!");
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
	public boolean predicate(LivingEntityPatch<?> entitypatch) {
		double degree = entitypatch.getAngleTo(entitypatch.getTarget());
		return this.min < degree && degree < this.max;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public Set<Map.Entry<String, Object>> getAcceptingParameters() {
		return ImmutableMap.of("chance", (Object)"").entrySet();
	}
	
	public static class TargetInPovHorizontal extends TargetInPov {
		public TargetInPovHorizontal() {
		}
		
		public TargetInPovHorizontal(double min, double max) {
			super(min, max);
		}
		
		@Override
		public boolean predicate(LivingEntityPatch<?> entitypatch) {
			double degree = entitypatch.getAngleToHorizontal(entitypatch.getTarget());
			return this.min < degree && degree < this.max;
		}
	}
}
