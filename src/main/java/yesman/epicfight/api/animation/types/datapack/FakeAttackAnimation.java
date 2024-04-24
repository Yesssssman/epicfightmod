package yesman.epicfight.api.animation.types.datapack;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.AnimationClip;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.client.animation.property.ClientAnimationProperties;
import yesman.epicfight.api.client.animation.property.TrailInfo;
import yesman.epicfight.api.model.Armature;

@OnlyIn(Dist.CLIENT)
public class FakeAttackAnimation extends AttackAnimation implements ClipHoldingAnimation {
	protected AnimationClip clip;
	protected FakeAnimation fakeAnimation;
	
	/**
	public FakeAttackAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, InteractionHand hand, @Nullable Collider collider, Joint colliderJoint, String path, Armature armature) {
		super(convertTime, antic, preDelay, contact, recovery, hand, collider, colliderJoint, path, armature, true);
	}
	**/
	
	public FakeAttackAnimation(float convertTime, String path, Armature armature, List<Phase> phases) {
		super(convertTime, path, armature, true, phases.toArray(new Phase[0]));
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
	public void setAnimationClip(AnimationClip clip) {
		this.clip = clip;
	}
	
	@Override
	public AnimationClip getAnimationClip() {
		return this.clip;
	}
	
	@Override
	public FakeAnimation buildAnimation(JsonObject rawAnimationJson) {
		FakeAnimation fakeAnimation = new FakeAnimation(this.registryName.toString(), this.armature, this.clip, rawAnimationJson);
		fakeAnimation.setAnimationClass(AttackAnimation.class);
		fakeAnimation.setParameter("convertTime", this.convertTime);
		fakeAnimation.setParameter("antic", this.phases[0].antic);
		fakeAnimation.setParameter("preDelay", this.phases[0].preDelay);
		fakeAnimation.setParameter("contact", this.phases[0].contact);
		fakeAnimation.setParameter("recovery", this.phases[0].recovery);
		fakeAnimation.setParameter("hand", this.phases[0].hand);
		fakeAnimation.setParameter("collider", this.phases[0].colliders[0].getSecond());
		fakeAnimation.setParameter("colliderJoint", this.phases[0].colliders[0].getFirst());
		fakeAnimation.setParameter("path", this.registryName.toString());
		fakeAnimation.setParameter("armature", this.armature);
		
		this.getProperty(ClientAnimationProperties.TRAIL_EFFECT).ifPresent((trailInfos) -> {
			JsonArray trailArray = new JsonArray();
			
			for (TrailInfo trailInfo : trailInfos) {
				JsonObject trailObj = new JsonObject();
				trailObj.addProperty("start_time", trailInfo.startTime);
				trailObj.addProperty("end_time", trailInfo.endTime);
				trailObj.addProperty("joint", trailInfo.joint);
				trailObj.addProperty("item_skin_hand", trailInfo.hand.toString());
				trailArray.add(trailObj);
			}
			
			fakeAnimation.getPropertiesJson().add("trail_effects", trailArray);
			fakeAnimation.addProperty(ClientAnimationProperties.TRAIL_EFFECT, trailInfos);
		});
		
		this.fakeAnimation = fakeAnimation;
		
		return fakeAnimation;
	}
}
