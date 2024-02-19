package yesman.epicfight.data.conditions.entity;

import net.minecraft.nbt.CompoundTag;
import yesman.epicfight.data.conditions.Condition;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public abstract class LivingEntityCondition implements Condition<LivingEntityPatch<?>> {
	public LivingEntityCondition(CompoundTag tag) {
		this.read(tag);
	}
}