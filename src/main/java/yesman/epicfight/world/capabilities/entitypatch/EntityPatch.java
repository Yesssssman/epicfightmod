package yesman.epicfight.world.capabilities.entitypatch;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import yesman.epicfight.api.utils.math.MathUtils;
import yesman.epicfight.api.utils.math.OpenMatrix4f;

public abstract class EntityPatch<T extends Entity> {
	protected T original;
	protected boolean initialized = false;
	
	public abstract void tick(LivingUpdateEvent event);
	protected abstract void clientTick(LivingUpdateEvent event);
	protected abstract void serverTick(LivingUpdateEvent event);
	
	public void postInit() {
	}

	public void onConstructed(T entityIn) {
		this.original = entityIn;
	}
	
	public void onJoinWorld(T entityIn, EntityJoinWorldEvent event) {
		this.initialized = true;
	}

	public T getOriginal() {
		return this.original;
	}
	
	public boolean isInitialized() {
		return this.initialized;
	}
	
	public boolean isLogicalClient() {
		return this.original.level.isClientSide();
	}
	
	public OpenMatrix4f getMatrix(float partialTicks) {
		return MathUtils.getModelMatrixIntegral(0, 0, 0, 0, 0, 0, this.original.xRotO, this.original.getXRot(), this.original.yRotO, this.original.getYRot(), partialTicks, 1, 1, 1);
	}
	
	public double getAngleTo(Entity entityIn) {
		Vec3 a = this.original.getLookAngle();
		Vec3 b = new Vec3(entityIn.getX() - this.original.getX(), entityIn.getY() - this.original.getY(), entityIn.getZ() - this.original.getZ()).normalize();
		double cos = (a.x * b.x + a.y * b.y + a.z * b.z);
		
		return Math.toDegrees(Math.acos(cos));
	}
	
	public double getAngleToHorizontal(Entity entityIn) {
		Vec3 a = this.original.getLookAngle();
		Vec3 b = new Vec3(entityIn.getX() - this.original.getX(), 0.0D, entityIn.getZ() - this.original.getZ()).normalize();
		double cos = (a.x * b.x + a.y * b.y + a.z * b.z);
		
		return Math.toDegrees(Math.acos(cos));
	}
	
	public abstract OpenMatrix4f getModelMatrix(float partialTicks);
}