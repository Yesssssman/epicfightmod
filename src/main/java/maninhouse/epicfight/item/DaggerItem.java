package maninhouse.epicfight.item;

import maninhouse.epicfight.capabilities.item.CapabilityItem.HoldStyle;
import maninhouse.epicfight.capabilities.item.CapabilityItem.WeaponCategory;
import maninhouse.epicfight.capabilities.item.ModWeaponCapability;
import maninhouse.epicfight.gamedata.Animations;
import maninhouse.epicfight.gamedata.Colliders;
import maninhouse.epicfight.gamedata.Skills;
import maninhouse.epicfight.gamedata.Sounds;
import net.minecraft.block.BlockState;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemTier;
import net.minecraft.util.Hand;

public class DaggerItem extends WeaponItem {
	public DaggerItem(Item.Properties build, ItemTier materialIn) {
		super(materialIn, 1, -1.6F, build);
	}
	
	@Override
	public boolean canHarvestBlock(BlockState blockIn) {
        return false;
    }
    
    @Override
	public void setWeaponCapability(IItemTier tier) {
    	int harvestLevel = tier.getHarvestLevel();
		ModWeaponCapability weaponCapability = new ModWeaponCapability(new ModWeaponCapability.Builder()
			.setCategory(WeaponCategory.DAGGER)
			.setStyleGetter((playerdata) -> playerdata.getHeldItemCapability(Hand.OFF_HAND).getWeaponCategory() == WeaponCategory.DAGGER ? HoldStyle.TWO_HAND : HoldStyle.ONE_HAND)
			.setHitSound(Sounds.BLADE_HIT)
			.setWeaponCollider(Colliders.dagger)
			.addStyleCombo(HoldStyle.ONE_HAND, Animations.DAGGER_AUTO_1, Animations.DAGGER_AUTO_2, Animations.DAGGER_AUTO_3, Animations.SWORD_DASH, Animations.DAGGER_AIR_SLASH)
			.addStyleCombo(HoldStyle.TWO_HAND, Animations.DAGGER_DUAL_AUTO_1, Animations.DAGGER_DUAL_AUTO_2, Animations.DAGGER_DUAL_AUTO_3, Animations.DAGGER_DUAL_AUTO_4, Animations.DAGGER_DUAL_DASH, Animations.DAGGER_DUAL_AIR_SLASH)
			.addStyleCombo(HoldStyle.MOUNT, Animations.SWORD_MOUNT_ATTACK)
			.addStyleSpecialAttack(HoldStyle.ONE_HAND, Skills.EVISCERATE)
			.addStyleSpecialAttack(HoldStyle.TWO_HAND, Skills.BLADE_RUSH)
		);
		weaponCapability.addStyleAttributeSimple(HoldStyle.ONE_HAND, 0.0D, 0.5D + harvestLevel * 0.1D, 0);
		weaponCapability.addStyleAttributeSimple(HoldStyle.TWO_HAND, 0.0D, 0.5D + harvestLevel * 0.1D, 1);
		this.capability = weaponCapability;
    }
}