package maninhouse.epicfight.network.server;

import java.util.function.Supplier;

import io.netty.buffer.Unpooled;
import maninhouse.epicfight.client.ClientEngine;
import maninhouse.epicfight.client.capabilites.entity.ClientPlayerData;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class STCExecuteSkill
{
	private int skillSlot;
	private boolean active;
	private PacketBuffer buffer;
	
	public STCExecuteSkill()
	{
		this(0);
	}
	
	public STCExecuteSkill(int slotIndex)
	{
		this(slotIndex, true);
	}
	
	public STCExecuteSkill(int slotIndex, boolean active)
	{
		this.skillSlot = slotIndex;
		this.active = active;
		this.buffer = new PacketBuffer(Unpooled.buffer());
	}
	
	public PacketBuffer getBuffer()
	{
		return buffer;
	}
	
	public static STCExecuteSkill fromBytes(PacketBuffer buf)
	{
		STCExecuteSkill msg = new STCExecuteSkill(buf.readInt(), buf.readBoolean());
		
		while(buf.isReadable())
		{
			msg.buffer.writeByte(buf.readByte());
		}
		
		return msg;
	}
	
	public static void toBytes(STCExecuteSkill msg, PacketBuffer buf)
	{
		buf.writeInt(msg.skillSlot);
		buf.writeBoolean(msg.active);
		
		while(msg.buffer.isReadable())
		{
			buf.writeByte(msg.buffer.readByte());
		}
	}
	
	public static void handle(STCExecuteSkill msg, Supplier<NetworkEvent.Context> ctx)
	{
		ctx.get().enqueueWork(() -> {
			ClientPlayerData playerdata = ClientEngine.INSTANCE.getPlayerData();
			
			if(msg.active)
				playerdata.getSkill(msg.skillSlot).getContaining().executeOnClient(playerdata, msg.getBuffer());
			else
				playerdata.getSkill(msg.skillSlot).getContaining().cancelOnClient(playerdata, msg.getBuffer());
		});
		ctx.get().setPacketHandled(true);
	}
}