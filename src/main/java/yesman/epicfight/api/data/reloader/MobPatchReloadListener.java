package yesman.epicfight.api.data.reloader;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.utils.ExtendedDamageSource.StunType;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.network.server.SPDatapackSync;
import yesman.epicfight.world.capabilities.entitypatch.CustomHumanoidMobPatch;
import yesman.epicfight.world.capabilities.entitypatch.CustomMobPatch;
import yesman.epicfight.world.capabilities.entitypatch.EntityPatch;
import yesman.epicfight.world.capabilities.entitypatch.Faction;
import yesman.epicfight.world.capabilities.entitypatch.HumanoidMobPatch;
import yesman.epicfight.world.capabilities.entitypatch.MobPatch;
import yesman.epicfight.world.capabilities.item.Style;
import yesman.epicfight.world.capabilities.item.WeaponCategory;
import yesman.epicfight.world.capabilities.provider.ProviderEntity;
import yesman.epicfight.world.entity.ai.attribute.EpicFightAttributes;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors.Behavior;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors.BehaviorPredicate;
import yesman.epicfight.world.entity.ai.goal.CombatBehaviors.BehaviorSeries;

public class MobPatchReloadListener extends SimpleJsonResourceReloadListener {
	private static final Gson GSON = (new GsonBuilder()).create();
	private static final Map<EntityType<?>, CompoundTag> TAGMAP = Maps.newHashMap();
	private static final Map<EntityType<?>, AbstractMobPatchProvider> MOB_PATCH_PROVIDERS = Maps.newHashMap();
	
	public MobPatchReloadListener() {
		super(GSON, "epicfight_mobpatch");
	}
	
	@Override
	protected void apply(Map<ResourceLocation, JsonElement> objectIn, ResourceManager resourceManagerIn, ProfilerFiller profilerIn) {
		for (Map.Entry<ResourceLocation, JsonElement> entry : objectIn.entrySet()) {
			ResourceLocation rl = entry.getKey();
			String pathString = rl.getPath();
			ResourceLocation registryName = new ResourceLocation(rl.getNamespace(), pathString);
			
			if (!ForgeRegistries.ENTITIES.containsKey(registryName)) {
				EpicFightMod.LOGGER.warn("[Custom Entity] Entity named " + registryName + " does not exist");
				continue;
			}
			
			EntityType<?> entityType = ForgeRegistries.ENTITIES.getValue(registryName);
			
			CompoundTag tag = null;
			
			try {
				tag = TagParser.parseTag(entry.getValue().toString());
			} catch (CommandSyntaxException e) {
				e.printStackTrace();
			}
			
			MOB_PATCH_PROVIDERS.put(entityType, deserialize(tag, false));
			ProviderEntity.putCustomEntityPatch(entityType, (entity) -> () -> MOB_PATCH_PROVIDERS.get(entity.getType()).get(entity));
			TAGMAP.put(entityType, filterClientData(tag));
			
			if (EpicFightMod.isPhysicalClient()) {
				ClientEngine.instance.renderEngine.registerCustomEntityRenderer(entityType, tag.contains("preset") ? tag.getString("preset") : tag.getString("renderer"));
			}
		}
	}
	
	public static abstract class AbstractMobPatchProvider {
		public abstract EntityPatch<?> get(Entity entity);
	}
	
	public static class NullPatchProvider extends AbstractMobPatchProvider {
		@Override
		public EntityPatch<?> get(Entity entity) {
			return null;
		}
	}
	
	public static class BranchProvider extends AbstractMobPatchProvider {
		protected List<Pair<EpicFightPredicates<Entity>, AbstractMobPatchProvider>> providers = Lists.newArrayList();
		protected AbstractMobPatchProvider defaultProvider;
		
		@Override
		public EntityPatch<?> get(Entity entity) {
			for (Pair<EpicFightPredicates<Entity>, AbstractMobPatchProvider> provider : this.providers) {
				if (provider.getFirst().test(entity)) {
					return provider.getSecond().get(entity);
				}
			}
			
			return this.defaultProvider.get(entity);
		}
	}
	
	public static class MobPatchPresetProvider extends AbstractMobPatchProvider {
		protected Function<Entity, Supplier<EntityPatch<?>>> presetProvider;
		
		@Override
		public EntityPatch<?> get(Entity entity) {
			return this.presetProvider.apply(entity).get();
		}
	}
	
	public static class CustomHumanoidMobPatchProvider extends CustomMobPatchProvider {
		protected Map<WeaponCategory, Map<Style, CombatBehaviors.Builder<HumanoidMobPatch<?>>>> humanoidCombatBehaviors;
		protected Map<WeaponCategory, Map<Style, Set<Pair<LivingMotion, StaticAnimation>>>> humanoidWeaponMotions;
		
		@SuppressWarnings("rawtypes")
		@Override
		public EntityPatch<?> get(Entity entity) {
			return new CustomHumanoidMobPatch(this.faction, this);
		}
		
		public Map<WeaponCategory, Map<Style, Set<Pair<LivingMotion, StaticAnimation>>>> getHumanoidWeaponMotions() {
			return this.humanoidWeaponMotions;
		}
		
		public Map<WeaponCategory, Map<Style, CombatBehaviors.Builder<HumanoidMobPatch<?>>>> getHumanoidCombatBehaviors() {
			return this.humanoidCombatBehaviors;
		}
	}
	
	public static class CustomMobPatchProvider extends AbstractMobPatchProvider {
		protected ResourceLocation modelLocation;
		protected CombatBehaviors.Builder<?> combatBehaviorsBuilder;
		protected List<Pair<LivingMotion, StaticAnimation>> defaultAnimations;
		protected Map<StunType, StaticAnimation> stunAnimations;
		protected Map<Attribute, Double> attributeValues;
		protected Faction faction;
		protected double chasingSpeed;
		protected float scale;
		
		@Override
		@SuppressWarnings("rawtypes")
		public EntityPatch<?> get(Entity entity) {
			return new CustomMobPatch(this.faction, this);
		}
		
		public ResourceLocation getModelLocation() {
			return this.modelLocation;
		}

		public CombatBehaviors.Builder<?> getCombatBehaviorsBuilder() {
			return this.combatBehaviorsBuilder;
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
		
		public double getChasingSpeed() {
			return this.chasingSpeed;
		}
		
		public float getScale() {
			return this.scale;
		}
	}
	
	public static AbstractMobPatchProvider deserialize(CompoundTag tag, boolean clientSide) {
		AbstractMobPatchProvider provider = null;
		int i = 0;
		boolean hasBranch = tag.contains(String.format("branch_%d", i));
		
		if (hasBranch) {
			provider = new BranchProvider();
			((BranchProvider)provider).defaultProvider = deserializeMobPatchProvider(tag, clientSide);
		} else {
			provider = deserializeMobPatchProvider(tag, clientSide);
		}
		
		while (hasBranch) {
			CompoundTag branchTag = tag.getCompound(String.format("branch_%d", i));
			((BranchProvider)provider).providers.add(Pair.of(deserializePredicate(branchTag.getCompound("condition")), deserialize(branchTag, clientSide)));
			hasBranch = tag.contains(String.format("branch_%d", ++i));
		}
		
		return provider;
	}
	
	public static EpicFightPredicates<Entity> deserializePredicate(CompoundTag tag) {
		String predicateType = tag.getString("predicate");
		EpicFightPredicates<Entity> predicate = null;
		List<String[]> loggerNote = Lists.newArrayList();
		
		switch (predicateType) {
		case "has_tags":
			if (!tag.contains("tags", 9)) {
				loggerNote.add(new String[] {"has_tags", "tags", "string list"});
			}
			predicate = new EpicFightPredicates.HasTag(tag.getList("tags", 8));
			break;
		}
		
		for (String[] formatArgs : loggerNote) {
			EpicFightMod.LOGGER.info(String.format("[Custom Entity Error] can't find a proper argument for %s. [name: %s, type: %s]", (Object[])formatArgs));
		}
		
		if (predicate == null) {
			throw new IllegalArgumentException("[Custom Entity Error] No predicate type: " + predicateType);
		}
		
		return predicate;
	}
	
	public static AbstractMobPatchProvider deserializeMobPatchProvider(CompoundTag tag, boolean clientSide) {
		boolean disabled = tag.contains("disabled") ? tag.getBoolean("disabled") : false;
		
		if (disabled) {
			return new NullPatchProvider();
		} else {
			if (tag.contains("preset")) {
				Function<Entity, Supplier<EntityPatch<?>>> preset = ProviderEntity.get(tag.getString("preset"));
				MobPatchPresetProvider provider = new MobPatchPresetProvider();
				provider.presetProvider = preset;
				return provider;
			} else {
				boolean humanoid = tag.getBoolean("isHumanoid") ? tag.getBoolean("isHumanoid") : false;
				CustomMobPatchProvider provider = humanoid ? new CustomHumanoidMobPatchProvider() : new CustomMobPatchProvider();
				provider.attributeValues = deserializeAttributes(tag.getCompound("attributes"));
				provider.modelLocation = new ResourceLocation(tag.getString("model"));
				provider.defaultAnimations = deserializeDefaultAnimations(tag.getCompound("default_livingmotions"));
				provider.faction = Faction.valueOf(tag.getString("faction").toUpperCase(Locale.ROOT));
				provider.scale = tag.getCompound("attributes").contains("scale") ? (float)tag.getCompound("attributes").getDouble("scale") : 1.0F;
				
				if (!clientSide) {
					provider.stunAnimations = deserializeStunAnimations(tag.getCompound("stun_animations"));
					provider.chasingSpeed = tag.getCompound("attributes").getDouble("chasing_speed");
					
					if (humanoid) {
						CustomHumanoidMobPatchProvider humanoidProvider = (CustomHumanoidMobPatchProvider)provider;
						humanoidProvider.humanoidCombatBehaviors = deserializeHumanoidCombatBehaviors(tag.getList("combat_behavior", 10));
						humanoidProvider.humanoidWeaponMotions = deserializeHumanoidWeaponMotions(tag.getList("humanoid_weapon_motions", 10));
					} else {
						provider.combatBehaviorsBuilder = deserializeCombatBehaviorsBuilder(tag.getList("combat_behavior", 10));
					}
				}
				
				return provider;
			}
		}
	}
	
	public static Map<WeaponCategory, Map<Style, CombatBehaviors.Builder<HumanoidMobPatch<?>>>> deserializeHumanoidCombatBehaviors(ListTag tag) {
		Map<WeaponCategory, Map<Style, CombatBehaviors.Builder<HumanoidMobPatch<?>>>> combatBehaviorsMapBuilder = Maps.newHashMap();
		
		for (int i = 0; i < tag.size(); i++) {
			CompoundTag combatBehavior = tag.getCompound(i);
			ListTag categories = combatBehavior.getList("weapon_categories", 8);
			Style style = Style.ENUM_MANAGER.get(combatBehavior.getString("style"));
			CombatBehaviors.Builder<HumanoidMobPatch<?>> builder = deserializeCombatBehaviorsBuilder(combatBehavior.getList("behavior_series", 10));
			
			for (int j = 0; j < categories.size(); j++) {
				WeaponCategory category = WeaponCategory.ENUM_MANAGER.get(categories.getString(j));
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
			defaultAnimations.add(Pair.of(LivingMotion.ENUM_MANAGER.get(key), EpicFightMod.getInstance().animationManager.findAnimationByPath(animation)));
		}
		
		return defaultAnimations;
	}
	
	public static Map<StunType, StaticAnimation> deserializeStunAnimations(CompoundTag tag) {
		Map<StunType, StaticAnimation> stunAnimations = Maps.newHashMap();
		stunAnimations.put(StunType.SHORT, EpicFightMod.getInstance().animationManager.findAnimationByPath(tag.getString("short")));
		stunAnimations.put(StunType.LONG, EpicFightMod.getInstance().animationManager.findAnimationByPath(tag.getString("long")));
		stunAnimations.put(StunType.FALL, EpicFightMod.getInstance().animationManager.findAnimationByPath(tag.getString("fall")));
		stunAnimations.put(StunType.KNOCKDOWN, EpicFightMod.getInstance().animationManager.findAnimationByPath(tag.getString("knockdown")));
		stunAnimations.put(StunType.HOLD, EpicFightMod.getInstance().animationManager.findAnimationByPath(tag.getString("short")));
		
		return stunAnimations;
	}
	
	public static Map<Attribute, Double> deserializeAttributes(CompoundTag tag) {
		Map<Attribute, Double> attributes = Maps.newHashMap();
		attributes.put(EpicFightAttributes.IMPACT.get(), tag.contains("impact", 6) ? tag.getDouble("impact") : 0.5D);
		attributes.put(EpicFightAttributes.ARMOR_NEGATION.get(), tag.contains("armor_negation", 6) ? tag.getDouble("armor_negation") : 0.0D);
		attributes.put(EpicFightAttributes.MAX_STRIKES.get(), (double)(tag.contains("max_strikes", 3) ? tag.getInt("max_strikes") : 1));
		if (tag.contains("attack_damage", 6)) {
			attributes.put(Attributes.ATTACK_DAMAGE, tag.getDouble("attack_damage"));
		}
		
		return attributes;
	}
	
	public static Map<WeaponCategory, Map<Style, Set<Pair<LivingMotion, StaticAnimation>>>> deserializeHumanoidWeaponMotions(ListTag tag) {
		Map<WeaponCategory, Map<Style, Set<Pair<LivingMotion, StaticAnimation>>>> map = Maps.newHashMap();
		
		for (int i = 0; i < tag.size(); i++) {
			ImmutableSet.Builder<Pair<LivingMotion, StaticAnimation>> motions = ImmutableSet.builder();
			CompoundTag weaponMotionTag = tag.getCompound(i);
			Style style = Style.ENUM_MANAGER.get(weaponMotionTag.getString("style"));
			CompoundTag motionsTag = weaponMotionTag.getCompound("livingmotions");
			
			for (String key : motionsTag.getAllKeys()) {
				motions.add(Pair.of(LivingMotion.ENUM_MANAGER.get(key), EpicFightMod.getInstance().animationManager.findAnimationByPath(motionsTag.getString(key))));
			}
			
			Tag weponTypeTag = weaponMotionTag.get("weapon_categories");
			
			if (weponTypeTag instanceof StringTag) {
				WeaponCategory weaponCategory = WeaponCategory.ENUM_MANAGER.get(weponTypeTag.getAsString());
				if (!map.containsKey(weaponCategory)) {
					map.put(weaponCategory, Maps.newHashMap());
				}
				map.get(weaponCategory).put(style, motions.build());
				
			} else if (weponTypeTag instanceof ListTag) {
				ListTag weponTypesTag = ((ListTag)weponTypeTag);
				
				for (int j = 0; j < weponTypesTag.size(); j++) {
					WeaponCategory weaponCategory = WeaponCategory.ENUM_MANAGER.get(weponTypesTag.getString(j));
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
				StaticAnimation animation = EpicFightMod.getInstance().animationManager.findAnimationByPath(behavior.getString("animation"));
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
	
	public static CompoundTag filterClientData(CompoundTag tag) {
		CompoundTag clientTag = new CompoundTag();
		int i = 0;
		boolean hasBranch = tag.contains(String.format("branch_%d", i));
		
		while (hasBranch) {
			CompoundTag branchTag = tag.getCompound(String.format("branch_%d", i));
			CompoundTag copiedTag = new CompoundTag();
			extractBranch(copiedTag, branchTag);
			clientTag.put(String.format("branch_%d", i), copiedTag);
			hasBranch = tag.contains(String.format("branch_%d", ++i));
		}
		
		extractBranch(clientTag, tag);
		
		return clientTag;
	}
	
	public static CompoundTag extractBranch(CompoundTag extract, CompoundTag original) {
		if (original.contains("disabled") && original.getBoolean("disabled")) {
			extract.put("disabled", original.get("disabled"));
		} else if (original.contains("preset")) {
			extract.put("preset", original.get("preset"));
		} else {
			extract.put("model", original.get("model"));
			extract.putBoolean("isHumanoid", original.contains("isHumanoid") ? original.getBoolean("isHumanoid") : false);
			extract.put("renderer", original.get("renderer"));
			extract.put("faction", original.get("faction"));
			extract.put("default_livingmotions", original.get("default_livingmotions"));
			extract.put("attributes", original.get("attributes"));
		}
		
		return extract;
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
			MOB_PATCH_PROVIDERS.put(entityType, deserialize(tag, true));
			ProviderEntity.putCustomEntityPatch(entityType, (entity) -> () -> MOB_PATCH_PROVIDERS.get(entity.getType()).get(entity));
			
			if (!disabled) {
				ClientEngine.instance.renderEngine.registerCustomEntityRenderer(entityType, tag.contains("preset") ? tag.getString("preset") : tag.getString("renderer"));
			}
		}
	}
}