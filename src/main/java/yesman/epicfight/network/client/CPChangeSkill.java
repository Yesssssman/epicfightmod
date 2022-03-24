package yesman.epicfight.network.client;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import yesman.epicfight.gameasset.Skills;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;

public class CPChangeSkill {
	private int slotIndex;
	private String skillName;
	private boolean consumeXp;
	
	public CPChangeSkill() {
		this(0, "", false);
	}
	
	public CPChangeSkill(int slotIndex, String name, boolean consumeXp) {
		this.slotIndex = slotIndex;
		this.skillName = name;
		this.consumeXp = consumeXp;
	}
	
	public static CPChangeSkill fromBytes(FriendlyByteBuf buf) {
		CPChangeSkill msg = new CPChangeSkill(buf.readInt(), buf.readUtf(), buf.readBoolean());
		return msg;
	}

	public static void toBytes(CPChangeSkill msg, FriendlyByteBuf buf) {
		buf.writeInt(msg.slotIndex);
		buf.writeUtf(msg.skillName);
		buf.writeBoolean(msg.consumeXp);
	}
	
	public static void handle(CPChangeSkill msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer serverPlayer = ctx.get().getSender();
			ServerPlayerPatch playerpatch = (ServerPlayerPatch) serverPlayer.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			Skill skill = Skills.findSkill(msg.skillName);
			playerpatch.getSkill(msg.slotIndex).setSkill(skill);
			
			if (skill.getCategory().modifiable()) {
				playerpatch.getSkillCapability().addLearnedSkills(skill);
			}
			
			if (msg.consumeXp) {
				serverPlayer.giveExperienceLevels(-skill.getRequiredXp());
			} else {
				if (!serverPlayer.isCreative()) {
					serverPlayer.getInventory().removeItem(serverPlayer.getInventory().getSelected());
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
