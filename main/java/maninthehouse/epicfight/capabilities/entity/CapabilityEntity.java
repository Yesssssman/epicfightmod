package maninthehouse.epicfight.capabilities.entity;

import maninthehouse.epicfight.utils.math.MathUtils;
import maninthehouse.epicfight.utils.math.VisibleMatrix4f;
import net.minecraft.entity.Entity;

public abstract class CapabilityEntity<T extends Entity> {
	protected T orgEntity;
	public abstract void update();
	protected abstract void updateOnClient();
	protected abstract void updateOnServer();

	public void postInit() {
	}

	public void onEntityConstructed(T entityIn) {
		this.orgEntity = entityIn;
	}

	public void onEntityJoinWorld(T entityIn) {
		
	}

	public T getOriginalEntity() {
		return orgEntity;
	}

	public boolean isRemote() {
		return orgEntity.world.isRemote;
	}

	public void aboutToDeath() {

	}
	
	public VisibleMatrix4f getMatrix(float partialTicks) {
		return MathUtils.getModelMatrixIntegrated(0, 0, 0, 0, 0, 0, orgEntity.prevRotationPitch,
				orgEntity.rotationPitch, orgEntity.prevRotationYaw, orgEntity.rotationYaw, partialTicks, 1, 1, 1);
	}

	public abstract VisibleMatrix4f getModelMatrix(float partialTicks);
}