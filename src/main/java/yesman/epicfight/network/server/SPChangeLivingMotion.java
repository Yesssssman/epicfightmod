package yesman.epicfight.network.server;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.animation.ClientAnimator;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class SPChangeLivingMotion {
	private int entityId;
	private int count;
	private LivingMotion[] motion;
	private StaticAnimation[] animation;
	private SPPlayAnimation.Layer layer;
	
	public SPChangeLivingMotion() {
		this.entityId = 0;
		this.count = 0;
	}
	
	public SPChangeLivingMotion(int entityId, int count, SPPlayAnimation.Layer layer) {
		this.entityId = entityId;
		this.count = count;
		this.motion = new LivingMotion[0];
		this.animation = new StaticAnimation[0];
		this.layer = layer;
	}
	
	public void setAnimations(StaticAnimation... animation) {
		this.animation = animation;
	}

	public void setMotions(LivingMotion... motion) {
		this.motion = motion;
	}

	public LivingMotion[] getMotions() {
		return this.motion;
	}

	public StaticAnimation[] getAnimations() {
		return this.animation;
	}

	public static SPChangeLivingMotion fromBytes(FriendlyByteBuf buf) {
		SPChangeLivingMotion msg = new SPChangeLivingMotion(buf.readInt(), buf.readInt(), SPPlayAnimation.Layer.values()[buf.readInt()]);
		LivingMotion[] motionarr = new LivingMotion[msg.count];
		StaticAnimation[] idarr = new StaticAnimation[msg.count];
		
		for(int i = 0; i < msg.count; i++) {
			motionarr[i] = LivingMotion.values()[buf.readInt()];
		}
		
		for(int i = 0; i < msg.count; i++) {
			idarr[i] = EpicFightMod.getInstance().animationManager.findAnimation(buf.readInt(), buf.readInt());
		}
		
		msg.motion = motionarr;
		msg.animation = idarr;
		
		return msg;
	}

	public static void toBytes(SPChangeLivingMotion msg, FriendlyByteBuf buf) {
		buf.writeInt(msg.entityId);
		buf.writeInt(msg.count);
		buf.writeInt(msg.layer.ordinal());
		
		for(LivingMotion motion : msg.motion) {
			buf.writeInt(motion.getId());
		}
		
		for(StaticAnimation anim : msg.animation) {
			buf.writeInt(anim.getNamespaceId());
			buf.writeInt(anim.getId());
		}
	}
	
	public static void handle(SPChangeLivingMotion msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			Entity entity = Minecraft.getInstance().player.level.getEntity(msg.entityId);
			if (entity != null) {
				LivingEntityPatch<?> playerpatch = (LivingEntityPatch<?>) entity.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				ClientAnimator animator = playerpatch.getClientAnimator();
				animator.resetCompositeMotions();
				animator.resetCompositeMotion();
				
				for (int i = 0; i < msg.count; i++) {
					switch (msg.layer) {
					case BASE_LAYER:
						playerpatch.getClientAnimator().addLivingAnimation(msg.motion[i], msg.animation[i]);
						break;
					case COMPOSITE_LAYER:
						playerpatch.getClientAnimator().addCompositeAnimation(msg.motion[i], msg.animation[i]);
						break;
					}
				}
			}
		});
		
		ctx.get().setPacketHandled(true);
	}
}