package yesman.epicfight.world.damagesource;

import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;

public class EpicFightEntityDamageSource extends EntityDamageSource implements EpicFightDamageSource {
	private DamageSourceElements damageSourceElements;
	private final StaticAnimation animation;
	private Vec3 initialPosition;
	
	public EpicFightEntityDamageSource(String msgId, Entity owner, StaticAnimation animation) {
		super(msgId, owner);
		this.animation = animation;
		this.damageSourceElements = new DamageSourceElements();
	}
	
	@Override
	public EpicFightDamageSource setInitialPosition(Vec3 initialPosition) {
		this.initialPosition = initialPosition;
		return this;
	}
	
	@Override
	public boolean isBasicAttack() {
		if (this.animation instanceof AttackAnimation) {
			return ((AttackAnimation)this.animation).isBasicAttackAnimation();
		}
		
		return false;
	}
	
	@Override
	public StaticAnimation getAnimation() {
		return this.animation;
	}
	
	@Override
	public Vec3 getSourcePosition() {
		return this.initialPosition != null ? this.initialPosition : super.getSourcePosition();
	}
	
	public Vec3 getInitialPosition() {
		return this.initialPosition;
	}
	
	@Override
	public DamageSourceElements getDamageSourceElements() {
		return this.damageSourceElements;
	}
}