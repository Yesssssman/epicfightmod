package yesman.epicfight.network.server;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import yesman.epicfight.capabilities.ModCapabilities;
import yesman.epicfight.capabilities.skill.CapabilitySkill;
import yesman.epicfight.client.capabilites.player.ClientPlayerData;
import yesman.epicfight.gamedata.Skills;

public class STCAddLearnedSkill {
	private String[] skillNames;
	private int count;
	
	public STCAddLearnedSkill() {
		this("");
	}
	
	public STCAddLearnedSkill(String... skillNames) {
		this.skillNames = skillNames;
		this.count = skillNames.length;
	}
	
	public static STCAddLearnedSkill fromBytes(PacketBuffer buf) {
		STCAddLearnedSkill msg = new STCAddLearnedSkill();
		int count  = buf.readInt();
		msg.skillNames = new String[count];
		
		for (int i = 0; i < count; i++) {
			msg.skillNames[i] = buf.readString();
		}
		
		return msg;
	}
	
	public static void toBytes(STCAddLearnedSkill msg, PacketBuffer buf) {
		buf.writeInt(msg.count);
		for (String skillName : msg.skillNames) {
			buf.writeString(skillName);
		}
	}
	
	public static void handle(STCAddLearnedSkill msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ClientPlayerEntity player = Minecraft.getInstance().player;
			ClientPlayerData playerdata = (ClientPlayerData) player.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			CapabilitySkill skillCapability = playerdata.getSkillCapability();
			for (String skillName : msg.skillNames) {
				skillCapability.addLearnedSkills(Skills.findSkill(skillName));
			}
		});
		ctx.get().setPacketHandled(true);
	}
}