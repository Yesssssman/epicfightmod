package yesman.epicfight.world.damagesource;

import java.util.Set;

import com.google.common.collect.Sets;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.math.ValueModifier;

public interface EpicFightDamageSource {
	public static EpicFightDamageSource commonEntityDamageSource(String msg, LivingEntity owner, StaticAnimation animation) {
		return new EpicFightEntityDamageSource(msg, owner, animation);
	}
	
	public DamageSourceElements getDamageSourceElements();
	
	default EpicFightDamageSource setHurtItem(ItemStack hurtItem) {
		this.getDamageSourceElements().hurtItem = hurtItem;
		return this;
	}
	
	default ItemStack getHurtItem() {
		return this.getDamageSourceElements().hurtItem;
	}
	
	default EpicFightDamageSource setDamageModifier(ValueModifier damageModifier) {
		this.getDamageSourceElements().damageModifier = damageModifier;
		return this;
	}
	
	default ValueModifier getDamageModifier() {
		return this.getDamageSourceElements().damageModifier;
	}
	
	default EpicFightDamageSource setImpact(float f) {
		this.getDamageSourceElements().impact = f;
		return this;
	}
	
	default float getImpact() {
		return this.getDamageSourceElements().impact;
	}
	
	default EpicFightDamageSource setArmorNegation(float f) {
		this.getDamageSourceElements().armorNegation = f;
		return this;
	}
	
	default float getArmorNegation() {
		return this.getDamageSourceElements().armorNegation;
	}
	
	default EpicFightDamageSource setStunType(StunType stunType) {
		this.getDamageSourceElements().stunType = stunType;
		return this;
	}
	
	default StunType getStunType() {
		return this.getDamageSourceElements().stunType;
	}
	
	default EpicFightDamageSource addTag(SourceTag tag) {
		if (this.getDamageSourceElements().sourceTag == null) {
			this.getDamageSourceElements().sourceTag = Sets.newHashSet();
		}
		
		this.getDamageSourceElements().sourceTag.add(tag);
		
		return this;
	}
	
	default boolean hasTag(SourceTag tag) {
		Set<SourceTag> tags = this.getDamageSourceElements().sourceTag;
		
		if (tags != null) {
			return tags.contains(tag);
		}
		
		return false;
	}
	
	default EpicFightDamageSource addExtraDamage(ExtraDamageInstance extraDamage) {
		if (this.getDamageSourceElements().extraDamages == null) {
			this.getDamageSourceElements().extraDamages = Sets.newHashSet();
		}
		
		this.getDamageSourceElements().extraDamages.add(extraDamage);
		
		return this;
	}
	
	default Set<ExtraDamageInstance> getExtraDamages() {
		return this.getDamageSourceElements().extraDamages;
	}
	
	default DamageSource cast() {
		return (DamageSource)this;
	}
	
	public EpicFightDamageSource setInitialPosition(Vec3 initialPosition);
	public Vec3 getInitialPosition();
	public boolean isBasicAttack();
	public StaticAnimation getAnimation();
}