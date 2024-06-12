package yesman.epicfight.api.animation.types.datapack;

import com.google.gson.JsonArray;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.AnimationClip;
import yesman.epicfight.api.animation.types.KnockdownAnimation;
import yesman.epicfight.api.model.Armature;

@OnlyIn(Dist.CLIENT)
public class FakeKnockdownAnimation extends KnockdownAnimation implements ClipHoldingAnimation {
	protected AnimationClip clip;
	protected FakeAnimation fakeAnimation;
	
	public FakeKnockdownAnimation(float convertTime, String path, Armature armature) {
		super(convertTime, path, armature, true);
	}
	
	@Override
	public void setAnimationClip(AnimationClip clip) {
		this.clip = clip;
	}
	
	@Override
	public AnimationClip getAnimationClip() {
		return this.clip;
	}

	@Override
	public void setCreator(FakeAnimation fakeAnimation) {
		this.fakeAnimation = fakeAnimation;
	}

	@Override
	public FakeAnimation getCreator() {
		return this.fakeAnimation;
	}

	@Override
	public FakeAnimation buildAnimation(JsonArray rawAnimationJson) {
		FakeAnimation fakeAnimation = new FakeAnimation(this.registryName.toString(), this.armature, this.clip, rawAnimationJson);
		fakeAnimation.setAnimationClass(FakeAnimation.AnimationType.KNOCK_DOWN);
		fakeAnimation.setParameter("convertTime", this.convertTime);
		fakeAnimation.setParameter("path", this.registryName.toString());
		fakeAnimation.setParameter("armature", this.armature);
		
		this.fakeAnimation = fakeAnimation;
		
		return fakeAnimation;
	}
}
