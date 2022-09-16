package yesman.epicfight.network.client;

import java.util.function.Supplier;

import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class CPExecuteSkill {
	private int skillSlot;
	private boolean active;
	private PacketBuffer buffer;

	public CPExecuteSkill() {
		this(0);
	}

	public CPExecuteSkill(int slotIndex) {
		this(slotIndex, true);
	}
	
	public CPExecuteSkill(int slotIndex, boolean active) {
		this.skillSlot = slotIndex;
		this.active = active;
		this.buffer = new PacketBuffer(Unpooled.buffer());
	}
	
	public CPExecuteSkill(int slotIndex, boolean active, PacketBuffer pb) {
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

	public static CPExecuteSkill fromBytes(PacketBuffer buf) {
		CPExecuteSkill msg = new CPExecuteSkill(buf.readInt(), buf.readBoolean());

		while (buf.isReadable()) {
			msg.buffer.writeByte(buf.readByte());
		}
		
		return msg;
	}

	public static void toBytes(CPExecuteSkill msg, PacketBuffer buf) {
		buf.writeInt(msg.skillSlot);
		buf.writeBoolean(msg.active);
		
		while (msg.buffer.isReadable()) {
			buf.writeByte(msg.buffer.readByte());
		}
	}
	
	public static void handle(CPExecuteSkill msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayerEntity serverPlayer = ctx.get().getSender();
			ServerPlayerPatch playerpatch = (ServerPlayerPatch) serverPlayer.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);

			if (msg.active) {
				playerpatch.getSkill(msg.skillSlot).requestExecute(playerpatch, msg.getBuffer());
			} else {
				Skill contain = playerpatch.getSkill(msg.skillSlot).getSkill();
				if (contain != null) {
					contain.cancelOnServer(playerpatch, msg.getBuffer());
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}
}