package yesman.epicfight.api.animation.types.procedural;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import yesman.epicfight.api.utils.math.Vec3f;

public class IKInfo {
	public final String startJoint;
	public final String endJoint;
	final String opponentJoint;
	final boolean clipAnimation;
	final int startFrame;
	final int endFrame;
	final int ikPose;
	final float rayLeastHeight;
	final boolean[] touchingGround;
	List<String> pathToEndJoint;
	Vec3f startpos;
	Vec3f endpos;
	Vec3f startToEnd;
	
	private IKInfo(String startJoint, String endJoint, String opponentJoint, Pair<Integer, Integer> clipFrame, float rayLeastHeight, int ikFrame, boolean[] touchGround) {
		this.startJoint = startJoint;
		this.endJoint = endJoint;
		this.opponentJoint = opponentJoint;
		this.clipAnimation = clipFrame != null;
		this.startFrame = this.clipAnimation ? clipFrame.getLeft() : -1;
		this.endFrame = this.clipAnimation ? clipFrame.getRight() : -1;
		this.ikPose = ikFrame;
		this.rayLeastHeight = rayLeastHeight;
		this.touchingGround = touchGround;
	}
	
	public static IKInfo make(String startJoint, String endJoint, String opponentJoint, Pair<Integer, Integer> clipFrame, float rayLeastHeight, int ikFrame, boolean[] touchGround) {
		return new IKInfo(startJoint, endJoint, opponentJoint, clipFrame, rayLeastHeight, ikFrame, touchGround);
	}
}