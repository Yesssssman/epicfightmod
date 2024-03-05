package yesman.epicfight.api.animation;

import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.Maps;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.fml.ModLoader;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.animation.AnimationDataReader;
import yesman.epicfight.api.data.reloader.SkillManager;
import yesman.epicfight.api.forgeevent.AnimationRegistryEvent;
import yesman.epicfight.api.utils.ClearableIdMapper;
import yesman.epicfight.main.EpicFightMod;

public class AnimationManager extends SimpleJsonResourceReloadListener {
	private static final AnimationManager INSTANCE = new AnimationManager();
	
	public static AnimationManager getInstance() {
		return INSTANCE;
	}
	
	private final Map<StaticAnimation, AnimationClip> animationClips = Maps.newHashMap();
	private final Map<ResourceLocation, StaticAnimation> animationRegistry = Maps.newHashMap();
	private final ClearableIdMapper<StaticAnimation> animationIdMap = new ClearableIdMapper<> ();
	private String currentWorkingModid;
	
	private ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
	
	public AnimationManager() {
		super((new GsonBuilder()).create(), "animmodels/animations");
	}
	
	public StaticAnimation byId(int animationId) {
		if (!this.animationIdMap.contains(animationId)) {
			throw new IllegalArgumentException("No animation with id " + animationId);
		}
		
		return this.animationIdMap.byId(animationId);
	}
	
	public StaticAnimation byKey(String resourceLocation) {
		return this.byKey(new ResourceLocation(resourceLocation));
	}
	
	public StaticAnimation byKey(ResourceLocation rl) {
		if (this.animationRegistry.containsKey(rl)) {
			return this.animationRegistry.get(rl);
		}
		
		throw new IllegalArgumentException("Can't find animation in path: " + rl);
	}
	
	public AnimationClip getStaticAnimationClip(StaticAnimation animation) {
		if (!this.animationClips.containsKey(animation)) {
			animation.loadAnimation(this.resourceManager);
		}
		
		return this.animationClips.get(animation);
	}
	
	public int registerAnimation(StaticAnimation staticAnimation) {
		if (this.currentWorkingModid == null) {
			throw new IllegalStateException("[EpicFightMod] You must register an animation when AnimationRegistryEvent is being called!");
		}
		
		this.animationRegistry.put(staticAnimation.getRegistryName(), staticAnimation);
		
		int id = this.animationRegistry.size();
		
		this.animationIdMap.addMapping(staticAnimation, id);
		
		return id;
	}
	
	public void loadAnimationClip(StaticAnimation animation, Function<StaticAnimation, AnimationClip> clipProvider) {
		if (!this.animationClips.containsKey(animation)) {
			AnimationClip animationClip = clipProvider.apply(animation);
			this.animationClips.put(animation, animationClip);
		}
	}
	
	private void readAnimationProperties(ResourceManager resourceManager, StaticAnimation animation) {
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
	
	public String workingModId() {
		return this.currentWorkingModid;
	}
	
	@Override
	protected Map<ResourceLocation, JsonElement> prepare(ResourceManager resourceManager, ProfilerFiller profilerIn) {
		this.animationClips.clear();
		this.animationIdMap.clear();
		this.animationRegistry.clear();
		
		Map<String, Runnable> registryMap = Maps.newLinkedHashMap();
		ModLoader.get().postEvent(new AnimationRegistryEvent(registryMap));
		
		registryMap.entrySet().forEach((entry) -> {
			EpicFightMod.LOGGER.info("Register animations from " + entry.getKey());
			this.currentWorkingModid = entry.getKey();
			entry.getValue().run();
			
			this.animationRegistry.values().forEach((animation) -> {
				this.readAnimationProperties(resourceManager, animation);
			});
			
			this.currentWorkingModid = null;
		});
		
		return super.prepare(resourceManager, profilerIn);
	}
	
	@Override
	protected void apply(Map<ResourceLocation, JsonElement> objectIn, ResourceManager resourceManager, ProfilerFiller profilerIn) {
		/**
		 * Load animations that are not registered from {@link AnimationRegistryEvent}
		 * Reads from Resource Pack in physical client, Datapack in physical server.
		 */
		for (Map.Entry<ResourceLocation, JsonElement> entry : objectIn.entrySet()) {
			boolean shouldRegisterPackAnimation = !this.animationRegistry.containsKey(entry.getKey()) && !entry.getKey().getPath().contains("/data/");
			
			if (shouldRegisterPackAnimation) {
				try {
					readAnimationFromJson(entry.getKey(), entry.getValue());
				} catch (Exception e) {
					EpicFightMod.LOGGER.error("Failed to load Runtime animation because of " + e + ". Skipped");
				}
			}
		}
		
		SkillManager.reloadAllSkillsAnimations();
	}
	
	private static StaticAnimation readAnimationFromJson(ResourceLocation rl, JsonElement json) throws Exception {
		JsonElement constructorElement = json.getAsJsonObject().get("constructor");
		
		if (constructorElement == null) {
			throw new IllegalStateException("No constructor information has provided for Runtime animation " + rl);
		}
		
		JsonObject constructorObject = constructorElement.getAsJsonObject();
		String classpath = constructorObject.get("class").getAsString();
		String arguments = constructorObject.get("arguments").getAsString();
		
		if (classpath == null) {
			throw new IllegalStateException("No class information has provided for Runtime animation " + rl);
		}
		
		if (arguments == null) {
			throw new IllegalStateException("No argument information has provided for Runtime animation " + rl);
		}
		
		String[] sArgumentArr = arguments.split(",");
		Object[] oArgumentArr = new Object[sArgumentArr.length];
		
		for (String element : sArgumentArr) {
			String[] value$type = element.split("$");
			
		}
		
		Class<?> cls = Class.forName(classpath);
		
		//cls.getconstructor
		
		return null;
	}
}
