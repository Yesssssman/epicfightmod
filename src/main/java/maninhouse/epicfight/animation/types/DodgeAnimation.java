package maninhouse.epicfight.animation.types;

import maninhouse.epicfight.capabilities.entity.LivingData;
import maninhouse.epicfight.client.capabilites.entity.ClientPlayerData;
import maninhouse.epicfight.network.ModNetworkManager;
import maninhouse.epicfight.network.client.CTSRotatePlayerYaw;
import net.minecraft.entity.EntitySize;

public class DodgeAnimation extends ActionAnimation {
	private final EntitySize size;
	
	public DodgeAnimation(int id, float convertTime, boolean affectVelocity, String path, float width, float height) {
		this(id, convertTime, 0.0F, affectVelocity, path, width, height);
	}
	
	public DodgeAnimation(int id, float convertTime, float delayTime, boolean affectVelocity, String path, float width, float height) {
		super(id, convertTime, delayTime, affectVelocity, false, path);
		if(width > 0.0F || height > 0.0F) {
			this.size = EntitySize.flexible(width, height);
		} else {
			this.size = null;
		}
	}
	
	@Override
	public void onUpdate(LivingData<?> entitydata) {
		super.onUpdate(entitydata);
		if(this.size != null) {
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
		if(time < this.delayTime) {
			return EntityState.PRE_DELAY;
		} else {
			return EntityState.DODGE;
		}
	}
}