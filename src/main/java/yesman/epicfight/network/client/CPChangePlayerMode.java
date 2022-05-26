package yesman.epicfight.network.client;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class CPChangePlayerMode {
	private PlayerPatch.PlayerMode mode;
	
	public CPChangePlayerMode(PlayerPatch.PlayerMode mode) {
		this.mode = mode;
	}

	public static CPChangePlayerMode fromBytes(FriendlyByteBuf buf) {
		return new CPChangePlayerMode(PlayerPatch.PlayerMode.values()[buf.readInt()]);
	}

	public static void toBytes(CPChangePlayerMode msg, FriendlyByteBuf buf) {
		buf.writeInt(msg.mode.ordinal());
	}
	
	public static void handle(CPChangePlayerMode msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(()->{
			ServerPlayer player = ctx.get().getSender();
			
			if (player != null) {
				PlayerPatch<?> playerpatch = (PlayerPatch<?>) player.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				
				if (playerpatch != null) {
					playerpatch.toMode(msg.mode);
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}
}