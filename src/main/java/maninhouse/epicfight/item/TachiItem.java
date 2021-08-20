package maninhouse.epicfight.item;

import maninhouse.epicfight.animation.LivingMotion;
import maninhouse.epicfight.capabilities.item.CapabilityItem.HoldOption;
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

public class TachiItem extends WeaponItem {
	public TachiItem(Item.Properties build, ItemTier materialIn) {
		super(materialIn, 4, -2.4F, build);
	}
	
	@Override
	public boolean canHarvestBlock(BlockState blockIn) {
        return false;
    }
    
    @Override
	public void setWeaponCapability(IItemTier tier) {
    	int harvestLevel = tier.getHarvestLevel();
		ModWeaponCapability weaponCapability = new ModWeaponCapability(new ModWeaponCapability.Builder()
			.setCategory(WeaponCategory.TACHI)
			.setStyleGetter((playerdata) -> HoldStyle.TWO_HAND)
			.setHitSound(Sounds.BLADE_HIT)
			.setWeaponCollider(Colliders.longsword)
			.setHoldOption(HoldOption.TWO_HANDED)
			.addStyleCombo(HoldStyle.TWO_HAND, Animations.LONGSWORD_AUTO_1, Animations.LONGSWORD_AUTO_2, Animations.LONGSWORD_AUTO_3, Animations.TACHI_DASH, Animations.LONGSWORD_AIR_SLASH)
			.addStyleCombo(HoldStyle.MOUNT, Animations.SWORD_MOUNT_ATTACK)
			.addStyleSpecialAttack(HoldStyle.TWO_HAND, Skills.LETHAL_SLICING)
			.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.IDLE, Animations.BIPED_IDLE_TACHI)
			.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.KNEEL, Animations.BIPED_IDLE_TACHI)
			.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.WALK, Animations.BIPED_IDLE_TACHI)
			.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.RUN, Animations.BIPED_IDLE_TACHI)
			.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.SNEAK, Animations.BIPED_IDLE_TACHI)
			.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.SWIM, Animations.BIPED_IDLE_TACHI)
			.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.FLOAT, Animations.BIPED_IDLE_TACHI)
			.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.FALL, Animations.BIPED_IDLE_TACHI)
			.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.BLOCK, Animations.LONGSWORD_GUARD)
		);
		weaponCapability.addStyleAttributeSimple(HoldStyle.TWO_HAND, 0.0D, 1.0D + harvestLevel * 0.5D, 2);
		this.capability = weaponCapability;
    }
}