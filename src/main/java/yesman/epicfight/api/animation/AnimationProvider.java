package yesman.epicfight.api.animation;

import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;

/**
 * Use this for the animations that should be reloaded after reloading resource
 * e.g. () -> Animations.DUMMY_ANIMATION
 */
@FunctionalInterface
public interface AnimationProvider<T extends StaticAnimation> {
	public T get();
	
	/**
	 * These interfaces are for array use
	 */
	@FunctionalInterface
	interface StaticAnimationProvider extends AnimationProvider<StaticAnimation> {
		public StaticAnimation get();
	}
	
	@FunctionalInterface
	interface AttackAnimationProvider extends AnimationProvider<AttackAnimation> {
		public AttackAnimation get();
	}
}