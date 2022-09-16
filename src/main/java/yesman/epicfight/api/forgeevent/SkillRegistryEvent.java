package yesman.epicfight.api.forgeevent;

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.lifecycle.IModBusEvent;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.skill.Skill;

public class SkillRegistryEvent extends Event implements IModBusEvent {
	private Map<ResourceLocation, Skill> skills = Maps.newHashMap();
	private Map<ResourceLocation, Skill> learnableSkills = Maps.newHashMap();
	
	public SkillRegistryEvent(Map<ResourceLocation, Skill> skills, Map<ResourceLocation, Skill> learnableSkills) {
		this.skills = skills;
		this.learnableSkills = learnableSkills;
	}
	
	public Skill registerSkill(Skill skill, boolean learnable) {
		registerIfAbsent(this.skills, skill);
		
		if (skill.getCategory().learnable() && learnable) {
			registerIfAbsent(this.learnableSkills, skill);
		}
		
		return skill;
	}
	
	private static void registerIfAbsent(Map<ResourceLocation, Skill> map, Skill skill) {
		if (map.containsKey(skill.getRegistryName())) {
			EpicFightMod.LOGGER.info("Duplicated skill name : " + skill.getRegistryName() + ". Registration was skipped.");
		} else {
			map.put(skill.getRegistryName(), skill);
		}
	}
}