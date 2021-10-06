package yesman.epicfight.animation;

import java.io.IOException;
import java.util.Map;

import com.google.common.collect.Maps;

import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.fml.event.lifecycle.IModBusEvent;
import yesman.epicfight.animation.types.StaticAnimation;
import yesman.epicfight.client.animation.AnimationDataReader;
import yesman.epicfight.gamedata.Animations;
import yesman.epicfight.main.EpicFightMod;

public class AnimationManager extends ReloadListener<Map<Integer, Map<Integer, StaticAnimation>>> {
	private final Map<Integer, Map<Integer, StaticAnimation>> animationById = Maps.newHashMap();
	private final Map<ResourceLocation, StaticAnimation> animationByName = Maps.newHashMap();
	private String modid;
	private int namespaceHash;
	private int counter = 0;
	
	public StaticAnimation findAnimation(int namespaceId, int animationId) {
		if (this.animationById.containsKey(namespaceId)) {
			Map<Integer, StaticAnimation> map = this.animationById.get(namespaceId);
			if (map.containsKey(animationId)) {
				return map.get(animationId);
			}
		}
		throw new IllegalArgumentException("Unable to find " + animationId + " from " + namespaceId);
	}
	
	public void registerAnimations() {
		Map<String, Runnable> registryMap = Maps.newHashMap();
		ModLoader.get().postEvent(new AnimationRegistryEvent(registryMap));
		registryMap.entrySet().forEach((entry) -> {
			this.modid = entry.getKey();
			this.namespaceHash = this.modid.hashCode();
			this.animationById.put(this.namespaceHash, Maps.newHashMap());
			this.counter = 0;
			entry.getValue().run();
		});
	}
	
	public void loadAnimationsInit(IResourceManager resourceManager) {
		this.animationById.values().forEach((map) -> {
			map.values().forEach((animation) -> {
				animation.loadAnimation(resourceManager);
				this.animationMetadata(resourceManager, animation);
			});
		});
	}
	
	@Override
	protected Map<Integer, Map<Integer, StaticAnimation>> prepare(IResourceManager resourceManager, IProfiler profilerIn) {
		if (EpicFightMod.isPhysicalClient()) {
			this.animationById.values().forEach((map) -> {
				map.values().forEach((animation) -> {
					this.animationMetadata(resourceManager, animation);
				});
			});
		}
		Animations.buildClient();
		
		return this.animationById;
	}
	
	@Override
	protected void apply(Map<Integer, Map<Integer, StaticAnimation>> objectIn, IResourceManager resourceManagerIn, IProfiler profilerIn) {
		objectIn.values().forEach((map) -> {
			map.values().forEach((animation) -> {
				animation.loadAnimation(resourceManagerIn);
			});
		});
	}
	
	private void animationMetadata(IResourceManager resourceManager, StaticAnimation animation) {
		if (resourceManager == null) {
			return;
		}
		ResourceLocation location = animation.getLocation();
		String path = location.getPath();
		int last = location.getPath().lastIndexOf('/');
		if (last > 0) {
			ResourceLocation dataLocation = new ResourceLocation(location.getNamespace(), String.format("%s/data%s.json", path.substring(0, last), path.substring(last)));
			if (resourceManager.hasResource(dataLocation)) {
				try {
					AnimationDataReader.readAndApply(animation, resourceManager.getResource(dataLocation));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
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
	
	public class AnimationRegistryEvent extends Event implements IModBusEvent {
		private Map<String, Runnable> registryMap;
		
		public AnimationRegistryEvent(Map<String, Runnable> registryMap) {
			this.registryMap = registryMap;
		}
		
		public Map<String, Runnable> getRegistryMap() {
			return this.registryMap;
		}
	}
}