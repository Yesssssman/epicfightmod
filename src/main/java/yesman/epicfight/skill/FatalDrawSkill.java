package yesman.epicfight.skill;

import net.minecraft.network.PacketBuffer;
import yesman.epicfight.gameasset.Animations;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class FatalDrawSkill extends SeperativeMotionSkill {
	public FatalDrawSkill(Builder<? extends Skill> builder) {
		super(builder, (executer)->executer.getOriginal().isSprinting() ? 1 : 0, Animations.FATAL_DRAW, Animations.FATAL_DRAW_DASH);
	}
	
	@Override
	public void executeOnServer(ServerPlayerPatch executer, PacketBuffer args) {
		boolean isSheathed = executer.getSkill(SkillCategories.WEAPON_PASSIVE).getDataManager().getDataValue(KatanaPassive.SHEATH);
		
		if (isSheathed) {
			executer.playAnimationSynchronized(this.attackAnimations[this.getAnimationInCondition(executer)], -0.666F);
		} else {
			executer.playAnimationSynchronized(this.attackAnimations[this.getAnimationInCondition(executer)], 0);
		}
		
		this.setConsumptionSynchronize(executer, 0);
		this.setStackSynchronize(executer, executer.getSkill(this.category).getStack() - 1);
		this.setDurationSynchronize(executer, this.maxDuration);
		executer.getSkill(this.category).activate();
	}
}