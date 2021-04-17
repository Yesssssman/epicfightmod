package maninthehouse.epicfight.network.client;

import io.netty.buffer.ByteBuf;
import maninthehouse.epicfight.capabilities.ModCapabilities;
import maninthehouse.epicfight.capabilities.entity.player.PlayerData;
import maninthehouse.epicfight.network.ModNetworkManager;
import maninthehouse.epicfight.network.server.STCNotifyPlayerYawChanged;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CTSRotatePlayerYaw implements IMessage
{
	private float yaw;
	
	public CTSRotatePlayerYaw()
	{
		this.yaw = 0F;
	}
	
	public CTSRotatePlayerYaw(float yaw)
	{
		this.yaw = yaw;
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.yaw = buf.readFloat();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeFloat(this.yaw);
	}
	
	public static class Handler implements IMessageHandler<CTSRotatePlayerYaw, IMessage>
	{
		@Override
		public IMessage onMessage(CTSRotatePlayerYaw message, MessageContext ctx)
		{
			EntityPlayerMP playerMP = ctx.getServerHandler().player;
			playerMP.getServerWorld().addScheduledTask(() -> 
			{
				if(playerMP != null)
				{
					PlayerData<?> entitydata = (PlayerData<?>) playerMP.getCapability(ModCapabilities.CAPABILITY_ENTITY, null);
					
					if(entitydata != null)
					{
						ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCNotifyPlayerYawChanged(playerMP.getEntityId(), message.yaw), playerMP);
					}
				}
			});
			
			return null;
		}
	}
}