package yesman.epicfight.api.animation.types;

import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.EntityDimensions;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.client.CPRotatePlayerYaw;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class DodgeAnimation extends ActionAnimation {
	private final EntityDimensions size;
	
	public DodgeAnimation(float convertTime, String path, float width, float height, Model model) {
		this(convertTime, 0.0F, path, width, height, model);
	}
	
	public DodgeAnimation(float convertTime, float delayTime, String path, float width, float height, Model model) {
		super(convertTime, delayTime, path, model);
		
		if (width > 0.0F || height > 0.0F) {
			this.size = EntityDimensions.scalable(width, height);
		} else {
			this.size = null;
		}
		
		this.stateSpectrumBlueprint.clear()
			.newTimePair(0.0F, 10.0F)
			.addState(EntityState.TURNING_LOCKED, true)
			.addState(EntityState.MOVEMENT_LOCKED, true)
			.addState(EntityState.CAN_BASIC_ATTACK, false)
			.addState(EntityState.CAN_SKILL_EXECUTION, false)
			.addState(EntityState.INACTION, true)
			.newTimePair(delayTime, Float.MAX_VALUE)
			.addState(EntityState.INVULNERABILITY_PREDICATE, (damagesource) -> {
				if (damagesource instanceof EntityDamageSource && !damagesource.isExplosion() && !damagesource.isMagic() && !damagesource.isBypassInvul()) {
					return true;
				}
				return false;
			});
		
		this.addProperty(ActionAnimationProperty.AFFECT_SPEED, true);
	}
	
	@Override
	public void tick(LivingEntityPatch<?> entitypatch) {
		super.tick(entitypatch);
		
		if (this.size != null) {
			entitypatch.resetSize(this.size);
		}
	}
	
	@Override
	public void end(LivingEntityPatch<?> entitypatch, boolean isEnd) {
		super.end(entitypatch, isEnd);
		
		if (this.size != null) {
			entitypatch.getOriginal().refreshDimensions();
		}
		
		if (entitypatch.isLogicalClient() && entitypatch instanceof LocalPlayerPatch) {
			((LocalPlayerPatch)entitypatch).changeYaw(0);
			EpicFightNetworkManager.sendToServer(new CPRotatePlayerYaw(0));
		}
	}
}