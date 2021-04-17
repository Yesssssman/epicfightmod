package maninthehouse.epicfight.network.server;

import io.netty.buffer.ByteBuf;
import maninthehouse.epicfight.animation.LivingMotion;
import maninthehouse.epicfight.animation.types.StaticAnimation;
import maninthehouse.epicfight.capabilities.ModCapabilities;
import maninthehouse.epicfight.capabilities.entity.LivingData;
import maninthehouse.epicfight.client.animation.AnimatorClient;
import maninthehouse.epicfight.gamedata.Animations;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class STCLivingMotionChange implements IMessage
{
	private int entityId;
	private int count;
	
	private LivingMotion[] motion;
	private StaticAnimation[] animation;
	
	public STCLivingMotionChange()
	{
		this.entityId = 0;
		this.count = 0;
	}
	
	public STCLivingMotionChange(int entityId, int count)
	{
		this.entityId = entityId;
		this.count = count;
		this.motion = new LivingMotion[0];
		this.animation = new StaticAnimation[0];
	}
	
	public void setAnimations(StaticAnimation... animation)
	{
		this.animation = animation;
	}
	
	public void setMotions(LivingMotion... motion)
	{
		this.motion = motion;
	}
	
	public LivingMotion[] getMotions()
	{
		return motion;
	}
	
	public StaticAnimation[] getAnimations()
	{
		return animation;
	}
	
	@Override
	public void fromBytes(ByteBuf buf)
	{
		this.entityId = buf.readInt();
		this.count = buf.readInt();
		
		LivingMotion[] motionarr = new LivingMotion[this.count];
		StaticAnimation[] idarr = new StaticAnimation[this.count];
		
		for(int i = 0; i < this.count; i++)
			motionarr[i] = LivingMotion.values()[buf.readInt()];
			
		for(int i = 0; i < this.count; i++)
			idarr[i] = Animations.findAnimationDataById(buf.readInt());
		
		this.motion = motionarr;
		this.animation = idarr;
	}

	@Override
	public void toBytes(ByteBuf buf)
	{
		buf.writeInt(this.entityId);
		buf.writeInt(this.count);
		
		for(LivingMotion motion : this.motion)
			buf.writeInt(motion.getId());
		
		for(StaticAnimation anim : this.animation)
			buf.writeInt(anim.getId());
	}
	
	public static class Handler implements IMessageHandler<STCLivingMotionChange, IMessage>
	{
		@Override
		public IMessage onMessage(STCLivingMotionChange message, MessageContext ctx)
		{
			Minecraft.getMinecraft().addScheduledTask(() ->
			{
				Entity entity = Minecraft.getMinecraft().player.world.getEntityByID(message.entityId);
				if(entity != null)
				{
					LivingData<?> entitydata = (LivingData<?>) entity.getCapability(ModCapabilities.CAPABILITY_ENTITY, null);
					AnimatorClient animator = entitydata.getClientAnimator();
					animator.resetMixMotion();
					animator.offMixLayer(false);
					animator.resetModifiedLivingMotions();
					
					for(int i = 0; i < message.count; i++)
						entitydata.getClientAnimator().addModifiedLivingMotion(message.motion[i], message.animation[i]);
				}
		    });
			
			return null;
		}
	}
}