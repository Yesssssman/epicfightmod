package yesman.epicfight.skill;

import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.animation.LivingMotion;
import yesman.epicfight.animation.types.EntityState;
import yesman.epicfight.animation.types.StaticAnimation;
import yesman.epicfight.capabilities.entity.player.PlayerData;
import yesman.epicfight.capabilities.entity.player.ServerPlayerData;
import yesman.epicfight.client.capabilites.player.ClientPlayerData;
import yesman.epicfight.network.ModNetworkManager;
import yesman.epicfight.network.client.CTSExecuteSkill;

public class AirAttack extends Skill {
	public AirAttack() {
		super(SkillCategory.AIR_ATTACK, 2, ActivateType.ONE_SHOT, Resource.STAMINA, "air_attack");
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void executeOnClient(ClientPlayerData executer, PacketBuffer args) {
		ModNetworkManager.sendToServer(new CTSExecuteSkill(this.slot.getIndex(), true, args));
	}
	
	@Override
	public boolean isExecutableState(PlayerData<?> executer) {
		EntityState playerState = executer.getEntityState();
		PlayerEntity player = executer.getOriginalEntity();
		return !(player.isPassenger() || player.isSpectator() || player.isElytraFlying() || executer.currentMotion == LivingMotion.FALL
				|| !playerState.canBasicAttack());
	}
	
	@Override
	public void executeOnServer(ServerPlayerData executer, PacketBuffer args) {
		List<StaticAnimation> motions = executer.getHeldItemCapability(Hand.MAIN_HAND).getAutoAttckMotion(executer);
		StaticAnimation attackMotion = motions.get(motions.size() - 1);
		
		if (attackMotion != null) {
			super.executeOnServer(executer, args);
			executer.playAnimationSynchronize(attackMotion, 0);
		}
	}
}