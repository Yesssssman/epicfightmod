package yesman.epicfight.data.conditions;

import java.util.Map;
import java.util.Set;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public interface Condition<T> {
	public Condition<T> read(CompoundTag tag);
	public CompoundTag serializePredicate();
	public boolean predicate(T target);
	
	@OnlyIn(Dist.CLIENT)
	public Set<Map.Entry<String, Object>> getAcceptingParameters();
	
	public static abstract class LivingEntityCondition implements Condition<LivingEntityPatch<?>> {
	}
	
	public static abstract class ItemStackCondition implements Condition<ItemStack> {
	}
}