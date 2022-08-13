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
		this.animationPlayer.getAnimation().end(this.entitypatch, this.animationPlayer.isEnd());
		nextAnimation.begin(this.entitypatch);
		nextAnimation.setLinkAnimation(nextAnimation.getPoseByTime(this.entitypatch, 0.0F, 0.0F), modifyTime, this.entitypatch, this.linkAnimation);
		this.linkAnimation.putOnPlayer(this.animationPlayer);
		this.entitypatch.updateEntityState();
		this.nextPlaying = nextAnimation;
	}
	
	@Override
	public void playAnimationInstantly(StaticAnimation nextAnimation) {
		this.pause = false;
		this.animationPlayer.getAnimation().end(this.entitypatch, this.animationPlayer.isEnd());
		nextAnimation.begin(this.entitypatch);
		nextAnimation.putOnPlayer(this.animationPlayer);
		this.entitypatch.updateEntityState();
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
	public void poseTick() {
		this.prevPose = this.currentPose;
		this.currentPose = this.animationPlayer.getCurrentPose(this.entitypatch, 1.0F);
	}
	
	@Override
	public void tick() {
		if (this.pause) {
			this.entitypatch.updateEntityState();
			return;
		}
		
		this.animationPlayer.tick(this.entitypatch);
		this.poseTick();
		this.entitypatch.updateEntityState();
		this.animationPlayer.getAnimation().tick(this.entitypatch);
		
		if (this.animationPlayer.isEnd()) {
			this.animationPlayer.getAnimation().end(this.entitypatch, true);
			
			if (this.nextPlaying == null) {
				Animations.DUMMY_ANIMATION.putOnPlayer(this.animationPlayer);
				this.pause = true;
			} else {
				if (!(this.animationPlayer.getAnimation() instanceof LinkAnimation) && !(this.nextPlaying instanceof LinkAnimation)) {
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
		return this.animationPlayer.getAnimation().getState(this.animationPlayer.getElapsedTime());
	}
}