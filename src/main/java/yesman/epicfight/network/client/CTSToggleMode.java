package yesman.epicfight.network.client;

import java.util.function.Supplier;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import yesman.epicfight.capabilities.ModCapabilities;
import yesman.epicfight.capabilities.entity.player.PlayerData;
import yesman.epicfight.network.ModNetworkManager;
import yesman.epicfight.network.server.STCToggleMode;

public class CTSToggleMode {
	private boolean battleMode;
	
	public CTSToggleMode(boolean battleMode) {
		this.battleMode = battleMode;
	}

	public static CTSToggleMode fromBytes(PacketBuffer buf) {
		return new CTSToggleMode(buf.readBoolean());
	}

	public static void toBytes(CTSToggleMode msg, PacketBuffer buf) {
		buf.writeBoolean(msg.battleMode);
	}
	
	public static void handle(CTSToggleMode msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(()->{
			ServerPlayerEntity player = ctx.get().getSender();

			if (player != null) {
				PlayerData<?> entitydata = (PlayerData<?>) player.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				
				if (entitydata != null) {
					entitydata.setBattleMode(msg.battleMode);
					ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCToggleMode(player.getEntityId(), msg.battleMode), player);
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}
}