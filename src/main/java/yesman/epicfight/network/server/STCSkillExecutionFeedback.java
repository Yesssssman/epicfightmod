package yesman.epicfight.network.server;

import java.util.function.Supplier;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.capabilites.player.ClientPlayerData;

public class STCSkillExecutionFeedback {
	private int skillSlot;
	private boolean active;
	private PacketBuffer buffer;
	
	public STCSkillExecutionFeedback() {
		this(0);
	}
	
	public STCSkillExecutionFeedback(int slotIndex) {
		this(slotIndex, true);
	}

	public STCSkillExecutionFeedback(int slotIndex, boolean active) {
		this.skillSlot = slotIndex;
		this.active = active;
		this.buffer = new PacketBuffer(Unpooled.buffer());
	}

	public PacketBuffer getBuffer() {
		return buffer;
	}
	
	public static STCSkillExecutionFeedback fromBytes(PacketBuffer buf) {
		STCSkillExecutionFeedback msg = new STCSkillExecutionFeedback(buf.readInt(), buf.readBoolean());

		while (buf.isReadable()) {
			msg.buffer.writeByte(buf.readByte());
		}
		
		return msg;
	}

	public static void toBytes(STCSkillExecutionFeedback msg, PacketBuffer buf) {
		buf.writeInt(msg.skillSlot);
		buf.writeBoolean(msg.active);

		while (msg.buffer.isReadable()) {
			buf.writeByte(msg.buffer.readByte());
		}
	}
	
	public static void handle(STCSkillExecutionFeedback msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ClientPlayerData playerdata = ClientEngine.instance.getPlayerData();
			
			if (playerdata != null) {
				if (!msg.active) {
					playerdata.getSkill(msg.skillSlot).getContaining().cancelOnClient(playerdata, msg.getBuffer());
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}
}