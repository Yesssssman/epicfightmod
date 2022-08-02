package yesman.epicfight.api.client.animation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;

@OnlyIn(Dist.CLIENT)
public class ClientAnimationProperties {
	public static final StaticAnimationProperty<Layer.LayerType> LAYER_TYPE = new StaticAnimationProperty<Layer.LayerType> ();
	public static final StaticAnimationProperty<Layer.Priority> PRIORITY = new StaticAnimationProperty<Layer.Priority> ();
	public static final StaticAnimationProperty<JointMaskEntry> JOINT_MASK = new StaticAnimationProperty<JointMaskEntry> ();
}