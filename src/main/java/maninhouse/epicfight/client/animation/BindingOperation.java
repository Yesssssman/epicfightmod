package maninhouse.epicfight.client.animation;

import java.util.Map;

import maninhouse.epicfight.animation.Joint;
import maninhouse.epicfight.animation.Pose;
import maninhouse.epicfight.utils.math.OpenMatrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
@FunctionalInterface
public interface BindingOperation {
	public void bind(AnimatorClient clientAnimator, Layer.Priority priority, Joint joint, OpenMatrix4f parentTransform, Map<Layer.Priority, Pose> poses, float partialTicks);
}