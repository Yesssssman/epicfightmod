package yesman.epicfight.network.server;

import java.util.List;
import java.util.Map;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillSlot;

public class SPDatapackSyncSkill extends SPDatapackSync {
	private List<String> learnedSkills = Lists.newArrayList();
	private Map<SkillSlot, String> skillsBySlot = Maps.newHashMap();
	
	public SPDatapackSyncSkill() {
		this(0, SPDatapackSyncSkill.Type.WEAPON);
	}
	
	public SPDatapackSyncSkill(int count, SPDatapackSyncSkill.Type type) {
		super(count, type);
	}
	
	public void putSlotSkill(SkillSlot slot, Skill skill) {
		this.skillsBySlot.put(slot, skill.toString());
	}
	
	public Map<SkillSlot, String> getSkillsBySlot() {
		return this.skillsBySlot;
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
		
		int slotSkillsCount = buf.readInt();
		
		for (int i = 0; i < slotSkillsCount; i++) {
			SkillSlot slot = SkillSlot.ENUM_MANAGER.get(buf.readInt());
			String skillName = buf.readUtf();
			
			msg.skillsBySlot.put(slot, skillName);
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
			buf.writeUtf(skill.toString());
		}
		
		buf.writeInt(msg.skillsBySlot.size());
		
		for (Map.Entry<SkillSlot, String> slotBySkillEntry : msg.skillsBySlot.entrySet()) {
			buf.writeInt(slotBySkillEntry.getKey().universalOrdinal());
			buf.writeUtf(slotBySkillEntry.getValue().toString());
		}
	}
}