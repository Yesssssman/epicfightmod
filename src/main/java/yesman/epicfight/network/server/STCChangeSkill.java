package yesman.epicfight.network.server;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import yesman.epicfight.capabilities.ModCapabilities;
import yesman.epicfight.client.capabilites.player.ClientPlayerData;
import yesman.epicfight.gamedata.Skills;

public class STCChangeSkill {
	private int slotIndex;
	private String skillName;
	private STCChangeSkill.State state;
	
	public STCChangeSkill() {
		this(0, "", STCChangeSkill.State.ENABLE);
	}
	
	public STCChangeSkill(int slotIndex, String name, STCChangeSkill.State state) {
		this.slotIndex = slotIndex;
		this.skillName = name;
		this.state = state;
	}
	
	public static STCChangeSkill fromBytes(PacketBuffer buf) {
		STCChangeSkill msg = new STCChangeSkill(buf.readInt(), buf.readString(), STCChangeSkill.State.values()[buf.readInt()]);
		return msg;
	}
	
	public static void toBytes(STCChangeSkill msg, PacketBuffer buf) {
		buf.writeInt(msg.slotIndex);
		buf.writeString(msg.skillName);
		buf.writeInt(msg.state.ordinal());
	}
	
	public static void handle(STCChangeSkill msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ClientPlayerEntity player = Minecraft.getInstance().player;
			ClientPlayerData playerdata = (ClientPlayerData) player.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			if (!msg.skillName.equals("empty")) {
				playerdata.getSkill(msg.slotIndex).setSkill(Skills.findSkill(msg.skillName));
			}
			playerdata.getSkill(msg.slotIndex).setDisabled(msg.state.setter);
		});
		ctx.get().setPacketHandled(true);
	}
	
	public static enum State {
		ENABLE(false), DISABLE(true);
		
		boolean setter;
		
		State(boolean setter) {
			this.setter = setter;
		}
	}
}