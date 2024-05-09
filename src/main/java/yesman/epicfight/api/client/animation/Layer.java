package yesman.epicfight.api.client.animation;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;

import com.google.common.collect.Maps;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.types.ConcurrentLinkAnimation;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.LayerOffAnimation;
import yesman.epicfight.api.animation.types.LinkAnimation;
import yesman.epicfight.api.animation.types.MainFrameAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class Layer {
	protected DynamicAnimation nextAnimation;
	protected final LinkAnimation linkAnimation;
	protected final ConcurrentLinkAnimation concurrentLinkAnimation;
	protected final LayerOffAnimation layerOffAnimation;
	protected final Layer.Priority priority;
	protected boolean disabled;
	protected boolean paused;
	public final AnimationPlayer animationPlayer;
	
	public Layer(Priority priority) {
		this(priority, AnimationPlayer::new);
	}
	
	public Layer(Priority priority, Supplier<AnimationPlayer> animationPlayerProvider) {
		this.animationPlayer = animationPlayerProvider.get();
		this.linkAnimation = new LinkAnimation();
		this.concurrentLinkAnimation = new ConcurrentLinkAnimation();
		this.layerOffAnimation = new LayerOffAnimation(priority);
		this.priority = priority;
		this.disabled = true;
	}
	
	public void playAnimation(StaticAnimation nextAnimation, LivingEntityPatch<?> entitypatch, float convertTimeModifier) {
		this.animationPlayer.getAnimation().end(entitypatch, nextAnimation, this.animationPlayer.isEnd());
		this.resume();
		nextAnimation.begin(entitypatch);
		
		if (!nextAnimation.isMetaAnimation()) {
			this.setLinkAnimation(nextAnimation, entitypatch, entitypatch.getClientAnimator().getPose(0.0F, false), convertTimeModifier);
			this.linkAnimation.putOnPlayer(this.animationPlayer, entitypatch);
			entitypatch.updateEntityState();
			this.nextAnimation = nextAnimation;
		}
	}
	
	/**
	 * Plays an animation without a link animation
	 */
	public void playAnimationInstant(DynamicAnimation nextAnimation, LivingEntityPatch<?> entitypatch) {
		this.animationPlayer.getAnimation().end(entitypatch, nextAnimation, this.animationPlayer.isEnd());
		this.resume();
		
		nextAnimation.begin(entitypatch);
		nextAnimation.putOnPlayer(this.animationPlayer, entitypatch);
		entitypatch.updateEntityState();
		this.nextAnimation = null;
	}
	
	protected void playLivingAnimation(StaticAnimation nextAnimation, LivingEntityPatch<?> entitypatch) {
		this.animationPlayer.getAnimation().end(entitypatch, nextAnimation, this.animationPlayer.isEnd());
		this.resume();
		nextAnimation.begin(entitypatch);
		
		if (!nextAnimation.isMetaAnimation()) {
			this.concurrentLinkAnimation.acceptFrom(this.animationPlayer.getAnimation().getRealAnimation(), nextAnimation, this.animationPlayer.getElapsedTime());
			this.concurrentLinkAnimation.putOnPlayer(this.animationPlayer, entitypatch);
			entitypatch.updateEntityState();
			this.nextAnimation = nextAnimation;
		}
	}
	
	protected void setLinkAnimation(StaticAnimation nextAnimation, LivingEntityPatch<?> entitypatch, Pose lastPose, float convertTimeModifier) {
		DynamicAnimation fromAnimation = this.animationPlayer.isEmpty() ? entitypatch.getClientAnimator().baseLayer.animationPlayer.getAnimation() : this.animationPlayer.getAnimation();
		
		if (fromAnimation instanceof LinkAnimation linkAnimation) {
			fromAnimation = linkAnimation.getFromAnimation();
		}
		
		nextAnimation.setLinkAnimation(fromAnimation, lastPose, !this.animationPlayer.isEmpty(), convertTimeModifier, entitypatch, this.linkAnimation);
	}
	
	public void update(LivingEntityPatch<?> entitypatch) {
		if (this.paused) {
			this.animationPlayer.setElapsedTime(this.animationPlayer.getElapsedTime());
		} else {
			this.animationPlayer.tick(entitypatch);
		}
		
		if (this.isBaseLayer()) {
			entitypatch.updateEntityState();
			entitypatch.updateMotion(true);
		}
		
		this.animationPlayer.getAnimation().tick(entitypatch);
		
		if (!this.paused && this.animationPlayer.isEnd()) {
			if (this.nextAnimation != null) {
				this.animationPlayer.getAnimation().end(entitypatch, this.nextAnimation, true);
				
				if (!this.animationPlayer.getAnimation().isLinkAnimation() && !this.nextAnimation.isLinkAnimation()) {
					this.nextAnimation.begin(entitypatch);
				}
				
				this.nextAnimation.putOnPlayer(this.animationPlayer, entitypatch);
				this.nextAnimation = null;
			} else {
				if (this.animationPlayer.getAnimation() instanceof LayerOffAnimation) {
					this.animationPlayer.getAnimation().end(entitypatch, Animations.DUMMY_ANIMATION, true);
				} else {
					this.off(entitypatch);
				}
			}
		}
	}
	
	public void pause() {
		this.paused = true;
	}
	
	public void resume() {
		this.paused = false;
		this.disabled = false;
	}
	
	protected boolean isDisabled() {
		return this.disabled;
	}
	
	protected boolean isBaseLayer() {
		return false;
	}
	
	public void copyLayerTo(Layer layer, float playbackTime) {
		DynamicAnimation animation;
		
		if (this.animationPlayer.getAnimation() == this.linkAnimation) {
			this.linkAnimation.copyTo(layer.linkAnimation);
			animation = layer.linkAnimation;
		} else {
			animation = this.animationPlayer.getAnimation();
		}
		
		layer.animationPlayer.setPlayAnimation(animation);
		layer.animationPlayer.setElapsedTime(this.animationPlayer.getPrevElapsedTime() + playbackTime, this.animationPlayer.getElapsedTime() + playbackTime);
		layer.nextAnimation = this.nextAnimation;
		layer.resume();
	}
	
	public LivingMotion getLivingMotion(LivingEntityPatch<?> entitypatch, boolean current) {
		ClientAnimator animator = entitypatch.getClientAnimator();
		
		if (this.isBaseLayer()) {
			return current ? entitypatch.currentLivingMotion : animator.currentMotion();
		} else {
			return current ? entitypatch.currentCompositeMotion : animator.currentCompositeMotion();
		}
	}
	
	public Pose getEnabledPose(LivingEntityPatch<?> entitypatch, boolean useCurrentMotion, float partialTick) {
		Pose pose = this.animationPlayer.getCurrentPose(entitypatch, partialTick);
		this.animationPlayer.getAnimation().getJointMaskEntry(entitypatch, useCurrentMotion).ifPresent((jointEntry) -> pose.removeJointIf((entry) -> jointEntry.isMasked(this.getLivingMotion(entitypatch, useCurrentMotion), entry.getKey())));
		
		return pose;
	}
	
	public void off(LivingEntityPatch<?> entitypatch) {
		if (!this.isDisabled() && !(this.animationPlayer.getAnimation() instanceof LayerOffAnimation)) {
			float convertTime = entitypatch.getClientAnimator().baseLayer.animationPlayer.getAnimation().getConvertTime();
			setLayerOffAnimation(this.animationPlayer.getAnimation(), this.getEnabledPose(entitypatch, false, 1.0F), this.layerOffAnimation, convertTime);
			this.playAnimationInstant(this.layerOffAnimation, entitypatch);
		}
	}
	
	public static void setLayerOffAnimation(DynamicAnimation currentAnimation, Pose currentPose, LayerOffAnimation offAnimation, float convertTime) {
		offAnimation.setLastAnimation(currentAnimation.getRealAnimation());
		offAnimation.setLastPose(currentPose);
		offAnimation.setTotalTime(convertTime);
	}
	
	public DynamicAnimation getNextAnimation() {
		return this.nextAnimation;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append(this.isBaseLayer() ? "Base Layer(" + ((BaseLayer)this).baseLayerPriority + ") : " : " Composite Layer(" + this.priority + ") : ");
		sb.append(this.animationPlayer.getAnimation() + " ");
		sb.append(", prev elapsed time: " + this.animationPlayer.getPrevElapsedTime() + " ");
		sb.append(", elapsed time: " + this.animationPlayer.getElapsedTime() + " ");
		sb.append(", total time: " + this.animationPlayer.getAnimation().getTotalTime() + " ");
		
		return sb.toString();
	}
	
	@OnlyIn(Dist.CLIENT)
	public static class BaseLayer extends Layer {
		protected Map<Layer.Priority, Layer> compositeLayers = Maps.newLinkedHashMap();
		protected Layer.Priority baseLayerPriority;
		
		public BaseLayer() {
			this(AnimationPlayer::new);
		}
		
		public BaseLayer(Supplier<AnimationPlayer> animationPlayerProvider) {
			super(null, animationPlayerProvider);
			
			this.compositeLayers.computeIfAbsent(Priority.LOWEST, Layer::new);
			this.compositeLayers.computeIfAbsent(Priority.MIDDLE, Layer::new);
			this.compositeLayers.computeIfAbsent(Priority.HIGHEST, Layer::new);
			this.baseLayerPriority = Priority.LOWEST;
		}
		
		@Override
		public void playAnimation(StaticAnimation nextAnimation, LivingEntityPatch<?> entitypatch, float convertTimeModifier) {
			this.offCompositeLayerLowerThan(entitypatch, nextAnimation);
			super.playAnimation(nextAnimation, entitypatch, convertTimeModifier);
			this.baseLayerPriority = nextAnimation.getPriority();
		}
		
		@Override
		protected void playLivingAnimation(StaticAnimation nextAnimation, LivingEntityPatch<?> entitypatch) {
			this.animationPlayer.getAnimation().end(entitypatch, nextAnimation, this.animationPlayer.isEnd());
			this.resume();
			nextAnimation.begin(entitypatch);
			
			if (!nextAnimation.isMetaAnimation()) {
				this.concurrentLinkAnimation.acceptFrom(this.animationPlayer.getAnimation().getRealAnimation(), nextAnimation, this.animationPlayer.getElapsedTime());
				this.concurrentLinkAnimation.putOnPlayer(this.animationPlayer, entitypatch);
				entitypatch.updateEntityState();
				this.nextAnimation = nextAnimation;
			}
		}
		
		@Override
		public void update(LivingEntityPatch<?> entitypatch) {
			super.update(entitypatch);
			
			for (Layer layer : this.compositeLayers.values()) {
				layer.update(entitypatch);
			}
		}
		
		public void offCompositeLayerLowerThan(LivingEntityPatch<?> entitypatch, StaticAnimation nextAnimation) {
			for (Priority p : nextAnimation.getPriority().lowerEquals()) {
				if (p == Priority.LOWEST && !nextAnimation.isMainFrameAnimation()) {
					continue;
				}
				
				this.compositeLayers.get(p).off(entitypatch);
			}
		}
		
		public void disableLayer(Priority priority) {
			Layer layer = this.compositeLayers.get(priority);
			layer.disabled = true;
			layer.animationPlayer.setPlayAnimation(Animations.DUMMY_ANIMATION);
		}
		
		public Layer getLayer(Priority priority) {
			return this.compositeLayers.get(priority);
		}
		
		public Priority getBaseLayerPriority() {
			return this.baseLayerPriority;
		}
		
		@Override
		public void off(LivingEntityPatch<?> entitypatch) {
			
		}
		
		@Override
		protected boolean isDisabled() {
			return false;
		}
		
		@Override
		protected boolean isBaseLayer() {
			return true;
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public enum LayerType {
		BASE_LAYER, COMPOSITE_LAYER
	}
	
	@OnlyIn(Dist.CLIENT)
	public enum Priority {
		/**
		 * LOWEST: Common living motions (Composite layer having this priority will be overrided if base layer is {@link MainFrameAnimation})
		 * MIDDLE: Composite living motions
		 * HIGHEST: Not repeating composite motions (Shield hits, Katana sheath)
		 * BASE: Base layer (not used)
		 */
		LOWEST, MIDDLE, HIGHEST;
		
		public Priority[] lowers() {
			return Arrays.copyOfRange(Priority.values(), 0, this.ordinal());
		}
		
		public Priority[] uppers() {
			return Arrays.copyOfRange(Priority.values(), this == LOWEST ? this.ordinal() : this.ordinal() + 1, 3);
		}
		
		public Priority[] lowerEquals() {
			return Arrays.copyOfRange(Priority.values(), 0, this.ordinal() + 1);
		}
	}
}