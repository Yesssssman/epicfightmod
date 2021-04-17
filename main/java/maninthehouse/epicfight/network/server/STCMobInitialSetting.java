package maninthehouse.epicfight.network.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import maninthehouse.epicfight.capabilities.ModCapabilities;
import maninthehouse.epicfight.capabilities.entity.mob.BipedMobData;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class STCMobInitialSetting implements IMessage
{
	private int entityId;
	private PacketBuffer buffer;
	
	public STCMobInitialSetting()
	{
		this.entityId = 0;
		this.buffer = new PacketBuffer(Unpooled.buffer());
	}
	
	public STCMobInitialSetting(int entityId)
	{
		this.entityId = entityId;
		this.buffer = new PacketBuffer(Unpooled.buffer());
	}
	
	public PacketBuffer getBuffer()
	{
		return this.buffer;
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.entityId = buf.readInt();
		
		while(buf.isReadable())
		{
			this.buffer.writeByte(buf.readByte());
		}
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(this.entityId);
		
		while(this.buffer.isReadable())
		{
			buf.writeByte(this.buffer.readByte());
		}
	}
	
	public static class Handler implements IMessageHandler<STCMobInitialSetting, IMessage>
	{
		@Override
		public IMessage onMessage(STCMobInitialSetting message, MessageContext ctx)
		{
			Minecraft.getMinecraft().addScheduledTask(() ->
			{
				Entity entity = Minecraft.getMinecraft().player.world.getEntityByID(message.entityId);
				if(entity != null)
				{
					if(entity != null)
					{
						BipedMobData<?> entitydata = (BipedMobData<?>) entity.getCapability(ModCapabilities.CAPABILITY_ENTITY, null);
						entitydata.clientInitialSettings(message.getBuffer());
					}
				}
		    });
			
			return null;
		}
	}
}
