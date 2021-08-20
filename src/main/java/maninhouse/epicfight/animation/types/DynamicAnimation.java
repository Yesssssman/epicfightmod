package maninhouse.epicfight.animation.types;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import maninhouse.epicfight.animation.AnimationPlayer;
import maninhouse.epicfight.animation.JointKeyframe;
import maninhouse.epicfight.animation.JointTransform;
import maninhouse.epicfight.animation.Pose;
import maninhouse.epicfight.animation.TransformSheet;
import maninhouse.epicfight.animation.property.Property;
import maninhouse.epicfight.capabilities.entity.LivingData;
import maninhouse.epicfight.client.animation.BindingOperation;
import maninhouse.epicfight.client.animation.BindingOption;
import maninhouse.epicfight.config.ConfigurationIngame;
import maninhouse.epicfight.gamedata.Animations;

public abstract class DynamicAnimation {
	protected Map<String, TransformSheet> jointTransforms;
	protected final boolean isRepeat;
	protected final float convertTime;
	protected float totalTime;

	public DynamicAnimation() {
		this(0.0F, ConfigurationIngame.GENERAL_ANIMATION_CONVERT_TIME, false);
	}

	public DynamicAnimation(float convertTime, boolean isRepeat) {
		this(0, convertTime, isRepeat);
	}

	public DynamicAnimation(float totalTime, float convertTime, boolean isRepeat) {
		this.jointTransforms = new HashMap<String, TransformSheet>();
		this.totalTime = totalTime;
		this.isRepeat = isRepeat;
		this.convertTime = convertTime;
	}

	public void addSheet(String jointName, TransformSheet sheet) {
		this.jointTransforms.put(jointName, sheet);
	}
	
	public Pose getPoseByTime(LivingData<?> entitydata, float time) {
		return this.getPoseByTime(this, entitydata, time);
	}
	
	protected Pose getPoseByTime(DynamicAnimation animation, LivingData<?> entitydata, float time) {
		Pose pose = new Pose();
		for (String jointName : animation.jointTransforms.keySet()) {
			if (!entitydata.isRemote() || this.isEnabledJoint(jointName)) {
				pose.putJointData(jointName, animation.jointTransforms.get(jointName).getInterpolatedTransform(time));
			}
		}
		return pose;
	}
	
	public void getLinkAnimation(Pose pose1, float timeModifier, LivingData<?> entitydata, LinkAnimation dest) {
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
		Map<String, JointTransform> data2 = this.getPoseByTime(entitydata, nextStart).getJointTransformData();
		
		for (String jointName : data1.keySet()) {
			JointKeyframe[] keyframes = new JointKeyframe[2];
			keyframes[0] = new JointKeyframe(0, data1.get(jointName));
			keyframes[1] = new JointKeyframe(totalTime, data2.get(jointName));

			TransformSheet sheet = new TransformSheet(keyframes);
			dest.addSheet(jointName, sheet);
		}
	}

	public void putOnPlayer(AnimationPlayer player) {
		player.setPlayAnimation(this);
	}
	
	public void onActivate(LivingData<?> entitydata) {}
	public void onUpdate(LivingData<?> entitydata) {}
	public void onFinish(LivingData<?> entitydata, boolean isEnd) {}
	public void updateOnLinkAnimation(LivingData<?> entitydata, LinkAnimation linkAnimation) {};
	
	public boolean isEnabledJoint(String joint) {
		return this.jointTransforms.containsKey(joint);
	}
	
	public BindingOperation getBindingOperation(String joint) {
		return BindingOption.DEFAULT;
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