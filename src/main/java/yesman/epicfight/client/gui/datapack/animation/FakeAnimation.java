package yesman.epicfight.client.gui.datapack.animation;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.AnimationClip;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.model.Armature;

@OnlyIn(Dist.CLIENT)
public class FakeAnimation extends StaticAnimation {
	private Class<?> animationClass;
	private String constructorParameters;
	private AnimationClip animationClip;
	
	public FakeAnimation(Armature armature, AnimationClip clip) {
		super(new ResourceLocation(""), 0.0F, false, "", armature, true);
		
		this.animationClip = clip;
	}
	
	public void setConstructorParameter(String constructorParameters) {
		this.constructorParameters = constructorParameters;
	}
	
	public Class<?> getAnimationClass() {
		return this.animationClass;
	}
	
	public void setAnimationClass(Class<?> animationClass) {
		this.animationClass = animationClass;
	}
	
	@Override
	public AnimationClip getAnimationClip() {
		return this.animationClip;
	}
}