package maninhouse.epicfight.client.animation;

import maninhouse.epicfight.animation.AnimationPlayer;
import maninhouse.epicfight.animation.Pose;
import maninhouse.epicfight.animation.types.DynamicAnimation;
import maninhouse.epicfight.animation.types.LayerOffAnimation;
import maninhouse.epicfight.animation.types.LinkAnimation;
import maninhouse.epicfight.capabilities.entity.LivingData;
import maninhouse.epicfight.collada.AnimationDataExtractor;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Layer {
	public final AnimationPlayer animationPlayer;
	protected DynamicAnimation nextAnimation;
	protected LinkAnimation linkAnimationStorage;
	protected LayerOffAnimation layerOffAnimationStorage;
	protected Priority priority;
	protected boolean paused;
	
	public Layer(Priority priority) {
		this.priority = priority;
		this.animationPlayer = new AnimationPlayer();
		this.linkAnimationStorage = new LinkAnimation();
		this.layerOffAnimationStorage = new LayerOffAnimation(priority);
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
	
	private void playAnimation(DynamicAnimation nextAnimation, LivingData<?> entitydata) {
		this.resume();
		this.animationPlayer.getPlay().onFinish(entitydata, this.animationPlayer.isEnd());
		nextAnimation.onActivate(entitydata);
		nextAnimation.putOnPlayer(this.animationPlayer);
		this.nextAnimation = null;
	}
	
	public void setLinkAnimation(DynamicAnimation nextAnimation, LivingData<?> entitydata, float timeModifier) {
		Pose currentPose = entitydata.getClientAnimator().getComposedLayerPose(Minecraft.getInstance().getRenderPartialTicks());
		nextAnimation.getLinkAnimation(currentPose, timeModifier, entitydata, this.linkAnimationStorage);
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
			AnimationDataExtractor.extractLayerOffAnimation(this.animationPlayer.getPlay(),
					this.animationPlayer.getCurrentPose(entitydata, Minecraft.getInstance().getRenderPartialTicks()), this.layerOffAnimationStorage);
			this.playAnimation(this.layerOffAnimationStorage, entitydata);
		}
	}
	
	public static enum Priority {
		/**
		 * Usages
		 * LOWEST: LivingMotions
		 * MIDDLE: OverridenLivingMotions
		 * HIGHEST: AttackMotions
		 */
		LOWEST, MIDDLE, HIGHEST;
	}
}