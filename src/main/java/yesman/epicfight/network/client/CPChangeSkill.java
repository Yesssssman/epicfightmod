package yesman.epicfight.network.client;

import java.util.function.Supplier;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import yesman.epicfight.gameasset.Skills;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class CPChangeSkill {
	private int skillSlotIndex;
	private int itemSlotIndex;
	private String skillName;
	private boolean consumeXp;
	
	public CPChangeSkill() {
		this(0, -1, "", false);
	}
	
	public CPChangeSkill(int skillSlotIndex, int itemSlotIndex, String name, boolean consumeXp) {
		this.skillSlotIndex = skillSlotIndex;
		this.itemSlotIndex = itemSlotIndex;
		this.skillName = name;
		this.consumeXp = consumeXp;
	}
	
	public static CPChangeSkill fromBytes(PacketBuffer buf) {
		CPChangeSkill msg = new CPChangeSkill(buf.readInt(), buf.readInt(), buf.readUtf(), buf.readBoolean());
		return msg;
	}
	
	public static void toBytes(CPChangeSkill msg, PacketBuffer buf) {
		buf.writeInt(msg.skillSlotIndex);
		buf.writeInt(msg.itemSlotIndex);
		buf.writeUtf(msg.skillName);
		buf.writeBoolean(msg.consumeXp);
	}
	
	public static void handle(CPChangeSkill msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayerEntity serverPlayer = ctx.get().getSender();
			ServerPlayerPatch playerpatch = (ServerPlayerPatch) serverPlayer.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			
			if (playerpatch != null) {
				Skill skill = Skills.getSkill(msg.skillName);
				playerpatch.getSkill(msg.skillSlotIndex).setSkill(skill);
				
				if (skill.getCategory().learnable()) {
					playerpatch.getSkillCapability().addLearnedSkill(skill);
				}
				
				if (msg.consumeXp) {
					serverPlayer.giveExperienceLevels(-skill.getRequiredXp());
				} else {
					if (!serverPlayer.isCreative()) {
						serverPlayer.inventory.removeItem(serverPlayer.inventory.getItem(msg.itemSlotIndex));
					}
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
