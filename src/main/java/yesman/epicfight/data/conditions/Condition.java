package yesman.epicfight.data.conditions;

import java.util.Map;
import java.util.Set;

import com.google.common.base.Function;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface Condition<T> {
	public void read(CompoundTag tag);
	public CompoundTag serializePredicate();
	public boolean predicate(T target);
	
	@OnlyIn(Dist.CLIENT)
	public Set<Map.Entry<String, Object>> getAcceptingParameters();
	
	public static class ConditionBuilder<T extends Condition<?>> {
		final Function<CompoundTag, T> constructor;
		CompoundTag tag;
		
		private ConditionBuilder(Function<CompoundTag, T> constructor) {
			this.constructor = constructor;
			this.tag = new CompoundTag();
		}
		
		public ConditionBuilder<T> setTag(CompoundTag tag) {
			this.tag = tag;
			return this;
		}
		
		public CompoundTag getTag() {
			return this.tag;
		}
		
		public T build() {
			return this.constructor.apply(this.tag);
		}
		
		public static <T extends Condition<?>> ConditionBuilder<T> builder(Function<CompoundTag, T> constructor) {
			return new ConditionBuilder<>(constructor);
		}
	}
}