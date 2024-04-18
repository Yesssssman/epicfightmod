package yesman.epicfight.api.animation;

import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ibm.icu.impl.locale.XCldrStub.ImmutableMap;
import com.mojang.datafixers.util.Pair;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.fml.ModLoader;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.animation.ClientAnimationDataReader;
import yesman.epicfight.api.collider.Collider;
import yesman.epicfight.api.collider.MultiOBBCollider;
import yesman.epicfight.api.collider.OBBCollider;
import yesman.epicfight.api.data.reloader.SkillManager;
import yesman.epicfight.api.forgeevent.AnimationRegistryEvent;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.ClearableIdMapper;
import yesman.epicfight.gameasset.Armatures;
import yesman.epicfight.gameasset.ColliderPreset;
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
	
	public StaticAnimation byKey(String resourceLocation) {
		return this.byKey(new ResourceLocation(resourceLocation));
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
		Optional<Resource> resource = resourceManager.getResource(dataLocation);
		
		if (!resource.isEmpty()) {
			ClientAnimationDataReader.readAndApply(animation, resourceManager, resourceManager.getResource(dataLocation).get());
		}
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
		
		if (EpicFightMod.isPhysicalClient()) {
			this.animationRegistry.values().stream().reduce(Lists.<StaticAnimation>newArrayList(), (list, anim) -> {
				list.addAll(anim.getAllClipAnimations());
				return list;
			}, (list1, list2) -> {
				list1.addAll(list2);
				return list1;
			});
		}
		
		return super.prepare(resourceManager, profilerIn);
	}
	
	@Override
	protected void apply(Map<ResourceLocation, JsonElement> objectIn, ResourceManager resourceManager, ProfilerFiller profilerIn) {
		final Map<ResourceLocation, StaticAnimation> registeredAnimation = Maps.newHashMap();
		this.animationRegistry.values().forEach(a1 -> a1.getAllClipAnimations().forEach((a2) -> registeredAnimation.put(a2.getRegistryName(), a2)));
		
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
		
		if (EpicFightMod.isPhysicalClient()) {
			this.animationRegistry.values().forEach(AnimationManager::readAnimationProperties);
		}
	}
	
	private static ResourceLocation getAnimationDataFileLocation(ResourceLocation location) {
		int splitIdx = location.getPath().lastIndexOf('/');
		
		if (splitIdx < 0) {
			splitIdx = 0;
		}
		
		return new ResourceLocation(location.getNamespace(), String.format("%s/data%s", location.getPath().substring(0, splitIdx), location.getPath().substring(splitIdx)));
	}
	
	/**************************************************
	 * User-animation loader
	 **************************************************/
	private static final Map<String, Class<?>> PRIMITIVE_KEYWORDS = Maps.newHashMap();
	private static final Map<Class<?>, Function<String, Object>> STRING_TO_OBJECT_PARSER = Maps.newHashMap();
	
	private static void reloadResourceManager(ResourceManager pResourceManager) {
		if (resourceManager != pResourceManager) {
			resourceManager = pResourceManager;
		}
	}
	
	static {
		PRIMITIVE_KEYWORDS.put("B", byte.class);
		PRIMITIVE_KEYWORDS.put("C", char.class);
		PRIMITIVE_KEYWORDS.put("D", double.class);
		PRIMITIVE_KEYWORDS.put("F", float.class);
		PRIMITIVE_KEYWORDS.put("I", int.class);
		PRIMITIVE_KEYWORDS.put("J", long.class);
		PRIMITIVE_KEYWORDS.put("S", short.class);
		PRIMITIVE_KEYWORDS.put("Z", boolean.class);
		
		STRING_TO_OBJECT_PARSER.put(byte.class, Byte::parseByte);
		STRING_TO_OBJECT_PARSER.put(char.class, (s) -> s.charAt(0));
		STRING_TO_OBJECT_PARSER.put(double.class, Double::parseDouble);
		STRING_TO_OBJECT_PARSER.put(float.class, Float::parseFloat);
		STRING_TO_OBJECT_PARSER.put(int.class, Integer::parseInt);
		STRING_TO_OBJECT_PARSER.put(long.class, Long::parseLong);
		STRING_TO_OBJECT_PARSER.put(short.class, Short::parseShort);
		STRING_TO_OBJECT_PARSER.put(boolean.class, Boolean::parseBoolean);
		STRING_TO_OBJECT_PARSER.put(String.class, (s) -> s);
		STRING_TO_OBJECT_PARSER.put(Collider.class, (s) -> ColliderPreset.get(new ResourceLocation(s)));
		STRING_TO_OBJECT_PARSER.put(OBBCollider.class, (s) -> {
			String[] colliderArgs = s.split(",");
			return new OBBCollider(Double.valueOf(colliderArgs[0]), Double.valueOf(colliderArgs[1]), Double.valueOf(colliderArgs[2]), Double.valueOf(colliderArgs[3]), Double.valueOf(colliderArgs[4]), Double.valueOf(colliderArgs[5]));
		});
		STRING_TO_OBJECT_PARSER.put(MultiOBBCollider.class, (s) -> {
			String[] colliderArgs = s.split(",");
			return new MultiOBBCollider(Integer.valueOf(colliderArgs[0]), Double.valueOf(colliderArgs[1]), Double.valueOf(colliderArgs[2]), Double.valueOf(colliderArgs[3]), Double.valueOf(colliderArgs[4]), Double.valueOf(colliderArgs[5]), Double.valueOf(colliderArgs[6]));
		});
		STRING_TO_OBJECT_PARSER.put(Joint.class, (s) -> {
			String[] armature$joint = s.split("\\.");
			Armature armature = Armatures.getOrCreateArmature(resourceManager, new ResourceLocation(armature$joint[0]), Armature::new);
			Joint joint = armature.searchJointByName(armature$joint[1]);
			
			return joint;
		});
		STRING_TO_OBJECT_PARSER.put(Armature.class, (s) -> Armatures.getOrCreateArmature(resourceManager, new ResourceLocation(s), Armature::new));
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked", "deprecation" })
	private static StaticAnimation readAnimationFromJson(ResourceLocation rl, JsonObject json) throws Exception {
		JsonElement constructorElement = json.getAsJsonObject().get("constructor");
		
		if (constructorElement == null) {
			throw new IllegalStateException("No constructor information has provided in User animation " + rl);
		}
		
		JsonObject constructorObject = constructorElement.getAsJsonObject();
		String classpath = constructorObject.get("class").getAsString();
		String arguments = constructorObject.get("arguments").getAsString();
		
		if (classpath == null) {
			throw new IllegalStateException("No class information has provided in User animation " + rl);
		}
		
		if (arguments == null) {
			throw new IllegalStateException("No argument information has provided in User animation " + rl);
		}
		
		Object[] oArgumentArr = null;
		Class[] argumentClasses = null;
		
		try {
			Pair<Object[], Class[]> argumentsPair = splitArguments(arguments);
			oArgumentArr = argumentsPair.getFirst();
			argumentClasses = argumentsPair.getSecond();
		} catch (Exception e) {
			throw e;
		}
		
		Class<? extends StaticAnimation> animationClass = (Class<? extends StaticAnimation>)Class.forName(classpath);
		Constructor<? extends StaticAnimation> constructor = animationClass.getConstructor(argumentClasses);
		StaticAnimation animation = constructor.newInstance(oArgumentArr);
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
	
	@SuppressWarnings("rawtypes")
	public static Pair<Object[], Class[]> splitArguments(String sArgs) throws Exception {
		List<String> sArgsList = Lists.newArrayList();
		
		int innderArgCounter = 0;
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < sArgs.length(); i++) {
			char c = sArgs.charAt(i);
			
			if (c == ',') {
				if (innderArgCounter < 1) {
					sArgsList.add(sb.toString());
					sb.setLength(0);
				} else {
					sb.append(c);
				}
			} else if (c == '(') {
				innderArgCounter++;
			} else if (c == ')') {
				innderArgCounter--;
			} else {
				sb.append(c);
			}
		}
		
		if (!sb.isEmpty()) {
			sArgsList.add(sb.toString());
		}
		
		Object[] oArgs = new Object[sArgsList.size()];
		Class[] oArgClss = new Class[sArgsList.size()];
		
		for (int i = 0; i < oArgs.length; i++) {
			String element = sArgsList.get(i);
			
			if (!element.contains("$")) {
				throw new IllegalStateException("No splitter have found in animation constructor in User animation. " + element);
			}
			
			String[] value$type = element.split("\\$");
			String value = value$type[0];
			String clsType = value$type[1];
			
			Class<?> cls = null;
			
			if (PRIMITIVE_KEYWORDS.containsKey(clsType)) {
				cls = PRIMITIVE_KEYWORDS.get(clsType);
			} else {
				cls = Class.forName(clsType);
			}
			
			oArgClss[i] = cls;
			
			if (value == "null") {
				oArgs[i] = null;
				continue;
			}
			
			if (!STRING_TO_OBJECT_PARSER.containsKey(cls)) {
				throw new IllegalStateException("Unknown class specified in User animation. " + cls);
			}
			
			Object obj = STRING_TO_OBJECT_PARSER.get(cls).apply(value);
			oArgs[i] = obj;
		}
		
		return Pair.of(oArgs, oArgClss);
	}
}
