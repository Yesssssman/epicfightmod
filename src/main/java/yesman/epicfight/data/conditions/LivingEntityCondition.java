package yesman.epicfight.data.conditions;

import java.util.Map;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public abstract class LivingEntityCondition implements Condition<LivingEntityPatch<?>> {
	public static final Map<ResourceLocation, Function<CompoundTag, LivingEntityCondition>> LIVING_ENTITY_CONDITIONS = Maps.newHashMap();
	
	static {
		register(new ResourceLocation(EpicFightMod.MODID, "offhand_category"), OffhandCategoryCondition::new);
	}
	
	public static void register(ResourceLocation rl, Function<CompoundTag, LivingEntityCondition> builder) {
		LIVING_ENTITY_CONDITIONS.put(rl, builder);
	}
	
	public static Function<CompoundTag, LivingEntityCondition> get(ResourceLocation rl) {
		if (!LIVING_ENTITY_CONDITIONS.containsKey(rl)) {
			throw new IllegalArgumentException("Can't find the condition constructor: " + rl);
		}
		
		return LIVING_ENTITY_CONDITIONS.get(rl);
	}
	
	public LivingEntityCondition(CompoundTag tag) {
		this.read(tag);
	}
}