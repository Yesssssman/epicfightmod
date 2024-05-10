package yesman.epicfight.data.conditions.entity;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ImmutableMap;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.data.conditions.Condition.LivingEntityCondition;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class HealthPoint extends LivingEntityCondition {
	private float health;
	private Comparator comparator;
	
	public HealthPoint() {
		this.health = 0.0F;
	}
	
	public HealthPoint(float health, Comparator comparator) {
		this.health = health;
		this.comparator = comparator;
	}
	
	@Override
	public HealthPoint read(CompoundTag tag) {
		if (!tag.contains("comparator")) {
			throw new IllegalArgumentException("HealthPoint condition error: comparator not specified!");
		}
		
		if (!tag.contains("health")) {
			throw new IllegalArgumentException("HealthPoint condition error: health not specified!");
		}
		
		String sComparator = tag.getString("comparator").toUpperCase(Locale.ROOT);
		
		try {
			this.comparator = Comparator.valueOf(sComparator);
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("HealthPoint condition error: invalid comparator " + sComparator);
		}
		
		this.health = tag.getFloat("health");
		
		return this;
	}
	
	@Override
	public CompoundTag serializePredicate() {
		CompoundTag tag = new CompoundTag();
		tag.putString("comparator", this.comparator.toString().toLowerCase(Locale.ROOT));
		tag.putFloat("health", this.health);
		
		return tag;
	}
	
	@Override
	public boolean predicate(LivingEntityPatch<?> target) {
		switch (this.comparator) {
		case LESS_ABSOLUTE:
			return this.health > target.getOriginal().getHealth();
		case GREATER_ABSOLUTE:
			return this.health < target.getOriginal().getHealth();
		case LESS_RATIO:
			return this.health > target.getOriginal().getHealth() / target.getOriginal().getMaxHealth();
		case GREATER_RATIO:
			return this.health < target.getOriginal().getHealth() / target.getOriginal().getMaxHealth();
		}
		
		return true;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public Set<Map.Entry<String, Object>> getAcceptingParameters() {
		return ImmutableMap.of("health", (Object)"", "comparator", (Object)"").entrySet();
	}
	
	public enum Comparator {
		GREATER_ABSOLUTE, LESS_ABSOLUTE, GREATER_RATIO, LESS_RATIO
	}
}
