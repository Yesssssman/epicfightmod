package maninhouse.epicfight.capabilities.item;

import maninhouse.epicfight.animation.LivingMotion;
import maninhouse.epicfight.capabilities.entity.LivingData;
import maninhouse.epicfight.capabilities.entity.player.PlayerData;
import maninhouse.epicfight.gamedata.Animations;
import maninhouse.epicfight.gamedata.Colliders;
import maninhouse.epicfight.gamedata.Skills;
import maninhouse.epicfight.gamedata.Sounds;
import maninhouse.epicfight.skill.KatanaPassive;
import maninhouse.epicfight.skill.SkillCategory;

public class KatanaCapability extends ModWeaponCapability {
	public KatanaCapability() {
		super(new ModWeaponCapability.Builder()
			.setCategory(WeaponCategory.KATANA)
			.setStyleGetter((playerdata) -> HoldStyle.TWO_HAND)
			.setPassiveSkill(Skills.KATANA_PASSIVE)
			.setHitSound(Sounds.BLADE_HIT)
			.setWeaponCollider(Colliders.katana)
			.setHoldOption(HoldOption.TWO_HANDED)
			.addStyleCombo(HoldStyle.SHEATH, Animations.KATANA_SHEATHING_AUTO, Animations.KATANA_SHEATHING_DASH, Animations.KATANA_SHEATH_AIR_SLASH)
			.addStyleCombo(HoldStyle.TWO_HAND, Animations.KATANA_AUTO_1, Animations.KATANA_AUTO_2, Animations.KATANA_AUTO_3, Animations.SWORD_DASH, Animations.KATANA_AIR_SLASH)
			.addStyleCombo(HoldStyle.MOUNT, Animations.SWORD_MOUNT_ATTACK)
			.addStyleSpecialAttack(HoldStyle.SHEATH, Skills.FATAL_DRAW)
			.addStyleSpecialAttack(HoldStyle.TWO_HAND, Skills.FATAL_DRAW)
			.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.IDLE, Animations.BIPED_IDLE_UNSHEATHING)
			.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.KNEEL, Animations.BIPED_IDLE_UNSHEATHING)
			.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.WALK, Animations.BIPED_WALK_UNSHEATHING)
			.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.RUN, Animations.BIPED_RUN_UNSHEATHING)
			.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.SNEAK, Animations.BIPED_WALK_UNSHEATHING)
			.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.SWIM, Animations.BIPED_WALK_UNSHEATHING)
			.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.FLOAT, Animations.BIPED_WALK_UNSHEATHING)
			.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.FALL, Animations.BIPED_WALK_UNSHEATHING)
			.addLivingMotionModifier(HoldStyle.TWO_HAND, LivingMotion.BLOCK, Animations.KATANA_GUARD)
			.addLivingMotionModifier(HoldStyle.SHEATH, LivingMotion.IDLE, Animations.BIPED_IDLE_SHEATHING)
			.addLivingMotionModifier(HoldStyle.SHEATH, LivingMotion.KNEEL, Animations.BIPED_IDLE_SHEATHING_MIX)
			.addLivingMotionModifier(HoldStyle.SHEATH, LivingMotion.WALK, Animations.BIPED_MOVE_SHEATHING)
		    .addLivingMotionModifier(HoldStyle.SHEATH, LivingMotion.RUN, Animations.BIPED_MOVE_SHEATHING)
		    .addLivingMotionModifier(HoldStyle.SHEATH, LivingMotion.SNEAK, Animations.BIPED_MOVE_SHEATHING)
		    .addLivingMotionModifier(HoldStyle.SHEATH, LivingMotion.SWIM, Animations.BIPED_MOVE_SHEATHING)
		    .addLivingMotionModifier(HoldStyle.SHEATH, LivingMotion.FLOAT, Animations.BIPED_MOVE_SHEATHING)
		    .addLivingMotionModifier(HoldStyle.SHEATH, LivingMotion.FALL, Animations.BIPED_MOVE_SHEATHING)
		);
		this.addStyleAttributeSimple(HoldStyle.TWO_HAND, 0.0D, 0.6D, 1);
	}
	
	@Override
	public HoldStyle getStyle(LivingData<?> entitydata) {
		if (entitydata instanceof PlayerData) {
			PlayerData<?> playerdata = (PlayerData<?>)entitydata;
			if (playerdata.getSkill(SkillCategory.WEAPON_PASSIVE).getDataManager().hasData(KatanaPassive.SHEATH) && 
					playerdata.getSkill(SkillCategory.WEAPON_PASSIVE).getDataManager().getDataValue(KatanaPassive.SHEATH)) {
				return HoldStyle.SHEATH;
			}
		}
		
		return HoldStyle.TWO_HAND;
	}
	
	@Override
	public boolean canUseOnMount() {
		return true;
	}
}