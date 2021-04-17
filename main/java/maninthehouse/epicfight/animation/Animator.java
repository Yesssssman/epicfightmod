package maninthehouse.epicfight.animation;

import maninthehouse.epicfight.animation.types.StaticAnimation;
import maninthehouse.epicfight.capabilities.entity.LivingData;
import maninthehouse.epicfight.gamedata.Animations;

public abstract class Animator {
	protected LivingData<?> entitydata;
	
	public abstract void playAnimation(int id, float modifyTime);
	public abstract void playAnimation(StaticAnimation nextAnimation, float modifyTime);
	public abstract void vacateCurrentPlay();
	public abstract void update();
	public abstract void onEntityDeath();
	public abstract AnimationPlayer getPlayer();
	/**
	 * Use this if you don't know what layer you want to get(only effective in client animator)
	 */
	public abstract AnimationPlayer getPlayerFor(StaticAnimation animation);
	
	public void playDeathAnimation() {
		this.playAnimation(Animations.BIPED_DEATH, 0);
	}
}