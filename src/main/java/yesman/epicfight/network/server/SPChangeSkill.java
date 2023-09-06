package yesman.epicfight.network.server;

import java.util.function.Supplier;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import yesman.epicfight.api.data.reloader.SkillManager;
import yesman.epicfight.skill.SkillSlots;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillSlot;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class SPChangeSkill {
	private SkillSlot skillSlot;
	private String skillName;
	private SPChangeSkill.State state;
	
	public SPChangeSkill() {
		this(SkillSlots.BASIC_ATTACK, "", SPChangeSkill.State.ENABLE);
	}
	
	public SPChangeSkill(SkillSlot slot, String name, SPChangeSkill.State state) {
		this.skillSlot = slot;
		this.skillName = name;
		this.state = state;
	}
	
	public static SPChangeSkill fromBytes(FriendlyByteBuf buf) {
		SPChangeSkill msg = new SPChangeSkill(SkillSlot.ENUM_MANAGER.get(buf.readInt()), buf.readUtf(), SPChangeSkill.State.values()[buf.readInt()]);
		return msg;
	}
	
	public static void toBytes(SPChangeSkill msg, FriendlyByteBuf buf) {
		buf.writeInt(msg.skillSlot.universalOrdinal());
		buf.writeUtf(msg.skillName);
		buf.writeInt(msg.state.ordinal());
	}
	
	public static void handle(SPChangeSkill msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			Minecraft mc = Minecraft.getInstance();
			PlayerPatch<?> playerpatch = (PlayerPatch<?>)mc.player.getCapability(EpicFightCapabilities.CAPABILITY_ENTITY).orElse(null);
			
			if (playerpatch != null) {
				if (!msg.skillName.equals("")) {
					Skill skill = SkillManager.getSkill(msg.skillName);
					
					playerpatch.getSkill(msg.skillSlot).setSkill(skill);
					
					if (msg.skillSlot.category().learnable()) {
						playerpatch.getSkillCapability().addLearnedSkill(skill);
					}
				}
				
				playerpatch.getSkill(msg.skillSlot).setDisabled(msg.state.setter);
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