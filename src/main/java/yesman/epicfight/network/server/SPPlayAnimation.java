package yesman.epicfight.network.server;

import java.util.function.Supplier;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class SPPlayAnimation {
	protected int namespaceId;
	protected int animationId;
	protected int entityId;
	protected float convertTimeModifier;
	protected Layer playOn;

	public SPPlayAnimation() {
		this.animationId = 0;
		this.entityId = 0;
		this.convertTimeModifier = 0;
	}
	
	public SPPlayAnimation(StaticAnimation animation, float convertTimeModifier, LivingEntityPatch<?> entitypatch) {
		this(animation, convertTimeModifier, entitypatch, SPPlayAnimation.Layer.BASE_LAYER);
	}
	
	public SPPlayAnimation(StaticAnimation animation, float convertTimeModifier, LivingEntityPatch<?> entitypatch, SPPlayAnimation.Layer layer) {
		this(animation.getNamespaceId(), animation.getId(), entitypatch.getOriginal().getId(), convertTimeModifier, layer);
	}
	
	public SPPlayAnimation(int namespaceId, int animation, int entityId, float convertTimeModifier) {
		this(namespaceId, animation, entityId, convertTimeModifier, Layer.BASE_LAYER);
	}
	
	public SPPlayAnimation(StaticAnimation animation, int entityId, float convertTimeModifier) {
		this(animation, entityId, convertTimeModifier, Layer.BASE_LAYER);
	}
	
	public SPPlayAnimation(StaticAnimation animation, int entityId, float convertTimeModifier, Layer playOn) {
		this(animation.getNamespaceId(), animation.getId(), entityId, convertTimeModifier, playOn);
	}
	
	public SPPlayAnimation(int namespaceId, int animation, int entityId, float convertTimeModifier, Layer playOn) {
		this.namespaceId = namespaceId;
		this.animationId = animation;
		this.entityId = entityId;
		this.convertTimeModifier = convertTimeModifier;
		this.playOn = playOn;
	}
	
	public <T extends SPPlayAnimation> void onArrive() {
		Entity entity = Minecraft.getInstance().player.level.getEntity(this.entityId);
		
		if (entity == null) {
			return;
		}
		
		LivingEntityPatch<?> entitypatch = (LivingEntityPatch<?>)entity.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
		
		if (this.playOn == Layer.BASE_LAYER) {
			entitypatch.getAnimator().playAnimation(this.namespaceId, this.animationId, this.convertTimeModifier);
		} else if (this.playOn == Layer.COMPOSITE_LAYER) {
			entitypatch.getClientAnimator().playCompositeAnimation(this.namespaceId, this.animationId, this.convertTimeModifier);
		}
	}
	
	public static SPPlayAnimation fromBytes(FriendlyByteBuf buf) {
		return new SPPlayAnimation(buf.readInt(), buf.readInt(), buf.readInt(), buf.readFloat(), Layer.values()[buf.readInt()]);
	}
	
	public static void toBytes(SPPlayAnimation msg, ByteBuf buf) {
		buf.writeInt(msg.namespaceId);
		buf.writeInt(msg.animationId);
		buf.writeInt(msg.entityId);
		buf.writeFloat(msg.convertTimeModifier);
		buf.writeInt(msg.playOn.ordinal());
	}
	
	public static void handle(SPPlayAnimation msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			msg.onArrive();
		});
		
		ctx.get().setPacketHandled(true);
	}
	
	public static enum Layer {
		BASE_LAYER, COMPOSITE_LAYER;
	}
}