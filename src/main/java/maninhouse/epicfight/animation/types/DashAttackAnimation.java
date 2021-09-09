package maninhouse.epicfight.animation.types;

import javax.annotation.Nullable;

import maninhouse.epicfight.animation.property.Property.AttackAnimationProperty;
import maninhouse.epicfight.capabilities.entity.LivingData;
import maninhouse.epicfight.physics.Collider;
import maninhouse.epicfight.utils.game.IExtendedDamageSource;
import net.minecraft.entity.Entity;

public class DashAttackAnimation extends AttackAnimation {
	public DashAttackAnimation(int id, float convertTime, float antic, float preDelay, float contact, float recovery, @Nullable Collider collider, String index,
			String path) {
		super(id, convertTime, antic, preDelay, contact, recovery, false, collider, index, path);
		this.addProperty(AttackAnimationProperty.DIRECTIONAL, true);
		this.addProperty(AttackAnimationProperty.FIXED_MOVE_DISTANCE, true);
		this.addProperty(AttackAnimationProperty.ATTACK_SPEED_FACTOR, 0.5F);
	}
	
	public DashAttackAnimation(int id, float convertTime, float antic, float preDelay, float contact, float recovery, @Nullable Collider collider, String index,
			String path, boolean noDirectionAttack) {
		super(id, convertTime, antic, preDelay, contact, recovery, false, collider, index, path);
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