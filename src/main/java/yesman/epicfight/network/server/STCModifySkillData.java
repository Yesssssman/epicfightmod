package yesman.epicfight.network.server;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import yesman.epicfight.capabilities.ModCapabilities;
import yesman.epicfight.client.capabilites.player.ClientPlayerData;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillDataManager.SkillDataKey;

public class STCModifySkillData {
	private Object value;
	private int slot;
	private int id;
	
	public STCModifySkillData() {
		this.value = null;
	}
	
	public STCModifySkillData(SkillDataKey<?> key, int slot, Object value) {
		this.id = key.getId();
		this.slot = slot;
		this.value = value;
	}
	
	public static STCModifySkillData fromBytes(PacketBuffer buf) {
		int id = buf.readInt();
		int slot = buf.readInt();
		Object value = SkillDataKey.findById(id).getValueType().readFromBuffer(buf);
		return new STCModifySkillData(SkillDataKey.findById(id), slot, value);
	}
	
	public static void toBytes(STCModifySkillData msg, PacketBuffer buf) {
		buf.writeInt(msg.id);
		buf.writeInt(msg.slot);
		SkillDataKey.findById(msg.id).getValueType().writeToBuffer(buf, msg.value);
	}
	
	public static void handle(STCModifySkillData msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ClientPlayerData playerdata = (ClientPlayerData) Minecraft.getInstance().player.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			if (playerdata != null) {
				SkillDataManager dataManager = playerdata.getSkill(msg.slot).getDataManager();
				dataManager.setData(SkillDataKey.findById(msg.id), msg.value);
			}
		});
		ctx.get().setPacketHandled(true);
	}
}