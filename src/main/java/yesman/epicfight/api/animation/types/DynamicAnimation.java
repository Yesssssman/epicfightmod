package yesman.epicfight.api.animation.types;

import java.util.Map;
import java.util.Optional;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.Keyframe;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.TransformSheet;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.EntityState.StateFactor;
import yesman.epicfight.api.client.animation.Layer;
import yesman.epicfight.api.client.animation.property.JointMask.BindModifier;
import yesman.epicfight.api.utils.TypeFlexibleHashMap;
import yesman.epicfight.config.ConfigurationIngame;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public abstract class DynamicAnimation {
	protected Map<String, TransformSheet> jointTransforms = Maps.newHashMap();
	protected final boolean isRepeat;
	protected final float convertTime;
	protected float totalTime = 0.0F;
	
	public DynamicAnimation() {
		this(ConfigurationIngame.GENERAL_ANIMATION_CONVERT_TIME, false);
	}
	
	public DynamicAnimation(float convertTime, boolean isRepeat) {
		this.isRepeat = isRepeat;
		this.convertTime = convertTime;
	}
	
	public void addSheet(String jointName, TransformSheet sheet) {
		this.jointTransforms.put(jointName, sheet);
	}
	
	public final Pose getRawPose(float time) {
		Pose pose = new Pose();
		
		for (String jointName : this.jointTransforms.keySet()) {
			pose.putJointData(jointName, this.jointTransforms.get(jointName).getInterpolatedTransform(time));
		}
		
		return pose;
	}
	
	public Pose getPoseByTime(LivingEntityPatch<?> entitypatch, float time, float partialTicks) {
		Pose pose = new Pose();
		
		for (String jointName : this.jointTransforms.keySet()) {
			pose.putJointData(jointName, this.jointTransforms.get(jointName).getInterpolatedTransform(time));
		}
		
		this.modifyPose(this, pose, entitypatch, time, partialTicks);
		
		return pose;
	}
	
	/** Modify the pose both this and link animation. **/
	public void modifyPose(DynamicAnimation animation, Pose pose, LivingEntityPatch<?> entitypatch, float time, float partialTicks) {
	}
	
	public void setLinkAnimation(Pose pose1, float convertTimeModifier, LivingEntityPatch<?> entitypatch, LinkAnimation dest) {
		if (!entitypatch.isLogicalClient()) {
			pose1 = Animations.DUMMY_ANIMATION.getPoseByTime(entitypatch, 0.0F, 1.0F);
		}
		
		float totalTime = convertTimeModifier >= 0.0F ? convertTimeModifier + this.convertTime : this.convertTime;
		boolean isNeg = convertTimeModifier < 0.0F;
		float nextStart = isNeg ? -convertTimeModifier : 0.0F;
		
		if (isNeg) {
			dest.startsAt = nextStart;
		}
		
		dest.getTransfroms().clear();
		dest.setTotalTime(totalTime);
		dest.setNextAnimation(this);
		
		Map<String, JointTransform> data1 = pose1.getJointTransformData();
		Map<String, JointTransform> data2 = this.getPoseByTime(entitypatch, nextStart, 1.0F).getJointTransformData();
		
		for (String jointName : data1.keySet()) {
			if (data1.containsKey(jointName) && data2.containsKey(jointName)) {
				Keyframe[] keyframes = new Keyframe[2];
				keyframes[0] = new Keyframe(0.0F, data1.get(jointName));
				keyframes[1] = new Keyframe(totalTime, data2.get(jointName));
				TransformSheet sheet = new TransformSheet(keyframes);
				dest.addSheet(jointName, sheet);
			}
		}
	}
	
	public void putOnPlayer(AnimationPlayer player) {
		player.setPlayAnimation(this);
	}
	
	public void begin(LivingEntityPatch<?> entitypatch) {}
	public void tick(LivingEntityPatch<?> entitypatch) {}
	public void end(LivingEntityPatch<?> entitypatch, DynamicAnimation nextAnimation, boolean isEnd) {}
	public void linkTick(LivingEntityPatch<?> entitypatch, DynamicAnimation linkAnimation) {};
	
	@OnlyIn(Dist.CLIENT)
	public boolean isJointEnabled(LivingEntityPatch<?> entitypatch, Layer.Priority layer, String joint) {
		return this.jointTransforms.containsKey(joint);
	}
	
	@OnlyIn(Dist.CLIENT)
	public BindModifier getBindModifier(LivingEntityPatch<?> entitypatch, Layer.Priority layer, String joint) {
		return null;
	}
	
	public EntityState getState(LivingEntityPatch<?> entitypatch, float time) {
		return EntityState.DEFAULT_STATE;
	}
	
	public TypeFlexibleHashMap<StateFactor<?>> getStatesMap(LivingEntityPatch<?> entitypatch, float time) {
		return new TypeFlexibleHashMap<> (false);
	}
	
	public <T> T getState(StateFactor<T> stateFactor, LivingEntityPatch<?> entitypatch, float time) {
		return stateFactor.defaultValue();
	}
	
	public Map<String, TransformSheet> getTransfroms() {
		return this.jointTransforms;
	}
	
	public float getPlaySpeed(LivingEntityPatch<?> entitypatch) {
		return 1.0F;
	}
	
	public TransformSheet getCoord() {
		return this.jointTransforms.get("Root");
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
	
	public boolean canBePlayedReverse() {
		return false;
	}
	
	public int getNamespaceId() {
		return -1;
	}
	
	public int getId() {
		return -1;
	}
	
	public <V> Optional<V> getProperty(AnimationProperty<V> propertyType) {
		return Optional.empty();
	}
	
	public boolean isBasicAttackAnimation() {
		return false;
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
	
	public boolean isClientAnimation() {
		return false;
	}
	
	public boolean isStaticAnimation() {
		return false;
	}
	
	public DynamicAnimation getThis() {
		return this;
	}
	
	@OnlyIn(Dist.CLIENT)
	public void renderDebugging(PoseStack poseStack, MultiBufferSource buffer, LivingEntityPatch<?> entitypatch, float playTime, float partialTicks) {
		
	}
}