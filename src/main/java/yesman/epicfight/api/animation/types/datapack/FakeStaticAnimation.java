package yesman.epicfight.api.animation.types.datapack;

import java.util.Map;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.AnimationClip;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.animation.property.ClientAnimationProperties;
import yesman.epicfight.api.client.animation.property.JointMask.JointMaskSet;
import yesman.epicfight.api.client.animation.property.JointMaskReloadListener;
import yesman.epicfight.api.model.Armature;

@OnlyIn(Dist.CLIENT)
public class FakeStaticAnimation extends StaticAnimation implements ClipHoldingAnimation {
	protected AnimationClip clip;
	protected FakeAnimation fakeAnimation;
	
	public FakeStaticAnimation(float convertTime, boolean isRepeat, String path, Armature armature) {
		super(convertTime, isRepeat, path, armature, true);
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
	public FakeAnimation buildAnimation(JsonArray rawAnimationJson) {
		FakeAnimation fakeAnimation = new FakeAnimation(this.registryName.toString(), this.armature, this.clip, rawAnimationJson);
		fakeAnimation.setAnimationClass(FakeAnimation.AnimationType.STATIC);
		fakeAnimation.setParameter("convertTime", this.convertTime);
		fakeAnimation.setParameter("isRepeat", this.isRepeat());
		fakeAnimation.setParameter("path", this.registryName.toString());
		fakeAnimation.setParameter("armature", this.armature);
		
		final JsonObject propertiesJson = fakeAnimation.getPropertiesJson();
		
		this.getProperty(ClientAnimationProperties.MULTILAYER_ANIMATION).ifPresentOrElse((multilayer) -> {
			JsonObject multilayerJson = new JsonObject();
			JsonObject baseJson = new JsonObject();
			
			baseJson.addProperty("priority", multilayer.getPriority().toString());
			
			final JsonArray baseMasks = new JsonArray();
			
			this.getProperty(ClientAnimationProperties.JOINT_MASK).ifPresent((jointMaskEntry) -> {
				for (Map.Entry<LivingMotion, JointMaskSet> entry : jointMaskEntry.getEntries()) {
					JsonObject maskObj = new JsonObject();
					maskObj.addProperty("livingmotion", entry.getKey().toString());
					maskObj.addProperty("type", JointMaskReloadListener.getKey(entry.getValue()).toString());
					baseMasks.add(maskObj);
				}
			});
			
			baseJson.add("masks", baseMasks);
			
			JsonObject compositeJson = new JsonObject();
			
			compositeJson.addProperty("priority", this.getPriority().toString());
			
			final JsonArray compositeMasks = new JsonArray();
			
			multilayer.getProperty(ClientAnimationProperties.JOINT_MASK).ifPresent((jointMaskEntry) -> {
				for (Map.Entry<LivingMotion, JointMaskSet> entry : jointMaskEntry.getEntries()) {
					JsonObject maskObj = new JsonObject();
					maskObj.addProperty("livingmotion", entry.getKey().toString());
					maskObj.addProperty("type", JointMaskReloadListener.getKey(entry.getValue()).toString());
					compositeMasks.add(maskObj);
				}
			});
			
			baseJson.add("masks", compositeMasks);
			
			multilayerJson.add("base", baseJson);
			multilayerJson.add("composite", compositeJson);
			propertiesJson.add("multilayer", multilayerJson);
		}, () -> {
			final JsonArray masks = new JsonArray();
			
			this.getProperty(ClientAnimationProperties.JOINT_MASK).ifPresent((jointMaskEntry) -> {
				for (Map.Entry<LivingMotion, JointMaskSet> entry : jointMaskEntry.getEntries()) {
					JsonObject maskObj = new JsonObject();
					maskObj.addProperty("livingmotion", entry.getKey().toString());
					maskObj.addProperty("type", JointMaskReloadListener.getKey(entry.getValue()).toString());
					masks.add(maskObj);
				}
			});
			
			if (!masks.isEmpty()) {
				propertiesJson.add("layer", propertiesJson);
				propertiesJson.add("priority", propertiesJson);
				propertiesJson.add("masks", masks);
			}
		});
		
		this.fakeAnimation = fakeAnimation;
		
		return fakeAnimation;
	}
}
