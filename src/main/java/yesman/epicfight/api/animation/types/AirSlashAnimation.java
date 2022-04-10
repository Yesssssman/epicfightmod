package yesman.epicfight.api.animation.types;

import javax.annotation.Nullable;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import yesman.epicfight.api.animation.property.Property.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.Property.AttackAnimationProperty;
import yesman.epicfight.api.animation.property.Property.AttackPhaseProperty;
import yesman.epicfight.api.animation.types.EntityState.Translation;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.api.utils.math.ValueCorrector;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class AirSlashAnimation extends AttackAnimation {
	public AirSlashAnimation(float convertTime, float antic, float contact, float recovery, @Nullable Collider collider, String index, String path, Model model) {
		this(convertTime, antic, antic, contact, recovery, true, collider, index, path, model);
	}
	
	public AirSlashAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, boolean directional, @Nullable Collider collider, String index, String path, Model model) {
		super(convertTime, antic, preDelay, contact, recovery, collider, index, path, model);
		if (directional) {
			this.addProperty(AttackAnimationProperty.ROTATE_X, true);
		}
		this.addProperty(AttackPhaseProperty.DAMAGE, ValueCorrector.multiplier(1.5F));
		this.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.5F);
		this.addProperty(ActionAnimationProperty.INTERRUPT_PREVIOUS_DELTA_MOVEMENT, false);
		this.addProperty(ActionAnimationProperty.MOVE_VERTICAL, true);
	}
	
	@Override
	protected void spawnHitParticle(ServerLevel world, LivingEntityPatch<?> attackerpatch, Entity hit, Phase phase) {
		super.spawnHitParticle(world, attackerpatch, hit, phase);
		world.sendParticles(ParticleTypes.CRIT, hit.getX(), hit.getY(), hit.getZ(), 15, 0.0D, 0.0D, 0.0D, 1.0D);
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