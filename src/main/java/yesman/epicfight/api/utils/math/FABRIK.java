package yesman.epicfight.api.utils.math;

import java.util.List;

import com.google.common.collect.Lists;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;

import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.animation.JointTransform;
import yesman.epicfight.api.animation.Pose;
import yesman.epicfight.api.model.Armature;

public class FABRIK {
	private Armature armature;
	private List<Chain> chains = Lists.newArrayList();
	private Vec3f target = new Vec3f();
	private Vec3f startPos = new Vec3f();
	private Pose pose;
	
	public FABRIK(Pose pose, Armature armature, Joint startJoint, Joint endJoint) {
		this.armature = armature;
		this.pose = pose;
		this.addChain(pose, this.armature.searchJointByName(startJoint.getName()), this.armature.searchJointByName(endJoint.getName()));
	}
	
	public void addChain(Pose pose, Joint startJoint, Joint endJoint) {
		OpenMatrix4f bindTransform = armature.getBindedTransformFor(pose, startJoint);
		int pathIndex = Integer.parseInt(startJoint.searchPath(new String(""), endJoint.getName()));
		this.startPos.set(bindTransform.toTranslationVector());
		this.addChainInternal(pose, bindTransform, startJoint, pathIndex);
	}
	
	private void addChainInternal(Pose pose, OpenMatrix4f parentTransform, Joint joint, int pathIndex) {
		Joint nextJoint = joint.getSubJoints().get((pathIndex % 10) - 1);
		JointTransform jt = pose.getOrDefaultTransform(nextJoint.getName());
		OpenMatrix4f result = jt.getAnimationBindedMatrix(nextJoint, parentTransform);
		this.chains.add(new Chain(joint.getName(), parentTransform.toTranslationVector(), result.toTranslationVector()));
		int remainPath = pathIndex / 10;
		
		if (remainPath > 0) {
			this.addChainInternal(pose, result, nextJoint, remainPath);
		}
	}
	
	public void run(Vec3f target, int iteration) {
		this.target.set(target);
		
		for (int i = 0; i < iteration; i++) {
			this.backward();
			this.forward();
		}
		
		Quaternion parentQuaternion = Quaternion.ONE;
		
		for (Chain chain : this.chains) {
			Vector3f tailToHeadM = chain.tailToHead.toMojangVector();
			tailToHeadM.transform(parentQuaternion);
			Vec3f tailToHead = Vec3f.fromMojangVector(tailToHeadM);
			Vec3f tailToNewHead = chain.head.copy().sub(chain.tail);
			Vec3f axis = Vec3f.cross(tailToNewHead, tailToHead, null).normalise();
			float radian = Vec3f.getAngleBetween(tailToNewHead, tailToHead);
			Quaternion rotationQuat = new Quaternion(axis.toMojangVector(), radian, false);
			parentQuaternion = new Quaternion(axis.scale(-1.0F).toMojangVector(), radian, false);
			
			JointTransform jt = this.pose.getOrDefaultTransform(chain.jointName);
			jt.frontResult(JointTransform.getRotation(rotationQuat), OpenMatrix4f::mulAsOriginFront);
		}
	}
	
	private void forward() {
		int chainNum = this.chains.size();
		Vec3f newTailPos = new Vec3f();
		newTailPos.set(this.startPos);
		
		for (int i = 0; i < chainNum; i++) {
			Chain chain = this.chains.get(i);
			chain.forwardAlign(newTailPos);
			newTailPos.set(chain.head);
		}
	}
	
	private void backward() {
		int chainNum = this.chains.size();
		Vec3f newHeadPos = new Vec3f();
		newHeadPos.set(this.target);
		
		for (int i = chainNum - 1; i >= 0; i--) {
			Chain chain = this.chains.get(i);
			chain.backwardAlign(newHeadPos);
			newHeadPos.set(chain.tail);
		}
	}
	
	public List<Vec3f> getChainingPosition() {
		List<Vec3f> list = Lists.newArrayList();
		for (Chain chain : this.chains) {
			list.add(chain.tail);
		}
		
		list.add(this.chains.get(this.chains.size() - 1).head);
		return list;
	}
	
	class Chain {
		final String jointName;
		float length;
		Vec3f tail;
		Vec3f head;
		Vec3f tailToHead;
		
		Chain(String jointName, Vec3f tail, Vec3f head) {
			this.jointName = jointName;
			this.tail = tail;
			this.head = head;
			this.tailToHead = head.copy().sub(tail);
			this.length = (float)Math.sqrt(tail.distanceSqr(head));
		}
		
		public void forwardAlign(Vec3f newHeadPos) {
			this.correct(this.tail, this.head, newHeadPos);
		}
		
		public void backwardAlign(Vec3f newHeadPos) {
			this.correct(this.head, this.tail, newHeadPos);
		}
		
		private void correct(Vec3f start, Vec3f end, Vec3f newpos) {
			start.set(newpos);
			Vec3f startToEnd = end.sub(start);
			float newLength = startToEnd.length();
			float lengthRatio = this.length / newLength;
			Vec3f startToEndScaled = startToEnd.copy().scale(lengthRatio);
			end.set(start.copy().add(startToEndScaled));
		}
		
		public void init(Vec3f tail, Vec3f head) {
			this.tail.set(tail);
			this.head.set(head);
			this.tailToHead.set(head.copy().sub(tail));
			this.length = (float)Math.sqrt(tail.distanceSqr(head));
		}
	}
}