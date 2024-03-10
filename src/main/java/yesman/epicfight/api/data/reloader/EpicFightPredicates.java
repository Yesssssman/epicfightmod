package yesman.epicfight.api.data.reloader;

import java.util.Set;
import java.util.stream.Collectors;

import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;

public abstract class EpicFightPredicates<T> {
	public abstract boolean test(T object);
	
	public static class HasTag extends EpicFightPredicates<Entity> {
		private final Set<String> allowedTags;
		
		public HasTag(ListTag allowedTags) {
			this.allowedTags = allowedTags.stream().map(Tag::getAsString).collect(Collectors.toUnmodifiableSet());
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