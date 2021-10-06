package yesman.epicfight.client.animation;

import java.util.Map;

import com.mojang.datafixers.util.Pair;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.animation.Joint;
import yesman.epicfight.animation.Pose;
import yesman.epicfight.animation.types.DynamicAnimation;
import yesman.epicfight.utils.math.OpenMatrix4f;

@OnlyIn(Dist.CLIENT)
@FunctionalInterface
public interface PoseModifyingFunction {
	public void modify(AnimatorClient clientAnimator, Pose resultPose, Layer.Priority priority, Joint joint, OpenMatrix4f parentTransform, Map<Layer.Priority, Pair<DynamicAnimation, Pose>> poses);
}