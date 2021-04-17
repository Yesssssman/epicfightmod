package maninthehouse.epicfight.network.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import maninthehouse.epicfight.client.ClientEngine;
import maninthehouse.epicfight.client.capabilites.entity.ClientPlayerData;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class STCExecuteSkill implements IMessage {
	private int skillSlot;
	private boolean active;
	private PacketBuffer buffer;

	public STCExecuteSkill() {
		this(0);
	}

	public STCExecuteSkill(int slotIndex) {
		this(slotIndex, true);
	}

	public STCExecuteSkill(int slotIndex, boolean active) {
		this.skillSlot = slotIndex;
		this.active = active;
		this.buffer = new PacketBuffer(Unpooled.buffer());
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
	
	public static class Handler implements IMessageHandler<STCExecuteSkill, IMessage> {
		@Override
		public IMessage onMessage(STCExecuteSkill message, MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask(() -> {
				ClientPlayerData playerdata = ClientEngine.INSTANCE.getPlayerData();

				if (message.active) {
					playerdata.getSkill(message.skillSlot).getContaining().executeOnClient(playerdata, message.getBuffer());
				} else {
					playerdata.getSkill(message.skillSlot).getContaining().cancelOnClient(playerdata, message.getBuffer());
				}
		    });
			
			return null;
		}
	}
}