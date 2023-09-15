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
import yesman.epicfight.api.animation.AnimationManager;
import yesman.epicfight.api.animation.AnimationPlayer;
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
import yesman.epicfight.api.client.animation.property.JointMask;
import yesman.epicfight.api.client.animation.property.JointMask.BindModifier;
import yesman.epicfight.api.client.animation.property.TrailInfo;
import yesman.epicfight.api.client.model.ItemSkin;
import yesman.epicfight.api.client.model.ItemSkins;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.model.JsonModelLoader;
import yesman.epicfight.api.utils.TypeFlexibleHashMap;
import yesman.epicfight.config.ConfigurationIngame;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class StaticAnimation extends DynamicAnimation {
	protected final Map<AnimationProperty<?>, Object> properties = Maps.newHashMap();
	protected final StateSpectrum.Blueprint stateSpectrumBlueprint = new StateSpectrum.Blueprint();
	protected final ResourceLocation resourceLocation;
	protected final Armature armature;
	protected final int namespaceId;
	protected final int animationId;
	
	private final StateSpectrum stateSpectrum = new StateSpectrum();
	
	public StaticAnimation() {
		super(0.0F, false);
		this.namespaceId = -1;
		this.animationId = -1;
		this.resourceLocation = null;
		this.armature = null;
	}
	
	public StaticAnimation(boolean repeatPlay, String path, Armature armature) {
		this(ConfigurationIngame.GENERAL_ANIMATION_CONVERT_TIME, repeatPlay, path, armature);
	}
	
	public StaticAnimation(float convertTime, boolean isRepeat, String path, Armature armature) {
		super(convertTime, isRepeat);
		
		AnimationManager animationManager = EpicFightMod.getInstance().animationManager;
		this.namespaceId = animationManager.getNamespaceHash();
		this.animationId = animationManager.getIdCounter();
		
		int colon = path.indexOf(':');
		String modid = (colon == -1) ? animationManager.getModid() : path.substring(0, colon);
		String folderPath = (colon == -1) ? path : path.substring(colon + 1, path.length());
		
		animationManager.getIdMap().put(this.animationId, this);
		this.resourceLocation = new ResourceLocation(modid, "animmodels/animations/" + folderPath);
		animationManager.getNameMap().put(new ResourceLocation(animationManager.getModid(), folderPath), this);
		this.armature = armature;
	}
	
	public StaticAnimation(float convertTime, boolean repeatPlay, String path, Armature armature, boolean notRegisteredInAnimationManager) {
		super(convertTime, repeatPlay);
		
		AnimationManager animationManager = EpicFightMod.getInstance().animationManager;
		this.namespaceId = animationManager.getModid().hashCode();
		this.animationId = -1;
		this.resourceLocation = new ResourceLocation(animationManager.getModid(), "animmodels/animations/" + path);
		this.armature = armature;
	}
	
	public static void load(ResourceManager resourceManager, ResourceLocation rl, StaticAnimation animation) {
		(new JsonModelLoader(resourceManager, rl)).loadStaticAnimation(animation);
	}
	
	public static void load(ResourceManager resourceManager, StaticAnimation animation) {
		ResourceLocation path = new ResourceLocation(animation.resourceLocation.getNamespace(), animation.resourceLocation.getPath() + ".json");
		(new JsonModelLoader(resourceManager, path)).loadStaticAnimation(animation);
	}
	
	public static void loadBothSide(ResourceManager resourceManager, StaticAnimation animation) {
		ResourceLocation path = new ResourceLocation(animation.resourceLocation.getNamespace(), animation.resourceLocation.getPath() + ".json");
		(new JsonModelLoader(resourceManager, path)).loadStaticAnimationBothSide(animation);
	}
	
	public void loadAnimation(ResourceManager resourceManager) {
		try {
			int id = Integer.parseInt(this.resourceLocation.getPath().substring(22));
			StaticAnimation animation = EpicFightMod.getInstance().animationManager.findAnimationById(this.namespaceId, id);
			ResourceLocation path = new ResourceLocation(animation.resourceLocation.getNamespace(), animation.resourceLocation.getPath() + ".json");
			load(resourceManager, path, this);
			
			this.jointTransforms = animation.jointTransforms;
		} catch (NumberFormatException e) {
			load(resourceManager, this);
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
					double eid = Double.longBitsToDouble(entitypatch.getOriginal().getId());
					double modid = Double.longBitsToDouble(this.namespaceId);
					double animid = Double.longBitsToDouble(this.animationId);
					double jointId = Double.longBitsToDouble(this.armature.searchJointByName(trailInfo.joint).getId());
					double index = Double.longBitsToDouble(idx++);
					
					if (trailInfo.hand != null) {
						ItemStack stack = entitypatch.getOriginal().getItemInHand(trailInfo.hand);
						ItemSkin itemSkin = ItemSkins.getItemSkin(stack.getItem());
						
						if (itemSkin != null) {
							trailInfo = itemSkin.trailInfo.overwrite(trailInfo);
						}
					}
					
					if (trailInfo.particle == null) {
						continue;
					}
					
					entitypatch.getOriginal().level.addParticle(trailInfo.particle, eid, modid, animid, jointId, index, 0);
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
			return this.getProperty(ClientAnimationProperties.JOINT_MASK).map((bindModifier) -> 
						!bindModifier.isMasked(entitypatch.getCurrentLivingMotion(), joint)).orElse(true);
		}
	}
	
	@Override
	public BindModifier getBindModifier(LivingEntityPatch<?> entitypatch, Layer.Priority layer, String joint) {
		return this.getProperty(ClientAnimationProperties.JOINT_MASK).map((jointMaskEntry) -> {
			List<JointMask> list = jointMaskEntry.getMask(entitypatch.getCurrentLivingMotion());
			int position = list.indexOf(JointMask.of(joint));
			
			if (position >= 0) {
				return list.get(position).getBindModifier();
			} else {
				return null;
			}
		}).orElse(null);
	}
	
	@Override
	public void modifyPose(DynamicAnimation animation, Pose pose, LivingEntityPatch<?> entitypatch, float time, float partialTicks) {
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
	public int getNamespaceId() {
		return this.namespaceId;
	}
	
	@Override
	public int getId() {
		return this.animationId;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof StaticAnimation staticAnimation) {
			return this.getNamespaceId() == staticAnimation.getNamespaceId() && this.getId() == staticAnimation.getId();
		}
		
		return super.equals(obj);
	}
	
	public boolean between(StaticAnimation a1, StaticAnimation a2) {
		if (a1.getNamespaceId() != a2.getNamespaceId()) {
			return false;
		} else if (a1.getId() <= this.getId() && a2.getId() >= this.getId()) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean in(StaticAnimation[] animations) {
		for (StaticAnimation animation : animations) {
			if (this.equals(animation)) {
				return true;
			}
		}
		
		return false;
	}
	
	public ResourceLocation getLocation() {
		return this.resourceLocation;
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
}