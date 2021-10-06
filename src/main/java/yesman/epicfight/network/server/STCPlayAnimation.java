package yesman.epicfight.network.server;

import java.util.function.Supplier;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import yesman.epicfight.capabilities.ModCapabilities;
import yesman.epicfight.capabilities.entity.LivingData;

public class STCPlayAnimation {
	protected int namespaceId;
	protected int animationId;
	protected int entityId;
	protected float modifyTime;

	public STCPlayAnimation() {
		this.animationId = 0;
		this.entityId = 0;
		this.modifyTime = 0;
	}
	
	public STCPlayAnimation(int namespaceId, int animation, int entityId, float modifyTime) {
		this.namespaceId = namespaceId;
		this.animationId = animation;
		this.entityId = entityId;
		this.modifyTime = modifyTime;
	}
	
	public <T extends STCPlayAnimation> void onArrive() {
		Entity entity = Minecraft.getInstance().player.world.getEntityByID(this.entityId);
		if (entity == null) {
			return;
		}

		LivingData<?> entitydata = (LivingData<?>) entity.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
		
		if (this.animationId >= 0) {
			entitydata.getAnimator().playAnimation(this.namespaceId, this.animationId, this.modifyTime);
		}
	}
	
	public static STCPlayAnimation fromBytes(PacketBuffer buf) {
		return new STCPlayAnimation(buf.readInt(), buf.readInt(), buf.readInt(), buf.readFloat());
	}

	public static void toBytes(STCPlayAnimation msg, ByteBuf buf) {
		buf.writeInt(msg.namespaceId);
		buf.writeInt(msg.animationId);
		buf.writeInt(msg.entityId);
		buf.writeFloat(msg.modifyTime);
	}

	public static void handle(STCPlayAnimation msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			msg.onArrive();
		});
		ctx.get().setPacketHandled(true);
	}
}