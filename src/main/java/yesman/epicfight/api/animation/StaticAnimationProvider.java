package yesman.epicfight.api.animation;

import yesman.epicfight.api.animation.types.StaticAnimation;

/**
 * This interface is for array use
 */
@FunctionalInterface
public interface StaticAnimationProvider extends AnimationProvider<StaticAnimation> {
	public StaticAnimation get();
}