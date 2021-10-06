package yesman.epicfight.client.animation;

import java.util.Map;

import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;

import yesman.epicfight.animation.AnimationPlayer;
import yesman.epicfight.animation.Animator;
import yesman.epicfight.animation.Joint;
import yesman.epicfight.animation.JointTransform;
import yesman.epicfight.animation.LivingMotion;
import yesman.epicfight.animation.Pose;
import yesman.epicfight.animation.types.DynamicAnimation;
import yesman.epicfight.animation.types.EntityState;
import yesman.epicfight.animation.types.StaticAnimation;
import yesman.epicfight.capabilities.entity.LivingData;
import yesman.epicfight.client.model.ClientModels;
import yesman.epicfight.gamedata.Animations;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.utils.math.OpenMatrix4f;

public class AnimatorClient extends Animator {
	private final Map<LivingMotion, StaticAnimation> livingAnimations;
	private final Map<LivingMotion, StaticAnimation> overwritingLivingAnimations;
	private final Map<LivingMotion, StaticAnimation> defaultAnimations;
	public final Map<Layer.Priority, Layer> layers;
	private LivingMotion currentMotion;
	private LivingMotion currentOverwritingMotion;
	private Layer livingMotionLayer;
	private Layer overwritingMotionLayer;
	
	public AnimatorClient(LivingData<?> entitydata) {
		this.entitydata = entitydata;
		this.currentMotion = LivingMotion.IDLE;
		this.currentOverwritingMotion = LivingMotion.IDLE;
		this.livingAnimations = Maps.<LivingMotion, StaticAnimation>newHashMap();
		this.overwritingLivingAnimations = Maps.<LivingMotion, StaticAnimation>newHashMap();
		this.defaultAnimations = Maps.<LivingMotion, StaticAnimation>newHashMap();
		this.layers = Maps.<Layer.Priority, Layer>newLinkedHashMap();
		this.layers.put(Layer.Priority.HIGHEST, new Layer(Layer.Priority.HIGHEST));
		this.layers.put(Layer.Priority.MIDDLE, new Layer(Layer.Priority.MIDDLE));
		this.layers.put(Layer.Priority.LOWEST, new Layer(Layer.Priority.LOWEST));
		this.livingMotionLayer = this.getLayer(Layer.Priority.LOWEST);
		this.overwritingMotionLayer = this.getLayer(Layer.Priority.MIDDLE);
	}
	
	/** Play an animation by animation id **/
	@Override
	public void playAnimation(int namespaceId, int id, float modifyTime) {
		this.playAnimation(EpicFightMod.getInstance().animationManager.findAnimation(namespaceId, id), modifyTime);
	}
	
	/** Play an animation by animation instance **/
	@Override
	public void playAnimation(StaticAnimation nextAnimation, float modifyTime) {
		Layer layer = this.getLayer(nextAnimation.getPriority());
		layer.paused  = false;
		layer.playAnimation(nextAnimation, this.entitydata, modifyTime);
	}
	
	@Override
	public void reserveAnimation(StaticAnimation nextAnimation) {
		
	}
	
	/** Add new Living animation of entity **/
	public void addLivingAnimation(LivingMotion motion, StaticAnimation animation) {
		this.livingAnimations.put(motion, animation);
	}
	
	public void addOverwritingLivingMotion(LivingMotion motion, StaticAnimation animation) {
		if (animation != null) {
			this.overwritingLivingAnimations.put(motion, animation);
			if (motion == this.currentMotion) {
				EntityState state = this.getEntityState();
				if (!state.isInaction()) {
					this.playAnimation(animation, 0.0F);
				}
			}
		}
	}
	
	public void addDefaultLivingMotion(LivingMotion motion, StaticAnimation animation) {
		this.defaultAnimations.put(motion, animation);
	}
	
	public void resetOverwritingMotionMap() {
		this.overwritingLivingAnimations.clear();
	}
	
	public void playInitialLivingMotion() {
		StaticAnimation idleMotion = this.livingAnimations.get(this.currentMotion);
		this.getLayer(idleMotion.getPriority()).playAnimation(idleMotion, this.entitydata);
	}
	
	public StaticAnimation getLivingMotion(LivingMotion motion) {
		return this.livingAnimations.getOrDefault(motion, this.defaultAnimations.getOrDefault(motion, Animations.DUMMY_ANIMATION));
	}
	
	public StaticAnimation getOverwritingLivingMotion(LivingMotion motion) {
		return this.overwritingLivingAnimations.getOrDefault(motion, this.defaultAnimations.getOrDefault(motion, Animations.DUMMY_ANIMATION));
	}
	
	public void playLivingLoopMotion(LivingMotion motion) {
		StaticAnimation animation = this.getLivingMotion(motion);
		Layer layer = this.layers.get(animation.getPriority());
		layer.playAnimation(animation, this.entitydata, 0.0F);
		this.livingMotionLayer = layer;
	}
	
	public Runnable livingLoopTask() {
		if (this.livingAnimations.containsKey(this.entitydata.currentMotion)) {
			StaticAnimation animation = this.getLivingMotion(this.entitydata.currentMotion);
			Layer layer = this.layers.get(animation.getPriority());
			layer.load(animation, this.entitydata, 0.0F);
			
			return () -> {
				if (this.livingMotionLayer != null) {
					this.livingMotionLayer.off(this.entitydata);
				}
				layer.play(animation, entitydata, 0);
				this.livingMotionLayer = layer;
			};
		}
		
		return () -> {
			if (this.livingMotionLayer != null) {
				this.livingMotionLayer.off(this.entitydata);
			}
		};
	}
	
	public Runnable overwritingLoopTask() {
		if (this.livingMotionLayer.priority != Layer.Priority.MIDDLE) {
			if (this.overwritingLivingAnimations.containsKey(this.entitydata.currentOverwritingMotion) || this.defaultAnimations.containsKey(this.entitydata.currentOverwritingMotion)) {
				StaticAnimation animation = this.getOverwritingLivingMotion(this.entitydata.currentOverwritingMotion);
				Layer layer = this.layers.get(animation.getPriority());
				layer.load(animation, this.entitydata, 0.0F);
				this.overwritingMotionLayer = layer;
				return () -> {
					layer.play(animation, this.entitydata, 0.0F);
				};
			} else {
				return () -> {
					this.overwritingMotionLayer.off(this.entitydata);
				};
			}
		}
		return () -> {};
	}
	
	public void setPoseToModel(float partialTicks) {
		Joint rootJoint = this.entitydata.getEntityModel(ClientModels.LOGICAL_CLIENT).getArmature().getJointHierarcy();
		this.applyPoseToJoint(rootJoint, new OpenMatrix4f(), Maps.<Layer.Priority, Pose>newHashMap(), partialTicks);
	}
	
	public void applyPoseToJoint(Joint joint, OpenMatrix4f parentTransform, Map<Layer.Priority, Pose> poses, float partialTicks) {
		Pose pose = this.getComposedLayerPose(partialTicks);
		OpenMatrix4f transform = pose.getTransformByName(joint.getName()).toMatrix();
		OpenMatrix4f.mul(joint.getLocalTrasnform(), transform, transform);
		OpenMatrix4f.pushOrMul(JointTransform.PARENT, "parent_local", parentTransform, null, parentTransform, null, transform);
		OpenMatrix4f.mul(transform, joint.getAnimatedTransform(), transform);
		OpenMatrix4f result = transform.getResult();
		joint.setAnimatedTransform(result);
		
		for (Joint joints : joint.getSubJoints()) {
			this.applyPoseToJoint(joints, result, poses, partialTicks);
		}
	}
	
	@Override
	public void update() {
		for (Map.Entry<Layer.Priority, Layer> entry : this.layers.entrySet()) {
			Layer layer = entry.getValue();
			layer.update(this.entitydata);
			//System.out.println(entry.getKey() +" : "+ layer.animationPlayer.getPlay());
			if (entry.getKey() == this.livingMotionLayer.priority && layer.animationPlayer.isEnd() && layer.nextAnimation == null && this.currentMotion != LivingMotion.DEATH) {
				this.entitydata.updateMotion(false);
				this.playLivingLoopMotion(this.entitydata.currentMotion);
			}
		}
		
		Runnable livingMotion = () -> {};
		Runnable overwritingMotion = () -> {};
		
		if (!this.compareOverwritingMotion(this.entitydata.currentOverwritingMotion)) {
			overwritingMotion = this.overwritingLoopTask();
		}
		
		if (!this.compareMotion(this.entitydata.currentMotion)) {
			if (this.entitydata.getEntityState() == EntityState.CANCELABLE_POST_DELAY) {
				this.getLayer(Layer.Priority.HIGHEST).off(this.entitydata);
			}
			livingMotion = this.livingLoopTask();
		}
		
		overwritingMotion.run();
		livingMotion.run();
		this.currentMotion = this.entitydata.currentMotion;
		this.currentOverwritingMotion = this.entitydata.currentOverwritingMotion;
	}
	
	@Override
	public void playDeathAnimation() {
		this.playAnimation(this.livingAnimations.get(LivingMotion.DEATH), 0);
		this.currentMotion = LivingMotion.DEATH;
	}
	
	public StaticAnimation getJumpAnimation() {
		return this.livingAnimations.get(LivingMotion.JUMP);
	}
	
	public Pose getLayerPose(Layer layer, float partialTicks) {
		return layer.animationPlayer.getCurrentPose(this.entitydata, layer.paused ? 1.0F : partialTicks);
	}
	
	public Pose getComposedLayerPose(float partialTicks) {
		Pose composedPose = new Pose();
		Map<Layer.Priority, Pair<DynamicAnimation, Pose>> layerPoses = Maps.newLinkedHashMap();
		for (Layer layer : this.layers.values()) {
			Pose currentPose = this.getLayerPose(layer, partialTicks);
			layerPoses.put(layer.priority, Pair.of(layer.animationPlayer.getPlay(), currentPose));
			for (Map.Entry<String, JointTransform> transformEntry : currentPose.getJointTransformData().entrySet()) {
				composedPose.getJointTransformData().computeIfAbsent(transformEntry.getKey(), (key) -> transformEntry.getValue());
			}
		}
		Joint rootJoint = this.entitydata.getEntityModel(ClientModels.LOGICAL_CLIENT).getArmature().getJointHierarcy();
		this.modifyPoseTransform(composedPose, rootJoint, new OpenMatrix4f(), layerPoses);
		return composedPose;
	}
	
	public Pose getComposedLayerPoseLimit(Layer.Priority priority, float partialTicks) {
		Pose composedPose = new Pose();
		Map<Layer.Priority, Pair<DynamicAnimation, Pose>> layerPoses = Maps.newLinkedHashMap();
		for (Map.Entry<Layer.Priority, Layer> entry : this.layers.entrySet()) {
			if (priority.compareTo(entry.getKey()) >= 0) {
				Pose currentPose = this.getLayerPose(entry.getValue(), partialTicks);
				layerPoses.put(entry.getValue().priority, Pair.of(entry.getValue().animationPlayer.getPlay(), currentPose));
				for (Map.Entry<String, JointTransform> transformEntry  : currentPose.getJointTransformData().entrySet()) {
					composedPose.getJointTransformData().computeIfAbsent(transformEntry.getKey(), (key) -> transformEntry.getValue());
				}
			}
		}
		Joint rootJoint = this.entitydata.getEntityModel(ClientModels.LOGICAL_CLIENT).getArmature().getJointHierarcy();
		this.modifyPoseTransform(composedPose, rootJoint, new OpenMatrix4f(), layerPoses);
		return composedPose;
	}
	
	public Pose getNextStartingPose(float startAt) {
		Pose composedPose = new Pose();
		Map<Layer.Priority, Pair<DynamicAnimation, Pose>> layerPoses = Maps.newLinkedHashMap();
		for (Layer layer : this.layers.values()) {
			DynamicAnimation nextAnimation = layer.animationPlayer.getPlay().getRealAnimation();
			Pose currentPose = nextAnimation.getLinkFirstPose(this.entitydata, nextAnimation.getTotalTime() > startAt ? startAt : 0.0F);
			layerPoses.put(layer.priority, Pair.of(nextAnimation.getRealAnimation(), currentPose));
			for (Map.Entry<String, JointTransform> transformEntry : currentPose.getJointTransformData().entrySet()) {
				composedPose.getJointTransformData().computeIfAbsent(transformEntry.getKey(), (key) -> transformEntry.getValue());
			}
		}
		Joint rootJoint = this.entitydata.getEntityModel(ClientModels.LOGICAL_CLIENT).getArmature().getJointHierarcy();
		this.modifyPoseTransform(composedPose, rootJoint, new OpenMatrix4f(), layerPoses);
		return composedPose;
	}
	
	public void modifyPoseTransform(Pose result, Joint joint, OpenMatrix4f parentTransform, Map<Layer.Priority, Pair<DynamicAnimation, Pose>> poses) {
		for (Layer.Priority priority : poses.keySet()) {
			DynamicAnimation nowPlaying = poses.get(priority).getFirst();
			if (nowPlaying.isEnabledJoint(this.entitydata, joint.getName())) {
				PoseModifyingFunction function = nowPlaying.getPoseModifyingFunction(this.entitydata, joint.getName());
				function.modify(this, result, priority, joint, parentTransform, poses);
				break;
			}
		}
		for (Joint subJoints : joint.getSubJoints()) {
			this.modifyPoseTransform(result, subJoints, parentTransform, poses);
		}
	}
	
	public boolean compareMotion(LivingMotion motion) {
		boolean flag = this.currentMotion == motion || (this.currentMotion == LivingMotion.INACTION && motion == LivingMotion.IDLE);
		if (flag) {
			this.currentMotion = motion;
		}
		return flag;
	}

	public boolean compareOverwritingMotion(LivingMotion motion) {
		return this.currentOverwritingMotion == motion;
	}
	
	public void switchToInaction() {
		this.playLivingLoopMotion(LivingMotion.IDLE);
		this.currentMotion = LivingMotion.INACTION;
		this.entitydata.currentMotion = LivingMotion.INACTION;
	}
	
	public void resetOverwritingMotion() {
		this.currentOverwritingMotion = LivingMotion.NONE;
		this.entitydata.currentOverwritingMotion = LivingMotion.NONE;
	}
	
	public boolean prevAiming() {
		return this.currentOverwritingMotion == LivingMotion.AIM;
	}
	
	public void playReboundAnimation() {
		if (this.overwritingLivingAnimations.containsKey(LivingMotion.SHOT)) {
			this.playAnimation(this.overwritingLivingAnimations.get(LivingMotion.SHOT), 0.0F);
			this.entitydata.currentOverwritingMotion = LivingMotion.NONE;
			this.resetOverwritingMotion();
		}
	}
	
	@Override
	public AnimationPlayer getPlayerFor(DynamicAnimation playingAnimation) {
		if (playingAnimation instanceof StaticAnimation) {
			return this.layers.get(((StaticAnimation)playingAnimation).getPriority()).animationPlayer;
		} else {
			for (Layer layer : this.layers.values()) {
				if (layer.animationPlayer.getPlay().equals(playingAnimation)) {
					return layer.animationPlayer;
				}
			}
		}
		return null;
	}
	
	public Layer getLayer(Layer.Priority priority) {
		return this.layers.get(priority);
	}
	
	public Layer getMainFrameLayer() {
		for (Layer layer : this.layers.values()) {
			if (layer.animationPlayer.getPlay().isMainFrameAnimation()) {
				return layer;
			}
		}
		return this.layers.get(Layer.Priority.LOWEST);
	}
	
	public Layer getLivingLayer() {
		return this.livingMotionLayer;
	}
	
	public LivingData<?> getOwner() {
		return this.entitydata;
	}
	
	public LivingMotion getLivingMotionFor(Layer.Priority priority) {
		if (this.livingMotionLayer.priority == priority) {
			return this.currentMotion;
		} else if (this.overwritingMotionLayer.priority == priority) {
			return this.currentOverwritingMotion;
		}
		return this.currentMotion;
	}
	
	@Override
	public EntityState getEntityState() {
		AnimationPlayer player = this.getMainFrameLayer().animationPlayer;
		return player.getPlay().getState(player.getElapsedTime());
	}
}