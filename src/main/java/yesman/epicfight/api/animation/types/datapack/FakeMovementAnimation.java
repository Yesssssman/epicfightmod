package yesman.epicfight.api.animation.types.datapack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.AnimationClip;
import yesman.epicfight.api.animation.types.MovementAnimation;
import yesman.epicfight.api.model.Armature;

@OnlyIn(Dist.CLIENT)
public class FakeMovementAnimation extends MovementAnimation implements ClipHoldingAnimation {
	private AnimationClip clip;
	
	public FakeMovementAnimation(float convertTime, boolean isRepeat, String path, Armature armature) {
		super(convertTime, isRepeat, path, armature);
	}
	
	@Override
	public void setAnimationClip(AnimationClip clip) {
		this.clip = clip;
	}
	
	@Override
	public AnimationClip getAnimationClip() {
		return this.clip;
	}
}
