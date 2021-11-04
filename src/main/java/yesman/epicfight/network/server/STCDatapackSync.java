package yesman.epicfight.network.server;

import java.util.function.Supplier;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import yesman.epicfight.capabilities.item.ItemCapabilityListener;

public class STCDatapackSync {
	private int count;
	private int index;
	private STCDatapackSync.Type type;
	private CompoundNBT[] tags;
	
	public STCDatapackSync() {
		this(0, STCDatapackSync.Type.WEAPON);
	}
	
	public STCDatapackSync(int count, STCDatapackSync.Type type) {
		this.count = count;
		this.index = 0;
		this.type = type;
		this.tags = new CompoundNBT[count];
	}
	
	public void write(CompoundNBT tag) {
		this.tags[this.index] = tag;
		this.index++;
	}
	
	public CompoundNBT[] getTags() {
		return this.tags;
	}
	
	public STCDatapackSync.Type getType() {
		return this.type;
	}
	
	public static STCDatapackSync fromBytes(PacketBuffer buf) {
		STCDatapackSync msg = new STCDatapackSync(buf.readInt(), STCDatapackSync.Type.values()[buf.readInt()]);
		for (int i = 0; i < msg.count; i++) {
			msg.tags[i] = buf.readCompoundTag();
		}
		
		return msg;
	}
	
	public static void toBytes(STCDatapackSync msg, PacketBuffer buf) {
		buf.writeInt(msg.count);
		buf.writeInt(msg.type.ordinal());
		for (CompoundNBT tag : msg.tags) {
			buf.writeCompoundTag(tag);
		}
	}
	
	public static void handle(STCDatapackSync msg, Supplier<NetworkEvent.Context> ctx) {
		ItemCapabilityListener.processServerData(msg);
		ctx.get().setPacketHandled(true);
	}
	
	public static enum Type {
		ARMOR, WEAPON
	}
}