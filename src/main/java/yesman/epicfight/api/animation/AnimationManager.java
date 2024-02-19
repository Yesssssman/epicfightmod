package yesman.epicfight.api.animation;

import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.fml.ModLoader;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.animation.AnimationDataReader;
import yesman.epicfight.api.data.reloader.SkillManager;
import yesman.epicfight.api.forgeevent.AnimationRegistryEvent;
import yesman.epicfight.main.EpicFightMod;

public class AnimationManager extends SimplePreparableReloadListener<Map<Integer, Map<Integer, StaticAnimation>>> {
	private final Map<Integer, Map<Integer, StaticAnimation>> animationById = Maps.newHashMap();
	private final Map<ResourceLocation, StaticAnimation> animationByName = Maps.newHashMap();
	private String modid;
	private int namespaceHash;
	private int counter = 0;
	
	public StaticAnimation findAnimationById(int namespaceId, int animationId) {
		if (this.animationById.containsKey(namespaceId)) {
			Map<Integer, StaticAnimation> map = this.animationById.get(namespaceId);
			
			if (map.containsKey(animationId)) {
				return map.get(animationId);
			}
		}
		
		throw new IllegalArgumentException("Can't find animation. id: " + animationId + ", namespcae hash: " + namespaceId);
	}
	
	public StaticAnimation findAnimationByPath(String resourceLocation) {
		ResourceLocation rl = new ResourceLocation(resourceLocation);
		
		if (this.animationByName.containsKey(rl)) {
			return this.animationByName.get(rl);
		}
		
		throw new IllegalArgumentException("Can't find animation named: " + rl);
	}
	
	public void registerAnimations() {
		this.animationById.clear();
		this.animationByName.clear();
		
		Map<String, Runnable> registryMap = Maps.newHashMap();
		ModLoader.get().postEvent(new AnimationRegistryEvent(registryMap));
		
		registryMap.entrySet().forEach((entry) -> {
			EpicFightMod.LOGGER.info("Start animation registration of " + entry.getKey());
			this.modid = entry.getKey();
			this.namespaceHash = this.modid.hashCode();
			this.animationById.put(this.namespaceHash, Maps.newHashMap());
			this.counter = 0;
			entry.getValue().run();
		});
	}
	
	public void loadAnimationsOnServer() {
		this.registerAnimations();
		
		this.animationById.values().forEach((map) -> {
			map.values().forEach((animation) -> {
				animation.loadAnimation(null);
				this.setAnimationProperties(null, animation);
			});
		});
	}
	
	@Override
	protected Map<Integer, Map<Integer, StaticAnimation>> prepare(ResourceManager resourceManager, ProfilerFiller profilerIn) {
		if (EpicFightMod.isPhysicalClient()) {
			this.registerAnimations();
			
			this.animationById.values().forEach((map) -> {
				map.values().forEach((animation) -> {
					this.setAnimationProperties(resourceManager, animation);
				});
			});
		}
		
		return this.animationById;
	}
	
	@Override
	protected void apply(Map<Integer, Map<Integer, StaticAnimation>> objectIn, ResourceManager resourceManager, ProfilerFiller profilerIn) {
		objectIn.values().forEach((map) -> {
			map.values().forEach((animation) -> {
				animation.loadAnimation(resourceManager);
			});
		});
		
		SkillManager.reloadAllSkillsAnimations();
	}
	
	private void setAnimationProperties(ResourceManager resourceManager, StaticAnimation animation) {
		if (resourceManager == null) {
			return;
		}
		
		ResourceLocation location = animation.getLocation();
		String path = location.getPath();
		int last = location.getPath().lastIndexOf('/');
		
		if (last > 0) {
			ResourceLocation dataLocation = new ResourceLocation(location.getNamespace(), String.format("%s/data%s.json", path.substring(0, last), path.substring(last)));
			
			if (resourceManager.getResource(dataLocation).isPresent()) {
				AnimationDataReader.readAndApply(animation, resourceManager, resourceManager.getResource(dataLocation).get());
			}
		}
	}
	
	public StaticAnimation refreshAnimation(StaticAnimation oldAnimation) {
		return this.animationByName.get(oldAnimation.getRegistryName());
	}
	
	public String getModid() {
		return this.modid;
	}
	
	public int getNamespaceHash() {
		return this.namespaceHash;
	}
	
	public int getIdCounter() {
		return this.counter++;
	}
	
	public Map<Integer, StaticAnimation> getIdMap() {
		return this.animationById.get(this.namespaceHash);
	}
	
	public Map<ResourceLocation, StaticAnimation> getNameMap() {
		return this.animationByName;
	}
}
