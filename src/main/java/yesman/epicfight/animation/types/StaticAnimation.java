package yesman.epicfight.animation.types;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.Maps;

import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.animation.AnimationManager;
import yesman.epicfight.animation.AnimationPlayer;
import yesman.epicfight.animation.property.Property;
import yesman.epicfight.animation.property.Property.StaticAnimationProperty;
import yesman.epicfight.capabilities.entity.LivingData;
import yesman.epicfight.client.animation.ClientAnimationProperties;
import yesman.epicfight.client.animation.Layer;
import yesman.epicfight.client.animation.PoseModifyingEntry;
import yesman.epicfight.client.animation.PoseModifyingFunction;
import yesman.epicfight.collada.AnimationDataExtractor;
import yesman.epicfight.config.ConfigurationIngame;
import yesman.epicfight.main.EpicFightMod;
import yesman.epicfight.model.Model;

public class StaticAnimation extends DynamicAnimation {
	protected final Map<Property<?>, Object> properties = Maps.<Property<?>, Object>newHashMap();
	protected final Model model;
	protected final ResourceLocation animationLocation;
	protected final int namespaceId;
	protected final int animationId;
	
	public StaticAnimation() {
		super(0.0F, false);
		this.namespaceId = -1;
		this.animationId = -1;
		this.animationLocation = null;
		this.model = null;
	}
	
	public StaticAnimation(boolean repeatPlay, String path, Model model) {
		this(ConfigurationIngame.GENERAL_ANIMATION_CONVERT_TIME, repeatPlay, path, model);
	}
	
	public StaticAnimation(float convertTime, boolean isRepeat, String path, Model model) {
		super(convertTime, isRepeat);
		AnimationManager animationManager = EpicFightMod.getInstance().animationManager;
		this.namespaceId = animationManager.getNamespaceHash();
		this.animationId = animationManager.getIdCounter();
		animationManager.getIdMap().put(this.animationId, this);
		this.animationLocation = new ResourceLocation(animationManager.getModid(), "animations/" + path);
		animationManager.getNameMap().put(this.animationLocation, this);
		this.model = model;
	}
	
	public StaticAnimation(float convertTime, boolean repeatPlay, String path, Model model, boolean isInner) {
		super(convertTime, repeatPlay);
		this.namespaceId = -1;
		this.animationId = -1;
		this.animationLocation = new ResourceLocation(EpicFightMod.getInstance().animationManager.getModid(), "animations/" + path);
		this.model = model;
	}
	
	public void loadAnimation(IResourceManager resourceManager) {
		try {
			int id = Integer.parseInt(this.animationLocation.getPath().substring(11));
			StaticAnimation animation = EpicFightMod.getInstance().animationManager.findAnimation(this.namespaceId, id);
			this.jointTransforms = animation.jointTransforms;
			this.setTotalTime(animation.totalTime);
		} catch (NumberFormatException e) {
			this.load(resourceManager);
		}
	}
	
	protected void load(IResourceManager resourceManager) {
		AnimationDataExtractor.loadStaticAnimation(resourceManager, this);
	}
	
	@Override
	public void onUpdate(LivingData<?> entitydata) {
		this.getProperty(StaticAnimationProperty.SOUNDS).ifPresent((sounds) -> {
			AnimationPlayer player = entitydata.getAnimator().getPlayerFor(this);
			if (player != null) {
				float prevElapsed = player.getPrevElapsedTime();
				float elapsed = player.getElapsedTime();
				
				for (SoundKey key : sounds) {
					if (key.time < prevElapsed || key.time >= elapsed) {
						continue;
					} else {
						if (entitydata.isRemote() == key.isRemote) {
							entitydata.playSound(key.sound, 0.0F, 0.0F);
						}
					}
				}
			}
		});
	}
	
	@Override
	public boolean isEnabledJoint(LivingData<?> entitydata, String joint) {
		if (!super.isEnabledJoint(entitydata, joint)) {
			return false;
		} else {
			boolean bool = this.getProperty(ClientAnimationProperties.POSE_MODIFIER).map((bindModifier) -> {
				return !bindModifier.isMasked(entitydata.getClientAnimator().getLivingMotionFor(this.getPriority()), joint);
			}).orElse(true);
			return bool;
		}
	}
	
	@Override
	public PoseModifyingFunction getPoseModifyingFunction(LivingData<?> entitydata, String joint) {
		return this.getProperty(ClientAnimationProperties.POSE_MODIFIER).map((bindModifier) -> {
			List<PoseModifyingEntry> list = bindModifier.getBindData(entitydata.getClientAnimator().getLivingMotionFor(this.getPriority()));
			int position = list.indexOf(PoseModifyingEntry.compareUtil(joint));
			if (position >= 0) {
				return list.get(position).getPoseModifyingFunction();
			} else {
				return PoseModifyingEntry.NONE;
			}
		}).orElse(PoseModifyingEntry.NONE);
	}
	
	@Override
	public int getNamespaceId() {
		return this.namespaceId;
	}
	
	@Override
	public int getId() {
		return this.animationId;
	}
	
	public ResourceLocation getLocation() {
		return this.animationLocation;
	}
	
	public Model getModel() {
		return this.model;
	}
	
	public boolean isBasicAttackAnimation() {
		return false;
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
	
	@SuppressWarnings("unchecked")
	@Override
	public <V> Optional<V> getProperty(Property<V> propertyType) {
		return (Optional<V>) Optional.ofNullable(this.properties.get(propertyType));
	}
	
	@OnlyIn(Dist.CLIENT)
	public Layer.Priority getPriority() {
		return this.getProperty(ClientAnimationProperties.PRIORITY).orElse(Layer.Priority.LOWEST);
	}
	
	public static class SoundKey implements Comparable<SoundKey> {
		float time;
		boolean isRemote;
		SoundEvent sound;
		
		private SoundKey(float time, boolean isRemote, SoundEvent sound) {
			this.time = time;
			this.isRemote = isRemote;
			this.sound = sound;
		}
		
		@Override
		public int compareTo(SoundKey arg0) {
			if(this.time == arg0.time) {
				return 0;
			} else {
				return this.time > arg0.time ? 1 : -1;
			}
		}
		
		public static SoundKey create(float time, SoundEvent sound, boolean isRemote) {
			return new SoundKey(time, isRemote, sound);
		}
	}
}