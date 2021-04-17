package maninthehouse.epicfight.animation.types.attack;

import javax.annotation.Nullable;

import maninthehouse.epicfight.capabilities.entity.LivingData;
import maninthehouse.epicfight.capabilities.entity.LivingData.EntityState;
import maninthehouse.epicfight.physics.Collider;
import maninthehouse.epicfight.utils.math.Vec3f;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;

public class TargetTraceAnimation extends AttackAnimation {
	public TargetTraceAnimation(int id, float convertTime, float antic, float preDelay, float contact, float recovery, boolean affectY,
			@Nullable Collider collider, String index, String path) {
		this(id, convertTime, antic, preDelay, contact, recovery, affectY, EnumHand.MAIN_HAND, collider, index, path);
	}
	
	public TargetTraceAnimation(int id, float convertTime, float antic, float preDelay, float contact, float recovery, boolean affectY,
			EnumHand hand, @Nullable Collider collider, String index, String path) {
		this(id, convertTime, affectY, path, new Phase(antic, preDelay, contact, recovery, hand, index, collider));
	}
	
	public TargetTraceAnimation(int id, float convertTime, boolean affectY, String path, Phase... phases) {
		super(id, convertTime, affectY, path, phases);
	}
	
	@Override
	protected Vec3f getCoordVector(LivingData<?> entitydata) {
		EntityState state = this.getState(entitydata.getAnimator().getPlayer().getElapsedTime());
		Vec3f vec3 = super.getCoordVector(entitydata);
		if(state.getLevel() < 3) {
			EntityLivingBase orgEntity = entitydata.getOriginalEntity();
			EntityLivingBase target = entitydata.getAttackTarget();
			float multiplier = (orgEntity instanceof EntityPlayer) ? 2.0F : 1.0F;
			
			if (target != null) {
				float distance = Math.max(Math.min(orgEntity.getDistance(target) - orgEntity.width - target.width, multiplier), 0.0F);
				vec3.x *= distance;
				vec3.z *= distance;
			} else {
				vec3.x *= 0.5F;
				vec3.z *= 0.5F;
			}
		}
		
		return vec3;
	}
}