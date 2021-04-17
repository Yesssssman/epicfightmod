package maninthehouse.epicfight.network.server;

import io.netty.buffer.ByteBuf;
import maninthehouse.epicfight.capabilities.ModCapabilities;
import maninthehouse.epicfight.capabilities.entity.player.PlayerData;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class STCNotifyPlayerYawChanged implements IMessage
{
	private int entityId;
	private float yaw;
	
	public STCNotifyPlayerYawChanged()
	{
		this.entityId = 0;
		this.yaw = 0;
	}
	
	public STCNotifyPlayerYawChanged(int entityId, float yaw)
	{
		this.entityId = entityId;
		this.yaw = yaw;
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.entityId = buf.readInt();
		this.yaw = buf.readFloat();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(this.entityId);
		buf.writeFloat(this.yaw);
	}
	
	public static class Handler implements IMessageHandler<STCNotifyPlayerYawChanged, IMessage>
	{
		@Override
		public IMessage onMessage(STCNotifyPlayerYawChanged message, MessageContext ctx)
		{
			Minecraft.getMinecraft().addScheduledTask(() ->
			{
				Entity entity = Minecraft.getMinecraft().player.world.getEntityByID(message.entityId);
				
				if(entity != null)
				{
					PlayerData<?> entitydata = (PlayerData<?>) entity.getCapability(ModCapabilities.CAPABILITY_ENTITY, null);
					
					if(entitydata != null)
					{
						entitydata.changeYaw(message.yaw);
					}
				}
		    });
			
			return null;
		}
	}
}