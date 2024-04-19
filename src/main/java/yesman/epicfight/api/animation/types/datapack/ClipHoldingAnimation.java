package yesman.epicfight.api.animation.types.datapack;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.AnimationClip;
import yesman.epicfight.api.animation.types.StaticAnimation;

@OnlyIn(Dist.CLIENT)
public interface ClipHoldingAnimation {
	public void setAnimationClip(AnimationClip clip);
	
	public FakeAnimation toFakeAnimation(CompoundTag rawAnimationData);
	
	default StaticAnimation toStaticAnimation() {
		return (StaticAnimation)this;
	}
}
