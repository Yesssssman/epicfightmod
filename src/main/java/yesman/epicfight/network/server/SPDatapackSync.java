package yesman.epicfight.network.server;

import java.util.function.Supplier;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import yesman.epicfight.api.data.reloader.ItemCapabilityReloadListener;
import yesman.epicfight.api.data.reloader.MobPatchReloadListener;

public class SPDatapackSync {
	private int count;
	private int index;
	private SPDatapackSync.Type type;
	private CompoundNBT[] tags;
	
	public SPDatapackSync() {
		this(0, SPDatapackSync.Type.WEAPON);
	}
	
	public SPDatapackSync(int count, SPDatapackSync.Type type) {
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
	
	public SPDatapackSync.Type getType() {
		return this.type;
	}
	
	public static SPDatapackSync fromBytes(PacketBuffer buf) {
		SPDatapackSync msg = new SPDatapackSync(buf.readInt(), SPDatapackSync.Type.values()[buf.readInt()]);
		for (int i = 0; i < msg.count; i++) {
			msg.tags[i] = buf.readNbt();
		}
		
		return msg;
	}
	
	public static void toBytes(SPDatapackSync msg, PacketBuffer buf) {
		buf.writeInt(msg.count);
		buf.writeInt(msg.type.ordinal());
		for (CompoundNBT tag : msg.tags) {
			buf.writeNbt(tag);
		}
	}
	
	public static void handle(SPDatapackSync msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			if (msg.getType() == Type.MOB) {
				MobPatchReloadListener.processServerPacket(msg);
			} else {
				ItemCapabilityReloadListener.processServerPacket(msg);
			}
		});
		
		ctx.get().setPacketHandled(true);
	}
	
	public static enum Type {
		ARMOR, WEAPON, MOB
	}
}