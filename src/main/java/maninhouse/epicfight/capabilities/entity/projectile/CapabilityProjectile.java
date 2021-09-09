package maninhouse.epicfight.capabilities.entity.projectile;

import java.util.Map;
import java.util.function.Supplier;

import maninhouse.epicfight.capabilities.ModCapabilities;
import maninhouse.epicfight.capabilities.item.CapabilityItem;
import maninhouse.epicfight.capabilities.item.CapabilityItem.HoldStyle;
import maninhouse.epicfight.capabilities.item.RangedWeaponCapability;
import maninhouse.epicfight.entity.ai.attribute.ModAttributes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;

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
				Map<Supplier<Attribute>, AttributeModifier> modifierMap = itemCap.getDamageAttributesInCondition(HoldStyle.AIMING);
				if (modifierMap != null) {
					this.armorNegation = modifierMap.containsKey(ModAttributes.ARMOR_NEGATION) ?
							(float)modifierMap.get(ModAttributes.ARMOR_NEGATION).getAmount() : (float)ModAttributes.ARMOR_NEGATION.get().getDefaultValue();
					this.impact = modifierMap.containsKey(ModAttributes.IMPACT) ?
							(float)modifierMap.get(ModAttributes.IMPACT).getAmount() : (float)ModAttributes.IMPACT.get().getDefaultValue();
					if (modifierMap.containsKey(ModAttributes.MAX_STRIKES)) {
						this.setMaxStrikes(projectileEntity, (int)modifierMap.get(ModAttributes.MAX_STRIKES).getAmount());
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