package yesman.epicfight.world.capabilities.item;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.ModLoader;
import net.minecraftforge.registries.ForgeRegistries;
import yesman.epicfight.api.animation.LivingMotion;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.data.reloader.ItemCapabilityReloadListener;
import yesman.epicfight.api.data.reloader.SkillManager;
import yesman.epicfight.api.forgeevent.WeaponCapabilityPresetRegistryEvent;
import yesman.epicfight.data.conditions.Condition.ConditionBuilder;
import yesman.epicfight.data.conditions.EpicFightConditions;
import yesman.epicfight.data.conditions.entity.LivingEntityCondition;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.network.server.SPDatapackSync;
import yesman.epicfight.particle.HitParticleType;

public class WeaponTypeReloadListener extends SimpleJsonResourceReloadListener {
	public static void registerDefaultWeaponTypes() {
		Map<ResourceLocation, Function<Item, CapabilityItem.Builder>> typeEntry = Maps.newHashMap();
		typeEntry.put(new ResourceLocation(EpicFightMod.MODID, "axe"), WeaponCapabilityPresets.AXE);
		typeEntry.put(new ResourceLocation(EpicFightMod.MODID, "fist"), WeaponCapabilityPresets.FIST);
		typeEntry.put(new ResourceLocation(EpicFightMod.MODID, "hoe"), WeaponCapabilityPresets.HOE);
		typeEntry.put(new ResourceLocation(EpicFightMod.MODID, "pickaxe"), WeaponCapabilityPresets.PICKAXE);
		typeEntry.put(new ResourceLocation(EpicFightMod.MODID, "shovel"), WeaponCapabilityPresets.SHOVEL);
		typeEntry.put(new ResourceLocation(EpicFightMod.MODID, "sword"), WeaponCapabilityPresets.SWORD);
		typeEntry.put(new ResourceLocation(EpicFightMod.MODID, "spear"), WeaponCapabilityPresets.SPEAR);
		typeEntry.put(new ResourceLocation(EpicFightMod.MODID, "greatsword"), WeaponCapabilityPresets.GREATSWORD);
		typeEntry.put(new ResourceLocation(EpicFightMod.MODID, "uchigatana"), WeaponCapabilityPresets.UCHIGATANA);
		typeEntry.put(new ResourceLocation(EpicFightMod.MODID, "tachi"), WeaponCapabilityPresets.TACHI);
		typeEntry.put(new ResourceLocation(EpicFightMod.MODID, "longsword"), WeaponCapabilityPresets.LONGSWORD);
		typeEntry.put(new ResourceLocation(EpicFightMod.MODID, "dagger"), WeaponCapabilityPresets.DAGGER);
		typeEntry.put(new ResourceLocation(EpicFightMod.MODID, "bow"), WeaponCapabilityPresets.BOW);
		typeEntry.put(new ResourceLocation(EpicFightMod.MODID, "crossbow"), WeaponCapabilityPresets.CROSSBOW);
		typeEntry.put(new ResourceLocation(EpicFightMod.MODID, "trident"), WeaponCapabilityPresets.TRIDENT);
		typeEntry.put(new ResourceLocation(EpicFightMod.MODID, "shield"), WeaponCapabilityPresets.SHIELD);
		
		WeaponCapabilityPresetRegistryEvent weaponCapabilityPresetRegistryEvent = new WeaponCapabilityPresetRegistryEvent(typeEntry);
		ModLoader.get().postEvent(weaponCapabilityPresetRegistryEvent);
		weaponCapabilityPresetRegistryEvent.getTypeEntry().forEach(PRESETS::put);
	}
	
	private static final Gson GSON = (new GsonBuilder()).create();
	private static final Map<ResourceLocation, Function<Item, CapabilityItem.Builder>> PRESETS = Maps.newHashMap();
	private static final Map<ResourceLocation, CompoundTag> TAGMAP = Maps.newHashMap();
	
	public WeaponTypeReloadListener() {
		super(GSON, "capabilities/weapons/types");
	}
	
	@Override
	protected void apply(Map<ResourceLocation, JsonElement> packEntry, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
		for (Map.Entry<ResourceLocation, JsonElement> entry : packEntry.entrySet()) {
			CompoundTag nbt = null;
			
			try {
				nbt = TagParser.parseTag(entry.getValue().toString());
			} catch (CommandSyntaxException e) {
				e.printStackTrace();
			}
			
			try {
				WeaponCapability.Builder builder = deserializeWeaponCapabilityBuilder(nbt);
				
				PRESETS.put(entry.getKey(), (itemstack) -> builder);
				TAGMAP.put(entry.getKey(), nbt);
			} catch (Exception e) {
				EpicFightMod.LOGGER.warn("Error while deserializing weapon type datapack: " + entry.getKey());
				e.printStackTrace();
			}
		}
	}
	
	public static Function<Item, CapabilityItem.Builder> get(String typeName) {
		ResourceLocation rl = new ResourceLocation(typeName);
		
		if (!PRESETS.containsKey(rl)) {
			throw new IllegalArgumentException("Can't find weapon type: " + rl);
		}
		
		return PRESETS.get(rl);
	}
	
	private static WeaponCapability.Builder deserializeWeaponCapabilityBuilder(CompoundTag tag) {
		WeaponCapability.Builder builder = WeaponCapability.builder();
		builder.category(WeaponCategory.ENUM_MANAGER.get(tag.getString("category")));
		builder.collider(ItemCapabilityReloadListener.deserializeCollider(tag.getCompound("collider")));
		
		if (tag.contains("hit_particle")) {
			ParticleType<?> particleType = ForgeRegistries.PARTICLE_TYPES.getValue(new ResourceLocation(tag.getString("hit_particle")));
			
			if (particleType == null) {
				EpicFightMod.LOGGER.warn("Can't find particle type " + tag.getString("hit_particle"));
			} else if (!(particleType instanceof HitParticleType)) {
				EpicFightMod.LOGGER.warn(tag.getString("hit_particle") + " is not a hit particle type");
			} else {
				builder.hitParticle((HitParticleType)particleType);
			}
		}
		
		if (tag.contains("swing_sound")) {
			SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(tag.getString("swing_sound")));
			
			if (sound == null) {
				EpicFightMod.LOGGER.warn("Can't find swing sound " + tag.getString("swing_sound"));
			} else {
				builder.swingSound(sound);
			}
		}
		
		if (tag.contains("hit_sound")) {
			SoundEvent sound = ForgeRegistries.SOUND_EVENTS.getValue(new ResourceLocation(tag.getString("hit_sound")));
			
			if (sound == null) {
				EpicFightMod.LOGGER.warn("Can't find hit sound " + tag.getString("hit_sound"));
			} else {
				builder.hitSound(sound);
			}
		}
		
		CompoundTag combosTag = tag.getCompound("combos");
		
		for (String key : combosTag.getAllKeys()) {
			Style style = Style.ENUM_MANAGER.get(key);
			ListTag comboAnimations = combosTag.getList(key, Tag.TAG_STRING);
			StaticAnimation[] animArray = new StaticAnimation[comboAnimations.size()];
			
			for (int i = 0; i < comboAnimations.size(); i++) {
				animArray[i] = EpicFightMod.getInstance().animationManager.findAnimationByPath(comboAnimations.getString(i));
			}
			
			builder.newStyleCombo(style, animArray);
		}
		
		CompoundTag innateSkillsTag = tag.getCompound("innate_skills");
		
		for (String key : innateSkillsTag.getAllKeys()) {
			Style style = Style.ENUM_MANAGER.get(key);
			
			builder.innateSkill(style, (itemstack) -> SkillManager.getSkill(innateSkillsTag.getString(key)));
		}
		
		CompoundTag livingmotionModifierTag = tag.getCompound("livingmotion_modifier");
		
		for (String sStyle : livingmotionModifierTag.getAllKeys()) {
			Style style = Style.ENUM_MANAGER.get(sStyle);
			CompoundTag styleAnimationTag = livingmotionModifierTag.getCompound(sStyle);
			
			for (String sLivingmotion : styleAnimationTag.getAllKeys()) {
				LivingMotion livingmotion = LivingMotion.ENUM_MANAGER.get(sLivingmotion);
				StaticAnimation animation = EpicFightMod.getInstance().animationManager.findAnimationByPath(styleAnimationTag.getString(sLivingmotion));
				
				builder.livingMotionModifier(style, livingmotion, animation);
			}
		}
		
		CompoundTag stylesTag = tag.getCompound("styles");
		StyleEntry styleEntry = new StyleEntry();
		
		stylesTag.getList("cases", Tag.TAG_COMPOUND);
		
		for (Tag caseTag : stylesTag.getList("cases", Tag.TAG_COMPOUND)) {
			CompoundTag caseCompTag = (CompoundTag)caseTag;
			
			Function<CompoundTag, LivingEntityCondition> conditionProvider = EpicFightConditions.getConditionOrThrow(new ResourceLocation(caseCompTag.getString("condition")));
			LivingEntityCondition condition = ConditionBuilder.builder(conditionProvider).setTag(caseCompTag.getCompound("predicate")).build();
			
			Style style = Style.ENUM_MANAGER.get(caseCompTag.getString("style"));
			styleEntry.putNewEntry(condition, style);
		}
		
		styleEntry.elseStyle = Style.ENUM_MANAGER.get(stylesTag.getString("default"));
		builder.styleProvider(styleEntry::getStyle);
		
		CompoundTag offhandValidatorTag = tag.getCompound("offhand_item_compatible_predicate");
		
		Function<CompoundTag, LivingEntityCondition> conditionProvider = EpicFightConditions.getConditionOrThrow(new ResourceLocation(offhandValidatorTag.getString("condition")));
		builder.weaponCombinationPredicator(ConditionBuilder.builder(conditionProvider).setTag(offhandValidatorTag.getCompound("predicate")).build()::predicate);
		
		return builder;
	}
	
	public static int getTagCount() {
		return TAGMAP.size();
	}
	
	public static Stream<CompoundTag> getWeaponTypeDataStream() {
		Stream<CompoundTag> tagStream = TAGMAP.entrySet().stream().map((entry) -> {
			entry.getValue().putString("registry_name", entry.getKey().toString());
			return entry.getValue();
		});
		return tagStream;
	}
	
	public static void clear() {
		PRESETS.clear();
	}
	
	@OnlyIn(Dist.CLIENT)
	public static void processServerPacket(SPDatapackSync packet) {
		if (packet.getType() == SPDatapackSync.Type.WEAPON_TYPE) {
			PRESETS.clear();
			registerDefaultWeaponTypes();
			
			for (CompoundTag tag : packet.getTags()) {
				PRESETS.put(new ResourceLocation(tag.getString("registry_name")), (itemstack) -> deserializeWeaponCapabilityBuilder(tag));
			}
			
			ItemCapabilityReloadListener.weaponTypeProcessedCheck();
		}
	}
}