package yesman.epicfight.animation;

import yesman.epicfight.animation.types.DynamicAnimation;
import yesman.epicfight.animation.types.EntityState;
import yesman.epicfight.animation.types.LinkAnimation;
import yesman.epicfight.animation.types.StaticAnimation;
import yesman.epicfight.capabilities.entity.LivingData;
import yesman.epicfight.gamedata.Animations;
import yesman.epicfight.main.EpicFightMod;

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
	public void playAnimation(int namespaceId, int id, float modifyTime) {
		this.playAnimation(EpicFightMod.getInstance().animationManager.findAnimation(namespaceId, id), modifyTime);
	}
	
	/** Play an animation by animation instance **/
	@Override
	public void playAnimation(StaticAnimation nextAnimation, float modifyTime) {
		this.pause = false;
		this.animationPlayer.getPlay().onFinish(this.entitydata, this.animationPlayer.isEnd());
		nextAnimation.onActivate(this.entitydata);
		nextAnimation.setLinkAnimation(nextAnimation.getPoseByTime(this.entitydata, 0), modifyTime, this.entitydata, this.linkAnimation);
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
	
	@Override
	public AnimationPlayer getPlayerFor(DynamicAnimation playingAnimation) {
		return this.animationPlayer;
	}
	
	@Override
	public EntityState getEntityState() {
		return this.animationPlayer.getPlay().getState(this.animationPlayer.getElapsedTime());
	}
	
	@Override
	public Pose getNextStartingPose(float startAt) {
		return this.linkAnimation.getRealAnimation().getLinkFirstPose(this.entitydata, startAt);
	}
}