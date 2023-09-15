package yesman.epicfight.api.animation.types;

import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.client.animation.Layer;
import yesman.epicfight.api.client.animation.property.ClientAnimationProperties;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

public class MirrorAnimation extends StaticAnimation {
	public StaticAnimation original;
	public StaticAnimation mirror;
	
	public MirrorAnimation(float convertTime, boolean repeatPlay, String path1, String path2, Armature armature) {
		super(0.0F, false, path1, armature);
		
		this.original = new StaticAnimation(convertTime, repeatPlay, path1, armature);
		this.mirror = new StaticAnimation(convertTime, repeatPlay, path2, armature);
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
		load(resourceManager, this.original);
		load(resourceManager, this.mirror);
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