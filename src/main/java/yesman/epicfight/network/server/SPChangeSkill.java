package yesman.epicfight.network.server;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.gameasset.Skills;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;

public class SPChangeSkill {
	private int slotIndex;
	private String skillName;
	private SPChangeSkill.State state;
	
	public SPChangeSkill() {
		this(0, "", SPChangeSkill.State.ENABLE);
	}
	
	public SPChangeSkill(int slotIndex, String name, SPChangeSkill.State state) {
		this.slotIndex = slotIndex;
		this.skillName = name;
		this.state = state;
	}
	
	public static SPChangeSkill fromBytes(FriendlyByteBuf buf) {
		SPChangeSkill msg = new SPChangeSkill(buf.readInt(), buf.readUtf(), SPChangeSkill.State.values()[buf.readInt()]);
		return msg;
	}
	
	public static void toBytes(SPChangeSkill msg, FriendlyByteBuf buf) {
		buf.writeInt(msg.slotIndex);
		buf.writeUtf(msg.skillName);
		buf.writeInt(msg.state.ordinal());
	}
	
	public static void handle(SPChangeSkill msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			Minecraft mc = Minecraft.getInstance();
			LocalPlayerPatch playerpatch = (LocalPlayerPatch) mc.player.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY, null).orElse(null);
			
			if (playerpatch != null) {
				if (!msg.skillName.equals("")) {
					Skill skill = Skills.getSkill(msg.skillName);
					playerpatch.getSkill(msg.slotIndex).setSkill(skill);
					
					if (SkillCategory.ASSIGNMENT_MANAGER.get(msg.slotIndex).learnable()) {
						playerpatch.getSkillCapability().addLearnedSkill(skill);
					}
				}
				playerpatch.getSkill(msg.slotIndex).setDisabled(msg.state.setter);
			}
		});
		ctx.get().setPacketHandled(true);
	}
	
	public static enum State {
		ENABLE(false), DISABLE(true);
		
		boolean setter;
		
		State(boolean setter) {
			this.setter = setter;
		}
	}
}