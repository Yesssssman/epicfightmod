package yesman.epicfight.api.client.animation;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.ServerAnimator;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.animation.JointMask.BindModifier;
import yesman.epicfight.api.client.animation.Layer.Priority;
import yesman.epicfight.api.client.model.ClientModels;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class ClientAnimator extends Animator {
	public static Animator getAnimator(LivingEntityPatch<?> entitypatch) {
		return entitypatch.isLogicalClient() ? new ClientAnimator(entitypatch) : ServerAnimator.getAnimator(entitypatch);
	}
	
	private final Map<LivingMotion, StaticAnimation> livingAnimations;
	private final Map<LivingMotion, StaticAnimation> compositeLivingAnimations;
	private final Map<LivingMotion, StaticAnimation> defaultAnimations;
	public final Layer.BaseLayer baseLayer;
	private LivingMotion currentMotion;
	private LivingMotion currentCompositeMotion;
	
	public ClientAnimator(LivingEntityPatch<?> entitypatch) {
		this.entitypatch = entitypatch;
		this.currentMotion = LivingMotion.IDLE;
		this.currentCompositeMotion = LivingMotion.IDLE;
		this.livingAnimations = Maps.<LivingMotion, StaticAnimation>newHashMap();
		this.compositeLivingAnimations = Maps.<LivingMotion, StaticAnimation>newHashMap();
		this.defaultAnimations = Maps.<LivingMotion, StaticAnimation>newHashMap();
		this.baseLayer = new Layer.BaseLayer(null);
	}
	
	/** Play an animation by animation instance **/
	@Override
	public void playAnimation(StaticAnimation nextAnimation, float convertTimeModifier) {
		this.baseLayer.paused  = false;
		this.baseLayer.playAnimation(nextAnimation, this.entitypatch, convertTimeModifier);
	}
	
	@Override
	public void playAnimationInstantly(StaticAnimation nextAnimation) {
		this.baseLayer.paused  = false;
		this.baseLayer.playAnimation(nextAnimation, this.entitypatch);
	}
	
	public void playCompositeAnimation(int namespaceId, int id, float convertTimeModifier) {
		this.playCompositeAnimation(EpicFightMod.getInstance().animationManager.findAnimation(namespaceId, id), convertTimeModifier);
	}
	
	public void playCompositeAnimation(StaticAnimation nextAnimation, float convertTimeModifier) {
		Priority priority = nextAnimation.getPriority();
		Layer compositeLayer = this.baseLayer.compositeLayers.get(priority);
		compositeLayer.playAnimation(nextAnimation, this.entitypatch, convertTimeModifier);
	}
	
	@Override
	public void reserveAnimation(StaticAnimation nextAnimation) {
		this.baseLayer.paused = false;
		this.baseLayer.nextAnimation = nextAnimation;
	}
	
	/** Add new Living animation of entity **/
	public void addLivingAnimation(LivingMotion motion, StaticAnimation animation) {
		this.livingAnimations.put(motion, animation);
		
		if (motion == this.currentMotion) {
			EntityState state = this.getEntityState();
			
			if (!state.inaction()) {
				this.playAnimation(animation, 0.0F);
			}
		}
	}
	
	public void addCompositeAnimation(LivingMotion motion, StaticAnimation animation) {
		if (animation != null) {
			this.compositeLivingAnimations.put(motion, animation);
			
			if (motion == this.currentCompositeMotion) {
				EntityState state = this.getEntityState();
				
				if (!state.inaction()) {
					this.playCompositeAnimation(animation, 0.0F);
				}
			}
		}
	}
	
	public void addDefaultLivingMotion(LivingMotion motion, StaticAnimation animation) {
		this.defaultAnimations.put(motion, animation);
	}
	
	public void resetCompositeMotions() {
		this.compositeLivingAnimations.clear();
	}
	
	public StaticAnimation getLivingMotion(LivingMotion motion) {
		return this.livingAnimations.getOrDefault(motion, this.defaultAnimations.getOrDefault(motion, Animations.DUMMY_ANIMATION));
	}
	
	public StaticAnimation getCompositeLivingMotion(LivingMotion motion) {
		return this.compositeLivingAnimations.getOrDefault(motion, this.defaultAnimations.getOrDefault(motion, Animations.DUMMY_ANIMATION));
	}
	
	public void setPoseToModel(float partialTicks) {
		Joint rootJoint = this.entitypatch.getEntityModel(ClientModels.LOGICAL_CLIENT).getArmature().getJointHierarcy();
		this.applyPoseToJoint(rootJoint, new OpenMatrix4f(), this.getPose(partialTicks), partialTicks);
	}
	
	public void applyPoseToJoint(Joint joint, OpenMatrix4f parentTransform, Pose pose, float partialTicks) {
		OpenMatrix4f result = pose.getTransformByName(joint.getName()).getAnimationBindedMatrix(joint, parentTransform);
		joint.setAnimatedTransform(result);
		
		for (Joint joints : joint.getSubJoints()) {
			this.applyPoseToJoint(joints, result, pose, partialTicks);
		}
	}
	
	@Override
	public void init() {
		this.entitypatch.initAnimator(this);
		StaticAnimation idleMotion = this.livingAnimations.get(this.currentMotion);
		this.baseLayer.playAnimation(idleMotion, this.entitypatch);
	}
	
	@Override
	public void updatePose() {
		this.prevPose = this.currentPose;
		this.currentPose = this.getComposedLayerPose(1.0F);
	}
	
	@Override
	public void update() {
		this.baseLayer.update(this.entitypatch);
		this.updatePose();
		
		if (this.baseLayer.animationPlayer.isEnd() && this.baseLayer.nextAnimation == null && this.currentMotion != LivingMotion.DEATH) {
			this.entitypatch.updateMotion(false);
			this.baseLayer.playAnimation(this.getLivingMotion(this.entitypatch.currentMotion), this.entitypatch, 0.0F);
		}
		
		if (!this.compareCompositeMotion(this.entitypatch.currentCompositeMotion)) {
			if (this.compositeLivingAnimations.containsKey(this.entitypatch.currentCompositeMotion)) {
				this.playCompositeAnimation(this.getCompositeLivingMotion(this.entitypatch.currentCompositeMotion), 0.0F);
			} else {
				this.getCompositeLayer(Layer.Priority.MIDDLE).off(this.entitypatch);
			}
		}
		
		if (!this.compareMotion(this.entitypatch.currentMotion)) {
			if (this.livingAnimations.containsKey(this.entitypatch.currentMotion)) {
				this.baseLayer.playAnimation(this.getLivingMotion(this.entitypatch.currentMotion), this.entitypatch, 0.0F);
			}
		}
		
		this.currentMotion = this.entitypatch.currentMotion;
		this.currentCompositeMotion = this.entitypatch.currentCompositeMotion;
	}
	
	@Override
	public void playDeathAnimation() {
		this.playAnimation(this.livingAnimations.get(LivingMotion.DEATH), 0);
		this.currentMotion = LivingMotion.DEATH;
	}
	
	public StaticAnimation getJumpAnimation() {
		return this.livingAnimations.get(LivingMotion.JUMP);
	}
	
	public Layer getCompositeLayer(Layer.Priority priority) {
		return this.baseLayer.compositeLayers.get(priority);
	}
	
	public Pose getComposedLayerPose(float partialTicks) {
		Pose composedPose = new Pose();
		Pose currentBasePose = this.baseLayer.animationPlayer.getCurrentPose(this.entitypatch, partialTicks);;
		Map<Layer.Priority, Pair<DynamicAnimation, Pose>> layerPoses = Maps.newLinkedHashMap();
		layerPoses.put(Layer.Priority.LOWEST, Pair.of(this.baseLayer.animationPlayer.getPlay(), currentBasePose));
		
		for (Map.Entry<String, JointTransform> transformEntry : currentBasePose.getJointTransformData().entrySet()) {
			composedPose.putJointData(transformEntry.getKey(), transformEntry.getValue());
		}
		
		for (Layer.Priority priority : this.baseLayer.priority.uppers()) {
			Layer compositeLayer = this.baseLayer.compositeLayers.get(priority);
			
			if (!compositeLayer.isDisabled()) {
				Pose layerPose = compositeLayer.animationPlayer.getCurrentPose(this.entitypatch, compositeLayer.paused ? 1.0F : partialTicks);
				layerPoses.put(priority, Pair.of(compositeLayer.animationPlayer.getPlay(), layerPose));
				
				for (Map.Entry<String, JointTransform> transformEntry : layerPose.getJointTransformData().entrySet()) {
					composedPose.getJointTransformData().put(transformEntry.getKey(), transformEntry.getValue());
				}
			}
		}
		
		Joint rootJoint = this.entitypatch.getEntityModel(ClientModels.LOGICAL_CLIENT).getArmature().getJointHierarcy();
		this.applyBindModifier(composedPose, rootJoint, layerPoses);
		
		return composedPose;
	}
	
	public Pose getComposedLayerPoseBelow(Layer.Priority priorityLimit, float partialTicks) {
		Pose composedPose = this.baseLayer.animationPlayer.getCurrentPose(this.entitypatch, partialTicks);
		Map<Layer.Priority, Pair<DynamicAnimation, Pose>> layerPoses = Maps.newLinkedHashMap();
		
		for (Layer.Priority priority : priorityLimit.lowers()) {
			Layer compositeLayer = this.baseLayer.compositeLayers.get(priority);
			
			if (!compositeLayer.isDisabled()) {
				Pose layerPose = compositeLayer.animationPlayer.getCurrentPose(this.entitypatch, compositeLayer.paused ? 1.0F : partialTicks);
				layerPoses.put(priority, Pair.of(compositeLayer.animationPlayer.getPlay(), layerPose));
				
				for (Map.Entry<String, JointTransform> transformEntry : layerPose.getJointTransformData().entrySet()) {
					composedPose.getJointTransformData().put(transformEntry.getKey(), transformEntry.getValue());
				}
			}
		}
		
		Joint rootJoint = this.entitypatch.getEntityModel(ClientModels.LOGICAL_CLIENT).getArmature().getJointHierarcy();
		this.applyBindModifier(composedPose, rootJoint, layerPoses);
		
		return composedPose;
	}
	
	public void applyBindModifier(Pose result, Joint joint, Map<Layer.Priority, Pair<DynamicAnimation, Pose>> poses) {
		List<Priority> list = Lists.newArrayList(poses.keySet());
		Collections.reverse(list);
		
		for (Layer.Priority priority : list) {
			DynamicAnimation nowPlaying = poses.get(priority).getFirst();
			
			if (nowPlaying.isJointEnabled(this.entitypatch, joint.getName())) {
				BindModifier bindModifier = nowPlaying.getBindModifier(this.entitypatch, joint.getName());
				
				if (bindModifier != null) {
					bindModifier.modify(this, result, priority, joint, poses);
				}
				
				break;
			}
		}
		
		for (Joint subJoints : joint.getSubJoints()) {
			this.applyBindModifier(result, subJoints, poses);
		}
	}
	
	public boolean compareMotion(LivingMotion motion) {
		boolean flag = this.currentMotion == motion || (this.currentMotion == LivingMotion.INACTION && motion == LivingMotion.IDLE);
		
		if (flag) {
			this.currentMotion = motion;
		}
		
		return flag;
	}
	
	public boolean compareCompositeMotion(LivingMotion motion) {
		return this.currentCompositeMotion == motion;
	}
	
	public void startInaction() {
		this.currentMotion = LivingMotion.INACTION;
		this.entitypatch.currentMotion = LivingMotion.INACTION;
	}
	
	public void resetCompositeMotion() {
		this.currentCompositeMotion = LivingMotion.NONE;
		this.entitypatch.currentCompositeMotion = LivingMotion.NONE;
	}
	
	public boolean isAiming() {
		return this.currentCompositeMotion == LivingMotion.AIM;
	}
	
	public void playReboundAnimation() {
		if (this.compositeLivingAnimations.containsKey(LivingMotion.SHOT)) {
			this.playCompositeAnimation(this.compositeLivingAnimations.get(LivingMotion.SHOT), 0.0F);
			this.entitypatch.currentCompositeMotion = LivingMotion.NONE;
			this.resetCompositeMotion();
		}
	}
	
	@Override
	public AnimationPlayer getPlayerFor(DynamicAnimation playingAnimation) {
		for (Layer layer : this.baseLayer.compositeLayers.values()) {
			if (layer.animationPlayer.getPlay().equals(playingAnimation)) {
				return layer.animationPlayer;
			}
		}
		
		return this.baseLayer.animationPlayer;
	}
	
	public LivingEntityPatch<?> getOwner() {
		return this.entitypatch;
	}
	
	@Override
	public EntityState getEntityState() {
		return this.baseLayer.animationPlayer.getPlay().getState(this.baseLayer.animationPlayer.getElapsedTime());
	}
}