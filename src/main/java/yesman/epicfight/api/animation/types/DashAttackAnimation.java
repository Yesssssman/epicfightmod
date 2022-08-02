package yesman.epicfight.api.animation.types;

import javax.annotation.Nullable;

import net.minecraft.world.entity.Entity;
import yesman.epicfight.api.animation.property.AnimationProperty.AttackAnimationProperty;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.model.Model;
import yesman.epicfight.api.utils.ExtendedDamageSource;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class DashAttackAnimation extends AttackAnimation {
	public DashAttackAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, @Nullable Collider collider, String index, String path, Model model) {
		super(convertTime, antic, preDelay, contact, recovery, collider, index, path, model);
		this.addProperty(AttackAnimationProperty.ROTATE_X, true);
		this.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true);
		this.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.5F);
	}
	
	public DashAttackAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, @Nullable Collider collider, String index, String path, boolean noDirectionAttack, Model model) {
		super(convertTime, antic, preDelay, contact, recovery, collider, index, path, model);
		this.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true);
		this.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.5F);
	}
	
	@Override
	public ExtendedDamageSource getExtendedDamageSource(LivingEntityPatch<?> entitypatch, Entity target, Phase phase) {
		ExtendedDamageSource extSource = super.getExtendedDamageSource(entitypatch, target, phase);
		extSource.setImpact(extSource.getImpact() * 1.333F);
		return extSource;
	}
	
	@Override
	public boolean isBasicAttackAnimation() {
		return true;
	}
}