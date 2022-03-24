package yesman.epicfight.api.animation.types;

import yesman.epicfight.api.model.Model;
import yesman.epicfight.config.ConfigurationIngame;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class ReboundAnimation extends AimAnimation {
	public ReboundAnimation(float convertTime, boolean repeatPlay, String path1, String path2, String path3, String path4, Model model) {
		super(convertTime, repeatPlay, path1, path2, path3, path4, model);
	}
	
	public ReboundAnimation(boolean repeatPlay, String path1, String path2, String path3, String path4, Model model) {
		this(ConfigurationIngame.GENERAL_ANIMATION_CONVERT_TIME, repeatPlay, path1, path2, path3, path4, model);
	}
	
	@Override
	public void begin(LivingEntityPatch<?> entitypatch) {
		
	}
	
	@Override
	public void tick(LivingEntityPatch<?> entitypatch) {
		;
	}

	@Override
	public EntityState getState(float time) {
		return EntityState.RECOVERY;
	}
	
	@Override
	public boolean isReboundAnimation() {
		return true;
	}
}