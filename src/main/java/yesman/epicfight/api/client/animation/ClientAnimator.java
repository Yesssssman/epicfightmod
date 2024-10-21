package yesman.epicfight.api.client.animation;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.ServerAnimator;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.EntityState.StateFactor;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.animation.Layer.Priority;
import yesman.epicfight.api.client.animation.property.ClientAnimationProperties;
import yesman.epicfight.api.client.animation.property.JointMask.BindModifier;
import yesman.epicfight.api.client.animation.property.JointMask.JointMaskSet;
import yesman.epicfight.api.utils.datastruct.TypeFlexibleHashMap;
import yesman.epicfight.api.client.animation.property.JointMaskEntry;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class ClientAnimator extends Animator {
	public static Animator getAnimator(LivingEntityPatch<?> entitypatch) {
		return entitypatch.isLogicalClient() ? new ClientAnimator(entitypatch) : ServerAnimator.getAnimator(entitypatch);
	}
	
	private final Map<LivingMotion, StaticAnimation> compositeLivingAnimations;
	private final Map<LivingMotion, StaticAnimation> defaultLivingAnimations;
	private final Map<LivingMotion, StaticAnimation> defaultCompositeLivingAnimations;
	public final Layer.BaseLayer baseLayer;
	private LivingMotion currentMotion;
	private LivingMotion currentCompositeMotion;
	
	public ClientAnimator(LivingEntityPatch<?> entitypatch) {
		this(entitypatch, Layer.BaseLayer::new);
	}
	
	public ClientAnimator(LivingEntityPatch<?> entitypatch, Supplier<Layer.BaseLayer> layerSupplier) {
		this.entitypatch = entitypatch;
		this.currentMotion = LivingMotions.IDLE;
		this.currentCompositeMotion = LivingMotions.IDLE;
		this.compositeLivingAnimations = Maps.newHashMap();
		this.defaultLivingAnimations = Maps.newHashMap();
		this.defaultCompositeLivingAnimations = Maps.newHashMap();
		this.baseLayer = layerSupplier.get();
	}
	
	/** Play an animation by animation instance **/
	@Override
	public void playAnimation(StaticAnimation nextAnimation, float convertTimeModifier) {
		Layer layer = nextAnimation.getLayerType() == Layer.LayerType.BASE_LAYER ? this.baseLayer : this.baseLayer.compositeLayers.get(nextAnimation.getPriority());
		layer.paused = false;
		layer.playAnimation(nextAnimation, this.entitypatch, convertTimeModifier);
	}
	
	@Override
	public void playAnimationInstantly(StaticAnimation nextAnimation) {
		this.baseLayer.paused  = false;
		this.baseLayer.playAnimationInstant(nextAnimation, this.entitypatch);
	}
	
	@Override
	public void reserveAnimation(StaticAnimation nextAnimation) {
		this.baseLayer.paused = false;
		this.baseLayer.nextAnimation = nextAnimation;
	}
	
	@Override
	public void addLivingAnimation(LivingMotion livingMotion, StaticAnimation animation) {
		Layer.LayerType layerType = animation.getLayerType();
		boolean isBaseLayer = (layerType == Layer.LayerType.BASE_LAYER);
		
		Map<LivingMotion, StaticAnimation> storage = layerType == Layer.LayerType.BASE_LAYER ? this.livingAnimations : this.compositeLivingAnimations;
		LivingMotion compareMotion = layerType == Layer.LayerType.BASE_LAYER ? this.currentMotion : this.currentCompositeMotion;
		Layer layer = layerType == Layer.LayerType.BASE_LAYER ? this.baseLayer : this.baseLayer.compositeLayers.get(animation.getPriority());
		storage.put(livingMotion, animation);
		
		if (livingMotion == compareMotion) {
			EntityState state = this.getEntityState();
			
			if (!state.inaction()) {
				layer.playLivingAnimation(animation, this.entitypatch);
			}
		}
		
		if (isBaseLayer) {
			animation.getProperty(ClientAnimationProperties.MULTILAYER_ANIMATION).ifPresent(multilayerAnimation -> {
				this.compositeLivingAnimations.put(livingMotion, multilayerAnimation);
				
				if (livingMotion == this.currentCompositeMotion) {
					EntityState state = getEntityState();
					
					if (!state.inaction()) {
						layer.playLivingAnimation(multilayerAnimation, this.entitypatch);
					}
				}
			});
		}
	}
	
	public void setCurrentMotionsAsDefault() {
		this.defaultLivingAnimations.putAll(this.livingAnimations);
		this.defaultCompositeLivingAnimations.putAll(this.compositeLivingAnimations);
	}
	
	@Override
	public void resetLivingAnimations() {
		super.resetLivingAnimations();
		this.compositeLivingAnimations.clear();
		this.defaultLivingAnimations.forEach((key, val) -> this.addLivingAnimation(key, val));
		this.defaultCompositeLivingAnimations.forEach((key, val) -> this.addLivingAnimation(key, val));
	}
	
	public StaticAnimation getLivingMotion(LivingMotion motion) {
		return this.livingAnimations.getOrDefault(motion, this.livingAnimations.get(LivingMotions.IDLE));
	}
	
	public StaticAnimation getCompositeLivingMotion(LivingMotion motion) {
		return this.compositeLivingAnimations.get(motion);
	}
	
	@Override
	public void init() {
		super.init();
		
		this.setCurrentMotionsAsDefault();
		
		StaticAnimation idleMotion = this.livingAnimations.get(this.currentMotion);
		this.baseLayer.playAnimationInstant(idleMotion, this.entitypatch);
	}
	
	@Override
	public void tick() {
		// Layer debugging
		/**
		for (Layer layer : this.getAllLayers()) {
			System.out.println(layer);
		}
		System.out.println();
		**/
		this.baseLayer.update(this.entitypatch);
		
		if (this.baseLayer.animationPlayer.isEnd() && this.baseLayer.nextAnimation == null && this.currentMotion != LivingMotions.DEATH) {
			this.entitypatch.updateMotion(false);
			
			if (this.compositeLivingAnimations.containsKey(this.entitypatch.currentCompositeMotion)) {
				this.playAnimation(this.getCompositeLivingMotion(this.entitypatch.currentCompositeMotion), 0.0F);
			}
			
			this.baseLayer.playAnimation(this.getLivingMotion(this.entitypatch.currentLivingMotion), this.entitypatch, 0.0F);
		} else {
			if (!this.compareCompositeMotion(this.entitypatch.currentCompositeMotion)) {
				/* Turns off the multilayer of the base layer */
				this.getLivingMotion(this.currentCompositeMotion).getProperty(ClientAnimationProperties.MULTILAYER_ANIMATION).ifPresent((multilayerAnimation) -> {
					if (!this.compositeLivingAnimations.containsKey(this.entitypatch.currentCompositeMotion)) {
						this.getCompositeLayer(multilayerAnimation.getPriority()).off(this.entitypatch);
					}
				});
				
				if (this.compositeLivingAnimations.containsKey(this.currentCompositeMotion)) {
					StaticAnimation nextLivingAnimation = this.getCompositeLivingMotion(this.entitypatch.currentCompositeMotion);
					
					if (nextLivingAnimation == null || nextLivingAnimation.getPriority() != this.getCompositeLivingMotion(this.currentCompositeMotion).getPriority()) {
						this.getCompositeLayer(this.getCompositeLivingMotion(this.currentCompositeMotion).getPriority()).off(this.entitypatch);
					}
				}
				
				if (this.compositeLivingAnimations.containsKey(this.entitypatch.currentCompositeMotion)) {
					this.playAnimation(this.getCompositeLivingMotion(this.entitypatch.currentCompositeMotion), 0.0F);
				}
			}
			
			if (!this.compareMotion(this.entitypatch.currentLivingMotion) && this.entitypatch.currentLivingMotion != LivingMotions.DEATH) {
				if (this.livingAnimations.containsKey(this.entitypatch.currentLivingMotion)) {
					this.baseLayer.playAnimation(this.getLivingMotion(this.entitypatch.currentLivingMotion), this.entitypatch, 0.0F);
				}
			}
		}
		
		this.currentMotion = this.entitypatch.currentLivingMotion;
		this.currentCompositeMotion = this.entitypatch.currentCompositeMotion;
	}
	
	@Override
	public void playDeathAnimation() {
		if (!this.getPlayerFor(null).getAnimation().getProperty(ActionAnimationProperty.IS_DEATH_ANIMATION).orElse(false)) {
			this.playAnimation(this.livingAnimations.getOrDefault(LivingMotions.DEATH, Animations.DUMMY_ANIMATION), 0.0F);
			this.currentMotion = LivingMotions.DEATH;
		}
	}
	
	public StaticAnimation getJumpAnimation() {
		return this.livingAnimations.get(LivingMotions.JUMP);
	}
	
	public Layer getCompositeLayer(Layer.Priority priority) {
		return this.baseLayer.compositeLayers.get(priority);
	}
	
	public Collection<Layer> getAllLayers() {
		List<Layer> layerList = Lists.newArrayList();
		layerList.add(this.baseLayer);
		layerList.addAll(this.baseLayer.compositeLayers.values());
		
		return layerList;
	}
	
	@Override
	public Pose getPose(float partialTicks) {
		return this.getPose(partialTicks, true);
	}
	
	public Pose getPose(float partialTicks, boolean useCurrentMotion) {
		Pose composedPose = new Pose();
		Pose baseLayerPose = this.baseLayer.getEnabledPose(this.entitypatch, useCurrentMotion, partialTicks);
		
		Map<Layer.Priority, Pair<DynamicAnimation, Pose>> layerPoses = Maps.newLinkedHashMap();
		composedPose.putJointData(baseLayerPose);
		
		for (Layer.Priority priority : this.baseLayer.baseLayerPriority.uppers()) {
			Layer compositeLayer = this.baseLayer.compositeLayers.get(priority);
			
			if (priority == Layer.Priority.LOWEST && this.baseLayer.animationPlayer.getAnimation().isMainFrameAnimation()) {
				continue;
			}
			
			if (!compositeLayer.isDisabled() && !compositeLayer.animationPlayer.isEmpty()) {
				Pose layerPose = compositeLayer.getEnabledPose(this.entitypatch, useCurrentMotion, partialTicks);
				layerPoses.put(priority, Pair.of(compositeLayer.animationPlayer.getAnimation(), layerPose));
				composedPose.putJointData(layerPose);
			}
		}
		
		Joint rootJoint = this.entitypatch.getArmature().getRootJoint();
		this.applyBindModifier(baseLayerPose, composedPose, rootJoint, layerPoses, useCurrentMotion);
		
		return composedPose;
	}
	
	public Pose getComposedLayerPoseBelow(Layer.Priority priorityLimit, float partialTicks) {
		Pose composedPose = this.baseLayer.getEnabledPose(this.entitypatch, true, partialTicks);
		Pose baseLayerPose = this.baseLayer.getEnabledPose(this.entitypatch, true, partialTicks);
		Map<Layer.Priority, Pair<DynamicAnimation, Pose>> layerPoses = Maps.newLinkedHashMap();
		
		for (Layer.Priority priority : priorityLimit.lowers()) {
			Layer compositeLayer = this.baseLayer.compositeLayers.get(priority);
			
			if (priority == Layer.Priority.LOWEST && this.baseLayer.animationPlayer.getAnimation().isMainFrameAnimation()) {
				continue;
			}
			
			if (!compositeLayer.isDisabled()) {
				Pose layerPose = compositeLayer.getEnabledPose(this.entitypatch, true, partialTicks);
				layerPoses.put(priority, Pair.of(compositeLayer.animationPlayer.getAnimation(), layerPose));
				composedPose.putJointData(layerPose);
			}
		}
		
		Joint rootJoint = this.entitypatch.getArmature().getRootJoint();
		
		if (!layerPoses.isEmpty()) {
			this.applyBindModifier(baseLayerPose, composedPose, rootJoint, layerPoses, true);
		}
		
		return composedPose;
	}
	
	public void applyBindModifier(Pose basePose, Pose result, Joint joint, Map<Layer.Priority, Pair<DynamicAnimation, Pose>> poses, boolean useCurrentMotion) {
		List<Priority> list = Lists.newArrayList(poses.keySet());
		Collections.reverse(list);
		
		for (Layer.Priority priority : list) {
			DynamicAnimation nowPlaying = poses.get(priority).getFirst();
			JointMaskEntry jointMaskEntry = nowPlaying.getJointMaskEntry(this.entitypatch, useCurrentMotion).orElse(null);
			
			if (jointMaskEntry != null) {
				LivingMotion livingMotion = this.getCompositeLayer(priority).getLivingMotion(this.entitypatch, useCurrentMotion);
				
				if (nowPlaying.hasTransformFor(joint.getName()) && !jointMaskEntry.isMasked(livingMotion, joint.getName())) {
					JointMaskSet set = jointMaskEntry.getMask(livingMotion);
					BindModifier bindModifier = set.getBindModifier(joint.getName());
					
					if (bindModifier != null) {
						bindModifier.modify(this.entitypatch, basePose, result, livingMotion, jointMaskEntry, priority, joint, poses);
						break;
					}
				}
			}
		}
		
		for (Joint subJoints : joint.getSubJoints()) {
			this.applyBindModifier(basePose, result, subJoints, poses, useCurrentMotion);
		}
	}
	
	public boolean compareMotion(LivingMotion motion) {
		return this.currentMotion.isSame(motion);
	}
	
	public boolean compareCompositeMotion(LivingMotion motion) {
		return this.currentCompositeMotion.isSame(motion);
	}
	
	public void resetMotion() { 
		this.currentMotion = LivingMotions.IDLE;
		this.entitypatch.currentLivingMotion = LivingMotions.IDLE;
	}
	
	public void resetCompositeMotion() {
		this.currentCompositeMotion = LivingMotions.NONE;
		this.entitypatch.currentCompositeMotion = LivingMotions.NONE;
	}
	
	public void offAllLayers() {
		for (Layer layer : this.baseLayer.compositeLayers.values()) {
			layer.off(this.entitypatch);
		}
	}
	
	public boolean isAiming() {
		return this.currentCompositeMotion == LivingMotions.AIM;
	}
	
	public void playReboundAnimation() {
		if (this.compositeLivingAnimations.containsKey(LivingMotions.SHOT)) {
			this.playAnimation(this.compositeLivingAnimations.get(LivingMotions.SHOT), 0.0F);
			this.entitypatch.currentCompositeMotion = LivingMotions.NONE;
			this.resetCompositeMotion();
		}
	}
	
	@Override
	public AnimationPlayer getPlayerFor(DynamicAnimation playingAnimation) {
		for (Layer layer : this.baseLayer.compositeLayers.values()) {
			if (layer.animationPlayer.getAnimation().getRealAnimation().equals(playingAnimation)) {
				return layer.animationPlayer;
			}
		}
		
		return this.baseLayer.animationPlayer;
	}
	
	public Layer.Priority getPriorityFor(DynamicAnimation playingAnimation) {
		for (Layer layer : this.baseLayer.compositeLayers.values()) {
			if (layer.animationPlayer.getAnimation().getRealAnimation().equals(playingAnimation)) {
				return layer.priority;
			}
		}
		
		return this.baseLayer.priority;
	}
	
	public LivingMotion getLivingMotionFor(DynamicAnimation animation) {
		Layer.LayerType layerType = animation.getProperty(ClientAnimationProperties.LAYER_TYPE).orElse(Layer.LayerType.BASE_LAYER);
		
		if (layerType == Layer.LayerType.BASE_LAYER) {
			return animation == this.baseLayer.animationPlayer.getAnimation() ? this.currentMotion : this.entitypatch.currentLivingMotion;
		} else {
			Layer.Priority priority = animation.getProperty(ClientAnimationProperties.PRIORITY).orElse(Layer.Priority.LOWEST);
			return animation == this.baseLayer.compositeLayers.get(priority).animationPlayer.getAnimation() ? this.currentCompositeMotion : this.entitypatch.currentCompositeMotion;
		}
	}
	
	public LivingMotion currentMotion() {
		return this.currentMotion;
	}
	
	public LivingMotion currentCompositeMotion() {
		return this.currentCompositeMotion;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> Pair<AnimationPlayer, T> findFor(Class<T> animationType) {
		for (Layer layer : this.baseLayer.compositeLayers.values()) {
			if (animationType.isAssignableFrom(layer.animationPlayer.getAnimation().getClass())) {
				return Pair.of(layer.animationPlayer, (T)layer.animationPlayer.getAnimation());
			}
		}
		
		return animationType.isAssignableFrom(this.baseLayer.animationPlayer.getAnimation().getClass()) ? Pair.of(this.baseLayer.animationPlayer, (T)this.baseLayer.animationPlayer.getAnimation()) : null;
	}
	
	public LivingEntityPatch<?> getOwner() {
		return this.entitypatch;
	}
	
	@Override
	public EntityState getEntityState() {
		TypeFlexibleHashMap<StateFactor<?>> stateMap = new TypeFlexibleHashMap<> (false);
		
		for (Layer layer : this.baseLayer.compositeLayers.values()) {
			if (!layer.disabled) {
				stateMap.putAll(layer.animationPlayer.getAnimation().getStatesMap(this.entitypatch, layer.animationPlayer.getElapsedTime()));
			}
			
			if (layer.priority == this.baseLayer.baseLayerPriority) {
				stateMap.putAll(this.baseLayer.animationPlayer.getAnimation().getStatesMap(this.entitypatch, this.baseLayer.animationPlayer.getElapsedTime()));
			}
		}
		
		return new EntityState(stateMap);
	}
}