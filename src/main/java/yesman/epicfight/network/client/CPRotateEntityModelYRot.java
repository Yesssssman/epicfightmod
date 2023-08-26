package yesman.epicfight.network.client;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.server.SPModifyPlayerData;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class CPRotateEntityModelYRot {
	private float modelYRot;

	public CPRotateEntityModelYRot() {
		this.modelYRot = 0F;
	}

	public CPRotateEntityModelYRot(float degree) {
		this.modelYRot = degree;
	}

	public static CPRotateEntityModelYRot fromBytes(FriendlyByteBuf buf) {
		return new CPRotateEntityModelYRot(buf.readFloat());
	}

	public static void toBytes(CPRotateEntityModelYRot msg, FriendlyByteBuf buf) {
		buf.writeFloat(msg.modelYRot);
	}

	public static void handle(CPRotateEntityModelYRot msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(()->{
			ServerPlayer player = ctx.get().getSender();
			
			if (player != null) {
				PlayerPatch<?> entitypatch = EpicFightCapabilities.getEntityPatch(player, PlayerPatch.class);
				
				if (entitypatch != null) {
					EpicFightNetworkManager.sendToAllPlayerTrackingThisEntity(new SPModifyPlayerData(player.getId(), msg.modelYRot), player);
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}
}