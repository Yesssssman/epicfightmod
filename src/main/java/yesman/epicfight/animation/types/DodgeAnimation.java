package yesman.epicfight.animation.types;

import net.minecraft.entity.EntitySize;
import yesman.epicfight.capabilities.entity.LivingData;
import yesman.epicfight.client.capabilites.player.ClientPlayerData;
import yesman.epicfight.model.Model;
import yesman.epicfight.network.ModNetworkManager;
import yesman.epicfight.network.client.CTSRotatePlayerYaw;

public class DodgeAnimation extends ActionAnimation {
	private final EntitySize size;
	
	public DodgeAnimation(float convertTime, boolean affectVelocity, String path, float width, float height, Model model) {
		this(convertTime, 0.0F, affectVelocity, path, width, height, model);
	}
	
	public DodgeAnimation(float convertTime, float delayTime, boolean affectVelocity, String path, float width, float height, Model model) {
		super(convertTime, delayTime, affectVelocity, false, path, model);
		if (width > 0.0F || height > 0.0F) {
			this.size = EntitySize.flexible(width, height);
		} else {
			this.size = null;
		}
	}
	
	@Override
	public void onUpdate(LivingData<?> entitydata) {
		super.onUpdate(entitydata);
		if (this.size != null) {
			entitydata.resetSize(size);
		}
	}
	
	@Override
	public void onFinish(LivingData<?> entitydata, boolean isEnd) {
		super.onFinish(entitydata, isEnd);
		if (this.size != null) {
			entitydata.getOriginalEntity().recalculateSize();
		}
		if (entitydata.isRemote() && entitydata instanceof ClientPlayerData) {
			((ClientPlayerData) entitydata).changeYaw(0);
			ModNetworkManager.sendToServer(new CTSRotatePlayerYaw(0));
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