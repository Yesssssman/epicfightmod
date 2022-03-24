package yesman.epicfight.world.capabilities.item;

import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.gameasset.Skills;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

public class KnuckleCapability extends ModWeaponCapability {
	public KnuckleCapability() {
		super(new ModWeaponCapability.Builder()
			.setCategory(WeaponCategory.FIST)
			.addStyleCombo(Style.ONE_HAND, Animations.FIST_AUTO_1, Animations.FIST_AUTO_2, Animations.FIST_AUTO_3, Animations.FIST_DASH, Animations.FIST_AIR_SLASH)
			.addStyleSpecialAttack(Style.ONE_HAND, Skills.RELENTLESS_COMBO)
		);
	}
	
	@Override
	public boolean isValidOffhandItem(ItemStack item) {
		CapabilityItem itemCap = EpicFightCapabilities.getItemStackCapability(item);
		boolean isFist = itemCap != null && itemCap.getWeaponCategory() == WeaponCategory.FIST;
		return isFist || !(item.getItem() instanceof SwordItem || item.getItem() instanceof DiggerItem);
	}
	
	@Override
	public boolean canUsedInOffhandAlone() {
		return true;
	}
}