package yesman.epicfight.api.utils;

import net.minecraft.entity.Entity;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.math.vector.Vector3d;
import yesman.epicfight.api.animation.types.AttackAnimation;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class EpicFightDamageSource extends EntityDamageSource implements ExtendedDamageSource {
	private float impact;
	private float armorNegation;
	private boolean finisher;
	private StunType stunType;
	private final StaticAnimation animation;
	private Vector3d initialPosition;
	
	public EpicFightDamageSource(String damageTypeIn, Entity damageSourceEntityIn, StunType stunType, StaticAnimation animation) {
		this(damageTypeIn, damageSourceEntityIn, stunType, animation, Hand.MAIN_HAND);
	}
	
	public EpicFightDamageSource(String damageTypeIn, Entity damageSourceEntityIn, StunType stunType, StaticAnimation animation, Hand hand) {
		super(damageTypeIn, damageSourceEntityIn);
		LivingEntityPatch<?> entitypatch = (LivingEntityPatch<?>) damageSourceEntityIn.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
		this.stunType = stunType;
		this.impact = entitypatch.getImpact(hand);
		this.armorNegation = entitypatch.getArmorNegation(hand);
		this.animation = animation;
	}
	
	public EpicFightDamageSource(String damageTypeIn, Entity damageSourceEntityIn, StunType stunType, float impact, float armorNegation) {
		super(damageTypeIn, damageSourceEntityIn);
		this.stunType = stunType;
		this.impact = impact;
		this.armorNegation = armorNegation;
		this.animation = Animations.DUMMY_ANIMATION;
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
	public void setFinisher(boolean flag) {
		this.finisher = flag;
	}
	
	@Override
	public void setInitialPosition(Vector3d initialPosition) {
		this.initialPosition = initialPosition;
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
		return super.getEntity();
	}

	@Override
	public String getType() {
		return super.msgId;
	}
	
	@Override
	public boolean isBasicAttack() {
		if (this.animation instanceof AttackAnimation) {
			return ((AttackAnimation)this.animation).isBasicAttackAnimation();
		}
		
		return false;
	}
	
	@Override
	public boolean isFinisher() {
		return this.finisher;
	}
	
	@Override
	public int getAnimationId() {
		return this.animation.getId();
	}
	
	@Override
	public Vector3d getSourcePosition() {
		return this.initialPosition != null ? this.initialPosition : super.getSourcePosition();
	}
}