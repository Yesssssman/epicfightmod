package yesman.epicfight.api.animation;

import yesman.epicfight.api.animation.types.AttackAnimation;

/**
 * These interfaces are for array use
 */
@FunctionalInterface
public interface AttackAnimationProvider extends AnimationProvider<AttackAnimation> {
	public AttackAnimation get();
}