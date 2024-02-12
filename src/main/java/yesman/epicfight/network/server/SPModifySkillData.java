package yesman.epicfight.network.server;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class SPModifySkillData {
	private final Object value;
	private int slot;
	private int keyId;
	private int entityId;
	
	public SPModifySkillData() {
		this.value = null;
	}
	
	public SPModifySkillData(SkillDataKey<?> key, int slot, Object value, int entityId) {
		this.keyId = key.getId();
		this.slot = slot;
		this.value = value;
		this.entityId = entityId;
	}
	
	public static SPModifySkillData fromBytes(FriendlyByteBuf buf) {
		int id = buf.readInt();
		int slot = buf.readInt();
		int entityId = buf.readInt();
		Object value = SkillDataKey.byId(id).readFromBuffer(buf);
		
		return new SPModifySkillData(SkillDataKey.byId(id), slot, value, entityId);
	}
	
	public static void toBytes(SPModifySkillData msg, FriendlyByteBuf buf) {
		buf.writeInt(msg.keyId);
		buf.writeInt(msg.slot);
		buf.writeInt(msg.entityId);
		SkillDataKey.byId(msg.keyId).writeToBuffer(buf, msg.value);
	}
	
	@SuppressWarnings("deprecation")
	public static void handle(SPModifySkillData msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			Minecraft mc = Minecraft.getInstance();
			Entity entity = mc.level.getEntity(msg.entityId);
			
			if (entity.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).orElse(null) instanceof PlayerPatch<?> playerpatch) {
				SkillDataManager dataManager = playerpatch.getSkill(msg.slot).getDataManager();
				SkillDataKey<?> dataKey = SkillDataKey.byId(msg.keyId);
				
				dataManager.setDataRawtype(dataKey, msg.value);
			}
		});
		ctx.get().setPacketHandled(true);
	}
}