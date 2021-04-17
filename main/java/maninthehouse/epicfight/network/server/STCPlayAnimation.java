package maninthehouse.epicfight.network.server;

import io.netty.buffer.ByteBuf;
import maninthehouse.epicfight.capabilities.ModCapabilities;
import maninthehouse.epicfight.capabilities.entity.LivingData;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class STCPlayAnimation implements IMessage
{
	protected int animationId;
	protected int entityId;
	protected float modifyTime;
	protected boolean mixLayer;
	
	public STCPlayAnimation()
	{
		this.animationId = 0;
		this.entityId = 0;
		this.modifyTime = 0;
		this.mixLayer = false;
	}
	
	public STCPlayAnimation(int animation, int entityId, float modifyTime)
	{
		this(animation, entityId, modifyTime, false);
	}
	
	public STCPlayAnimation(int animation, int entityId, float modifyTime, boolean mixLayer)
	{
		this.animationId = animation;
		this.entityId = entityId;
		this.modifyTime = modifyTime;
		this.mixLayer = mixLayer;
	}
	
	public <T extends STCPlayAnimation> void onArrive()
	{
		Entity entity = Minecraft.getMinecraft().player.world.getEntityByID(entityId);
		
		if(entity == null)
			return;
		
		LivingData<?> entitydata = (LivingData<?>) entity.getCapability(ModCapabilities.CAPABILITY_ENTITY, null);
		
		if(animationId < 0)
			entitydata.getClientAnimator().offMixLayer(false);
		else
		{
			if(mixLayer)
				entitydata.getClientAnimator().playMixLayerAnimation(animationId);
			else
				entitydata.getAnimator().playAnimation(animationId, modifyTime);
		}
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.animationId = buf.readInt();
		this.entityId = buf.readInt();
		this.modifyTime = buf.readFloat();
		this.mixLayer = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(this.animationId);
		buf.writeInt(this.entityId);
		buf.writeFloat(this.modifyTime);
		buf.writeBoolean(this.mixLayer);
	}
	
	public static class Handler implements IMessageHandler<STCPlayAnimation, IMessage>
	{
		@Override
		public IMessage onMessage(STCPlayAnimation message, MessageContext ctx)
		{
			Minecraft.getMinecraft().addScheduledTask(() ->
			{
				message.onArrive();
		    });
			
			return null;
		}
	}
}