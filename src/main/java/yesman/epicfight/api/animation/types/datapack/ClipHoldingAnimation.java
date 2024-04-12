package yesman.epicfight.api.animation.types.datapack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.AnimationClip;

@OnlyIn(Dist.CLIENT)
public interface ClipHoldingAnimation {
	public void setAnimationClip(AnimationClip clip);
}
