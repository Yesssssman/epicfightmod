package yesman.epicfight.api.animation.types;

import yesman.epicfight.api.model.Armature;
import yesman.epicfight.config.ConfigurationIngame;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class ReboundAnimation extends AimAnimation {
	public ReboundAnimation(float convertTime, boolean repeatPlay, String path1, String path2, String path3, String path4, Armature armature) {
		super(convertTime, repeatPlay, path1, path2, path3, path4, armature);
		
		this.stateSpectrumBlueprint.clear()
			.newTimePair(0.0F, Float.MAX_VALUE)
			.addState(EntityState.TURNING_LOCKED, true)
			.addState(EntityState.MOVEMENT_LOCKED, true)
			.addState(EntityState.CAN_BASIC_ATTACK, false)
			.addState(EntityState.INACTION, true);
	}
	
	public ReboundAnimation(boolean repeatPlay, String path1, String path2, String path3, String path4, Armature armature) {
		this(ConfigurationIngame.GENERAL_ANIMATION_CONVERT_TIME, repeatPlay, path1, path2, path3, path4, armature);
	}
	
	@Override
	public void tick(LivingEntityPatch<?> entitypatch) {
		;
	}
	
	@Override
	public boolean isReboundAnimation() {
		return true;
	}
}