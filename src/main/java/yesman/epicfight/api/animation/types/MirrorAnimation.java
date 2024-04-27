package yesman.epicfight.api.animation.types;

import java.util.List;
import java.util.function.Function;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.AnimationClip;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.types.EntityState.StateFactor;
import yesman.epicfight.api.client.animation.Layer;
import yesman.epicfight.api.client.animation.property.ClientAnimationProperties;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class MirrorAnimation extends StaticAnimation {
	public StaticAnimation original;
	public StaticAnimation mirror;
	
	public MirrorAnimation(float convertTime, boolean repeatPlay, String registryName, String path1, String path2, Armature armature) {
		super(0.0F, false, registryName, armature);
		
		this.original = new StaticAnimation(convertTime, repeatPlay, path1, armature, true);
		this.mirror = new StaticAnimation(convertTime, repeatPlay, path2, armature, true);
	}
	
	@Override
	public void begin(LivingEntityPatch<?> entitypatch) {
		super.begin(entitypatch);
		
		if (entitypatch.isLogicalClient()) {
			StaticAnimation animation = this.checkHandAndReturnAnimation(entitypatch.getOriginal().getUsedItemHand());
			entitypatch.getClientAnimator().playAnimation(animation, 0.0F);
		}
	}
	
	@Override
	public void loadAnimation(ResourceManager resourceManager) {
		try {
			loadClip(resourceManager, this.original);
			loadClip(resourceManager, this.mirror);
		} catch (Exception e) {
			EpicFightMod.LOGGER.warn("Failed to load animation: " + this.resourceLocation);
			e.printStackTrace();
		}
		
		//this.original.onLoaded();
		//this.mirror.onLoaded();
		
		this.original.stateSpectrum.readFrom(this.stateSpectrumBlueprint);
		this.mirror.stateSpectrum.readFrom(this.stateSpectrumBlueprint);
	}
	
	@Override
	public List<StaticAnimation> getAllClipAnimations() {
		return List.of(this.original, this.mirror);
	}
	
	@Override
	public boolean isMetaAnimation() {
		return true;
	}
	
	@Override
	public boolean isClientAnimation() {
		return true;
	}
	
	@Override
	public AnimationClip getAnimationClip() {
		return this.original.getAnimationClip();
	}
	
	@Override
	public <V> StaticAnimation addProperty(StaticAnimationProperty<V> propertyType, V value) {
		this.original.properties.put(propertyType, value);
		this.mirror.properties.put(propertyType, value);
		return this;
	}
	
	@Override
	public StaticAnimation newTimePair(float start, float end) {
		super.newTimePair(start, end);
		
		this.original.newTimePair(start, end);
		this.mirror.newTimePair(start, end);
		
		return this;
	}
	
	@Override
	public StaticAnimation newConditionalTimePair(Function<LivingEntityPatch<?>, Integer> condition, float start, float end) {
		super.newConditionalTimePair(condition, start, end);
		
		this.original.newConditionalTimePair(condition, start, end);
		this.mirror.newConditionalTimePair(condition, start, end);
		
		return this;
	}
	
	@Override
	public <T> StaticAnimation addState(StateFactor<T> factor, T val) {
		super.addState(factor, val);
		
		this.original.addState(factor, val);
		this.mirror.addState(factor, val);
		
		return this;
	}
	
	@Override
	public <T> StaticAnimation removeState(StateFactor<T> factor) {
		super.removeState(factor);
		
		this.original.removeState(factor);
		this.mirror.removeState(factor);
		
		return this;
	}
	
	@Override
	public <T> StaticAnimation addConditionalState(int metadata, StateFactor<T> factor, T val) {
		super.addConditionalState(metadata, factor, val);
		
		this.original.addConditionalState(metadata, factor, val);
		this.mirror.addConditionalState(metadata, factor, val);
		
		return this;
	}
	
	@Override
	public <T> StaticAnimation addStateRemoveOld(StateFactor<T> factor, T val) {
		super.addStateRemoveOld(factor, val);
		
		this.original.addStateRemoveOld(factor, val);
		this.mirror.addStateRemoveOld(factor, val);
		
		return this;
	}
	
	@Override
	public <T> StaticAnimation addStateIfNotExist(StateFactor<T> factor, T val) {
		super.addStateIfNotExist(factor, val);
		
		this.original.addStateIfNotExist(factor, val);
		this.mirror.addStateIfNotExist(factor, val);
		
		return this;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public Layer.Priority getPriority() {
		return this.original.getProperty(ClientAnimationProperties.PRIORITY).orElse(Layer.Priority.LOWEST);
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public Layer.LayerType getLayerType() {
		return this.original.getProperty(ClientAnimationProperties.LAYER_TYPE).orElse(Layer.LayerType.BASE_LAYER);
	}
	
	private StaticAnimation checkHandAndReturnAnimation(InteractionHand hand) {
		if (hand == InteractionHand.OFF_HAND) {
			return this.mirror;
		}
		
		return this.original;
	}
}