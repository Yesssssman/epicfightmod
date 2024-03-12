package yesman.epicfight.api.forgeevent;

import java.util.List;
import java.util.function.Function;

import com.google.common.collect.Lists;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.event.IModBusEvent;
import yesman.epicfight.skill.Skill;

public class SkillBuildEvent extends Event implements IModBusEvent {
	private final List<ModRegistryWorker> modRegisterWorkers = Lists.newArrayList();
	
	public ModRegistryWorker createRegistryWorker(String modid) {
		ModRegistryWorker modRegisterWorker = new ModRegistryWorker(modid);
		this.modRegisterWorkers.add(modRegisterWorker);
		
		return modRegisterWorker;
	}
	
	public List<Skill> getAllSkills() {
		List<Skill> skills = Lists.newArrayList();
		
		this.modRegisterWorkers.forEach((registryWorker) -> {
			skills.addAll(registryWorker.modSkills);
		});
		
		return skills;
	}
	
	public static class ModRegistryWorker {
		private final String modid;
		private final List<Skill> modSkills = Lists.newArrayList();
		
		private ModRegistryWorker(String modid) {
			this.modid = modid;
		}
		
		public <S extends Skill, B extends Skill.Builder<S>> S build(String name, Function<B, S> constructor, B builder) {
			final ResourceLocation registryName = new ResourceLocation(this.modid, name);
			builder.setRegistryName(registryName);
			
			final S skill = constructor.apply(builder);
			
			this.modSkills.add(skill);
			
			return skill;
		}
	}
}