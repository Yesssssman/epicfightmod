package yesman.epicfight.api.animation.types;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.Keyframe;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.TransformSheet;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class HitAnimation extends MainFrameAnimation {
	public HitAnimation(float convertTime, String path, Armature armature) {
		super(convertTime, path, armature);
		
		this.stateSpectrumBlueprint.clear()
			.newTimePair(0.0F, Float.MAX_VALUE)
			.addState(EntityState.TURNING_LOCKED, true)
			.addState(EntityState.MOVEMENT_LOCKED, true)
			.addState(EntityState.UPDATE_LIVING_MOTION, false)
			.addState(EntityState.CAN_BASIC_ATTACK, false)
			.addState(EntityState.CAN_SKILL_EXECUTION, false)
			.addState(EntityState.INACTION, true)
			.addState(EntityState.HURT_LEVEL, 1);
	}
	
	@Override
	public void begin(LivingEntityPatch<?> entitypatch) {
		super.begin(entitypatch);
		entitypatch.cancelAnyAction();
	}
	
	@Override
	public void setLinkAnimation(DynamicAnimation fromAnimation, Pose pose1, boolean isOnSameLayer, float convertTimeModifier, LivingEntityPatch<?> entitypatch, LinkAnimation dest) {
		dest.getTransfroms().clear();
		dest.setTotalTime(convertTimeModifier + this.convertTime);
		dest.setConnectedAnimations(fromAnimation, this);
		
		Map<String, JointTransform> data1 = pose1.getJointTransformData();
		Map<String, JointTransform> data2 = super.getPoseByTime(entitypatch, 0.0F, 0.0F).getJointTransformData();
		Map<String, JointTransform> data3 = super.getPoseByTime(entitypatch, this.getTotalTime() - 0.00001F, 0.0F).getJointTransformData();
		
		Set<String> joint1 = new HashSet<> (data1.keySet());
		joint1.removeIf((jointName) -> !fromAnimation.isJointEnabled(entitypatch, jointName));
		Set<String> joint2 = new HashSet<> (data2.keySet());
		joint2.removeIf((jointName) -> !this.isJointEnabled(entitypatch, jointName));
		joint1.addAll(joint2);
		
		for (String jointName : joint1) {
			if (data1.containsKey(jointName) && data2.containsKey(jointName)) {
				Keyframe[] keyframes = new Keyframe[4];
				keyframes[0] = new Keyframe(0, data1.get(jointName));
				keyframes[1] = new Keyframe(this.convertTime, data2.get(jointName));
				keyframes[2] = new Keyframe(this.convertTime + 0.033F, data3.get(jointName));
				keyframes[3] = new Keyframe(convertTimeModifier + this.convertTime, data3.get(jointName));
				TransformSheet sheet = new TransformSheet(keyframes);
				dest.getAnimationClip().addJointTransform(jointName, sheet);
			}
		}
	}
	
	@Override
	public Pose getPoseByTime(LivingEntityPatch<?> entitypatch, float time, float partialTicks) {
		return super.getPoseByTime(entitypatch, this.getTotalTime() - 0.000001F, 0.0F);
	}
}