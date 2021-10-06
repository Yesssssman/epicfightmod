package yesman.epicfight.animation.types;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.server.ServerWorld;
import yesman.epicfight.animation.property.Property.AttackAnimationProperty;
import yesman.epicfight.animation.property.Property.AttackPhaseProperty;
import yesman.epicfight.animation.types.EntityState.Translation;
import yesman.epicfight.capabilities.entity.LivingData;
import yesman.epicfight.model.Model;
import yesman.epicfight.physics.Collider;
import yesman.epicfight.utils.math.ValueCorrector;

public class AirSlashAnimation extends AttackAnimation {
	public AirSlashAnimation(float convertTime, float antic, float contact, float recovery, @Nullable Collider collider, String index, String path, Model model) {
		this(convertTime, antic, antic, contact, recovery, true, collider, index, path, model);
	}
	
	public AirSlashAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, boolean directional, @Nullable Collider collider, String index, String path, Model model) {
		super(convertTime, antic, preDelay, contact, recovery, false, true, collider, index, path, model);
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