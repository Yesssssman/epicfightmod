package yesman.epicfight.api.animation.types.datapack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.types.MovementAnimation;
import yesman.epicfight.api.model.Armature;

@OnlyIn(Dist.CLIENT)
public class FakeMovementAnimation extends FakeStaticAnimation {
	public FakeMovementAnimation(float convertTime, boolean isRepeat, String path, Armature armature) {
		super(convertTime, isRepeat, path, armature);
	}
	
	@Override
	public FakeAnimation toFakeAnimation() {
		FakeAnimation fakeAnimation = new FakeAnimation(this.registryName.toString(), this.armature, this.clip);
		fakeAnimation.setAnimationClass(MovementAnimation.class);
		fakeAnimation.setParameter("convertTime", this.convertTime);
		fakeAnimation.setParameter("isRepeat", this.isRepeat());
		fakeAnimation.setParameter("path", this.registryName.toString());
		fakeAnimation.setParameter("armature", this.armature);
		
		return fakeAnimation;
	}
}
