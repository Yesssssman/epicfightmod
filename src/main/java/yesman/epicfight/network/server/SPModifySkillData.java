package yesman.epicfight.network.server;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillDataManager.SkillDataKey;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

public class SPModifySkillData {
	private Object value;
	private int slot;
	private int id;
	
	public SPModifySkillData() {
		this.value = null;
	}
	
	public SPModifySkillData(SkillDataKey<?> key, int slot, Object value) {
		this.id = key.getId();
		this.slot = slot;
		this.value = value;
	}
	
	public static SPModifySkillData fromBytes(FriendlyByteBuf buf) {
		int id = buf.readInt();
		int slot = buf.readInt();
		Object value = SkillDataKey.findById(id).getValueType().readFromBuffer(buf);
		return new SPModifySkillData(SkillDataKey.findById(id), slot, value);
	}
	
	public static void toBytes(SPModifySkillData msg, FriendlyByteBuf buf) {
		buf.writeInt(msg.id);
		buf.writeInt(msg.slot);
		SkillDataKey.findById(msg.id).getValueType().writeToBuffer(buf, msg.value);
	}
	
	public static void handle(SPModifySkillData msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			LocalPlayerPatch playerpatch = (LocalPlayerPatch) Minecraft.getInstance().player.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			if (playerpatch != null) {
				SkillDataManager dataManager = playerpatch.getSkill(msg.slot).getDataManager();
				dataManager.setData(SkillDataKey.findById(msg.id), msg.value);
			}
		});
		ctx.get().setPacketHandled(true);
	}
}