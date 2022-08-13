package yesman.epicfight.skill;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.types.EntityState;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.network.client.CPExecuteSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class KnockdownWakeupSkill extends DodgeSkill {
	public KnockdownWakeupSkill(Builder builder) {
		super(builder);
	}
	
	@OnlyIn(Dist.CLIENT)
	@Override
	public Object getExecutionPacket(LocalPlayerPatch executer, FriendlyByteBuf args) {
		args.readInt();
		args.readInt();
		int left = args.readInt();
		int right = args.readInt();
		int horizon = left + right;
		int animation = horizon > 0 ? 0 : 1; 
		
		CPExecuteSkill packet = new CPExecuteSkill(this.category.universalOrdinal());
		packet.getBuffer().writeInt(animation);
		packet.getBuffer().writeFloat(0.0F);
		
		return packet;
	}
	
	@Override
	public boolean isExecutableState(PlayerPatch<?> executer) {
		EntityState playerState = executer.getEntityState();
		
		return !(executer.isUnstable() || (playerState.hurt() && !playerState.knockDown())) && !executer.getOriginal().isInWater() && !executer.getOriginal().onClimbable();
	}
}