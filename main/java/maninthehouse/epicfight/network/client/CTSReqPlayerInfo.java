package maninthehouse.epicfight.network.client;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;

import io.netty.buffer.ByteBuf;
import maninthehouse.epicfight.animation.LivingMotion;
import maninthehouse.epicfight.animation.types.StaticAnimation;
import maninthehouse.epicfight.capabilities.ModCapabilities;
import maninthehouse.epicfight.capabilities.entity.player.ServerPlayerData;
import maninthehouse.epicfight.network.ModNetworkManager;
import maninthehouse.epicfight.network.server.STCLivingMotionChange;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CTSReqPlayerInfo implements IMessage
{
	private int entityId;
	
	public CTSReqPlayerInfo()
	{
		this.entityId = 0;
	}
	
	public CTSReqPlayerInfo(int entityId)
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
	
	public static class Handler implements IMessageHandler<CTSReqPlayerInfo, IMessage>
	{
		@Override
		public IMessage onMessage(CTSReqPlayerInfo message, MessageContext ctx)
		{
			EntityPlayerMP playerMP = ctx.getServerHandler().player;
			playerMP.getServerWorld().addScheduledTask(() -> 
			{
				ServerPlayerData playerdata = (ServerPlayerData) playerMP.getCapability(ModCapabilities.CAPABILITY_ENTITY, null);
				
				if(playerdata != null)
				{
					List<LivingMotion> motions = Lists.<LivingMotion>newArrayList();
					List<StaticAnimation> animations = Lists.<StaticAnimation>newArrayList();
					
					int i = 0;
					
					for(Map.Entry<LivingMotion, StaticAnimation> entry : playerdata.getLivingMotionEntrySet())
					{
						motions.add(entry.getKey());
						animations.add(entry.getValue());
						i++;
					}
					
					LivingMotion[] motionarr = motions.toArray(new LivingMotion[0]);
					StaticAnimation[] animationarr = animations.toArray(new StaticAnimation[0]);
					STCLivingMotionChange mg = new STCLivingMotionChange(playerdata.getOriginalEntity().getEntityId(), i);
					mg.setMotions(motionarr);
					mg.setAnimations(animationarr);
					ModNetworkManager.sendToPlayer(mg, playerMP);
				}
			});
			
			return null;
		}	
	}
}
