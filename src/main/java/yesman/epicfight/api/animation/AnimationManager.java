package yesman.epicfight.api.animation;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;

import net.minecraft.core.IdMapper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryInternal;
import net.minecraftforge.registries.IForgeRegistryModifiable;
import net.minecraftforge.registries.RegistryManager;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.animation.AnimationDataReader;
import yesman.epicfight.api.data.reloader.SkillManager;
import yesman.epicfight.api.forgeevent.AnimationRegistryEvent;
import yesman.epicfight.main.EpicFightMod;

public class AnimationManager extends SimpleJsonResourceReloadListener {
	private static final AnimationManager INSTANCE = new AnimationManager();
	
	public static AnimationManager getInstance() {
		return INSTANCE;
	}
	
	private final Map<StaticAnimation, AnimationClip> animationClips = Maps.newHashMap();
	private IForgeRegistryModifiable<StaticAnimation> animationRegistry;
	private IdMapper<StaticAnimation> animationIdMap;
	
	public StaticAnimation findAnimationById(int animationId) {
		return this.animationIdMap.byIdOrThrow(animationId);
	}
	
	public AnimationClip getStaticAnimationClip(StaticAnimation animation) {
		return this.animationClips.get(animation);
	}
	
	public StaticAnimation findAnimationByPath(String resourceLocation) {
		ResourceLocation rl = new ResourceLocation(resourceLocation);
		
		if (this.animationRegistry.containsKey(rl)) {
			return this.animationRegistry.getValue(rl);
		}
		
		throw new IllegalArgumentException("Can't find animation in path: " + rl);
	}
	
	public void registerAnimations() {
		this.animationRegistry.clear();
		
		Map<String, Runnable> registryMap = Maps.newHashMap();
		ModLoader.get().postEvent(new AnimationRegistryEvent(registryMap));
		
		registryMap.entrySet().forEach((entry) -> {
			EpicFightMod.LOGGER.info("Register animations from " + entry.getKey());
			entry.getValue().run();
		});
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
	
	@Override
	protected Map<ResourceLocation, JsonElement> prepare(ResourceManager resourceManager, ProfilerFiller profilerIn) {
		this.registerAnimations();
		
		this.animationRegistry.forEach((animation) -> {
			this.setAnimationProperties(resourceManager, animation);
		});
		
		return super.prepare(resourceManager, profilerIn);
	}
	
	@Override
	protected void apply(Map<ResourceLocation, JsonElement> objectIn, ResourceManager resourceManager, ProfilerFiller profilerIn) {
		/**
		 * Load animations that are not registered from {@link AnimationRegistryEvent} in resource packs
		 * If am animation using the same path is already registered, it will be skipped
		 */
		objectIn.values().forEach((map) -> {
			map.values().forEach((animation) -> {
				animation.loadAnimation(resourceManager);
			});
		});
		
		SkillManager.reloadAllSkillsAnimations();
	}
	
	public static AnimationRegistryCallbacks getCallBack() {
		return AnimationRegistryCallbacks.INSTANCE;
	}
	
	private static class AnimationRegistryCallbacks implements IForgeRegistry.BakeCallback<StaticAnimation>, IForgeRegistry.CreateCallback<StaticAnimation> {
		private static final AnimationRegistryCallbacks INSTANCE = new AnimationRegistryCallbacks();
		private static final ResourceLocation ANIMATION_ID_MAP = new ResourceLocation(EpicFightMod.MODID, "animationidmap");
		
		@Override
		@SuppressWarnings("unchecked")
        public void onBake(IForgeRegistryInternal<StaticAnimation> owner, RegistryManager stage) {
            IdMapper<StaticAnimation> animationIdMap = owner.getSlaveMap(ANIMATION_ID_MAP, IdMapper.class);
            
			for (StaticAnimation animation : owner) {
				animationIdMap.add(animation);
				animation.assignIdFromMap(animationIdMap);
			}
        }
		
		@Override
		public void onCreate(IForgeRegistryInternal<StaticAnimation> owner, RegistryManager stage) {
			IdMapper<StaticAnimation> animationIdMap = new IdMapper<StaticAnimation> (owner.getKeys().size());
			owner.setSlaveMap(ANIMATION_ID_MAP, animationIdMap);
			EpicFightMod.getInstance().animationManager.animationIdMap = animationIdMap;
		}
	}
	
	public void onRegistryCreated(Supplier<IForgeRegistry<StaticAnimation>> animationRegistry) {
		this.animationRegistry = (IForgeRegistryModifiable<StaticAnimation>)animationRegistry.get();
	}
}
