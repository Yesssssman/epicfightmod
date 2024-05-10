package yesman.epicfight.data.conditions.entity;

import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.data.conditions.Condition.LivingEntityCondition;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class TargetInEyeHeight extends LivingEntityCondition {
	@Override
	public TargetInEyeHeight read(CompoundTag tag) {
		return this;
	}
	
	@Override
	public CompoundTag serializePredicate() {
		return new CompoundTag();
	}
	
	@Override
	public boolean predicate(LivingEntityPatch<?> target) {
		double veticalDistance = Math.abs(target.getOriginal().getY() - target.getTarget().getY());
		return veticalDistance < target.getOriginal().getEyeHeight();
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public Set<Map.Entry<String, Object>> getAcceptingParameters() {
		return ImmutableMap.<String, Object>of().entrySet();
	}
}
