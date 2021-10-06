package yesman.epicfight.network.client;

import java.util.function.Supplier;

import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import yesman.epicfight.capabilities.ModCapabilities;
import yesman.epicfight.capabilities.entity.player.ServerPlayerData;
import yesman.epicfight.skill.Skill;

public class CTSExecuteSkill {
	private int skillSlot;
	private boolean active;
	private PacketBuffer buffer;

	public CTSExecuteSkill() {
		this(0);
	}

	public CTSExecuteSkill(int slotIndex) {
		this(slotIndex, true);
	}
	
	public CTSExecuteSkill(int slotIndex, boolean active) {
		this.skillSlot = slotIndex;
		this.active = active;
		this.buffer = new PacketBuffer(Unpooled.buffer());
	}
	
	public CTSExecuteSkill(int slotIndex, boolean active, PacketBuffer pb) {
		this.skillSlot = slotIndex;
		this.active = active;
		this.buffer = new PacketBuffer(Unpooled.buffer());
		if(pb != null) {
			this.buffer.writeBytes(pb);
		}
	}

	public PacketBuffer getBuffer() {
		return buffer;
	}

	public static CTSExecuteSkill fromBytes(PacketBuffer buf) {
		CTSExecuteSkill msg = new CTSExecuteSkill(buf.readInt(), buf.readBoolean());

		while (buf.isReadable()) {
			msg.buffer.writeByte(buf.readByte());
		}
		
		return msg;
	}

	public static void toBytes(CTSExecuteSkill msg, PacketBuffer buf) {
		buf.writeInt(msg.skillSlot);
		buf.writeBoolean(msg.active);

		while (msg.buffer.isReadable()) {
			buf.writeByte(msg.buffer.readByte());
		}
	}
	
	public static void handle(CTSExecuteSkill msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayerEntity serverPlayer = ctx.get().getSender();
			ServerPlayerData playerdata = (ServerPlayerData) serverPlayer.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);

			if (msg.active) {
				playerdata.getSkill(msg.skillSlot).requestExecute(playerdata, msg.getBuffer());
			} else {
				Skill contain = playerdata.getSkill(msg.skillSlot).getContaining();
				if (contain != null) {
					contain.cancelOnServer(playerdata, msg.getBuffer());
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}
}