package yesman.epicfight.api.data.reloader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Pair;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import yesman.epicfight.api.forgeevent.SkillBuildEvent;
import yesman.epicfight.client.ClientEngine;
import yesman.epicfight.client.world.capabilites.entitypatch.player.LocalPlayerPatch;
import yesman.epicfight.data.loot.SkillBookLootModifier;
import yesman.epicfight.gameasset.EpicFightSkills;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.network.server.SPDatapackSyncSkill;
import yesman.epicfight.skill.Skill;
import yesman.epicfight.skill.SkillCategories;
import yesman.epicfight.skill.SkillSlot;
import yesman.epicfight.world.capabilities.skill.CapabilitySkill;

public class SkillManager extends SimpleJsonResourceReloadListener {
	private static final Map<ResourceLocation, Skill> SKILLS = Maps.newHashMap();
	private static final Map<ResourceLocation, Skill> LEARNABLE_SKILLS = Maps.newHashMap();
	private static final Map<ResourceLocation, CompoundTag> PARAMETER_MAP = Maps.newHashMap();
	private static final Map<ResourceLocation, Pair<? extends Skill.Builder<?>, Function<? extends Skill.Builder<?>, ? extends Skill>>> BUILDERS = Maps.newHashMap();
	private static final Gson GSON = (new GsonBuilder()).create();
	private static final Random RANDOM = new Random();
	private static int LAST_PICK = 0;
	
	public static Stream<ResourceLocation> getLearnableSkillNames(Predicate<Skill.Builder<?>> predicate) {
		return BUILDERS.values().stream().map(map -> map.getFirst()).filter(builder -> predicate.test(builder)).map(builder -> builder.getRegistryName());
	}
	
	public static Skill getSkill(String name) {
		ResourceLocation rl;
		
		if (name.indexOf(':') >= 0) {
			rl = new ResourceLocation(name);
		} else {
			rl = new ResourceLocation(EpicFightMod.MODID, name);
		}
		
		if (SKILLS.containsKey(rl)) {
			return SKILLS.get(rl);
		} else {
			return null;
		}
	}
	
	public static String getRandomLearnableSkillName() {
		List<Skill> values = new ArrayList<Skill>(LEARNABLE_SKILLS.values());
		LAST_PICK = (LAST_PICK + RANDOM.nextInt(values.size() - 1) + 1) % values.size();
		
		return values.get(LAST_PICK).toString();
	}
	
	public static <T extends Skill, B extends Skill.Builder<T>> void register(Function<B, T> constructor, B builder, String modid, String name) {
		ResourceLocation registryName = new ResourceLocation(modid, name);
		BUILDERS.put(registryName, Pair.of(builder.setRegistryName(registryName), constructor));
		
		EpicFightMod.LOGGER.info("register skill " + registryName);
	}
	
	public static void buildAll() {
		SkillBuildEvent onBuild = new SkillBuildEvent(BUILDERS, SKILLS, LEARNABLE_SKILLS);
		
		MinecraftForge.EVENT_BUS.post(onBuild);
		
		SkillBookLootModifier.createSkillLootTable(BUILDERS.keySet());
	}
	
	public static Stream<CompoundTag> getDataStream() {
		Stream<CompoundTag> tagStream = PARAMETER_MAP.entrySet().stream().map((entry) -> {
			entry.getValue().putString("id", entry.getKey().toString());
			
			return entry.getValue();
		});
		
		return tagStream;
	}
	
	public static int getParamCount() {
		return PARAMETER_MAP.size();
	}
	
	public SkillManager() {
		super(GSON, "skill_parameters");
	}
	
	@Override
	protected void apply(Map<ResourceLocation, JsonElement> objectIn, ResourceManager resourceManager, ProfilerFiller profileFiller) {
		SkillManager.buildAll();
		
		for (Map.Entry<ResourceLocation, JsonElement> entry : objectIn.entrySet()) {
			CompoundTag tag = null;
			
			try {
				tag = TagParser.parseTag(entry.getValue().toString());
			} catch (CommandSyntaxException e) {
				e.printStackTrace();
			}
			
			if (SKILLS.containsKey(entry.getKey())) {
				SKILLS.get(entry.getKey()).setParams(tag);
				PARAMETER_MAP.put(entry.getKey(), tag);
			} else {
				EpicFightMod.LOGGER.warn("Skill " + entry.getKey() + " not exists");
			}
		}
	}
	
	@OnlyIn(Dist.CLIENT)
	public static void processServerPacket(SPDatapackSyncSkill packet) {
		SkillManager.buildAll();
		
		for (CompoundTag tag : packet.getTags()) {
			if (!SKILLS.containsKey(new ResourceLocation(tag.getString("id")))) {
				EpicFightMod.LOGGER.warn("Failed to syncronize Datapack for skill: " + tag.getString("id"));
				continue;
			}
			
			SKILLS.get(new ResourceLocation(tag.getString("id"))).setParams(tag);
		}
		
		LocalPlayerPatch localplayerpatch = ClientEngine.getInstance().getPlayerPatch();
		
		if (localplayerpatch != null) {
			for (String skillName : packet.getLearnedSkills()) {
				localplayerpatch.getSkillCapability().addLearnedSkill(SkillManager.getSkill(skillName));
			}
			
			for (Map.Entry<SkillSlot, String> skillsBySlotEntry : packet.getSkillsBySlot().entrySet()) {
				localplayerpatch.getSkill(skillsBySlotEntry.getKey()).setSkill(SkillManager.getSkill(skillsBySlotEntry.getValue()));
				localplayerpatch.getSkill(skillsBySlotEntry.getKey()).setDisabled(false);
			}
			
			CapabilitySkill skillCapability = localplayerpatch.getSkillCapability();
			skillCapability.skillContainers[SkillCategories.BASIC_ATTACK.universalOrdinal()].setSkill(EpicFightSkills.BASIC_ATTACK);
			skillCapability.skillContainers[SkillCategories.AIR_ATTACK.universalOrdinal()].setSkill(EpicFightSkills.AIR_ATTACK);
			skillCapability.skillContainers[SkillCategories.KNOCKDOWN_WAKEUP.universalOrdinal()].setSkill(EpicFightSkills.KNOCKDOWN_WAKEUP);
		}
	}
}