package maninthehouse.epicfight.network.server;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class STCPlayAnimationTP extends STCPlayAnimationTarget
{
	protected double posX;
	protected double posY;
	protected double posZ;
	protected float yaw;
	
	public STCPlayAnimationTP()
	{
		super();
		posX = 0;
		posY = 0;
		posZ = 0;
		yaw = 0;
	}
	
	public STCPlayAnimationTP(int animation, int entityId, float modifyTime, int targetId, double posX, double posY, double posZ, float yaw)
	{
		this(animation, entityId, modifyTime, false, targetId, posX, posY, posZ, yaw);
	}
	
	public STCPlayAnimationTP(int animation, int entityId, float modifyTime, boolean mixLayer, int targetId, double posX, double posY, double posZ, float yaw)
	{
		super(animation, entityId, modifyTime, mixLayer, targetId);
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
		this.yaw = yaw;
	}
	
	@Override
	public void onArrive()
	{
		super.onArrive();
		Entity entity = Minecraft.getMinecraft().player.world.getEntityByID(this.entityId);
		entity.setPositionAndUpdate(this.posX, this.posY, this.posZ);
		entity.prevPosX = entity.posX;
		entity.prevPosY = entity.posY;
		entity.prevPosZ = entity.posZ;
		entity.lastTickPosX = entity.posX;
		entity.lastTickPosY = entity.posY;
		entity.lastTickPosZ = entity.posZ;
		entity.prevRotationYaw = yaw;
		entity.rotationYaw = yaw;
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		super.fromBytes(buf);
		this.posX = buf.readDouble();
		this.posY = buf.readDouble();
		this.posZ = buf.readDouble();
		this.yaw = buf.readFloat();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		super.toBytes(buf);
		
		buf.writeDouble(this.posX);
		buf.writeDouble(this.posY);
		buf.writeDouble(this.posZ);
		buf.writeFloat(this.yaw);
	}
	
	public static class Handler implements IMessageHandler<STCPlayAnimationTP, IMessage>
	{
		@Override
		public IMessage onMessage(STCPlayAnimationTP message, MessageContext ctx)
		{
			Minecraft.getMinecraft().addScheduledTask(() ->
			{
				message.onArrive();
		    });
			return null;
		}
	}
}