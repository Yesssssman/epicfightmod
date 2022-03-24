package yesman.epicfight.api.animation.types;

import net.minecraft.world.entity.EntityDimensions;
import yesman.epicfight.api.animation.property.Property.ActionAnimationProperty;
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
			((LocalPlayerPatch) entitypatch).changeYaw(0);
			EpicFightNetworkManager.sendToServer(new CPRotatePlayerYaw(0));
		}
	}
	
	@Override
	public EntityState getState(float time) {
		if (time < this.delayTime) {
			return EntityState.PRE_DELAY;
		} else {
			return EntityState.DODGE;
		}
	}
}