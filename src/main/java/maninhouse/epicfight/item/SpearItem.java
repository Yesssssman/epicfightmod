package maninhouse.epicfight.item;

import maninhouse.epicfight.animation.LivingMotion;
import maninhouse.epicfight.capabilities.item.CapabilityItem.HoldOption;
import maninhouse.epicfight.capabilities.item.CapabilityItem.WeaponCategory;
import maninhouse.epicfight.capabilities.item.CapabilityItem.HoldStyle;
import maninhouse.epicfight.capabilities.item.ModWeaponCapability;
import maninhouse.epicfight.gamedata.Animations;
import maninhouse.epicfight.gamedata.Colliders;
import maninhouse.epicfight.gamedata.Skills;
import maninhouse.epicfight.gamedata.Sounds;
import net.minecraft.block.BlockState;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemTier;

public class SpearItem extends WeaponItem {
	public SpearItem(Item.Properties build, ItemTier materialIn) {
		super(materialIn, 3, -2.8F, build);
	}
	
	@Override
	public boolean canHarvestBlock(BlockState blockIn) {
        return false;
    }
    
    @Override
	public void setWeaponCapability(IItemTier tier) {
    	int harvestLevel = tier.getHarvestLevel();
		ModWeaponCapability weaponCapability = new ModWeaponCapability(new ModWeaponCapability.Builder()
			.setCategory(WeaponCategory.SPEAR)
			.setStyleGetter((playerdata)  -> playerdata.getOriginalEntity().getHeldItemOffhand().isEmpty() ? HoldStyle.TWO_HAND : HoldStyle.ONE_HAND)
			.setHitSound(Sounds.BLADE_HIT)
			.setWeaponCollider(Colliders.spear)
			.setHoldOption(HoldOption.MAINHAND_ONLY)
			.addStyleCombo(HoldStyle.ONE_HAND, Animations.SPEAR_ONEHAND_AUTO, Animations.SPEAR_DASH, Animations.SPEAR_ONEHAND_AIR_SLASH)
			.addStyleCombo(HoldStyle.TWO_HAND, Animations.SPEAR_TWOHAND_AUTO_1, Animations.SPEAR_TWOHAND_AUTO_2, Animations.SPEAR_DASH, Animations.SPEAR_TWOHAND_AIR_SLASH)
			.addStyleCombo(HoldStyle.MOUNT, Animations.SPEAR_MOUNT_ATTACK)
			.addStyleSpecialAttack(HoldStyle.ONE_HAND, Skills.HEARTPIERCER)
			.addStyleSpecialAttack(HoldStyle.TWO_HAND, Skills.SLAUGHTER_STANCE)
			.addLivingMotionModifier(HoldStyle.ONE_HAND, LivingMotion.RUN, Animations.BIPED_RUN_SPEAR)
			.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.RUN, Animations.BIPED_RUN_SPEAR)
			.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.BLOCK, Animations.SPEAR_GUARD)
		);
		
		weaponCapability.addStyleAttributeSimple(HoldStyle.ONE_HAND, 4.0D + 4.0D * harvestLevel, 2.4D + harvestLevel * 0.3D, 1);
		weaponCapability.addStyleAttributeSimple(HoldStyle.TWO_HAND, 0.0D, 0.6D + harvestLevel * 0.5D, 3);
		
		this.capability = weaponCapability;
    }
}