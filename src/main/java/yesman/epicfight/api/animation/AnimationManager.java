package yesman.epicfight.api.animation;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ibm.icu.impl.locale.XCldrStub.ImmutableMap;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.fml.ModLoader;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.animation.types.datapack.ClipHoldingAnimation;
import yesman.epicfight.api.client.animation.ClientAnimationDataReader;
import yesman.epicfight.api.data.reloader.SkillManager;
import yesman.epicfight.api.forgeevent.AnimationRegistryEvent;
import yesman.epicfight.api.utils.ClearableIdMapper;
import yesman.epicfight.api.utils.InstantiateInvoker;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.main.EpicFightMod;

public class AnimationManager extends SimpleJsonResourceReloadListener {
	private static final AnimationManager INSTANCE = new AnimationManager();
	private static ResourceManager resourceManager = null;
	
	public static AnimationManager getInstance() {
		return INSTANCE;
	}
	
	private final Map<ResourceLocation, AnimationClip> animationClips = Maps.newHashMap();
	private final Map<ResourceLocation, StaticAnimation> animationRegistry = Maps.newHashMap();
	private final ClearableIdMapper<StaticAnimation> animationIdMap = new ClearableIdMapper<> ();
	private String currentWorkingModid;
	
	public AnimationManager() {
		super((new GsonBuilder()).create(), "animmodels/animations");
	}
	
	public StaticAnimation byId(int animationId) {
		if (!this.animationIdMap.contains(animationId)) {
			throw new IllegalArgumentException("No animation id " + animationId);
		}
		
		return this.animationIdMap.byId(animationId);
	}
	
	public StaticAnimation byKeyOrThrow(String resourceLocation) {
		return this.byKeyOrThrow(new ResourceLocation(resourceLocation));
	}
	
	public StaticAnimation byKeyOrThrow(ResourceLocation rl) {
		if (!this.animationRegistry.containsKey(rl)) {
			throw new IllegalArgumentException("No animation with registry name " + rl);
		}
		
		return this.byKey(rl);
	}
	
	public StaticAnimation byKey(ResourceLocation rl) {
		return this.animationRegistry.get(rl);
	}
	
	public AnimationClip getStaticAnimationClip(StaticAnimation animation) {
		if (!this.animationClips.containsKey(animation.getLocation())) {
			animation.loadAnimation(resourceManager);
		}
		
		return this.animationClips.get(animation.getLocation());
	}
	
	public Map<ResourceLocation, StaticAnimation> getAnimations() {
		return ImmutableMap.copyOf(this.animationRegistry);
	}
	
	public Map<ResourceLocation, StaticAnimation> getAnimations(Predicate<StaticAnimation> filter) {
		Map<ResourceLocation, StaticAnimation> filteredItems = this.animationRegistry.entrySet().stream().filter((entry) -> filter.test(entry.getValue())).reduce(Maps.newHashMap(), (map, entry) -> {
			map.put(entry.getKey(), entry.getValue());
			return map;
		}, (map1, map2) -> {
			map1.putAll(map2);
			return map1;
		});
		
		return ImmutableMap.copyOf(filteredItems);
	}
	
	public int registerAnimation(StaticAnimation staticAnimation) {
		if (this.currentWorkingModid != null) {
			if (this.animationRegistry.containsKey(staticAnimation.getRegistryName())) {
				EpicFightMod.LOGGER.error("Animation registration failed.");
				new IllegalStateException("[EpicFightMod] Animation with registry name " + staticAnimation.getRegistryName() + " already exists!").printStackTrace();
				return -1;
			}
			
			this.animationRegistry.put(staticAnimation.getRegistryName(), staticAnimation);
			int id = this.animationRegistry.size();
			this.animationIdMap.addMapping(staticAnimation, id);
			
			return id;
		}
		
		return -1;
	}
	
	/**
	 * Registers animations created by datapack edit screen
	 */
	public void registerUserAnimation(ClipHoldingAnimation animation) {
		this.animationRegistry.put(animation.getCreator().getRegistryName(), animation.cast());
	}
	
	public StaticAnimation refreshAnimation(StaticAnimation staticAnimation) {
		if (!this.animationRegistry.containsKey(staticAnimation.getRegistryName())) {
			throw new IllegalStateException("Animation refresh exception: No animation named " + staticAnimation.getRegistryName());
		}
		
		return this.animationRegistry.get(staticAnimation.getRegistryName());
	}
	
	public void loadAnimationClip(StaticAnimation animation, Function<StaticAnimation, AnimationClip> clipProvider) {
		if (!this.animationClips.containsKey(animation.getLocation())) {
			AnimationClip animationClip = clipProvider.apply(animation);
			this.animationClips.put(animation.getLocation(), animationClip);
		}
	}
	
	public void onFailed(StaticAnimation animation) {
		if (!this.animationClips.containsKey(animation.getLocation())) {
			this.animationClips.put(animation.getLocation(), AnimationClip.EMPTY_CLIP);
		}
	}
	
	public String workingModId() {
		return this.currentWorkingModid;
	}
	
	public static void readAnimationProperties(StaticAnimation animation) {
		ResourceLocation dataLocation = getAnimationDataFileLocation(animation.getLocation());
		
		resourceManager.getResource(dataLocation).ifPresent((rs) -> {
			ClientAnimationDataReader.readAndApply(animation, rs);
		});
	}
	
	@Override
	protected Map<ResourceLocation, JsonElement> prepare(ResourceManager resourceManager, ProfilerFiller profilerIn) {
		reloadResourceManager(resourceManager);
		Armatures.build(resourceManager);
		
		this.animationClips.clear();
		this.animationIdMap.clear();
		this.animationRegistry.clear();
		
		Map<String, Runnable> registryMap = Maps.newLinkedHashMap();
		ModLoader.get().postEvent(new AnimationRegistryEvent(registryMap));
		
		registryMap.entrySet().forEach((entry) -> {
			EpicFightMod.LOGGER.info("Register animations from " + entry.getKey());
			this.currentWorkingModid = entry.getKey();
			entry.getValue().run();
			
			this.currentWorkingModid = null;
		});
		
		return super.prepare(resourceManager, profilerIn);
	}
	
	@Override
	protected void apply(Map<ResourceLocation, JsonElement> objectIn, ResourceManager resourceManager, ProfilerFiller profilerIn) {
		final Map<ResourceLocation, StaticAnimation> registeredAnimation = Maps.newHashMap();
		this.animationRegistry.values().forEach(a1 -> a1.getClipHolders().forEach((a2) -> registeredAnimation.put(a2.getRegistryName(), a2)));
		
		/**
		 * Load animations that are not registered from {@link AnimationRegistryEvent}
		 * Reads from Resource Pack in physical client, Datapack in physical server.
		 */
		objectIn.entrySet().stream().filter((entry) -> !registeredAnimation.containsKey(entry.getKey()) && !entry.getKey().getPath().contains("/data/"))
									.sorted((e1, e2) -> e1.getKey().toString().compareTo(e2.getKey().toString()))
									.forEach((entry) -> {
										if (!entry.getKey().getNamespace().equals(this.currentWorkingModid)) {
											this.currentWorkingModid = entry.getKey().getNamespace();
										}
										
										try {
											readAnimationFromJson(entry.getKey(), entry.getValue().getAsJsonObject());
										} catch (Exception e) {
											EpicFightMod.LOGGER.error("Failed to load User animation " + entry.getKey() + " because of " + e + ". Skipped.");
											e.printStackTrace();
										}
									});
		
		SkillManager.reloadAllSkillsAnimations();
		
		this.animationRegistry.values().stream().reduce(Lists.<StaticAnimation>newArrayList(), (list, anim) -> {
			list.addAll(anim.getClipHolders());
			return list;
		}, (list1, list2) -> {
			list1.addAll(list2);
			return list1;
		}).forEach((animation) -> {
			animation.postInit();
			
			if (EpicFightMod.isPhysicalClient()) {
				AnimationManager.readAnimationProperties(animation);
			}
		});
	}
	
	public static ResourceLocation getAnimationDataFileLocation(ResourceLocation location) {
		int splitIdx = location.getPath().lastIndexOf('/');
		
		if (splitIdx < 0) {
			splitIdx = 0;
		}
		
		return new ResourceLocation(location.getNamespace(), String.format("%s/data%s", location.getPath().substring(0, splitIdx), location.getPath().substring(splitIdx)));
	}
	
	private static void reloadResourceManager(ResourceManager pResourceManager) {
		if (resourceManager != pResourceManager) {
			resourceManager = pResourceManager;
		}
	}
	
	public static ResourceManager getAnimationResourceManager() {
		return EpicFightMod.isPhysicalClient() ? Minecraft.getInstance().getResourceManager() : resourceManager;
	}
	
	/**************************************************
	 * User-animation loader
	 **************************************************/
	@SuppressWarnings({ "deprecation" })
	private static StaticAnimation readAnimationFromJson(ResourceLocation rl, JsonObject json) throws Exception {
		JsonElement constructorElement = json.get("constructor");
		
		if (constructorElement == null) {
			throw new IllegalStateException("No constructor information has provided in User animation " + rl);
		}
		
		JsonObject constructorObject = constructorElement.getAsJsonObject();
		String invocationCommand = constructorObject.get("invocation_command").getAsString();
		StaticAnimation animation = InstantiateInvoker.invoke(invocationCommand, StaticAnimation.class).getResult();
		JsonElement propertiesElement = json.getAsJsonObject().get("properties");
		
		if (propertiesElement != null) {
			JsonObject propertiesObject = propertiesElement.getAsJsonObject();
			
			for (Map.Entry<String, JsonElement> entry : propertiesObject.entrySet()) {
				AnimationProperty<?> propertyKey = AnimationProperty.getSerializableProperty(entry.getKey());
				Object value = propertyKey.parseFrom(entry.getValue());
				animation.addPropertyUnsafe(propertyKey, value);
			}
		}
		
		return animation;
	}
}
