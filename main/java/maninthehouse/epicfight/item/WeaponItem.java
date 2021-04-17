package maninthehouse.epicfight.item;

import javax.annotation.Nullable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import maninthehouse.epicfight.capabilities.ProviderItem;
import maninthehouse.epicfight.capabilities.item.ModWeaponCapability;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public abstract class WeaponItem extends ItemSword {
	protected ModWeaponCapability capability;
	protected float attackDamage;
	protected float attackSpeed;
	
	public WeaponItem(ToolMaterial tier, int damageIn, float speedIn) {
		super(tier);
		this.attackDamage = damageIn;
		this.attackSpeed = speedIn;
		this.setWeaponCapability();
	}
	
	@Override
	public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
		return super.hitEntity(stack, target, attacker);
    }
	
	public abstract void setWeaponCapability();
	
	@Override
	public Multimap<String, AttributeModifier> getItemAttributeModifiers(EntityEquipmentSlot equipmentSlot) {
		Multimap<String, AttributeModifier> multimap = HashMultimap.<String, AttributeModifier>create();
		if (equipmentSlot == EntityEquipmentSlot.MAINHAND) {
			multimap.put(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), new AttributeModifier(ATTACK_DAMAGE_MODIFIER, "Weapon modifier", (double) this.attackDamage, 0));
			multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ATTACK_SPEED_MODIFIER, "Weapon modifier", (double) this.attackSpeed, 0));
		}

		return multimap;
	}
	
	@Nullable
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable NBTTagCompound nbt) {
		ProviderItem itemProvider = new ProviderItem(this, false);
		if (!itemProvider.hasCapability()) {
			ProviderItem.addInstance(WeaponItem.this, capability);
		}
		return super.initCapabilities(stack, nbt);
	}
}