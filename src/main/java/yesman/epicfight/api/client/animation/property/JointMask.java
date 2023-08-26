package yesman.epicfight.api.client.animation.property;

import java.util.Map;

import com.mojang.datafixers.util.Pair;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.types.DynamicAnimation;
import yesman.epicfight.api.client.animation.Layer;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.world.capabilities.entitypatch.LivingEntityPatch;

@OnlyIn(Dist.CLIENT)
public class JointMask {
	@FunctionalInterface
	public interface BindModifier {
		public void modify(LivingEntityPatch<?> entitypatch, Pose baseLayerPose, Pose resultPose, Layer.Priority priority, Joint joint, Map<Layer.Priority, Pair<DynamicAnimation, Pose>> poses);
	}
	
	public static final BindModifier KEEP_CHILD_LOCROT = (entitypatch, baseLayerPose, result, priority, joint, poses) -> {
		Pose currentPose = poses.get(priority).getSecond();
		JointTransform lowestTransform = baseLayerPose.getOrDefaultTransform(joint.getName());
		JointTransform currentTransform = currentPose.getOrDefaultTransform(joint.getName());
		result.getJointTransformData().getOrDefault(joint.getName(), JointTransform.empty()).translation().y = lowestTransform.translation().y;
		
		OpenMatrix4f lowestMatrix = lowestTransform.toMatrix();
		OpenMatrix4f currentMatrix = currentTransform.toMatrix();
		OpenMatrix4f currentToLowest = OpenMatrix4f.mul(OpenMatrix4f.invert(currentMatrix, null), lowestMatrix, null);
		
		for (Joint subJoint : joint.getSubJoints()) {
			if (!poses.get(priority).getFirst().isJointEnabled(entitypatch, priority, subJoint.getName())) {
				OpenMatrix4f lowestLocalTransform = OpenMatrix4f.mul(joint.getLocalTrasnform(), lowestMatrix, null);
				OpenMatrix4f currentLocalTransform = OpenMatrix4f.mul(joint.getLocalTrasnform(), currentMatrix, null);
				OpenMatrix4f childTransform = OpenMatrix4f.mul(subJoint.getLocalTrasnform(), result.getOrDefaultTransform(subJoint.getName()).toMatrix(), null);
				OpenMatrix4f lowestFinal = OpenMatrix4f.mul(lowestLocalTransform, childTransform, null);
				OpenMatrix4f currentFinal = OpenMatrix4f.mul(currentLocalTransform, childTransform, null);
				Vec3f vec = new Vec3f((currentFinal.m30 - lowestFinal.m30) * 0.5F, currentFinal.m31 - lowestFinal.m31, currentFinal.m32 - lowestFinal.m32);
				JointTransform jt = result.getJointTransformData().getOrDefault(subJoint.getName(), JointTransform.empty());
				jt.parent(JointTransform.getTranslation(vec), OpenMatrix4f::mul);
				jt.jointLocal(JointTransform.fromMatrixNoScale(currentToLowest), OpenMatrix4f::mul);
			}
		}
	};
	
	public static JointMask of(String jointName, BindModifier bindModifier) {
		return new JointMask(jointName, bindModifier);
	}
	
	public static JointMask of(String jointName) {
		return new JointMask(jointName, null);
	}
	
	private final String jointName;
	private final BindModifier bindModifier;
	
	private JointMask(String jointName, BindModifier bindModifier) {
		this.jointName = jointName;
		this.bindModifier = bindModifier;
	}
	
	@Override
	public boolean equals(Object object) {
		if (object instanceof JointMask jointMask) {
			return jointMask.jointName.equals(this.jointName);
		}
		
		return super.equals(object);
	}
	
	public String getJointName() {
		return this.jointName;
	}
	
	public BindModifier getBindModifier() {
		return this.bindModifier;
	}
}