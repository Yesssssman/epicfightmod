package yesman.epicfight.network.client;

import java.util.function.Supplier;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import yesman.epicfight.capabilities.ModCapabilities;
import yesman.epicfight.capabilities.entity.player.ServerPlayerData;

public class CTSSetPlayerTarget {
	private int entityId;

	public CTSSetPlayerTarget() {
		this.entityId = 0;
	}

	public CTSSetPlayerTarget(int entityId) {
		this.entityId = entityId;
	}

	public static CTSSetPlayerTarget fromBytes(PacketBuffer buf) {
		return new CTSSetPlayerTarget(buf.readInt());
	}

	public static void toBytes(CTSSetPlayerTarget msg, PacketBuffer buf) {
		buf.writeInt(msg.entityId);
	}
	
	public static void handle(CTSSetPlayerTarget msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(()->{
			ServerPlayerEntity player = ctx.get().getSender();
			
			if (player != null) {
				ServerPlayerData entitydata = (ServerPlayerData)player.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				
				if (msg.entityId >= 0) {
					Entity entity = entitydata.getOriginalEntity().world.getEntityByID(msg.entityId);
					
					if (entitydata != null && entity instanceof LivingEntity) {
						entitydata.setAttackTarget((LivingEntity)entity);
					}
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}
}