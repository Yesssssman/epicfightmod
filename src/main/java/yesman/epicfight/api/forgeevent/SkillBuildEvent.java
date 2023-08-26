package yesman.epicfight.api.forgeevent;

import java.util.Map;
import java.util.function.Function;

import com.mojang.datafixers.util.Pair;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import yesman.epicfight.skill.Skill;

public class SkillBuildEvent extends Event {
	protected final Map<ResourceLocation, Pair<? extends Skill.Builder<?>, Function<? extends Skill.Builder<?>, ? extends Skill>>> builders;
	protected final Map<ResourceLocation, Skill> skills;
	protected final Map<ResourceLocation, Skill> learnableSkills;
	
	public SkillBuildEvent(Map<ResourceLocation, Pair<? extends Skill.Builder<?>, Function<? extends Skill.Builder<?>, ? extends Skill>>> builders,
			Map<ResourceLocation, Skill> skills, Map<ResourceLocation, Skill> learnableSkills) {
		this.builders = builders;
		this.skills = skills;
		this.learnableSkills = learnableSkills;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Skill, B extends Skill.Builder<T>> T build(String modid, String name) {
		try {
			ResourceLocation registryName = new ResourceLocation(modid, name);
			Pair<B, Function<B, T>> pair = (Pair<B, Function<B, T>>) (Object)this.builders.get(registryName);
			
			if (pair == null) {
				Exception e = new IllegalArgumentException("Can't find the skill " + registryName + " in the registry");
				e.printStackTrace();
			}
			
			T skill = pair.getSecond().apply(pair.getFirst());
			
			if (skill != null) {
				this.skills.put(registryName, skill);
				
				if (skill.getCategory().learnable()) {
					this.learnableSkills.put(registryName, skill);
				}
			}
			
			return skill;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}