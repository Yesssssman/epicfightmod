package yesman.epicfight.api.animation.types.procedural;

import java.util.List;

import it.unimi.dsi.fastutil.ints.IntIntPair;
import yesman.epicfight.api.animation.Joint;
import yesman.epicfight.api.utils.math.Vec3f;

public class IKInfo {
	public final Joint startJoint;
	public final Joint endJoint;
	final Joint opponentJoint;
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
	
	private IKInfo(Joint startJoint, Joint endJoint, Joint opponentJoint, IntIntPair clipFrame, float rayLeastHeight, int ikFrame, boolean[] touchGround) {
		this.startJoint = startJoint;
		this.endJoint = endJoint;
		this.opponentJoint = opponentJoint;
		this.clipAnimation = clipFrame != null;
		this.startFrame = this.clipAnimation ? clipFrame.firstInt() : -1;
		this.endFrame = this.clipAnimation ? clipFrame.secondInt() : -1;
		this.ikPose = ikFrame;
		this.rayLeastHeight = rayLeastHeight;
		this.touchingGround = touchGround;
	}
	
	public static IKInfo make(Joint startJoint, Joint endJoint, Joint opponentJoint, IntIntPair clipFrame, float rayLeastHeight, int ikFrame, boolean[] touchGround) {
		return new IKInfo(startJoint, endJoint, opponentJoint, clipFrame, rayLeastHeight, ikFrame, touchGround);
	}
}