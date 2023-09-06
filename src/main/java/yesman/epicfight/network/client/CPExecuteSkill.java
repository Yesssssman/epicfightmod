package yesman.epicfight.network.client;

import java.util.function.Supplier;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class CPExecuteSkill {
	private int skillSlot;
	private WorkType workType;
	private FriendlyByteBuf buffer;

	public CPExecuteSkill() {
		this(0);
	}

	public CPExecuteSkill(int slotIndex) {
		this(slotIndex, WorkType.ACTIVATE);
	}
	
	public CPExecuteSkill(int slotIndex, WorkType active) {
		this.skillSlot = slotIndex;
		this.workType = active;
		this.buffer = new FriendlyByteBuf(Unpooled.buffer());
	}
	
	public CPExecuteSkill(int slotIndex, WorkType active, FriendlyByteBuf pb) {
		this.skillSlot = slotIndex;
		this.workType = active;
		this.buffer = new FriendlyByteBuf(Unpooled.buffer());
		
		if (pb != null) {
			this.buffer.writeBytes(pb);
		}
	}

	public FriendlyByteBuf getBuffer() {
		return buffer;
	}

	public static CPExecuteSkill fromBytes(FriendlyByteBuf buf) {
		CPExecuteSkill msg = new CPExecuteSkill(buf.readInt(), WorkType.values()[buf.readInt()]);

		while (buf.isReadable()) {
			msg.buffer.writeByte(buf.readByte());
		}
		
		return msg;
	}

	public static void toBytes(CPExecuteSkill msg, FriendlyByteBuf buf) {
		buf.writeInt(msg.skillSlot);
		buf.writeInt(msg.workType.ordinal());
		
		while (msg.buffer.isReadable()) {
			buf.writeByte(msg.buffer.readByte());
		}
	}
	
	public static void handle(CPExecuteSkill msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer serverPlayer = ctx.get().getSender();
			ServerPlayerPatch playerpatch = EpicFightCapabilities.getEntityPatch(serverPlayer, ServerPlayerPatch.class);
			SkillContainer skillContainer = playerpatch.getSkill(msg.skillSlot);
			
			switch (msg.workType) {
			case ACTIVATE:
				skillContainer.requestExecute(playerpatch, msg.getBuffer());
				break;
			case CANCEL:
				skillContainer.requestCancel(playerpatch, msg.getBuffer());
				break;
			case CHARGING_START:
				skillContainer.requestCharging(playerpatch, msg.getBuffer());
				break;
			}
		});
		
		ctx.get().setPacketHandled(true);
	}
	
	public static enum WorkType {
		ACTIVATE, CANCEL, CHARGING_START
	}
}