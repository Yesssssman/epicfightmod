package yesman.epicfight.world.capabilities.skill;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.HashMultimap;

import net.minecraft.nbt.CompoundTag;
import yesman.epicfight.gameasset.Skills;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class CapabilitySkill {
	public static final CapabilitySkill EMPTY = new CapabilitySkill(null);
	public final SkillContainer[] skillContainers;
	private final HashMultimap<SkillCategory, Skill> learnedSkills = HashMultimap.create();
	
	public CapabilitySkill(PlayerPatch<?> playerpatch) {
		Collection<SkillCategory> categories = SkillCategory.ASSIGNMENT_MANAGER.universalValues();
		this.skillContainers = new SkillContainer[categories.size()];
		
		for (SkillCategory slot : categories) {
			this.skillContainers[slot.universalOrdinal()] = new SkillContainer(playerpatch, slot.universalOrdinal());
		}
	}
	
	public void clear() {
		int i = 0;
		
		for (SkillContainer container : this.skillContainers) {
			if (SkillCategory.ASSIGNMENT_MANAGER.get(i).learnable()) {
				container.setSkill(null);
			}
			++i;
		}
		
		this.learnedSkills.clear();
	}
	
	public void addLearnedSkill(Skill skill) {
		SkillCategory category = skill.getCategory();
		if (!this.learnedSkills.containsKey(category) || !this.learnedSkills.get(category).contains(skill)) {
			this.learnedSkills.put(category, skill);
		}
	}
	
	public boolean removeLearnedSkill(Skill skill) {
		SkillCategory category = skill.getCategory();
		
		if (this.learnedSkills.containsKey(category)) {
			if (this.learnedSkills.remove(category, skill)) {
				if (this.learnedSkills.get(category).size() == 0) {
					this.learnedSkills.removeAll(category);
				}
				
				return true;
			}
		}
		
		return false;
	}
	
	public Collection<Skill> getLearnedSkills(SkillCategory skillCategory) {
		return this.learnedSkills.get(skillCategory);
	}
	
	public boolean hasCategory(SkillCategory skillCategory) {
		return this.learnedSkills.containsKey(skillCategory);
	}
	
	public CompoundTag toNBT() {
		CompoundTag nbt = new CompoundTag();
		
		for (SkillContainer container : this.skillContainers) {
			if (container.getSkill() != null && container.getSkill().getCategory().shouldSaved()) {
				nbt.putString(String.valueOf(container.getSkill().getCategory().universalOrdinal()), container.getSkill().toString());
			}
		}
		
		for (Map.Entry<SkillCategory, Collection<Skill>> entry : this.learnedSkills.asMap().entrySet()) {
			CompoundTag learnedNBT = new CompoundTag();
			int i = 0;
			for (Skill skill : entry.getValue()) {
				learnedNBT.putString(String.valueOf(i++), skill.toString());
			}
			nbt.put(String.valueOf("learned" + entry.getKey().universalOrdinal()), learnedNBT);
		}
		
		return nbt;
	}
	
	public void fromNBT(CompoundTag nbt) {
		int i = 0;
		for (SkillContainer container : this.skillContainers) {
			if (nbt.contains(String.valueOf(i))) {
				Skill skill = Skills.getSkill(nbt.getString(String.valueOf(i)));
				container.setSkill(skill);
				this.addLearnedSkill(skill);
			}
			i++;
		}
		
		for (SkillCategory category : SkillCategory.ASSIGNMENT_MANAGER.universalValues()) {
			if (nbt.contains("learned" + String.valueOf(category.universalOrdinal()))) {
				CompoundTag learnedNBT = nbt.getCompound("learned" + String.valueOf(category.universalOrdinal()));
				for (String key : learnedNBT.getAllKeys()) {
					this.addLearnedSkill(Skills.getSkill(learnedNBT.getString(key)));
				}
			}
		}
	}
}