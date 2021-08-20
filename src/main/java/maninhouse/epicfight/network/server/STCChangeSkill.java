package maninhouse.epicfight.network.server;

import java.util.function.Supplier;

import maninhouse.epicfight.capabilities.ModCapabilities;
import maninhouse.epicfight.client.capabilites.entity.ClientPlayerData;
import maninhouse.epicfight.gamedata.Skills;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class STCChangeSkill {
	private int slotIndex;
	private String skillName;
	
	public STCChangeSkill() {
		this(0, "");
	}
	
	public STCChangeSkill(int slotIndex, String name) {
		this.slotIndex = slotIndex;
		this.skillName = name;
	}
	
	public static STCChangeSkill fromBytes(PacketBuffer buf) {
		STCChangeSkill msg = new STCChangeSkill(buf.readInt(), buf.readString());
		return msg;
	}

	public static void toBytes(STCChangeSkill msg, PacketBuffer buf) {
		buf.writeInt(msg.slotIndex);
		buf.writeString(msg.skillName);
	}
	
	public static void handle(STCChangeSkill msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ClientPlayerEntity player = Minecraft.getInstance().player;
			ClientPlayerData playerdata = (ClientPlayerData) player.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			playerdata.getSkill(msg.slotIndex).setSkill(Skills.findSkill(msg.skillName));
		});
		ctx.get().setPacketHandled(true);
	}
}
