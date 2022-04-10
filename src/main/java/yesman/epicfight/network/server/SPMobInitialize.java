package yesman.epicfight.network.server;

import java.util.function.Supplier;

import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.mob.HumanoidMobPatch;

public class SPMobInitialize {
	private int entityId;
	private FriendlyByteBuf buffer;

	public SPMobInitialize() {
		this.entityId = 0;
		buffer = new FriendlyByteBuf(Unpooled.buffer());
	}

	public SPMobInitialize(int entityId) {
		this.entityId = entityId;
		buffer = new FriendlyByteBuf(Unpooled.buffer());
	}

	public FriendlyByteBuf getBuffer() {
		return this.buffer;
	}

	public static SPMobInitialize fromBytes(FriendlyByteBuf buf) {
		SPMobInitialize msg = new SPMobInitialize(buf.readInt());

		while (buf.isReadable()) {
			msg.buffer.writeByte(buf.readByte());
		}

		return msg;
	}

	public static void toBytes(SPMobInitialize msg, FriendlyByteBuf buf) {
		buf.writeInt(msg.entityId);

		while (msg.buffer.isReadable()) {
			buf.writeByte(msg.buffer.readByte());
		}
	}

	public static void handle(SPMobInitialize msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			Entity entity = Minecraft.getInstance().player.level.getEntity(msg.entityId);
			if (entity != null) {
				HumanoidMobPatch<?> playerpatch = (HumanoidMobPatch<?>) entity.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
				playerpatch.clientInitialSettings(msg.getBuffer());
			}
		});

		ctx.get().setPacketHandled(true);
	}
}