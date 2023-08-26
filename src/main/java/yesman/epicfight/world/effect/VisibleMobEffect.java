package yesman.epicfight.world.effect;

import java.util.Map;
import java.util.function.Function;

import com.google.common.collect.Maps;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;

public class VisibleMobEffect extends MobEffect {
	protected final Map<Integer, ResourceLocation> icons;
	protected final Function<MobEffectInstance, Integer> metadataGetter;
	
	public VisibleMobEffect(MobEffectCategory category, int color, ResourceLocation textureLocation) {
		this(category, color, (effectInstance) -> 0, textureLocation);
	}
	
	public VisibleMobEffect(MobEffectCategory category, int color, Function<MobEffectInstance, Integer> metadataGetter, ResourceLocation... textureLocations) {
		super(MobEffectCategory.BENEFICIAL, color);
		this.icons = Maps.newHashMap();
		this.metadataGetter = metadataGetter;
		
		for (int i = 0; i < textureLocations.length; i++) {
			this.icons.put(i, textureLocations[i]);
		}
	}
	
	public ResourceLocation getIcon(MobEffectInstance effectInstance) {
		return this.icons.get(this.metadataGetter.apply(effectInstance));
	}
}