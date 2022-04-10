package yesman.epicfight.network.server;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class SPPlayAnimationInstant extends SPPlayAnimation {
	public SPPlayAnimationInstant(int namespaceId, int animation, int entityId, float convertTimeModifier, Layer playOn) {
		super(namespaceId, animation, entityId, convertTimeModifier, playOn);
	}
	
	public SPPlayAnimationInstant(StaticAnimation animation, float convertTimeModifier, LivingEntityPatch<?> entitypatch, SPPlayAnimation.Layer layer) {
		this(animation.getNamespaceId(), animation.getId(), entitypatch.getOriginal().getId(), convertTimeModifier, layer);
	}
	
	public static SPPlayAnimationInstant fromBytes(FriendlyByteBuf buf) {
		return new SPPlayAnimationInstant(buf.readInt(), buf.readInt(), buf.readInt(), buf.readFloat(), Layer.values()[buf.readInt()]);
	}
	
	@Override
	public void onArrive() {
		Entity entity = Minecraft.getInstance().player.level.getEntity(this.entityId);
		
		if (entity == null) {
			return;
		}
		
		LivingEntityPatch<?> entitypatch = (LivingEntityPatch<?>)entity.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
		entitypatch.getAnimator().playAnimationInstantly(this.namespaceId, this.animationId);
		entitypatch.getAnimator().updatePose();
		entitypatch.getAnimator().updatePose();
	}
}