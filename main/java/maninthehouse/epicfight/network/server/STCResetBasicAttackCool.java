package maninthehouse.epicfight.network.server;

import java.lang.reflect.Field;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class STCResetBasicAttackCool implements IMessage
{
	private static Field cooldownSeeker = ObfuscationReflectionHelper.findField(EntityLivingBase.class, "field_184617_aD");
	
	public static STCResetBasicAttackCool fromBytes(PacketBuffer buf)
	{
		STCResetBasicAttackCool msg = new STCResetBasicAttackCool();
		
		return msg;
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		
	}
	
	public static class Handler implements IMessageHandler<STCResetBasicAttackCool, IMessage>
	{
		@Override
		public IMessage onMessage(STCResetBasicAttackCool message, MessageContext ctx)
		{
			Minecraft.getMinecraft().addScheduledTask(() ->
			{
				EntityPlayerSP clientPlayer = Minecraft.getMinecraft().player;
				
				try
				{
					cooldownSeeker.setInt(clientPlayer, 10000);
				}
				catch (IllegalArgumentException | IllegalAccessException e)
				{
					e.printStackTrace();
				}
		    });
			
			return null;
		}
	}
}