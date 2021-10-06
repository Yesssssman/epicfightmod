package yesman.epicfight.animation.types;

import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Hand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.animation.property.Property.StaticAnimationProperty;
import yesman.epicfight.capabilities.entity.LivingData;
import yesman.epicfight.client.animation.ClientAnimationProperties;
import yesman.epicfight.client.animation.Layer;
import yesman.epicfight.collada.AnimationDataExtractor;
import yesman.epicfight.model.Model;

public class MirrorAnimation extends StaticAnimation {
	public StaticAnimation original;
	public StaticAnimation mirror;

	public MirrorAnimation(float convertTime, boolean repeatPlay, String path1, String path2, Model model) {
		super(0.0F, false, path1, model);
		this.original = new StaticAnimation(convertTime, repeatPlay, path1, model, true);
		this.mirror = new StaticAnimation(convertTime, repeatPlay, path2, model, true);
	}
	
	@Override
	public void onActivate(LivingData<?> entitydata) {
		StaticAnimation animation = this.checkHandAndReturnAnimation(entitydata.getOriginalEntity().getActiveHand());
		entitydata.getAnimator().playAnimation(animation, 0.0F);
	}
	
	@Override
	public void loadAnimation(IResourceManager resourceManager) {
		AnimationDataExtractor.loadStaticAnimation(resourceManager, this.original);
		AnimationDataExtractor.loadStaticAnimation(resourceManager, this.mirror);
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
		return this.original.getProperty(ClientAnimationProperties.PRIORITY).orElse(Layer.Priority.LOWEST);
	}
	
	private StaticAnimation checkHandAndReturnAnimation(Hand hand) {
		if (hand == Hand.OFF_HAND) {
			return this.mirror;
		}
		return this.original;
	}
}