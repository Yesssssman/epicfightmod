package maninhouse.epicfight.animation.types;

import javax.annotation.Nullable;

import maninhouse.epicfight.animation.property.Property.AttackAnimationProperty;
import maninhouse.epicfight.animation.property.Property.AttackPhaseProperty;
import maninhouse.epicfight.animation.types.EntityState.Translation;
import maninhouse.epicfight.capabilities.entity.LivingData;
import maninhouse.epicfight.physics.Collider;
import maninhouse.epicfight.utils.math.ValueCorrector;
import net.minecraft.entity.Entity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.server.ServerWorld;

public class AirSlashAnimation extends AttackAnimation {
	public AirSlashAnimation(int id, float convertTime, float antic, float contact, float recovery, @Nullable Collider collider, String index, String path) {
		this(id, convertTime, antic, antic, contact, recovery, true, collider, index, path);
	}
	
	public AirSlashAnimation(int id, float convertTime, float antic, float preDelay, float contact, float recovery, boolean directional, @Nullable Collider collider, String index, String path) {
		super(id, convertTime, antic, preDelay, contact, recovery, false, true, collider, index, path);
		if (directional) {
			this.addProperty(AttackAnimationProperty.DIRECTIONAL, true);
		}
		this.addProperty(AttackPhaseProperty.DAMAGE, ValueCorrector.getMultiplier(0.5F));
		this.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.5F);
	}
	
	@Override
	protected void spawnHitParticle(ServerWorld world, LivingData<?> attacker, Entity hit, Phase phase) {
		super.spawnHitParticle(world, attacker, hit, phase);
		world.spawnParticle(ParticleTypes.CRIT, hit.getPosX(), hit.getPosY(), hit.getPosZ(), 15, 0.0D, 0.0D, 0.0D, 1.0D);
	}
	
	@Override
	public EntityState getState(float time) {
		return EntityState.translation(super.getState(time), Translation.TO_LOCKED);
	}
	
	@Override
	public boolean isBasicAttackAnimation() {
		return true;
	}
}