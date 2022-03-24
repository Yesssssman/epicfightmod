package yesman.epicfight.network.client;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class CPSetPlayerTarget {
	private int entityId;

	public CPSetPlayerTarget() {
		this.entityId = 0;
	}

	public CPSetPlayerTarget(int entityId) {
		this.entityId = entityId;
	}

	public static CPSetPlayerTarget fromBytes(FriendlyByteBuf buf) {
		return new CPSetPlayerTarget(buf.readInt());
	}

	public static void toBytes(CPSetPlayerTarget msg, FriendlyByteBuf buf) {
		buf.writeInt(msg.entityId);
	}
	
	public static void handle(CPSetPlayerTarget msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(()->{
			ServerPlayer player = ctx.get().getSender();
			
			if (player != null) {
				ServerPlayerPatch entitypatch = (ServerPlayerPatch)player.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				
				if (msg.entityId >= 0) {
					Entity entity = entitypatch.getOriginal().level.getEntity(msg.entityId);
					
					if (entitypatch != null && entity instanceof LivingEntity) {
						entitypatch.setAttackTarget((LivingEntity)entity);
					}
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}
}