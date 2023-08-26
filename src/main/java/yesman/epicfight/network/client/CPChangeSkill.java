package yesman.epicfight.network.client;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import yesman.epicfight.api.data.reloader.SkillManager;
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
	
	public static CPChangeSkill fromBytes(FriendlyByteBuf buf) {
		CPChangeSkill msg = new CPChangeSkill(buf.readInt(), buf.readInt(), buf.readUtf(), buf.readBoolean());
		return msg;
	}
	
	public static void toBytes(CPChangeSkill msg, FriendlyByteBuf buf) {
		buf.writeInt(msg.skillSlotIndex);
		buf.writeInt(msg.itemSlotIndex);
		buf.writeUtf(msg.skillName);
		buf.writeBoolean(msg.consumeXp);
	}
	
	public static void handle(CPChangeSkill msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			ServerPlayer serverPlayer = ctx.get().getSender();
			ServerPlayerPatch playerpatch = EpicFightCapabilities.getEntityPatch(serverPlayer, ServerPlayerPatch.class);
			
			if (playerpatch != null) {
				Skill skill = SkillManager.getSkill(msg.skillName);
				playerpatch.getSkill(msg.skillSlotIndex).setSkill(skill);
				
				if (skill.getCategory().learnable()) {
					playerpatch.getSkillCapability().addLearnedSkill(skill);
				}
				
				if (msg.consumeXp) {
					serverPlayer.giveExperienceLevels(-skill.getRequiredXp());
				} else {
					if (!serverPlayer.isCreative()) {
						serverPlayer.getInventory().removeItem(serverPlayer.getInventory().getItem(msg.itemSlotIndex));
					}
				}
			}
		});
		ctx.get().setPacketHandled(true);
	}
}
