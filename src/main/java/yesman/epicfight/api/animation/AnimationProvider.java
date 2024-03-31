package yesman.epicfight.api.animation;

import net.minecraft.resources.ResourceLocation;
import yesman.epicfight.api.animation.types.StaticAnimation;

/**
 * Use this for the animations that should be automatically refreshed after reloading resource
 * e.g. AnimationProvider.of(Animations.DUMMY_ANIMATION)
 */
@FunctionalInterface
public interface AnimationProvider<T extends StaticAnimation> {
	public T get();
	
	@SuppressWarnings("unchecked")
	public static <T extends StaticAnimation> AnimationProvider<T> of(ResourceLocation rl) {
		return () -> (T)AnimationManager.getInstance().byKeyOrThrow(rl);
	}
}