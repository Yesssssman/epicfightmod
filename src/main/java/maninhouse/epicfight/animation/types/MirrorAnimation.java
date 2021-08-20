package maninhouse.epicfight.animation.types;

import maninhouse.epicfight.animation.property.Property.StaticAnimationProperty;
import maninhouse.epicfight.capabilities.entity.LivingData;
import maninhouse.epicfight.client.animation.ClientAnimationProperty;
import maninhouse.epicfight.client.animation.Layer;
import maninhouse.epicfight.collada.AnimationDataExtractor;
import maninhouse.epicfight.model.Armature;
import net.minecraft.util.Hand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MirrorAnimation extends StaticAnimation {
	public StaticAnimation original;
	public StaticAnimation mirror;

	public MirrorAnimation(int id, float convertTime, boolean repeatPlay, String path1, String path2) {
		super(id, 0.0F, false, null);
		this.original = new StaticAnimation(convertTime, repeatPlay, path1);
		this.mirror = new StaticAnimation(convertTime, repeatPlay, path2);
	}
	
	@Override
	public void onActivate(LivingData<?> entitydata) {
		StaticAnimation animation = this.checkHandAndReturnAnimation(entitydata.getOriginalEntity().getActiveHand());
		entitydata.getAnimator().playAnimation(animation, 0.0F);
	}
	
	@Override
	public StaticAnimation loadAnimation(Armature armature, Dist dist) {
		AnimationDataExtractor.extractStaticAnimation(this.original.animationLocation, original, armature, dist);
		AnimationDataExtractor.extractStaticAnimation(this.mirror.animationLocation, mirror, armature, dist);
		return this;
	}
	
	@Override
	public boolean isMetaAnimation() {
		return true;
	}
	
	@Override
	public <V> StaticAnimation addProperty(StaticAnimationProperty<V> propertyType, V value) {
		this.original.properties.put(propertyType, value);
		this.mirror.properties.put(propertyType, value);
		return this;
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public Layer.Priority getPriority() {
		return this.original.getProperty(ClientAnimationProperty.PRIORITY).orElse(Layer.Priority.LOWEST);
	}
	
	private StaticAnimation checkHandAndReturnAnimation(Hand hand) {
		if (hand == Hand.OFF_HAND) {
			return this.mirror;
		}
		return this.original;
	}
}