package maninthehouse.epicfight.network.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import maninthehouse.epicfight.capabilities.ModCapabilities;
import maninthehouse.epicfight.capabilities.entity.player.ServerPlayerData;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CTSExecuteSkill implements IMessage {
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
		if (pb != null)
			this.buffer.writeBytes(pb);
	}

	public PacketBuffer getBuffer() {
		return buffer;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.skillSlot = buf.readInt();
		this.active = buf.readBoolean();

		while (buf.isReadable()) {
			this.buffer.writeByte(buf.readByte());
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.skillSlot);
		buf.writeBoolean(this.active);

		while (this.buffer.isReadable()) {
			buf.writeByte(this.buffer.readByte());
		}
	}

	public static class Handler implements IMessageHandler<CTSExecuteSkill, IMessage> {
		@Override
		public IMessage onMessage(CTSExecuteSkill message, MessageContext ctx) {
			EntityPlayerMP playerMP = ctx.getServerHandler().player;
			playerMP.getServerWorld().addScheduledTask(() -> {
				ServerPlayerData playerdata = (ServerPlayerData) playerMP
						.getCapability(ModCapabilities.CAPABILITY_ENTITY, null);
				if (message.active) {
					playerdata.getSkill(message.skillSlot).requestExecute(playerdata, message.getBuffer());
				} else {
					playerdata.getSkill(message.skillSlot).getContaining().cancelOnServer(playerdata,message.getBuffer());
				}
			});

			return null;
		}
	}
}