package yesman.epicfight.animation.types;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import yesman.epicfight.animation.property.Property.AttackAnimationProperty;
import yesman.epicfight.capabilities.entity.LivingData;
import yesman.epicfight.model.Model;
import yesman.epicfight.physics.Collider;
import yesman.epicfight.utils.game.IExtendedDamageSource;

public class DashAttackAnimation extends AttackAnimation {
	public DashAttackAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, @Nullable Collider collider, String index, String path, Model model) {
		super(convertTime, antic, preDelay, contact, recovery, false, collider, index, path, model);
		this.addProperty(AttackAnimationProperty.DIRECTIONAL, true);
		this.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true);
		this.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.5F);
	}
	
	public DashAttackAnimation(float convertTime, float antic, float preDelay, float contact, float recovery, @Nullable Collider collider, String index, String path, boolean noDirectionAttack, Model model) {
		super(convertTime, antic, preDelay, contact, recovery, false, collider, index, path, model);
		this.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true);
		this.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.5F);
	}
	
	@Override
	public IExtendedDamageSource getDamageSourceExt(LivingData<?> entitydata, Entity target, Phase phase) {
		IExtendedDamageSource extSource = super.getDamageSourceExt(entitydata, target, phase);
		extSource.setImpact(extSource.getImpact() * 1.333F);
		return extSource;
	}
	
	@Override
	public boolean isBasicAttackAnimation() {
		return true;
	}
}