package maninhouse.epicfight.animation.types;

import java.util.Map;
import java.util.Optional;

import com.google.common.collect.Maps;

import maninhouse.epicfight.animation.AnimationPlayer;
import maninhouse.epicfight.animation.property.Property;
import maninhouse.epicfight.animation.property.Property.StaticAnimationProperty;
import maninhouse.epicfight.capabilities.entity.LivingData;
import maninhouse.epicfight.client.animation.BindingOperation;
import maninhouse.epicfight.client.animation.BindingOption;
import maninhouse.epicfight.client.animation.ClientAnimationProperty;
import maninhouse.epicfight.client.animation.Layer;
import maninhouse.epicfight.collada.AnimationDataExtractor;
import maninhouse.epicfight.config.ConfigurationIngame;
import maninhouse.epicfight.gamedata.Animations;
import maninhouse.epicfight.main.EpicFightMod;
import maninhouse.epicfight.model.Armature;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class StaticAnimation extends DynamicAnimation {
	protected final Map<Property<?>, Object> properties = Maps.<Property<?>, Object>newHashMap();
	protected ResourceLocation animationLocation;
	protected final int animationId;
	
	public StaticAnimation() {
		super();
		this.animationId = -1;
	}
	
	public StaticAnimation(int id, float convertTime, boolean isRepeat, String path) {
		super(convertTime, isRepeat);
		
		if (Animations.ANIMATIONS.keySet().contains(id)) {
			throw new IllegalStateException("EpicFightMod : Animation id " + id + " is duplicated!");
		}
		
		if (path != null) {
			this.animationLocation = new ResourceLocation(EpicFightMod.MODID, "models/animations/" + path);
		}
		
		this.totalTime = 0;
		this.animationId = id;
		
		if(id >= 0) {
			Animations.ANIMATIONS.put(id, this);
		}
	}
	
	public StaticAnimation(String path) {
		this();
		this.animationLocation = new ResourceLocation(EpicFightMod.MODID, "models/animations/" + path);
	}
	
	public StaticAnimation(float convertTime, boolean repeatPlay, String path) {
		super(convertTime, repeatPlay);
		this.animationId = -1;
		this.animationLocation = new ResourceLocation(EpicFightMod.MODID, "models/animations/" + path);
	}

	public StaticAnimation(int id, boolean repeatPlay, String path) {
		this(id, ConfigurationIngame.GENERAL_ANIMATION_CONVERT_TIME, repeatPlay, path);
	}
	
	public StaticAnimation loadFrom(StaticAnimation animation) {
		this.jointTransforms = animation.jointTransforms;
		this.setTotalTime(animation.totalTime);
		return this;
	}
	
	public StaticAnimation loadAnimation(Armature armature, Dist dist) {
		if (this.animationLocation != null) {
			AnimationDataExtractor.extractStaticAnimation(this.animationLocation, this, armature, dist);
		}
		
		return this;
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
	public boolean isEnabledJoint(String joint) {
		if (!super.isEnabledJoint(joint)) {
			return false;
		} else {
			return this.getProperty(ClientAnimationProperty.JOINT_BINDING_OPTION).map((bindingOptions) -> {
				for (BindingOption bindingOption : bindingOptions) {
					if (bindingOption.isEqualTo(joint)) {
						return true;
					}
				}
				return false;
			}).orElse(true);
		}
	}
	
	@Override
	public BindingOperation getBindingOperation(String joint) {
		return this.getProperty(ClientAnimationProperty.JOINT_BINDING_OPTION).map((bindingOptions) -> {
			for (BindingOption bindingOption : bindingOptions) {
				if (bindingOption.isEqualTo(joint)) {
					return bindingOption.getOperation();
				}
			}
			return BindingOption.DEFAULT;
		}).orElse(BindingOption.DEFAULT);
	}
	
	@Override
	public int getId() {
		return this.animationId;
	}
	
	public boolean isBasicAttackAnimation() {
		return false;
	}
	
	@Override
	public String toString() {
		return String.valueOf(this.getId());
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
		return this.getProperty(ClientAnimationProperty.PRIORITY).orElse(Layer.Priority.LOWEST);
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