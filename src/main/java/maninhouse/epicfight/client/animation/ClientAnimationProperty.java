package maninhouse.epicfight.client.animation;

import maninhouse.epicfight.animation.property.Property.StaticAnimationProperty;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientAnimationProperty {
	public static final StaticAnimationProperty<Layer.Priority> PRIORITY = new StaticAnimationProperty<Layer.Priority> ();
	public static final StaticAnimationProperty<BindingOption[]> JOINT_BINDING_OPTION = new StaticAnimationProperty<BindingOption[]> ();
}