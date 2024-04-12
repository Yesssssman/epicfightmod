package yesman.epicfight.api.animation.types.datapack;

import javax.annotation.Nullable;

import net.minecraft.world.InteractionHand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.AnimationClip;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Armature;

@OnlyIn(Dist.CLIENT)
public class FakeAttackAnimation extends AttackAnimation implements ClipHoldingAnimation {
	private AnimationClip clip;
	
	public FakeAttackAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, InteractionHand hand, @Nullable Collider collider, Joint colliderJoint, String path, Armature armature) {
		super(convertTime, antic, preDelay, contact, recovery, hand, collider, colliderJoint, path, armature);
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
