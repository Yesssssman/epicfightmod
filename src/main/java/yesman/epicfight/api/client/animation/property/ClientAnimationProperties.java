package yesman.epicfight.api.client.animation.property;

import java.util.List;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.property.AnimationProperty.StaticAnimationProperty;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.api.client.animation.Layer;

@OnlyIn(Dist.CLIENT)
public class ClientAnimationProperties {
	/**
	 * Layer type. (BASE: Living, attack animations, COMPOSITE: Aiming, weapon holding, digging animation)
	 */
	public static final StaticAnimationProperty<Layer.LayerType> LAYER_TYPE = new StaticAnimationProperty<Layer.LayerType> ();
	
	/**
	 * Priority of composite layer.
	 */
	public static final StaticAnimationProperty<Layer.Priority> PRIORITY = new StaticAnimationProperty<Layer.Priority> ();
	
	/**
	 * Joint mask for composite layer.
	 */
	public static final StaticAnimationProperty<JointMaskEntry> JOINT_MASK = new StaticAnimationProperty<JointMaskEntry> ();
	
	/**
	 * Trail particle information
	 */
	public static final StaticAnimationProperty<List<TrailInfo>> TRAIL_EFFECT = new StaticAnimationProperty<List<TrailInfo>> ();
	
	/**
	 * Multilayer for living animations (e.g. Greatsword holding animation should be played simultaneously with jumping animation) 
	 */
	public static final StaticAnimationProperty<StaticAnimation> MULTILAYER_ANIMATION = new StaticAnimationProperty<StaticAnimation> ();
}