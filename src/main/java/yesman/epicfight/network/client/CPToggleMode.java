package yesman.epicfight.network.client;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.server.SPTogglePlayerMode;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class CPToggleMode {
	private boolean battleMode;
	
	public CPToggleMode(boolean battleMode) {
		this.battleMode = battleMode;
	}

	public static CPToggleMode fromBytes(FriendlyByteBuf buf) {
		return new CPToggleMode(buf.readBoolean());
	}

	public static void toBytes(CPToggleMode msg, FriendlyByteBuf buf) {
		buf.writeBoolean(msg.battleMode);
	}
	
	public static void handle(CPToggleMode msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(()->{
			ServerPlayer player = ctx.get().getSender();

			if (player != null) {
				PlayerPatch<?> playerpatch = (PlayerPatch<?>) player.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				
				if (playerpatch != null) {
					playerpatch.setBattleMode(msg.battleMode);
					EpicFightNetworkManager.sendToAllPlayerTrackingThisEntity(new SPTogglePlayerMode(player.getId(), msg.battleMode), player);
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}
}