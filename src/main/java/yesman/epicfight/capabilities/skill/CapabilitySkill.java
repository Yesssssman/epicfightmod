package yesman.epicfight.capabilities.skill;

import net.minecraft.nbt.CompoundNBT;
import yesman.epicfight.capabilities.entity.player.PlayerData;
import yesman.epicfight.gamedata.Skills;
import yesman.epicfight.skill.SkillCategory;
import yesman.epicfight.skill.SkillContainer;

public class CapabilitySkill {
	public static final CapabilitySkill EMPTY = new CapabilitySkill(null);
	public SkillContainer[] skills;
	
	public CapabilitySkill(PlayerData<?> player) {
		SkillCategory[] categories = SkillCategory.values();
		this.skills = new SkillContainer[SkillCategory.values().length];
		for (SkillCategory slot : categories) {
			this.skills[slot.getIndex()] = new SkillContainer(player, slot.getIndex());
		}
	}
	
	public CompoundNBT toNBT() {
		CompoundNBT nbt = new CompoundNBT();
		for (SkillContainer container : this.skills) {
			if (container.getContaining() != null && container.getContaining().getCategory().shouldSave()) {
				nbt.putString(String.valueOf(container.getContaining().getCategory().getIndex()), container.getContaining().getSkillName());
			}
		}
		return nbt;
	}
	
	public void fromNBT(CompoundNBT nbt) {
		if (nbt instanceof CompoundNBT) {
			CompoundNBT nbtCompound = ((CompoundNBT)nbt);
			int i = 0;
			for (SkillContainer container : this.skills) {
				if (nbtCompound.contains(String.valueOf(i))) {
					container.setSkill(Skills.findSkill(nbtCompound.getString(String.valueOf(i))));
				}
				i++;
			}
		}
	}
}