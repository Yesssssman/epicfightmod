package yesman.epicfight.capabilities.entity;

import net.minecraft.entity.Entity;
import yesman.epicfight.utils.math.MathUtils;
import yesman.epicfight.utils.math.OpenMatrix4f;

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
		return this.orgEntity;
	}

	public boolean isRemote() {
		return this.orgEntity.world.isRemote;
	}
	
	public OpenMatrix4f getMatrix(float partialTicks) {
		return MathUtils.getModelMatrixIntegrated(0, 0, 0, 0, 0, 0, orgEntity.prevRotationPitch,
				orgEntity.rotationPitch, orgEntity.prevRotationYaw, orgEntity.rotationYaw, partialTicks, 1, 1, 1);
	}

	public abstract OpenMatrix4f getModelMatrix(float partialTicks);
}