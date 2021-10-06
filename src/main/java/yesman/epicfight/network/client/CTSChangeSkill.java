package yesman.epicfight.network.client;

import java.util.function.Supplier;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import yesman.epicfight.capabilities.ModCapabilities;
import yesman.epicfight.capabilities.entity.player.ServerPlayerData;
import yesman.epicfight.gamedata.Skills;

public class CTSChangeSkill {
	private int slotIndex;
	private String skillName;
	
	public CTSChangeSkill() {
		this(0, "");
	}
	
	public CTSChangeSkill(int slotIndex, String name) {
		this.slotIndex = slotIndex;
		this.skillName = name;
	}
	
	public static CTSChangeSkill fromBytes(PacketBuffer buf) {
		CTSChangeSkill msg = new CTSChangeSkill(buf.readInt(), buf.readString());
		return msg;
	}

	public static void toBytes(CTSChangeSkill msg, PacketBuffer buf) {
		buf.writeInt(msg.slotIndex);
		buf.writeString(msg.skillName);
	}
	
	public static void handle(CTSChangeSkill msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayerEntity serverPlayer = ctx.get().getSender();
			ServerPlayerData playerdata = (ServerPlayerData) serverPlayer.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			playerdata.getSkill(msg.slotIndex).setSkill(Skills.findSkill(msg.skillName));
			if (!serverPlayer.isCreative()) {
				serverPlayer.inventory.removeStackFromSlot(serverPlayer.inventory.currentItem);
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
