package yesman.epicfight.utils.game;

import net.minecraft.entity.Entity;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.Hand;
import yesman.epicfight.animation.types.AttackAnimation;
import yesman.epicfight.capabilities.ModCapabilities;
import yesman.epicfight.capabilities.entity.LivingData;

public class DamageSourceExtended extends EntityDamageSource implements IExtendedDamageSource {
	private float impact;
	private float armorNegation;
	private StunType stunType;
	private final AttackAnimation attackMotion;
	
	public DamageSourceExtended(String damageTypeIn, Entity damageSourceEntityIn, StunType stunType, AttackAnimation animation) {
		this(damageTypeIn, damageSourceEntityIn, stunType, animation, Hand.MAIN_HAND);
	}
	
	public DamageSourceExtended(String damageTypeIn, Entity damageSourceEntityIn, StunType stunType, AttackAnimation animation, Hand hand) {
		super(damageTypeIn, damageSourceEntityIn);
		LivingData<?> entitydata = (LivingData<?>) damageSourceEntityIn.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
		this.stunType = stunType;
		this.impact = entitydata.getImpact(hand);
		this.armorNegation = entitydata.getArmorNegation(hand);
		this.attackMotion = animation;
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
	public float getImpact() {
		return this.impact;
	}

	@Override
	public float getArmorNegation() {
		return this.armorNegation;
	}

	@Override
	public StunType getStunType() {
		return this.stunType;
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
		return attackMotion.isBasicAttackAnimation();
	}
	
	@Override
	public int getSkillId() {
		return this.attackMotion.getId();
	}
}