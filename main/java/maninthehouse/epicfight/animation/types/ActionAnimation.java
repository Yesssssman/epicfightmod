package maninthehouse.epicfight.animation.types;

import maninthehouse.epicfight.animation.JointTransform;
import maninthehouse.epicfight.animation.Pose;
import maninthehouse.epicfight.capabilities.entity.LivingData;
import maninthehouse.epicfight.capabilities.entity.player.PlayerData;
import maninthehouse.epicfight.entity.event.EntityEventListener.Event;
import maninthehouse.epicfight.model.Armature;
import maninthehouse.epicfight.utils.math.Vec3f;
import maninthehouse.epicfight.utils.math.Vec4f;
import maninthehouse.epicfight.utils.math.VisibleMatrix4f;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayerMP;

public class ActionAnimation extends ImmovableAnimation {
	protected final boolean breakMovement;
	protected final boolean affectYCoord;
	protected float delayTime;
	
	public ActionAnimation(int id, float convertTime, boolean breakMove, boolean affectY, String path) {
		this(id, convertTime, -1.0F, breakMove, affectY, path);
	}

	public ActionAnimation(int id, float convertTime, float postDelay, boolean breakMove, boolean affectY,
			String path) {
		super(id, convertTime, path);
		this.breakMovement = breakMove;
		this.affectYCoord = affectY;
		this.delayTime = postDelay;
	}
	
	@Override
	public void onActivate(LivingData<?> entity) {
		super.onActivate(entity);
		Entity orgEntity = entity.getOriginalEntity();
		float yaw = orgEntity.rotationYaw;
		orgEntity.setRotationYawHead(yaw);
		orgEntity.setRenderYawOffset(yaw);
		
		if(breakMovement) {
			entity.getOriginalEntity().motionX = 0.0D;
			entity.getOriginalEntity().motionY = 0.0D;
			entity.getOriginalEntity().motionZ = 0.0D;
		}
		
		if(entity instanceof PlayerData) {
			((PlayerData<?>)entity).getEventListener().activateEvents(Event.ON_ACTION_SERVER_EVENT);
		}
	}
	
	@Override
	public void onUpdate(LivingData<?> entity) {
		super.onUpdate(entity);
		
		EntityLivingBase livingentity = entity.getOriginalEntity();
		
		if (entity.isRemote()) {
			if (!(livingentity instanceof EntityPlayerSP)) {
				return;
			}
		} else {
			if ((livingentity instanceof EntityPlayerMP)) {
				return;
			}
		}
		
		if (entity.isInaction()) {
			Vec3f vec3 = this.getCoordVector(entity);
			IAttributeInstance attribute = livingentity.getAttributeMap().getAttributeInstance(SharedMonsterAttributes.MOVEMENT_SPEED);
			double moveMultiplier = attribute.getAttributeValue() / attribute.getBaseValue();
			livingentity.move(MoverType.SELF, vec3.x * moveMultiplier, vec3.y, vec3.z * moveMultiplier);
		}
	}
	
	@Override
	public LivingData.EntityState getState(float time) {
		if(time < this.delayTime) {
			return LivingData.EntityState.PRE_DELAY;
		} else {
			return LivingData.EntityState.FREE;
		}
	}
	
	@Override
	public Pose getPoseByTime(LivingData<?> entity, float time) {
		Pose pose = new Pose();

		for (String jointName : jointTransforms.keySet()) {
			JointTransform jt = jointTransforms.get(jointName).getInterpolatedTransform(time);

			if (jointName.equals("Root")) {
				Vec3f vec = jt.getPosition();
				vec.x = 0.0F;
				vec.y = this.affectYCoord && vec.y > 0.0F ? 0.0F : vec.y;
				vec.z = 0.0F;
			}

			pose.putJointData(jointName, jt);
		}

		return pose;
	}
	
	@Override
	public StaticAnimation bindFull(Armature armature) {
		super.bindFull(armature);
		if(this.delayTime < 0.0F) {
			this.delayTime = this.totalTime;
		}
		
		return this;
	}
	
	protected Vec3f getCoordVector(LivingData<?> entitydata) {
		EntityLivingBase elb = entitydata.getOriginalEntity();
		JointTransform jt = jointTransforms.get("Root").getInterpolatedTransform(entitydata.getAnimator().getPlayer().getElapsedTime());
		JointTransform prevJt = jointTransforms.get("Root").getInterpolatedTransform(entitydata.getAnimator().getPlayer().getPrevElapsedTime());	
		Vec4f currentPos = new Vec4f(jt.getPosition().x, jt.getPosition().y, jt.getPosition().z, 1.0F);
		Vec4f prevPos = new Vec4f(prevJt.getPosition().x, prevJt.getPosition().y, prevJt.getPosition().z, 1.0F);
		VisibleMatrix4f mat = entitydata.getModelMatrix(1.0F);
		mat.m30 = 0;
		mat.m31 = 0;
		mat.m32 = 0;
		VisibleMatrix4f.transform(mat, currentPos, currentPos);
		VisibleMatrix4f.transform(mat, prevPos, prevPos);
		boolean hasNoGravity = entitydata.getOriginalEntity().hasNoGravity();
		float dx = prevPos.x - currentPos.x;
		float dy = (this.affectYCoord && currentPos.y > 0.0F) || hasNoGravity ? currentPos.y - prevPos.y : 0.0F;
		float dz = prevPos.z - currentPos.z;
		
		if (this.affectYCoord && currentPos.y > 0.0F && !hasNoGravity) {
			elb.motionY += 0.08D;
		}
		
		return new Vec3f(dx, dy, dz);
	}
}