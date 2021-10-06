package yesman.epicfight.client.animation;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.animation.AnimationPlayer;
import yesman.epicfight.animation.Pose;
import yesman.epicfight.animation.types.DynamicAnimation;
import yesman.epicfight.animation.types.LayerOffAnimation;
import yesman.epicfight.animation.types.LinkAnimation;
import yesman.epicfight.capabilities.entity.LivingData;
import yesman.epicfight.collada.AnimationDataExtractor;

@OnlyIn(Dist.CLIENT)
public class Layer {
	public final AnimationPlayer animationPlayer;
	protected DynamicAnimation nextAnimation;
	protected LinkAnimation linkAnimationStorage;
	protected LayerOffAnimation layerOffAnimation;
	protected Priority priority;
	protected boolean paused;
	
	public Layer(Priority priority) {
		this.priority = priority;
		this.animationPlayer = new AnimationPlayer();
		this.linkAnimationStorage = new LinkAnimation();
		this.layerOffAnimation = new LayerOffAnimation(priority);
	}
	
	public void playAnimation(DynamicAnimation nextAnimation, LivingData<?> entitydata, float modifyTime) {
		this.resume();
		this.animationPlayer.getPlay().onFinish(entitydata, this.animationPlayer.isEnd());
		nextAnimation.onActivate(entitydata);
		if (!nextAnimation.isMetaAnimation()) {
			this.setLinkAnimation(nextAnimation, entitydata, modifyTime);
			this.linkAnimationStorage.putOnPlayer(this.animationPlayer);
			this.nextAnimation = nextAnimation;
		}
	}
	
	public void playAnimation(DynamicAnimation nextAnimation, LivingData<?> entitydata) {
		this.resume();
		this.animationPlayer.getPlay().onFinish(entitydata, this.animationPlayer.isEnd());
		nextAnimation.onActivate(entitydata);
		nextAnimation.putOnPlayer(this.animationPlayer);
		this.nextAnimation = null;
	}
	
	public void load(DynamicAnimation nextAnimation, LivingData<?> entitydata, float modifyTime) {
		this.setLinkAnimation(nextAnimation, entitydata, modifyTime);
	}
	
	public void play(DynamicAnimation nextAnimation, LivingData<?> entitydata, float modifyTime) {
		this.resume();
		this.animationPlayer.getPlay().onFinish(entitydata, this.animationPlayer.isEnd());
		nextAnimation.onActivate(entitydata);
		if (!nextAnimation.isMetaAnimation()) {
			this.linkAnimationStorage.putOnPlayer(this.animationPlayer);
			this.nextAnimation = nextAnimation;
		}
	}
	
	public void setLinkAnimation(DynamicAnimation nextAnimation, LivingData<?> entitydata, float timeModifier) {
		Pose currentPose = entitydata.getClientAnimator().getComposedLayerPose(Minecraft.getInstance().getRenderPartialTicks());
		nextAnimation.setLinkAnimation(currentPose, timeModifier, entitydata, this.linkAnimationStorage);
	}
	
	public void update(LivingData<?> entitydata) {
		if (this.paused) {
			this.animationPlayer.setElapsedTime(this.animationPlayer.getElapsedTime());
			return;
		}
		if (this.animationPlayer.isEmpty()) {
			return;
		}
		
		this.animationPlayer.update(entitydata);
		this.animationPlayer.getPlay().onUpdate(entitydata);
		if (this.animationPlayer.isEnd()) {
			if (this.nextAnimation != null) {
				float exceedTime = this.animationPlayer.getExceedTime();
				this.animationPlayer.getPlay().onFinish(entitydata, true);
				this.nextAnimation.putOnPlayer(this.animationPlayer);
				this.animationPlayer.setElapsedTime(this.animationPlayer.getElapsedTime() + exceedTime);
				this.nextAnimation = null;
			} else {
				if (this.animationPlayer.getPlay() instanceof LayerOffAnimation) {
					this.animationPlayer.getPlay().onFinish(entitydata, true);
				} else {
					this.off(entitydata);
				}
			}
		}
	}
	
	public void pause() {
		this.paused = true;
	}
	
	public void resume() {
		this.paused = false;
	}
	
	public void off(LivingData<?> entitydata) {
		if (this.priority != Priority.LOWEST && !this.animationPlayer.isEmpty()) {
			AnimationDataExtractor.extractLayerOffAnimation(this.animationPlayer.getPlay(),this.animationPlayer.getCurrentPose(
					entitydata, Minecraft.getInstance().getRenderPartialTicks()), this.layerOffAnimation);
			this.playAnimation(this.layerOffAnimation, entitydata);
		}
	}
	
	public static enum Priority {
		/**
		 * LOWEST: LivingMotions
		 * MIDDLE: OverridenLivingMotions
		 * HIGHEST: Attack & Skill Motions
		 */
		LOWEST(0), MIDDLE(1), HIGHEST(2);
		
		int priority;
		
		Priority(int priority) {
			this.priority = priority;
		}
		
		public Priority lower() {
			return Priority.values()[Math.max(this.priority - 1, 0)];
		}
	}
}