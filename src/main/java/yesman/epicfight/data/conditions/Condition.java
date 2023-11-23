package yesman.epicfight.data.conditions;

import com.google.common.base.Function;

import net.minecraft.nbt.CompoundTag;

public interface Condition<T> {
	public abstract void read(CompoundTag tag);
	
	public abstract CompoundTag serializePredicate();
	
	public abstract boolean predicate(T target);
	
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