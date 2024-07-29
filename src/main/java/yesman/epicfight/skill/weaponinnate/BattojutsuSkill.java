package yesman.epicfight.skill.weaponinnate;

import yesman.epicfight.skill.SkillDataKeys;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class BattojutsuSkill extends ConditionalWeaponInnateSkill {
	public BattojutsuSkill(ConditionalWeaponInnateSkill.Builder builder) {
		super(builder);
	}
	
	@Override
	public void playSkillAnimation(ServerPlayerPatch executer) {
		boolean isSheathed = executer.getSkill(SkillSlots.WEAPON_PASSIVE).getDataManager().getDataValue(SkillDataKeys.SHEATH.get());
		
		if (isSheathed) {
			executer.playAnimationSynchronized(this.attackAnimations[this.getAnimationInCondition(executer)].get(), -0.65F);
		} else {
			executer.playAnimationSynchronized(this.attackAnimations[this.getAnimationInCondition(executer)].get(), 0);
		}
	}
}