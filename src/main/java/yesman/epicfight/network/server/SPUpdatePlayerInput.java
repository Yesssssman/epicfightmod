package yesman.epicfight.network.server;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;

public class SPUpdatePlayerInput {
	private int entityId;
	private float forwardImpulse;
	private float leftImpulse;

	public SPUpdatePlayerInput() {
	}

	public SPUpdatePlayerInput(int entityId, float forwardImpulse, float leftImpulse) {
		this.entityId = entityId;
		this.forwardImpulse = forwardImpulse;
		this.leftImpulse = leftImpulse;
	}

	public static SPUpdatePlayerInput fromBytes(FriendlyByteBuf buf) {
		return new SPUpdatePlayerInput(buf.readInt(), buf.readFloat(), buf.readFloat());
	}

	public static void toBytes(SPUpdatePlayerInput msg, FriendlyByteBuf buf) {
		buf.writeInt(msg.entityId);
		buf.writeFloat(msg.forwardImpulse);
		buf.writeFloat(msg.leftImpulse);
	}
	
	public static void handle(SPUpdatePlayerInput msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			Minecraft mc = Minecraft.getInstance();
			Entity entity = mc.player.level.getEntity(msg.entityId);
			
			if (entity instanceof LivingEntity livingentity) {
				livingentity.xxa = msg.leftImpulse;
				livingentity.zza = msg.forwardImpulse;
			}
		});
		ctx.get().setPacketHandled(true);
	}
}