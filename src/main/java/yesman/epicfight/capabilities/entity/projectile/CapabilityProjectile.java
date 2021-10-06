package yesman.epicfight.capabilities.entity.projectile;

import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import yesman.epicfight.capabilities.ModCapabilities;
import yesman.epicfight.capabilities.item.CapabilityItem;
import yesman.epicfight.capabilities.item.RangedWeaponCapability;
import yesman.epicfight.capabilities.item.CapabilityItem.Style;
import yesman.epicfight.entity.ai.attribute.EpicFightAttributes;

public abstract class CapabilityProjectile<T extends ProjectileEntity> {
	private float impact;
	private float armorNegation;
	
	public void onJoinWorld(T projectileEntity) {
		Entity shooter = projectileEntity.getShooter();
		boolean flag = true;
		if (shooter != null && shooter instanceof LivingEntity) {
			LivingEntity livingshooter = (LivingEntity)shooter;
			ItemStack heldItem = livingshooter.getHeldItemMainhand();
			CapabilityItem itemCap = ModCapabilities.getItemStackCapability(heldItem);
			if (itemCap instanceof RangedWeaponCapability) {
				Map<Attribute, AttributeModifier> modifierMap = itemCap.getDamageAttributesInCondition(Style.AIMING);
				if (modifierMap != null) {
					this.armorNegation = modifierMap.containsKey(EpicFightAttributes.ARMOR_NEGATION.get()) ?
							(float)modifierMap.get(EpicFightAttributes.ARMOR_NEGATION.get()).getAmount() : (float)EpicFightAttributes.ARMOR_NEGATION.get().getDefaultValue();
					this.impact = modifierMap.containsKey(EpicFightAttributes.IMPACT.get()) ?
							(float)modifierMap.get(EpicFightAttributes.IMPACT.get()).getAmount() : (float)EpicFightAttributes.IMPACT.get().getDefaultValue();
					if (modifierMap.containsKey(EpicFightAttributes.MAX_STRIKES.get())) {
						this.setMaxStrikes(projectileEntity, (int)modifierMap.get(EpicFightAttributes.MAX_STRIKES.get()).getAmount());
					}
				}
				
				flag = false;
			}
		}
		
		if (flag) {
			this.armorNegation = 0.0F;
			this.impact = 0.0F;
		}
	}
	
	protected abstract void setMaxStrikes(T projectileEntity, int maxStrikes);
	
	public float getArmorNegation() {
		return this.armorNegation;
	}
	
	public float getImpact() {
		return this.impact;
	}
}