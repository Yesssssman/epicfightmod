package yesman.epicfight.network.server;

import java.util.List;
import java.util.function.Supplier;

import com.google.common.collect.Lists;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import yesman.epicfight.api.data.reloader.SkillManager;
import yesman.epicfight.api.exception.DatapackException;

public class SPDatapackSyncSkill extends SPDatapackSync {
	private final List<String> learnedSkills = Lists.newArrayList();
	
	public SPDatapackSyncSkill() {
		this(0, SPDatapackSyncSkill.Type.WEAPON);
	}
	
	public SPDatapackSyncSkill(int count, SPDatapackSyncSkill.Type type) {
		super(count, type);
	}
	
	public void addLearnedSkill(List<String> newArrayList) {
		this.learnedSkills.addAll(learnedSkills);
	}
	
	public List<String> getLearnedSkills() {
		return this.learnedSkills;
	}
	
	public static SPDatapackSyncSkill fromBytes(FriendlyByteBuf buf) {
		SPDatapackSyncSkill msg = new SPDatapackSyncSkill(buf.readInt(), SPDatapackSync.Type.values()[buf.readInt()]);
		
		for (int i = 0; i < msg.count; i++) {
			msg.tags[i] = buf.readNbt();
		}
		
		int learnedSkillCount = buf.readInt();
		
		for (int i = 0; i < learnedSkillCount; i++) {
			String skillName = buf.readUtf();
			msg.learnedSkills.add(skillName);
		}
		
		return msg;
	}
	
	public static void toBytes(SPDatapackSyncSkill msg, FriendlyByteBuf buf) {
		buf.writeInt(msg.count);
		buf.writeInt(msg.type.ordinal());
		
		for (CompoundTag tag : msg.tags) {
			buf.writeNbt(tag);
		}
		
		buf.writeInt(msg.learnedSkills.size());
		
		for (String skill : msg.learnedSkills) {
			buf.writeUtf(skill);
		}
	}
	
	public static void handle(SPDatapackSyncSkill msg, Supplier<NetworkEvent.Context> ctx) {
		ctx.get().enqueueWork(() -> {
			try {
				SkillManager.processServerPacket(msg);
			} catch (Exception e) {
				e.printStackTrace();
				throw new DatapackException(e.getMessage());
			}
		});
		
		ctx.get().setPacketHandled(true);
	}
}