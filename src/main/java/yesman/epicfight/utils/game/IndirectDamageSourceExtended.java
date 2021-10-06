package yesman.epicfight.utils.game;

import net.minecraft.entity.Entity;
import net.minecraft.util.IndirectEntityDamageSource;

public class IndirectDamageSourceExtended extends IndirectEntityDamageSource implements IExtendedDamageSource {
	private float impact;
	private float armorIgnore;
	private StunType stunType;
	
	public IndirectDamageSourceExtended(String damageTypeIn, Entity source, Entity indirectEntityIn, StunType stunType) {
		super(damageTypeIn, source, indirectEntityIn);
		this.stunType = stunType;
	}
	
	@Override
	public void setImpact(float amount) {
		this.impact = amount;
	}

	@Override
	public void setArmorNegation(float amount) {
		this.armorIgnore = amount;
	}

	@Override
	public void setStunType(StunType stunType) {
		this.stunType = stunType;
	}

	@Override
	public float getImpact() {
		return impact;
	}

	@Override
	public float getArmorNegation() {
		return armorIgnore;
	}

	@Override
	public StunType getStunType() {
		return stunType;
	}

	@Override
	public Entity getOwner() {
		return super.getTrueSource();
	}

	@Override
	public String getType() {
		return super.damageType;
	}

	@Override
	public boolean isBasicAttack() {
		return false;
	}

	@Override
	public int getSkillId() {
		return -1;
	}
}