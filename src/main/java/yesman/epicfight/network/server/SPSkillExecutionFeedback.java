package yesman.epicfight.network.server;

import java.util.function.Supplier;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;

public class SPSkillExecutionFeedback {
	private int skillSlot;
	private boolean active;
	private PacketBuffer buffer;
	
	public SPSkillExecutionFeedback() {
		this(0);
	}
	
	public SPSkillExecutionFeedback(int slotIndex) {
		this(slotIndex, true);
	}

	public SPSkillExecutionFeedback(int slotIndex, boolean active) {
		this.skillSlot = slotIndex;
		this.active = active;
		this.buffer = new PacketBuffer(Unpooled.buffer());
	}

	public PacketBuffer getBuffer() {
		return buffer;
	}
	
	public static SPSkillExecutionFeedback fromBytes(PacketBuffer buf) {
		SPSkillExecutionFeedback msg = new SPSkillExecutionFeedback(buf.readInt(), buf.readBoolean());

		while (buf.isReadable()) {
			msg.buffer.writeByte(buf.readByte());
		}
		
		return msg;
	}

	public static void toBytes(SPSkillExecutionFeedback msg, PacketBuffer buf) {
		buf.writeInt(msg.skillSlot);
		buf.writeBoolean(msg.active);

		while (msg.buffer.isReadable()) {
			buf.writeByte(msg.buffer.readByte());
		}
	}
	
	public static void handle(SPSkillExecutionFeedback msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			LocalPlayerPatch playerpatch = ClientEngine.instance.getPlayerPatch();
			
			if (playerpatch != null) {
				if (!msg.active) {
					playerpatch.getSkill(msg.skillSlot).getSkill().cancelOnClient(playerpatch, msg.getBuffer());
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}
}