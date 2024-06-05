package yesman.epicfight.data.conditions.entity;

import java.util.List;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.data.conditions.Condition.MobPatchCondition;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;

public class TargetInEyeHeight extends MobPatchCondition {
	@Override
	public TargetInEyeHeight read(CompoundTag tag) {
		return this;
	}
	
	@Override
	public CompoundTag serializePredicate() {
		return new CompoundTag();
	}
	
	@Override
	public boolean predicate(MobPatch<?> target) {
		double veticalDistance = Math.abs(target.getOriginal().getY() - target.getTarget().getY());
		return veticalDistance < target.getOriginal().getEyeHeight();
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public List<ParameterEditor> getAcceptingParameters(Screen screen) {
		return List.of();
	}
}
