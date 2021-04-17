package maninthehouse.epicfight.network.server;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class STCPlayAnimationTarget extends STCPlayAnimation
{
	protected int targetId;
	
	public STCPlayAnimationTarget()
	{
		super();
		this.targetId = 0;
	}
	
	public STCPlayAnimationTarget(int animation, int entityId, float modifyTime, int targetId)
	{
		this(animation, entityId, modifyTime, false, targetId);
	}
	
	public STCPlayAnimationTarget(int animation, int entityId, float modifyTime, boolean mixLayer, int targetId)
	{
		super(animation, entityId, modifyTime, mixLayer);
		this.targetId = targetId;
	}
	
	@Override
	public void onArrive()
	{
		super.onArrive();
		
		Entity entity = Minecraft.getMinecraft().player.world.getEntityByID(entityId);
		Entity target = Minecraft.getMinecraft().player.world.getEntityByID(targetId);
		
		if(entity instanceof EntityMob && target instanceof EntityLivingBase)
		{
			EntityMob entityliving = (EntityMob) entity;
			entityliving.setAttackTarget((EntityLivingBase) target);
		}
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		super.fromBytes(buf);
		this.targetId = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		super.toBytes(buf);
		buf.writeInt(this.targetId);
	}
	
	public static class Handler implements IMessageHandler<STCPlayAnimationTarget, IMessage>
	{
		@Override
		public IMessage onMessage(STCPlayAnimationTarget message, MessageContext ctx)
		{
			Minecraft.getMinecraft().addScheduledTask(() ->
			{
				message.onArrive();
		    });
			
			return null;
		}
	}
}