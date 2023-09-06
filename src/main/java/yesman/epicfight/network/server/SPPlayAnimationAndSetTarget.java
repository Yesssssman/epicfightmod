package yesman.epicfight.network.server;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.network.NetworkEvent;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class SPPlayAnimationAndSetTarget extends SPPlayAnimation {
	protected int targetId;

	public SPPlayAnimationAndSetTarget() {
		super();
		this.targetId = 0;
	}
	
	public SPPlayAnimationAndSetTarget(int namespaceId, int animationId, int entityId, float modifyTime, int targetId) {
		super(namespaceId, animationId, entityId, modifyTime);
		this.targetId = targetId;
	}
	
	public SPPlayAnimationAndSetTarget(StaticAnimation animation, float modifyTime, LivingEntityPatch<?> entitypatch) {
		super(animation, modifyTime, entitypatch);
		this.targetId = entitypatch.getTarget().getId();
	}
	
	@Override
	public void onArrive() {
		super.onArrive();
		Minecraft mc = Minecraft.getInstance();
		Entity entity = mc.player.level().getEntity(this.entityId);
		Entity target = mc.player.level().getEntity(this.targetId);

		if (entity instanceof Mob entityliving && target instanceof LivingEntity) {
			entityliving.setTarget((LivingEntity)target);
		}
	}
	
	public static SPPlayAnimationAndSetTarget fromBytes(FriendlyByteBuf buf) {
		return new SPPlayAnimationAndSetTarget(buf.readInt(), buf.readInt(), buf.readInt(), buf.readFloat(), buf.readInt());
	}

	public static void toBytes(SPPlayAnimationAndSetTarget msg, FriendlyByteBuf buf) {
		buf.writeInt(msg.namespaceId);
		buf.writeInt(msg.animationId);
		buf.writeInt(msg.entityId);
		buf.writeFloat(msg.convertTimeModifier);
		buf.writeInt(msg.targetId);
	}

	public static void handle(SPPlayAnimationAndSetTarget msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(()->{
			msg.onArrive();
		});
		ctx.get().setPacketHandled(true);
	}
}