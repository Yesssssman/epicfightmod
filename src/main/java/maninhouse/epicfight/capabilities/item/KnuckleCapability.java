package maninhouse.epicfight.capabilities.item;

import maninhouse.epicfight.capabilities.ModCapabilities;
import maninhouse.epicfight.gamedata.Animations;
import maninhouse.epicfight.gamedata.Skills;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolItem;

public class KnuckleCapability extends ModWeaponCapability {
	public KnuckleCapability() {
		super(new ModWeaponCapability.Builder()
			.setCategory(WeaponCategory.FIST)
			.addStyleCombo(HoldStyle.ONE_HAND, Animations.FIST_AUTO_1, Animations.FIST_AUTO_2, Animations.FIST_AUTO_3, Animations.FIST_DASH, Animations.FIST_AIR_SLASH)
			.addStyleSpecialAttack(HoldStyle.ONE_HAND, Skills.RELENTLESS_COMBO)
		);
		this.addStyleAttributeSimple(HoldStyle.ONE_HAND, 0.0D, 1.0D, 1);
	}
	
	@Override
	public boolean isValidOffhandItem(ItemStack item) {
		CapabilityItem itemCap = ModCapabilities.getItemStackCapability(item);
		boolean isFist = itemCap != null && itemCap.getWeaponCategory() == WeaponCategory.FIST;
		return isFist || !(item.getItem() instanceof SwordItem || item.getItem() instanceof ToolItem);
	}
}