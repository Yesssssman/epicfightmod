package yesman.epicfight.network.client;

import java.util.function.Supplier;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
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

	public static CPSetPlayerTarget fromBytes(PacketBuffer buf) {
		return new CPSetPlayerTarget(buf.readInt());
	}

	public static void toBytes(CPSetPlayerTarget msg, PacketBuffer buf) {
		buf.writeInt(msg.entityId);
	}
	
	public static void handle(CPSetPlayerTarget msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayerEntity player = ctx.get().getSender();
			
			if (player != null) {
				ServerPlayerPatch entitypatch = (ServerPlayerPatch)player.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				
				if (entitypatch != null) {
					Entity entity = entitypatch.getOriginal().level.getEntity(msg.entityId);
					
					if (entity instanceof LivingEntity) {
						entitypatch.setAttackTarget((LivingEntity)entity);
					} else if (entity == null) {
						entitypatch.setAttackTarget((LivingEntity)null);
					}
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}
}