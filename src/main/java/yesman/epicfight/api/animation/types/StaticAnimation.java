package yesman.epicfight.api.animation.types;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import com.google.common.collect.Maps;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.AnimationClip;
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.AnimationPlayer;
import yesman.epicfight.api.animation.AnimationProvider;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.TransformSheet;
import yesman.epicfight.api.animation.property.AnimationEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.TimePeriodEvent;
import yesman.epicfight.api.animation.property.AnimationEvent.TimeStampedEvent;
import yesman.epicfight.api.animation.property.AnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.ActionAnimationProperty;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.types.EntityState.StateFactor;
import yesman.epicfight.api.client.animation.Layer;
import yesman.epicfight.api.client.animation.Layer.LayerType;
import yesman.epicfight.api.client.animation.property.ClientAnimationProperties;
import yesman.epicfight.api.client.animation.property.JointMask.BindModifier;
import yesman.epicfight.api.client.animation.property.TrailInfo;
import yesman.epicfight.api.client.model.ItemSkin;
import yesman.epicfight.api.client.model.ItemSkins;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.model.JsonModelLoader;
import yesman.epicfight.api.utils.TypeFlexibleHashMap;
import yesman.epicfight.config.EpicFightOptions;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class StaticAnimation extends DynamicAnimation implements AnimationProvider<StaticAnimation> {
	protected final Map<AnimationProperty<?>, Object> properties = Maps.newHashMap();
	protected final StateSpectrum.Blueprint stateSpectrumBlueprint = new StateSpectrum.Blueprint();
	protected final Armature armature;
	protected final int animationId;
	protected final ResourceLocation registryName;
	protected final StateSpectrum stateSpectrum = new StateSpectrum();
	protected ResourceLocation resourceLocation;
	
	public StaticAnimation() {
		super(0.0F, true);
		this.resourceLocation = null;
		this.registryName = null;
		this.armature = null;
		this.animationId = -1;
	}
	
	public StaticAnimation(boolean repeatPlay, String path, Armature armature) {
		this(EpicFightOptions.GENERAL_ANIMATION_CONVERT_TIME, repeatPlay, path, armature);
	}
	
	public StaticAnimation(float convertTime, boolean isRepeat, String path, Armature armature) {
		super(convertTime, isRepeat);
		
		int colon = path.indexOf(':');
		String modid = (colon == -1) ? AnimationManager.getInstance().workingModId() : path.substring(0, colon);
		String folderPath = (colon == -1) ? path : path.substring(colon + 1);
		
		this.resourceLocation = new ResourceLocation(modid, "animmodels/animations/" + folderPath + ".json");
		this.registryName = new ResourceLocation(modid, folderPath);
		this.armature = armature;
		this.animationId = AnimationManager.getInstance().registerAnimation(this);
	}
	
	public StaticAnimation(float convertTime, boolean repeatPlay, String path, Armature armature, boolean noRegister) {
		super(convertTime, repeatPlay);
		
		int colon = path.indexOf(':');
		String modid = (colon == -1) ? AnimationManager.getInstance().workingModId() : path.substring(0, colon);
		String folderPath = (colon == -1) ? path : path.substring(colon + 1);
		
		this.resourceLocation = new ResourceLocation(modid, "animmodels/animations/" + folderPath + ".json");
		this.registryName = new ResourceLocation(modid, folderPath);
		this.armature = armature;
		
		if (noRegister) {
			this.animationId = -1;
		} else {
			this.animationId = AnimationManager.getInstance().registerAnimation(this);
		}
	}
	
	/* Multilayer Constructor */
	public StaticAnimation(ResourceLocation baseAnimPath, float convertTime, boolean repeatPlay, String registryName, Armature armature, boolean multilayer) {
		super(convertTime, repeatPlay);
		
		this.resourceLocation = baseAnimPath;
		this.registryName = new ResourceLocation(registryName);
		this.armature = armature;
		this.animationId = -1;
	}
	
	public static void loadClip(ResourceManager resourceManager, StaticAnimation animation) throws Exception {
		JsonModelLoader modelLoader = (new JsonModelLoader(resourceManager, animation.resourceLocation));
		AnimationManager.getInstance().loadAnimationClip(animation, modelLoader::loadClipForAnimation);
	}
	
	public static void loadAllJointsClip(ResourceManager resourceManager, StaticAnimation animation) throws Exception {
		JsonModelLoader modelLoader = (new JsonModelLoader(resourceManager, animation.resourceLocation));
		AnimationManager.getInstance().loadAnimationClip(animation, modelLoader::loadAllJointsClipForAnimation);
	}
	
	public void loadAnimation(ResourceManager resourceManager) {
		try {
			loadClip(resourceManager, this);
		} catch (Exception e) {
			AnimationManager.getInstance().onFailed(this);
			EpicFightMod.LOGGER.warn("Failed to load animation: " + this.resourceLocation);
			e.printStackTrace();
		}
		
		this.onLoaded();
	}
	
	protected void onLoaded() {
		this.stateSpectrum.readFrom(this.stateSpectrumBlueprint);
	}
	
	@Override
	public void begin(LivingEntityPatch<?> entitypatch) {
		this.getProperty(StaticAnimationProperty.ON_BEGIN_EVENTS).ifPresent((events) -> {
			for (AnimationEvent event : events) {
				event.executeIfRightSide(entitypatch, this);
			}
		});
		
		if (entitypatch.isLogicalClient()) {
			this.getProperty(ClientAnimationProperties.TRAIL_EFFECT).ifPresent((trailInfos) -> {
				int idx = 0;
				
				for (TrailInfo trailInfo : trailInfos) {
					double eid = Double.longBitsToDouble((long)entitypatch.getOriginal().getId());
					double animid = Double.longBitsToDouble((long)this.animationId);
					double jointId = Double.longBitsToDouble((long)this.armature.searchJointByName(trailInfo.joint).getId());
					double index = Double.longBitsToDouble((long)idx++);
					
					if (trailInfo.hand != null) {
						ItemStack stack = entitypatch.getOriginal().getItemInHand(trailInfo.hand);
						ItemSkin itemSkin = ItemSkins.getItemSkin(stack.getItem());
						
						if (itemSkin != null) {
							trailInfo = itemSkin.trailInfo.overwrite(trailInfo);
						}
					}
					
					if (!trailInfo.playable()) {
						continue;
					}
					
					entitypatch.getOriginal().level().addParticle(trailInfo.particle, eid, 0, animid, jointId, index, 0);
				}
			});
		}
	}
	
	@Override
	public void end(LivingEntityPatch<?> entitypatch, DynamicAnimation nextAnimation, boolean isEnd) {
		this.getProperty(StaticAnimationProperty.ON_END_EVENTS).ifPresent((events) -> {
			for (AnimationEvent event : events) {
				event.executeIfRightSide(entitypatch, this);
			}
		});
	}
	
	@Override
	public void tick(LivingEntityPatch<?> entitypatch) {
		this.getProperty(StaticAnimationProperty.EVENTS).ifPresent((events) -> {
			for (AnimationEvent event : events) {
				event.executeIfRightSide(entitypatch, this);
			}
		});
		
		this.getProperty(StaticAnimationProperty.TIME_STAMPED_EVENTS).ifPresent((events) -> {
			AnimationPlayer player = entitypatch.getAnimator().getPlayerFor(this);
			
			if (player != null) {
				float prevElapsed = player.getPrevElapsedTime();
				float elapsed = player.getElapsedTime();
				
				for (TimeStampedEvent event : events) {
					event.executeIfRightSide(entitypatch, this, prevElapsed, elapsed);
				}
			}
		});
		
		this.getProperty(StaticAnimationProperty.TIME_PERIOD_EVENTS).ifPresent((events) -> {
			AnimationPlayer player = entitypatch.getAnimator().getPlayerFor(this);
			
			if (player != null) {
				float prevElapsed = player.getPrevElapsedTime();
				float elapsed = player.getElapsedTime();
				
				for (TimePeriodEvent event : events) {
					event.executeIfRightSide(entitypatch, this, prevElapsed, elapsed);
				}
			}
		});
	}
	
	@Override
	public EntityState getState(LivingEntityPatch<?> entitypatch, float time) {
		return new EntityState(this.getStatesMap(entitypatch, time));
	}
	
	@Override
	public TypeFlexibleHashMap<StateFactor<?>> getStatesMap(LivingEntityPatch<?> entitypatch, float time) {
		return this.stateSpectrum.getStateMap(entitypatch, time);
	}
	
	@Override
	public <T> T getState(StateFactor<T> stateFactor, LivingEntityPatch<?> entitypatch, float time) {
		return this.stateSpectrum.getSingleState(stateFactor, entitypatch, time);
	}
	
	@Override
	public boolean isJointEnabled(LivingEntityPatch<?> entitypatch, Layer.Priority layer, String joint) {
		if (!super.isJointEnabled(entitypatch, layer, joint)) {
			return false;
		} else {
			return this.getProperty(ClientAnimationProperties.JOINT_MASK).map((bindModifier) -> !bindModifier.isMasked(entitypatch.getCurrentLivingMotion(), joint)).orElse(true);
		}
	}
	
	@Override
	public BindModifier getBindModifier(LivingEntityPatch<?> entitypatch, Layer.Priority layer, String joint) {
		return this.getProperty(ClientAnimationProperties.JOINT_MASK).map((jointMaskEntry) -> {
			return jointMaskEntry.getMask(entitypatch.getCurrentLivingMotion()).getBindModifier(joint);
		}).orElse(null);
	}
	
	@Override
	public void modifyPose(DynamicAnimation animation, Pose pose, LivingEntityPatch<?> entitypatch, float time, float partialTicks) {
		entitypatch.poseTick(animation, pose, time);
		
		AnimationProperty.PoseModifier modifier = this.getProperty(StaticAnimationProperty.POSE_MODIFIER).orElse(null);
		
		if (modifier != null) {
			modifier.modify(animation, pose, entitypatch, time, partialTicks);
		}
	}
	
	@Override
	public boolean isStaticAnimation() {
		return true;
	}
	
	@Override
	public int getId() {
		return this.animationId;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StaticAnimation staticAnimation) {
			return this.getId() == staticAnimation.getId();
		}
		
		return super.equals(obj);
	}
	
	public boolean idBetween(StaticAnimation a1, StaticAnimation a2) {
		return a1.getId() <= this.getId() && a2.getId() >= this.getId();
	}
	
	public boolean in(StaticAnimation[] animations) {
		for (StaticAnimation animation : animations) {
			if (this.equals(animation)) {
				return true;
			}
		}
		
		return false;
	}
	
	@SuppressWarnings("rawtypes")
	public boolean in(AnimationProvider[] animationProviders) {
		for (AnimationProvider animationProvider : animationProviders) {
			if (this.equals(animationProvider.get())) {
				return true;
			}
		}
		
		return false;
	}
	
	public StaticAnimation setResourceLocation(String path) {
		int colon = path.indexOf(':');
		String modid = (colon == -1) ? AnimationManager.getInstance().workingModId() : path.substring(0, colon);
		String folderPath = (colon == -1) ? path : path.substring(colon + 1);
		
		this.resourceLocation = new ResourceLocation(modid, "animmodels/animations/" + folderPath + ".json");
		
		return this;
	}
	
	public ResourceLocation getLocation() {
		return this.resourceLocation;
	}
	
	@Override
	public ResourceLocation getRegistryName() {
		return this.registryName;
	}
	
	public Armature getArmature() {
		return this.armature;
	}
	
	@Override
	public float getPlaySpeed(LivingEntityPatch<?> entitypatch) {
		return 1.0F;
	}
	
	@Override
	public TransformSheet getCoord() {
		return this.getProperty(ActionAnimationProperty.COORD).orElse(super.getCoord());
	}
	
	@Override
	public String toString() {
		String classPath = this.getClass().toString();
		
		return classPath.substring(classPath.lastIndexOf(".") + 1) + " " + this.getLocation();
	}
	
	/**
	 * Internal use only
	 */
	@Deprecated
	public StaticAnimation addPropertyUnsafe(AnimationProperty<?> propertyType, Object value) {
		this.properties.put(propertyType, value);
		return this;
	}
	
	public <V> StaticAnimation addProperty(StaticAnimationProperty<V> propertyType, V value) {
		this.properties.put(propertyType, value);
		return this;
	}
	
	public StaticAnimation addEvents(StaticAnimationProperty<?> key, AnimationEvent... events) {
		this.properties.put(key, events);
		return this;
	}
	
	public <V extends AnimationEvent> StaticAnimation addEvents(TimeStampedEvent... events) {
		this.properties.put(StaticAnimationProperty.TIME_STAMPED_EVENTS, events);
		return this;
	}
	
	public <V extends AnimationEvent> StaticAnimation addEvents(TimePeriodEvent... events) {
		this.properties.put(StaticAnimationProperty.TIME_PERIOD_EVENTS, events);
		return this;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <V> Optional<V> getProperty(AnimationProperty<V> propertyType) {
		return (Optional<V>) Optional.ofNullable(this.properties.get(propertyType));
	}
	
	@OnlyIn(Dist.CLIENT)
	public Layer.Priority getPriority() {
		return this.getProperty(ClientAnimationProperties.PRIORITY).orElse(Layer.Priority.LOWEST);
	}
	
	@OnlyIn(Dist.CLIENT)
	public Layer.LayerType getLayerType() {
		return this.getProperty(ClientAnimationProperties.LAYER_TYPE).orElse(LayerType.BASE_LAYER);
	}
	
	public StaticAnimation newTimePair(float start, float end) {
		this.stateSpectrumBlueprint.newTimePair(start, end);
		return this;
	}
	
	public StaticAnimation newConditionalTimePair(Function<LivingEntityPatch<?>, Integer> condition, float start, float end) {
		this.stateSpectrumBlueprint.newConditionalTimePair(condition, start, end);
		return this;
	}
	
	public <T> StaticAnimation addState(StateFactor<T> factor, T val) {
		this.stateSpectrumBlueprint.addState(factor, val);
		return this;
	}
	
	public <T> StaticAnimation removeState(StateFactor<T> factor) {
		this.stateSpectrumBlueprint.removeState(factor);
		return this;
	}
	
	public <T> StaticAnimation addConditionalState(int metadata, StateFactor<T> factor, T val) {
		this.stateSpectrumBlueprint.addConditionalState(metadata, factor, val);
		return this;
	}
	
	public <T> StaticAnimation addStateRemoveOld(StateFactor<T> factor, T val) {
		this.stateSpectrumBlueprint.addStateRemoveOld(factor, val);
		return this;
	}
	
	public <T> StaticAnimation addStateIfNotExist(StateFactor<T> factor, T val) {
		this.stateSpectrumBlueprint.addStateIfNotExist(factor, val);
		return this;
	}

	@Override
	public AnimationClip getAnimationClip() {
		return AnimationManager.getInstance().getStaticAnimationClip(this);
	}
	
	public List<StaticAnimation> getAllClipAnimations() {
		return List.of(this);
	}
	
	@Override
	public StaticAnimation get() {
		return AnimationManager.getInstance().refreshAnimation(this);
	}
}