package yesman.epicfight.network.client;

import java.util.function.Supplier;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import yesman.epicfight.capabilities.ModCapabilities;
import yesman.epicfight.capabilities.entity.player.ServerPlayerData;
import yesman.epicfight.gamedata.Skills;
import yesman.epicfight.skill.Skill;

public class CTSChangeSkill {
	private int slotIndex;
	private String skillName;
	private boolean consumeXp;
	
	public CTSChangeSkill() {
		this(0, "", false);
	}
	
	public CTSChangeSkill(int slotIndex, String name, boolean consumeXp) {
		this.slotIndex = slotIndex;
		this.skillName = name;
		this.consumeXp = consumeXp;
	}
	
	public static CTSChangeSkill fromBytes(PacketBuffer buf) {
		CTSChangeSkill msg = new CTSChangeSkill(buf.readInt(), buf.readString(), buf.readBoolean());
		return msg;
	}

	public static void toBytes(CTSChangeSkill msg, PacketBuffer buf) {
		buf.writeInt(msg.slotIndex);
		buf.writeString(msg.skillName);
		buf.writeBoolean(msg.consumeXp);
	}
	
	public static void handle(CTSChangeSkill msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayerEntity serverPlayer = ctx.get().getSender();
			ServerPlayerData playerdata = (ServerPlayerData) serverPlayer.getCapability(ModCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			Skill skill = Skills.findSkill(msg.skillName);
			playerdata.getSkill(msg.slotIndex).setSkill(skill);
			
			if (skill.getCategory().modifiable()) {
				playerdata.getSkillCapability().addLearnedSkills(skill);
			}
			
			if (!serverPlayer.isCreative()) {
				serverPlayer.inventory.removeStackFromSlot(serverPlayer.inventory.currentItem);
			}
			
			if (msg.consumeXp) {
				serverPlayer.addExperienceLevel(-skill.getRequiredXp());
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
