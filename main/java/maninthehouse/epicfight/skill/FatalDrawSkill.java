package maninthehouse.epicfight.skill;

import maninthehouse.epicfight.capabilities.entity.player.ServerPlayerData;
import maninthehouse.epicfight.gamedata.Animations;
import maninthehouse.epicfight.network.ModNetworkManager;
import maninthehouse.epicfight.network.server.STCResetBasicAttackCool;
import net.minecraft.network.PacketBuffer;

public class FatalDrawSkill extends SelectiveAttackSkill
{
	public FatalDrawSkill(String skillName)
	{
		super(SkillSlot.WEAPON_SPECIAL_ATTACK, 30.0F, skillName, (executer)->{
					return executer.getOriginalEntity().isSprinting() ? 1 : 0;
				}, Animations.FATAL_DRAW, Animations.FATAL_DRAW_DASH);
	}
	
	@Override
	public void executeOnServer(ServerPlayerData executer, PacketBuffer args)
	{
		boolean isSheathed = executer.getSkill(SkillSlot.WEAPON_GIMMICK).getVariableNBT().getBoolean("sheath");
		
		if(isSheathed)
			executer.playAnimationSynchronize(this.attackAnimations[this.getAnimationInCondition(executer)], -0.666F);
		else
			executer.playAnimationSynchronize(this.attackAnimations[this.getAnimationInCondition(executer)], 0);
		
		ModNetworkManager.sendToPlayer(new STCResetBasicAttackCool(), executer.getOriginalEntity());
	}
}