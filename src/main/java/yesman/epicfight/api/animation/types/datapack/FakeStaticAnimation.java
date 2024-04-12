package yesman.epicfight.api.animation.types.datapack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.AnimationClip;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.model.Armature;

@OnlyIn(Dist.CLIENT)
public class FakeStaticAnimation extends StaticAnimation implements ClipHoldingAnimation {
	private AnimationClip clip;
	
	public FakeStaticAnimation(float convertTime, boolean isRepeat, String path, Armature armature) {
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
