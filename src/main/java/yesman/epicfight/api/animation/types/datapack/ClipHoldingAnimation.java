package yesman.epicfight.api.animation.types.datapack;

import com.google.gson.JsonArray;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.AnimationClip;
import yesman.epicfight.api.animation.types.StaticAnimation;

@OnlyIn(Dist.CLIENT)
public interface ClipHoldingAnimation {
	public void setAnimationClip(AnimationClip clip);
	public void setCreator(FakeAnimation fakeAnimation);
	public FakeAnimation getCreator();
	public FakeAnimation buildAnimation(JsonArray rawAnimationJson);
	
	default StaticAnimation cast() {
		return (StaticAnimation)this;
	}
}