package yesman.epicfight.skill;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.LivingMotions;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.client.CPExecuteSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class KnockdownWakeupSkill extends DodgeSkill {
	public KnockdownWakeupSkill(Builder builder) {
		super(builder);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public void executeOnClient(LocalPlayerPatch executer, FriendlyByteBuf args) {
		args.readInt();
		args.readInt();
		int left = args.readInt();
		int right = args.readInt();
		int horizon = left + right;
		int animation = horizon > 0 ? 0 : 1; 
		
		CPExecuteSkill packet = new CPExecuteSkill(this.category.universalOrdinal());
		packet.getBuffer().writeInt(animation);
		packet.getBuffer().writeFloat(0.0F);
		
		EpicFightNetworkManager.sendToServer(packet);
	}
	
	@Override
	public boolean isExecutableState(PlayerPatch<?> executer) {
		executer.updateEntityState();
		EntityState playerState = executer.getEntityState();
		
		return !(executer.getOriginal().isFallFlying() || executer.currentLivingMotion == LivingMotions.FALL || (playerState.hurt() && playerState != EntityState.KNOCKDOWN)) && !executer.getOriginal().isInWater() && !executer.getOriginal().onClimbable();
	}
}