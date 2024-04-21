package yesman.epicfight.api.animation.types.datapack;

import com.google.gson.JsonObject;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.AnimationClip;
import yesman.epicfight.api.animation.types.StaticAnimation;

@OnlyIn(Dist.CLIENT)
public interface ClipHoldingAnimation {
	public void setAnimationClip(AnimationClip clip);
	
	public FakeAnimation toFakeAnimation(JsonObject rawAnimationJson);
	
	default StaticAnimation cast() {
		return (StaticAnimation)this;
	}
}