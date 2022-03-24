package yesman.epicfight.api.client.animation;

import java.util.Arrays;
import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.LayerOffAnimation;
import yesman.epicfight.api.animation.types.LinkAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class Layer {
	public final AnimationPlayer animationPlayer;
	protected DynamicAnimation nextAnimation;
	protected LinkAnimation linkAnimationStorage;
	protected LayerOffAnimation layerOffAnimation;
	protected boolean disabled;
	protected boolean paused;
	
	public Layer(Priority priority) {
		this.animationPlayer = new AnimationPlayer();
		this.linkAnimationStorage = new LinkAnimation();
		this.layerOffAnimation = new LayerOffAnimation(priority);
		this.disabled = true;
	}
	
	public void playAnimation(StaticAnimation nextAnimation, LivingEntityPatch<?> entitypatch, float convertTimeModifier) {
		Pose lastPose = entitypatch.getAnimator().getPose(1.0F);
		this.animationPlayer.getPlay().end(entitypatch, this.animationPlayer.isEnd());
		this.resume();
		nextAnimation.begin(entitypatch);
		
		if (!nextAnimation.isMetaAnimation()) {
			this.setLinkAnimation(nextAnimation, entitypatch, lastPose, convertTimeModifier);
			this.linkAnimationStorage.putOnPlayer(this.animationPlayer);
			this.nextAnimation = nextAnimation;
		}
	}
	
	public void playAnimation(DynamicAnimation nextAnimation, LivingEntityPatch<?> entitypatch) {
		this.animationPlayer.getPlay().end(entitypatch, this.animationPlayer.isEnd());
		this.resume();
		nextAnimation.begin(entitypatch);
		nextAnimation.putOnPlayer(this.animationPlayer);
		this.nextAnimation = null;
	}
	
	public void setLinkAnimation(DynamicAnimation nextAnimation, LivingEntityPatch<?> entitypatch, Pose lastPose, float convertTimeModifier) {
		nextAnimation.setLinkAnimation(lastPose, convertTimeModifier, entitypatch, this.linkAnimationStorage);
	}
	
	public void update(LivingEntityPatch<?> entitypatch) {
		if (this.paused) {
			this.animationPlayer.setElapsedTime(this.animationPlayer.getElapsedTime());
			return;
		}
		
		if (this.animationPlayer.isEmpty()) {
			return;
		}
		
		this.animationPlayer.update(entitypatch);
		this.animationPlayer.getPlay().tick(entitypatch);
		
		if (this.animationPlayer.isEnd()) {
			if (this.nextAnimation != null) {
				this.animationPlayer.getPlay().end(entitypatch, true);
				
				if (!(this.animationPlayer.getPlay() instanceof LinkAnimation) && !(this.nextAnimation instanceof LinkAnimation)) {
					this.nextAnimation.begin(entitypatch);
				}
				
				this.nextAnimation.putOnPlayer(this.animationPlayer);
				this.nextAnimation = null;
			} else {
				if (this.animationPlayer.getPlay() instanceof LayerOffAnimation) {
					this.animationPlayer.getPlay().end(entitypatch, true);
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
	
	public void off(LivingEntityPatch<?> entitypatch) {
		if (!this.isDisabled() && !(this.animationPlayer.getPlay() instanceof LayerOffAnimation)) {
			float convertTime = entitypatch.getClientAnimator().baseLayer.animationPlayer.getPlay().getConvertTime();
			setLayerOffAnimation(this.animationPlayer.getPlay(), this.animationPlayer.getCurrentPose(entitypatch, 1.0F), this.layerOffAnimation, convertTime);
			this.playAnimation(this.layerOffAnimation, entitypatch);
		}
	}
	
	public static void setLayerOffAnimation(DynamicAnimation currentAnimation, Pose currentPose, LayerOffAnimation offAnimation, float convertTime) {
		offAnimation.setLastAnimation(currentAnimation.getRealAnimation());
		offAnimation.setLastPose(currentPose);
		offAnimation.setTotalTime(convertTime);
	}
	
	public static class BaseLayer extends Layer {
		protected Map<Layer.Priority, Layer> compositeLayers = Maps.newHashMap();
		protected Layer.Priority priority;
		
		public BaseLayer(Priority priority) {
			super(priority);
			this.compositeLayers.computeIfAbsent(Priority.HIGHEST, Layer::new);
			this.compositeLayers.computeIfAbsent(Priority.MIDDLE, Layer::new);
			this.compositeLayers.put(Priority.LOWEST, this);
			this.priority = Priority.LOWEST;
		}
		
		@Override
		public void playAnimation(StaticAnimation nextAnimation, LivingEntityPatch<?> entitypatch, float convertTimeModifier) {
			Priority priority = nextAnimation.getPriority();
			this.priority = priority;
			this.offCompositeLayerLowerThan(entitypatch, priority);
			super.playAnimation(nextAnimation, entitypatch, convertTimeModifier);
		}
		
		@Override
		public void update(LivingEntityPatch<?> entitypatch) {
			super.update(entitypatch);
			
			for (Layer layer : this.compositeLayers.values()) {
				if (layer != this) {
					layer.update(entitypatch);
				}
			}
		}
		
		public void offCompositeLayerLowerThan(LivingEntityPatch<?> entitypatch, Priority priority) {
			for (Priority p : priority.notUpperThan()) {
				this.compositeLayers.get(p).off(entitypatch);
			}
		}
		
		public void disableLayer(Priority priority) {
			Layer layer = this.compositeLayers.get(priority);
			layer.disabled = true;
			layer.animationPlayer.setPlayAnimation(Animations.DUMMY_ANIMATION);
		}
		
		@Override
		public void off(LivingEntityPatch<?> entitypatch) {
			
		}
		
		@Override
		protected boolean isDisabled() {
			return false;
		}
	}
	
	public static enum Priority {
		/**
		 * LOWEST: Most living motions
		 * MIDDLE: Composite living motions
		 * HIGHEST: Attack or skill motions
		 */
		LOWEST, MIDDLE, HIGHEST;
		
		public Priority[] lowers() {
			return Arrays.copyOfRange(Priority.values(), 0, this.ordinal());
		}
		
		public Priority[] uppers() {
			return Arrays.copyOfRange(Priority.values(), this.ordinal() + 1, 3);
		}
		
		public Priority[] notUpperThan() {
			return Arrays.copyOfRange(Priority.values(), 0, this.ordinal() + 1);
		}
	}
}