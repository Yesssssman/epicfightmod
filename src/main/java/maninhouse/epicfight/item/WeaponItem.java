package maninhouse.epicfight.item;

import javax.annotation.Nullable;

import maninhouse.epicfight.capabilities.item.CapabilityItem;
import maninhouse.epicfight.capabilities.provider.ProviderItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public abstract class WeaponItem extends SwordItem {
	protected CapabilityItem capability;
	protected LazyOptional<CapabilityItem> optional = LazyOptional.of(() -> this.capability);
	
	public WeaponItem(IItemTier tier, int damageIn, float speedIn, Item.Properties builder) {
		super(tier, damageIn, speedIn, builder);
		this.setWeaponCapability(tier);
	}
	
	@Override
	public boolean hitEntity(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damageItem(1, attacker, (entity) -> {
        	entity.sendBreakAnimation(EquipmentSlotType.MAINHAND);
        });
        return true;
    }
	
	public abstract void setWeaponCapability(IItemTier tier);
	
	@Nullable
	public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
		ProviderItem itemProvider = new ProviderItem(this, false);
		if (!itemProvider.hasCapability()) {
			ProviderItem.addInstance(this, optional.orElse(null));
		}
		return super.initCapabilities(stack, nbt);
    }
}