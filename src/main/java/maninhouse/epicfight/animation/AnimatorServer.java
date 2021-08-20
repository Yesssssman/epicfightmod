package maninhouse.epicfight.animation;

import maninhouse.epicfight.animation.types.DynamicAnimation;
import maninhouse.epicfight.animation.types.EntityState;
import maninhouse.epicfight.animation.types.LinkAnimation;
import maninhouse.epicfight.animation.types.StaticAnimation;
import maninhouse.epicfight.capabilities.entity.LivingData;
import maninhouse.epicfight.gamedata.Animations;

public class AnimatorServer extends Animator {
	public final AnimationPlayer animationPlayer;
	protected DynamicAnimation nextPlaying;
	private LinkAnimation linkAnimation;
	public boolean pause = false;

	public AnimatorServer(LivingData<?> modEntity) {
		this.entitydata = modEntity;
		this.linkAnimation = new LinkAnimation();
		this.animationPlayer = new AnimationPlayer();
	}
	
	/** Play an animation by animation id **/
	@Override
	public void playAnimation(int id, float modifyTime) {
		this.playAnimation(Animations.findAnimationDataById(id), modifyTime);
	}
	
	/** Play an animation by animation instance **/
	@Override
	public void playAnimation(StaticAnimation nextAnimation, float modifyTime) {
		this.pause = false;
		this.animationPlayer.getPlay().onFinish(this.entitydata, this.animationPlayer.isEnd());
		nextAnimation.onActivate(this.entitydata);
		nextAnimation.getLinkAnimation(nextAnimation.getPoseByTime(this.entitydata, 0), modifyTime, this.entitydata, this.linkAnimation);
		this.linkAnimation.putOnPlayer(this.animationPlayer);
		this.nextPlaying = nextAnimation;
	}
	
	@Override
	public void reserveAnimation(StaticAnimation nextAnimation) {
		this.pause = false;
		this.nextPlaying = nextAnimation;
	}
	
	@Override
	public void update() {
		if (this.pause) {
			return;
		}
		
		this.animationPlayer.update(this.entitydata);
		this.animationPlayer.getPlay().onUpdate(this.entitydata);
		
		if (this.animationPlayer.isEnd()) {
			this.animationPlayer.getPlay().onFinish(this.entitydata, true);
			
			if (this.nextPlaying == null) {
				Animations.DUMMY_ANIMATION.putOnPlayer(this.animationPlayer);
				this.pause = true;
			} else {
				this.nextPlaying.putOnPlayer(this.animationPlayer);
				this.nextPlaying = null;
			}
		}
	}
	
	protected Pose getCurrentPose() {
		return this.animationPlayer.getCurrentPose(this.entitydata, 0.5F);
	}
	
	@Override
	public AnimationPlayer getPlayerFor(DynamicAnimation playingAnimation) {
		return this.animationPlayer;
	}
	
	@Override
	public EntityState getEntityState() {
		return this.animationPlayer.getPlay().getState(this.animationPlayer.getElapsedTime());
	}
}