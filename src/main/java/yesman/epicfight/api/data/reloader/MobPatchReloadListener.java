package yesman.epicfight.api.data.reloader;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.game.ExtendedDamageSource.StunType;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.network.server.SPDatapackSync;
import yesman.epicfight.world.capabilities.entitypatch.CustomHumanoidMobPatch;
import yesman.epicfight.world.capabilities.entitypatch.CustomMobPatch;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;
import yesman.epicfight.world.capabilities.entitypatch.mob.Faction;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.capabilities.item.CapabilityItem.Style;
import yesman.epicfight.world.capabilities.item.CapabilityItem.WeaponCategory;
import yesman.epicfight.world.capabilities.item.WeaponCapability;
import yesman.epicfight.world.capabilities.provider.ProviderEntity;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors.Behavior;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors.BehaviorPredicate;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors.BehaviorSeries;

public class MobPatchReloadListener extends SimpleJsonResourceReloadListener {
	private static final Gson GSON = (new GsonBuilder()).create();
	private static final Map<EntityType<?>, CompoundTag> TAGMAP = Maps.newHashMap();
	private static final Map<EntityType<?>, MobPatchProvider> MOB_PATCH_PROVIDERS = Maps.newHashMap();
	
	public MobPatchReloadListener() {
		super(GSON, "epicfight_mobpatch");
	}
	
	@Override
	protected void apply(Map<ResourceLocation, JsonElement> objectIn, ResourceManager resourceManagerIn, ProfilerFiller profilerIn) {
		for (Map.Entry<ResourceLocation, JsonElement> entry : objectIn.entrySet()) {
			ResourceLocation rl = entry.getKey();
			String pathString = rl.getPath();
			ResourceLocation registryName = new ResourceLocation(rl.getNamespace(), pathString);
			EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(registryName);
			
			if (entityType == null) {
				EpicFightMod.LOGGER.warn("Tried to add a mob patch for entity " + registryName + ", but it's not exist!");
				return;
			}
			
			CompoundTag tag = null;
			
			try {
				tag = TagParser.parseTag(entry.getValue().toString());
			} catch (CommandSyntaxException e) {
				e.printStackTrace();
			}
			
			MOB_PATCH_PROVIDERS.put(entityType, deserializeMobPatchProvider(tag, false));
			ProviderEntity.putCustomEntityPatch(entityType, (entity) -> MOB_PATCH_PROVIDERS.get(entity.getType())::get);
			TAGMAP.put(entityType, extractClientData(tag));
			
			if (EpicFightMod.isPhysicalClient()) {
				ClientEngine.instance.renderEngine.registerCustomEntityRenderer(entityType, tag.getString("renderer"));
			}
		}
	}
	
	public static class MobPatchProvider {
		private ResourceLocation modelLocation;
		private CombatBehaviors.Builder<?> combatBehaviorsBuilder;
		private Map<WeaponCategory, Map<CapabilityItem.Style, CombatBehaviors.Builder<HumanoidMobPatch<?>>>> humanoidCombatBehaviors;
		private List<Pair<LivingMotion, StaticAnimation>> defaultAnimations;
		private Map<StunType, StaticAnimation> stunAnimations;
		private Map<Attribute, Double> attributeValues;
		private Map<WeaponCategory, Map<WeaponCapability.Style, Set<Pair<LivingMotion, StaticAnimation>>>> humanoidWeaponMotions;
		private Faction faction;
		private double chasingSpeed;
		private boolean isHumanoid;
		private boolean disabled;
		
		@SuppressWarnings("rawtypes")
		public EntityPatch<?> get() {
			if (this.disabled) {
				return null;
			} else if (this.isHumanoid) {
				return new CustomHumanoidMobPatch(this.faction, this);
			} else {
				return new CustomMobPatch(this.faction, this);
			}
		}
		
		public ResourceLocation getModelLocation() {
			return this.modelLocation;
		}

		public CombatBehaviors.Builder<?> getCombatBehaviorsBuilder() {
			return this.combatBehaviorsBuilder;
		}
		
		public Map<WeaponCategory, Map<CapabilityItem.Style, CombatBehaviors.Builder<HumanoidMobPatch<?>>>> getHumanoidCombatBehaviors() {
			return this.humanoidCombatBehaviors;
		}
		
		public List<Pair<LivingMotion, StaticAnimation>> getDefaultAnimations() {
			return this.defaultAnimations;
		}

		public Map<StunType, StaticAnimation> getStunAnimations() {
			return this.stunAnimations;
		}

		public Map<Attribute, Double> getAttributeValues() {
			return this.attributeValues;
		}
		
		public Map<WeaponCategory, Map<WeaponCapability.Style, Set<Pair<LivingMotion, StaticAnimation>>>> getHumanoidWeaponMotions() {
			return this.humanoidWeaponMotions;
		}
		
		public double getChasingSpeed() {
			return this.chasingSpeed;
		}
	}
	
	public static MobPatchProvider deserializeMobPatchProvider(CompoundTag tag, boolean clientSide) {
		MobPatchProvider provider = new MobPatchProvider();
		provider.disabled = tag.contains("disabled") ? tag.getBoolean("disabled") : false;
		
		if (!provider.disabled) {
			provider.attributeValues = deserializeAttributes(tag.getCompound("attributes"));
			provider.modelLocation = new ResourceLocation(tag.getString("model"));
			provider.defaultAnimations = deserializeDefaultAnimations(tag.getCompound("default_livingmotions"));
			provider.isHumanoid = tag.getBoolean("isHumanoid");
			provider.faction = Faction.valueOf(tag.getString("faction").toUpperCase(Locale.ROOT));
			
			if (!clientSide) {
				provider.stunAnimations = deserializeStunAnimations(tag.getCompound("stun_animations"));
				provider.chasingSpeed = tag.getCompound("attributes").getDouble("chasing_speed");
				
				if (provider.isHumanoid) {
					provider.humanoidCombatBehaviors = deserializeHumanoidCombatBehaviors(tag.getList("combat_behavior", 10));
					provider.humanoidWeaponMotions = deserializeHumanoidWeaponMotions(tag.getList("humanoid_weapon_motions", 10));
				} else {
					provider.combatBehaviorsBuilder = deserializeCombatBehaviorsBuilder(tag.getList("combat_behavior", 10));
				}
			}
		}
		
		return provider;
	}
	
	public static Map<WeaponCategory, Map<CapabilityItem.Style, CombatBehaviors.Builder<HumanoidMobPatch<?>>>> deserializeHumanoidCombatBehaviors(ListTag tag) {
		Map<WeaponCategory, Map<CapabilityItem.Style, CombatBehaviors.Builder<HumanoidMobPatch<?>>>> combatBehaviorsMapBuilder = Maps.newHashMap();
		
		for (int i = 0; i < tag.size(); i++) {
			CompoundTag combatBehavior = tag.getCompound(i);
			ListTag categories = combatBehavior.getList("weapon_categories", 8);
			CapabilityItem.Style style = CapabilityItem.Style.valueOf(combatBehavior.getString("weapon_style").toUpperCase());
			CombatBehaviors.Builder<HumanoidMobPatch<?>> builder = deserializeCombatBehaviorsBuilder(combatBehavior.getList("behavior_series", 10));
			
			for (int j = 0; j < categories.size(); j++) {
				WeaponCategory category = WeaponCategory.valueOf(categories.getString(j).toUpperCase(Locale.ROOT));
				combatBehaviorsMapBuilder.computeIfAbsent(category, (key) -> Maps.newHashMap());
				combatBehaviorsMapBuilder.get(category).put(style, builder);
			}
		}
		
		return combatBehaviorsMapBuilder;
	}
	
	public static List<Pair<LivingMotion, StaticAnimation>> deserializeDefaultAnimations(CompoundTag defaultLivingmotions) {
		List<Pair<LivingMotion, StaticAnimation>> defaultAnimations = Lists.newArrayList();
		
		for (String key : defaultLivingmotions.getAllKeys()) {
			String animation = defaultLivingmotions.getString(key);
			defaultAnimations.add(Pair.of(LivingMotion.valueOf(key.toUpperCase(Locale.ROOT)), EpicFightMod.getInstance().animationManager.findAnimationByResourceLocation(animation)));
		}
		
		return defaultAnimations;
	}
	
	public static Map<StunType, StaticAnimation> deserializeStunAnimations(CompoundTag tag) {
		Map<StunType, StaticAnimation> stunAnimations = Maps.newHashMap();
		stunAnimations.put(StunType.SHORT, EpicFightMod.getInstance().animationManager.findAnimationByResourceLocation(tag.getString("short")));
		stunAnimations.put(StunType.LONG, EpicFightMod.getInstance().animationManager.findAnimationByResourceLocation(tag.getString("long")));
		stunAnimations.put(StunType.FALL, EpicFightMod.getInstance().animationManager.findAnimationByResourceLocation(tag.getString("fall")));
		stunAnimations.put(StunType.KNOCKDOWN, EpicFightMod.getInstance().animationManager.findAnimationByResourceLocation(tag.getString("knockdown")));
		stunAnimations.put(StunType.HOLD, EpicFightMod.getInstance().animationManager.findAnimationByResourceLocation(tag.getString("short")));
		
		return stunAnimations;
	}
	
	public static Map<Attribute, Double> deserializeAttributes(CompoundTag tag) {
		Map<Attribute, Double> attributes = Maps.newHashMap();
		attributes.put(EpicFightAttributes.IMPACT.get(), tag.contains("impact", 6) ? tag.getDouble("impact") : 0.5D);
		attributes.put(EpicFightAttributes.ARMOR_NEGATION.get(), tag.contains("armor_negation", 6) ? tag.getDouble("armor_negation") : 0.5D);
		attributes.put(EpicFightAttributes.MAX_STRIKES.get(), (double) (tag.contains("max_strikes", 3) ? tag.getInt("max_strikes") : 1));
		
		return attributes;
	}
	
	public static Map<WeaponCategory, Map<WeaponCapability.Style, Set<Pair<LivingMotion, StaticAnimation>>>> deserializeHumanoidWeaponMotions(ListTag tag) {
		Map<WeaponCategory, Map<WeaponCapability.Style, Set<Pair<LivingMotion, StaticAnimation>>>> map = Maps.newHashMap();
		
		for (int i = 0; i < tag.size(); i++) {
			ImmutableSet.Builder<Pair<LivingMotion, StaticAnimation>> motions = ImmutableSet.builder();
			CompoundTag weaponMotionTag = tag.getCompound(i);
			Style style = Style.valueOf(weaponMotionTag.getString("style").toUpperCase(Locale.ROOT));
			CompoundTag motionsTag = weaponMotionTag.getCompound("livingmotions");
			
			for (String key : motionsTag.getAllKeys()) {
				motions.add(Pair.of(LivingMotion.valueOf(key.toUpperCase(Locale.ROOT)), EpicFightMod.getInstance().animationManager.findAnimationByResourceLocation(motionsTag.getString(key))));
			}
			
			Tag weponTypeTag = weaponMotionTag.get("weapon_type");
			
			if (weponTypeTag instanceof StringTag) {
				WeaponCategory weaponCategory = WeaponCategory.valueOf(weponTypeTag.getAsString().toUpperCase(Locale.ROOT));
				if (!map.containsKey(weaponCategory)) {
					map.put(weaponCategory, Maps.newHashMap());
				}
				map.get(weaponCategory).put(style, motions.build());
				
			} else if (weponTypeTag instanceof ListTag) {
				ListTag weponTypesTag = ((ListTag)weponTypeTag);
				
				for (int j = 0; j < weponTypesTag.size(); j++) {
					WeaponCategory weaponCategory = WeaponCategory.valueOf(weponTypesTag.getString(j).toUpperCase(Locale.ROOT));
					if (!map.containsKey(weaponCategory)) {
						map.put(weaponCategory, Maps.newHashMap());
					}
					map.get(weaponCategory).put(style, motions.build());
				}
			}
		}
		
		return map;
	}
	
	public static <T extends MobPatch<?>> CombatBehaviors.Builder<T> deserializeCombatBehaviorsBuilder(ListTag tag) {
		CombatBehaviors.Builder<T> builder = CombatBehaviors.builder();
		
		for (int i = 0; i < tag.size(); i++) {
			CompoundTag behaviorSeries = tag.getCompound(i);
			float weight = (float)behaviorSeries.getDouble("weight");
			int cooldown = behaviorSeries.contains("cooldown") ? behaviorSeries.getInt("cooldown") : 0;
			boolean canBeInterrupted = behaviorSeries.contains("canBeInterrupted") ? behaviorSeries.getBoolean("canBeInterrupted") : false;
			boolean looping = behaviorSeries.contains("looping") ? behaviorSeries.getBoolean("looping") : false;
			ListTag behaviorList = behaviorSeries.getList("behaviors", 10);
			BehaviorSeries.Builder<T> behaviorSeriesBuilder = BehaviorSeries.builder();
			behaviorSeriesBuilder.weight(weight).cooldown(cooldown).canBeInterrupted(canBeInterrupted).looping(looping);
			
			for (int j = 0; j < behaviorList.size(); j++) {
				Behavior.Builder<T> behaviorBuilder = Behavior.builder();
				CompoundTag behavior = behaviorList.getCompound(j);
				StaticAnimation animation = EpicFightMod.getInstance().animationManager.findAnimationByResourceLocation(behavior.getString("animation"));
				ListTag conditionList = behavior.getList("conditions", 10);
				behaviorBuilder.animationBehavior(animation);
				
				for (int k = 0; k < conditionList.size(); k++) {
					CompoundTag condition = conditionList.getCompound(k);
					BehaviorPredicate<T> predicate = deserializeBehaviorPredicate(condition.getString("predicate"), condition);
					behaviorBuilder.predicate(predicate);
				}
				
				behaviorSeriesBuilder.nextBehavior(behaviorBuilder);
			}
			
			builder.newBehaviorSeries(behaviorSeriesBuilder);
		}
		
		return builder;
	}
	
	public static <T extends MobPatch<?>> BehaviorPredicate<T> deserializeBehaviorPredicate(String type, CompoundTag args) {
		BehaviorPredicate<T> predicate = null;
		List<String[]> loggerNote = Lists.newArrayList();
		
		switch (type) {
		case "random_chance":
			if (!args.contains("chance", 6)) {
				loggerNote.add(new String[] {"random_chance", "chance", "double", "0.0"});
			}
			
			predicate = new CombatBehaviors.RandomChance<T>((float)args.getDouble("chance"));
			break;
		case "within_eye_height":
			predicate = new CombatBehaviors.TargetWithinEyeHeight<T>();
			break;
		case "within_distance":
			if (!args.contains("min", 6)) {
				loggerNote.add(new String[] {"within_distance", "min", "double", "0.0"});
			}
			
			if (!args.contains("max", 6)) {
				loggerNote.add(new String[] {"within_distance", "max", "double", "0.0"});
			}
			
			predicate = new CombatBehaviors.TargetWithinDistance<T>(args.getDouble("min"), args.getDouble("max"));
			break;
		case "within_angle":
			if (!args.contains("min", 6)) {
				loggerNote.add(new String[] {"within_angle", "within_distance", "min", "double", "0.0F"});
			}
			
			if (!args.contains("max", 6)) {
				loggerNote.add(new String[] {"within_angle", "max", "double", "0.0F"});
			}
			
			predicate = new CombatBehaviors.TargetWithinAngle<T>(args.getDouble("min"), args.getDouble("max"));
			break;
		case "within_angle_horizontal":
			if (!args.contains("min", 6)) {
				loggerNote.add(new String[] {"within_angle_horizontal", "min", "double", "0.0F"});
			}
			
			if (!args.contains("max", 6)) {
				loggerNote.add(new String[] {"within_angle_horizontal", "max", "double", "0.0F"});
			}
			
			predicate = new CombatBehaviors.TargetWithinAngle.Horizontal<T>(args.getDouble("min"), args.getDouble("max"));
			break;
		case "health":
			if (!args.contains("health", 6)) {
				loggerNote.add(new String[] {"health", "health", "double", "0.0F"});
			}
			
			if (!args.contains("comparator", 8)) {
				loggerNote.add(new String[] {"health", "comparator", "string", ""});
			}
			
			predicate = new CombatBehaviors.Health<T>((float)args.getDouble("health"), CombatBehaviors.Health.Comparator.valueOf(args.getString("comparator").toUpperCase(Locale.ROOT)));
			break;
		}
		
		for (String[] formatArgs : loggerNote) {
			EpicFightMod.LOGGER.info(String.format("[Custom Entity Error] can't find a proper argument for %s. [name: %s, type: %s, default: %s]", (Object[])formatArgs));
		}
		
		if (predicate == null) {
			throw new IllegalArgumentException("[Custom Entity Error] No predicate type: " + type);
		}
		
		return predicate;
	}
	
	public static CompoundTag extractClientData(CompoundTag tag) {
		CompoundTag clientTag = new CompoundTag();
		boolean disabled = false;
		
		if (tag.contains("disabled")) {
			disabled = tag.getBoolean("disabled");
			
			if (disabled) {
				clientTag.put("disabled", tag.get("disabled"));
			}
		}
		
		if (!disabled) {
			clientTag.put("model", tag.get("model"));
			clientTag.putBoolean("isHumanoid", tag.contains("isHumanoid") ? tag.getBoolean("isHumanoid") : false);
			clientTag.put("renderer", tag.get("renderer"));
			clientTag.put("faction", tag.get("faction"));
			clientTag.put("default_livingmotions", tag.get("default_livingmotions"));
			clientTag.put("attributes", tag.get("attributes"));
		}
		
		return clientTag;
	}
	
	public static Stream<CompoundTag> getDataStream() {
		Stream<CompoundTag> tagStream = TAGMAP.entrySet().stream().map((entry) -> {
			entry.getValue().putString("id", entry.getKey().getRegistryName().toString());
			return entry.getValue();
		});
		
		return tagStream;
	}
	
	public static int getTagSize() {
		return TAGMAP.size();
	}
	
	@OnlyIn(Dist.CLIENT)
	public static void processServerPacket(SPDatapackSync packet) {
		for (CompoundTag tag : packet.getTags()) {
			boolean disabled = false;
			
			if (tag.contains("disabled")) {
				disabled = tag.getBoolean("disabled");
			}
			
			EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(new ResourceLocation(tag.getString("id")));
			MOB_PATCH_PROVIDERS.put(entityType, deserializeMobPatchProvider(tag, true));
			ProviderEntity.putCustomEntityPatch(entityType, (entity) -> MOB_PATCH_PROVIDERS.get(entity.getType())::get);
			
			if (!disabled) {
				ClientEngine.instance.renderEngine.registerCustomEntityRenderer(entityType, tag.getString("renderer"));
			}
		}
	}
}