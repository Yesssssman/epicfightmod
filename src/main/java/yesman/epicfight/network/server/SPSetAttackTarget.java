package yesman.epicfight.network.server;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class SPSetAttackTarget {
	private int entityId;
	private int targetEntityId;
	
	public SPSetAttackTarget() {
		this.entityId = 0;
	}
	
	public SPSetAttackTarget(int entityId, int targetEntityId) {
		this.entityId = entityId;
		this.targetEntityId = targetEntityId;
	}
	
	public static SPSetAttackTarget fromBytes(PacketBuffer buf) {
		return new SPSetAttackTarget(buf.readInt(), buf.readInt());
	}
	
	public static void toBytes(SPSetAttackTarget msg, PacketBuffer buf) {
		buf.writeInt(msg.entityId);
		buf.writeInt(msg.targetEntityId);
	}
	
	public static void handle(SPSetAttackTarget msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(()->{
			Minecraft minecraft = Minecraft.getInstance();
			Entity entity = minecraft.level.getEntity(msg.entityId);
			Entity targetEntity = minecraft.level.getEntity(msg.targetEntityId);
			
			if (entity != null && entity instanceof MobEntity) {
				if (targetEntity == null || !(targetEntity instanceof LivingEntity)) {
					((MobEntity)entity).setTarget((LivingEntity)null);
				} else {
					((MobEntity)entity).setTarget((LivingEntity)targetEntity);
				}
			}
		});
		
		ctx.get().setPacketHandled(true);
	}
}