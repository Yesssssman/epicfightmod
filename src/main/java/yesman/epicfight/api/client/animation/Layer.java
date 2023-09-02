package yesman.epicfight.api.client.animation;

import java.util.Arrays;
import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.AnimationPlayer;
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
		this.animationPlayer = new AnimationPlayer();
		this.linkAnimation = new LinkAnimation();
		this.concurrentLinkAnimation = new ConcurrentLinkAnimation();
		this.layerOffAnimation = new LayerOffAnimation(priority);
		this.priority = priority;
		this.disabled = true;
	}
	
	public void playAnimation(StaticAnimation nextAnimation, LivingEntityPatch<?> entitypatch, float convertTimeModifier) {
		Pose lastPose = entitypatch.getArmature().getPose(1.0F);
		
		this.animationPlayer.getAnimation().end(entitypatch, nextAnimation, this.animationPlayer.isEnd());
		this.resume();
		nextAnimation.begin(entitypatch);
		
		if (!nextAnimation.isMetaAnimation()) {
			this.setLinkAnimation(nextAnimation, entitypatch, lastPose, convertTimeModifier);
			this.linkAnimation.putOnPlayer(this.animationPlayer);
			entitypatch.updateEntityState();
			this.nextAnimation = nextAnimation;
		}
	}
	
	public void playAnimationInstant(DynamicAnimation nextAnimation, LivingEntityPatch<?> entitypatch) {
		this.animationPlayer.getAnimation().end(entitypatch, nextAnimation, this.animationPlayer.isEnd());
		this.resume();
		nextAnimation.begin(entitypatch);
		nextAnimation.putOnPlayer(this.animationPlayer);
		entitypatch.updateEntityState();
		this.nextAnimation = null;
	}
	
	protected void playLivingAnimation(StaticAnimation nextAnimation, LivingEntityPatch<?> entitypatch) {
		this.animationPlayer.getAnimation().end(entitypatch, nextAnimation, this.animationPlayer.isEnd());
		this.resume();
		nextAnimation.begin(entitypatch);
		
		if (!nextAnimation.isMetaAnimation()) {
			this.concurrentLinkAnimation.acceptFrom(this.animationPlayer.getAnimation().getRealAnimation(), nextAnimation, this.animationPlayer.getElapsedTime());
			this.concurrentLinkAnimation.putOnPlayer(this.animationPlayer);
			entitypatch.updateEntityState();
			this.nextAnimation = nextAnimation;
		}
	}
	
	protected void setLinkAnimation(DynamicAnimation nextAnimation, LivingEntityPatch<?> entitypatch, Pose lastPose, float convertTimeModifier) {
		nextAnimation.setLinkAnimation(lastPose, convertTimeModifier, entitypatch, this.linkAnimation);
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
				
				if (!(this.animationPlayer.getAnimation() instanceof LinkAnimation) && !(this.nextAnimation instanceof LinkAnimation)) {
					this.nextAnimation.begin(entitypatch);
				}
				
				this.nextAnimation.putOnPlayer(this.animationPlayer);
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
	
	public Pose getEnabledPose(LivingEntityPatch<?> entitypatch, float partialTick) {
		Pose pose = this.animationPlayer.getCurrentPose(entitypatch, partialTick);
		DynamicAnimation animation = this.animationPlayer.getAnimation();
		pose.removeJointIf((entry) -> !animation.isJointEnabled(entitypatch, this.priority, entry.getKey()));
		
		return pose;
	}
	
	public void off(LivingEntityPatch<?> entitypatch) {
		if (!this.isDisabled() && !(this.animationPlayer.getAnimation() instanceof LayerOffAnimation)) {
			float convertTime = entitypatch.getClientAnimator().baseLayer.animationPlayer.getAnimation().getConvertTime();
			setLayerOffAnimation(this.animationPlayer.getAnimation(), this.getEnabledPose(entitypatch, 1.0F), this.layerOffAnimation, convertTime);
			this.playAnimationInstant(this.layerOffAnimation, entitypatch);
		}
	}
	
	public static void setLayerOffAnimation(DynamicAnimation currentAnimation, Pose currentPose, LayerOffAnimation offAnimation, float convertTime) {
		offAnimation.setLastAnimation(currentAnimation.getRealAnimation());
		offAnimation.setLastPose(currentPose);
		offAnimation.setTotalTime(convertTime);
	}
	
	@Override
	public String toString() {
		return (this.isBaseLayer() ? "" : this.priority) + (this.isBaseLayer() ? " Base Layer : " : " Composite Layer : ") + this.animationPlayer.getAnimation() +" "+ this.animationPlayer.getElapsedTime();
	}
	
	public static class BaseLayer extends Layer {
		protected Map<Layer.Priority, Layer> compositeLayers = Maps.newLinkedHashMap();
		protected Layer.Priority baseLayerPriority;
		
		public BaseLayer(Layer.Priority priority) {
			super(priority);
			this.compositeLayers.computeIfAbsent(Priority.LOWEST, Layer::new);
			this.compositeLayers.computeIfAbsent(Priority.MIDDLE, Layer::new);
			this.compositeLayers.computeIfAbsent(Priority.HIGHEST, Layer::new);
			this.baseLayerPriority = Priority.LOWEST;
		}
		
		@Override
		public void playAnimation(StaticAnimation nextAnimation, LivingEntityPatch<?> entitypatch, float convertTimeModifier) {
			Priority priority = nextAnimation.getPriority();
			this.baseLayerPriority = priority;
			this.offCompositeLayerLowerThan(entitypatch, nextAnimation);
			super.playAnimation(nextAnimation, entitypatch, convertTimeModifier);
		}
		
		@Override
		protected void playLivingAnimation(StaticAnimation nextAnimation, LivingEntityPatch<?> entitypatch) {
			this.animationPlayer.getAnimation().end(entitypatch, nextAnimation, this.animationPlayer.isEnd());
			this.resume();
			nextAnimation.begin(entitypatch);
			
			if (!nextAnimation.isMetaAnimation()) {
				this.concurrentLinkAnimation.acceptFrom(this.animationPlayer.getAnimation().getRealAnimation(), nextAnimation, this.animationPlayer.getElapsedTime());
				this.concurrentLinkAnimation.putOnPlayer(this.animationPlayer);
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
			Animations.DUMMY_ANIMATION.putOnPlayer(layer.animationPlayer);
		}
		
		public Layer getLayer(Priority priority) {
			return this.compositeLayers.get(priority);
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
	
	public static enum LayerType {
		BASE_LAYER, COMPOSITE_LAYER;
	}
	
	public static enum Priority {
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