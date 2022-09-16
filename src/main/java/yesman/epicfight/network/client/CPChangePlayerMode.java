package yesman.epicfight.network.client;

import java.util.function.Supplier;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.server.SPChangePlayerMode;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class CPChangePlayerMode {
	private PlayerPatch.PlayerMode mode;
	
	public CPChangePlayerMode(PlayerPatch.PlayerMode mode) {
		this.mode = mode;
	}

	public static CPChangePlayerMode fromBytes(PacketBuffer buf) {
		return new CPChangePlayerMode(PlayerPatch.PlayerMode.values()[buf.readInt()]);
	}

	public static void toBytes(CPChangePlayerMode msg, PacketBuffer buf) {
		buf.writeInt(msg.mode.ordinal());
	}
	
	public static void handle(CPChangePlayerMode msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayerEntity player = ctx.get().getSender();
			
			if (player != null) {
				ServerPlayerPatch playerpatch = (ServerPlayerPatch) player.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).orElse(null);
				
				if (playerpatch != null) {
					playerpatch.toMode(msg.mode, false);
					
					EpicFightNetworkManager.sendToAllPlayerTrackingThisEntity(new SPChangePlayerMode(player.getId(), playerpatch.getPlayerMode()), player);
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}
}