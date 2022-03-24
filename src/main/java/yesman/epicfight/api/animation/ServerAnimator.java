package yesman.epicfight.api.animation;

import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.api.animation.types.LinkAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class ServerAnimator extends Animator {
	public static Animator getAnimator(LivingEntityPatch<?> entitypatch) {
		return new ServerAnimator(entitypatch);
	}
	
	public final AnimationPlayer animationPlayer;
	protected DynamicAnimation nextPlaying;
	private LinkAnimation linkAnimation;
	public boolean pause = false;
	
	public ServerAnimator(LivingEntityPatch<?> entitypatch) {
		this.entitypatch = entitypatch;
		this.linkAnimation = new LinkAnimation();
		this.animationPlayer = new AnimationPlayer();
	}
	
	/** Play an animation by animation instance **/
	@Override
	public void playAnimation(StaticAnimation nextAnimation, float modifyTime) {
		this.pause = false;
		this.animationPlayer.getPlay().end(this.entitypatch, this.animationPlayer.isEnd());
		nextAnimation.begin(this.entitypatch);
		nextAnimation.setLinkAnimation(nextAnimation.getPoseByTime(this.entitypatch, 0.0F, 0.0F), modifyTime, this.entitypatch, this.linkAnimation);
		this.linkAnimation.putOnPlayer(this.animationPlayer);
		this.nextPlaying = nextAnimation;
	}
	
	@Override
	public void playAnimationInstantly(StaticAnimation nextAnimation) {
		this.pause = false;
		this.animationPlayer.getPlay().end(this.entitypatch, this.animationPlayer.isEnd());
		nextAnimation.begin(this.entitypatch);
		nextAnimation.putOnPlayer(this.animationPlayer);
	}
	
	@Override
	public void reserveAnimation(StaticAnimation nextAnimation) {
		this.pause = false;
		this.nextPlaying = nextAnimation;
	}
	
	@Override
	public void init() {
		
	}
	
	@Override
	public void updatePose() {
		this.prevPose = this.currentPose;
		this.currentPose = this.animationPlayer.getCurrentPose(this.entitypatch, 1.0F);
	}
	
	@Override
	public void update() {
		if (this.pause) {
			return;
		}
		
		this.animationPlayer.update(this.entitypatch);
		this.updatePose();
		this.animationPlayer.getPlay().tick(this.entitypatch);
		
		if (this.animationPlayer.isEnd()) {
			this.animationPlayer.getPlay().end(this.entitypatch, true);
			
			if (this.nextPlaying == null) {
				Animations.DUMMY_ANIMATION.putOnPlayer(this.animationPlayer);
				this.pause = true;
			} else {
				if (!(this.animationPlayer.getPlay() instanceof LinkAnimation) && !(this.nextPlaying instanceof LinkAnimation)) {
					this.nextPlaying.begin(this.entitypatch);
				}
				
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
}