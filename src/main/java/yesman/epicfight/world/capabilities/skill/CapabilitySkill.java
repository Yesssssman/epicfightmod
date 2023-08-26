package yesman.epicfight.world.capabilities.skill;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashMultimap;

import net.minecraft.nbt.CompoundTag;
import yesman.epicfight.api.data.reloader.SkillManager;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillSlot;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

public class CapabilitySkill {
	public static final CapabilitySkill EMPTY = new CapabilitySkill(null);
	public final SkillContainer[] skillContainers;
	private final HashMultimap<SkillCategory, SkillContainer> slotByCategory = HashMultimap.create();
	private final HashMultimap<SkillCategory, Skill> learnedSkills = HashMultimap.create();
	
	public CapabilitySkill(PlayerPatch<?> playerpatch) {
		Collection<SkillSlot> slots = SkillSlot.ENUM_MANAGER.universalValues();
		this.skillContainers = new SkillContainer[slots.size()];
		
		for (SkillSlot slot : slots) {
			SkillContainer skillContainer = new SkillContainer(playerpatch, slot);
			this.skillContainers[slot.universalOrdinal()] = skillContainer;
			this.slotByCategory.put(slot.category(), skillContainer);
		}
	}
	
	public void clear() {
		int i = 0;
		
		for (SkillContainer container : this.skillContainers) {
			if (SkillSlot.ENUM_MANAGER.get(i).category().learnable()) {
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
	
	public Set<SkillContainer> getSkillContainersFor(SkillCategory skillCategory) {
		return this.slotByCategory.get(skillCategory);
	}
	
	public SkillContainer getSkillContainer(Skill skill) {
		Set<SkillContainer> containers = this.slotByCategory.get(skill.getCategory());
		
		for (SkillContainer skillContainer : containers) {
			if (skill.equals(skillContainer.getSkill())) {
				return skillContainer;
			}
		}
		
		return null;
	}
	
	public CompoundTag toNBT() {
		CompoundTag nbt = new CompoundTag();
		
		for (SkillContainer container : this.skillContainers) {
			if (container.getSkill() != null && container.getSkill().getCategory().shouldSave()) {
				nbt.putString(container.getSlot().toString().toLowerCase(Locale.ROOT), container.getSkill().toString());
			}
		}
		
		for (Map.Entry<SkillCategory, Collection<Skill>> entry : this.learnedSkills.asMap().entrySet()) {
			CompoundTag learnedNBT = new CompoundTag();
			int i = 0;
			
			for (Skill skill : entry.getValue()) {
				learnedNBT.putString(String.valueOf(i++), skill.toString());
			}
			
			nbt.put("learned:" + entry.getKey().toString().toLowerCase(Locale.ROOT), learnedNBT);
		}
		
		return nbt;
	}
	
	public void fromNBT(CompoundTag nbt) {
		for (SkillContainer container : this.skillContainers) {
			String key = container.getSlot().toString().toLowerCase(Locale.ROOT);
			
			if (nbt.contains(key)) {
				Skill skill = SkillManager.getSkill(nbt.getString(key));
				
				if (skill != null) {
					container.setSkill(skill);
					this.addLearnedSkill(skill);
				}
			}
		}
		
		for (SkillCategory category : SkillCategory.ENUM_MANAGER.universalValues()) {
			if (nbt.contains("learned:" + category.toString().toLowerCase(Locale.ROOT))) {
				CompoundTag learnedNBT = nbt.getCompound("learned:" + category.toString().toLowerCase(Locale.ROOT));
				
				for (String key : learnedNBT.getAllKeys()) {
					Skill skill = SkillManager.getSkill(learnedNBT.getString(key));
					
					if (skill != null) {
						this.addLearnedSkill(skill);
					}
				}
			}
		}
	}
}