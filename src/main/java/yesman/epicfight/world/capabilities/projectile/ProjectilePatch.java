package yesman.epicfight.world.capabilities.projectile;

import java.util.Map;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import yesman.epicfight.api.utils.ExtendedDamageSource.StunType;
import yesman.epicfight.api.utils.IndirectEpicFightDamageSource;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.CapabilityItem.Styles;
import yesman.epicfight.world.capabilities.item.RangedWeaponCapability;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

public abstract class ProjectilePatch<T extends ProjectileEntity> {
	protected float impact;
	protected float armorNegation;
	protected Vector3d initialFirePosition;
	
	public void onJoinWorld(T projectileEntity, EntityJoinWorldEvent event) {
		Entity shooter = projectileEntity.getOwner();
		boolean flag = true;
		
		if (shooter != null && shooter instanceof LivingEntity) {
			this.initialFirePosition = shooter.position();
			LivingEntity livingshooter = (LivingEntity)shooter;
			ItemStack heldItem = livingshooter.getMainHandItem();
			CapabilityItem itemCap = EpicFightCapabilities.getItemStackCapability(heldItem);
			
			if (itemCap instanceof RangedWeaponCapability) {
				Map<Attribute, AttributeModifier> modifierMap = itemCap.getDamageAttributesInCondition(Styles.RANGED);
				
				if (modifierMap != null) {
					this.armorNegation = modifierMap.containsKey(EpicFightAttributes.ARMOR_NEGATION.get()) ? (float)modifierMap.get(EpicFightAttributes.ARMOR_NEGATION.get()).getAmount() : (float)EpicFightAttributes.ARMOR_NEGATION.get().getDefaultValue();
					this.impact = modifierMap.containsKey(EpicFightAttributes.IMPACT.get()) ? (float)modifierMap.get(EpicFightAttributes.IMPACT.get()).getAmount() : (float)EpicFightAttributes.IMPACT.get().getDefaultValue();
					
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
	
	public boolean onProjectileImpact(ProjectileImpactEvent event) {
		return false;
	}
	
	protected abstract void setMaxStrikes(T projectileEntity, int maxStrikes);
	
	public IndirectEpicFightDamageSource getEpicFightDamageSource(DamageSource original) {
		IndirectEpicFightDamageSource extSource = new IndirectEpicFightDamageSource(original.msgId, original.getEntity(), original.getDirectEntity(), StunType.SHORT);
		extSource.setProjectile();
		extSource.setArmorNegation(this.armorNegation);
		extSource.setImpact(this.impact);
		extSource.setInitialPosition(this.initialFirePosition);
		
		return extSource;
	}
}