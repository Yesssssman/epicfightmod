package maninhouse.epicfight.network.server;

import java.util.function.Supplier;

import io.netty.buffer.ByteBuf;
import maninhouse.epicfight.capabilities.ModCapabilities;
import maninhouse.epicfight.capabilities.entity.LivingData;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class STCPlayAnimation {
	protected int animationId;
	protected int entityId;
	protected float modifyTime;

	public STCPlayAnimation() {
		this.animationId = 0;
		this.entityId = 0;
		this.modifyTime = 0;
	}
	
	public STCPlayAnimation(int animation, int entityId, float modifyTime) {
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
			entitydata.getAnimator().playAnimation(this.animationId, this.modifyTime);
		}
	}
	
	public static STCPlayAnimation fromBytes(PacketBuffer buf) {
		return new STCPlayAnimation(buf.readInt(), buf.readInt(), buf.readFloat());
	}

	public static void toBytes(STCPlayAnimation msg, ByteBuf buf) {
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