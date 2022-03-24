package yesman.epicfight.api.animation.types.procedural;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import yesman.epicfight.api.utils.math.Vec3f;

public class IKSetter {
	public final String startJoint;
	public final String endJoint;
	final String opponentJoint;
	final boolean hasPartAnimation;
	final int startFrame;
	final int endFrame;
	final int ikPose;
	final float rayLeastHeight;
	final boolean[] touchingGround;
	List<String> pathToEndJoint;
	Vec3f startpos;
	Vec3f endpos;
	Vec3f startToEnd;
	
	private IKSetter(String startJoint, String endJoint, String opponentJoint, Pair<Integer, Integer> partAnimation, float rayLeastHeight, int ikFrame, boolean[] touchGround) {
		this.startJoint = startJoint;
		this.endJoint = endJoint;
		this.opponentJoint = opponentJoint;
		this.hasPartAnimation = partAnimation != null;
		this.startFrame = this.hasPartAnimation ? partAnimation.getLeft() : -1;
		this.endFrame = this.hasPartAnimation ? partAnimation.getRight() : -1;
		this.ikPose = ikFrame;
		this.rayLeastHeight = rayLeastHeight;
		this.touchingGround = touchGround;
	}
	
	public static IKSetter make(String startJoint, String endJoint, String opponentJoint, Pair<Integer, Integer> partAnimation, float rayLeastHeight, int ikFrame, boolean[] touchGround) {
		return new IKSetter(startJoint, endJoint, opponentJoint, partAnimation, rayLeastHeight, ikFrame, touchGround);
	}
}