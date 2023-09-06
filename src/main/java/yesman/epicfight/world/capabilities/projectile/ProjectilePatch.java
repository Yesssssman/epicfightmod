package yesman.epicfight.world.capabilities.projectile;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.CapabilityItem.Styles;
import yesman.epicfight.world.capabilities.item.RangedWeaponCapability;
import yesman.epicfight.world.damagesource.EpicFightDamageSource;
import yesman.epicfight.world.damagesource.EpicFightDamageSources;
import yesman.epicfight.world.damagesource.StunType;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;

import java.util.Map;

public abstract class ProjectilePatch<T extends Projectile> extends EntityPatch<T> {
	protected float impact;
	protected float armorNegation;
	protected Vec3 initialFirePosition;
	
	@Override
	public void onJoinWorld(T projectileEntity, EntityJoinLevelEvent event) {
		Entity shooter = projectileEntity.getOwner();
		boolean flag = true;
		
		if (shooter != null && shooter instanceof LivingEntity livingshooter) {
			this.initialFirePosition = shooter.position();
			ItemStack heldItem = livingshooter.getMainHandItem();
			CapabilityItem itemCap = EpicFightCapabilities.getItemStackCapability(heldItem);
			
			if (itemCap instanceof RangedWeaponCapability) {
				Map<Attribute, AttributeModifier> modifierMap = itemCap.getDamageAttributesInCondition(Styles.RANGED);
				
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
	
	@Override
	public final void tick(LivingEvent.LivingTickEvent event) {
	}
	@Override
	protected final void clientTick(LivingEvent.LivingTickEvent event) {}
	@Override
	protected final void serverTick(LivingEvent.LivingTickEvent event) {}
	
	public boolean onProjectileImpact(ProjectileImpactEvent event) {
		return false;
	}
	
	protected abstract void setMaxStrikes(T projectileEntity, int maxStrikes);
	
	public EpicFightDamageSource getEpicFightDamageSource(DamageSource original) {
		EpicFightDamageSource extSource = EpicFightDamageSources.copy(original);
		extSource.setStunType(StunType.SHORT);
		extSource.addRuntimeTag(DamageTypeTags.IS_PROJECTILE);
		extSource.setArmorNegation(this.armorNegation);
		extSource.setImpact(this.impact);
		extSource.setInitialPosition(this.initialFirePosition);
		
		return extSource;
	}
	
	@Override
	public boolean overrideRender() {
		return false;
	}
	
	@Override
	public OpenMatrix4f getModelMatrix(float partialTicks) {
		return null;
	}
}