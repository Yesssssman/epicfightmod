package yesman.epicfight.network.server;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.SkillDataManager.SkillDataKey;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class SPAddOrRemoveSkillData {
	private AddRemove type;
	private Object value;
	private int slot;
	private int keyId;
	private int entityId;
	
	public SPAddOrRemoveSkillData() {
		this.type = null;
	}
	
	public SPAddOrRemoveSkillData(SkillDataKey<?> key, int slot, Object value, AddRemove type, int entityId) {
		this.keyId = key.getId();
		this.slot = slot;
		this.type = type;
		this.value = value;
		this.entityId = entityId;
	}
	
	public static SPAddOrRemoveSkillData fromBytes(FriendlyByteBuf buf) {
		int id = buf.readInt();
		int slot = buf.readInt();
		Object value = SkillDataKey.findById(id).getValueType().readFromBuffer(buf);
		
		return new SPAddOrRemoveSkillData(SkillDataKey.findById(id), slot, value, AddRemove.values()[buf.readInt()], buf.readInt());
	}
	
	public static void toBytes(SPAddOrRemoveSkillData msg, FriendlyByteBuf buf) {
		buf.writeInt(msg.keyId);
		buf.writeInt(msg.slot);
		SkillDataKey.findById(msg.keyId).getValueType().writeToBuffer(buf, msg.value);
		buf.writeInt(msg.type.ordinal());
		buf.writeInt(msg.entityId);
	}
	
	@SuppressWarnings("deprecation")
	public static void handle(SPAddOrRemoveSkillData msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			Minecraft mc = Minecraft.getInstance();
			Entity entity = mc.level.getEntity(msg.entityId);
			
			if (entity.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).orElse(null) instanceof PlayerPatch<?> playerpatch) {
				SkillDataManager dataManager = playerpatch.getSkill(msg.slot).getDataManager();
				SkillDataKey<?> dataKey = SkillDataKey.findById(msg.keyId);
				
				if (msg.type == AddRemove.ADD) {
					dataManager.registerData(dataKey);
					dataManager.setDataRawtype(dataKey, msg.value);
				} else {
					dataManager.removeData(dataKey);
				}
			}
		});
		
		ctx.get().setPacketHandled(true);
	}
	
	public static enum AddRemove {
		ADD, REMOVE
	}
}