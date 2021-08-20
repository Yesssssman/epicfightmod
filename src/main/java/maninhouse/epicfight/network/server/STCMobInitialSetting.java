package maninhouse.epicfight.network.server;

import java.util.function.Supplier;

import io.netty.buffer.Unpooled;
import maninhouse.epicfight.capabilities.ModCapabilities;
import maninhouse.epicfight.capabilities.entity.mob.BipedMobData;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class STCMobInitialSetting
{
	private int entityId;
	private PacketBuffer buffer;
	
	public STCMobInitialSetting()
	{
		this.entityId = 0;
		buffer = new PacketBuffer(Unpooled.buffer());
	}
	
	public STCMobInitialSetting(int entityId)
	{
		this.entityId = entityId;
		buffer = new PacketBuffer(Unpooled.buffer());
	}
	
	public PacketBuffer getBuffer()
	{
		return this.buffer;
	}
	
	public static STCMobInitialSetting fromBytes(PacketBuffer buf)
	{
		STCMobInitialSetting msg = new STCMobInitialSetting(buf.readInt());
		
		while(buf.isReadable())
		{
			msg.buffer.writeByte(buf.readByte());
		}
		
		return msg;
	}
	
	public static void toBytes(STCMobInitialSetting msg, PacketBuffer buf)
	{
		buf.writeInt(msg.entityId);
		
		while(msg.buffer.isReadable())
		{
			buf.writeByte(msg.buffer.readByte());
		}
	}
	
	public static void handle(STCMobInitialSetting msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(()->{
			Entity entity = Minecraft.getInstance().player.world.getEntityByID(msg.entityId);
			if(entity != null)
			{
				BipedMobData<?> entitydata = (BipedMobData<?>) entity.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				entitydata.clientInitialSettings(msg.getBuffer());
			}
		});
		
		ctx.get().setPacketHandled(true);
	}
}
