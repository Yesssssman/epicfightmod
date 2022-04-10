package yesman.epicfight.network.client;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import yesman.epicfight.network.EpicFightNetworkManager;
import yesman.epicfight.network.server.SPMobInitialize;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.mob.HumanoidMobPatch;

public class CPReqSpawnInfo {
	private int entityId;

	public CPReqSpawnInfo() {
		this.entityId = 0;
	}

	public CPReqSpawnInfo(int entityId) {
		this.entityId = entityId;
	}
	
	public static CPReqSpawnInfo fromBytes(FriendlyByteBuf buf) {
		return new CPReqSpawnInfo(buf.readInt());
	}

	public static void toBytes(CPReqSpawnInfo msg, FriendlyByteBuf buf) {
		buf.writeInt(msg.entityId);
	}
	
	public static void handle(CPReqSpawnInfo msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(()->{
			Entity entity = ctx.get().getSender().level.getEntity(msg.entityId);

			if (entity != null) {
				HumanoidMobPatch<?> entitypatch = (HumanoidMobPatch<?>) entity.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);

				if (entitypatch != null) {
					SPMobInitialize mobSet = entitypatch.sendInitialInformationToClient();
					
					if (mobSet != null) {
						EpicFightNetworkManager.sendToPlayer(mobSet, ctx.get().getSender());
					}
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}
}