package yesman.epicfight.world.damagesource;

import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.gameasset.Animations;

public class IndirectEpicFightDamageSource extends IndirectEntityDamageSource implements EpicFightDamageSource {
	private DamageSourceElements damageSourceElements;
	private Vec3 projectileInitialPosition;
	
	public IndirectEpicFightDamageSource(String damageTypeIn, Entity owner, Entity projectile, StunType stunType) {
		super(damageTypeIn, projectile, owner);
		
		this.damageSourceElements = new DamageSourceElements();
		this.damageSourceElements.stunType = stunType;
	}
	
	@Override
	public EpicFightDamageSource setInitialPosition(Vec3 initialPosition) {
		this.projectileInitialPosition = initialPosition;
		return this;
	}
	
	@Override
	public Vec3 getSourcePosition() {
		return this.projectileInitialPosition != null ? this.projectileInitialPosition : super.getSourcePosition();
	}
	
	@Override
	public Vec3 getInitialPosition() {
		return this.projectileInitialPosition;
	}
	
	@Override
	public boolean isBasicAttack() {
		return false;
	}
	
	@Override
	public StaticAnimation getAnimation() {
		return Animations.DUMMY_ANIMATION;
	}
	
	@Override
	public DamageSourceElements getDamageSourceElements() {
		return this.damageSourceElements;
	}
}