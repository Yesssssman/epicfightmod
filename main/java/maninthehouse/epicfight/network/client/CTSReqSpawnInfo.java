package maninthehouse.epicfight.network.client;

import io.netty.buffer.ByteBuf;
import maninthehouse.epicfight.capabilities.ModCapabilities;
import maninthehouse.epicfight.capabilities.entity.mob.BipedMobData;
import maninthehouse.epicfight.network.ModNetworkManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CTSReqSpawnInfo implements IMessage
{
	private int entityId;
	
	public CTSReqSpawnInfo()
	{
		this.entityId = 0;
	}
	
	public CTSReqSpawnInfo(int entityId)
	{
		this.entityId = entityId;
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.entityId = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(this.entityId);
	}
	
	public static class Handler implements IMessageHandler<CTSReqSpawnInfo, IMessage>
	{
		@Override
		public IMessage onMessage(CTSReqSpawnInfo message, MessageContext ctx)
		{
			EntityPlayerMP playerMP = ctx.getServerHandler().player;
			playerMP.getServerWorld().addScheduledTask(() -> 
			{
				Entity entity = playerMP.getServerWorld().getEntityByID(message.entityId);
				
				if(entity != null)
				{
					BipedMobData<?> entitydata = (BipedMobData<?>) entity.getCapability(ModCapabilities.CAPABILITY_ENTITY, null);
					
					if(entitydata != null)
						ModNetworkManager.sendToPlayer(entitydata.sendInitialInformationToClient(), playerMP);
				}
			});
			
			return null;
		}
	}
}