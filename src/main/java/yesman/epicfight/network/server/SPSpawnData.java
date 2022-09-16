package yesman.epicfight.network.server;

import java.util.function.Supplier;

import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;

public class SPSpawnData {
	private int entityId;
	private PacketBuffer buffer;
	
	public SPSpawnData() {
		this.entityId = 0;
		this.buffer = new PacketBuffer(Unpooled.buffer());
	}
	
	public SPSpawnData(int entityId) {
		this.entityId = entityId;
		this.buffer = new PacketBuffer(Unpooled.buffer());
	}
	
	public PacketBuffer getBuffer() {
		return this.buffer;
	}
	
	public static SPSpawnData fromBytes(PacketBuffer buf) {
		SPSpawnData msg = new SPSpawnData(buf.readInt());

		while (buf.isReadable()) {
			msg.buffer.writeByte(buf.readByte());
		}

		return msg;
	}
	
	public static void toBytes(SPSpawnData msg, PacketBuffer buf) {
		buf.writeInt(msg.entityId);

		while (msg.buffer.isReadable()) {
			buf.writeByte(msg.buffer.readByte());
		}
	}
	
	public static void handle(SPSpawnData msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			Minecraft mc = Minecraft.getInstance();
			Entity entity = mc.player.level.getEntity(msg.entityId);
			
			if (entity != null) {
				EntityPatch<?> playerpatch = entity.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				playerpatch.processSpawnData(msg.getBuffer());
			}
		});
		
		ctx.get().setPacketHandled(true);
	}
}