package yesman.epicfight.api.animation;

import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;

/**
 * Use this for the animations that should be reloaded after reloading resource
 * e.g. () -> Animations.DUMMY_ANIMATION
 */
@FunctionalInterface
public interface AnimationProvider {
	public StaticAnimation get();
	
	@FunctionalInterface
	public interface AttackAnimationProvider {
		public AttackAnimation get();
	}
}