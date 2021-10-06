package yesman.epicfight.capabilities.item;

import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;
import yesman.epicfight.capabilities.ModCapabilities;
import yesman.epicfight.gamedata.Animations;
import yesman.epicfight.gamedata.Skills;

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
		CapabilityItem itemCap = ModCapabilities.getItemStackCapability(item);
		boolean isFist = itemCap != null && itemCap.getWeaponCategory() == WeaponCategory.FIST;
		return isFist || !(item.getItem() instanceof SwordItem || item.getItem() instanceof ToolItem);
	}
}