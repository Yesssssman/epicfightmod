package yesman.epicfight.api.utils;

import net.minecraft.entity.Entity;
import net.minecraft.util.IndirectEntityDamageSource;
import net.minecraft.util.math.vector.Vector3d;

public class IndirectEpicFightDamageSource extends IndirectEntityDamageSource implements ExtendedDamageSource {
	private float impact;
	private float armorNegation;
	private boolean finisher;
	private StunType stunType;
	private Vector3d projectileInitialPosition;
	
	public IndirectEpicFightDamageSource(String damageTypeIn, Entity owner, Entity projectile, StunType stunType) {
		super(damageTypeIn, projectile, owner);
		this.stunType = stunType;
	}
	
	@Override
	public void setImpact(float amount) {
		this.impact = amount;
	}

	@Override
	public void setArmorNegation(float amount) {
		this.armorNegation = amount;
	}

	@Override
	public void setStunType(StunType stunType) {
		this.stunType = stunType;
	}
	
	@Override
	public void setInitialPosition(Vector3d initialPosition) {
		this.projectileInitialPosition = initialPosition;
	}
	
	@Override
	public void setFinisher(boolean flag) {
		this.finisher = flag;
	}
	
	@Override
	public float getImpact() {
		return impact;
	}
	
	@Override
	public float getArmorNegation() {
		return armorNegation;
	}
	
	@Override
	public StunType getStunType() {
		return stunType;
	}
	
	@Override
	public Vector3d getSourcePosition() {
		return this.projectileInitialPosition != null ? this.projectileInitialPosition : super.getSourcePosition();
	}
	
	@Override
	public Entity getOwner() {
		return super.getEntity();
	}

	@Override
	public String getType() {
		return super.msgId;
	}

	@Override
	public boolean isBasicAttack() {
		return false;
	}
	
	@Override
	public boolean isFinisher() {
		return this.finisher;
	}
	
	@Override
	public int getAnimationId() {
		return -1;
	}
}