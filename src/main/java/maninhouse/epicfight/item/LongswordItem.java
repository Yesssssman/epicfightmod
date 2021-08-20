package maninhouse.epicfight.item;

import maninhouse.epicfight.animation.LivingMotion;
import maninhouse.epicfight.capabilities.item.ModWeaponCapability;
import maninhouse.epicfight.capabilities.entity.player.PlayerData;
import maninhouse.epicfight.capabilities.item.CapabilityItem.HoldOption;
import maninhouse.epicfight.capabilities.item.CapabilityItem.WeaponCategory;
import maninhouse.epicfight.capabilities.item.CapabilityItem.HoldStyle;
import maninhouse.epicfight.gamedata.Animations;
import maninhouse.epicfight.gamedata.Colliders;
import maninhouse.epicfight.gamedata.Skills;
import maninhouse.epicfight.gamedata.Sounds;
import maninhouse.epicfight.skill.SkillCategory;
import net.minecraft.block.BlockState;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemTier;

public class LongswordItem extends WeaponItem {
	public LongswordItem(Item.Properties build, ItemTier materialIn) {
		super(materialIn, 4, -2.6F, build);
	}
	
	@Override
	public boolean canHarvestBlock(BlockState blockIn) {
        return false;
    }
    
    @Override
	public void setWeaponCapability(IItemTier tier) {
    	int harvestLevel = tier.getHarvestLevel();
		ModWeaponCapability weaponCapability = new ModWeaponCapability(new ModWeaponCapability.Builder()
			.setCategory(WeaponCategory.LONGSWORD)
			.setStyleGetter((playerdata) -> {
				if (playerdata instanceof PlayerData<?>) {
					if (((PlayerData<?>)playerdata).getSkill(SkillCategory.WEAPON_SPECIAL_ATTACK).getRemainDuration() > 0) {
						return HoldStyle.LIECHTENHAUER;
					}
				}
				return HoldStyle.TWO_HAND;
			})
			.setHitSound(Sounds.BLADE_HIT)
			.setWeaponCollider(Colliders.longsword)
			.setHoldOption(HoldOption.TWO_HANDED)
			.addStyleCombo(HoldStyle.TWO_HAND, Animations.LONGSWORD_AUTO_1, Animations.LONGSWORD_AUTO_2, Animations.LONGSWORD_AUTO_3, Animations.LONGSWORD_DASH, Animations.LONGSWORD_AIR_SLASH)
			.addStyleCombo(HoldStyle.LIECHTENHAUER, Animations.LONGSWORD_AUTO_1, Animations.LONGSWORD_AUTO_2, Animations.LONGSWORD_AUTO_3, Animations.LONGSWORD_DASH, Animations.LONGSWORD_AIR_SLASH)
			.addStyleCombo(HoldStyle.MOUNT, Animations.SWORD_MOUNT_ATTACK)
			.addStyleSpecialAttack(HoldStyle.TWO_HAND, Skills.LIECHTENAUER)
			.addStyleSpecialAttack(HoldStyle.LIECHTENHAUER, Skills.LIECHTENAUER)
			.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.IDLE, Animations.BIPED_IDLE_GREATSWORD)
			.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.WALK, Animations.BIPED_IDLE_GREATSWORD)
			.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.RUN, Animations.BIPED_IDLE_GREATSWORD)
			.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.SNEAK, Animations.BIPED_IDLE_GREATSWORD)
			.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.KNEEL, Animations.BIPED_IDLE_GREATSWORD)
			.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.JUMP, Animations.BIPED_IDLE_GREATSWORD)
			.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.BLOCK, Animations.LONGSWORD_GUARD)
			.addLivingMotionModifier(HoldStyle.LIECHTENHAUER, LivingMotion.IDLE, Animations.BIPED_IDLE_LONGSWORD)
			.addLivingMotionModifier(HoldStyle.LIECHTENHAUER, LivingMotion.WALK, Animations.BIPED_WALK_LONGSWORD)
			.addLivingMotionModifier(HoldStyle.LIECHTENHAUER, LivingMotion.RUN, Animations.BIPED_WALK_LONGSWORD)
			.addLivingMotionModifier(HoldStyle.LIECHTENHAUER, LivingMotion.SNEAK, Animations.BIPED_WALK_LONGSWORD)
			.addLivingMotionModifier(HoldStyle.LIECHTENHAUER, LivingMotion.KNEEL, Animations.BIPED_WALK_LONGSWORD)
			.addLivingMotionModifier(HoldStyle.LIECHTENHAUER, LivingMotion.JUMP, Animations.BIPED_WALK_LONGSWORD)
			.addLivingMotionModifier(HoldStyle.LIECHTENHAUER, LivingMotion.BLOCK, Animations.LONGSWORD_GUARD)
		);
		weaponCapability.addStyleAttributeSimple(HoldStyle.TWO_HAND, 0.0D, 1.0D + harvestLevel * 0.5D, 2);
		weaponCapability.addStyleAttributeSimple(HoldStyle.LIECHTENHAUER, 0.0D, 1.0D + harvestLevel * 0.5D, 2);
		this.capability = weaponCapability;
    }
}