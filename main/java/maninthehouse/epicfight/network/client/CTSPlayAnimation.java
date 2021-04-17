package maninthehouse.epicfight.network.client;

import io.netty.buffer.ByteBuf;
import maninthehouse.epicfight.animation.types.StaticAnimation;
import maninthehouse.epicfight.capabilities.ModCapabilities;
import maninthehouse.epicfight.capabilities.entity.player.ServerPlayerData;
import maninthehouse.epicfight.network.ModNetworkManager;
import maninthehouse.epicfight.network.server.STCPlayAnimation;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CTSPlayAnimation implements IMessage
{
	private int animationId;
	private float modifyTime;
	private boolean isClientSideAnimation;
	private boolean resendToSender;
	
	public CTSPlayAnimation()
	{
		this.animationId = 0;
		this.modifyTime = 0;
		this.resendToSender = false;
	}
	
	public CTSPlayAnimation(StaticAnimation animation, float modifyTime, boolean clinetOnly, boolean resendToSender)
	{
		this(animation.getId(), modifyTime, clinetOnly, resendToSender);
	}
	
	public CTSPlayAnimation(int animation, float modifyTime, boolean clinetOnly, boolean resendToSender)
	{
		this.animationId = animation;
		this.modifyTime = modifyTime;
		this.isClientSideAnimation = clinetOnly;
		this.resendToSender = resendToSender;
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.animationId = buf.readInt();
		this.modifyTime = buf.readFloat();
		this.isClientSideAnimation = buf.readBoolean();
		this.resendToSender = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(this.animationId);
		buf.writeFloat(this.modifyTime);
		buf.writeBoolean(this.isClientSideAnimation);
		buf.writeBoolean(this.resendToSender);
	}
	
	public static class Handler implements IMessageHandler<CTSPlayAnimation, IMessage>
	{
		@Override
		public IMessage onMessage(CTSPlayAnimation message, MessageContext ctx)
		{
			EntityPlayerMP playerMP = ctx.getServerHandler().player;
			playerMP.getServerWorld().addScheduledTask(() -> 
			{
				ServerPlayerData playerdata = (ServerPlayerData) playerMP.getCapability(ModCapabilities.CAPABILITY_ENTITY, null);
				if(!message.isClientSideAnimation)
					playerdata.getAnimator().playAnimation(message.animationId, message.modifyTime);
				
				ModNetworkManager.sendToAllPlayerTrackingThisEntity(new STCPlayAnimation(message.animationId, playerMP.getEntityId(), message.modifyTime), playerMP);
				
				if(message.resendToSender)
					ModNetworkManager.sendToPlayer(new STCPlayAnimation(message.animationId, playerMP.getEntityId(), message.modifyTime), playerMP);
			});
			
			return null;
		}
	}
}