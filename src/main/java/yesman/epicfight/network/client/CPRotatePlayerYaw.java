package yesman.epicfight.network.client;

import java.util.function.Supplier;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.server.SPChangePlayerYaw;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class CPRotatePlayerYaw {
	private float yaw;

	public CPRotatePlayerYaw() {
		this.yaw = 0F;
	}

	public CPRotatePlayerYaw(float yaw) {
		this.yaw = yaw;
	}

	public static CPRotatePlayerYaw fromBytes(PacketBuffer buf) {
		return new CPRotatePlayerYaw(buf.readFloat());
	}

	public static void toBytes(CPRotatePlayerYaw msg, PacketBuffer buf) {
		buf.writeFloat(msg.yaw);
	}

	public static void handle(CPRotatePlayerYaw msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(()->{
			ServerPlayerEntity player = ctx.get().getSender();
			
			if (player != null) {
				PlayerPatch<?> entitypatch = (PlayerPatch<?>) player.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				if (entitypatch != null) {
					EpicFightNetworkManager.sendToAllPlayerTrackingThisEntity(new SPChangePlayerYaw(player.getId(), msg.yaw), player);
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}
}