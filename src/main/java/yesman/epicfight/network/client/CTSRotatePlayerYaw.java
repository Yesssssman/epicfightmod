package yesman.epicfight.network.client;

import java.util.function.Supplier;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import yesman.epicfight.capabilities.ModCapabilities;
import yesman.epicfight.capabilities.entity.player.PlayerData;
import yesman.epicfight.network.ModNetworkManager;
import yesman.epicfight.network.server.STCNotifyPlayerYawChanged;

public class CTSRotatePlayerYaw {
	private float yaw;

	public CTSRotatePlayerYaw() {
		this.yaw = 0F;
	}

	public CTSRotatePlayerYaw(float yaw) {
		this.yaw = yaw;
	}

	public static CTSRotatePlayerYaw fromBytes(PacketBuffer buf) {
		return new CTSRotatePlayerYaw(buf.readFloat());
	}

	public static void toBytes(CTSRotatePlayerYaw msg, PacketBuffer buf) {
		buf.writeFloat(msg.yaw);
	}

	public static void handle(CTSRotatePlayerYaw msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(()->{
			ServerPlayerEntity player = ctx.get().getSender();

			if (player != null) {
				PlayerData<?> entitydata = (PlayerData<?>) player.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);

				if (entitydata != null) {
					ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCNotifyPlayerYawChanged(player.getEntityId(), msg.yaw), player);
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}
}