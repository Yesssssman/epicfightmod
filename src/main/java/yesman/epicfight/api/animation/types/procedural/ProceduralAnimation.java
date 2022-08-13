package yesman.epicfight.api.animation.types.procedural;

import java.util.Map;

import com.google.common.collect.Lists;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import yesman.epicfight.api.animation.Animator;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.Keyframe;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.animation.TransformSheet;
import yesman.epicfight.api.model.Armature;
import yesman.epicfight.api.utils.math.FABRIK;
import yesman.epicfight.api.utils.math.OpenMatrix4f;
import yesman.epicfight.api.utils.math.Vec3f;
import yesman.epicfight.world.capabilities.entitypatch.boss.enderdragon.EnderDragonPatch;

public interface ProceduralAnimation {
	default void setIKInfo(IKInfo[] ikInfos, Map<String, TransformSheet> src, Map<String, TransformSheet> dest, Armature armature, boolean correctY, boolean correctZ) {
		for (IKInfo ikInfo : ikInfos) {
			ikInfo.pathToEndJoint = Lists.newArrayList();
			Joint start = armature.searchJointByName(ikInfo.startJoint);
			int pathToEnd = Integer.parseInt(start.searchPath(new String(""), ikInfo.endJoint));
			ikInfo.pathToEndJoint.add(start.getName());
			
			while (pathToEnd > 0) {
				start = start.getSubJoints().get(pathToEnd % 10 - 1);
				pathToEnd /= 10;
				ikInfo.pathToEndJoint.add(start.getName());
			}
			
			Keyframe[] keyframes = src.get(ikInfo.endJoint).getKeyframes();
			Keyframe[] bindedposKeyframes = new Keyframe[keyframes.length];
			int keyframeLength = src.get(ikInfo.endJoint).getKeyframes().length;
			
			for (int i = 0; i < keyframeLength; i++) {
				Keyframe kf = keyframes[i];
				Pose pose = new Pose();
				
				for (String jointName : src.keySet()) {
					pose.putJointData(jointName, src.get(jointName).getInterpolatedTransform(kf.time()));
				}
				
				OpenMatrix4f bindedTransform = Animator.getBindedJointTransformByName(pose, armature, ikInfo.endJoint);
				JointTransform bindedJointTransform = JointTransform.fromMatrixNoScale(bindedTransform);
				bindedposKeyframes[i] = new Keyframe(kf);
				JointTransform tipTransform = bindedposKeyframes[i].transform();
				tipTransform.copyFrom(bindedJointTransform);
				
				if (correctY || correctZ) {
					JointTransform rootTransform = src.get("Root").getInterpolatedTransform(kf.time());
					Vec3f rootPos = rootTransform.translation();
					float yCorrection = correctY ? -rootPos.z : 0.0F;
					float zCorrection = correctZ ? rootPos.y : 0.0F;
					tipTransform.translation().add(0.0F, yCorrection, zCorrection);
				}
			}
			
			TransformSheet tipAnimation = new TransformSheet(bindedposKeyframes);
			dest.put(ikInfo.endJoint, tipAnimation);
			
			if (ikInfo.clipAnimation) {
				TransformSheet part = tipAnimation.copy(ikInfo.startFrame, ikInfo.endFrame);
				Keyframe[] partKeyframes = part.getKeyframes();
				ikInfo.startpos = partKeyframes[0].transform().translation();
				ikInfo.endpos = partKeyframes[partKeyframes.length - 1].transform().translation();
			} else {
				ikInfo.startpos = tipAnimation.getKeyframes()[0].transform().translation();
				ikInfo.endpos = ikInfo.startpos;
			}
			
			ikInfo.startToEnd = Vec3f.sub(ikInfo.endpos, ikInfo.startpos, null).multiply(-1.0F, 1.0F, -1.0F);
		}
	}
	
	default TransformSheet getFirstPart(TransformSheet transformSheet) {
		TransformSheet part = transformSheet.copy(0, 2);
		Keyframe[] keyframes = part.getKeyframes();
		keyframes[1].transform().copyFrom(keyframes[0].transform());
		return part;
	}
	
	default TransformSheet clipAnimation(TransformSheet transformSheet, IKInfo ikInfo) {
		if (ikInfo.clipAnimation) {
			return transformSheet.copy(ikInfo.startFrame, ikInfo.endFrame);
		} else {
			return this.getFirstPart(transformSheet);
		}
	}
	
	default Vec3f getRayCastedTipPosition(Vec3f clipStart, OpenMatrix4f toWorldCoord, EnderDragonPatch enderdragonpatch, float maxYDown, float leastHeight) {
		Vec3f clipStartWorld = OpenMatrix4f.transform3v(toWorldCoord, clipStart, null);
		BlockHitResult clipResult = enderdragonpatch.getOriginal().level.clip(new ClipContext(new Vec3(clipStartWorld.x, clipStartWorld.y, clipStartWorld.z), new Vec3(clipStartWorld.x, clipStartWorld.y - maxYDown, clipStartWorld.z), ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, enderdragonpatch.getOriginal()));
		float dy = (clipResult.getType() != HitResult.Type.MISS) ? (float)clipStartWorld.y - clipResult.getBlockPos().getY() - 1 : maxYDown;
		return new Vec3f((float)clipStartWorld.x, (float)clipStartWorld.y - dy + leastHeight, (float)clipStartWorld.z);
	}
	
	default void correctRootRotation(JointTransform rootTransform, EnderDragonPatch enderdragonpatch, float partialTicks) {
		float xRoot = enderdragonpatch.xRootO + (enderdragonpatch.xRoot - enderdragonpatch.xRootO) * partialTicks;
		float zRoot = enderdragonpatch.zRootO + (enderdragonpatch.zRoot - enderdragonpatch.zRootO) * partialTicks;
		Quaternion quat = Vector3f.ZP.rotationDegrees(zRoot);
		quat.mul(Vector3f.XP.rotationDegrees(-xRoot));
		rootTransform.frontResult(JointTransform.getRotation(quat), OpenMatrix4f::mulAsOriginFront);
	}
	
	default void applyFabrikToJoint(Vec3f recalculatedPosition, Pose pose, Armature armature, String startJoint, String endJoint, Quaternion tipRotation) {
		FABRIK fabrik = new FABRIK(pose, armature, startJoint, endJoint);
    	fabrik.run(recalculatedPosition, 10);
    	OpenMatrix4f tipRotationMatrix = OpenMatrix4f.fromQuaternion(tipRotation);
    	OpenMatrix4f animRotation = Animator.getBindedJointTransformByName(pose, armature, endJoint).removeTranslation();
    	OpenMatrix4f animToTipRotation = OpenMatrix4f.mul(OpenMatrix4f.invert(animRotation, null), tipRotationMatrix, null);
    	pose.getOrDefaultTransform(endJoint).overwriteRotation(JointTransform.fromMatrixNoScale(animToTipRotation));
	}
	
	default void startPartAnimation(IKInfo ikInfo, TipPointAnimation tipAnim, TransformSheet partAnimation, Vec3f targetpos) {
		Vec3f footpos = tipAnim.getTipPosition(1.0F);
		Vec3f worldStartToEnd = targetpos.copy().sub(footpos);
		partAnimation.correctAnimationByNewPosition(ikInfo.startpos, ikInfo.startToEnd, footpos, worldStartToEnd);
		tipAnim.start(targetpos, partAnimation, 1.0F);
	}
	
	default void startSimple(IKInfo ikInfo, TipPointAnimation tipAnim) {
		tipAnim.start(new Vec3f(0.0F, 0.0F, 0.0F), tipAnim.getAnimation(), 1.0F);
	}
}