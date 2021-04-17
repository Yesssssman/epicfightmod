package maninthehouse.epicfight.client.animation;

import maninthehouse.epicfight.animation.AnimationPlayer;
import maninthehouse.epicfight.animation.Pose;
import maninthehouse.epicfight.animation.types.DynamicAnimation;
import maninthehouse.epicfight.animation.types.LinkAnimation;
import maninthehouse.epicfight.animation.types.StaticAnimation;
import maninthehouse.epicfight.capabilities.entity.LivingData;
import maninthehouse.epicfight.main.GameConstants;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BaseLayer {
	public final AnimationPlayer animationPlayer;
	protected DynamicAnimation nextPlaying;
	protected LinkAnimation linkAnimation = new LinkAnimation();
	
	public boolean pause;
	
	public BaseLayer(DynamicAnimation animation) {
		this.animationPlayer = new AnimationPlayer(animation);
		this.nextPlaying = new StaticAnimation();
	}

	public void playAnimation(DynamicAnimation nextAnimation, LivingData<?> entitydata, float modifyTime) {
		this.animationPlayer.getPlay().onFinish(entitydata, this.animationPlayer.isEnd());
		nextAnimation.onActivate(entitydata);
		setLinkAnimation(nextAnimation, entitydata, modifyTime);
		this.linkAnimation.putOnPlayer(this.animationPlayer);
		this.nextPlaying = nextAnimation;
	}
	
	public void playAnimation(DynamicAnimation nextAnimation, LivingData<?> entitydata) {
		this.animationPlayer.getPlay().onFinish(entitydata, this.animationPlayer.isEnd());
		nextAnimation.onActivate(entitydata);
		nextAnimation.putOnPlayer(this.animationPlayer);
		this.nextPlaying = null;
	}
	
	public void setLinkAnimation(DynamicAnimation nextAnimation, LivingData<?> entitydata, float timeModifier) {
		Pose currentPose = this.animationPlayer.getCurrentPose(entitydata, Minecraft.getMinecraft().getRenderPartialTicks());
		nextAnimation.getLinkAnimation(currentPose, timeModifier, entitydata, this.linkAnimation);
	}
	
	public void update(LivingData<?> entitydata, boolean reversePlay) {
		if (pause) {
			this.animationPlayer.setElapsedTime(this.animationPlayer.getElapsedTime());
			return;
		}
		
		float frameTime = GameConstants.A_TICK * this.animationPlayer.getPlay().getPlaySpeed(entitydata);
		frameTime = reversePlay ? -frameTime : frameTime;
		
		this.animationPlayer.update(frameTime);
		this.animationPlayer.getPlay().onUpdate(entitydata);
		
		if (this.animationPlayer.isEnd()) {
			if (nextPlaying != null) {
				float exceedTime = this.animationPlayer.getExceedTime();
				this.animationPlayer.getPlay().onFinish(entitydata, true);
				this.nextPlaying.putOnPlayer(this.animationPlayer);
				this.animationPlayer.setElapsedTime(this.animationPlayer.getElapsedTime() + exceedTime);
				this.nextPlaying = null;
			}
		}
	}
	
	public void clear(LivingData<?> entitydata) {
		if (animationPlayer.getPlay() != null) {
			animationPlayer.getPlay().onFinish(entitydata, animationPlayer.isEnd());
		}

		if (nextPlaying != null) {
			nextPlaying.onFinish(entitydata, false);
		}
	}
}