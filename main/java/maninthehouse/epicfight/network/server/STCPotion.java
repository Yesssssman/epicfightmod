package maninthehouse.epicfight.network.server;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class STCPotion implements IMessage
{
	private Potion effect;
	private Action action;
	private int entityId;
	
	public STCPotion()
	{
		this.effect = null;
		this.entityId = 0;
		this.action = Action.Remove;
	}
	
	public STCPotion(Potion effect, Action action, int entityId)
	{
		this.effect = effect;
		this.entityId = entityId;
		this.action = action;
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.effect = Potion.getPotionById(buf.readInt());
		this.entityId = buf.readInt();
		this.action = Action.getAction(buf.readInt());
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(Potion.getIdFromPotion(this.effect));
		buf.writeInt(this.entityId);
		buf.writeInt(this.action.getSymb());
	}
	
	public static class Handler implements IMessageHandler<STCPotion, IMessage>
	{
		@Override
		public IMessage onMessage(STCPotion message, MessageContext ctx)
		{
			Minecraft.getMinecraft().addScheduledTask(() ->
			{
				Entity entity = Minecraft.getMinecraft().world.getEntityByID(message.entityId);
				
				if(entity != null && entity instanceof EntityLivingBase)
				{
					EntityLivingBase livEntity = ((EntityLivingBase)entity);
					
					switch(message.action)
					{
					case Active:
						livEntity.addPotionEffect(new PotionEffect(message.effect, 0));
						break;
					case Remove:
						livEntity.removePotionEffect(message.effect);
						break;
					}
				}
		    });
			
			return null;
		}
	}
	
	public static enum Action
	{
		Active(0), Remove(1);
		
		int action;
		
		Action(int action)
		{
			this.action = action;
		}
		
		public int getSymb()
		{
			return action;
		}
		
		private static Action getAction(int symb)
		{
			if(symb == 0) return Active;
			else if(symb == 1) return Remove;
			else return null;
		}
	}
}
