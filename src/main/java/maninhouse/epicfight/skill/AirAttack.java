package maninhouse.epicfight.skill;

import java.util.List;

import maninhouse.epicfight.animation.LivingMotion;
import maninhouse.epicfight.animation.types.EntityState;
import maninhouse.epicfight.animation.types.StaticAnimation;
import maninhouse.epicfight.capabilities.entity.player.PlayerData;
import maninhouse.epicfight.capabilities.entity.player.ServerPlayerData;
import maninhouse.epicfight.client.capabilites.entity.ClientPlayerData;
import maninhouse.epicfight.network.ModNetworkManager;
import maninhouse.epicfight.network.client.CTSExecuteSkill;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.Hand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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