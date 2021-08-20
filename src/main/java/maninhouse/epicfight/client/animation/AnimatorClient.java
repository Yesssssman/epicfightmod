package maninhouse.epicfight.client.animation;

import java.util.Map;

import com.google.common.collect.Maps;

import maninhouse.epicfight.animation.AnimationPlayer;
import maninhouse.epicfight.animation.Animator;
import maninhouse.epicfight.animation.Joint;
import maninhouse.epicfight.animation.JointTransform;
import maninhouse.epicfight.animation.LivingMotion;
import maninhouse.epicfight.animation.Pose;
import maninhouse.epicfight.animation.types.DynamicAnimation;
import maninhouse.epicfight.animation.types.EntityState;
import maninhouse.epicfight.animation.types.StaticAnimation;
import maninhouse.epicfight.capabilities.entity.LivingData;
import maninhouse.epicfight.client.model.ClientModels;
import maninhouse.epicfight.gamedata.Animations;
import maninhouse.epicfight.utils.math.OpenMatrix4f;

public class AnimatorClient extends Animator {
	private final Map<LivingMotion, StaticAnimation> livingAnimations;
	private final Map<LivingMotion, StaticAnimation> overridenLivingAnimations;
	public final Map<Layer.Priority, Layer> layers;
	private LivingMotion currentMotion;
	private LivingMotion currentOverridenMotion;
	private Layer livingMotionLayer;
	private Layer overridenMotionLayer;
	
	public AnimatorClient(LivingData<?> entitydata) {
		this.entitydata = entitydata;
		this.currentMotion = LivingMotion.IDLE;
		this.currentOverridenMotion = LivingMotion.IDLE;
		this.livingAnimations = Maps.<LivingMotion, StaticAnimation>newHashMap();
		this.overridenLivingAnimations = Maps.<LivingMotion, StaticAnimation>newHashMap();
		this.layers = Maps.<Layer.Priority, Layer>newLinkedHashMap();
		this.layers.put(Layer.Priority.HIGHEST, new Layer(Layer.Priority.HIGHEST));
		this.layers.put(Layer.Priority.MIDDLE, new Layer(Layer.Priority.MIDDLE));
		this.layers.put(Layer.Priority.LOWEST, new Layer(Layer.Priority.LOWEST));
		this.livingMotionLayer = this.getLayer(Layer.Priority.LOWEST);
		this.overridenMotionLayer = this.getLayer(Layer.Priority.MIDDLE);
	}
	
	/** Play an animation by animation id **/
	@Override
	public void playAnimation(int id, float modifyTime) {
		this.playAnimation(Animations.findAnimationDataById(id), modifyTime);
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
	
	public void addOverridenLivingMotion(LivingMotion motion, StaticAnimation animation) {
		if (animation != null) {
			this.overridenLivingAnimations.put(motion, animation);
			if (motion == this.currentMotion) {
				EntityState state = this.getEntityState();
				if (!state.isInaction()) {
					this.playAnimation(animation, 0.0F);
				}
			}
		}
	}
	
	public void clearOverridenMotions() {
		this.overridenLivingAnimations.clear();
	}
	
	public void playMotion(LivingMotion motion, boolean livingMotion) {
		StaticAnimation animation = livingMotion ? this.livingAnimations.getOrDefault(motion, Animations.DUMMY_ANIMATION) :
			this.overridenLivingAnimations.getOrDefault(motion, Animations.DUMMY_ANIMATION);
		Layer layer = this.layers.get(animation.getPriority());
		layer.paused  = false;
		layer.playAnimation(animation, this.entitydata, 0.0F);
		
		if (livingMotion) {
			this.livingMotionLayer = layer;
		} else {
			this.overridenMotionLayer = layer;
		}
	}
	
	public void playLoopMotion() {
		if (this.livingMotionLayer != null) {
			this.livingMotionLayer.off(this.entitydata);
		}
		
		this.currentMotion = this.entitydata.currentMotion;
		if (this.livingAnimations.containsKey(this.entitydata.currentMotion)) {
			this.playMotion(this.currentMotion, true);
		}
	}
	
	public void playOverridenLoopMotion() {
		this.currentOverridenMotion = this.entitydata.currentOverridenMotion;
		if (this.overridenLivingAnimations.containsKey(this.entitydata.currentOverridenMotion)) {
			this.playMotion(this.currentOverridenMotion, false);
		} else {
			this.overridenMotionLayer.off(this.entitydata);
		}
	}
	
	public void setPoseToModel(float partialTicks) {
		Joint rootJoint = this.entitydata.getEntityModel(ClientModels.LOGICAL_CLIENT).getArmature().getJointHierarcy();
		this.applyPoseToJoint(rootJoint, new OpenMatrix4f(), Maps.<Layer.Priority, Pose>newHashMap(), partialTicks);
	}
	
	public void applyPoseToJoint(Joint joint, OpenMatrix4f parentTransform, Map<Layer.Priority, Pose> poses, float partialTicks) {
		for (Map.Entry<Layer.Priority, Layer> entry : this.layers.entrySet()) {
			if (entry.getValue().animationPlayer.getPlay().isEnabledJoint(joint.getName())) {
				entry.getValue().animationPlayer.getPlay().getBindingOperation(joint.getName()).bind(this, entry.getKey(), joint, parentTransform, poses, partialTicks);
				break;
			}
		}
	}
	
	@Override
	public void update() {
		for (Map.Entry<Layer.Priority, Layer> entry : this.layers.entrySet()) {
			Layer layer = entry.getValue();
			layer.update(this.entitydata);
			if (entry.getKey() == this.livingMotionLayer.priority && layer.animationPlayer.isEnd() && layer.nextAnimation == null && this.currentMotion != LivingMotion.DEATH) {
				this.entitydata.updateMotion();
				this.playLoopMotion();
			}
		}
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
		for (Layer layer : this.layers.values()) {
			Pose currentPose = this.getLayerPose(layer, partialTicks);
			for (Map.Entry<String, JointTransform> transformEntry  : currentPose.getJointTransformData().entrySet()) {
				composedPose.getJointTransformData().computeIfAbsent(transformEntry.getKey(), (key) -> transformEntry.getValue());
			}
		}
		return composedPose;
	}
	
	public Pose getComposedLowerLayerPose(Layer.Priority priority, float partialTicks) {
		Pose composedPose = new Pose();
		for (Map.Entry<Layer.Priority, Layer> entry : this.layers.entrySet()) {
			if (priority.compareTo(entry.getKey()) > 0 || priority == Layer.Priority.LOWEST) {
				Pose currentPose = this.getLayerPose(entry.getValue(), partialTicks);
				for (Map.Entry<String, JointTransform> transformEntry  : currentPose.getJointTransformData().entrySet()) {
					composedPose.getJointTransformData().computeIfAbsent(transformEntry.getKey(), (key) -> transformEntry.getValue());
				}
			}
		}
		return composedPose;
	}
	
	public boolean compareMotion(LivingMotion motion) {
		return this.currentMotion == motion;
	}

	public boolean compareOverridenMotion(LivingMotion motion) {
		return this.currentOverridenMotion == motion;
	}

	public void resetMotion() {
		this.playMotion(LivingMotion.IDLE, true);
		this.currentMotion = LivingMotion.IDLE;
		this.entitydata.currentMotion = LivingMotion.IDLE;
	}
	
	public void resetOverridenMotion() {
		this.currentOverridenMotion = LivingMotion.NONE;
		this.entitydata.currentOverridenMotion = LivingMotion.NONE;
	}
	
	public boolean prevAiming() {
		return this.currentOverridenMotion == LivingMotion.AIM;
	}
	
	public void playReboundAnimation() {
		this.playAnimation(this.overridenLivingAnimations.get(LivingMotion.SHOT), 0.0F);
		this.entitydata.currentOverridenMotion = LivingMotion.NONE;
		this.resetOverridenMotion();
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
	
	@Override
	public EntityState getEntityState() {
		AnimationPlayer player = this.getMainFrameLayer().animationPlayer;
		return player.getPlay().getState(player.getElapsedTime());
	}
}