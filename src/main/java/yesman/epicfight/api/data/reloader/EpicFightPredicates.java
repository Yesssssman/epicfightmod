package yesman.epicfight.api.data.reloader;

import java.util.Set;

import com.google.common.collect.Sets;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;

public abstract class EpicFightPredicates<T> {
	public abstract boolean test(T object);
	
	public static class HasTag extends EpicFightPredicates<Entity> {
		private Set<String> allowedTags = Sets.newHashSet();
		
		public HasTag(ListNBT allowedTags) {
			allowedTags.stream().map(INBT::getAsString).forEach(this.allowedTags::add);
		}
		
		@Override
		public boolean test(Entity object) {
			for (String tag : this.allowedTags) {
				if (object.getTags().contains(tag)) {
					return true;
				}
			}
			
			return false;
		}
	}
}