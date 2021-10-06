package yesman.epicfight.animation.types;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import yesman.epicfight.animation.AnimationPlayer;
import yesman.epicfight.animation.JointTransform;
import yesman.epicfight.animation.Keyframe;
import yesman.epicfight.animation.Pose;
import yesman.epicfight.animation.TransformSheet;
import yesman.epicfight.animation.property.Property;
import yesman.epicfight.capabilities.entity.LivingData;
import yesman.epicfight.client.animation.PoseModifyingEntry;
import yesman.epicfight.client.animation.PoseModifyingFunction;
import yesman.epicfight.config.ConfigurationIngame;
import yesman.epicfight.gamedata.Animations;

public abstract class DynamicAnimation {
	protected Map<String, TransformSheet> jointTransforms;
	protected final boolean isRepeat;
	protected final float convertTime;
	protected float totalTime = 0.0F;

	public DynamicAnimation() {
		this(ConfigurationIngame.GENERAL_ANIMATION_CONVERT_TIME, false);
	}
	
	public DynamicAnimation(float convertTime, boolean isRepeat) {
		this.jointTransforms = new HashMap<String, TransformSheet>();
		this.isRepeat = isRepeat;
		this.convertTime = convertTime;
	}

	public void addSheet(String jointName, TransformSheet sheet) {
		this.jointTransforms.put(jointName, sheet);
	}
	
	public Pose getPoseByTime(LivingData<?> entitydata, float time) {
		Pose pose = new Pose();
		for (String jointName : this.jointTransforms.keySet()) {
			if (!entitydata.isRemote() || this.isEnabledJoint(entitydata, jointName)) {
				pose.putJointData(jointName, this.jointTransforms.get(jointName).getInterpolatedTransform(time));
			}
		}
		this.modifyPose(pose, entitydata, time);
		return pose;
	}
	
	protected void modifyPose(Pose pose, LivingData<?> entitydata, float time) {
		;
	}
	
	public Pose getLinkFirstPose(LivingData<?> entitydata, float nextStart) {
		return this.getPoseByTime(entitydata, nextStart);
	}
	
	public void setLinkAnimation(Pose pose1, float timeModifier, LivingData<?> entitydata, LinkAnimation dest) {
		if (!entitydata.isRemote()) {
			pose1 = Animations.DUMMY_ANIMATION.getPoseByTime(entitydata, 0.0F);
		}
		
		float totalTime = timeModifier >= 0 ? timeModifier + this.convertTime : this.convertTime;
		boolean isNeg = timeModifier < 0;
		float nextStart = isNeg ? -timeModifier : 0;
		
		if (isNeg) {
			dest.startsAt = nextStart;
		}
		
		dest.getTransfroms().clear();
		dest.setTotalTime(totalTime);
		dest.setNextAnimation(this);
		
		Map<String, JointTransform> data1 = pose1.getJointTransformData();
		Map<String, JointTransform> data2 = entitydata.getAnimator().getNextStartingPose(nextStart).getJointTransformData();
		
		for (String jointName : data1.keySet()) {
			if (data1.containsKey(jointName) && data2.containsKey(jointName)) {
				Keyframe[] keyframes = new Keyframe[2];
				keyframes[0] = new Keyframe(0, data1.get(jointName));
				keyframes[1] = new Keyframe(totalTime, data2.get(jointName));
				TransformSheet sheet = new TransformSheet(keyframes);
				dest.addSheet(jointName, sheet);
			}
		}
	}

	public void putOnPlayer(AnimationPlayer player) {
		player.setPlayAnimation(this);
	}
	
	public void onActivate(LivingData<?> entitydata) {}
	public void onUpdate(LivingData<?> entitydata) {}
	public void onFinish(LivingData<?> entitydata, boolean isEnd) {}
	public void updateOnLinkAnimation(LivingData<?> entitydata, LinkAnimation linkAnimation) {};
	
	public boolean isEnabledJoint(LivingData<?> entitydata, String joint) {
		return this.jointTransforms.containsKey(joint);
	}
	
	public PoseModifyingFunction getPoseModifyingFunction(LivingData<?> entitydata, String joint) {
		return PoseModifyingEntry.NONE;
	}
	
	public EntityState getState(float time) {
		return EntityState.FREE;
	}
	
	public Map<String, TransformSheet> getTransfroms() {
		return this.jointTransforms;
	}
	
	public float getPlaySpeed(LivingData<?> entitydata) {
		return 1.0F;
	}
	
	public DynamicAnimation getRealAnimation() {
		return this;
	}
	
	public void setTotalTime(float totalTime) {
		this.totalTime = totalTime;
	}
	
	public float getTotalTime() {
		return this.totalTime - 0.001F;
	}
	
	public float getConvertTime() {
		return this.convertTime;
	}
	
	public boolean isRepeat() {
		return this.isRepeat;
	}
	
	public int getNamespaceId() {
		return -1;
	}
	
	public int getId() {
		return -1;
	}
	
	public <V> Optional<V> getProperty(Property<V> propertyType) {
		return Optional.empty();
	}
	
	public boolean isMainFrameAnimation() {
		return false;
	}
	
	public boolean isReboundAnimation() {
		return false;
	}
	
	public boolean isMetaAnimation() {
		return false;
	}
}