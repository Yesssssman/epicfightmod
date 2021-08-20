package maninhouse.epicfight.skill;

import maninhouse.epicfight.capabilities.entity.player.ServerPlayerData;
import maninhouse.epicfight.gamedata.Animations;
import maninhouse.epicfight.network.ModNetworkManager;
import maninhouse.epicfight.network.server.STCResetBasicAttackCool;
import net.minecraft.network.PacketBuffer;

public class FatalDrawSkill extends SelectiveAttackSkill {
	public FatalDrawSkill(String skillName) {
		super(30.0F, skillName, (executer)->executer.getOriginalEntity().isSprinting() ? 1 : 0, Animations.FATAL_DRAW, Animations.FATAL_DRAW_DASH);
	}
	
	@Override
	public void executeOnServer(ServerPlayerData executer, PacketBuffer args) {
		boolean isSheathed = executer.getSkill(SkillCategory.WEAPON_PASSIVE).getDataManager().getDataValue(KatanaPassive.SHEATH);
		
		if(isSheathed) {
			executer.playAnimationSynchronize(this.attackAnimations[this.getAnimationInCondition(executer)], -0.666F);
		} else {
			executer.playAnimationSynchronize(this.attackAnimations[this.getAnimationInCondition(executer)], 0);
		}
		
		ModNetworkManager.sendToPlayer(new STCResetBasicAttackCool(), executer.getOriginalEntity());
		
		this.setConsumptionSynchronize(executer, 0);
		this.setStackSynchronize(executer, executer.getSkill(this.slot).getStack() - 1);
		this.setDurationSynchronize(executer, this.maxDuration);
		executer.getSkill(this.slot).activate();
	}
}